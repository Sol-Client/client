package io.github.solclient.client.mod.impl.quickplay;

import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.*;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PreTickEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.impl.quickplay.database.*;
import io.github.solclient.client.mod.impl.quickplay.ui.*;
import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.util.Utils;

public class QuickPlayMod extends Mod {

	public static final QuickPlayMod INSTANCE = new QuickPlayMod();

	@Option
	private final KeyBinding menuKey = KeyBinding.create(getTranslationKey() + ".key", Input.R, Constants.KEY_CATEGORY);
	@Expose
	private final List<String> recentlyPlayed = new ArrayList<>();

	private boolean got;
	private QuickPlayDatabase database;

	@Override
	public void onRegister() {
		super.onRegister();
		mc.getOptions().addKey(menuKey);
	}

	@Override
	public String getId() {
		return "quickplay";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.INTEGRATION;
	}

	public List<QuickPlayOption> getRecentlyPlayed() {
		return recentlyPlayed.stream().map((fullId) -> database.getGame(fullId.substring(0, fullId.indexOf('.')))
						.getMode(fullId.substring(fullId.indexOf('.') + 1)))
				.collect(Collectors.toList());
	}

	public List<QuickPlayOption> getGameOptions() {
		return new ArrayList<>(database.getGames().values());
	}

	public List<QuickPlayGame> getGames() {
		return new ArrayList<>(database.getGames().values());
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if(database != null && menuKey.isHeld()
				&& Client.INSTANCE.detectedServer == DetectedServer.HYPIXEL) {
			mc.setScreen(new QuickPlayPalette(this));
		}
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		if(!got) {
			got = true;
			Utils.MAIN_EXECUTOR.submit(() -> {
				database = new QuickPlayDatabase();
			});
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	public void playGame(QuickPlayGameMode mode) {
		mc.getPlayer().chat(mode.getCommand());

		if(!Input.isShiftDown()) {
			mc.closeScreen();
		}

		recentlyPlayed.removeIf(mode.getFullId()::equals);
		recentlyPlayed.add(0, mode.getFullId());

		if(recentlyPlayed.size() > 15) {
			recentlyPlayed.remove(recentlyPlayed.size() - 1);
		}

		Client.INSTANCE.save();
	}

}
