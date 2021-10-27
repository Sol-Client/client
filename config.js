const fs = require("fs");

class Config {

	static data = {
		maxMemory: 2048
	};
	static file;

	static init(base) {
		Config.file = base + "/config.json";
	}

	static load() {
		if(fs.existsSync(Config.file)) {
			Config.data = JSON.parse(fs.readFileSync(Config.file, "UTF-8"));
		}
	}

	static save() {
		fs.writeFileSync(Config.file, JSON.stringify(Config.data));
	}

}

module.exports = Config;
