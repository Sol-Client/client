package io.github.solclient.client.mod.impl.hud.timers;

import java.nio.charset.StandardCharsets;
import java.text.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.util.BukkitMaterial;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

// Based around https://github.com/BadlionClient/BadlionClientTimerAPI.
// Works with any server that supports Badlion timers.
public class TimersMod extends HudMod {

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
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	private boolean shadow = true;
	@Expose
	@Option(applyToAllClass = Option.TEXT_COLOUR_CLASS)
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
			timers.put(0L, new Timer("Oven", new ItemStack(Blocks.furnace)));
			timers.put(1L, new Timer("Toaster", new ItemStack(Items.bread)));
			timers.put(2L, new Timer("Dishwasher", new ItemStack(Blocks.iron_bars)));
		} else {
			timers = this.timers;
		}
		RenderHelper.enableGUIStandardItemLighting();
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
			mc.getRenderItem().renderItemIntoGUI(timer.getRenderItem(), position.getX(), y);
			font.drawString(TIME_FORMAT.format(Math.ceil(timer.getTime() / 20F * 1000)),
					font.drawString(timer.getName(), position.getX() + 22, y + 5, nameColour.getValue(), shadow)
							+ (shadow ? 3 : 4),
					y + 5, timeColour.getValue(), shadow);
			y += 19;
		}
		RenderHelper.disableStandardItemLighting();
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
	public void onCustomPayload(S3FPacketCustomPayload payload) {
		if (!payload.getChannelName().equals(CHANNEL_NAME)) {
			return;
		}
		String message = new String(payload.getBufferData().array(), StandardCharsets.UTF_8);

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
