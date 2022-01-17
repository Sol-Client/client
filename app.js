const Launcher = require("./launcher");
const Utils = require("./utils");
const Config = require("./config");
const launcher = Launcher.instance;
const { ipcRenderer, shell } = require("electron");
const { MicrosoftAuthService, YggdrasilAuthService, Account } = require("./auth");
const microsoftAuthService = MicrosoftAuthService.instance;
const yggdrasilAuthService = YggdrasilAuthService.instance;
const fs = require("fs");
const msmc = require("msmc");
const os = require("os");
const nbt = require("nbt");

Utils.init();
Config.init(Utils.minecraftDirectory);
Config.load();

ipcRenderer.on("close", (event) => {
	ipcRenderer.send("quit", launcher.games.length < 1);
});

ipcRenderer.on("quitGame", (event) => {
	for(game of launcher.games) {
		game.kill();
	}
	launcher.games = [];
	ipcRenderer.send("quit", true);
});

window.addEventListener("DOMContentLoaded", () => {
	document.getElementsByTagName("title")[0].innerText += " " + Utils.version;

	const playButton = document.getElementById("launch-button");
	const microsoftLoginButton = document.querySelector(".microsoft-login-button");
	const mojangLoginButton = document.querySelector(".mojang-login-button");
	const accountButton = document.querySelector(".account-button");

	const login = document.querySelector(".login");
	const mojangLogin = document.querySelector(".mojang-login");
	const main = document.querySelector(".main");

	function saveAccount(account) {
		fs.writeFileSync(Utils.accountFile, JSON.stringify(account));
	}

	function updateAccount(account) {
		document.querySelector(".account-button").innerText = "🗘 " + account.username;
	}

	function updateMinecraftFolder() {
		document.querySelector(".minecraft-folder-path").innerText = Config.data.minecraftFolder;
	}

	updateMinecraftFolder();

	if(fs.existsSync(Utils.accountFile)) {
		var account = Account.from(JSON.parse(fs.readFileSync(Utils.accountFile)));
		launcher.account = account;
		main.style.display = "block";
		updateAccount(account);
	}
	else {
		login.style.display = "block";
	}

	var launching = false;
	var loggingIn = false;

	accountButton.onclick = () => {
		main.style.display = "none";
		login.style.display = "block";
	};

	microsoftLoginButton.onclick = () => {
		if(!loggingIn) {
			loggingIn = true;
			microsoftLoginButton.innerText = "...";
			ipcRenderer.send("msa");
		}
	};

	ipcRenderer.on("msa", (event, result) => {
		loggingIn = false;
		microsoftLoginButton.innerText = "Microsoft Account";
		result = JSON.parse(result);
		if(msmc.errorCheck(result)) {
			if(result.type == "Cancelled") {
				return;
			}
			alert("Could not log in: " + result.type);
			return;
		}
		var account = microsoftAuthService.authenticate(result.profile);
		launcher.account = account;
		login.style.display = "none";
		main.style.display = "block";
		saveAccount(account);
		updateAccount(account);
	})

	mojangLoginButton.onclick = () => {
		if(!loggingIn) {
			login.style.display = "none";
			mojangLogin.style.display = "block";
		}
	};

	async function play(server) {
		if(!launching) {
			launching = true;
			playButton.innerText = "...";
			var valid = await launcher.account.getService().validate(launcher.account);
			if(!valid) {
				var result = await launcher.account.getService().refresh(launcher.account);
				if(!result) {
					main.style.display = "none";
					login.style.display = "block";
					playButton.innerText = "Play";
					launching = false;
					return;
				}
				launcher.account = result;
				saveAccount(launcher.account);
			}
			launcher.launch(() => {
				playButton.innerText = "Play";
				launching = false;
			}, server);
		}
	}

	playButton.onclick = () => play();

	document.querySelector(".back-to-login-button").onclick = () => {
		mojangLogin.style.display = "none";
		login.style.display = "block";
	};

	document.querySelector(".about-tab").onclick = () => switchToTab("about");

	document.querySelector(".settings-tab").onclick = () => switchToTab("settings");

	document.querySelector(".minecraft-folder").onclick = () => ipcRenderer.send("directory");

	ipcRenderer.on("directory", (event, file) => {
		Config.data.minecraftFolder = file;
		Config.save();
		updateMinecraftFolder();
		updateServers();
	});

	document.querySelector(".devtools").onclick = () => ipcRenderer.send("devtools");

	function updateServers() {
		var serversList = document.querySelector(".quick-servers");
		var serversFile = Config.getGameDirectory(Utils.gameDirectory) + "/servers.dat";
		var serverText = document.querySelector(".quick-join-text");

		serversList.innerHTML = "";

		if(fs.existsSync(serversFile)) {
			nbt.parse(fs.readFileSync(serversFile), (error, data) => {
				if(error) {
					throw error;
				}

				var servers = data.value.servers.value.value;

				for(var i = 0; i < servers.length && i < 5; i++) {
					 // first time I've ever needed to use the let keyword
					let server = servers[i];
					let serverIndex = i;

					let serverElement = document.createElement("span");

					serverElement.onmouseenter = () => {
						serverText.innerText = server.name.value;
					};

					serverElement.onmouseout = () => {
						serverText.innerText = "Play Server";
					};

					serverElement.onclick = () => {
						play("§sc§" + serverIndex);
					}

					serverElement.classList.add("server");
					serverElement.innerHTML = `
						${server.icon ? `<img src="data:image/png;base64,${server.icon.value}"/>` : `<img src="unknown_server.svg"/>`}`;

					serversList.appendChild(serverElement);
				}
			});
		}
	}
	updateServers();

	var memory = document.querySelector(".memory");
	var memoryLabel = document.querySelector(".memory-label");

	memory.max = os.totalmem() / 1024 / 1024;
	memory.value = Config.data.maxMemory;

	var optifine = document.querySelector(".optifine");
	optifine.checked = Config.data.optifine;
	optifine.onchange = () => {
		Config.data.optifine = optifine.checked;
		Config.save();
	}

	var discord = document.querySelector(".discord");
	discord.checked = Config.data.discord;
	discord.onchange = () => {
		Config.data.discord = discord.checked;
		Config.save();
	}

	function updateMemoryLabel() {
		memoryLabel.innerText = (memory.value / 1024).toFixed(1) + " GB";
		Config.data.maxMemory = memory.value;
	}

	memory.oninput = updateMemoryLabel;
	memory.onchange = Config.save;

	updateMemoryLabel();

	function switchToTab(tab) {
			document.querySelector(".about").style.display = "none";
			document.querySelector(".settings").style.display = "none";

			playButton.style.display = null;

			document.querySelector(".about-tab").classList.remove("selected-tab");
			document.querySelector(".settings-tab").classList.remove("selected-tab");
			document.querySelector("." + tab).style.display = "block";
			document.querySelector("." + tab + "-tab").classList.add("selected-tab");
	}

	const loginButtonMojang = document.querySelector(".login-button-mojang");
	const emailField = document.getElementById("username");
	const passwordField = document.getElementById("password");
	const errorMessage = document.querySelector(".error-message");

	loginButtonMojang.onclick = async() => {
		if(!loggingIn) {
			loggingIn = true;
			loginButtonMojang.innerText = "...";
			try {
				var account = await yggdrasilAuthService.authenticateUsernamePassword(emailField.value, passwordField.value);
				launcher.account = account;
				mojangLogin.style.display = "none";
				main.style.display = "block";
				emailField.value = "";
				passwordField.value = "";
				errorMessage.innerText = "";
				saveAccount(account);
				updateAccount(account);
			}
			catch(error) {
				errorMessage.innerText = "Could not log in";
			}
			loginButtonMojang.innerText = "Log In";
			loggingIn = false;
		}
	};

	for(var element of document.querySelectorAll(".open-in-browser")) {
		const href = element.href;
		element.href = "javascript:void(0);";
		element.onclick = function(event) {
			shell.openExternal(href);
		};
	}
});
