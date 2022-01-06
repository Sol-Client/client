package me.mcblueparrot.client.ui.screen.mods;

import java.awt.Color;

import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.ScrollComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIContainer;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.ConstantColorConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.elementa.constraints.SubtractiveConstraint;
import gg.essential.elementa.constraints.animation.AnimatingConstraints;
import gg.essential.elementa.constraints.animation.Animations;
import gg.essential.universal.UMatrixStack;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.element.Button;
import me.mcblueparrot.client.util.data.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public abstract class AbstractModsScreen extends WindowScreen {

	protected ScrollComponent container;

	public AbstractModsScreen(GuiScreen parent) {
		super(ElementaVersion.V1, true, false);

		UIContainer scrollBox = (UIContainer) new UIContainer().setChildOf(getWindow())
				.setWidth(new RelativeConstraint())
				.setHeight(new SubtractiveConstraint(new RelativeConstraint(), new PixelConstraint(60)))
				.setX(new PixelConstraint(0)).setY(new PixelConstraint(30));

		container = (ScrollComponent) new ScrollComponent("", 0, Color.WHITE, false, true, false, false, 35, 1)
				.setChildOf(scrollBox)
				.setWidth(new RelativeConstraint()).setHeight(new RelativeConstraint());

		UIBlock scrollBar = (UIBlock) new UIBlock(SolClientMod.instance.uiColour.toAWT())
				.setChildOf(scrollBox)
				.setX(new SubtractiveConstraint(new RelativeConstraint(), new PixelConstraint(5)))
				.setY(new PixelConstraint(0))
				.setWidth(new PixelConstraint(5))
				.setHeight(container.getConstraints().getHeight());

		container.setScrollBarComponent(scrollBar, true, false);

		scrollBar.onMouseEnterRunnable(() -> {
			AnimatingConstraints animation = scrollBar.makeAnimation();
			animation.setColorAnimation(Animations.LINEAR, 0.1F, new ConstantColorConstraint(SolClientMod.instance.uiHover.toAWT()));
			scrollBar.animateTo(animation);
		});

		scrollBar.onMouseLeaveRunnable(() -> {
			AnimatingConstraints animation = scrollBar.makeAnimation();
			animation.setColorAnimation(Animations.LINEAR, 0.1F, new ConstantColorConstraint(SolClientMod.instance.uiColour.toAWT()));
			scrollBar.animateTo(animation);
		});



		new Button("Done", () -> Minecraft.getMinecraft().displayGuiScreen(parent))
				.setChildOf(getWindow())
				.setX(new CenterConstraint()).setY(new SubtractiveConstraint(new RelativeConstraint(), new PixelConstraint(25)))
				.setWidth(new PixelConstraint(100))
				.setHeight(new PixelConstraint(20));
	}

	@Override
	public void onDrawScreen(UMatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if(mc.theWorld == null) {
			drawRect(0, 0, width, height, new Colour(30, 30, 30).getValue());
		}
		else {
			drawWorldBackground(0);
		}

		super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
