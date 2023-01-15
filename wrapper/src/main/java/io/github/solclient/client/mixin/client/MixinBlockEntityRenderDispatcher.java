package io.github.solclient.client.mixin.client;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.culling.Cullable;

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {

	@Inject(method = "renderEntity(Lnet/minecraft/block/entity/BlockEntity;FI)V", at = @At("HEAD"), cancellable = true)
	public void cullTileEntity(BlockEntity blockEntity, float tickDelta, int destroyProgress, CallbackInfo callback) {
		if (((Cullable) blockEntity).isCulled()) {
			callback.cancel();
		}
	}

}
