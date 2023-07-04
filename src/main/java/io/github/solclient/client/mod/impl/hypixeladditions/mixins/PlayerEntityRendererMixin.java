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

package io.github.solclient.client.mod.impl.hypixeladditions.mixins;

import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.*;
import net.minecraft.util.Formatting;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends EntityRenderer<AbstractClientPlayerEntity> {

	protected PlayerEntityRendererMixin(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Inject(method = "method_10209(Lnet/minecraft/client/network/AbstractClientPlayerEntity;DDDLjava/lang/String;FD)V", at = @At("RETURN"))
	public void renderLevelhead(AbstractClientPlayerEntity entityIn, double x, double y, double z, String str,
			float p_177069_9_, double p_177069_10_, CallbackInfo callback) {
        if (BedwarsMod.instance.isEnabled() && BedwarsMod.instance.inGame() && BedwarsMod.instance.bedwarsLevelHead) {
            String levelhead = BedwarsMod.instance.getGame().get().getLevelHead(entityIn);
            if (levelhead != null) {
                renderLabelIfPresent(entityIn, Formatting.GRAY + levelhead, x,
                        y + (getFontRenderer().fontHeight * 1.15F * p_177069_9_), z, 64
                );
            }
        } else if (HypixelAdditionsMod.isEffective()) {
			String levelhead = HypixelAdditionsMod.instance.getLevelhead(
					entityIn == MinecraftClient.getInstance().player, entityIn.getName().asFormattedString(),
					entityIn.getUuid());
            if (levelhead != null) {
                renderLabelIfPresent(entityIn, Formatting.AQUA + "Level: " + Formatting.YELLOW + levelhead, x,
                        y + (getFontRenderer().fontHeight * 1.15F * p_177069_9_), z, 64
                );
            }
		}
	}

}