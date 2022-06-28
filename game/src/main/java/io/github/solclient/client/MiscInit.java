package io.github.solclient.client;

import java.io.File;

import io.github.solclient.client.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;

public class MiscInit {

	public static void init() {
		if(Util.getOSType() == EnumOS.LINUX) {
			try {
				Class.forName("org.lwjgl.opengl.LinuxKeycodes");
			}
			catch(ClassNotFoundException error) {
				error.printStackTrace();
			}
		}

		Utils.resetLineWidth();
		new File(Minecraft.getMinecraft().mcDataDir, "server-resource-packs").mkdirs(); // Fix crash

		System.setProperty("http.agent", "Sol Client/" + Client.VERSION);
	}

}
