package io.github.solclient.client;

import io.github.solclient.client.culling.CullTask;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.screen.mods.*;
import io.github.solclient.client.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.*;
import net.minecraft.util.*;

/**
 * Omnipresent listeners.
 */
public final class DefaultEvents {

	private final Minecraft mc = Minecraft.getMinecraft();
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
			Utils.unregisterKeyBinding((KeyBinding) GameSettings.class.getField("ofKeyBindZoom").get(mc.gameSettings));
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
				IChatComponent message = new ChatComponentText("A new version of Sol Client is available: " + latest
						+ ".\nYou are currently on version " + GlobalConstants.VERSION_STRING + '.');
				message.setChatStyle(message.getChatStyle().setColor(EnumChatFormatting.GREEN));
				mc.ingameGUI.getChatGUI().printChatMessage(message);
			}
		}
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (pollingTask != null)
			pollingTask.run();

		if (SolClientConfig.instance.modsKey.isPressed())
			mc.displayGuiScreen(new ModsScreen());
		else if (SolClientConfig.instance.editHudKey.isPressed()) {
			mc.displayGuiScreen(new ModsScreen());
			mc.displayGuiScreen(new MoveHudsScreen());
		}
	}

	@EventHandler
	public void onQuit(GameQuitEvent event) {
		NanoVGManager.closeContext();

		if (pollingTask != null)
			pollingTask.close();
	}

}
