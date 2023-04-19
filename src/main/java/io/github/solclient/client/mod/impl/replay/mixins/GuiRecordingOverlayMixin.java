package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.lib.de.johni0702.minecraft.gui.versions.MatrixStack;
import com.replaymod.recording.gui.*;

import io.github.solclient.client.mod.impl.replay.RecordingIndicator;

@Mixin(GuiRecordingOverlay.class)
public class GuiRecordingOverlayMixin {

	@Inject(method = "<init>", at = @At("RETURN"))
	public void postInit(CallbackInfo callback) {
		RecordingIndicator.guiControls = guiControls;
	}

	/**
	 * @author TheKodeToad
	 * @reason we do this anyway
	 */
	@Overwrite(remap = false)
	private void renderRecordingIndicator(MatrixStack stack) {
		// Overwritten by the HUD.
	}

	@Shadow
	private @Final GuiRecordingControls guiControls;

}
