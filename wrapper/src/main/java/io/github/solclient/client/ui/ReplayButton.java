package io.github.solclient.client.ui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class ReplayButton extends ButtonWidget {

	public static final Identifier ICON = new Identifier("replaymod", "logo_button.png");

	public ReplayButton(int buttonId, int x, int y) {
		super(buttonId, x, y, 20, 20, "");
	}

	@Override
	public void render(MinecraftClient mc, int mouseX, int mouseY) {
		super.render(mc, mouseX, mouseY);
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(ICON);
		drawTexture(x, y, 0, 0, width, height, width, height);
	}

}
