package me.mcblueparrot.client.ui.screen.mods.option;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import javax.imageio.ImageIO;

import com.google.common.base.Supplier;
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
import me.mcblueparrot.client.mod.ConfigOnlyMod;
import me.mcblueparrot.client.mod.ConfigOptionData;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.screen.mods.AbstractModsScreen;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.font.SlickFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class ModOptionsScreen extends AbstractModsScreen {

	private List<ModOptionButton> buttons = new ArrayList<>();
	private ModsScreen parent;

	public ModOptionsScreen(ModsScreen parent, Mod mod) {
		super(parent);

		this.parent = parent;

		new UIText(mod.getName(), false).setChildOf(getWindow()).setX(new CenterConstraint()).setY(new PixelConstraint(10F));

		for(ConfigOptionData option : mod.getOptions()) {
			if(mod instanceof ConfigOnlyMod && option.name.equals("Enabled")) {
				continue;
			}

			Function<ConfigOptionData, ? extends ModOptionButton> function = ModOptionButton::new;

			if(option.getType() == boolean.class) {
				function = TickboxOptionButton::new;
			}

			ModOptionButton button = function.apply(option);

			button.setX(new CenterConstraint()).setY(new SiblingConstraint(5F))
					.setWidth(new PixelConstraint(300)).setHeight(new PixelConstraint(20))
					.setChildOf(container);

			buttons.add(button);
		}
	}

	@Override
	public void onKeyPressed(int keyCode, char typedChar, Modifiers modifiers) {
		super.onKeyPressed(keyCode, typedChar, modifiers);

		if(keyCode == Client.INSTANCE.modsKey.getKeyCode()) {
			parent.restorePreviousScreen();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
