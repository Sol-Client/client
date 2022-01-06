package me.mcblueparrot.client.ui.element;

import java.util.function.Consumer;

import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.elementa.constraints.SubtractiveConstraint;
import gg.essential.universal.UMatrixStack;
import gg.essential.universal.USound;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;

public class Tickbox extends UIComponent {

	private UIComponent controller;
	private boolean value;
	private UIBlock box;

	public Tickbox(boolean value, Consumer<Boolean> valueHandler, UIComponent mouseController) {
		this.value = value;

		box = (UIBlock) new UIBlock()
				.setChildOf(this)
				.setX(new PixelConstraint(2)).setY(new PixelConstraint(2))
				.setWidth(new SubtractiveConstraint(new RelativeConstraint(), new PixelConstraint(4)))
				.setHeight(new SubtractiveConstraint(new RelativeConstraint(), new PixelConstraint(4)));

		if(mouseController == null) {
			mouseController = this;
		}

		this.controller = mouseController;

		updateColour(true);

		mouseController.onMouseEnterRunnable(() -> updateColour(false));
		mouseController.onMouseLeaveRunnable(() -> updateColour(false));

		mouseController.onMouseClickConsumer((event) -> {
			if(event.getMouseButton() != 0) {
				return;
			}

			USound.INSTANCE.playButtonPress();
			this.value = !this.value;
			valueHandler.accept(this.value);

			updateColour(false);
		});
	}

	private void updateColour(boolean first) {
		boolean hovered = !first && controller.isHovered();

		Colour tickboxColour = hovered ? SolClientMod.instance.uiHover : SolClientMod.instance.uiColour;
		Colour boxColour = tickboxColour;

		if(!value) {
			boxColour = boxColour.withAlpha(0);
		}

		if(first) {
			setColor(tickboxColour.toAWT());
			box.setColor(boxColour.toAWT());
		}
		else {
			Utils.animateColour(this, tickboxColour);
			Utils.animateColour(box, boxColour);
		}
	}

	@Override
	public void draw(UMatrixStack stack) {
		Utils.drawOutline(getLeft(), getTop(), getRight(), getBottom(), getColor().getRGB());

		super.draw(stack);
	}

}
