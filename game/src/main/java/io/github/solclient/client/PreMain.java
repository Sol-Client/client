package io.github.solclient.client;

import org.lwjgl.LWJGLUtil;

import io.github.solclient.client.util.Utils;
import net.minecraft.client.main.Main;

public class PreMain {

	public static void main(String[] args) {
		if(LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_LINUX) {
			preload("org.lwjgl.opengl.LinuxKeycodes");
		}
		Main.main(args);
	}

	private static void preload(String name) {
		try {
			Class.forName(name, true, Launch.classLoader);
		}
		catch(Exception error) {
			Client.LOGGER.error("Could not preload " + name + ". This may cause further issues.");
		}
	}

}
