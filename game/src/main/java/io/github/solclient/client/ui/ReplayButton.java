package io.github.solclient.client.ui;

import io.github.solclient.abstraction.mc.Identifier;
import io.github.solclient.client.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ReplayButton extends GuiButton {

	public static final Identifier ICON = Identifier.replayMod("logo_button.png");

	public ReplayButton(int buttonId, int x, int y) {
		super(buttonId, x, y, 20, 20, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		super.drawButton(mc, mouseX, mouseY);
		Utils.glColour(-1);
		mc.getTextureManager().bindTexture(ICON);
		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height,
				width, height);
	}

}
