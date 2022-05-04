package me.mcblueparrot.client.util;

import java.io.IOException;

import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;

import lombok.experimental.UtilityClass;
import me.mcblueparrot.client.util.data.Colour;
import net.minecraft.client.Minecraft;

@UtilityClass
public class VGManager {

	public final long MAIN = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS);
	public final NVGColor MAIN_COLOUR = NVGColor.create();
	public final String INTER = "Inter-Regular";

	static {
		try {
			loadFont(INTER);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void loadFont(String name) throws IOException {
		NanoVG.nvgCreateFontMem(MAIN, name, Utils.resourceToByteBuffer("/" + name + ".ttf"), 0);
	}

	public NVGColor mainColour(Colour colour) {
		colour.toNanoVG(MAIN_COLOUR);
		return MAIN_COLOUR;
	}

	public NVGColor mainColour(int r, int g, int b, int a) {
		NanoVG.nvgRGBA((byte) r, (byte) g, (byte) b, (byte) a, MAIN_COLOUR);
		return MAIN_COLOUR;
	}

	public void init() {
	}

}
