const axios = require("axios");
const msmc = require("msmc");

class AuthService {

	authenticate(key) {
		throw new Error("Unimplemented");
	}

}

class AuthKey {

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
		return new Promise((resolve) => {
			resolve(msmc.refresh(this.toMsmc(account), () => {}, {client_id: "00000000402b5328"}));
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
