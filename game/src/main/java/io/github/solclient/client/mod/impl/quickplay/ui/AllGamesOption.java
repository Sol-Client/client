package io.github.solclient.client.mod.impl.quickplay.ui;

import io.github.solclient.abstraction.mc.world.item.ItemStack;
import io.github.solclient.abstraction.mc.world.item.ItemType;
import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;

public class AllGamesOption implements QuickPlayOption {

	@Override
	public String getText() {
		return "All Games >";
	}

	@Override
	public void onClick(QuickPlayPalette palette, QuickPlayMod mod) {
		palette.openAllGames();
	}

	@Override
	public ItemStack getIcon() {
		return ItemStack.create(ItemType.COMPASS);
	}

}
