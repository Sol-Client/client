package io.github.solclient.client;

import io.github.solclient.client.culling.CullTask;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.screen.mods.*;
import io.github.solclient.client.util.*;
import io.github.solclient.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

/**
 * Omnipresent listeners.
 */
public final class DefaultEvents {

	private final MinecraftClient mc = MinecraftClient.getInstance();
	private FilePollingTask pollingTask;
	private boolean remindedUpdate;

	{
		try {
			pollingTask = new FilePollingTask(Client.INSTANCE.getMods());
		} catch (Throwable error) {
			Client.LOGGER.warn("Cannot create file polling task", error);
			pollingTask = null;
		}
	}

	@EventHandler
	public void onPostStart(PostGameStartEvent event) {
		Client.INSTANCE.getMods().forEach(Mod::postStart);

		try {
			Utils.unregisterKeyBinding((KeyBinding) GameOptions.class.getField("ofKeyBindZoom").get(mc.options));
		} catch (NoSuchFieldException | IllegalAccessException | ClassCastException ignored) {
			// OptiFine is not enabled.
		}

		new Thread(new CullTask()).start();
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		Utils.USER_DATA.cancel();
		if (!remindedUpdate && SolClientConfig.instance.remindMeToUpdate) {
			remindedUpdate = true;
			SemVer latest = SolClientConfig.instance.latestRelease;
			if (latest != null && latest.isNewerThan(GlobalConstants.VERSION)) {
				Text message = new LiteralText("A new version of Sol Client is available: " + latest
						+ ".\nYou are currently on version " + GlobalConstants.VERSION_STRING + '.');
				message.setStyle(message.getStyle().setFormatting(Formatting.GREEN));
				mc.inGameHud.getChatHud().addMessage(message);
			}
		}
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (pollingTask != null)
			pollingTask.run();

		if (SolClientConfig.instance.modsKey.wasPressed())
			mc.setScreen(new ModsScreen());
		else if (SolClientConfig.instance.editHudKey.wasPressed()) {
			mc.setScreen(new ModsScreen());
			mc.setScreen(new MoveHudsScreen());
		}
	}

	@EventHandler
	public void onQuit(GameQuitEvent event) {
		NanoVGManager.closeContext();

		if (pollingTask != null)
			pollingTask.close();
	}

}
