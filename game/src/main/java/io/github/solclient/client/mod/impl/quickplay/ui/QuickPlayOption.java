package io.github.solclient.client.mod.impl.quickplay.ui;

import io.github.solclient.abstraction.mc.world.item.ItemStack;
import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;

public interface QuickPlayOption {

	String getText();

	void onClick(QuickPlayPalette palette, QuickPlayMod mod);

	ItemStack getIcon();

}
