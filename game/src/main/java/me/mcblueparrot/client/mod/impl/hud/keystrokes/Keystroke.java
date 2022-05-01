package me.mcblueparrot.client.mod.impl.hud.keystrokes;

import lombok.RequiredArgsConstructor;
import me.mcblueparrot.client.CpsMonitor;
import me.mcblueparrot.client.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;

@RequiredArgsConstructor
public class Keystroke {

	private final Minecraft mc = Minecraft.getMinecraft();
	private final KeystrokesMod mod;
	private final KeyBinding keyBinding;
	private final String name;
	private final int x;
	protected final int width;
	private final int height;
	private boolean wasDown;
	private long end;

	public void render(int offsetX, int offsetY) {
		int x = this.x + offsetX;
		int y = offsetY;
		boolean down = keyBinding.isKeyDown();
		GlStateManager.enableBlend();

		if((wasDown && !down) || (!wasDown && down)) {
			end = System.currentTimeMillis();
		}

		float progress = 1F;

		if(mod.smoothColours) {
			progress = (System.currentTimeMillis() - end) / 100.0F;
		}

		if(down) {
			progress = 1F - progress;
		}

		progress = MathHelper.clamp_float(progress, 0, 1);

		if(mod.background) {
			GuiScreen.drawRect(x, y, x + width, y + height,
					Utils.blendColor(mod.backgroundColourPressed.getValue(), mod.backgroundColour.getValue(), progress));
		}

		if(mod.border) {
			Utils.drawOutline(x, y, x + width, y + height,
					Utils.blendColor(mod.borderColourPressed.getValue(), mod.borderColour.getValue(), progress));
		}

		int fgColour = Utils.blendColor(mod.textColourPressed.getValue(), mod.textColour.getValue(), progress);
		String name = this.name;

		if(name.equals("Space")) {
			GuiScreen.drawRect(x + 10, y + 3, x + width - 10, y + 4, fgColour);

			if(mod.shadow) {
				GuiScreen.drawRect(x + 11, y + 4, x + width - 9, y + 5, Utils.getShadowColour(fgColour));
			}
		}
		else {
			if(mod.cps) {
				CpsMonitor monitor;

				if(name.equals("LMB")) {
					monitor = CpsMonitor.LMB;
				}
				else if(name.equals("RMB")) {
					monitor = CpsMonitor.RMB;
				}
				else {
					monitor = null;
				}

				if(monitor != null) {
					String cpsText = monitor.getCps() + " CPS";
					float scale = 0.5F;

					GlStateManager.pushMatrix();
					GlStateManager.scale(scale, scale, scale);

					mc.fontRendererObj.drawString(cpsText,
							(x / scale) + (width / 2F / scale) - (mc.fontRendererObj.getStringWidth(cpsText) / 2F),
							(y + height - (mc.fontRendererObj.FONT_HEIGHT * scale)) / scale - 3, fgColour, mod.shadow);

					GlStateManager.popMatrix();

					y -= 3;
				}

			}

			y += 1;
			mc.fontRendererObj.drawString(name, x + (width / 2F) - (mc.fontRendererObj.getStringWidth(name) / 2F),
					y + (height / 2F) - (mc.fontRendererObj.FONT_HEIGHT / 2F), fgColour, mod.shadow);
		}
		wasDown = down;
	}

}
