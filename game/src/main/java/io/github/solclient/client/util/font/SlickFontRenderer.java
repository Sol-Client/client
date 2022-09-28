/*
 * TODO: Digital Lego with HD font?
 * TODO: With an additional library that hasn't been maintained in years?
 * TODO: "Borrowing" a large amount of code from Hyperium?
 *
 * Modified from original (https://github.com/HyperiumClient/Hyperium/blob/master/src/main/java/cc/hyperium/utils/HyperiumFontRenderer.java).
 * Depends on Slick2D (http://slick.ninjacave.com/).
 *
 *
 *       Copyright (C) 2018-present Hyperium <https://hyperium.cc/>
 *
 *       This program is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published
 *       by the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       This program is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.util.font;

import java.awt.FontFormatException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.font.effects.Effect;

import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;

public class SlickFontRenderer implements Font {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§[0123456789abcdefklmnor]");
	private final int[] colorCodes = { 0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
			0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF };
	private float scaleFactor;
	private UnicodeFont slickFont;
	private int prevScaleFactor;
	private String name;
	private float size;

	public static final SlickFontRenderer DEFAULT = new SlickFontRenderer("/Roboto-Regular.ttf", 16);

	public SlickFontRenderer(String path, float fontSize) {
		name = path;
		size = fontSize;
		loadFont();
	}

	private java.awt.Font getFontFromInput(String path) throws IOException, FontFormatException {
		return java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, SlickFontRenderer.class.getResourceAsStream(path));
	}

	@Override
	public int renderString(String text, float x, float y, int colour) {
		if(text == null || slickFont == null) {
			return 0;
		}

		x = (int) x;
		y = (int) y;

		loadFont();

		GL11.glPushMatrix();
		GlStateManager.scale(1 / scaleFactor, 1 / scaleFactor, 1 / scaleFactor);
		x *= scaleFactor;
		y *= scaleFactor;
		float originalX = x;
		float red = (colour >> 16 & 255) / 255.0F;
		float green = (colour >> 8 & 255) / 255.0F;
		float blue = (colour & 255) / 255.0F;
		float alpha = (colour >> 24 & 255) / 255.0F;
		GlStateManager.color(red, green, blue, alpha);

		int currentColour = colour;

		char[] characters = text.toCharArray();

		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		String[] parts = COLOR_CODE_PATTERN.split(text);
		int index = 0;
		for(String s : parts) {
			for(String s2 : s.split("\n")) {
				for(String s3 : s2.split("\r")) {

					slickFont.drawString(x, y, s3, new org.newdawn.slick.Color(currentColour));
					x += slickFont.getWidth(s3);

					index += s3.length();
					if(index < characters.length && characters[index] == '\r') {
						x = originalX;
						index++;
					}
				}
				if(index < characters.length && characters[index] == '\n') {
					x = originalX;
					y += getHeight(s2) * 2;
					index++;
				}
			}
			if(index < characters.length) {
				char colorCode = characters[index];
				if(colorCode == '§') {
					char colorChar = characters[index + 1];
					int codeIndex = ("0123456789" + "abcdef").indexOf(colorChar);
					if(codeIndex < 0) {
						if(colorChar == 'r') {
							currentColour = colour;
						}
					}
					else {
						currentColour = colorCodes[codeIndex];
					}
					index += 2;
				}
			}
		}

		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.bindTexture(0);
		GlStateManager.popMatrix();
		return (int) x;
	}

	@SuppressWarnings("unchecked")
	private void loadFont() {
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		scaleFactor = resolution.getScaleFactor();

		if(scaleFactor != prevScaleFactor) {
			if(slickFont != null) {
				free();
			}

			try {
				prevScaleFactor = resolution.getScaleFactor();
				slickFont = new UnicodeFont(getFontFromInput(name).deriveFont(size * prevScaleFactor / 2));
				slickFont.addAsciiGlyphs();
				slickFont.addNeheGlyphs();

				// Cyrillic
				slickFont.addGlyphs(0x0400, 0x04FF);

				// Vietnamese
				slickFont.addGlyphs(0x0041, 0x005A);
				slickFont.addGlyphs(0x0061, 0x007A);``
				slickFont.addGlyphs(0x00C0, 0x00C3);
				slickFont.addGlyphs(0x00C8, 0x00CA);
				slickFont.addGlyphs(0x00CC, 0x00CD);
				slickFont.addGlyphs(0x00D2, 0x00D5);
				slickFont.addGlyphs(0x00D9, 0x00DA);
				slickFont.addGlyphs(0x00DD, 0x00DD);
				slickFont.addGlyphs(0x00E0, 0x00E3);
				slickFont.addGlyphs(0x00E8, 0x00EA);
				slickFont.addGlyphs(0x00EC, 0x00ED);
				slickFont.addGlyphs(0x00F2, 0x00F5);
				slickFont.addGlyphs(0x00F9, 0x00FA);
				slickFont.addGlyphs(0x00FD, 0x00FD);
				slickFont.addGlyphs(0x0102, 0x0103);
				slickFont.addGlyphs(0x0110, 0x0111);
				slickFont.addGlyphs(0x0128, 0x0129);
				slickFont.addGlyphs(0x0168, 0x0169);
				slickFont.addGlyphs(0x01A0, 0x01A1);
				slickFont.addGlyphs(0x01AF, 0x01B0);
				slickFont.addGlyphs(0x1EA0, 0x1EF9);

				((List<Effect>) slickFont.getEffects()).add(new ColorEffect(Colour.WHITE.toAWT()));
				slickFont.loadGlyphs();
			}
			catch(FontFormatException | IOException | SlickException error) {
				// fallback to vanilla font
				slickFont = null;
				SolClientMod.instance.fancyFont = false;
				LOGGER.error("Could not load font", error);
			}
		}
	}

	@Override
	public int renderStringWithShadow(String text, float x, float y, int color) {
		renderString(StringUtils.stripControlCodes(text), x + 0.5F, y + 0.5F, 0x000000);
		return renderString(text, x, y, color);
	}

	@Override
	public void renderCenteredString(String text, float x, float y, int color) {
		renderString(text, x - ((int) getWidth(text) >> 1), y, color);
	}

	public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
		renderCenteredString(StringUtils.stripControlCodes(text), x + 0.5F, y + 0.5F, color);
		renderCenteredString(text, x, y, color);
	}

	public float getAscent() {
		if(slickFont == null) return 0;
		return slickFont.getAscent();
	}

	@Override
	public float getWidth(String text) {
		if(slickFont == null) return 0;
		return slickFont.getWidth(EnumChatFormatting.getTextWithoutFormattingCodes(text)) / scaleFactor;
	}

	public float getCharWidth(char c) {
		if(slickFont == null) return 0;
		return slickFont.getWidth(String.valueOf(c));
	}

	public float getHeight(String s) {
		if(slickFont == null) return 0;
		return slickFont.getHeight(s) / 2.0F;
	}

	public void drawSplitString(ArrayList<String> lines, int x, int y, int color) {
		renderString(String.join("\n\r", lines), x, y, color);
	}

	public List<String> splitString(String text, int wrapWidth) {
		List<String> lines = new ArrayList<>();

		String[] splitText = text.split(" ");
		StringBuilder currentString = new StringBuilder();

		for(String word : splitText) {
			String potential = currentString + " " + word;

			if(getWidth(potential) >= wrapWidth) {
				lines.add(currentString.toString());
				currentString = new StringBuilder();
			}

			currentString.append(word).append(" ");
		}

		lines.add(currentString.toString());
		return lines;
	}

	@Override
	public int getHeight() {
		return 11;
	}

	public void free() {
		slickFont.destroy();
	}

}
