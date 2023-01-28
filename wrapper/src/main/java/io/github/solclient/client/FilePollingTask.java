package io.github.solclient.client;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.*;

public class FilePollingTask implements Runnable, Closeable {

	private Map<String, ModOption<?>> files = new HashMap<>();
	private WatchKey key;

	public FilePollingTask(ModManager mods) throws IOException {
		WatchService service = FileSystems.getDefault().newWatchService();

		key = Client.INSTANCE.getConfigFolder().register(service, StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_CREATE);

		for (Mod mod : mods)
			for (ModOption<?> option : mod.getOptions())
				if (option instanceof FileOption)
					files.put(((FileOption) option).getPath().getFileName().toString(), option);
	}

	@Override
	public void run() {
		for (WatchEvent<?> event : key.pollEvents()) {
			ModOption<?> option = files.get(((Path) event.context()).getFileName().toString());

			if (option != null) {
				try {
					((FileOption) option).readFile();
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
