const electronInstaller = require("electron-winstaller");
async function build() {
	await electronInstaller.createWindowsInstaller({
		appDirectory: "dist/sol-client-launcher-x64/",
		outDirectory: "dist/installers/",
		authors: "mcblueparrot",
		exe: "Sol Client Setup.exe"
	});
}

build();
