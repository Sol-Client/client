package io.github.solclient.client;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.LWJGLUtil;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.wrapper.ClassWrapper;
import io.github.solclient.wrapper.transformer.AccessWidenerTransformer;
import net.minecraft.client.main.Main;

public class Premain {

	public static void main(String[] args) throws IOException {
		Mixins.addConfiguration("mixins.solclient.json");
		AccessWidenerTransformer.addWideners("sol-client.accesswidener");

		Main.main(args);
	}

	private static void preload(String name) {
		try {
			Class.forName(name, true, ClassWrapper.getInstance());
		} catch (Exception error) {
			LogManager.getLogger().error("Could not preload " + name + ". This may cause further issues.", error);
		}
	}

}
