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

package io.github.solclient.client.mod.impl.hud.timers;

import java.nio.charset.StandardCharsets;
import java.text.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.BukkitMaterial;
import io.github.solclient.client.util.data.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

// Based around https://github.com/BadlionClient/BadlionClientTimerAPI.
// Works with any server that supports Badlion timers.
public class TimersMod extends SolClientHudMod {

	private static final String CHANNEL_NAME = "badlion:timers";
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss");
	private static final int TIMER_HEIGHT = 19;

	private final Map<Long, Timer> timers = new HashMap<>();

	@Expose
	@Option
	private VerticalAlignment alignment = VerticalAlignment.MIDDLE;
	@Expose
	@Option
	private boolean icon = true;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean shadow = true;
	@Expose
	@ColourKey(ColourKey.TEXT_COLOUR)
	private Colour nameColour = Colour.WHITE;
	@Expose
	@Option
	private Colour timeColour = new Colour(8355711);

	@Override
	public String getId() {
		return "timers";
	}

	@Override
	public Rectangle getBounds(Position position) {
		int y = position.getY();
		switch (alignment) {
			case TOP:
				break;
			case MIDDLE:
				y -= ((TIMER_HEIGHT * 3) / 2) * getScale();
				break;
			case BOTTOM:
				y -= (TIMER_HEIGHT * 3) * getScale();
		}
		return new Rectangle(position.getX(), y,
				22 + font.getStringWidth("Dishwasher") + 4 + font.getStringWidth("00:00"), 19 * 3);
	}

	@Override
	public void render(Position position, boolean editMode) {
		super.render(position, editMode);
		Map<Long, Timer> timers;
		if (editMode) {
			timers = new HashMap<>();
			timers.put(0L, new Timer("Oven", new ItemStack(Blocks.FURNACE)));
			timers.put(1L, new Timer("Toaster", new ItemStack(Items.BREAD)));
			timers.put(2L, new Timer("Dishwasher", new ItemStack(Blocks.IRON_BARS)));
		} else {
			timers = this.timers;
		}
		DiffuseLighting.enable();
		int y = position.getY();

		switch (alignment) {
			case TOP:
				break;
			case MIDDLE:
				y -= (TIMER_HEIGHT * (timers.size())) / 2;
				break;
			case BOTTOM:
				y -= TIMER_HEIGHT * timers.size();
		}

		for (Timer timer : timers.values()) {
			mc.getItemRenderer().renderInGuiWithOverrides(timer.getRenderItem(), position.getX(), y);
			font.draw(TIME_FORMAT.format(Math.ceil(timer.getTime() / 20F * 1000)),
					font.draw(timer.getName(), position.getX() + 22, y + 5, nameColour.getValue(), shadow)
							+ (shadow ? 3 : 4),
					y + 5, timeColour.getValue(), shadow);
			y += 19;
		}
		DiffuseLighting.disable();
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		timers.clear();
	}

	@EventHandler
	public void onTick(PostTickEvent event) {
		timers.values().forEach(Timer::tick);
	}

	@EventHandler
	public void onCustomPayload(CustomPayloadS2CPacket payload) {
		if (!payload.getChannel().equals(CHANNEL_NAME)) {
			return;
		}
		String message = new String(payload.getPayload().array(), StandardCharsets.UTF_8);

		String type = message.substring(0, message.indexOf('|'));
		JsonObject data = JsonParser.parseString(message.substring(message.indexOf('|') + 1)).getAsJsonObject();

		long id = -1;
		if (data.has("id")) {
			id = data.get("id").getAsLong();
		}

		if (type.equals("REMOVE_ALL_TIMERS")) {
			timers.clear();
		} else if (type.equals("REMOVE_TIMER")) {
			timers.remove(id);
		} else if (type.equals("SYNC_TIMERS")) {
			timers.get(id).setTime(data.get("time").getAsLong());
		} else if (type.equals("ADD_TIMER")) {
			JsonObject item = data.get("item").getAsJsonObject();
			Timer tickTock = new Timer(data.get("name").getAsString(),
					new ItemStack(BukkitMaterial.valueOf(item.get("type").getAsString()).getItem(),
							item.has("amount") ? item.get("amount").getAsInt() : 1));
			timers.put(id, tickTock);
			tickTock.setId(id);
			tickTock.setTime(id);
		}
	}

}
