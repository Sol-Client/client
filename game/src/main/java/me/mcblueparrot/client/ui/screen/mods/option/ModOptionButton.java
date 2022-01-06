package me.mcblueparrot.client.ui.screen.mods.option;

import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import me.mcblueparrot.client.mod.ConfigOptionData;
import me.mcblueparrot.client.util.data.Colour;

public class ModOptionButton extends UIBlock {

	protected ConfigOptionData option;

	public ModOptionButton(ConfigOptionData option) {
		super(Colour.BLACK_100.toAWT());
		this.option = option;
		new UIText(option.name, false).setChildOf(this)
				.setX(new PixelConstraint(6)).setY(new CenterConstraint());
	}

}
