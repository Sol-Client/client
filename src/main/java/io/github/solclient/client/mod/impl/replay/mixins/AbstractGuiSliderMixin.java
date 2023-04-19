package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import com.replaymod.lib.de.johni0702.minecraft.gui.GuiRenderer;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.AbstractGuiSlider;

@Mixin(AbstractGuiSlider.class)
public class AbstractGuiSliderMixin {

	@Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lcom/replaymod/lib/de/johni0702/minecraft/gui/GuiRenderer;drawCenteredString(IIILjava/lang/String;)I"), remap = false)
	public int useShadow(GuiRenderer instance, int x, int y, int colour, String text) {
		return instance.drawCenteredString(x, y, colour, text, true);
	}

}
