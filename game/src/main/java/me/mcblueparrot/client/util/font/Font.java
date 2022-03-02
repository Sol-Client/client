package me.mcblueparrot.client.util.font;

import org.lwjgl.opengl.GL11;

/* Uses methods from (https://github.com/HyperiumClient/Hyperium/blob/master/src/main/java/cc/hyperium/utils/HyperiumFontRenderer.java).
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
public interface Font {

	default void renderStringScaled(String text, int givenX, int givenY, int colour, double givenScale) {
		GL11.glPushMatrix();
		GL11.glTranslated(givenX, givenY, 0);
		GL11.glScaled(givenScale, givenScale, givenScale);
		renderString(text, 0, 0, colour);
		GL11.glPopMatrix();
	}

	int renderString(String text, float x, float y, int colour);

	int renderStringWithShadow(String text, float x, float y, int colour);

	void renderCenteredString(String text, float x, float y, int color);

	default void drawCenteredTextScaled(String text, int givenX, int givenY, int colour, double givenScale) {
		GL11.glPushMatrix();
		GL11.glTranslated(givenX, givenY, 0);
		GL11.glScaled(givenScale, givenScale, givenScale);
		renderCenteredString(text, 0, 0, colour);
		GL11.glPopMatrix();
	}

	float getWidth(String text);

	int getHeight();

}
