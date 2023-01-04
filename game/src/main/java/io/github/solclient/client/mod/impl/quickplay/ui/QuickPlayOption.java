package io.github.solclient.client.mod.impl.quickplay.ui;

import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayPalette.QuickPlayPaletteComponent;
import io.github.solclient.client.ui.component.Component;
import net.minecraft.item.ItemStack;

public abstract class QuickPlayOption {

	public abstract String getText();

	public abstract void onClick(QuickPlayPaletteComponent palette, QuickPlayMod mod);

	public abstract ItemStack getIcon();

	public Component component(QuickPlayPaletteComponent screen) {
		return new QuickPlayOptionComponent(screen, this);
	}

}
