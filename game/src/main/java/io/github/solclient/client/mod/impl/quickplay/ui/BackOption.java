package io.github.solclient.client.mod.impl.quickplay.ui;

import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayPalette.QuickPlayPaletteComponent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class BackOption extends QuickPlayOption {

	@Override
	public String getText() {
		return "← Back";
	}

	@Override
	public void onClick(QuickPlayPaletteComponent palette, QuickPlayMod mod) {
		palette.back();
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Items.arrow);
	}

}
