package io.github.solclient.client;

import java.io.File;

import org.lwjgl.LWJGLUtil;

import io.github.solclient.client.util.Utils;
import net.minecraft.client.Minecraft;

public class MiscInit {

	public static void init() {
		if(LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_LINUX) {
			Utils.earlyLoad("org.lwjgl.opengl.LinuxKeycodes");
		}

		Utils.resetLineWidth();
		new File(Minecraft.getMinecraft().mcDataDir, "server-resource-packs").mkdirs(); // Fix crash

		System.setProperty("http.agent", "Sol Client/" + Client.VERSION);
	}

}
