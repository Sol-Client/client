package me.mcblueparrot.client.mod.impl.quickplay;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.DetectedServer;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PreTickEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.impl.quickplay.database.QuickPlayDatabase;
import me.mcblueparrot.client.mod.impl.quickplay.database.QuickPlayGame;
import me.mcblueparrot.client.mod.impl.quickplay.database.QuickPlayGameMode;
import me.mcblueparrot.client.mod.impl.quickplay.ui.QuickPlayOption;
import me.mcblueparrot.client.mod.impl.quickplay.ui.QuickPlayPalette;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

public class QuickPlayMod extends Mod {

	public KeyBinding menuKey = new KeyBinding("Quick Play", Keyboard.KEY_M, "Sol Client");
	private QuickPlayDatabase database;
	@Expose
	private List<String> recentlyPlayed = new ArrayList<>();

	public QuickPlayMod() {
		super("Quick Play", "quickplay", "Quickly queue any game.", ModCategory.UTILITY);
		database = new QuickPlayDatabase();
		Client.INSTANCE.registerKeyBinding(menuKey);
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
		if(menuKey.isKeyDown()) {
			mc.displayGuiScreen(new QuickPlayPalette(this));
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	public void playGame(QuickPlayGameMode mode) {
		mc.thePlayer.sendChatMessage(mode.getCommand());

		if(!GuiScreen.isShiftKeyDown()) {
			mc.displayGuiScreen(null);
		}

		recentlyPlayed.removeIf(mode.getFullId()::equals);
		recentlyPlayed.add(0, mode.getFullId());

		if(recentlyPlayed.size() > 15) {
			recentlyPlayed.remove(recentlyPlayed.size() - 1);
		}

		Client.INSTANCE.save();
	}

}
