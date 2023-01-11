package io.github.solclient.client.mod.impl.cosmetica;

import cc.cosmetica.api.Model;
import io.github.solclient.client.mixin.client.*;
import io.github.solclient.client.util.Utils;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.resources.model.*;

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
	public void render(ModelRenderer part, IBakedModel model, float x, float y, float z, boolean flip) {
		GlStateManager.pushMatrix();
		transform(part);
		GlStateManager.scale(MAGIC_SCALE, -MAGIC_SCALE, -MAGIC_SCALE);
		GlStateManager.rotate(180, 0, 1, 0);
		GlStateManager.translate(x, y, z);

		if (flip) {
			GlStateManager.scale(-1, 1, 1);
		}

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
	private void renderBakedModel(IBakedModel model) {
		GlStateManager.pushMatrix();
		boolean isGUI3D = model.isGui3d();
		float transformStrength = 0.25F;
		float rotation = 0.0f;
		float transform = model.getItemCameraTransforms()
				.getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
		GlStateManager.translate(0.0D, rotation + transformStrength * transform, 0.0D);
		float xScale = model.getItemCameraTransforms().ground.scale.x;
		float yScale = model.getItemCameraTransforms().ground.scale.y;
		float zScale = model.getItemCameraTransforms().ground.scale.z;

		GlStateManager.pushMatrix();

		ItemCameraTransforms.TransformType transformType = ItemCameraTransforms.TransformType.NONE;
		// ItemRenderer#render start

		model.getItemCameraTransforms().applyTransform(transformType);
		GlStateManager.translate(-0.5, -0.5, -0.5);

		((MixinRenderItem) Minecraft.getMinecraft().getRenderItem()).renderBakedModel(model, -1);

		// ItemRenderer#render end

		GlStateManager.popMatrix();
		if (!isGUI3D) {
			GlStateManager.translate(0.0F * xScale, 0.0F * yScale, 0.09375F * zScale);
		}

		GlStateManager.popMatrix();
	}

	// taken from ModelRenderer
	public void transform(ModelRenderer part) {
		GlStateManager.translate(part.rotationPointX / 16, part.rotationPointY / 16, part.rotationPointZ / 16);

		if (part.rotateAngleY != 0) {
			GlStateManager.rotate(part.rotateAngleY * (180 / (float) Math.PI), 0, 1, 0);
		}

		if (part.rotateAngleX != 0) {
			GlStateManager.rotate(part.rotateAngleX * (180 / (float) Math.PI), 1, 0, 0);
		}

		if (part.rotateAngleZ != 0) {
			GlStateManager.rotate(part.rotateAngleZ * (180 / (float) Math.PI), 0, 0, 1);
		}
	}

	public IBakedModel createModel(Model model) {
		ModelBlock blockModel = ModelBlock.deserialize(model.getModel());
		blockModel.name = model.getId();
		// TODO fix??
		// weird leftover stuff down here v
//		blockModel.getElements().forEach((part) -> {
//			part.mapFaces.forEach((key, value) -> {
//				System.out.println(Arrays.toString(value.blockFaceUV.uvs));
//			});
//		});
		((MixinModelBlock) blockModel).getTextures().put("1", "missingno");
		return ((MixinModelBakery) Utils.modelBakery).bakeBlockModel(blockModel, ModelRotation.X0_Y0, false);
	}

}
