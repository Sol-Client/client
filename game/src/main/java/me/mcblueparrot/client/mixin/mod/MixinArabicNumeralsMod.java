package me.mcblueparrot.client.mixin.mod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mcblueparrot.client.mod.impl.ArabicNumeralsMod;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemPotion;
import net.minecraft.util.StatCollector;

public class MixinArabicNumeralsMod {

    @Mixin(Enchantment.class)
    public static abstract class MixinEnchantment {

        @Inject(method = "getTranslatedName", at = @At("HEAD"), cancellable = true)
        public void overrideName(int level, CallbackInfoReturnable<String> callback) {
            if(ArabicNumeralsMod.enabled) {
                callback.setReturnValue(StatCollector.translateToLocal(getName()) + " " + level);
            }
        }

        @Shadow
        public abstract String getName();
        
    }

    @Mixin(InventoryEffectRenderer.class)
    public static class MixinInventoryEffectRenderer {

        @Redirect(method = "drawActivePotionEffects", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
        public String overrideLevel(String translateKey, Object[] parameters) {
            if(ArabicNumeralsMod.enabled && translateKey.startsWith("enchantment.level.")) {
                return Integer.toString(Integer.parseInt(translateKey.substring(18)) + 1);
            }
            return I18n.format(translateKey, parameters);
        }

    }

    @Mixin(ItemPotion.class)
    public static class MixinItemPotion {

        @Redirect(method = "addInformation", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/util/StatCollector;translateToLocal(Ljava/lang/String;)Ljava/lang/String;",
                ordinal = 1))
        public String overrideAmplifier(String key) {
            if(ArabicNumeralsMod.enabled && key.startsWith("potion.potency.")) {
                return key.substring(15);
            }
            return StatCollector.translateToLocal(key);
        }

    }

}
