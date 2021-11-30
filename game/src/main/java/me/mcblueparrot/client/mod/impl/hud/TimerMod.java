package me.mcblueparrot.client.mod.impl.hud;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PostTickEvent;
import me.mcblueparrot.client.event.impl.WorldLoadEvent;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.hud.HudMod;
import me.mcblueparrot.client.util.BukkitMaterial;
import me.mcblueparrot.client.util.data.Alignment;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

// Based around https://github.com/BadlionClient/BadlionClientTimerAPI.
// Works with any server that supports Badlion timers.
public class TimerMod extends HudMod {

	private static final String CHANNEL_NAME = "badlion:timers";
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss");
	private static final int TIMER_HEIGHT = 19;

	private Map<Long, Timer> timers = new HashMap<>();

	@Expose
	@ConfigOption("Alignment")
	private Alignment alignment = Alignment.MIDDLE;
	@Expose
	@ConfigOption("Icon")
	private boolean icon = true;
	@Expose
	@ConfigOption("Text Shadow")
	private boolean shadow = true;
	@Expose
	@ConfigOption("Name Colour")
	private Colour nameColour = Colour.WHITE;
	@Expose
	@ConfigOption("Time Colour")
	private Colour timeColour = new Colour(8355711);

	public TimerMod() {
		super("Timers", "timers", "Timers for game events.");
	}

	@Override
	public Rectangle getBounds(Position position) {
		int y = position.getY();
		switch(alignment) {
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
		if(editMode) {
			timers = new HashMap<>();
			Timer oven = new Timer();
			oven.renderItem = new ItemStack(Blocks.furnace);
			oven.name = "Oven";
			Timer toaster = new Timer();
			toaster.renderItem = new ItemStack(Items.bread);
			toaster.name = "Toaster";
			Timer dishwasher = new Timer();
			dishwasher.renderItem = new ItemStack(Blocks.iron_bars);
			dishwasher.name = "Dishwasher";

			timers.put(0L, oven);
			timers.put(1L, toaster);
			timers.put(2L, dishwasher);
		}
		else {
			timers = this.timers;
		}
		RenderHelper.enableGUIStandardItemLighting();
		int y = position.getY();


		switch(alignment) {
			case TOP:
				break;
			case MIDDLE:
				y -= (TIMER_HEIGHT * (timers.size())) / 2;
				break;
			case BOTTOM:
				y -= TIMER_HEIGHT * timers.size();
		}

		for(Timer timer : timers.values()) {
			mc.getRenderItem().renderItemIntoGUI(timer.renderItem, position.getX(), y);
			font.drawString(TIME_FORMAT.format(Math.ceil(timer.time / 20F * 1000)),
					font.drawString(timer.name, position.getX() + 22, y + 5, nameColour.getValue(), shadow) + (shadow ? 3 :
							4), y + 5,
					timeColour.getValue(), shadow);
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
		if(!payload.getChannelName().equals(CHANNEL_NAME)) {
			return;
		}
		String message = new String(payload.getBufferData().array(), StandardCharsets.UTF_8);

		String type = message.substring(0, message.indexOf('|'));
		JsonObject data = new JsonParser().parse(message.substring(message.indexOf('|') + 1)).getAsJsonObject();

		long id = -1;
		if(data.has("id")) {
			id = data.get("id").getAsLong();
		}

		if(type.equals("REMOVE_ALL_TIMERS")) {
			timers.clear();
		}
		else if(type.equals("REMOVE_TIMER")) {
			timers.remove(id);
		}
		else if(type.equals("SYNC_TIMERS")) {
			timers.get(id).time = data.get("time").getAsLong();
		}
		else if(type.equals("ADD_TIMER")) {
			JsonObject item = data.get("item").getAsJsonObject();
			Timer tickTock = new Timer();
			timers.put(id, tickTock);
			tickTock.id = id;
			tickTock.name = data.get("name").getAsString();
			tickTock.renderItem = new ItemStack(BukkitMaterial.valueOf(item.get("type").getAsString()).getItem(),
					item.has("amount") ? item.get("amount").getAsInt() : 1);
			tickTock.time = data.get("time").getAsLong();
		}
	}

	public class Timer {

		public long id;
		public String name;
		public ItemStack renderItem;
		public long time;

		public void tick() {
			if(time > 0) {
				time--;
			}
		}

	}

}
