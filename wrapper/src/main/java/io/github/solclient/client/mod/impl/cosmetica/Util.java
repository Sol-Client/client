package io.github.solclient.client.mod.impl.cosmetica;

import com.mojang.blaze3d.platform.GlStateManager;

import cc.cosmetica.api.Model;
import io.github.solclient.client.mixin.client.*;
import io.github.solclient.client.util.Utils;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.BlockModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;

@UtilityClass
class Util {

	private static final float MAGIC_SCALE = 1.001F;

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
	public void render(ModelPart part, BakedModel model, float x, float y, float z, boolean flip) {
		GlStateManager.pushMatrix();
		transform(part);
		GlStateManager.scale(MAGIC_SCALE, -MAGIC_SCALE, -MAGIC_SCALE);
		GlStateManager.rotate(180, 0, 1, 0);
		GlStateManager.translate(x, y, z);

		if (flip)
			GlStateManager.scale(-1, 1, 1);

		renderBakedModel(model);

		GlStateManager.popMatrix();
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
	private void renderBakedModel(BakedModel model) {
		GlStateManager.pushMatrix();
		boolean isGUI3D = model.hasDepth();
		float transformStrength = 0.25F;
		float rotation = 0.0f;
		float transform = model.getTransformation()
				.getTransformation(Mode.GROUND).scale.y;
		GlStateManager.translate(0, rotation + transformStrength * transform, 0);
		float xScale = model.getTransformation().ground.scale.x;
		float yScale = model.getTransformation().ground.scale.y;
		float zScale = model.getTransformation().ground.scale.z;

		GlStateManager.pushMatrix();

		Mode transformType = Mode.NONE;
		// ItemRenderer#render start

		model.getTransformation().apply(transformType);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);

		((MixinRenderItem) MinecraftClient.getInstance().getItemRenderer()).renderBakedModel(model, -1);

		// ItemRenderer#render end

		GlStateManager.popMatrix();
		if (!isGUI3D) {
			GlStateManager.translate(0.0F * xScale, 0.0F * yScale, 0.09375F * zScale);
		}

		GlStateManager.popMatrix();
	}

	// taken from ModelRenderer
	public void transform(ModelPart part) {
		GlStateManager.translate(part.pivotX / 16, part.pivotY / 16, part.pivotZ / 16);

		if (part.posY != 0)
			GlStateManager.rotate(part.posY * (180 / (float) Math.PI), 0, 1, 0);

		if (part.posX != 0)
			GlStateManager.rotate(part.posX * (180 / (float) Math.PI), 1, 0, 0);

		if (part.posZ != 0)
			GlStateManager.rotate(part.posZ * (180 / (float) Math.PI), 0, 0, 1);
	}

	public BakedModel createModel(Model model) {
		BlockModel blockModel = BlockModel.create(model.getModel());
		blockModel.field_10928 = model.getId();
		// TODO fix??
		// weird leftover stuff down here v
//		blockModel.getElements().forEach((part) -> {
//			part.mapFaces.forEach((key, value) -> {
//				System.out.println(Arrays.toString(value.blockFaceUV.uvs));
//			});
//		});
		((MixinBlockModel) blockModel).getTextures().put("1", "missingno");
		return ((MixinModelLoader) Utils.modelLoader).bakeBlockModel(blockModel, ModelRotation.X0_Y0, false);
	}

}
