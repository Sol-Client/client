package io.github.solclient.client.mod.impl.quickplay.ui;

import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.platform.mc.world.item.*;

public final class AllGamesOption implements QuickPlayOption {

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
