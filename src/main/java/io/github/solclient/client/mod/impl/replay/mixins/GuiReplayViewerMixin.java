package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiScreen;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiButton;
import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import io.github.solclient.client.ui.screen.JGuiPreviousScreen;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import net.minecraft.client.MinecraftClient;

@Mixin(GuiReplayViewer.class)
public class GuiReplayViewerMixin extends GuiScreen {

	@Inject(method = "<init>", at = @At("RETURN"), remap = false)
	public void overrideSettings(ReplayModReplay mod, CallbackInfo callback) {
		MinecraftClient.getInstance().currentScreen = new JGuiPreviousScreen(this);
		settingsButton.onClick(() -> MinecraftClient.getInstance().setScreen(new ModsScreen(SCReplayMod.instance)));
	}

	@Override
	public void display() {
		if (!SCReplayMod.enabled)
			MinecraftClient.getInstance().setScreen(null);
		else
			super.display();
	}

	@Shadow
	public @Final GuiButton settingsButton;

}
