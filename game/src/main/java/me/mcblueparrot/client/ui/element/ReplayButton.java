package me.mcblueparrot.client.ui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ReplayButton extends GuiButton {

	public static final ResourceLocation ICON = new ResourceLocation("replaymod", "logo_button.png");

	public ReplayButton(int buttonId, int x, int y) {
		super(buttonId, x, y, 20, 20, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		super.drawButton(mc, mouseX, mouseY);
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(ICON);
		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height,
				width, height);
	}

}
