package io.github.solclient.client.ui.component.impl;

import java.util.function.Consumer;

import org.lwjgl.nanovg.NanoVG;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.util.math.MathHelper;

public class SliderComponent extends Component {

	private float min;
	private float max;
	private float step;
	private float value;
	private final Consumer<Float> callback;
	private boolean selected;
	private final Controller<Colour> colour = theme.button();

	public SliderComponent(float min, float max, float step, float value, Consumer<Float> callback) {
		this.min = min;
		this.max = max;
		this.step = step;
		this.value = value;
		this.callback = callback;
	}

	@Override
	public void render(ComponentRenderInfo info) {
		GlStateManager.enableAlphaTest();
		GlStateManager.enableBlend();

		float x = 80 * (((value - min) / (max - min)));

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, colour.get(this).nvg());
		NanoVG.nvgRoundedRect(nvg, 0, 3, 80, 4, 2);
		NanoVG.nvgFill(nvg);

		NanoVG.nvgFillColor(nvg, theme.fg.nvg());

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgCircle(nvg, x, 5, 4);
		NanoVG.nvgFill(nvg);

		if (selected) {
			value = MathHelper.clamp(
					(float) (min + Math.floor(((info.getRelativeMouseX() / 80F) * (max - min)) / step) * step), min,
					max);
			callback.accept(value);
		}

		super.render(info);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if (button == 0) {
			MinecraftUtils.playClickSound(true);
			selected = true;
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		if (selected) {
			selected = false;
			return true;
		}

		return false;
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, 80, 10);
	}

}
