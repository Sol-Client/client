package me.mcblueparrot.client.mod.impl.quickplay.ui;

import me.mcblueparrot.client.mod.impl.quickplay.QuickPlayMod;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

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
		return new ItemStack(Items.arrow);
	}

}
