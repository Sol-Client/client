package io.github.solclient.client.v1_8_9.mixins.resource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.util.TranslationProvider;
import io.github.solclient.client.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.TranslationStorage;

@Mixin(I18n.class)
public class I18nMixin {

	private static final String KEY_PREFIX = "sol_client.";

	@Inject(method = "translate", at = @At("HEAD"), cancellable = true)
	private static void translate(String key, Object[] args, CallbackInfoReturnable<String> callback) {
		if(key.startsWith(KEY_PREFIX)) {
			callback.setReturnValue(Utils.format(TranslationProvider.translate(key.substring(KEY_PREFIX.length())), args));
		}
	}

}
