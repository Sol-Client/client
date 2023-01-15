package io.github.solclient.client.ui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

public class IngameServerList extends MultiplayerScreen {

	public IngameServerList(Screen parentScreen) {
		super(parentScreen);
	}

	@Override
	public void connect() {
		disconnect();
		super.connect();
	}

	private void disconnect() {
		client.world.disconnect();
		client.connect(null);
	}

}
