package me.mcblueparrot.client.mixin.client;

import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public class MixinGameSettings {

    private static boolean firstLoad = true;

    @Inject(method = "loadOptions", at = @At("HEAD"))
    public void setDefaults(CallbackInfo callback) {
        useVbo = true; // Use VBOs by default.
    }

    @Inject(method = "loadOptions", at = @At("TAIL"), cancellable = true)
    public void postLoadOptions(CallbackInfo callback) {
        if(firstLoad) {
            callback.cancel();
            firstLoad = false;
        }
    }

    @Shadow
    public boolean useVbo;

}
