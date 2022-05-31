const https = require("https");
const fs = require("fs");
const path = require("path");
const os = require("os");
const unzipper = require("unzipper");
const childProcess = require("child_process");
const axios = require("axios");
const tar = require("tar");
const Config = require("./config");
const Utils = require("./utils");
const Patcher = require("./patcher");
const { ipcRenderer, shell } = require("electron");
const crypto = require("crypto");
const url = require("url");
const { AccountManager } = require("./auth");

class Launcher {

	static instance = new Launcher();
	accountManager = null;
	games = [];

	async launch(callback, progress, server) {
		console.log("Downloading manifest...");
		progress("Loading manifest...");
		var manifest = await Manifest.getManifest();
		console.log("Downloading version manifest...");
		var version = await Manifest.getVersion(manifest, "1.8.9");
		var jars = [];
		var versionFolder = Version.getPath(version);
		var versionJar = Version.getJar(version);
		var nativesFolder = Version.getNatives(version);
		var optifineRelative = "net/optifine/optifine/1.8.9_HD_U_M5/optifine-1.8.9_HD_U_M5.jar";
		var optifine = Utils.librariesDirectory + "/" + optifineRelative;
		var secret = crypto.randomBytes(32).toString("hex");
		var alreadyRunning = this.games.length > 0;

		if(!alreadyRunning && fs.existsSync(nativesFolder)) {
			fs.rmdirSync(nativesFolder, { recursive: true });
		}

		console.log("Downloading libraries...");
		progress("Downloading libraries...");

		version.libraries.push({
			downloads: {
				artifact: {
					url: "https://repo.maven.apache.org/maven2/org/slick2d/slick2d-core/1.0.2/slick2d-core-1.0.2.jar",
					path: "org/slick2d/slick2d-core/1.0.2/slick2d-core-1.0.2.jar",
					size: 590652
				}
			}
		});

		// culling lib removed due to repo issues

		version.libraries.push({
			downloads: {
				artifact: {
					url: "https://repo.hypixel.net/repository/Hypixel/net/hypixel/hypixel-api-core/4.0/hypixel-api-core-4.0.jar",
					path: "net/hypixel/hypixel-api-core/4.0/hypixel-api-core-4.0.jar",
					size: 76463
				}
			}
		});

		version.libraries.push({
			downloads: {
				artifact: {
					url: "https://repo.spongepowered.org/repository/maven-public/org/spongepowered/mixin/0.7.11-SNAPSHOT/mixin-0.7.11-20180703.121122-1.jar",
					path: "org/spongepowered/mixin/0.7.11-SNAPSHOT/mixin-0.7.11-20180703.121122-1.jar",
					size: 1017668
				}
			}
		});

		version.libraries.push({
			downloads: {
				artifact: {
					url: "https://libraries.minecraft.net/net/minecraft/launchwrapper/1.12/launchwrapper-1.12.jar",
					path: "net/minecraft/launchwrapper/1.12/launchwrapper-1.12.jar",
					size: 32999
				}
			}
		});

		version.libraries.push({
			downloads: {
				artifact: {
					url: "https://repo.maven.apache.org/maven2/org/ow2/asm/asm-debug-all/5.2/asm-debug-all-5.2.jar",
					path: "org/ow2/asm/asm-debug-all/5.2/asm-debug-all-5.2.jar",
					size: 387903
				}
			}
		});

		version.libraries.push({
			downloads: {
				artifact: {
					url: "https://repo.maven.apache.org/maven2/org/apache/logging/log4j/log4j-core/2.17.1/log4j-core-2.17.1.jar",
					path: "org/apache/logging/log4j/log4j-core/2.17.1/log4j-core-2.17.1.jar",
					size: 1789769
				}
			}
		});

		version.libraries.push({
			downloads: {
				artifact: {
					url: "https://repo.maven.apache.org/maven2/org/apache/logging/log4j/log4j-api/2.17.1/log4j-api-2.17.1.jar",
					path: "org/apache/logging/log4j/log4j-api/2.17.1/log4j-api-2.17.1.jar",
					size: 1789769
				}
			}
		});

		version.libraries.push({
			downloads: {
				artifact: {
					url: "https://libraries.minecraft.net/com/google/code/gson/gson/2.8.8/gson-2.8.8.jar",
					path: "com/google/code/gson/gson/2.8.8/gson-2.8.8.jar",
					size: 242047
				}
			}
		});

		version.libraries.push({
			downloads: {
				artifact: {
					url: "https://jitpack.io/com/github/JnCrMx/discord-game-sdk4j/v0.5.4/discord-game-sdk4j-v0.5.4.jar",
					path: "com/github/JnCrMx/discord-game-sdk4j/v0.5.4/discord-game-sdk4j-v0.5.4.jar",
					size: 202275
				}
			}
		});

		version.libraries.push({
			downloads: {
				artifact: {
					url: "https://repo.maven.apache.org/maven2/org/java-websocket/Java-WebSocket/1.5.3/Java-WebSocket-1.5.3.jar",
					path: "org/java-websocket/Java-WebSocket/1.5.3/Java-WebSocket-1.5.3.jar",
					size: 134209
				}
			}
		});

		version.libraries.push({
			downloads: {
				artifact: {
					url: "https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar",
					path: "org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar",
					size: 41125
				}
			}
		});

		for(var library of version.libraries) {
			if(library.name == "org.apache.logging.log4j:log4j-api:2.0-beta9"
					|| library.name == "org.apache.logging.log4j:log4j-core:2.0-beta9"
					|| library.name == "com.google.code.gson:gson:2.2.4") {
				continue;
			}

			if(!Library.isApplicable(library.rules)) {
				continue;
			}

			if(library.downloads.artifact != null) {
				await Library.download(library.downloads.artifact);
				jars.push(Library.getPath(library.downloads.artifact));
			}

			if(!alreadyRunning && library.natives != null) {
				var nativeName = library.natives[Utils.getOsName()];
				if(nativeName != null) {
					var download = library.downloads.classifiers[nativeName];
					if(download != null) {
						await Library.download(download);
						var zip = fs.createReadStream(Library.getPath(download))
							.pipe(unzipper.Parse({ forceStream: true }));

						for await(const entry of zip) {
							const fileName = entry.path;

							if(library.extract.exclude != null && library.extract.exclude.includes(fileName)) {
								await entry.autodrain();
							}
							else {
								var destination = nativesFolder + "/" + fileName;
								if(!fs.existsSync(path.dirname(destination))) {
									fs.mkdirSync(path.dirname(destination), { recursive: true });
								}

									await entry.pipe(fs.createWriteStream(nativesFolder + "/" + fileName));
							}
						}
					}
				}
			}
		}

		console.log("Downloading assets...");
		progress("Downloading assets...");

		var assetIndex = await Version.getAssetIndex(version);
		assetIndex.id = version.assetIndex.id;

		AssetIndex.save(assetIndex);

		for(var object of Object.values(assetIndex.objects)) {
			await AssetIndex.download(object);
		}

		console.log("Downloading client...");

		await Version.downloadJar(version);

		console.log("Downloading JRE...");
		progress("Downloading runtime...");

		var java;

		await new Promise((resolve) => {
			axios.get("https://api.adoptium.net/v3/assets/feature_releases/8/ga" +
					"?release_type=ga" +
					`&architecture=${os.arch()}` +
					"&heap_size=normal" +
					"&image_type=jre" +
					"&jvm_impl=hotspot" +
					`&os=${Utils.getJdkOsName()}` +
					"&page=0" +
					"&page_size=1" +
					"&project=jdk" +
					"&sort_method=DATE" +
					"&sort_order=DESC" +
					"&vendor=eclipse")
				.then(async(response) => {
					var jrePackage = response.data[0].binaries[0].package;
					var name = jrePackage.name;
					var jrePath = Utils.dataDirectory + "/jre/" + name;
					var dest = Utils.dataDirectory + "/jre/"
							+ name.substring(0, name.indexOf("."));
					var doneFile = dest + "/.done";
					if(!fs.existsSync(dest + "/.done")) {
						await Utils.download(jrePackage.link,
							jrePath, jrePackage.size);


						if(!fs.existsSync(dest)) {
							fs.mkdirSync(dest, {recursive: true});
						}

						if(name.endsWith(".tar.gz")) {
							await tar.x({
								file: jrePath,
								C: dest
							});
						}
						else if(name.endsWith(".zip")) {
							var zip = fs.createReadStream(jrePath)
									.pipe(unzipper.Parse({ forceStream: true }));

							for await(const entry of zip) {
								const fileName = entry.path;

								var destination = dest + "/" + fileName;
								if(!fs.existsSync(path.dirname(destination))) {
									fs.mkdirSync(path.dirname(destination), { recursive: true });
								}

								await entry.pipe(fs.createWriteStream(destination));
							}
						}

						fs.closeSync(fs.openSync(doneFile, "w"));

						fs.unlinkSync(jrePath);
					}

					java = dest + "/" + response.data[0].release_name
							+ "-jre/";
					switch(Utils.getOsName()) {
						case "linux":
							java += "bin/java";
							break;
						case "windows":
							java += "bin/java.exe";
							break;
						case "osx":
							java += "Contents/Home/bin/java";
							break;
					}

					resolve();
				});
		});

		console.log("Patching...");
		progress("Patching...");

		var versionToAdd;

		if(Config.data.optifine) {
			var optifinePatchedJar = versionFolder + "/" + version.id + "-patched-optifine.jar";
			var optifineSize = 2585014;
			var optifineVersion = "1.8.9_HD_U_M5";

			await Library.download({
					url: await Utils.getOptiFine(optifineVersion),
					size: optifineSize,
					path: optifineRelative
				});

			if(!fs.existsSync(optifinePatchedJar)) {
				await Patcher.patch(java, versionFolder, versionJar, optifinePatchedJar, optifine);
			}

			versionToAdd = optifinePatchedJar;
		}
		else {
			var mappedJar = versionFolder + "/" + version.id + "-searge.jar";

			if(!fs.existsSync(mappedJar)) {
				await Patcher.patch(java, versionFolder, versionJar, mappedJar);
			}

			versionToAdd = mappedJar;
		}

		console.log("Preparing Discord library...");
		progress("Downloading Discord library...");

		var discordNativeLibrary;

		var discordVersion = "2.5.6";
		var discordPath = `com/discord/game-sdk/${discordVersion}/game-sdk-${discordVersion}.zip`;
		var discordFile = Utils.librariesDirectory + "/" + discordPath;

		await Library.download({
				url: "https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip",
				size: 22808634,
				path: discordPath
			});

		var sdkZip = fs.createReadStream(discordFile)
					.pipe(unzipper.Parse({ forceStream: true }));

		var suffix;

		switch(Utils.getOsName()) {
			case "windows":
				suffix = ".dll";
				break;
			case "linux":
				suffix = ".so";
				break;
			case "osx":
				suffix = ".dylib";
				break;
		}

		var discordLibraryName = "discord_game_sdk" + suffix;
		discordNativeLibrary = nativesFolder + "/" + discordLibraryName;
		var searchPath = "lib/x86_64/" + discordLibraryName;

		for await(const entry of sdkZip) {
			const fileName = entry.path;

			if(fileName != searchPath) {
				await entry.autodrain();
			}
			else {
					await entry.pipe(fs.createWriteStream(discordNativeLibrary));
			}
		}
		progress("Starting...");

		var args = [];
		args.push("-Djava.library.path=" + nativesFolder);

		args.push("-Dio.github.solclient.client.version=" + Utils.version);
		args.push("-Dio.github.solclient.client.secret=" + secret);
		args.push("-Dmixin.target.mapid=searge");

		args.push("-Dlog4j2.formatMsgNoLookups=true"); // See https://hypixel.net/threads/understanding-the-recent-rce-exploit-for-minecraft-and-what-it-actually-means.4703643/. Thank you Draconish and danterus on Discord for informing me of this.

		args.push("-Xmx" + Config.data.maxMemory + "M");

		if(Utils.getOsName() == "windows") {
			args.push("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
		}

		args.push("-Dio.github.solclient.client.discord_lib=" + discordNativeLibrary);

		// Fix crashing on some non-English setups. Basically, Mixin is broken in the current version.
		// This shouldn't (at least I hope it doesn't) interfere with anything, and you can still select your own language from the menu.
		args.push("-Duser.language=en");
		args.push("-Duser.country=US");

		// Fix Log4j encoding.
		args.push("-Dfile.encoding=UTF-8");

		var classpathSeparator = Utils.getOsName() == "windows" ? ";" : ":";
		var classpath = "";

		args.push("-cp");

		for(var jar of jars) {
			classpath += jar;
			classpath += classpathSeparator;
		}

		classpath += versionToAdd;
		classpath += classpathSeparator;
		classpath += path.join(__dirname, "game/build/libs/game.jar");
		classpath += classpathSeparator;

		args.push(classpath);

		args.push("net.minecraft.launchwrapper.Launch");

		args.push("--version");
		args.push("Sol Client");

		var activeAccount = this.accountManager.activeAccount;

		args.push("--username");
		args.push(activeAccount.username);

		args.push("--uuid");
		args.push(activeAccount.uuid);

		if(server) {
			args.push("--server");
			args.push(server);
		}

		args.push("--accessToken");
		args.push(activeAccount.accessToken);

		args.push("--versionType");
		args.push("release");

		if(activeAccount.demo) {
			args.push("--demo");
		}

		args.push("--assetsDir");
		args.push(Utils.assetsDirectory);

		args.push("--assetIndex");
		args.push(version.assetIndex.id);

		var gameDirectory = Config.getGameDirectory(Utils.gameDirectory);

		args.push("--gameDir");
		args.push(gameDirectory);

		args.push("--tweakClass");
		args.push("io.github.solclient.client.tweak.Tweaker");

		var process = childProcess.spawn(java, args, { cwd: gameDirectory });
		this.games.push(process);

		let fullOutput = "";

		process.stdout.on("data", (data) => {
			var dataString = data.toString("UTF-8");
			fullOutput += dataString;

			if(dataString.endsWith("\n")) {
				dataString = dataString.substring(0, dataString.length - 1);
			}

			if(dataString.indexOf("message ") == 0) {
				var splitDataString = dataString.split(" ");
				if(splitDataString[1] === secret) {
					if(splitDataString[2] == "openUrl") {
						var openUrl = splitDataString[3];

						if(Utils.getOsName() == "windows") {
							openUrl = openUrl.substring(0, openUrl.length - 1);
						}

						if(openUrl.endsWith("§scshowinfolder§")) {
							openUrl = openUrl.substring(0, openUrl.length - 16);
							shell.showItemInFolder(url.fileURLToPath(openUrl));
						}
						else {
							shell.openExternal(openUrl);
						}
					}
				}
			}
			else {
				console.log("[Game/STDOUT] " + dataString);
			}
		});
		process.stderr.on("data", (data) => {
			var dataString = data.toString("UTF-8");
			fullOutput += dataString;
			console.error("[Game/STDERR] " + dataString);
		});

		process.on("exit", (code) => {
			if(code != 0) {
				console.error("Game crashed with exit code " + code);
				if(optifineVersion) {
					var optifineName = "OptiFine " + optifineVersion.replace(/_/g, " ");
				}

				ipcRenderer.send("crash", fullOutput, Config.getGameDirectory(Utils.gameDirectory) + "/logs/latest.log", optifineName);
			}
			else {
				console.log("Game exited with code 0");
			}
			this.games.splice(this.games.indexOf(process), 1);
		});

		callback();
	}

}

class Manifest {

