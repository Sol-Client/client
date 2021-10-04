const {Launcher, Utils} = require("./launcher.js");
const launcher = Launcher.instance;
const {ipcRenderer, shell} = require("electron");
const {MicrosoftAuthService, YggdrasilAuthService, Account} = require("./auth");
const microsoftAuthService = MicrosoftAuthService.instance;
const yggdrasilAuthService = YggdrasilAuthService.instance;
const fs = require("fs");
const msmc = require("msmc");

window.addEventListener("DOMContentLoaded", () => {
	const playButton = document.getElementById("launch-button");
	const microsoftLoginButton = document.querySelector(".microsoft-login-button");
	const mojangLoginButton = document.querySelector(".mojang-login-button");
	const accountButton = document.querySelector(".account-button");

	const login = document.querySelector(".login");
	const mojangLogin = document.querySelector(".mojang-login");
	const main = document.querySelector(".main");

	if(fs.existsSync(Utils.accountFile)) {
		var account = Account.from(JSON.parse(fs.readFileSync(Utils.accountFile)));
		launcher.account = account;
		main.style.display = "block";
		document.querySelector(".account-button").innerText = "🗘 " + account.username;
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
		fs.writeFileSync(Utils.accountFile, JSON.stringify(account));
		document.querySelector(".account-button").innerText = "🗘 " + account.username;
	})

	mojangLoginButton.onclick = () => {
		if(!loggingIn) {
			login.style.display = "none";
			mojangLogin.style.display = "block";
		}
	};

	playButton.onclick = async() => {
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
				fs.writeFileSync(Utils.accountFile, JSON.stringify(launcher.account));
			}
			launcher.launch(() => {
				playButton.innerText = "Play";
				launching = false;
			});
		}
	};

	document.querySelector(".back-to-login-button").onclick = () => {
		mojangLogin.style.display = "none";
		login.style.display = "block";
	};

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
				fs.writeFileSync(Utils.accountFile, JSON.stringify(account));
				document.querySelector(".account-button").innerText = "🗘 " + account.username;
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
