package io.github.solclient.client.mod.impl.tweaks.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.PotionItem;

@Mixin(PotionItem.class)
public class PotionItemMixin {

	@Redirect(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/CommonI18n;translate(Ljava/lang/String;)Ljava/lang/String;", ordinal = 1))
	public String overrideAmplifier(String key) {
		if (TweaksMod.enabled && TweaksMod.instance.arabicNumerals && key.startsWith("potion.potency.")) {
			return Integer.toString(Integer.parseInt(key.substring(15)) + 1);
		}
		return I18n.translate(key);
	}

}