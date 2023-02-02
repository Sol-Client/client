package io.github.solclient.client.ui.component.impl;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.lib.penner.easing.*;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.util.data.Colour;

public class WindowComponent extends BlockComponent {

	private static final int DURATION = 150;

	private final long openTime = System.currentTimeMillis();

	public WindowComponent(Colour colour, float radius, float strokeWidth) {
		super(colour, radius, strokeWidth);
	}

	public void applyAnimation() {
		if (!SolClientConfig.instance.openAnimation)
			return;

		float progress = Sine.easeOut(Math.min(System.currentTimeMillis() - openTime, DURATION), 0, 1, DURATION);
		NanoVG.nvgGlobalAlpha(nvg, progress);
		progress = 0.8F + (progress * 0.2F);
		NanoVG.nvgTranslate(nvg, (getBounds().getWidth() / 2F) * (1 - progress),
				(getBounds().getHeight() / 2F) * (1 - progress));
		NanoVG.nvgScale(nvg, progress, progress);
	}

}
