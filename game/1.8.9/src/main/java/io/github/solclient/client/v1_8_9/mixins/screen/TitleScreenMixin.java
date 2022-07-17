package io.github.solclient.client.v1_8_9.mixins.screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
		buttons.add(new ButtonWidget(realmsButton.id, realmsButton.x, realmsButton.y, I18n.translate("sol_client.mod.screen.title")));
	}

}
