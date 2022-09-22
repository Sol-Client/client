package io.github.solclient.client.v1_8_9.mixins.screen;

import java.io.IOException;

import io.github.solclient.client.ui.screen.TestScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.TrueTypeFont;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

	@Shadow
	private ButtonWidget realmsButton;

	@Inject(method = "initWidgetsNormal", at = @At("RETURN"))
	public void customButtons(CallbackInfo callback) {
		buttons.remove(realmsButton);
		buttons.add(realmsButton = new ButtonWidget(realmsButton.id, realmsButton.x, realmsButton.y,
				I18n.translate("sol_client.mod.screen.title")));
	}

	@Redirect(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;"
			+ "switchToRealms()V"))
	public void openModsMenu(TitleScreen screen) throws IllegalStateException, IOException {
		client.openScreen((Screen) (Object) new TestScreen());
	}

}
