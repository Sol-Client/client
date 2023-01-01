package io.github.solclient.client.tweak;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.*;

import lombok.SneakyThrows;
import net.minecraft.launchwrapper.*;

public class Tweaker implements ITweaker {

	public static boolean optiFine;
	private List<String> args = new ArrayList<>();
	private LaunchClassLoader classLoader;
	private Set<String> exceptions;

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
		try {
			Class.forName("optifine.Patcher");
			optiFine = true;
		} catch (ClassNotFoundException ignored) {
		}

		this.args.addAll(args);

		if (gameDir != null) {
			this.args.add("--gameDir");
			this.args.add(gameDir.getAbsolutePath());
		}

		if (assetsDir != null) {
			this.args.add("--assetsDir");
			this.args.add(assetsDir.getAbsolutePath());
		}

		if (profile != null) {
			this.args.add("--version");
			this.args.add(profile);
		}
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		this.classLoader = classLoader;

		classLoader.registerTransformer("io.github.solclient.client.tweak.transformer.ClassTransformer");

		MixinBootstrap.init();

		Mixins.addConfiguration("mixins.solclient.json");

		// Replay Mod
		Mixins.addConfiguration("mixins.core.replaymod.json");
		Mixins.addConfiguration("mixins.recording.replaymod.json");
		Mixins.addConfiguration("mixins.render.replaymod.json");
		Mixins.addConfiguration("mixins.render.blend.replaymod.json");
		Mixins.addConfiguration("mixins.replay.replaymod.json");
		if (optiFine)
			Mixins.addConfiguration("mixins.compat.shaders.replaymod.json");
		Mixins.addConfiguration("mixins.extras.playeroverview.replaymod.json");

		MixinEnvironment env = MixinEnvironment.getDefaultEnvironment();

		env.setObfuscationContext("searge");
		env.setSide(MixinEnvironment.Side.CLIENT);

		removeClassLoaderException("org.lwjgl.");
	}

	@SneakyThrows
	private void removeClassLoaderException(String exception) {
		if (exceptions == null) {
			getExceptions();
		}

		exceptions.remove(exception);
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	private void getExceptions() {
		Field exceptionsField = classLoader.getClass().getDeclaredField("classLoaderExceptions");
		exceptionsField.setAccessible(true);
		exceptions = (Set<String>) exceptionsField.get(classLoader);
	}

	@Override
	public String getLaunchTarget() {
		return "io.github.solclient.client.PreMain";
	}

	@Override
	public String[] getLaunchArguments() {
		return args.toArray(new String[0]);
	}

}
