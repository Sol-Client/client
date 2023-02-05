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

package io.github.solclient.client.mod.option.impl;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.option.FileOption;
import io.github.solclient.client.mod.option.annotation.TextFile;
import lombok.Getter;

public final class FileFieldOption extends FieldOption<String> implements FileOption {

	private final TextFile annotation;
	@Getter
	private final Path path;

	public FileFieldOption(Mod owner, Field field) throws IllegalAccessException {
		super(owner, field);
		annotation = field.getAnnotation(TextFile.class);
		if (annotation == null)
			throw new IllegalArgumentException(field + " is not annotated with @TextFile");

		if (annotation.value().indexOf(File.separatorChar) != -1 || annotation.value().indexOf('/') != -1
				|| /* avoid "it works on my machine" */ annotation.value().indexOf('\\') != -1)
			throw new IllegalArgumentException("@TextField value must not contain slashes");

		path = owner.getConfigFolder().resolve(annotation.value());
		try {
			readFile();
		} catch (IOException error) {
			LogManager.getLogger().error("Failed initial read for " + field + " (" + path + ')', error);
		}
	}

	@Override
	public String getEditText() {
		return annotation.text();
	}

	@Override
	public void readFile() throws IOException {
		if (!Files.exists(path))
			FileUtils.writeStringToFile(path.toFile(), annotation.header(), StandardCharsets.UTF_8);

		setValue(FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8));
		owner.onFileUpdate(name);
	}

}
