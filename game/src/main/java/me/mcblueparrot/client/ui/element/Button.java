package me.mcblueparrot.client.ui.element;

import java.awt.Color;

import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.universal.USound;

public class Button extends UIBlock {

	public Button(String text, Runnable onClick) {
		super(new Color(0, 150, 0));
		onMouseClickConsumer((event) -> {
			if(event.getMouseButton() == 0) {
				USound.INSTANCE.playButtonPress();
				onClick.run();
			}
		});

		new UIText(text, false)
				.setChildOf(this)
				.setX(new CenterConstraint()).setY(new CenterConstraint());
	}

}
