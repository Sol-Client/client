package io.github.solclient.client.mod.impl.hud.timers;

import java.text.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PostTickEvent;
import io.github.solclient.client.event.impl.network.ServerMessageReceiveEvent;
import io.github.solclient.client.event.impl.world.level.LevelLoadEvent;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.platform.mc.world.item.*;
import io.github.solclient.client.util.data.*;

// Based around https://github.com/BadlionClient/BadlionClientTimerAPI.
// Works with any server that supports Badlion timers.
public class TimersMod extends HudMod {

	public static final TimersMod INSTANCE = new TimersMod();

	private static final String CHANNEL_NAME = "badlion:timers";
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss");
	private static final int TIMER_HEIGHT = 19;

	private static final Map<Long, Timer> DEFAULT_TIMERS = new HashMap<>();

	static {
		DEFAULT_TIMERS.put(0L, new Timer("Diamond II", ItemStack.create(ItemType.DIAMOND)));
		DEFAULT_TIMERS.put(1L, new Timer("Emerald II", ItemStack.create(ItemType.EMERALD)));
	}

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
				y -= ((TIMER_HEIGHT * 2) / 2) * getScale();
				break;
			case BOTTOM:
				y -= (TIMER_HEIGHT * 2) * getScale();
		}
		return new Rectangle(position.getX(), y,
				22 + font.getTextWidth(DEFAULT_TIMERS.get(0L).getName()) + 4 + font.getTextWidth("00:00"), 19 * 2);
	}

	@Override
	public void render(Position position, boolean editMode) {
		super.render(position, editMode);
		Map<Long, Timer> timers;
		if(editMode) {
			timers = DEFAULT_TIMERS;
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
		if(!CHANNEL_NAME.equals(event.getChannelId().path())) {
			return;
		}
		String message = event.getData();

		String type = message.substring(0, message.indexOf('|'));
		JsonObject data = JsonParser.parseString(message.substring(message.indexOf('|') + 1)).getAsJsonObject();

		// uses longs for some reason
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
					ItemStack.create(ItemType.bukkit(item.get("type").getAsString()),
							item.has("amount") ? item.get("amount").getAsInt() : 1));
			timers.put(id, tickTock);
			tickTock.setId(id);
			tickTock.setTime(id);
		}
	}

}
