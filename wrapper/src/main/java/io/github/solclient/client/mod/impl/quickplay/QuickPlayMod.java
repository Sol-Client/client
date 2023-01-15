package io.github.solclient.client.mod.impl.quickplay;

import java.util.*;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.*;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.impl.quickplay.database.*;
import io.github.solclient.client.mod.impl.quickplay.ui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;

public class QuickPlayMod extends Mod {

	@Option
	private final KeyBinding menuKey = new KeyBinding(getTranslationKey() + ".key", Keyboard.KEY_M,
			GlobalConstants.KEY_CATEGORY);
	private boolean got;
	private QuickPlayDatabase database;
	@Expose
	private final List<String> recentlyPlayed = new ArrayList<>();

	@Override
	public String getId() {
		return "quickplay";
	}

	@Override
	public String getCredit() {
		return I18n.translate("sol_client.mod.screen.originally_by", "robere2");
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.INTEGRATION;
	}

	public List<QuickPlayOption> getRecentlyPlayed() {
		return recentlyPlayed.stream().map((fullId) -> database.getGame(fullId.substring(0, fullId.indexOf('.')))
				.getMode(fullId.substring(fullId.indexOf('.') + 1))).collect(Collectors.toList());
	}

	public List<QuickPlayOption> getGameOptions() {
		return new ArrayList<>(database.getGames().values());
	}

	public List<QuickPlayGame> getGames() {
		return new ArrayList<>(database.getGames().values());
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (database != null && menuKey.isPressed() && DetectedServer.current() == DetectedServer.HYPIXEL)
			mc.setScreen(new QuickPlayPalette(this));
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		if (!got) {
			got = true;
			Thread thread = new Thread(() -> database = new QuickPlayDatabase());
			thread.setDaemon(true);
			thread.start();
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	public void playGame(QuickPlayGameMode mode) {
		mc.player.sendChatMessage(mode.getCommand());

		if (!Screen.hasShiftDown())
			mc.setScreen(null);

		recentlyPlayed.removeIf(mode.getFullId()::equals);
		recentlyPlayed.add(0, mode.getFullId());

		if (recentlyPlayed.size() > 15)
			recentlyPlayed.remove(recentlyPlayed.size() - 1);

		Client.INSTANCE.save();
	}

}
