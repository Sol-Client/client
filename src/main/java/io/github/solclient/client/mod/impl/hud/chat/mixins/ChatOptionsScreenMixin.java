package io.github.solclient.client.mod.impl.hud.chat.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.hud.chat.ChatMod;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ChatOptionsScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.I18n;

@Mixin(ChatOptionsScreen.class)
public class ChatOptionsScreenMixin extends Screen {

	@Shadow
	private @Final GameOptions options;

	@Inject(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/options/ChatOptionsScreen;title:Ljava/lang/String;", shift = At.Shift.AFTER), cancellable = true)
	public void replaceGui(CallbackInfo callback) {
		if (ChatMod.enabled) {
			buttons.add(new OptionButtonWidget(GameOptions.Option.REDUCED_DEBUG_INFO.ordinal(),
					this.width / 2 - (150 / 2), height / 6 + 76, GameOptions.Option.REDUCED_DEBUG_INFO,
					options.getValueMessage(GameOptions.Option.REDUCED_DEBUG_INFO)));
			buttons.add(new ButtonWidget(201, width / 2 - (150 / 2), height / 6 + 98, 150, 20,
					I18n.translate("sol_client.more_options")));
			buttons.add(new ButtonWidget(200, width / 2 - (150 / 2), height / 6 + 120, 150, 20,
					I18n.translate("gui.done")));
			callback.cancel();
		}
	}

	@Inject(method = "buttonClicked", at = @At("RETURN"))
	public void buttonClicked(ButtonWidget button, CallbackInfo callback) {
		if (button.id == 201)
			client.setScreen(new ModsScreen(ChatMod.instance));
	}

}
