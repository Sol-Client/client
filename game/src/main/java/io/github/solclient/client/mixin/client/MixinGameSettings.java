package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.settings.GameSettings;

@Mixin(GameSettings.class)
public class MixinGameSettings {

	private static boolean firstLoad = true;

	@Inject(method = "loadOptions", at = @At("HEAD"))
	public void setDefaults(CallbackInfo callback) {
		useVbo = true; // Use VBOs by default.
	}

	@Inject(method = "loadOptions", at = @At("TAIL"), cancellable = true)
	public void postLoadOptions(CallbackInfo callback) {
		if (firstLoad) {
			callback.cancel();
			firstLoad = false;
		}
	}

	@Shadow
	public boolean useVbo;

}