	static #instance;

	static getManifest() {
		return new Promise((resolve, reject) => {
			if(Manifest.#instance != null) {
				resolve(Manifest.#instance);
				return;
			}

			https.get("https://launchermeta.mojang.com/mc/game/version_manifest.json",
					(response, error) => {
				if(error) {
					reject(error);
					return;
				}

				var body = "";
				response.on("data", (data) => {
					body += data;
				});
				response.on("end", () => {
					resolve(Manifest.#instance = JSON.parse(body));
				});
			});
		});
	}

	static getVersion(manifest, id) {
		return new Promise((resolve, reject) => {
			for(var version of manifest.versions) {
				if(version.id == id) {
					https.get(version.url, (response, error) => {
						if(error) {
							reject(error);
							return;
						}

						var body = "";
						response.on("data", (data) => {
							body += data;
						});
						response.on("end", () => {
							resolve(JSON.parse(body));
						});
					});
					return;
				}
			}
		});
	}

}

class Version {

	static getAssetIndex(version) {
		return new Promise((resolve) => {
			https.get(version.assetIndex.url, (response) => {
				if(response.code == 404) {
					resolve(null);
				}
				var body = "";
				response.on("data", (data) => {
					body += data;
				});
				response.on("end", () => {
					resolve(JSON.parse(body));
				});
			});
		});
	}

	static getPath(version) {
		return Utils.versionsDirectory + "/" + version.id;
	}

	static getJar(version) {
		return Version.getPath(version) + "/" + version.id + ".jar";
	}

	static getNatives(version) {
		return Version.getPath(version) + "/" + version.id + "-natives";
	}

	static downloadJar(version) {
		return Utils.download(version.downloads.client.url, Version.getJar(version), version.downloads.client.size);
	}

}

class Library {

	static getPath(download) {
		return Utils.librariesDirectory + "/" + download.path;
	}

	static isApplicable(rules) {
		if(rules == null || rules.length == 0) {
			return true;
		}

		var result = false;
		for(var rule of rules) {
			if(rule.os != null) {
				if(rule.os.name == Utils.getOsName()) {
					return rule.action == "allow";
				}
			}
			else {
				result = rule.action == "allow";
			}
		}
		return result;
	}

	static download(download) {
		return Utils.download(download.url, Library.getPath(download), download.size);
	}

}

class AssetIndex {

	static getBasePath(object) {
		return object.hash.substring(0, 2) + "/" + object.hash;
	}

	static getFilePath(object) {
		return Utils.assetObjectsDirectory + "/" + AssetIndex.getBasePath(object);
	}

	static getIndexPath(index) {
		return Utils.assetIndexesDirectory + "/" + index.id + ".json";
	}

	static save(index) {
		var indexPath = AssetIndex.getIndexPath(index);

		if(!fs.existsSync(path.dirname(indexPath))) {
			fs.mkdirSync(path.dirname(indexPath), { recursive: true });
		}

		return fs.writeFileSync(indexPath, JSON.stringify(index));
	}

	static getUrl(object) {
		return "http://resources.download.minecraft.net/" + AssetIndex.getBasePath(object);
	}

	static download(object) {
		return Utils.download(AssetIndex.getUrl(object), AssetIndex.getFilePath(object), object.size);
	}

}

module.exports = Launcher;
