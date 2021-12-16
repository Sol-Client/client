package me.mcblueparrot.client.mixin.client;

import net.minecraft.client.LoadingScreenRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingScreenRenderer.class)
public class MixinLoadingScreenRenderer {

	@Overwrite
	public void setLoadingProgress(int progress) {
	}

	@Overwrite
	private void displayString(String message) {
	}

}
