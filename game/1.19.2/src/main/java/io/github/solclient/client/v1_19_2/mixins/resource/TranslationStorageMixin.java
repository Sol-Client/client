package io.github.solclient.client.v1_19_2.mixins.resource;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.util.TranslationProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.util.Language;

@Mixin(TranslationStorage.class)
public class TranslationStorageMixin {

	private static final String KEY_PREFIX = "sol_client.";

	@Inject(method = "get", at = @At("HEAD"), cancellable = true)
	private void get(String key, CallbackInfoReturnable<String> callback) {
		if(key.startsWith(KEY_PREFIX)) {
			callback.setReturnValue(TranslationProvider.translate(key.substring(KEY_PREFIX.length())));
		}
	}

	@Inject(method = "hasTranslation", at = @At("HEAD"), cancellable = true)
	private void hasTranslation(String key, CallbackInfoReturnable<Boolean> callback) {
		if(key.startsWith(KEY_PREFIX)) {
			callback.setReturnValue(TranslationProvider.hasTranslation(key.substring(KEY_PREFIX.length())));
		}
	}

}
