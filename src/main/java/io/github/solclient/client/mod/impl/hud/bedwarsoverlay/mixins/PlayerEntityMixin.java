package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.mixins;

import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsMod;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(
            method = "getArmorProtectionValue",
            at = @At(
                    "HEAD"
            ),
            cancellable = true
    )
    public void disableArmor(CallbackInfoReturnable<Integer> ci) {
        if (BedwarsMod.instance.isEnabled() && BedwarsMod.instance.inGame() && !BedwarsMod.instance.displayArmor) {
            ci.setReturnValue(0);
        }
    }

}
