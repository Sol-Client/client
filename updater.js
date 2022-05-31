const axios = require("axios").default;
const Utils = require("./utils");
const childProcess = require("child_process");
const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

class Updater {

	static update() {
		return new Promise(async(resolve) => {
			console.log("Checking for update...");

			var latestRelease = (await axios.get("https://api.github.com/repos/TheKodeToad/Sol-Client/releases/latest")).data;

			if(latestRelease.name != require("./package.json").version) {
				console.log("Installing update...");

				var selectedAsset;
				for(var asset of latestRelease.assets) {
					if(asset.name.endsWith(".exe")) {
						selectedAsset = asset;
					}
				}

				var file = Utils.dataDirectory + "/Setup.exe";
				await Utils.download(selectedAsset.browser_download_url, file);

				await sleep(1000);

				childProcess.execFileSync(file);
				resolve(true);
				return;
			}
			console.log("No updates found");
			resolve(false);
		});
	}

}

module.exports = Updater;
