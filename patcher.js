const temp = require("temp").track();
const childProcess = require("child_process");
const Utils = require("./utils");
const unzipper = require("unzipper");
const fs = require("fs");
const archiver = require("archiver");

class Patcher {

	static async patch(java, versionJar, optiFine) {
		var tempFolder = temp.mkdirSync("deobf");

		var inputJar = versionJar;
		if(optiFine) {
			var optiFineMod = tempFolder + "/optifine-mod.jar";
			inputJar = tempFolder + "/optifine-patched.jar";
			await new Promise((resolve) => {
				var process = childProcess.spawn(java, [
					"-cp",
					optiFine,
					"optifine.Patcher",
					versionJar,
					optiFine,
					optiFineMod
				]);

				process.on("exit", resolve);

				process.stdout.on("data", (data) => console.log(data.toString("UTF-8")));
				process.stderr.on("data", (data) => console.error(data.toString("UTF-8")));
			});

			await new Promise(async(resolve) => {
				var optiFinePatchedArchiver = archiver("zip");

				optiFinePatchedArchiver.pipe(fs.createWriteStream(inputJar));

				async function insert(jar) {
					var zip = fs.createReadStream(jar).pipe(
							unzipper.Parse({ forceStream: true }));

					for await(const entry of zip) {
						const fileName = entry.path;

						if(fileName.startsWith("META-INF")) {
							entry.autodrain();
							continue;
						}

						console.log("appending " + fileName + "...");
						optiFinePatchedArchiver.append(await entry.buffer(), { name: entry.path });
					}
				}

				await insert(versionJar);
				await insert(optiFineMod);

				console.log("done");

	//			var vanillaZip = fs.createReadStream(versionJar).pipe(
	//				unzipper.Parse({ forceStream: true }));

	//			for await(const entry of vanillaZip) {
	//				const fileName = entry.path;

	//				if(fileName.startsWith("META-INF")) {
	//					entry.autodrain();
	//					continue;
	//				}

	//				console.log(entry.path);

	//				await new Promise(async(resolve) => {
	//								console.log("doing");
	//					optiFinePatchedPacker.entry(await entry.buffer(),
	//							{
	//								name: fileName
	//							},
	//							(error, entry) => {
	//								console.log("done");
	//								resolve();
	//							})
	//				});
	//			}

	//			var modZip = fs.createReadStream(optiFineMod).pipe(
	//				unzipper.Parse({ forceStream: true }));

	//			for await(const entry of modZip) {
	//				const fileName = entry.path;

	//				await new Promise(async(resolve) => {
	//					optiFinePatchedPacker.entry(await entry.buffer(),
	//							{
	//								name: fileName
	//							},
	//							(error, entry) => {
	//								resolve();
	//							})
	//				});
	//			}

				optiFinePatchedArchiver.on("close", resolve);
				optiFinePatchedArchiver.on("end", resolve);
				optiFinePatchedArchiver.on("finish", resolve);

				console.log("finalising...");

				optiFinePatchedArchiver.finalize();
			});
		}

		var mapped = tempFolder + "mapped.jar";
		var specialSource = tempFolder + "SpecialSource.jar";
		var joinedSrg = tempFolder + "joined.srg";
		var mcpZip = tempFolder + "mcp.zip";

		await Utils.download("https://repo.maven.apache.org/maven2/net/md-5/SpecialSource/1.7.4/SpecialSource-1.7.4-shaded.jar", specialSource, 1526537);

		await Utils.download("https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp/1.8.9/mcp-1.8.9-srg.zip", mcpZip, 471509);

		var zip = fs.createReadStream(mcpZip).pipe(
				unzipper.Parse({ forceStream: true }));

		for await(const entry of zip) {
			const fileName = entry.path;

			if(fileName != "joined.srg") {
				await entry.autodrain();
			}
			else {
				await entry.pipe(fs.createWriteStream(joinedSrg));
			}
		}

		await new Promise((resolve) => {
			var process = childProcess.spawn(java, [
				"-jar",
				specialSource,
				"--in-jar",
				inputJar,
				"--out-jar",
				mapped,
				"--srg-in",
				joinedSrg
			]);

			process.on("exit", resolve);

			process.stdout.on("data", (data) => console.log(data.toString("UTF-8")));
			process.stderr.on("data", (data) => console.error(data.toString("UTF-8")));
		});
		return mapped;
	}

}

module.exports = Patcher;
