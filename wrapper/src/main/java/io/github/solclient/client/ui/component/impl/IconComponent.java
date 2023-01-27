package io.github.solclient.client.ui.component.impl;

import org.lwjgl.nanovg.*;

import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.util.Identifier;

public class IconComponent extends ColouredComponent {

	private final Controller<String> iconName;
	private final int width;
	private final int height;

	public IconComponent(String iconName, int width, int height) {
		this((component, defaultName) -> iconName, width, height, (component, defaultColour) -> defaultColour);
	}

	public IconComponent(String iconName, int width, int height, Controller<Colour> colour) {
		this((component, defaultName) -> iconName, width, height, colour);
	}

	public IconComponent(Controller<String> iconName, int width, int height, Controller<Colour> colour) {
		super(colour);
		this.iconName = iconName;
		this.width = width;
		this.height = height;
	}

	@Override
	public void render(ComponentRenderInfo info) {
		if (getColour().getAlpha() == 0)
			return;

		NanoVG.nvgBeginPath(nvg);

		NVGPaint paint = MinecraftUtils.nvgMinecraftTexturePaint(nvg,
				new Identifier("sol_client", "textures/gui/" + iconName.get(this) + ".png"), 0, 0, width, height, 0);
		paint.innerColor(getColour().nvg());

		NanoVG.nvgFillPaint(nvg, paint);
		NanoVG.nvgRect(nvg, 0, 0, width, height);
		NanoVG.nvgFill(nvg);

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, width, height);
	}

}
