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

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.ButtonComponent;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Alignment;
import lombok.Getter;
import net.minecraft.client.resource.language.I18n;

public class TextFileOption extends ModOption<String> {

	private final Mod mod;
	@Getter
	private final Path path;
	@Getter
	private final String header;
	@Getter
	private final String editText;

	public TextFileOption(Mod mod, String name, ModOptionStorage<String> storage, Path path, String header,
			String editText) {
		super(name, storage);
		this.mod = mod;
		this.path = path;
		this.header = header;
		this.editText = editText;

		try {
			read();
		} catch (IOException error) {
			LogManager.getLogger().error("Failed initial read for " + storage + " (" + path + ')', error);
		}
	}

	public void read() throws IOException {
		if (!Files.exists(path))
			Files.write(path, Arrays.asList(header), StandardCharsets.UTF_8);

		setValue(new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
		mod.onFileUpdate(getName());
	}

	@Override
	public Component createComponent() {
		Component container = createDefaultComponent();

		ButtonComponent editFile = new ButtonComponent((component, defaultText) -> I18n.translate(editText),
				Theme.button(), Theme.fg()).width(50).height(16);
		container.add(editFile, new AlignedBoundsController(Alignment.END, Alignment.CENTRE));

		editFile.onClick((info, button) -> {
			if (button == 0) {
				MinecraftUtils.playClickSound(true);
				try {
					MinecraftUtils.openUrl(path.toUri().toURL().toString());
				} catch (MalformedURLException error) {
					throw new IllegalStateException(error);
				}
				return true;
			}

			return false;
		});

		return container;
	}

}
