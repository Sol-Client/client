const Launcher = require("./launcher");
const Utils = require("./utils");
const Config = require("./config");
const launcher = Launcher.instance;
const { ipcRenderer, shell } = require("electron");
const { MicrosoftAuthService, Account, AccountManager } = require("./auth");
const microsoftAuthService = MicrosoftAuthService.instance;
const fs = require("fs");
const msmc = require("msmc");
const os = require("os");
const nbt = require("nbt");
const path = require("path");
const xss = require("xss");

Utils.init();
Config.init(Utils.dataDirectory);
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

window.addEventListener("DOMContentLoaded", async() => {
	if(Utils.getOsName() == "osx") {
		document.querySelector(".drag-region").style.display = "block";
	}

	const playButton = document.getElementById("launch-button");
	const launchNote = document.getElementById("launch-note");
	const microsoftLoginButton = document.querySelector(".microsoft-login-button");
	const accountButton = document.querySelector(".account-button");

	const login = document.querySelector(".login");
	const main = document.querySelector(".main");
	const accounts = document.querySelector(".accounts");
	const backToMain = document.querySelector(".back-to-main-button");
	const news = document.querySelector(".news");

	const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

	fetch("https://sol-client.github.io/news.html", {
				headers: {
					"Cache-Control": "no-cache"
				}
			})
			.then(async(response, error) => {
				let today = new Date();

				news.innerHTML = "<br/>" + xss(await response.text(), { whiteList: { ...xss.whiteList, time: ["datetime"] } });
				for(let timeElement of news.getElementsByTagName("time")) {
					let datetime = timeElement.getAttribute("datetime");

					if(datetime == "future") {
						timeElement.innerText = "The Future";
						continue;
					}

					let todayYear = today.getFullYear();
					let todayMonth = today.getMonth() + 1;
					let todayDay = today.getDate();

					let year = parseInt(datetime.substring(0, 4));
					let month = parseInt(datetime.substring(5, 7));
					let day = parseInt(datetime.substring(8, 10));

					if(todayYear == year && todayMonth == month) {
						if(todayDay == day) {
							timeElement.innerText = "Today";
							continue;
						}
						else if(todayDay - 1 == day) {
							timeElement.innerText = "Yesterday";
							continue;
						}
					}


					let friendlyName = day + " " + monthNames[month - 1];

					if(today.getFullYear() != year) {
						friendlyName += " " + year;
					}

					timeElement.innerText = friendlyName;
				}
			})
			.catch((error) => {
				console.error(error);
				news.innerHTML = `<p>${error}</p>`;
			});

	launcher.accountManager = new AccountManager(Utils.accountsFile, (account) => {
		if(account == launcher.accountManager.activeAccount) {
			updateAccount();
		}

		if(accounts.style.display) {
			updateAccounts();
		}
	});

	for(let account of launcher.accountManager.accounts) {
		await launcher.accountManager.storeInKeychain(account);
	}

	function updateAccount() {
		if(launcher.accountManager.activeAccount) {
			document.querySelector(".account-button").innerHTML = `<img src="${launcher.accountManager.activeAccount.head}"/> <span>${launcher.accountManager.activeAccount.username} <img src="arrow.svg" class="arrow-icon"/></span>`;
		}
	}

	function updateMinecraftFolder() {
		document.querySelector(".minecraft-folder-path").innerText =
				Config.data.minecraftFolder ?? "(use default)";
		// wow. I didn't know you could do that in js.
	}

	function updateJre() {
		document.querySelector(".jre-location").innerText =
				Config.data.jrePath ?? "(download automatically)"
	}

	updateMinecraftFolder();
	updateJre();

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

	let launching = false;
	let loggingIn = false;

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
			let accountElement = document.createElement("div");
			accountElement.classList.add("account");
			accountElement.innerHTML = `<img src="${account.head}"/> <span>${account.username}</span> <button class="remove-account"><img src="remove.svg"/></button>`;
			accountElement.onclick = async(event) => {
				if(event.target.classList.contains("remove-account")
						|| event.target.parentElement.classList.contains("remove-account")) {
					if(!(await launcher.accountManager.removeAccount(account))) {
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

		let addElement = document.createElement("div");
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

	ipcRenderer.on("msa", async(event, result) => {
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
		let account = await microsoftAuthService.authenticate(result.profile);
		launcher.accountManager.addAccount(account);
		login.style.display = "none";
		main.style.display = "block";
		updateAccount();
		updateAccounts();
	})

	async function play(server) {
		if(!launching) {
			launching = true;

			launchNote.style.display = "inline";
			playButton.innerText = "...";
			launchNote.innerText = "Refreshing login...";
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
				launchNote.style.display = null;
				return;
			}
			launcher.launch(() => {
				playButton.innerText = "Play";
				launching = false;
				launchNote.style.display = null;
			}, (text) => launchNote.innerText = text, server);
		}
	}

	playButton.onclick = () => play();

	document.querySelector(".about-tab").onclick = () => switchToTab("about");
	document.querySelector(".settings-tab").onclick = () => switchToTab("settings");
	document.querySelector(".news-tab").onclick = () => switchToTab("news");
	document.querySelector(".minecraft-folder").onclick = () => ipcRenderer.send("directory", "Select Minecraft Folder", "minecraft");
	document.querySelector(".jre-location-change").onclick = () => ipcRenderer.send("directory", "Select JRE Folder", "jre");
	document.querySelector(".jre-location-reset").onclick = () => {
		Config.data.jrePath = null;
		Config.save();
		updateJre();
	};

	ipcRenderer.on("directory", (event, file, id) => {
		switch(id) {
			case "minecraft":
				Config.data.minecraftFolder = file;
				Config.save();
				updateMinecraftFolder();
				updateServers();
				break;
			case "jre":
				if(!fs.existsSync(path.join(file, "bin"))) {
					ipcRenderer.send("jreError");
					return;
				}
				Config.data.jrePath = file;
				Config.save();
				updateJre();
				break;
		}
	});

	document.querySelector(".devtools").onclick = () => ipcRenderer.send("devtools");

	function updateServers() {
		let serversList = document.querySelector(".quick-servers");
		let serversFile = Config.getGameDirectory(Utils.gameDirectory) + "/servers.dat";
		let serverText = document.querySelector(".quick-join-text");

		if(fs.existsSync(serversFile)) {
			nbt.parse(fs.readFileSync(serversFile), (error, data) => {
				if(error) {
					throw error;
				}

				let servers = data.value.servers.value.value;

				if(servers.length > 0) {
					serversList.innerHTML = "";
				}

				for(let i = 0; i < servers.length && i < 5; i++) {
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

	let memory = document.querySelector(".memory");
	let memoryLabel = document.querySelector(".memory-label");

	memory.max = os.totalmem() / 1024 / 1024;
	memory.value = Config.data.maxMemory;

	let optifine = document.querySelector(".optifine");
	optifine.checked = Config.data.optifine;
	optifine.onchange = () => {
		Config.data.optifine = optifine.checked;
		Config.save();
	};

	let autoUpdate = document.querySelector(".auto-update");
	autoUpdate.checked = Config.data.autoUpdate;
	autoUpdate.onchange = () => {
		Config.data.autoUpdate = autoUpdate.checked;
		Config.save();
	};

	let jvmArguments = document.querySelector(".jvm-arguments");
	jvmArguments.value = Config.data.jvmArgs;
	jvmArguments.onchange = () => {
		Config.data.jvmArgs = jvmArguments.value;
		Config.save();
	};

	function updateMemoryLabel() {
		memoryLabel.innerText = (memory.value / 1024).toFixed(1) + " GB";
		Config.data.maxMemory = memory.value;
	};

	memory.oninput = updateMemoryLabel;
	memory.onchange = Config.save;

	updateMemoryLabel();

	let currentTab = "about";

	function switchToTab(tab) {
		document.querySelector("." + currentTab).style.display = "none";

		playButton.style.display = null;

		document.querySelector(".about-tab").classList.remove("selected-tab");
		document.querySelector(".settings-tab").classList.remove("selected-tab");
		document.querySelector(".news-tab").classList.remove("selected-tab");
		document.querySelector("." + tab).style.display = "block";
		document.querySelector("." + tab + "-tab").classList.add("selected-tab");

		currentTab = tab;
	}

	const emailField = document.getElementById("username");
	const passwordField = document.getElementById("password");
	const errorMessage = document.querySelector(".error-message");

	for(let element of document.querySelectorAll(".open-in-browser")) {
		const href = element.href;
		element.href = "javascript:void(0);";
		element.onclick = function(event) {
			shell.openExternal(href);
		};
	}
});
