const axios = require("axios");
const msmc = require("msmc");
const fs = require("fs");
const keytar = require("keytar");
const Utils = require("./utils");
const KEYCHAIN_PREFIX = "keychain:";
const SERVICE = "sol_client";
let manager;

class AccountManager {

	constructor(file, dataCallback) {
		this.file = file;
		if(fs.existsSync(file)) {
			let data = JSON.parse(fs.readFileSync(file, "UTF-8"));
			this.accounts = [];
			for(let account of data.accounts) {
				this.accounts.push(Account.from(account));
			}
			this.activeAccount = this.accounts[data.activeAccount];
			if(this.activeAccount) {
				this.fetchSkin(this.activeAccount);
			}
		}
		else {
			this.accounts = [];
		}
		this.save();
		this.dataCallback = dataCallback;
		this.refreshTask = {};
		manager = this;
	}

	save() {
		fs.writeFileSync(this.file, JSON.stringify({
			accounts: this.accounts,
			activeAccount: this.accounts.indexOf(this.activeAccount)
		}));
	}

	isInKeychain(prop) {
		return prop.startsWith(KEYCHAIN_PREFIX);
	}

	async storeInKeychain(account) {
		account.accessToken = await this.storeProp(account.accessToken, account.uuid + "_access_token");
		if(account._msmc) {
			account._msmc.refresh = await this.storeProp(account._msmc.refresh, account.uuid + "_refresh");
			account._msmc.mcToken = undefined; // ah yes, the number underfined
		}
	}

	async storeProp(prop, key) {
		if(this.isInKeychain(prop)) {
			return prop;
		}
		await keytar.setPassword(SERVICE, key, prop);
		let test = await keytar.getPassword(SERVICE, key);
		if(test != prop) {
			return prop;
		}
		return KEYCHAIN_PREFIX + key;
	}

	async retrieveProp(prop) {
		if(!this.isInKeychain(prop)) {
			return prop;
		}
		let key = prop.substring(KEYCHAIN_PREFIX.length);
		return keytar.getPassword(SERVICE, key);
	}

	async realToken(account) {
		return this.retrieveProp(account.accessToken);
	}

	async realRefresh(account) {
		if(!account._msmc) {
			return null;
		}

		return this.retrieveProp(account._msmc.refresh);
	}

	refreshAccount(account) {
		if(this.refreshTask[account]) {
			return this.refreshTask;
		}

		let task = new Promise(async(resolve, reject) => {
			let valid = await this.activeAccount.getService().validate(this.activeAccount);
			if(!valid) {
				let result = await this.activeAccount.getService().refresh(this.activeAccount);
				if(!result) {
					this.removeAccount(this.activeAccount);
					reject();
					return;
				}
				this.addAccount(result);
			}
			this.refreshTask[account] = null;
			resolve();
		});

		this.refreshTask[account] = task;
		return task;
	}

	getFullProfile(account) {
		return new Promise(async(resolve, reject) => {
			try {
				await this.refreshAccount(account);
				resolve((await axios.get("https://api.minecraftservices.com/minecraft/profile",
						{
							headers: {
								"Authorization": "Bearer " + account.accessToken
							}
						})).data);
			}
			catch(error) {
				reject(error);
			}
		});
	}

	async fetchSkin(account) {
		let textures = await Utils.getTextures(account.uuid);

		if(textures) {
			if(textures.SKIN) {
				account.skin = await Utils.expandImageURL(textures.SKIN.url);

				let image = await Utils.loadImage(account.skin);

				let canvas = document.createElement("canvas");
				let context = canvas.getContext("2d");

				canvas.width = 8;
				canvas.height = 8;
				context.drawImage(image, 8, 8, 8, 8, 0, 0, 8, 8);

				if(image.naturalWidth == 64) {
					context.drawImage(image, 40, 8, 8, 8, 0, 0, 8, 8);
				}

				account.head = canvas.toDataURL();
			}

			if(textures.CAPE) {
				account.cape = await Utils.expandImageURL(textures.CAPE.url);
			}
			else {
				account.cape = null;
			}

			this.dataCallback(account);

			this.save();
		}
	}

	switchAccount(account) {
		this.activeAccount = account;
		this.fetchSkin(account);
		this.save();
	}

	addAccount(account) {
		let sameUUIDIndex = -1;

		for(let i = 0; i < this.accounts.length; i++) {
			let item = this.accounts[i];
			if(item.uuid == account.uuid) {
				sameUUIDIndex = i;
			}
		}

		if(sameUUIDIndex == -1) {
			this.accounts.push(account);
		}
		else {
			this.accounts[sameUUIDIndex] = account;
		}

		this.switchAccount(account);
	}

