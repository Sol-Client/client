package io.github.solclient.client.mod.impl.quickplay.ui;

import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayPalette.QuickPlayPaletteComponent;
import net.minecraft.item.*;

public class AllGamesOption extends QuickPlayOption {

	@Override
	public String getText() {
		return "All Games →";
	}

	@Override
	public void onClick(QuickPlayPaletteComponent palette, QuickPlayMod mod) {
		palette.openAllGames();
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Items.COMPASS);
	}

}
