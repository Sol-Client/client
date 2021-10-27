if(require("electron-squirrel-startup")) return;

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
}

ipcMain.on("msa", async(event) => {
	msmc.fastLaunch("electron", (update) => {})
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
