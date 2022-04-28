package me.mcblueparrot.client.mod.impl.discordrpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.hud.BaseHudElement;
import me.mcblueparrot.client.mod.hud.HudPosition;
import me.mcblueparrot.client.mod.impl.discordrpc.socket.User;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class DiscordVoiceChatHud extends BaseHudElement {

	private static final int USER_HEIGHT = 20;

	protected FontRenderer font;
	private DiscordIntegrationMod mod;

	public DiscordVoiceChatHud(DiscordIntegrationMod mod) {
		this.mod = mod;
		font = Minecraft.getMinecraft().fontRendererObj;
	}

	@Override
	public boolean isVisible() {
		return mod.isEnabled() && mod.voiceChatHud;
	}

	@Override
	public Rectangle getBounds(Position position) {
		int yOffset = 0;

		switch(mod.voiceChatHudAlignment) {
			case MIDDLE:
				yOffset = (USER_HEIGHT * 4) / 2;
				break;
			case BOTTOM:
				yOffset = USER_HEIGHT * 4;
				break;
			default:
				break;
		}

		return position.offset(0, yOffset).rectangle(20 + font.getStringWidth("TheKodeToad") + 4, 76);
	}

	@Override
	public void render(Position position, boolean editMode) {
		List<User> users;

		if(editMode) {
			users = new ArrayList<>();

			User theKodeToad = new User("0");
			theKodeToad.setName("TheKodeToad");
			users.add(theKodeToad);

			User lynith = new User("0");
			lynith.setName("Lynith");
			users.add(lynith);

			User trigg = new User("0");
			trigg.setName("Trigg");
			users.add(trigg);

			User midget = new User("0");
			midget.setName("midget_3111");
			users.add(midget);
		}
		else if(mod.socket == null) {
			return;
		}
		else {
			users = new ArrayList<>(mod.socket.getVoiceCallUsers());
		}

		Collections.sort(users, (u1, u2) -> u1.getUsername().compareTo(u2.getUsername()));

		int y = position.getY();

		switch(mod.voiceChatHudAlignment) {
			case MIDDLE:
				y -= (USER_HEIGHT * (users.size())) / 2;
				break;
			case BOTTOM:
				y -= USER_HEIGHT * users.size();
				break;
			default:
				break;
		}

		for(User user : users) {
			user.bindTexture();
			Gui.drawModalRectWithCustomSizedTexture(position.getX(), y, 0, 0, 16, 16, 16, 16);

			if(user.isSpeaking()) {
				Utils.drawOutline(position.getX() - 1, y - 1, position.getX() + 17, y + 17, mod.speakingColour.getValue());
			}

			font.drawString(user.getName(), position.getX() + 20, y + 4, user.isMuted() ? mod.mutedColour.getValue() : mod.usernameColour.getValue(), mod.shadow);
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
	public HudPosition getHudPosition() {
		return mod.voiceChatHudPosition;
	}

	@Override
	public void setHudPosition(HudPosition position) {
		mod.voiceChatHudPosition = position;
	}

	@Override
	public float getHudScale() {
		return mod.voiceChatHudScale / 100F;
	}

}
