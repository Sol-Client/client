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

package io.github.solclient.client.mod.impl.hypixeladditions;

import java.util.regex.*;

import io.github.solclient.client.packet.Popup;

public enum HypixelPopupType {
	// Taken from
	// https://github.com/Sk1erLLC/PopupEvents/blob/master/src/main/resources/remoteresources/chat_regex.json
	FRIEND("Friend request from ((\\[.+] )?(\\S{1,16})).*", "Friend request from %3$s.", "/friend accept %3$s"),
	PARTY("(?:\\[.*] )?(\\S{1,16}) has invited you to join (?:their|(?:\\[.*] ?)?\\w{1,16}'s)? party!",
			"Party invite from %s.", "/party accept %s"),
	DUEL("(\\[.*] )?(\\S{1,16}) has invited you to (\\S+) Duels!", "%3$s duel request from %2$s.", "/duel accept %s"),
	GUILD("Click here to accept or type (\\/guild accept (\\w+))!", "Guild request from %2$s.", "%s"),
	GUILD_PARTY("(\\?[.*] )?(\\S{1,16}) has invited all members of (\\S+) to their party!",
			"Guild party invite from %2$s for %3$s.", "/party accept %2$s"),
	SKYBLOCK_TRADE("(\\S{1,16}) has sent you a trade request\\. Click here to accept!$",
			"Skyblock trade request from %s", "/trade %s");

	private final Pattern pattern;
	private final String message;
	private final String command;

	HypixelPopupType(String regex, String message, String command) {
		pattern = Pattern.compile(regex);
		this.message = message;
		this.command = command;
	}

	public Popup getPopup(String message) {
		Matcher matcher = pattern.matcher(message);

		if (matcher.matches()) {
			Object[] groups = new String[matcher.groupCount()];

			for (int i = 1; i <= matcher.groupCount(); i++) {
				groups[i - 1] = matcher.group(i);
			}

			return new Popup(String.format(this.message, groups), String.format(command, groups), 10000);
		}

		return null;
	}

	public static Popup popupFromMessage(String message) {
		for (HypixelPopupType type : HypixelPopupType.values()) {
			Popup popup = type.getPopup(message);

			if (popup != null) {
				return popup;
			}
		}

		return null;
	}

}
