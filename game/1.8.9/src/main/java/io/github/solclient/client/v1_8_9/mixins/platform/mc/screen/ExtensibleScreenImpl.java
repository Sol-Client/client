package io.github.solclient.client.v1_8_9.mixins.platform.mc.screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.screen.ProxyScreen;
import net.minecraft.client.gui.screen.Screen;

@Mixin(ProxyScreen.class)
public abstract class ExtensibleScreenImpl extends Screen {

	@Overwrite
	public void renderScreen(int mouseX, int mouseY, float tickDelta) {
		super.render(mouseX, mouseY, tickDelta);
	}

	@Shadow(prefix = "platform$")
	public abstract void platform$renderScreen(int mouseX, int mouseY, float tickDelta);

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		renderScreen(mouseX, mouseY, tickDelta);
	}

}
