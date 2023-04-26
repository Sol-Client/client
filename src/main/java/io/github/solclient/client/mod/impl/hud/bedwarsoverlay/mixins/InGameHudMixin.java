package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.mixins;

import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsMod;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.MinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    private static final Entity noHungerEntityTM = new MinecartEntity(null);

    @ModifyVariable(
            method = "renderStatusBars",
            at = @At(
                    value="STORE"
            ),
            ordinal = 18
    )
    public int displayHardcoreHearts(int offset) {
        boolean hardcore = BedwarsMod.instance.isEnabled() && BedwarsMod.instance.inGame() && BedwarsMod.instance.hardcoreHearts && !BedwarsMod.instance.getGame().get().getSelf().isBed();
        return hardcore ? 5 : 0;
    }

    @ModifyVariable(
            method = "renderStatusBars",
            at = @At(
                    value="STORE"
            ),
            ordinal = 0
    )
    public Entity dontHunger(Entity normal) {
        if (normal == null && BedwarsMod.instance.isEnabled() && BedwarsMod.instance.inGame() && !BedwarsMod.instance.showHunger) {
            return noHungerEntityTM;
        }
        return normal;
    }

}
