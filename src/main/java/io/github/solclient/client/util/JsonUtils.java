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

package io.github.solclient.client.util;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.experimental.UtilityClass;

// used because of old gson versions lacking features...
@UtilityClass
public class JsonUtils {

	// credit to Valoeghese for naming xD :P
	public JsonObject clone(JsonObject sheep) {
		JsonObject dolly = new JsonObject();
		for (Map.Entry<String, JsonElement> entry : sheep.entrySet())
			dolly.add(entry.getKey(), clone(entry.getValue()));
		return dolly;
	}

	public JsonArray clone(JsonArray sheep) {
		JsonArray dolly = new JsonArray();
		for (JsonElement item : sheep)
			dolly.add(clone(item));
		return dolly;
	}

	public JsonElement clone(JsonElement sheep) {
		if (sheep.isJsonNull() || sheep.isJsonPrimitive())
			return sheep;
		else if (sheep.isJsonObject())
			return clone((JsonObject) sheep);
		else if (sheep.isJsonArray())
			return clone((JsonArray) sheep);

		throw new IllegalArgumentException("i give up");
	}

}
