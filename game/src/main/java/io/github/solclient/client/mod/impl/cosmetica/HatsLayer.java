package io.github.solclient.client.mod.impl.cosmetica;

import cc.cosmetica.api.Model;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.model.IBakedModel;

public final class HatsLayer extends CosmeticLayer {

	public HatsLayer(RenderPlayer parent) {
		super(parent);
	}

	/*
	 * Copyright 2022 EyezahMC
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 *     http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 */
	@Override
	public void doRenderLayer(AbstractClientPlayer player, float p_177141_2_, float p_177141_3_,
			float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if(player.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
			return;
		}

		GlStateManager.pushMatrix();

		for(Model hat : CosmeticaMod.instance.getHats(player)) {
			IBakedModel model = CosmeticaMod.instance.bakeIfAbsent(hat);
			if((hat.flags() & Model.SHOW_HAT_WITH_HELMET) == 0 && player.getCurrentArmor(3) != null) {
				continue;
			}

			Minecraft.getMinecraft().getTextureManager().bindTexture(Texture.load(0, 0, hat.getTexture()));

			if((hat.flags() & Model.LOCK_HAT_ORIENTATION) == 0) {
				Util.render(parent.getMainModel().bipedHead, model, 0, 0.75F, 0, false);
			}
			else {
				Util.render(parent.getMainModel().bipedBody, model, 0, 0.77F, 0, false);
			}

			GlStateManager.scale(1.001F, 1.001F, 1.001F);
		}

		GlStateManager.popMatrix();
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

}
