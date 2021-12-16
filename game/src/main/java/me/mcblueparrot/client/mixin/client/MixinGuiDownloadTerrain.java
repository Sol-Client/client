package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.gui.GuiDownloadTerrain;

@Mixin(GuiDownloadTerrain.class)
public class MixinGuiDownloadTerrain {

	@Overwrite
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
	}

}
