const fs = require("fs");
const Utils = require("./utils");

class Config {

	static DEFAULT = {
		maxMemory: 2048,
		optifine: Utils.getOsName() != "osx",
		minecraftFolder: "<use default>",
		autoUpdate: true
	};

	static data = Config.DEFAULT;
	static file;

	static init(base) {
		Config.file = base + "/config.json";
	}

	static load() {
		if(fs.existsSync(Config.file)) {
			Config.data = { ...Config.DEFAULT, ...JSON.parse(fs.readFileSync(Config.file, "UTF-8")) };
		}
	}

	static save() {
		fs.writeFileSync(Config.file, JSON.stringify(Config.data));
	}

	static getGameDirectory(defaultDirectory) {
		if(Config.data.minecraftFolder != "<use default>") {
			return Config.data.minecraftFolder;
		}

		return defaultDirectory;
	}

}

module.exports = Config;
