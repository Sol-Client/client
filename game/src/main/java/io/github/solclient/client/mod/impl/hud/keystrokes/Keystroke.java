package io.github.solclient.client.mod.impl.hud.keystrokes;

import org.lwjgl.opengl.GL11;

import io.github.solclient.abstraction.mc.DrawableHelper;
import io.github.solclient.abstraction.mc.MinecraftClient;
import io.github.solclient.abstraction.mc.option.KeyBinding;
import io.github.solclient.client.CpsCounter;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Keystroke {

	private final MinecraftClient mc = MinecraftClient.getInstance();
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
		boolean down = keyBinding.isHeld();
		GL11.glEnable(GL11.GL_BLEND);

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

		progress = Utils.clamp(progress, 0, 1);

		if(mod.background) {
			DrawableHelper.fillRect(x, y, x + width, y + height,
					Utils.lerpColour(mod.backgroundColourPressed.getValue(), mod.backgroundColour.getValue(), progress));
		}

		if(mod.border) {
			DrawableHelper.strokeRect(x, y, x + width, y + height,
					Utils.lerpColour(mod.borderColourPressed.getValue(), mod.borderColour.getValue(), progress));
		}

		int fgColour = Utils.lerpColour(mod.textColourPressed.getValue(), mod.textColour.getValue(), progress);

		if(name == null) {
			DrawableHelper.fillRect(x + 10, y + 3, x + width - 10, y + 4, fgColour);

			if(mod.shadow) {
				DrawableHelper.fillRect(x + 11, y + 4, x + width - 9, y + 5, fgColour.getShadowValue());
			}
		}
		else {
			if(mod.cps) {
				CpsCounter monitor;

				if(name.equals("LMB")) {
					monitor = CpsCounter.LMB;
				}
				else if(name.equals("RMB")) {
					monitor = CpsCounter.RMB;
				}
				else {
					monitor = null;
				}

				if(monitor != null) {
					String cpsText = monitor.getCps() + " CPS";
					float scale = 0.5F;

					GL11.glPushMatrix();
					GL11.glScalef(scale, scale, scale);

					mc.getFont().render(cpsText,
							(int) ((x / scale) + (width / 2F / scale) - (mc.getFont().getWidth(cpsText) / 2F)),
							(int) ((y + height - (mc.getFont().getHeight() * scale)) / scale - 3), fgColour.getValue(),
							mod.shadow);

					GL11.glPopMatrix();

					y -= 3;
				}

			}

			y += 1;
			mc.getFont().render(name, (int) (x + (width / 2F) - (mc.getFont().getWidth(name) / 2F)),
					(int) (y + (height / 2F) - (mc.getFont().getHeight() / 2F)), fgColour.getValue(), mod.shadow);
		}
		wasDown = down;
	}

}
