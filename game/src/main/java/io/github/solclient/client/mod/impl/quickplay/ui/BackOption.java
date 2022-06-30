package io.github.solclient.client.mod.impl.quickplay.ui;

import io.github.solclient.abstraction.mc.world.item.ItemStack;
import io.github.solclient.abstraction.mc.world.item.ItemType;
import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;

public class BackOption implements QuickPlayOption {

	@Override
	public String getText() {
		return "< Back";
	}

	@Override
	public void onClick(QuickPlayPalette palette, QuickPlayMod mod) {
		palette.back();
	}

	@Override
	public ItemStack getIcon() {
		return ItemStack.create(ItemType.ARROW);
	}

}
