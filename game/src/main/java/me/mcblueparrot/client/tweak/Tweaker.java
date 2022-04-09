package me.mcblueparrot.client.tweak;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.mcblueparrot.client.extension.ExtensionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class Tweaker implements ITweaker {

	public static boolean optiFine;
	private static List<String> args = new ArrayList<>();

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
		try {
			Class.forName("optifine.Patcher");
			optiFine = true;
		}
		catch(ClassNotFoundException ignored) {
		}

		Tweaker.args.addAll(args);
		if(gameDir != null) {
			Tweaker.args.add("--gameDir");
			Tweaker.args.add(gameDir.getAbsolutePath());
		}
		if(assetsDir != null) {
			Tweaker.args.add("--assetsDir");
			Tweaker.args.add(assetsDir.getAbsolutePath());
		}
		if(profile != null) {
			Tweaker.args.add("--version");
			Tweaker.args.add(profile);
		}
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		classLoader.registerTransformer("me.mcblueparrot.client.tweak.transformer.ClassTransformer");

		MixinBootstrap.init();

		Mixins.addConfiguration("mixins.solclient.json");

		// Replay Mod
		Mixins.addConfiguration("mixins.core.replaymod.json");
		Mixins.addConfiguration("mixins.recording.replaymod.json");
		Mixins.addConfiguration("mixins.render.replaymod.json");
		Mixins.addConfiguration("mixins.render.blend.replaymod.json");
		Mixins.addConfiguration("mixins.replay.replaymod.json");
		if(optiFine) Mixins.addConfiguration("mixins.compat.shaders.replaymod.json");
		Mixins.addConfiguration("mixins.extras.playeroverview.replaymod.json");

		try {
			Class<?> extensionClass = classLoader.findClass("me.mcblueparrot.client.extension.ExtensionManager");
			Method initMethod = extensionClass.getDeclaredMethod("init");
			initMethod.setAccessible(true);
			initMethod.invoke(null);
		}
		catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException error) {
			throw new Error(error);
		}
		catch(InvocationTargetException error) {
			if(error.getCause() instanceof RuntimeException) {
				throw (RuntimeException) error.getCause();
			}
			else if(error.getCause() instanceof Error) {
				throw (Error) error.getCause();
			}

			throw new Error(error.getCause());
		}

		MixinEnvironment env = MixinEnvironment.getDefaultEnvironment();

		if(env.getObfuscationContext() == null) {
			env.setObfuscationContext("searge");
		}

		env.setSide(MixinEnvironment.Side.CLIENT);
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.client.main.Main";
	}

	@Override
	public String[] getLaunchArguments() {
		return args.toArray(new String[0]);
	}

}
