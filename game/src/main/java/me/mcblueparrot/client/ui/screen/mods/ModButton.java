package me.mcblueparrot.client.ui.screen.mods;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIImage;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.ConstantColorConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.elementa.constraints.SubtractiveConstraint;
import gg.essential.elementa.constraints.animation.AnimatingConstraints;
import gg.essential.elementa.constraints.animation.Animations;
import gg.essential.universal.UMatrixStack;
import gg.essential.universal.USound;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.screen.mods.option.ModOptionsScreen;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import net.minecraft.client.Minecraft;

public class ModButton extends UIComponent {

	private Mod mod;
	public UIImage cog;

	public ModButton(ModsScreen parent, Mod mod) {
		this.mod = mod;

		new UIText(mod.getName(), false).setChildOf(this)
				.setX(new PixelConstraint(6)).setY(new PixelConstraint(5));

		new UIText(mod.getDescription(), false).setChildOf(this)
				.setX(new PixelConstraint(6)).setY(new PixelConstraint(17))
				.setColor(new Color(140, 140, 140));

		onMouseEnterRunnable(() -> {
			updateColour(false);
		});

		onMouseLeaveRunnable(() -> {
			updateColour(false);
		});

		onMouseClickConsumer((event) -> {
			boolean cogHovered = cog.isHovered();

			if(event.getMouseButton() == 1) {
				cogHovered = true;
			}
			else if(event.getMouseButton() != 0) {
				return;
			}

			if(mod.isLocked()) {
				cogHovered = true;
			}

			USound.INSTANCE.playButtonPress();

			if(cogHovered) {
				Minecraft.getMinecraft().displayGuiScreen(new ModOptionsScreen(parent, mod));
			}
			else {
				mod.toggle();
				updateColour(false);
			}
		});
	}

	public void updateColour(boolean first) {
		boolean hovered = first ? false : isHovered();

		Colour colour = hovered ? SolClientMod.instance.uiHover : SolClientMod.instance.uiColour;

		if(!mod.isEnabled()) {
			colour = hovered ? Colour.DISABLED_MOD_HOVER : Colour.DISABLED_MOD;
		}

		Colour cogColour = Colour.TRANSPARENT;

		if(hovered) {
			cogColour = cog.isHovered() ? Colour.WHITE : Colour.LIGHTEST_GREY;
		}

		if(first) {
			setColor(colour.toAWT());
			cog.setColor(cogColour.toAWT());
		}
		else {
			Utils.animateColour(this, colour);
			Utils.animateColour(cog, cogColour);
		}
	}

	@Override
	public void draw(UMatrixStack stack) {
		Utils.drawRect(getLeft(), getTop(), getRight(), getBottom(), Colour.BLACK_100.getValue());
		Utils.drawOutline(getLeft(), getTop(), getRight(), getBottom(), getColor().getRGB());

		super.draw(stack);
	}

	public void init(BufferedImage cogImage) {
		if(cog != null) {
			removeChild(cog);
		}

		cog = (UIImage) new UIImage(CompletableFuture.completedFuture(cogImage))
				.setChildOf(this)
				.setWidth(new PixelConstraint(16))
				.setHeight(new PixelConstraint(16))
				.setX(new SubtractiveConstraint(new RelativeConstraint(), new PixelConstraint(16 + 5)))
				.setY(new CenterConstraint());

		cog.onMouseEnterRunnable(() -> updateColour(false));
		cog.onMouseLeaveRunnable(() -> updateColour(false));

		updateColour(true);
	}

}
