if(require("electron-squirrel-startup")) return;

const Updater = require("./updater");
const Utils = require("./utils");

Utils.init();

if(!require("electron-is-dev") && Utils.getOsName() == "windows" && Updater.update(os.type())) {
	return;
}

const {app, BrowserWindow, ipcMain} = require("electron");
const path = require("path");
const msmc = require("msmc");

var window;

function createWindow() {
	window = new BrowserWindow({
		width: 800,
		height: 600,
		webPreferences: {
			preload: path.join(__dirname, "app.js")
		}
	});

	window.loadFile("app.html");
	window.setMenu(null);
	window.webContents.openDevTools();
}

ipcMain.on("msa", async(event) => {
	msmc.fastLaunch("electron", () => {})
			.then((result) => {
		event.sender.send("msa", JSON.stringify(result));
	});
});

app.whenReady().then(() => {
	createWindow();

	app.on("window-all-closed", function() {
		app.quit(); // Don't implement Apple's worst feature
	});
});