	async removeAccount(account) {
		let index = this.accounts.indexOf(this.activeAccount);
		this.accounts = this.accounts.filter((item) => item != account);

		keytar.deletePassword(SERVICE, KEYCHAIN_PREFIX + account.uuid + "_access_token");
		keytar.deletePassword(SERVICE, KEYCHAIN_PREFIX + account.uuid + "_refresh");

		if(account == this.activeAccount) {
			this.activeAccount = this.accounts[index];

			if(!this.activeAccount) {
				this.activeAccount = this.accounts[index - 1];
			}

			this.save();
			return this.activeAccount != null;
		}

		this.save();
		return true;
	}

}

class AuthService {

	authenticate(_key) {
		throw new Error("Unimplemented");
	}

}

class YggdrasilAuthKey {

	constructor(username, password) {
		this.username = username;
		this.password = password;
	}

}

class MicrosoftAuthService extends AuthService {

	static instance = new MicrosoftAuthService();

	async authenticate(msmc) {
		let account = new Account("msa", msmc.name, msmc.id, msmc._msmc.mcToken, null, msmc._msmc.demo, msmc._msmc);
		msmc._msmc.mcToken = undefined;
		await manager.storeInKeychain(account);
		return account;
	}

	async toMsmc(account) {
		return {
			name: account.username,
			id: account.uuid,
			_msmc: { ...account._msmc, ...{ refresh: await manager.realRefresh(account), mcToken: await manager.realToken(account) } }
		}
	}

	validate(account) {
		return new Promise(async(resolve) => {
			resolve(msmc.validate(await this.toMsmc(account)));
		});
	}

	refresh(account) {
		return new Promise(async(resolve) => {
			let result = await msmc.refresh(await this.toMsmc(account), () => {}, {client_id: "00000000402b5328"});

			if(result.type != "Success") {
				resolve(null);
				return;
			}

			resolve(await this.authenticate(result.profile));
		});
	}

}

class YggdrasilAuthService extends AuthService {

	static instance = new YggdrasilAuthService();
	static api = "https://authserver.mojang.com";

	constructor() {
		super();
	}

	authenticateUsernamePassword(username, password) {
		return this.authenticate(new YggdrasilAuthKey(username, password));
	}

	authenticate(key) {
		const url = YggdrasilAuthService.api;
		return new Promise((resolve, reject) => {
			axios.post(url + "/authenticate", {
						"agent": {
							"name": "Minecraft",
							"version": 1
						},
						"username": key.username,
						"password": key.password
					})
					.catch((error) => {
						reject(error);
					})
					.then((response) => {
						if(response == null) {
							return;
						}
						let data = response.data;
						let account = new Account(
								"mojang",
								data.selectedProfile.name,
								data.selectedProfile.id,
								data.accessToken,
								data.clientToken
						);
						manager.storeInKeychain(account);
						resolve(account);
					});
		});
	}

	validate(_accessToken) {
		return new Promise(async(resolve) => {
			const url = YggdrasilAuthService.api;
			axios.post(url + "/validate", {
						accessToken: await manager.realToken(account),
						clientToken: account.clientToken
					})
					.catch((_error) => {
						resolve(false);
					})
					.then((_response) => {
						resolve(true);
					});
		});
	}

	refresh(account) {
		const url = YggdrasilAuthService.api;
		return new Promise(async(resolve) => {
			axios.post(url + "/refresh", {
				accessToken: await manager.realToken(account),
				clientToken: account.clientToken,
				selectedProfile: {
					name: account.username,
					id: account.uuid
				}
			})
			.catch((_error) => {
				resolve(false);
			})
			.then(async(response) => {
				account.accessToken = await manager.storeProp(response.data.accessToken, account.uuid + "_access_token");
				resolve(true);
			});
		});
	}

}

class Account {

	static from(object) {
		return Object.assign(new Account(null, null, null, null, null, false, null), object)
	}

	constructor(type, username, uuid, accessToken, clientToken, demo, _msmc) {
		this.type = type;
		this.username = username;
		this.uuid = uuid;
		this.accessToken = accessToken;
		this.clientToken = clientToken;
		this.demo = demo;
		this._msmc = _msmc;
		this.head = "headless.png";
	}

	getService() {
		if(this.type == "msa") {
			return MicrosoftAuthService.instance;
		}
		else {
			return YggdrasilAuthService.instance;
		}
	}

}

exports.YggdrasilAuthService = YggdrasilAuthService;
exports.MicrosoftAuthService = MicrosoftAuthService;
exports.Account = Account;
exports.AccountManager = AccountManager;
