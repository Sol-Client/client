package io.github.solclient.client.mod.impl.tweaks.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

	@Inject(method = "getTranslatedName", at = @At("HEAD"), cancellable = true)
	public void overrideName(int level, CallbackInfoReturnable<String> callback) {
		if (TweaksMod.enabled && TweaksMod.instance.arabicNumerals)
			callback.setReturnValue(I18n.translate(getTranslationKey()) + ' ' + level);
	}

	@Shadow
	public abstract String getTranslationKey();

}