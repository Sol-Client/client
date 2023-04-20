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

package io.github.solclient.client.mod.impl.packetapi.action;

import com.google.gson.*;

import io.github.solclient.client.mod.impl.packetapi.*;

public interface ApiAction {

	public static ApiAction createAction(String id, JsonObject inputs) {
		ApiAction action = createAction(id);
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.registerTypeAdapter(action.getClass(), (InstanceCreator<?>) (ignored) -> action).create();
		return gson.fromJson(inputs, action.getClass());
	}

	public static ApiAction createAction(String id) {
		switch (id) {
			case "block_mods":
				return new BlockModsAction();
			case "show_popup":
				return new ShowPopupAction();
			case "hide_popup":
				return new HidePopupAction();
			case "enable_dev_mode":
				return new EnableDevModeAction();
		}

		throw new ApiUsageError("Invalid action: " + id);
	}

	void exec(PacketApiMod api);

}
