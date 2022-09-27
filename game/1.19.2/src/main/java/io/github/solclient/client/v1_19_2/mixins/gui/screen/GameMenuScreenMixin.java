package io.github.solclient.client.v1_19_2.mixins.gui.screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.solclient.client.ui.screen.mods.ModsScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {

	protected GameMenuScreenMixin(Text title) {
		super(title);
	}

	@Redirect(method = "initWidgets", at = @At(value = "NEW", target = "(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)Lnet/minecraft/client/gui/widget/ButtonWidget;", ordinal = 3))
	public ButtonWidget replaceWithMods(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
		return new ButtonWidget(x, y, width, height, Text.translatable("mod.screen.title"),
				(ignored) -> client.setScreen((Screen) (Object) new ModsScreen()));
	}

}
