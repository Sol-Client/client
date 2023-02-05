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

package io.github.solclient.client.packet.action;

import java.util.*;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.packet.*;

public final class BlockModsAction implements ApiAction {

	@Expose
	private Map<String, Boolean> mods;

	@Override
	public void exec(PacketApi api) {
		if (mods == null)
			throw new ApiUsageError("No mods provided to block");

		mods.forEach((key, value) -> {
			Optional<Mod> modOpt = Client.INSTANCE.getMods().getById(key);
			if (!modOpt.isPresent()) {
				if (api.isDevMode())
					PacketApi.LOGGER.warn("Server tried to block mod with id " + key);

				return;
			}

			Mod mod = modOpt.get();

			if (value)
				mod.block();
			else
				mod.unblock();
		});
	}

}
