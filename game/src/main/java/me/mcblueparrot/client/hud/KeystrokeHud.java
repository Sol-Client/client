package me.mcblueparrot.client.hud;

import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Position;
import me.mcblueparrot.client.util.Rectangle;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.PpsMonitor;
import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.events.PlayerHeadRotateEvent;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.util.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;

public class KeystrokeHud extends Hud {

    @Expose
    @ConfigOption("Movement")
    private boolean movement = true;
    @Expose
    @ConfigOption("Mouse")
    private boolean mouse = true;
    @Expose
    @ConfigOption("Mouse Movement")
    private boolean mouseMovement = true;
    @Expose
    @ConfigOption("Space")
    private boolean showSpace;
    @Expose
    @ConfigOption("CPS")
    private boolean cps;
    @Expose
    @ConfigOption("Background")
    public boolean background = true;
    @Expose
    @ConfigOption("Background Color")
    private Colour backgroundColour = new Colour(0, 0, 0, 100);
    @Expose
    @ConfigOption("Background Color (Pressed)")
    private Colour backgroundColourPressed = new Colour(255, 255, 255, 100);
    @Expose
    @ConfigOption("Border")
    public boolean border = false;
    @Expose
    @ConfigOption("Border Color")
    private Colour borderColour = Colour.BLACK;
    @Expose
    @ConfigOption("Border Color (Pressed)")
    private Colour borderColourPressed = Colour.WHITE;
    @Expose
    @ConfigOption("Text Color")
    private Colour textColour = Colour.WHITE;
    @Expose
    @ConfigOption("Text Color (Pressed)")
    private Colour textColourPressed = Colour.BLACK;
    @Expose
    @ConfigOption("Text Shadow")
    public boolean shadow = false;
    @Expose
    @ConfigOption("Smooth Colours")
    public boolean smoothColours = true;
    private float mouseX;
    private float mouseY;
    private long lastMouseUpdate;

    private Keystroke w = new Keystroke(mc.gameSettings.keyBindForward, "W", 18, 17, 17),
                      a = new Keystroke(mc.gameSettings.keyBindLeft, "A", 0, 17, 17),
                      s = new Keystroke(mc.gameSettings.keyBindBack, "S", 18, 17, 17),
                      d = new Keystroke(mc.gameSettings.keyBindRight, "D", 36, 17, 17),
                      lmb = new Keystroke(mc.gameSettings.keyBindAttack, "LMB", 0, 26, 17),
                      rmb = new Keystroke(mc.gameSettings.keyBindUseItem, "RMB", 27, 26, 17),
                      space = new Keystroke(mc.gameSettings.keyBindJump, "Space", 0, 53, 8);

    public KeystrokeHud() {
        super("Keystrokes", "keystrokes", "Display the currently held keys.");
    }

    @Override
    public Rectangle getBounds(Position position) {
        int height = 0;
        if(movement) height += 36;
        if(mouse) height += 18;
        if(mouseMovement) height += 35;
        if(showSpace) height += 8;
        return new Rectangle(position.getX(), position.getY(), 53, height);
    }

    @EventHandler
    public void setAngles(PlayerHeadRotateEvent event) {
        mouseX += event.yaw / 40F;
        mouseY -= event.pitch / 40F;
        mouseX = MathHelper.clamp_float(mouseX, -(space.width / 2) + 4, space.width / 2 - 4);
        mouseY = MathHelper.clamp_float(mouseY, -34 / 2 + 4, 34 / 2 - 4);
    }

