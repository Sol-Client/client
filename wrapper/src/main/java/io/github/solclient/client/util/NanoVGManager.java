package io.github.solclient.client.util;

import java.io.IOException;

import org.lwjgl.nanovg.NanoVGGL3;

import lombok.Getter;

public class NanoVGManager {

	@Getter
	protected static long nvg;
	@Getter
	protected static Font regularFont;

	public static void createContext() throws IOException {
		nvg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS);
		regularFont = new Font(nvg, NanoVGManager.class.getResourceAsStream("/fonts/Inter-Regular.ttf"));
	}

	public static void closeContext() {
		regularFont.close();
	}

}
