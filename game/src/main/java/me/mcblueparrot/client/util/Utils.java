package me.mcblueparrot.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

import lombok.experimental.UtilityClass;
import me.mcblueparrot.client.mod.ArabicNumeralsMod;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.StatCollector;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class Utils {

    public Comparator<String> STRING_WIDTH_COMPARATOR = Comparator.comparingInt(Utils::getStringWidth);

    private static int getStringWidth(String text) {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
    }

    public JsonObject getGraph(URL url, String query) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        JsonObject object = new JsonObject();
        object.addProperty("query", query);

        OutputStream out = connection.getOutputStream();
        out.write(object.toString().getBytes());

        InputStream in;
        try {
            in = connection.getInputStream();
        }
        catch(IOException error) {
            in = connection.getErrorStream();
        }
        String result = IOUtils.toString(in, StandardCharsets.UTF_8);

        out.close();
        in.close();

        return new JsonParser().parse(result).getAsJsonObject();
    }

    public void glColour(Colour color) {
        GL11.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) color.getAlpha());
    }

    private void drawHorizontalLine(int startX, int endX, int y, int colour) {
        if(endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        GuiScreen.drawRect(startX, y, endX + 1, y + 1, colour);
    }

    private void drawVerticalLine(int x, int startY, int endY, int colour) {
        if(endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }

        GuiScreen.drawRect(x, startY + 1, x + 1, endY, colour);
    }

    public void drawOutline(Rectangle rectangle, Colour colour) {
        drawOutline(rectangle.getX(), rectangle.getY(), rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight(),
                colour.getValue());
    }

    public void drawOutline(int left, int top, int right, int bottom, int colour) {
        drawHorizontalLine(left, right - 1, top, colour);
        drawHorizontalLine(left, right - 1, bottom -1, colour);
        drawVerticalLine(left, top, bottom - 1, colour);
        drawVerticalLine(right - 1, top, bottom - 1, colour);
    }

    public void drawRectangle(Rectangle rectangle, Colour colour) {
        GuiScreen.drawRect(rectangle.getX(), rectangle.getY(), rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight(),
                colour.getValue());
    }

    public long toMegabytes(long bytes) {
        return bytes / 1024L / 1024L;
    }

    public int blendColor(int start, int end, float percent) {
        if(percent >= 1) {
            return end;
        }
        Colour startColor = new Colour(start);
        Colour endColor = new Colour(end);
        return new Colour(
                blendInt(startColor.getRed(), endColor.getRed(), percent),
                blendInt(startColor.getGreen(), endColor.getGreen(), percent),
                blendInt(startColor.getBlue(), endColor.getBlue(), percent),
                blendInt(startColor.getAlpha(), endColor.getAlpha(), percent)
        ).getValue();
    }

    public int blendInt(int start, int end, float percent) {
        return Math.round(start + (((float) (end - start)) * percent));
    }

    public void scissor(Rectangle rectangle) {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        double scale = resolution.getScaleFactor();
        GL11.glScissor((int) (rectangle.getX() * scale), (int) ((resolution.getScaledHeight() - rectangle.getHeight() - rectangle.getY()) * scale), (int) (rectangle.getWidth() * scale), (int) (rectangle.getHeight() * scale));
    }

    public void playClickSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public URL checkedParse(String url) {
        try {
            return new URL(url);
        }
        catch(MalformedURLException error) {
            throw new IllegalStateException(error);
        }
    }

    /*
     * Single following method:
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
    public void drawCircle(float xx, float yy, int radius, int col) {
        float f = (col >> 24 & 0xFF) / 255.0F;
        float f2 = (col >> 16 & 0xFF) / 255.0F;
        float f3 = (col >> 8 & 0xFF) / 255.0F;
        float f4 = (col & 0xFF) / 255.0F;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(2);
        GL11.glBegin(2);

        for (int i = 0; i < 70; i++) {
            float x = radius * MathHelper.cos((float) (i * 0.08975979010256552D));
            float y = radius * MathHelper.sin((float) (i * 0.08975979010256552D));
            GlStateManager.color(f2, f3, f4, f);
            GL11.glVertex2f(xx + x, yy + y);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
    }

    public GuiChat getChatGui() {
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        if(currentScreen != null && currentScreen instanceof GuiChat) {
            return (GuiChat) currentScreen;
        }
        return null;
    }

    public static void drawTexture(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
        float xMultiplier = 0.00390625F;
        float yMultiplier = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, zLevel).tex(((float) (textureX) * xMultiplier),
                (double)((float)(textureY + height) * yMultiplier)).endVertex();
        worldrenderer.pos(x + width, (double)(y + height), (double) zLevel).tex((double)((float)(textureX + width) * xMultiplier), (double)((float)(textureY + height) * yMultiplier)).endVertex();
        worldrenderer.pos(x + width, (double)(y + 0), (double) zLevel).tex((double)((float)(textureX + width) * xMultiplier), (double)((float)(textureY + 0) * yMultiplier)).endVertex();
        worldrenderer.pos(x, y, zLevel).tex(((float) (textureX) * xMultiplier),
                ((float) (textureY + 0) * yMultiplier)).endVertex();
        tessellator.draw();
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColour, int endColour) {
        float alpha1 = (float)(startColour >> 24 & 255) / 255.0F;
        float red1 = (float)(startColour >> 16 & 255) / 255.0F;
        float green1 = (float)(startColour >> 8 & 255) / 255.0F;
        float blue1 = (float)(startColour & 255) / 255.0F;
        float alpha2 = (float)(endColour >> 24 & 255) / 255.0F;
        float red2 = (float)(endColour >> 16 & 255) / 255.0F;
        float green2 = (float)(endColour >> 8 & 255) / 255.0F;
        float blue2 = (float)(endColour & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)right, (double)top, 0).color(red1, green1, blue1, alpha1).endVertex();
        worldrenderer.pos((double)left, (double)top, 0).color(red1, green1, blue1, alpha1).endVertex();
        worldrenderer.pos((double)left, (double)bottom, 0).color(red2, green2, blue2, alpha2).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0).color(red2, green2, blue2, alpha2).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

}
