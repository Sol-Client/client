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

package io.github.solclient.util;

import java.io.*;
import java.net.*;
import java.nio.file.*;

import org.apache.commons.io.*;

import lombok.experimental.UtilityClass;

/**
 * General utils.
 */
@UtilityClass
public class Utils {

	public URLConnection getConnection(String agent, URL url) throws IOException {
		URLConnection connection = url.openConnection();
		connection.addRequestProperty("User-Agent", agent); // Force consistent behaviour
		return connection;
	}

	public String urlToString(URL url) throws IOException {
		return urlToString(GlobalConstants.USER_AGENT, url);
	}

	public String urlToString(String agent, URL url) throws IOException {
		try (InputStream in = getConnection(agent, url).getInputStream()) {
			return IOUtils.toString(in);
		}
	}

	public void urlToFile(URL url, Path file) throws IOException {
		urlToFile(GlobalConstants.USER_AGENT, url, file);
	}

	public void urlToFile(String agent, URL url, Path file) throws IOException {
		try (InputStream in = getConnection(agent, url).getInputStream()) {
			FileUtils.copyInputStreamToFile(in, file.toFile());
		}
	}

	public URL sneakyParse(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException error) {
			throw new AssertionError(error);
		}
	}

	public void ensureDirectory(Path path) throws IOException {
		if (Files.isDirectory(path))
			return;

		// if the file is just an empty one created by mistake - or a broken link -
		// delete it
		if (Files.isRegularFile(path) && Files.size(path) == 0)
			Files.delete(path);

		if (!Files.isDirectory(path.getParent()))
			ensureDirectory(path.getParent());

		Files.createDirectory(path);
	}

}
