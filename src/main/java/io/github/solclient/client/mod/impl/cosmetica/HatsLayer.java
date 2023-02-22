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

package io.github.solclient.client.mod.impl.cosmetica;

import com.mojang.blaze3d.platform.GlStateManager;

import cc.cosmetica.api.Model;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.BakedModel;

public final class HatsLayer extends CosmeticLayer {

	public HatsLayer(PlayerEntityRenderer parent) {
		super(parent);
	}

	/*
	 * Modified from the Cosmetica mod for Fabric.
	 *
	 * Copyright 2022 EyezahMC
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
	 * use this file except in compliance with the License. You may obtain a copy of
	 * the License at
	 *
	 * http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	 * License for the specific language governing permissions and limitations under
	 * the License.
	 */
	@Override
	public void render(AbstractClientPlayerEntity player, float p_177141_2_, float p_177141_3_, float partialTicks,
			float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if (player.isInvisible()) {
			return;
		}

		GlStateManager.pushMatrix();

		for (Model hat : CosmeticaMod.instance.getHats(player)) {
			BakedModel model = CosmeticaMod.instance.bakeIfAbsent(hat);
			if ((hat.flags() & Model.SHOW_HAT_WITH_HELMET) == 0 && player.getArmorSlot(3) != null) {
				continue;
			}

			MinecraftClient.getInstance().getTextureManager().bindTexture(Texture.load(0, 0, hat.getTexture()));

			if ((hat.flags() & Model.LOCK_HAT_ORIENTATION) == 0) {
				Util.render(parent.getModel().head, model, 0, 0.75F, 0, false);
			} else {
				Util.render(parent.getModel().body, model, 0, 0.77F, 0, false);
			}

			GlStateManager.scale(1.001F, 1.001F, 1.001F);
		}

		GlStateManager.popMatrix();
	}

}
