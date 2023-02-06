package io.github.solclient.client.mod.impl.hud.crosshair;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.screen.mods.PixelMatrixComponent;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;

public class CrosshairOption extends ModOption<PixelMatrix> {

	public CrosshairOption(CrosshairMod mod) {
		super(mod.getTranslationKey("option.pixels"), ModOptionStorage.of(PixelMatrix.class, () -> mod.pixels));
	}

	@Override
	public Component createComponent() {
		Component container = createDefaultComponent();

		PixelMatrixComponent matrix = new PixelMatrixComponent(getValue());
		matrix.onClick((info, button) -> {
			if (button != 0)
				return false;

			MinecraftUtils.playClickSound(true);
			container.getScreen().getRoot().setDialog(new CrosshairPaintDialog(this));
			return true;
		});

		container.add(matrix, new AlignedBoundsController(Alignment.END, Alignment.CENTRE));

		return container;
	}

}