    @Override
    public void render(Position position, boolean editMode) {
        int heightOffset = font.FONT_HEIGHT / 2;

        int offset = 0;

        int x = position.getX();
        int y = position.getY();

        if(movement) {
            w.render(x, y);
            y += 18;
            a.render(x, y);
            s.render(x, y);
            d.render(x, y);
            y += 18;
        }
        if(mouseMovement) {
            if(background) {
                GuiScreen.drawRect(x, y, x + space.width, y + 34, backgroundColour.getValue());
            }

            if(border) {
                Utils.drawOutline(x, y, x + space.width, y + 34, borderColour.getValue());
            }

            if(shadow) Utils.drawRectangle(new Rectangle(x + space.width / 2 + 1, y + (34 / 2), 1, 2),
                    new Colour((textColour.getValue() & 16579836) >> 2 | textColour.getValue() & -16777216));
            Utils.drawRectangle(new Rectangle(x + space.width / 2, y + (34 / 2) - 1, 1, 2), textColour);

            if(shadow) Utils.drawCircle(x + space.width / 2 + mouseX + 1, y + (34 / 2) + mouseY + 1, 4,
                    (textColour.getValue() & 16579836) >> 2 | textColour.getValue() & -16777216);
            Utils.drawCircle(x + space.width / 2F + mouseX, y + (34F / 2F) + mouseY, 4, textColour.getValue());

            y += 35;
        }
        if(mouse) {
            long mouseUpdate = System.nanoTime();
            long since = mouseUpdate - lastMouseUpdate;

            if(mouseX > 0) {
                mouseX -= since / 25000000D;
                if(mouseX < 0) {
                    mouseX = 0;
                }
            }
            else if(mouseX < 0) {
                mouseX += since / 25000000D;
                if(mouseX > 0) {
                    mouseX = 0;
                }
            }

            if(mouseY > 0) {
                mouseY -= since / 25000000D;
                if(mouseY < 0) {
                    mouseY = 0;
                }
            }
            else if(mouseY < 0) {
                mouseY += since / 25000000D;
                if(mouseY > 0) {
                    mouseY = 0;
                }
            }

            lastMouseUpdate = mouseUpdate;
            lmb.render(x, y);
            rmb.render(x, y);
            y += 18;
        }
        if(showSpace) {
            space.render(x, y);
        }

    }

    public class Keystroke {

        private KeyBinding keyBind;
        private String name;
        private int x;
        private int width;
        private int height;
        private boolean wasDown;
        private long end;

        public Keystroke(KeyBinding keyBind, String name, int x, int width, int height) {
            this.keyBind = keyBind;
            this.name = name;
            this.x = x;
            this.width = width;
            this.height = height;
        }

        public void render(int offsetX, int offsetY) {
            int x = this.x + offsetX;
            int y = offsetY;
            boolean down = keyBind.isKeyDown();
            GlStateManager.enableBlend();
            if((wasDown && !down) || (!wasDown && down)) {
                end = System.currentTimeMillis();
            }
            float progress = 1F;
            if(smoothColours) {
                progress = (System.currentTimeMillis() - end) / 100.0F;
            }
            if(down) {
                progress = 1F - progress;
            }
            progress = MathHelper.clamp_float(progress, 0, 1);

            if(background) {
                GuiScreen.drawRect(x, y, x + width, y + height,
                        Utils.blendColor(backgroundColourPressed.getValue(), backgroundColour.getValue(), progress));
            }

            if(border) {
                Utils.drawOutline(x, y, x + width, y + height,
                        Utils.blendColor(borderColourPressed.getValue(), borderColour.getValue(), progress));
            }

            int fgColor = Utils.blendColor(textColourPressed.getValue(), textColour.getValue(), progress);
            String name = this.name;
            if(name.equals("Space")) {
                GuiScreen.drawRect(x + 10, y + 3, x + width - 10, y + 4, fgColor);
                if(shadow) {
                    GuiScreen.drawRect(x + 11, y + 4, x + width - 9, y + 5, (fgColor & 16579836) >> 2 | fgColor & -16777216);
                }
            }
            else {
                if(cps) {
                    PpsMonitor monitor;
                    if(name.equals("LMB")) {
                        monitor = PpsMonitor.LMB;
                    }
                    else if(name.equals("RMB")) {
                        monitor = PpsMonitor.RMB;
                    }
                    else {
                        monitor = null;
                    }
                    if(monitor != null) {
                        String cpsText = monitor.getPps() + " CPS";
                        float scale = 0.5F;

                        GlStateManager.pushMatrix();
                        GlStateManager.scale(scale, scale, scale);

                        font.drawString(cpsText,
                                (x / scale) + (width / 2F / scale) - (font.getStringWidth(cpsText) / 2F),
                                (y + height - (mc.fontRendererObj.FONT_HEIGHT * scale)) / scale - 3, fgColor, shadow);

                        GlStateManager.popMatrix();

                        y -= 3;
                    }

                }
                y += 1;
                font.drawString(name, x + (width / 2F) - (font.getStringWidth(name) / 2F), y + (height / 2F) - (font.FONT_HEIGHT / 2F), fgColor, shadow);
            }
            wasDown = down;
        }

    }

}
