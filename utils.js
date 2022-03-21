const os = require("os");
const fs = require("fs");
const path = require("path");
const https = require("https");
const http = require("http");
const axios = require("axios");

class Utils {

	static minecraftDirectory;
	static legacyDirectory;
	static librariesDirectory;
	static versionsDirectory;
	static assetsDirectory;
	static assetObjectsDirectory;
	static assetIndexesDirectory;
	static accountsFile;
	static gameDirectory;
	static serversFile;
	static version = require("./package.json").version;
	static configFile;
	static latestLog;

	static init() {
		Utils.minecraftDirectory = os.homedir();
		Utils.legacyDirectory = os.homedir();
		switch(Utils.getOsName()) {
			case "linux":
				Utils.minecraftDirectory += "/.config/Sol Client";
				Utils.legacyDirectory += "/.config/parrotclient";
				break;
			case "osx":
				Utils.minecraftDirectory += "/Library/Application Support/Sol Client";
				Utils.legacyDirectory += "/Library/Application Support/parrotclient";
				break;
			case "windows":
				Utils.minecraftDirectory += "/AppData/Roaming/Sol Client";
				Utils.legacyDirectory += "/AppData/Roaming/parrotclient";
				break;
		}

		try {
			if(fs.existsSync(Utils.legacyDirectory) && !fs.existsSync(Utils.minecraftDirectory)) {
				fs.renameSync(Utils.legacyDirectory, Utils.minecraftDirectory);
				fs.unlinkSync(Utils.minecraftDirectory + "/account.json");
			}
		}
		catch(error) {
		}

		Utils.librariesDirectory = Utils.minecraftDirectory + "/libraries";
		Utils.versionsDirectory = Utils.minecraftDirectory + "/versions";
		Utils.assetsDirectory = Utils.minecraftDirectory + "/assets";
		Utils.assetObjectsDirectory = Utils.assetsDirectory + "/objects";
		Utils.assetIndexesDirectory = Utils.assetsDirectory + "/indexes";
		Utils.accountsFile = Utils.minecraftDirectory + "/accounts.json";

		var accountFile = Utils.minecraftDirectory + "/account.json";
		if(fs.existsSync(accountFile) && !fs.existsSync(Utils.accountsFile)) {
			var oldAccount = JSON.parse(fs.readFileSync(accountFile, "UTF-8"));
			var converted = { accounts: [oldAccount], activeAccount: 0 }
			fs.writeFileSync(Utils.accountsFile, JSON.stringify(converted));
			fs.rmSync(accountFile);
		}

		Utils.skinsFile = Utils.minecraftDirectory + "/skins.json";
		Utils.gameDirectory = Utils.minecraftDirectory + "/minecraft";

		if(!fs.existsSync(Utils.gameDirectory)) {
			fs.mkdirSync(Utils.gameDirectory, { recursive: true });
		}
	}

	static isAlreadyDownloaded(file, size) {
		return fs.existsSync(file) && (size == -1 || fs.statSync(file).size == size);
	}

	static download(url, file, size) {
		if(!fs.existsSync(path.dirname(file))) {
			fs.mkdirSync(path.dirname(file), { recursive: true });
		}

		if(!Utils.isAlreadyDownloaded(file, size)) {
			return new Promise((resolve) => {
				(url.startsWith("https://") ? https : http).get(url, async(response) => {
					if(response.code == 404) {
						resolve(false);
					}
					if(response.headers.location) {
						var result = await Utils.download(response.headers.location, file, size);
						resolve(result);
						return;
					}
					var stream = fs.createWriteStream(file);
					response.pipe(stream);
					response.on("end", () => {
						stream.close();
						resolve(true);
					});
				});
			});
		}
		return new Promise((resolve) => resolve(true));
	}

	static getOptiFine(version) {
		return new Promise((resolve) => {
			axios.get("https://optifine.net/adloadx?f=OptiFine_" + version + ".jar")
				.then((response) => {
					var link = "https://optifine.net/downloadx?f=" +
							response.data.substring(response.data
									.indexOf("<a href='downloadx?f=")
									 		+ "<a href='downloadx?f=".length, response.data.indexOf("' onclick='onDownload()'>"))
					resolve(link);
				});
		});
	}

	static getOsName() {
		switch(os.type()) {
			case "Linux":
				return "linux";
			case "Darwin":
				return "osx";
			case "Windows_NT":
				return "windows";
		}
	}

	static getJdkOsName() {
		switch(os.type()) {
			case "Linux":
				return "linux";
			case "Darwin":
				return "mac";
			case "Windows_NT":
				return "windows";
		}
	}

	// Expands an image URL into full data url
	// Heavily based around https://stackoverflow.com/a/64929732
	static expandImageURL(url) {
		return new Promise(async(resolve) => {
			var data = await fetch(url);
			var reader = new FileReader();
			reader.readAsDataURL(await data.blob());
			reader.onloadend = () => {
				resolve(reader.result);
			};
		});
	}

	static expandImageFile(file) {
		return "data:image/png;base64," + fs.readFileSync(file).toString("base64");
	}

	static loadImage(url) {
		return new Promise(async(resolve) => {
			var image = new Image();
			image.onload = () => {
				resolve(image);
			};
			image.src = url;
		})
	}

	static getTextures(uuid) {
		return new Promise(async(resolve) => {
			var profile = await axios.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);

			if(!profile.data) {
				return;
			}
			else {
				for(var property of profile.data.properties) {
					if(property.name == "textures") {
						resolve(JSON.parse(atob(property.value)).textures);
						return;
					}
				}
			}

			resolve(null);
		});
	}

	static getProfile(username) {
		return new Promise(async(resolve) => {
			try {
				resolve((await axios.get("https://api.mojang.com/users/profiles/minecraft/" + username)).data);
			}
			catch(error) {
				resolve(null);
			}
		});
	}

	static dataURLToBuffer(url) {
		return Buffer.from(url.substring(url.indexOf(",")), "base64");
	}

}

module.exports = Utils;
