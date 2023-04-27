/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
