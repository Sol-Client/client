package me.mcblueparrot.client.ui.element;

import lombok.RequiredArgsConstructor;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import me.mcblueparrot.client.util.font.Font;
import me.mcblueparrot.client.util.font.SlickFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

@RequiredArgsConstructor
public class Button {

	private final Font font;
	private final String text;
	private final Rectangle bounds;
	private final Colour colour;
	private final Colour hoverColour;
	private String icon;

	public Button withIcon(String icon) {
		this.icon = icon;
		return this;
	}

	public void render(int mouseX, int mouseY) {
		Colour buttonColour = contains(mouseX, mouseY) ? hoverColour : colour;
		bounds.fill(Colour.BLACK_128);
		bounds.stroke(buttonColour);

		font.renderString(text, bounds.getX() + (bounds.getWidth() / 2) - (font.getWidth(text) / 2),
				bounds.getY() + (bounds.getHeight() / 2) + (font instanceof SlickFontRenderer ? 0 : 1) - 5, -1);

		if(icon != null) {
			int offset = (bounds.getHeight() / 2) - 8;

			GlStateManager.enableBlend();
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(icon + "_" + Utils.getTextureScale() + ".png"));
			Gui.drawModalRectWithCustomSizedTexture(bounds.getX() + offset, bounds.getY() + offset, 0, 0, 16, 16, 16, 16);
		}
	}

	public boolean contains(int x, int y) {
		return bounds.contains(x, y);
	}

}
