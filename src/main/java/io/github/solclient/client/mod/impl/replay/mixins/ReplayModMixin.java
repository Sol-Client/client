package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.*;

import com.replaymod.core.*;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import net.minecraft.client.MinecraftClient;

@Mixin(ReplayMod.class)
public class ReplayModMixin {

	/**
	 * @author TheKodeToad
	 * @reason Overwrites are not always a crime.
	 */
	@Overwrite(remap = false)
	public void registerKeyBindings(KeyBindingRegistry registry) {
		registry.registerKeyBinding("replaymod.input.settings", 0,
				() -> mc.setScreen(new ModsScreen(SCReplayMod.instance)), false);
	}

	@Final
	@Shadow
	private static MinecraftClient mc;

}
