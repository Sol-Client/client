const { ipcRenderer } = require("electron");

let domLoaded = false;
let deferredProgress;

ipcRenderer.on("progress", (event, progress) => {
	if(!domLoaded) {
		deferredProgress = progress;
		return;
	}

	updateProgress(progress);
});

function updateProgress(progress) {
	document.querySelector(".progress-bar-progress").style.width = progress;
}

window.addEventListener("DOMContentLoaded", () => {
	domLoaded = true;
	updateProgress(deferredProgress);

	document.querySelector(".skip-update").onclick = () => {
		window.close();
	};

	document.querySelector(".disable-updates").onclick = () => {
		ipcRenderer.send("disableUpdates");
		window.close();
	};
});
