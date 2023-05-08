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

package io.github.solclient.client.mod.impl.discord;

import java.time.OffsetDateTime;

import org.json.*;

import com.google.gson.JsonArray;
import com.jagrosh.discordipc.entities.RichPresence;

public /* hypocritical final modifier */ final class ExtendedRichPresence extends RichPresence {

	private final String button1Label;
	private final String button1Url;
	private final String button2Label;
	private final String button2Url;

	public ExtendedRichPresence(String state, String details, OffsetDateTime startTimestamp,
			OffsetDateTime endTimestamp, String largeImageKey, String largeImageText, String smallImageKey,
			String smallImageText, String partyId, int partySize, int partyMax, String matchSecret, String joinSecret,
			String spectateSecret, boolean instance, String button1Label, String button1Url, String button2Label,
			String button2Url) {
		super(state, details, startTimestamp, endTimestamp, largeImageKey, largeImageText, smallImageKey,
				smallImageText, partyId, partySize, partyMax, matchSecret, joinSecret, spectateSecret, instance);
		this.button1Label = button1Label;
		this.button1Url = button1Url;
		this.button2Label = button2Label;
		this.button2Url = button2Url;
	}

	@Override
	public JSONObject toJson() {
		JSONObject result = super.toJson();

		boolean button1 = button1Label != null && button1Url != null;
		boolean button2 = button2Label != null && button2Url != null;

		if (result.has("secrets") && result.getJSONObject("secrets").isEmpty())
			result.remove("secrets");
		
		if (button1 || button2) {
			JSONArray buttons = new JSONArray();
			if (button1)
				buttons.put(new JSONObject().put("label", button1Label).put("url", button1Url));
			if (button2)
				buttons.put(new JSONObject().put("label", button2Label).put("url", button2Url));
			result.put("buttons", buttons);
		}

		return result;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends RichPresence.Builder {

		private String state;
		private String details;
		private OffsetDateTime startTimestamp;
		private OffsetDateTime endTimestamp;
		private String largeImageKey;
		private String largeImageText;
		private String smallImageKey;
		private String smallImageText;
		private String partyId;
		private int partySize;
		private int partyMax;
		private String matchSecret;
		private String joinSecret;
		private String spectateSecret;
		private boolean instance;
		private String button1Label;
		private String button1Url;
		private String button2Label;
		private String button2Url;

		private Builder() {
		}

		public RichPresence build() {
			return new ExtendedRichPresence(state, details, startTimestamp, endTimestamp, largeImageKey, largeImageText,
					smallImageKey, smallImageText, partyId, partySize, partyMax, matchSecret, joinSecret,
					spectateSecret, instance, button1Label, button1Url, button2Label, button2Url);
		}

		public Builder setState(String state) {
			this.state = state;
			return this;
		}

		public Builder setDetails(String details) {
			this.details = details;
			return this;
		}

		public Builder setStartTimestamp(OffsetDateTime startTimestamp) {
			this.startTimestamp = startTimestamp;
			return this;
		}

		public Builder setEndTimestamp(OffsetDateTime endTimestamp) {
			this.endTimestamp = endTimestamp;
			return this;
		}

		public Builder setLargeImage(String largeImageKey, String largeImageText) {
			this.largeImageKey = largeImageKey;
			this.largeImageText = largeImageText;
			return this;
		}

		public Builder setLargeImage(String largeImageKey) {
			return setLargeImage(largeImageKey, null);
		}

		public Builder setSmallImage(String smallImageKey, String smallImageText) {
			this.smallImageKey = smallImageKey;
			this.smallImageText = smallImageText;
			return this;
		}

		public Builder setSmallImage(String smallImageKey) {
			return setSmallImage(smallImageKey, null);
		}

		public Builder setParty(String partyId, int partySize, int partyMax) {
			this.partyId = partyId;
			this.partySize = partySize;
			this.partyMax = partyMax;
			return this;
		}

		public Builder setMatchSecret(String matchSecret) {
			this.matchSecret = matchSecret;
			return this;
		}

		public Builder setJoinSecret(String joinSecret) {
			this.joinSecret = joinSecret;
			return this;
		}

		public Builder setSpectateSecret(String spectateSecret) {
			this.spectateSecret = spectateSecret;
			return this;
		}

		public Builder setInstance(boolean instance) {
			this.instance = instance;
			return this;
		}

		public Builder setButton1Label(String button1Label) {
			this.button1Label = button1Label;
			return this;
		}

		public Builder setButton1Url(String button1Url) {
			this.button1Url = button1Url;
			return this;
		}

		public Builder setButton2Label(String button2Label) {
			this.button2Label = button2Label;
			return this;
		}

		public Builder setButton2Url(String button2Url) {
			this.button2Url = button2Url;
			return this;
		}

	}

}
