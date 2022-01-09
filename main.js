async function run() {
	if(require("electron-squirrel-startup")) return;

	const Updater = require("./updater");
	const Utils = require("./utils");

	Utils.init();

	if(!require("electron-is-dev") && Utils.getOsName() == "windows" && (await Updater.update())) {
		return;
	}

	const { app, BrowserWindow, ipcMain, dialog, shell } = require("electron");
	const path = require("path");
	const msmc = require("msmc");
	const hastebin = require("hastebin");

	var window;
	var canQuit = false;

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
		window.setMenu(null);

		window.on("close", (event) => {
			if(!canQuit) {
				event.preventDefault();
				window.webContents.send("close");
			}
		});

		ipcMain.on("directory", async(event) => {
			var result = await dialog.showOpenDialog(window,
				{
					title: "Select Minecraft Folder",
					properties: ["openDirectory" ]
				}
			);

			var file = result.filePaths[0];

			if(!result.canceled && file) {
				event.sender.send("directory", file);
			}
		});
	}

	ipcMain.on("msa", async(event) => {
		msmc.fastLaunch("electron", () => {})
				.then((result) => {
			event.sender.send("msa", JSON.stringify(result));
		});
	});

	ipcMain.on("crash", async(_event, report, file, optifine) => {
		var option = dialog.showMessageBoxSync(window, {
			title: "Game Crashed",
			message: `The game has crashed.
You may submit a report on GitHub, so it can be fixed.
This may include chat messages.
If you have private messages, try reproducing this issue again.`,
			type: "question",
			buttons: [
				"Do Nothing",
				"Open log file",
				"Submit a report"
			]
		});

		// Indentation matters.

		if(option == 1) {
			shell.openPath(file);
		}
		if(option != 2) {
			return;
		}

		var crashReportText = "Add any applicable crash reports, making sure not to include any personal information. It is most important that you do not include the session id.";
		if(report) {
			report = report.replace(/\[.*\] \[.*\]: \(Session ID is .{3,}\)/gm, "<censored>");

			hasteUrl = await hastebin.createPaste(report, {
				raw: true,
				contentType: "text/plain",
				server: "https://www.toptal.com/developers/hastebin/"
			});
			hasteUrl = "https://www.toptal.com/developers/hastebin/" + hasteUrl.substring(hasteUrl.lastIndexOf('/') + 1) + ".txt";

			crashReportText = `<!-- Do not change this unless you need to. -->
[Game Log on Hastebin](${hasteUrl})`
		}

		var running = `Running Sol Client v${Utils.version}`;

		if(optifine) {
			running += " with " + optifine;
		}
		running += ".";

		var url = new URL("https://github.com/TheKodeToad/Sol-Client/issues/new/")
		url.searchParams.set("body", `## Description
A description of the problem that is occurring.
## Steps to Reproduce
1. What did you do...
2. ...to crash the game?
## Client Version
${running}
## Logs/Crash Report
${crashReportText}
`);
		url.searchParams.set("title", "Short Description")
		url.searchParams.set("labels", "bug");
		shell.openExternal(url.toString());
	});

	ipcMain.on("devtools", () => window.webContents.openDevTools());

	ipcMain.on("quit", (event, result) => {
		if(result) {
			canQuit = true;
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
