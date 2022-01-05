package me.mcblueparrot.client.ui.screen.mods;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import com.google.gson.JsonParser;

import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.ScrollComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIContainer;
import gg.essential.elementa.components.UIImage;
import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.input.UITextInput;
import gg.essential.elementa.components.inspector.Inspector;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.ChildBasedSizeConstraint;
import gg.essential.elementa.constraints.ColorConstraint;
import gg.essential.elementa.constraints.ConstantColorConstraint;
import gg.essential.elementa.constraints.ConstraintType;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.elementa.constraints.SiblingConstraint;
import gg.essential.elementa.constraints.SubtractiveConstraint;
import gg.essential.elementa.constraints.animation.AnimatingConstraints;
import gg.essential.elementa.constraints.animation.Animations;
import gg.essential.elementa.constraints.resolution.ConstraintVisitor;
import gg.essential.elementa.effects.ScissorEffect;
import gg.essential.elementa.font.BasicFontRenderer;
import gg.essential.elementa.font.FontProvider;
import gg.essential.elementa.font.FontRenderer;
import gg.essential.elementa.font.data.Font;
import gg.essential.elementa.font.data.FontInfo;
import gg.essential.elementa.markdown.MarkdownComponent;
import gg.essential.universal.UMatrixStack;
import gg.essential.universal.UKeyboard.Modifiers;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.font.SlickFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class ModsScreen extends WindowScreen {

	private List<ModButton> buttons = new ArrayList<>();

	public ModsScreen(GuiScreen parent, Mod mod) {
		this();
	}

	public ModsScreen(GuiScreen parent) {
		this();
	}

	public ModsScreen() {
//		FontProvider provider = new FontProvider() {
//
//			private FontProvider cachedValue = this;
//			private boolean recalculate;
//			private UIComponent constrainTo;
//
//			@Override
//			public void visitImpl(ConstraintVisitor arg0, ConstraintType arg1) {
//			}
//
//			@Override
//			public void setRecalculate(boolean recalculate) {
//				this.recalculate = recalculate;
//			}
//
//			@Override
//			public void setConstrainTo(UIComponent constrainTo) {
//				this.constrainTo = constrainTo;
//			}
//
//			@Override
//			public void setCachedValue(FontProvider cachedValue) {
//				this.cachedValue = cachedValue;
//			}
//
//			@Override
//			public boolean getRecalculate() {
//				return recalculate;
//			}
//
//			@Override
//			public UIComponent getConstrainTo() {
//				return constrainTo;
//			}
//
//			@Override
//			public FontProvider getCachedValue() {
//				return cachedValue;
//			}
//
//			@Override
//			public float getStringWidth(String string, float pointSize) {
//				return SlickFontRenderer.DEFAULT.getWidth(string);
//			}
//
//			@Override
//			public float getStringHeight(String string, float pointSize) {
//				return SlickFontRenderer.DEFAULT.getHeight(string);
//			}
//
//			@Override
//			public void drawString(UMatrixStack matrixStack, String string, Color color, float x, float y,
//					float originalPointSize, float scale, boolean shadow, Color shadowColor) {
//				SlickFontRenderer.DEFAULT.renderStringScaled(string, (int) x, (int) y, color.getRGB(), (float) scale);
//			}
//
//			@Override
//			public float getShadowHeight() {
//				return 1;
//			}
//
//			@Override
//			public float getBelowLineHeight() {
//				return 1;
//			}
//
//			@Override
//			public float getBaseLineHeight() {
//				return 7;
//			}
//
//		};

		super(ElementaVersion.V1, true, false);

		new UIText("Mods", false).setChildOf(getWindow()).setX(new CenterConstraint()).setY(new PixelConstraint(10F));

		UIContainer scrollBox = (UIContainer) new UIContainer().setChildOf(getWindow())
				.setWidth(new RelativeConstraint())
				.setHeight(new SubtractiveConstraint(new RelativeConstraint(), new PixelConstraint(60)))
				.setX(new PixelConstraint(0)).setY(new PixelConstraint(30));

		ScrollComponent container = (ScrollComponent) new ScrollComponent("", 0, Color.WHITE, false, true, false, false, 35, 1)
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

		for(ModCategory category : ModCategory.values()) {
			if(category.toString() == null) continue;

			new UIText(category.toString(), false).setChildOf(container).setX(new CenterConstraint())
					.setY(new SiblingConstraint(10F));

			for(Mod mod : category.getMods("")) {
				buttons.add((ModButton) new ModButton(mod).setChildOf(container).setX(new CenterConstraint()).setY(new SiblingConstraint(5F))
						.setWidth(new PixelConstraint(300F)).setHeight(new PixelConstraint(30F)));
			}
		}

//		new Inspector(getWindow()).setX(new PixelConstraint(5)).setY(new PixelConstraint(5)).setChildOf(getWindow());
	}

	@Override
	public void onKeyPressed(int keyCode, char typedChar, Modifiers modifiers) {
		super.onKeyPressed(keyCode, typedChar, modifiers);

		if(keyCode == Client.INSTANCE.modsKey.getKeyCode()) {
			restorePreviousScreen();
		}
	}

	public void updateFont() {

	}

	public void switchMod(Mod mod) {
		// TODO Auto-generated method stub

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
	public void initScreen(int width, int height) {
		super.initScreen(width, height);

		try {
			BufferedImage cogImage = ImageIO.read(getClass().getResourceAsStream("/assets/minecraft/textures/gui/sol_client_settings_" + Utils.getTextureScale() + ".png"));

			for(ModButton button : buttons) {
				button.init(cogImage);
			}
		}
		catch(IOException error) {
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
