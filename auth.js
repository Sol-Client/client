const axios = require("axios");
const msmc = require("msmc");
const fs = require("fs");
const Utils = require("./utils");

class AccountManager {

	constructor(file, dataCallback) {
		this.file = file;
		if(fs.existsSync(file)) {
			var data = JSON.parse(fs.readFileSync(file, "UTF-8"));
			this.accounts = [];
			for(var account of data.accounts) {
				this.accounts.push(Account.from(account));
			}
			this.activeAccount = this.accounts[data.activeAccount];
			this.fetchSkin(this.activeAccount);
		}
		else {
			this.accounts = [];
			this.save();
		}
		this.dataCallback = dataCallback;
		this.refreshTask = {};
	}

	save() {
		fs.writeFileSync(this.file, JSON.stringify({ accounts: this.accounts, activeAccount: this.accounts.indexOf(this.activeAccount) }));
	}

	refreshAccount(account) {
		if(this.refreshTask[account]) {
			return this.refreshTask;
		}

		var task = new Promise(async(resolve, reject) => {
			var valid = await this.activeAccount.getService().validate(this.activeAccount);
			if(!valid) {
				var result = await this.activeAccount.getService().refresh(this.activeAccount);
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
		var textures = await Utils.getTextures(account.uuid);

		if(textures) {
			if(textures.SKIN) {
				account.skin = await Utils.expandImageURL(textures.SKIN.url);

				var image = await Utils.loadImage(account.skin);

				var canvas = document.createElement("canvas");
				var context = canvas.getContext("2d");

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
		var sameUUIDIndex = -1;

		for(var i = 0; i < this.accounts.length; i++) {
			var item = this.accounts[i];
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

	removeAccount(account) {
		var index = this.accounts.indexOf(this.activeAccount);
		this.accounts = this.accounts.filter((item) => item != account);

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

	authenticate(key) {
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

	authenticate(msmc) {
		return new Account("msa", msmc.name, msmc.id, msmc._msmc.mcToken, null, msmc._msmc.demo, msmc._msmc);
	}

	toMsmc(account) {
		return {
			name: account.username,
			id: account.uuid,
			_msmc: account._msmc
		}
	}

	validate(account) {
		return new Promise((resolve) => {
			resolve(msmc.validate(this.toMsmc(account)));
		});
	}

	refresh(account) {
		return new Promise(async(resolve) => {
			var result = await msmc.refresh(this.toMsmc(account), () => {}, {client_id: "00000000402b5328"});

			if(result.type != "Success") {
			    resolve(null);
			    return;
			}

			resolve(this.authenticate(result.profile));
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
						var data = response.data;
						resolve(
							new Account(
								"mojang",
								data.selectedProfile.name,
								data.selectedProfile.id,
								data.accessToken,
								data.clientToken
							)
						);
					});
		});
	}

	validate(account) {
		return new Promise((resolve) => {
			const url = YggdrasilAuthService.api;
			axios.post(url + "/validate", {
						accessToken: account.accessToken,
						clientToken: account.clientToken
					})
					.catch((error) => {
						resolve(false);
					})
					.then((response) => {
						resolve(true);
					});
		});
	}

	refresh(account) {
		const url = YggdrasilAuthService.api;
		return new Promise((resolve) => {
			axios.post(url + "/refresh", {
				accessToken: account.accessToken,
				clientToken: account.clientToken,
				selectedProfile: {
					name: account.username,
					id: account.uuid
				}
			})
			.catch((error) => {
				resolve(false);
			})
			.then((response) => {
				account.accessToken = response.data.accessToken;
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
