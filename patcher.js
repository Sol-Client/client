const unzipper = require("unzipper");
const axios = require("axios");
const child_process = require("child_process");
const path = require("path");

class Patcher {

	static patch(java, jar, optifine, patchedJar, classpathSeparator) {
		return new Promise((resolve) => {
			var process = child_process.spawn(java, [
					"-cp",
					path.join(__dirname, "patcher/build/libs/patcher.jar") +
					classpathSeparator + optifine,
					"me.mcblueparrot.client.patcher.Patcher",
					jar, patchedJar, optifine
				]);
			process.on("exit", () => {
				resolve(patchedJar);
			});

			process.stdout.on("data", (data) => {}); // Don't know why you need this.
		});
	}

	static getOptiFine() {
		return new Promise((resolve) => {
			axios.get("https://optifine.net/adloadx?f=OptiFine_1.8.9_HD_U_M5.jar")
				.then((response) => {
					var link = "https://optifine.net/downloadx?f=" + response.data.substring(response.data.indexOf("<a href='downloadx?f=") + "<a href='downloadx?f=".length, response.data.indexOf("' onclick='onDownload()'>"))
					resolve(link);
				});
		});
	}

}

module.exports = Patcher;
