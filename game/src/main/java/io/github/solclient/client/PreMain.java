package io.github.solclient.client;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.LWJGLUtil;

import net.minecraft.client.main.Main;
import net.minecraft.launchwrapper.Launch;

public class PreMain {

	public static void main(String[] args) {
		if (LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_LINUX) {
			preload("org.lwjgl.opengl.LinuxKeycodes");
		}
		Main.main(args);
	}

	private static void preload(String name) {
		try {
			Class.forName(name, true, Launch.classLoader);
		} catch (Exception error) {
			LogManager.getLogger().error("Could not preload " + name + ". This may cause further issues.", error);
		}
	}

}
