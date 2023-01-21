package io.github.solclient.client;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import io.github.solclient.client.mod.*;
import net.minecraft.client.MinecraftClient;

public class FilePollingTask implements Runnable, Closeable {

	private Map<String, ModOption> files = new HashMap<>();
	private WatchKey key;

	public FilePollingTask(ModManager mods) throws IOException {
		WatchService service = FileSystems.getDefault().newWatchService();

		key = MinecraftClient.getInstance().runDirectory.toPath().register(service,
				StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);

		for (Mod mod : mods)
			for (ModOption option : mod.getOptions())
				if (option.isFile())
					files.put(option.getFile().getName(), option);
	}

	@Override
	public void run() {
		for (WatchEvent<?> event : key.pollEvents()) {
			File changedFile = ((Path) event.context()).toFile();
			ModOption option = files.get(changedFile.getName());

			if (option != null) {
				try {
					option.readFile();
				} catch (IOException error) {
				}
			}
		}
	}

	@Override
	public void close() {
		key.reset();
	}

}
