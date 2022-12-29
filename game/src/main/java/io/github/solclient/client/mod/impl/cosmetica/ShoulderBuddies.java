package io.github.solclient.client.mod.impl.cosmetica;

import cc.cosmetica.api.Model;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.ResourceLocation;

public final class ShoulderBuddies extends CosmeticLayer {

	public ShoulderBuddies(RenderPlayer parent) {
		super(parent);
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer player, float p_177141_2_, float p_177141_3_,
			float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if(player.isInvisible()) {
			return;
		}

		CosmeticaMod.instance.getShoulderBuddies(player).ifPresent((buddies) -> {
			if(buddies.getLeft().isPresent()) {
				render(buddies.getLeft().get(), false, player, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
			}

			if(buddies.getRight().isPresent()) {
				render(buddies.getRight().get(), true, player, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
			}
		});
	}

	private void render(Model buddy, boolean right, AbstractClientPlayer player, float p_177141_2_, float p_177141_3_,
			float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if(!buddy.getId().equals("-sheep")) {
			// work this out later
			return;
		}

		GlStateManager.pushMatrix();

		Minecraft.getMinecraft().getTextureManager().bindTexture(Texture.load(0, 0, buddy.getTexture()));

		boolean staticPosition = (buddy.flags() & Model.LOCK_SHOULDER_BUDDY_ORIENTATION) > 0;
		boolean flip = right && (buddy.flags() & Model.DONT_MIRROR_SHOULDER_BUDDY) == 0;
		IBakedModel model = CosmeticaMod.instance.bakeIfAbsent(buddy);

		if(staticPosition) {
			GlStateManager.translate(right ? -0.375 : 0.375, player.isSneaking() ? 0.1 : -0.15, player.isSneaking() ? -0.16 : 0);
			Util.render(parent.getMainModel().bipedBody, model, 0, 0.044f, 0, flip);
		}
		else {
			Util.render(right ? parent.getMainModel().bipedRightArm : parent.getMainModel().bipedLeftArm, model, 0,
					0.37f, 0, flip);
		}

		GlStateManager.popMatrix();
	}

}
