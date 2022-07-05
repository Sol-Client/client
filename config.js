const fs = require("fs");
const Utils = require("./utils");

class Config {

	static DEFAULT = {
		maxMemory: 2048,
		optifine: Utils.getOsName() != "osx",
		minecraftFolder: "<use default>",
		autoUpdate: true,
		jvmArgs: ""
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

	static getJvmArgs() {
		var args = Config.data.jvmArgs;
		var result = [];

		var prevC;
		var arg = "";

		for(var c of args) {
			if(prevC == "\\") {
				arg = arg.substring(0, arg.length - 1);
				arg += c;
				prevC = 0;
				continue;
			}
			else if(c == " ") {
				result.push(arg);
				arg = "";
			}
			else {
				arg += c;
			}
			prevC = c;
		}

		result.push(arg);

		return result;
	}

}

module.exports = Config;
