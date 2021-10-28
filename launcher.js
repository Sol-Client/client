if(require("electron-squirrel-startup")) return; // If on Windows, handle squirrel-related stuff.

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

class Launcher {

	static instance = new Launcher();
	account = null;

	launch(callback) {
		Manifest.getManifest((manifest) => {
			Manifest.getVersion(manifest, "1.8.9", async(version) => {
				var jars = [];
				var versionFolder = Version.getPath(version);
				var versionJar = Version.getJar(version);
				var nativesFolder = Version.getNatives(version);
				var optifineRelative = "net/optifine/optifine/1.8.9_HD_U_M5/optifine-1.8.9_HD_U_M5.jar";
				var optifine = Utils.librariesDirectory + "/" + optifineRelative;
				version.libraries.push({
					downloads: {
						artifact: {
							url: "https://repo.maven.apache.org/maven2/org/slick2d/slick2d-core/1.0.2/slick2d-core-1.0.2.jar",
							path: "org/slick2d/slick2d-core/1.0.2/slick2d-core-1.0.2.jar",
							size: 590652
						}
					}
				});
				version.libraries.push({
					downloads: {
						artifact: {
							url: "https://repo.codemc.io/repository/maven-public/com/logisticscraft/occlusionculling/0.0.5-SNAPSHOT/occlusionculling-0.0.5-20210620.172315-1.jar",
							path: "com/logisticscraft/occlusionculling/0.0.5-SNAPSHOT/occlusionculling-0.0.5-20210620.172315-1.jar",
							size: 12926
						},
					}
				});
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
				for(var library of version.libraries) {
					if(!Library.isApplicable(library.rules)) {
						continue;
					}
					if(library.downloads.artifact != null) {
						await Library.download(library.downloads.artifact);
						jars.push(Library.getPath(library.downloads.artifact));
					}
					if(library.natives != null) {
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

				for(var object of Object.values((await Version.getAssetIndex(version)).objects)) {
					await AssetIndex.download(object);
				}

				await Version.downloadJar(version);

				var java;

				await new Promise((resolve) => {
					axios.get("https://api.adoptopenjdk.net/v3/assets/feature_releases/8/ga" +
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
							"&vendor=adoptopenjdk")
						.then(async(response) => {
							var jrePackage = response.data[0].binaries[0].package;
							var name = jrePackage.name;
							var path = Utils.minecraftDirectory + "/jre/" + name;
							var dest = Utils.minecraftDirectory + "/jre/"
									+ name.substring(0, name.indexOf("."));
							var doneFile = dest + "/.done";
							if(!fs.existsSync(dest + "/.done")) {
								await Utils.download(jrePackage.link,
									path, jrePackage.size);


								if(!fs.existsSync(dest)) {
									fs.mkdirSync(dest, {recursive: true});
								}

								if(name.endsWith(".tar.gz")) {
									await tar.x({
										file: path,
										C: dest
									});
								}
								else if(name.endsWith(".zip")) {
									await fs.createReadStream(path).pipe(unzipper.Extract({path: dest}));
								}

								fs.closeSync(fs.openSync(doneFile, "w"));

								fs.unlinkSync(path);
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

				var args = [];
				args.push("-Djava.library.path=" + nativesFolder);
				args.push("-Dme.mcblueparrot.client.version=" + Utils.version);

				args.push("-Xmx" + Config.data.maxMemory + "M");

				if(Utils.getOsName() == "windows") {
					args.push("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
				}

				if(Utils.getOsName() == "osx") {
					args.push("-XstartOnFirstThread");
				}

				var classpathSeparator = Utils.getOsName() == "windows" ? ";" : ":";
				var classpath = "";

				args.push("-cp");

				for(var jar of jars) {
					classpath += jar;
					classpath += classpathSeparator;
				}

				classpath += Version.getJar(version);

				classpath += classpathSeparator;

				classpath += path.join(__dirname, "game/build/libs/game.jar");

				classpath += classpathSeparator;

				var optifineSize = 2585014;

				if(!Utils.isAlreadyDownloaded(optifine, optifineSize)) {
					await Library.download({
							url: await Utils.getOptiFine(),
							size: optifineSize,
							path: optifineRelative
						});
				}

				classpath += optifine;

				console.log(classpath);

				args.push(classpath);

				args.push("net.minecraft.launchwrapper.Launch");

				args.push("--version");
				args.push("Sol Client");

				args.push("--username");
				args.push(this.account.username);

				args.push("--uuid");
				args.push(this.account.uuid);

				args.push("--accessToken");
				args.push(this.account.accessToken);

				args.push("--userType");
				args.push("mojang");

				args.push("--versionType");
				args.push("release");

				if(this.account.demo) {
					args.push("--demo");
				}

				args.push("--assetsDir");
				args.push(Utils.assetsDirectory);

				args.push("--assetIndex");
				args.push(version.assetIndex.id);

				args.push("--gameDir");
				args.push(Utils.gameDirectory);

				args.push("--tweakClass");
				args.push("me.mcblueparrot.client.tweak.Tweaker");

				args.push("--tweakClass");
				args.push("optifine.OptiFineTweaker");

				var process = childProcess.spawn(java, args, { cwd: Utils.minecraftDirectory });

				process.stdout.on("data", (data) => console.log(data.toString("UTF-8"))); // Don't know why you need this.

				process.stderr.on("data", (data) => console.error(data.toString("UTF-8"))); // Don't know why you need this.

				callback();
			});
		});
	}

}

class Manifest {

	static #instance;

	static getManifest(callback) {
		if(Manifest.#instance != null) {
			callback(Manifest.#instance);
			return;
		}
		https.get("https://launchermeta.mojang.com/mc/game/version_manifest.json",
				(response) => {
			var body = "";
			response.on("data", (data) => {
				body += data;
			});
			response.on("end", () => {
				callback(Manifest.#instance = JSON.parse(body));
			});
		});
	}

	static getVersion(manifest, id, callback) {
		for(var version of manifest.versions) {
			if(version.id == id) {
				https.get(version.url, (response) => {
					var body = "";
					response.on("data", (data) => {
						body += data;
					});
					response.on("end", () => {
						callback(JSON.parse(body));
					});
				});
				return;
			}
		}
		callback(null);
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

	static getUrl(object) {
		return "http://resources.download.minecraft.net/" + AssetIndex.getBasePath(object);
	}

	static download(object) {
		return Utils.download(AssetIndex.getUrl(object), AssetIndex.getFilePath(object), object.size);
	}

}

module.exports = Launcher;
