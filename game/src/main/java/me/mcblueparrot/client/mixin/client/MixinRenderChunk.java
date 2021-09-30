package me.mcblueparrot.client.mixin.client;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.RenderChunkPositionEvent;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderChunk.class)
public class MixinRenderChunk {

    @Inject(method = "setPosition", at = @At("RETURN"))
    public void setPosition(BlockPos pos, CallbackInfo callback) {
        Client.INSTANCE.bus.post(new RenderChunkPositionEvent((RenderChunk) (Object) this, pos));
    }

}
