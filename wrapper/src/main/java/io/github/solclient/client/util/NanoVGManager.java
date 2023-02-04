package io.github.solclient.client.util;

import java.io.IOException;

import org.lwjgl.nanovg.*;

import lombok.Getter;

public class NanoVGManager {

	@Getter
	protected static long nvg;
	@Getter
	protected static Font regularFont;

	public static void createContext() throws IOException {
		nvg = NanoVGGL2.nvgCreate(NanoVGGL2.NVG_ANTIALIAS);
		if (nvg == 0)
			throw new IllegalStateException("NanoVG could not be initialised");
		regularFont = new Font(nvg, NanoVGManager.class.getResourceAsStream("/fonts/Inter-Regular.ttf"));
	}

	public static void closeContext() {
		regularFont.close();
	}

}
