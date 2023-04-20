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

import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

import io.github.solclient.client.mod.Mod;
import lombok.Setter;
import net.minecraft.client.network.ServerInfo;

public enum DetectedServer {
	HYPIXEL("([A-z]+\\.)?hypixel\\.net(:[0-9]+)?",
			"https://support.hypixel.net/hc/en-us/articles/6472550754962-Allowed-Modifications/", "freelook"),
	GOMMEHD("gommehd\\.net(:[0-9]+)?", "https://www.gommehd.net/forum/threads/rules-MinecraftClient.941059/",
			"freelook", "fullbright"),
	MINEMEN("([A-z]+\\.)?minemen\\.club(:[0-9]+)?",
			"https://docs.google.com/document/d/1g_NRnhHER2Rruwk6ysbNtaURrHGns_7_0U7OpPwrhqk/edit"),
	MINEPLEX("([A-z]+\\.)?mineplex\\.com(:[0-9]+)?", "https://www.mineplex.com/rules/", "freelook");

	@Setter
	private static DetectedServer current;

	private final Pattern pattern;
	private final URI blockedModPage;
	private final List<String> blockedMods;

	private DetectedServer(String regex, String blockModPage, String... blockedMods) {
		pattern = Pattern.compile(regex);
		try {
			this.blockedModPage = new URI(blockModPage);
		} catch (URISyntaxException error) {
			throw new IllegalStateException(error);
		}
		this.blockedMods = Arrays.asList(blockedMods);
	}

	public static DetectedServer current() {
		return current;
	}

	public URI getBlockedModPage() {
		return blockedModPage;
	}

	public boolean shouldBlockMod(Mod mod) {
		return blockedMods.contains(mod.getId());
	}

	public boolean matches(ServerInfo info) {
		return pattern.matcher(info.address).matches();
	}

}
