const electronInstaller = require("electron-winstaller");
const process = require("process");

async function build() {
	console.log("Building installer...");
	try {
		await electronInstaller.createWindowsInstaller({
			appDirectory: "dist\sol-client-launcher-x64",
			outDirectory: "dist\installers",
			authors: "mcblueparrot",
			exe: "Sol Client Setup.exe"
		});
	}
	catch(error) {
		console.error(error.message);
		process.exit(1);
	}
}

build();
