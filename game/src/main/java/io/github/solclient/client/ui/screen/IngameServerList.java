package io.github.solclient.client.ui.screen;

import net.minecraft.client.gui.*;

public class IngameServerList extends GuiMultiplayer {

	public IngameServerList(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	public void connectToSelected() {
		disconnect();
		super.connectToSelected();
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		super.confirmClicked(result, id);
	}

	private void disconnect() {
		mc.theWorld.sendQuittingDisconnectingPacket();
		mc.loadWorld(null);
	}

}
