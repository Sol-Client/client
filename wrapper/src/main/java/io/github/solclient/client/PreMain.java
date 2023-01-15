package io.github.solclient.client;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.LWJGLUtil;

import io.github.solclient.client.launch.ClassWrapper;
import net.minecraft.client.main.Main;

public class PreMain {

	public static void main(String[] args) {
		if (LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_LINUX) {
			preload("org.lwjgl.opengl.LinuxKeycodes");
		}

		Main.main(args);
	}

	private static void preload(String name) {
		try {
			Class.forName(name, true, ClassWrapper.INSTANCE);
		} catch (Exception error) {
			LogManager.getLogger().error("Could not preload " + name + ". This may cause further issues.", error);
		}
	}

}
