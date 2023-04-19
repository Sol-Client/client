/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.impl.core.CoreMod;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.mod.option.impl.TextFileOption;

public class FilePollingTask implements Runnable, Closeable {

	private Map<String, ModOption<?>> files = new HashMap<>();
	private WatchKey key;

	public FilePollingTask(SolClient mods) throws IOException {
		WatchService service = FileSystems.getDefault().newWatchService();

		key = SolClient.INSTANCE.getConfigFolder().register(service, StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_CREATE);

		for (Mod mod : mods)
			for (ModOption<?> option : mod.getOptions())
				if (option instanceof TextFileOption)
					files.put(((TextFileOption) option).getPath().getFileName().toString(), option);
	}

	@Override
	public void run() {
		for (WatchEvent<?> event : key.pollEvents()) {
			ModOption<?> option = files.get(((Path) event.context()).getFileName().toString());

			if (option != null) {
				try {
					((TextFileOption) option).read();
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
