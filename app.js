const Launcher = require("./launcher");
const Utils = require("./utils");
const Config = require("./config");
const launcher = Launcher.instance;
const { ipcRenderer, shell } = require("electron");
const { MicrosoftAuthService, YggdrasilAuthService, Account, AccountManager } = require("./auth");
const microsoftAuthService = MicrosoftAuthService.instance;
const yggdrasilAuthService = YggdrasilAuthService.instance;
const fs = require("fs");
const msmc = require("msmc");
const os = require("os");
const nbt = require("nbt");
const vm = require("vm");
const url = require("url");
const path = require("path");
const axios = require("axios");

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
	const launchNote = document.getElementById("launch-note");
	const microsoftLoginButton = document.querySelector(".microsoft-login-button");
	const mojangLoginButton = document.querySelector(".mojang-login-button");
	const accountButton = document.querySelector(".account-button");

	const login = document.querySelector(".login");
	const mojangLogin = document.querySelector(".mojang-login");
	const main = document.querySelector(".main");
	const accounts = document.querySelector(".accounts");
	const backToMain = document.querySelector(".back-to-main-button");

	launcher.accountManager = new AccountManager(Utils.accountsFile, (account) => {
		if(account == launcher.accountManager.activeAccount) {
			updateAccount();
		}

		if(accounts.style.display) {
			updateAccounts();
		}
	});

	function updateAccount() {
		document.querySelector(".account-button").innerHTML = `<img src="${launcher.accountManager.activeAccount.head}"/> <span>${launcher.accountManager.activeAccount.username} <img src="arrow.svg" class="arrow-icon"/></span>`;
	}

	function updateMinecraftFolder() {
		document.querySelector(".minecraft-folder-path").innerText = Config.data.minecraftFolder;
	}

	updateMinecraftFolder();

	backToMain.onclick = () => {
		if(loggingIn) {
			return;
		}

		login.style.display = null;
		main.style.display = "block";
	};

	if(launcher.accountManager.activeAccount != null) {
		main.style.display = "block";
		updateAccount();
	}
	else {
		login.style.display = "block";
		backToMain.style.display = null;
	}

	var launching = false;
	var loggingIn = false;

	document.onmousedown = (event) => {
		if(!main.style.display) {
			return;
		}

		if(!accounts.contains(event.target) && !accountButton.contains(event.target)) {
			accounts.style.display = null;
		}
	};

	function updateAccounts() {
		accounts.innerHTML = "";

		for(let account of launcher.accountManager.accounts) {
			var accountElement = document.createElement("div");
			accountElement.classList.add("account");
			accountElement.innerHTML = `<img src="${account.head}"/> <span>${account.username}</span> <button class="remove-account"><img src="remove.svg"/></button>`;
			accountElement.onclick = (event) => {
				if(event.target.classList.contains("remove-account")
						|| event.target.parentElement.classList.contains("remove-account")) {
					if(!launcher.accountManager.removeAccount(account)) {
						main.style.display = null;
						login.style.display = "block";
						backToMain.style.display = null;
					}
					else {
						updateAccounts();
					}
				}
				else {
					launcher.accountManager.switchAccount(account);
					accounts.style.display = null;
				}
				updateAccount();
			};
			accounts.appendChild(accountElement);
		}

		var addElement = document.createElement("div");
		addElement.classList.add("account");
		addElement.innerHTML = `<img src="add.svg"/> <span>Add Account</span>`;
		addElement.onclick = () => {
			main.style.display = null;
			login.style.display = "block";
			backToMain.style.display = "block";
		};

		accounts.appendChild(addElement);

		accounts.style.display = "block";
	}

	accountButton.onclick = () => {
		if(accounts.style.display) {
			accounts.style.display = null;
			return;
		}

		updateAccounts();
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
		launcher.accountManager.addAccount(account);
		login.style.display = "none";
		main.style.display = "block";
		updateAccount();
		updateAccounts();
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

			if(!fs.existsSync(Utils.assetsDirectory)) {
				launchNote.style.display = "inline";
			}

			playButton.innerText = "...";
			try {
				await launcher.accountManager.refreshAccount(launcher.accountManager.activeAccount);
			}
			catch(error) {
				console.error(error);
				if(launcher.accountManager.activeAccount) {
					updateAccount();
				}

				main.style.display = "none";
				login.style.display = "block";
				backToMain.style.display = launcher.accountManager.accounts.length > 0 ? "block" : "none";
				playButton.innerText = "Play";
				launching = false;
				return;
			}
			launcher.launch(() => {
				playButton.innerText = "Play";
				launching = false;
				launchNote.style.display = null;
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

		if(fs.existsSync(serversFile)) {
			nbt.parse(fs.readFileSync(serversFile), (error, data) => {
				if(error) {
					throw error;
				}

				var servers = data.value.servers.value.value;

				if(servers.length > 0) {
					serversList.innerHTML = "";
				}

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

	var currentTab = "about";

	function switchToTab(tab) {
		document.querySelector("." + currentTab).style.display = "none";

		playButton.style.display = null;

		document.querySelector(".about-tab").classList.remove("selected-tab");
		document.querySelector(".settings-tab").classList.remove("selected-tab");
		document.querySelector("." + tab).style.display = "block";
		document.querySelector("." + tab + "-tab").classList.add("selected-tab");

		currentTab = tab;
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
				launcher.accountManager.addAccount(account);
				mojangLogin.style.display = "none";
				main.style.display = "block";
				emailField.value = "";
				passwordField.value = "";
				errorMessage.innerText = "";
				updateAccount();
				updateAccounts();
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
