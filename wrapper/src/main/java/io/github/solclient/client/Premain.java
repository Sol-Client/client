package io.github.solclient.client;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixins;

import io.github.solclient.util.GlobalConstants;
import io.github.solclient.wrapper.transformer.AccessWidenerTransformer;
import net.minecraft.client.main.Main;

/**
 * Used to add some mixin and access wideners.
 */
public final class Premain {

	public static void main(String[] args) throws IOException {
		Mixins.addConfiguration("sol-client.mixins.json");

		Mixins.addConfiguration("mixins.core.replaymod.json");
		Mixins.addConfiguration("mixins.recording.replaymod.json");
		Mixins.addConfiguration("mixins.render.replaymod.json");
		Mixins.addConfiguration("mixins.render.blend.replaymod.json");
		Mixins.addConfiguration("mixins.replay.replaymod.json");
		if (GlobalConstants.OPTIFINE)
			Mixins.addConfiguration("mixins.compat.shaders.replaymod.json");
		Mixins.addConfiguration("mixins.extras.playeroverview.replaymod.json");

		AccessWidenerTransformer.addWideners("sol-client.accesswidener");

		Main.main(args);
	}

}
