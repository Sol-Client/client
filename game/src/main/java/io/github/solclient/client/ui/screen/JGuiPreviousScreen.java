package io.github.solclient.client.ui.screen;

import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiScreen;

public class JGuiPreviousScreen extends net.minecraft.client.gui.GuiScreen {

	private GuiScreen previous;

	public JGuiPreviousScreen(GuiScreen previous) {
		this.previous = previous;
	}

	@Override
	public void initGui() {
		super.initGui();
		previous.display();
	}

}
