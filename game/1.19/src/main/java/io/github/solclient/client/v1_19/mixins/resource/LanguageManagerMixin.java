package io.github.solclient.client.v1_19.mixins.resource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.util.TranslationProvider;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin {

	@Inject(method = "reload", at = @At("RETURN"))
	public void loadSCLanguage(ResourceManager manager, CallbackInfo callback) {
		TranslationProvider.load();
	}

}
