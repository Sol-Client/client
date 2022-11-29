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
const { AccountManager } = require("./auth").AccountManager;

class Launcher {

	static instance = new Launcher();
	accountManager = null;
	games = [];

	async launch(callback, progress, server) {
		const versionId = "1.8.9";
		const jars = [];
		const versionFolder = Version.getPath(versionId);
		const versionJar = Version.getJar(versionId);
		const versionJson = Version.getJson(versionId);

		fs.mkdirSync(versionFolder, { recursive: true });

		let version;
		if(!fs.existsSync(versionJson)) {
			console.log("Downloading version data...");
			progress("Downloading version data...");
			const manifest = await Manifest.getManifest();
			version = await Manifest.getVersion(manifest, versionId);
			fs.writeFileSync(versionJson, JSON.stringify(version), "UTF-8");
		}
		else {
			version = JSON.parse(fs.readFileSync(versionJson, "UTF-8"))
		}
		
		const nativesFolder = Version.getNatives(versionId);
		const optifineRelative = "net/optifine/optifine/1.8.9_HD_U_M5/optifine-1.8.9_HD_U_M5.jar";
		const optifine = Utils.librariesDirectory + "/" + optifineRelative;
		const secret = crypto.randomBytes(32).toString("hex");
		const alreadyRunning = this.games.length > 0;

		if(!alreadyRunning && fs.existsSync(nativesFolder)) {
			fs.rmSync(nativesFolder, { recursive: true });
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

		for(let library of version.libraries) {
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
				let nativeName = library.natives[Utils.getOsName()];
				if(nativeName != null) {
					let download = library.downloads.classifiers[nativeName];
					if(download != null) {
						await Library.download(download);
						let zip = fs.createReadStream(Library.getPath(download))
							.pipe(unzipper.Parse({ forceStream: true }));

						for await(const entry of zip) {
							const fileName = entry.path;

							if(library.extract.exclude != null && library.extract.exclude.includes(fileName)) {
								await entry.autodrain();
							}
							else {
								let destination = nativesFolder + "/" + fileName;
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

		let assetIndex = await Version.getAssetIndex(version);
		assetIndex.id = version.assetIndex.id;

		AssetIndex.save(assetIndex);

		for(let object of Object.values(assetIndex.objects)) {
			await AssetIndex.download(object);
		}

		console.log("Downloading client...");
		progress("Downloading client...");

		await Version.downloadJar(version);

		console.log("Downloading JRE...");
		progress("Downloading runtime...");

		let jrePath = Config.data.jrePath;
		let java;

		if(jrePath) {
			java = path.join(jrePath, "bin/java")
		}

		if(!jrePath || !java || !fs.existsSync(java)) {
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
						const jreBinary = response.data[0].binaries[0];
						const jrePackage = jreBinary.package;
						const packageName = jrePackage.name;
						const jrePath = Utils.dataDirectory + "/jre/" + packageName;
						const dest = Utils.dataDirectory + "/jre/" + jrePackage.checksum;
						const doneFile = dest + "/.done";

						if(!fs.existsSync(dest + "/.done")) {
							await Utils.download(jrePackage.link,
								jrePath, jrePackage.size);


							if(!fs.existsSync(dest)) {
								fs.mkdirSync(dest, { recursive: true });
							}

							if(packageName.endsWith(".tar.gz")) {
								await tar.x({
									file: jrePath,
									C: dest
								});
							}
							else if(packageName.endsWith(".zip")) {
								const zip = fs.createReadStream(jrePath)
										.pipe(unzipper.Parse({ forceStream: true }));

								for await(const entry of zip) {
									const fileName = entry.path;

									if(fileName.endsWith("/")) {
										continue;
									}

									const destination = dest + "/" + fileName;
									
									if(!fs.existsSync(path.dirname(destination))) {
										fs.mkdirSync(path.dirname(destination), { recursive: true });
									}

									await entry.pipe(fs.createWriteStream(destination));
								}
							}

							fs.closeSync(fs.openSync(doneFile, "w"));

							fs.unlinkSync(jrePath);
						}

						java = dest + "/" + jreBinary.scm_ref.substring(0, jreBinary.scm_ref.lastIndexOf("_")) + "-jre";
						switch(Utils.getOsName()) {
							case "linux":
								java = path.join(java, "bin/java");
								break;
							case "windows":
								java = path.join(java, "bin/java.exe");
								break;
							case "osx":
								java = path.join(java, "Contents/Home/bin/java");
								break;
						}

						resolve();
					});
			});
		}

		console.log("Patching...");
		progress("Patching...");

		let versionToAdd;
		let optifineVersion;

		if(Config.data.optifine) {
			let optifinePatchedJar = versionFolder + "/" + version.id + "-patched-optifine.jar";
			let optifineSize = 2585014;
			optifineVersion = "1.8.9_HD_U_M5";

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
			const mappedJar = versionFolder + "/" + version.id + "-searge.jar";

			if(!fs.existsSync(mappedJar)) {
				await Patcher.patch(java, versionFolder, versionJar, mappedJar);
			}

			versionToAdd = mappedJar;
		}

		console.log("Preparing Discord library...");
		progress("Downloading Discord library...");

		let discordNativeLibrary;

		let discordVersion = "2.5.6";
		let discordPath = `com/discord/game-sdk/${discordVersion}/game-sdk-${discordVersion}.zip`;
		let discordFile = Utils.librariesDirectory + "/" + discordPath;

		await Library.download({
				url: "https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip",
				size: 22808634,
				path: discordPath
			});

		let sdkZip = fs.createReadStream(discordFile)
				.pipe(unzipper.Parse({ forceStream: true }));

		let suffix;

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

		let discordLibraryName = "discord_game_sdk" + suffix;
		discordNativeLibrary = nativesFolder + "/" + discordLibraryName;
		let searchPath = "lib/x86_64/" + discordLibraryName;

		for await(const entry of sdkZip) {
			const fileName = entry.path;

			if(fileName != searchPath) {
				await entry.autodrain();
			}
			else {
				await entry.pipe(fs.createWriteStream(discordNativeLibrary));
			}
		}
		console.log("Starting...");
		progress("Starting...");

		let args = [];
		args.push("-Djava.library.path=" + nativesFolder);

		args.push("-Dio.github.solclient.client.version=" + Utils.version);
		args.push("-Dio.github.solclient.client.secret=" + secret);

		args.push("-Dlog4j2.formatMsgNoLookups=true");

		args.push("-Xmx" + Config.data.maxMemory + "M");

		if(Utils.getOsName() == "windows") {
			args.push("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
		}

		args.push("-Dio.github.solclient.client.discord_lib=" + discordNativeLibrary);

		// Fix crashing on some non-English setups. Basically, Mixin is broken in the current version.
		// This shouldn't (at least I hope it doesn't) interfere with anything, and you can still select your own language from the menu.
		// Update: it can conflict with decimal formatting - 1.3 will always appear in that format rather than 1,3.
		args.push("-Duser.language=en");
		args.push("-Duser.country=US");

		// Fix Log4j encoding.
		args.push("-Dfile.encoding=UTF-8");

		// Add custom args.
		args.push(...Config.getJvmArgs());

		let classpathSeparator = Utils.getOsName() == "windows" ? ";" : ":";
		let classpath = "";

		args.push("-cp");

		for(let jar of jars) {
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

		let activeAccount = this.accountManager.activeAccount;

		args.push("--username");
		args.push(activeAccount.username);

		args.push("--uuid");
		args.push(activeAccount.uuid);

		if(server) {
			args.push("--server");
			args.push(server);
		}

		args.push("--accessToken");
		args.push(await this.accountManager.realToken(activeAccount));

		args.push("--versionType");
		args.push("release");

		if(activeAccount.demo) {
			args.push("--demo");
		}

		args.push("--assetsDir");
		args.push(Utils.assetsDirectory);

		args.push("--assetIndex");
		args.push(version.assetIndex.id);

		let gameDirectory = Config.getGameDirectory(Utils.gameDirectory);

		args.push("--gameDir");
		args.push(gameDirectory);

		args.push("--tweakClass");
		args.push("io.github.solclient.client.tweak.Tweaker");

		let process = childProcess.spawn(java, args, { cwd: gameDirectory });
		this.games.push(process);

		let fullOutput = "";

		process.stdout.on("data", (data) => {
			let dataString = data.toString("UTF-8");
			fullOutput += dataString;

			if(dataString.endsWith("\n")) {
				dataString = dataString.substring(0, dataString.length - 1);
			}

			console.log("[Game/STDOUT] " + dataString);
		});
		process.stderr.on("data", (data) => {
			let dataString = data.toString("UTF-8");
			fullOutput += dataString;
			console.error("[Game/STDERR] " + dataString);
		});

		process.on("exit", (code) => {
			if(code != 0) {
				console.error("Game crashed with exit code " + code);
				
				let optifineName;
				
				if(optifineVersion) {
					optifineName = "OptiFine " + optifineVersion.replace(/_/g, " ");
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

				let body = "";
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
			for(let version of manifest.versions) {
				if(version.id == id) {
					https.get(version.url, (response, error) => {
						if(error) {
							reject(error);
							return;
						}

						let body = "";
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
				let body = "";
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
		return Utils.versionsDirectory + "/" + version;
	}

	static getJar(version) {
		return Version.getPath(version) + "/" + version + ".jar";
	}

	static getJson(version) {
		return Version.getPath(version) + "/" + version + ".json";
	}

	static getNatives(version) {
		return Version.getPath(version) + "/" + version + "-natives";
	}

	static downloadJar(version) {
		return Utils.download(version.downloads.client.url, Version.getJar(version.id), version.downloads.client.size);
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

		let result = false;
		for(let rule of rules) {
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
		let indexPath = AssetIndex.getIndexPath(index);

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
