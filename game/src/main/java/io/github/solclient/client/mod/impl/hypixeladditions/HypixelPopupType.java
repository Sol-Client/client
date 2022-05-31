package io.github.solclient.client.mod.impl.hypixeladditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.solclient.client.api.Popup;

public enum HypixelPopupType {
	// Taken from https://github.com/Sk1erLLC/PopupEvents/blob/master/src/main/resources/remoteresources/chat_regex.json
	FRIEND("Friend request from ((\\[.+] )?(\\S{1,16})).*",
			"Friend request from %3$s.",
			"/friend accept %3$s"),
	PARTY("(?:\\[.*] )?(\\S{1,16}) has invited you to join (?:their|(?:\\[.*] ?)?\\w{1,16}'s)? party!",
			"Party invite from %s.",
			"/party accept %s"),
	DUEL("(\\[.*] )?(\\S{1,16}) has invited you to (\\S+) Duels!",
			"%3$s duel request from %2$s.",
			"/duel accept %s"),
	GUILD("Click here to accept or type (\\/guild accept (\\w+))!",
			"Guild request from %2$s.",
			"%s"),
	GUILD_PARTY("(\\?[.*] )?(\\S{1,16}) has invited all members of (\\S+) to their party!",
			"Guild party invite from %2$s for %3$s.",
			"/party accept %2$s"),
	SKYBLOCK_TRADE("(\\S{1,16}) has sent you a trade request\\. Click here to accept!$", "Skyblock trade request from %s", "/trade %s");

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

		if(matcher.matches()) {
			Object[] groups = new String[matcher.groupCount()];

			for(int i = 1; i <= matcher.groupCount(); i++) {
				groups[i - 1] = matcher.group(i);
			}

			return new Popup(String.format(this.message, groups), String.format(command, groups));
		}

		return null;
	}

	public static Popup popupFromMessage(String message) {
		for(HypixelPopupType type : HypixelPopupType.values()) {
			Popup popup = type.getPopup(message);

			if(popup != null) {
				return popup;
			}
		}

		return null;
	}

}
