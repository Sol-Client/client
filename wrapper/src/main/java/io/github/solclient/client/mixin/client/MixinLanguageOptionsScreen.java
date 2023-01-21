package io.github.solclient.client.mixin.client;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.ui.screen.BetterLanguageGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.resource.language.I18n;

@Mixin(LanguageOptionsScreen.class)
public class MixinLanguageOptionsScreen extends Screen {

	private BetterLanguageGui betterList;

	@Inject(method = "init", at = @At("RETURN"))
	public void overrideList(CallbackInfo callback) {
		betterList = new BetterLanguageGui(client, (LanguageOptionsScreen) (Object) this);
		betterList.setButtonIds(7, 7);
	}

	@Inject(method = "handleMouse", at = @At("HEAD"), cancellable = true)
	public void overrideMouseInput(CallbackInfo callback) {
		callback.cancel();
		super.handleMouse();
		betterList.handleMouse();
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void overrideRender(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) throws IOException {
		callback.cancel();
		betterList.render(mouseX, mouseY, partialTicks);
		drawCenteredString(this.textRenderer, I18n.translate("options.language"), width / 2, 16, -1);
		drawCenteredString(this.textRenderer, "(" + I18n.translate("options.languageWarning") + ")", width / 2,
				height - 56, -1);
		super.render(mouseX, mouseY, partialTicks);
	}

}
