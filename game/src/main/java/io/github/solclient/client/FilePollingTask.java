package io.github.solclient.client;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModOption;
import io.github.solclient.client.platform.mc.MinecraftClient;

public class FilePollingTask implements Runnable, Closeable {

	private Map<String, ModOption> files = new HashMap<>();
	private WatchKey key;

	public FilePollingTask(List<Mod> mods) throws IOException {
		WatchService service = FileSystems.getDefault().newWatchService();

		key = MinecraftClient.getInstance().getDataFolder().toPath().register(service, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);

		for(Mod mod : mods) {
			for(ModOption option : mod.getOptions()) {
				if(option.isFile()) {
					files.put(option.getFile().getName(), option);
				}
			}
		}
	}

	@Override
	public void run() {
		for(WatchEvent<?> event : key.pollEvents()) {
			File changedFile = ((Path) event.context()).toFile();

			ModOption option = files.get(changedFile.getName());

			if(option != null) {
				try {
					option.readFile();
				}
				catch(IOException error) {
				}
			}
		}
	}

	@Override
	public void close() {
		key.reset();
	}

}
