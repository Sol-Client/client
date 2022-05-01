package me.mcblueparrot.client.mod.impl.hud.keystrokes;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.CpsMonitor;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PlayerHeadRotateEvent;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.hud.HudMod;
import me.mcblueparrot.client.mod.hud.SimpleHudMod;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;

public class KeystrokesMod extends HudMod {

	@Expose
	@Option
	private boolean movement = true;
	@Expose
	@Option
	private boolean mouse = true;
	@Expose
	@Option
	private boolean mouseMovement;
	@Expose
	@Option
	private boolean showSpace;
	@Expose
	@Option
	protected boolean cps;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	protected boolean background = true;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	protected Colour backgroundColour = new Colour(0, 0, 0, 100);
	@Expose
	@Option
	protected Colour backgroundColourPressed = new Colour(255, 255, 255, 100);
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	protected boolean border = false;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	protected Colour borderColour = Colour.BLACK;
	@Expose
	@Option
	protected Colour borderColourPressed = Colour.WHITE;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	protected Colour textColour = Colour.WHITE;
	@Expose
	@Option
	protected Colour textColourPressed = Colour.BLACK;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	protected boolean shadow = false;
	@Expose
	@Option
	protected boolean smoothColours = true;

	private float mouseX;
	private float mouseY;
	private long lastMouseUpdate;

	private Keystroke w, a, s, d, lmb, rmb, space;

	@Override
	public String getId() {
		return "keystrokes";
	}

	@Override
	public void postStart() {
		super.postStart();
		w = new Keystroke(this, mc.gameSettings.keyBindForward, "W", 18, 17, 17);
		a = new Keystroke(this, mc.gameSettings.keyBindLeft, "A", 0, 17, 17);
		s = new Keystroke(this, mc.gameSettings.keyBindBack, "S", 18, 17, 17);
		d = new Keystroke(this, mc.gameSettings.keyBindRight, "D", 36, 17, 17);
		lmb = new Keystroke(this, mc.gameSettings.keyBindAttack, "LMB", 0, 26, 17);
		rmb = new Keystroke(this, mc.gameSettings.keyBindUseItem, "RMB", 27, 26, 17);
		space = new Keystroke(this, mc.gameSettings.keyBindJump, "Space", 0, 53, 8);
	}

	@Override
	public Rectangle getBounds(Position position) {
		int height = 0;

		if(movement) height += 36;
		if(mouse) height += 18;
		if(mouseMovement) height += 36;
		if(showSpace) height += 9;

		height--;

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
					textColour.getShadowValue());

			Utils.drawCircle(x + space.width / 2F + mouseX, y + (34F / 2F) + mouseY, 4, textColour.getValue());

			y += 35;
		}

		if(mouse) {
			lmb.render(x, y);
			rmb.render(x, y);
			y += 18;
		}

		if(showSpace) {
			space.render(x, y);
		}
	}

}
