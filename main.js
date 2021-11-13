async function run() {
	if(require("electron-squirrel-startup")) return;

	const Updater = require("./updater");
	const Utils = require("./utils");

	Utils.init();

	if(!require("electron-is-dev") && Utils.getOsName() == "windows" && (await Updater.update())) {
		return;
	}

	const {app, BrowserWindow, ipcMain, dialog} = require("electron");
	const path = require("path");
	const msmc = require("msmc");

	var window;

	function createWindow() {
		window = new BrowserWindow({
			width: 800,
			height: 600,
			icon: __dirname + "/assets/icon.png",
			webPreferences: {
				preload: path.join(__dirname, "app.js")
			}
		});

		window.loadFile("app.html");
		window.webContents.openDevTools();
		window.setMenu(null);
	}

	ipcMain.on("msa", async(event) => {
		msmc.fastLaunch("electron", () => {})
				.then((result) => {
			event.sender.send("msa", JSON.stringify(result));
		});
	});

	ipcMain.on("devtools", (event) => window.webContents.openDevTools());

	ipcMain.on("quit", (event, result) => {
		if(result) {
			app.quit();
		}
		else {
			if(dialog.showMessageBoxSync(window, {
						title: "Quit Launcher?",
						message: "If you quit, the game will be closed.",
						type: "question",
						buttons: [
							"Don't quit",
							"Quit game and launcher"
						]
					}) == 1) {
				event.sender.send("quitGame");
			}
		}
	});

	app.whenReady().then(() => {
		createWindow();
	});
}

run();
