const axios = require("axios").default;
const Utils = require("./utils");
const childProcess = require("child_process");

class Updater {

	static async update() {
		console.log("Checking for update...");

		var latestRelease = await axios.get("https://api.github.com/repos/TheKodeToad/Sol-Client/releases/latest");

		if(latestRelease.name != require("./package.json").version) {
			console.log("Installing update...");

			var selectedAsset;
			for(var asset of latestRelease.assets) {
				if(asset.name.endsWith(".exe")) {
					selectedAsset = asset;
				}
			}

			var file = Utils.minecraftDirectory + "/" + selectedAsset.name;
			Utils.download(selectedAsset.url, file);
			childProcess.execFileSync(file);
			return true;
		}
		console.log("No updates found");
		return false;
	}

}

module.exports = Updater;