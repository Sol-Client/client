const childProcess = require("child_process");
const Utils = require("./utils");
const unzipper = require("unzipper");
const fs = require("fs");
const archiver = require("archiver");

class Patcher {

	static async patch(java, versionFolder, versionJar, outputFile, optiFine) {
		let tempFolder = versionFolder + "/patch/";
		
		if(fs.existsSync(tempFolder)) {
			fs.rmdirSync(tempFolder, { recursive: true });
		}
		
		fs.mkdirSync(tempFolder);

		let inputJar = versionJar;
		if(optiFine) {
			let optiFineMod = tempFolder + "/optifine-mod.jar";
			inputJar = tempFolder + "/optifine-patched.jar";
			await new Promise((resolve) => {
				let process = childProcess.spawn(java, [
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
				let optiFinePatchedArchiver = archiver("zip");

				optiFinePatchedArchiver.pipe(fs.createWriteStream(inputJar));

				async function insert(jar) {
					let zip = fs.createReadStream(jar).pipe(
							unzipper.Parse({ forceStream: true }));

					for await(const entry of zip) {
						const fileName = entry.path;

						if(fileName.startsWith("META-INF")) {
							entry.autodrain();
							continue;
						}

						optiFinePatchedArchiver.append(await entry.buffer(), { name: entry.path });
					}
				}

				await insert(versionJar);
				await insert(optiFineMod);

				optiFinePatchedArchiver.on("close", resolve);
				optiFinePatchedArchiver.on("end", resolve);
				optiFinePatchedArchiver.on("finish", resolve);

				optiFinePatchedArchiver.finalize();
			});
		}

		let mapped = tempFolder + "/mapped.jar";
		let specialSource = tempFolder + "/SpecialSource.jar";
		let joinedSrg = tempFolder + "/joined.srg";
		let mcpZip = tempFolder + "/mcp.zip";

		await Utils.download("https://repo.maven.apache.org/maven2/net/md-5/SpecialSource/1.7.4/SpecialSource-1.7.4-shaded.jar", specialSource, 1526537);

		await Utils.download("https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp/1.8.9/mcp-1.8.9-srg.zip", mcpZip, 471509);

		let zip = fs.createReadStream(mcpZip).pipe(
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
			let process = childProcess.spawn(java, [
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

		fs.renameSync(mapped,  outputFile);
		fs.rmdirSync(tempFolder, { recursive: true });
	}

}

module.exports = Patcher;
