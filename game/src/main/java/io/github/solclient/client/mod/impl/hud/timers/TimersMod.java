package io.github.solclient.client.mod.impl.hud.timers;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;

import io.github.solclient.abstraction.mc.world.item.ItemStack;
import io.github.solclient.abstraction.mc.world.item.ItemType;
import io.github.solclient.abstraction.mc.world.level.block.BlockType;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PostTickEvent;
import io.github.solclient.client.event.impl.network.ServerMessageReceiveEvent;
import io.github.solclient.client.event.impl.world.level.LevelLoadEvent;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.HudMod;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.util.BukkitMaterial;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;
import io.github.solclient.client.util.data.VerticalAlignment;

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
				22 + font.getWidth("Dishwasher") + 4 + font.getWidth("00:00"), 19 * 3);
	}

	@Override
	public void render(Position position, boolean editMode) {
		super.render(position, editMode);
		Map<Long, Timer> timers;
		if(editMode) {
			timers = new HashMap<>();
			timers.put(0L, new Timer("Diamond II", ItemStack.create(ItemType.DIAMOND)));
			timers.put(1L, new Timer("Emerald II", ItemStack.create(ItemType.EMERALD)));
		}
		else {
			timers = this.timers;
		}
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
			mc.getItemRenderer().render(timer.getRenderItem(), position.getX(), y);
			font.render(TIME_FORMAT.format(Math.ceil(timer.getTime() / 20F * 1000)),
					font.render(timer.getName(), position.getX() + 22, y + 5, nameColour.getValue(), shadow)
							+ (shadow ? 3 : 4),
					y + 5, timeColour.getValue(), shadow);
			y += 19;
		}
	}


	@EventHandler
	public void onWorldLoad(LevelLoadEvent event) {
		timers.clear();
	}

	@EventHandler
	public void onTick(PostTickEvent event) {
		timers.values().forEach(Timer::tick);
	}

	@EventHandler
	public void onServerMessage(ServerMessageReceiveEvent event) {
		if(!CHANNEL_NAME.equals(event.getChannelId())) {
			return;
		}
		String message = event.getData();

		String type = message.substring(0, message.indexOf('|'));
		JsonObject data = JsonParser.parseString(message.substring(message.indexOf('|') + 1)).getAsJsonObject();

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
			timers.get(id).setTime(data.get("time").getAsLong());
		}
		else if(type.equals("ADD_TIMER")) {
			JsonObject item = data.get("item").getAsJsonObject();
			Timer tickTock = new Timer(data.get("name").getAsString(),
					ItemStack.create(BukkitMaterial.valueOf(item.get("type").getAsString()).getItem(),
							item.has("amount") ? item.get("amount").getAsInt() : 1));
			timers.put(id, tickTock);
			tickTock.setId(id);
			tickTock.setTime(id);
		}
	}

}
