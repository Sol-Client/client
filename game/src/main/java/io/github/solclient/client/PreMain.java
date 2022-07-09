package io.github.solclient.client;

import org.lwjgl.LWJGLUtil;

import io.github.solclient.client.util.Utils;
import net.minecraft.client.main.Main;

public class PreMain {

	public static void main(String[] args) {
		if(LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_LINUX) {
			Utils.earlyLoad("org.lwjgl.opengl.LinuxKeycodes");
		}
		Main.main(args);
	}

}
