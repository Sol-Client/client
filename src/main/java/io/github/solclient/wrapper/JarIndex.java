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

package io.github.solclient.wrapper;

import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JarIndex {

	private static final List<String> CLASSES = new ArrayList<>();

	static {
		try {
			populate(item -> {
				if (item.endsWith(".class"))
					CLASSES.add(item);
			});
		} catch (IOException | URISyntaxException error) {
			throw new UnsupportedOperationException("Cannot initialise jar index", error);
		}
	}

	private void populate(Consumer<String> consumer) throws IOException, URISyntaxException {
		String reference = JarIndex.class.getName();
		URL resource = ClassWrapper.instance.getResource(reference.replace('.', '/') + ".class");
		if (resource.getProtocol().equals("file")) {
			Path path = Paths.get(resource.toURI());
			Path root = path.getRoot().resolve(path.subpath(0, (int) (path.getNameCount()
					- reference.toString().chars().filter(character -> character == '.').count() - 1)));

			Files.walk(root).forEach(entry -> {
				if (Files.isDirectory(entry))
					return;

				consumer.accept(root.relativize(entry).toString());
			});
		} else {
			URLConnection connection = resource.openConnection();
			if (connection instanceof JarURLConnection) {
				JarFile file = ((JarURLConnection) connection).getJarFile();
				file.stream().forEach(entry -> {
					if (!entry.isDirectory())
						consumer.accept(entry.getName());
				});
			}
		}
	}

	public List<String> getPackageChildren(String packageName) throws IOException {
		String target = packageName.replace(".", "/") + '/';
		return CLASSES.stream().filter(item -> item.startsWith(target))
				.map(item -> item.replace("/", ".").substring(0, item.lastIndexOf('.'))).collect(Collectors.toList());
	}

}
