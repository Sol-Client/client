package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Cullable;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRenderDispatcher {

	@Inject(method = "renderTileEntity", at = @At("HEAD"), cancellable = true)
	public void cullTileEntity(TileEntity tileentityIn, float partialTicks, int destroyStage, CallbackInfo callback) {
		if(((Cullable) tileentityIn).isCulled()) {
			callback.cancel();
		}
	}

}
