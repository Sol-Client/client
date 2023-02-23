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

package io.github.solclient.client.mod.impl;

import java.nio.file.Path;

import org.apache.logging.log4j.Logger;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import lombok.*;
import net.minecraft.client.MinecraftClient;

/**
 * Base class for built-in mods. Adds some handy stuff.
 */
public abstract class SolClientMod extends Mod {

	protected final Logger logger = getLogger();
	protected final MinecraftClient mc = MinecraftClient.getInstance();
	@Getter
	@Setter
	private int index = -1;

	public String getTranslationKey(String key) {
		return "sol_client.mod." + getId() + '.' + key;
	}

	@Override
	public boolean isEnabledByDefault() {
		return false;
	}

	@Override
	public Path getConfigFolder() {
		return Client.INSTANCE.getConfigFolder();
	}

}
