package me.mcblueparrot.client.ui.screen.mods.option;

import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.elementa.constraints.SubtractiveConstraint;
import me.mcblueparrot.client.mod.ConfigOptionData;
import me.mcblueparrot.client.ui.element.Tickbox;

public class TickboxOptionButton extends ModOptionButton {

	public TickboxOptionButton(ConfigOptionData option) {
		super(option);

		new Tickbox((boolean) option.getValue(), (value) -> option.setValue(value), this)
				.setChildOf(this)
				.setWidth(new PixelConstraint(15)).setHeight(new PixelConstraint(15))
				.setX(new SubtractiveConstraint(new RelativeConstraint(), new PixelConstraint(17)))
				.setY(new CenterConstraint());
	}

}
