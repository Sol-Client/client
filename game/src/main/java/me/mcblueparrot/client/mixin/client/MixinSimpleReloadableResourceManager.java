package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mcblueparrot.client.Client;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

@Mixin(SimpleReloadableResourceManager.class)
public class MixinSimpleReloadableResourceManager {

    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    public void getResource(ResourceLocation location, CallbackInfoReturnable<IResource> callback) {
        if(Client.INSTANCE.getResource(location) != null) {
            callback.setReturnValue(Client.INSTANCE.getResource(location));
        }
    }

}
