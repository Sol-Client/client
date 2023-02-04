package io.github.solclient.client.mod.impl.quickplay.ui;

import org.lwjgl.input.Mouse;
import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayPalette.QuickPlayPaletteComponent;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;

public class QuickPlayOptionComponent extends ColouredComponent {

	private final QuickPlayPaletteComponent screen;
	private final QuickPlayOption option;
	private int previousMouseX, previousMouseY;

	public QuickPlayOptionComponent(QuickPlayPaletteComponent screen, QuickPlayOption option) {
		super(new AnimatedColourController((component, defaultColour) -> screen.getSelected() == component
				? (component.isHovered() ? theme.buttonHover : theme.button)
				: Colour.TRANSPARENT));
		this.screen = screen;
		this.option = option;
		add(new LabelComponent((component, defaultText) -> option.getText()), new AlignedBoundsController(
				Alignment.START, Alignment.CENTRE, (component, defaultBounds) -> defaultBounds.offset(6, 0)));
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(230, 20);
	}

	@Override
	public void render(ComponentRenderInfo info) {
		if (isHovered() && (previousMouseX != Mouse.getX() || previousMouseY != Mouse.getY()))
			screen.setSelected(this);

		previousMouseX = Mouse.getX();
		previousMouseY = Mouse.getY();

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, getColour().nvg());
		NanoVG.nvgRoundedRect(nvg, 0, 0, getBounds().getWidth(), getBounds().getHeight(), 4);
		NanoVG.nvgFill(nvg);

		super.render(info);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if (button != 0)
			return false;

		MinecraftUtils.playClickSound(true);
		option.onClick(screen, screen.getMod());
		return true;
	}

}
