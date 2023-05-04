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

import java.util.*;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.mod.impl.discord.socket.User;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import lombok.Setter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;

public class DiscordVoiceChatHud implements HudElement {

	private static final int USER_HEIGHT = 20;

	private final DiscordIntegrationMod mod;
	@Setter
	private TextRenderer font;

	public DiscordVoiceChatHud(DiscordIntegrationMod mod) {
		this.mod = mod;
	}

	@Override
	public boolean isVisible() {
		return mod.isEnabled() && mod.voiceChatHud;
	}

	@Override
	public Rectangle getBounds(Position position) {
		int yOffset = 0;

		switch (mod.voiceChatHudAlignment) {
			case MIDDLE:
				yOffset = (USER_HEIGHT * 5) / 2;
				break;
			case BOTTOM:
				yOffset = USER_HEIGHT * 5;
				break;
			default:
				break;
		}

		return position.offset(0, -yOffset).rectangle(20 + font.getStringWidth("TheKodeToad") + 4, USER_HEIGHT * 5 - 4);
	}

	@Override
	public void render(Position position, boolean editMode) {
		Collection<User> users;

		if (editMode) {
			users = new ArrayList<>();

			User thanks = new User("0");
			thanks.setName("Thanks:");
			users.add(thanks);

			User lynith = new User("0");
			lynith.setName("Lynith");
			users.add(lynith);

			User theKodeToad = new User("0");
			theKodeToad.setName("TheKodeToad");
			users.add(theKodeToad);

			User trigg = new User("0");
			trigg.setName("Trigg");
			users.add(trigg);

			User midget = new User("0");
			midget.setName("midget_3111");
			users.add(midget);
		} else if (mod.socket == null) {
			return;
		} else {
			users = mod.socket.getVoiceCallUsers();
		}

		int y = position.getY();

		switch (mod.voiceChatHudAlignment) {
			case MIDDLE:
				y -= (USER_HEIGHT * (users.size())) / 2;
				break;
			case BOTTOM:
				y -= USER_HEIGHT * users.size();
				break;
			default:
				break;
		}

		for (User user : users) {
			user.bindTexture();
			DrawableHelper.drawTexture(position.getX(), y, 0, 0, 16, 16, 16, 16);

			if (user.isSpeaking()) {
				MinecraftUtils.drawOutline(position.getX() - 1, y - 1, position.getX() + 17, y + 17,
						mod.speakingColour.getValue());
			}

			font.draw(user.getName(), position.getX() + 20, y + 4,
					user.isMuted() ? mod.mutedColour.getValue() : mod.usernameColour.getValue(), mod.shadow);
			y += 20;
		}
	}

	@Override
	public Mod getMod() {
		return mod;
	}

	@Override
	public boolean isShownInReplay() {
		return false;
	}

	@Override
	public Position getConfiguredPosition() {
		return mod.voiceChatHudPosition;
	}

	@Override
	public void setPosition(Position position) {
		mod.voiceChatHudPosition = position;
	}

	@Override
	public float getScale() {
		return mod.voiceChatHudScale / 100F;
	}

}
