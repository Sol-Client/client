package io.github.solclient.client;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixins;

import io.github.solclient.wrapper.transformer.AccessWidenerTransformer;
import net.minecraft.client.main.Main;

/**
 * Used to add some mixin and access wideners.
 */
public final class Premain {

	public static void main(String[] args) throws IOException {
		Mixins.addConfiguration("sol-client.mixins.json");
		AccessWidenerTransformer.addWideners("sol-client.accesswidener");

		Main.main(args);
	}

}
