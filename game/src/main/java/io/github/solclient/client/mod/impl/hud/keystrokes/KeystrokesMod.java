package io.github.solclient.client.mod.impl.hud.keystrokes;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PreTickEvent;
import io.github.solclient.client.event.impl.input.CameraRotateEvent;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.platform.mc.*;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;

public class KeystrokesMod extends HudMod {

	public static final KeystrokesMod INSTANCE = new KeystrokesMod();

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
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY, applyToAllClass = Option.BACKGROUND_COLOUR_CLASS)
	protected Colour backgroundColour = new Colour(0, 0, 0, 100);
	@Expose
	@Option
	protected Colour backgroundColourPressed = new Colour(255, 255, 255, 100);
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	protected boolean border = false;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY, applyToAllClass = Option.BORDER_COLOUR_CLASS)
	protected Colour borderColour = Colour.BLACK;
	@Expose
	@Option
	protected Colour borderColourPressed = Colour.WHITE;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY, applyToAllClass = Option.TEXT_COLOUR_CLASS)
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

	private float mouseX, mouseY, lastMouseX, lastMouseY;
	private Keystroke w, a, s, d, lmb, rmb, space;

	@Override
	public String getId() {
		return "keystrokes";
	}

	@Override
	public void postStart() {
		super.postStart();
		w = new Keystroke(this, mc.getOptions().forwardsKey(), "W", 18, 17, 17);
		a = new Keystroke(this, mc.getOptions().strafeLeftKey(), "A", 0, 17, 17);
		s = new Keystroke(this, mc.getOptions().backwardsKey(), "S", 18, 17, 17);
		d = new Keystroke(this, mc.getOptions().strafeRightKey(), "D", 36, 17, 17);
		lmb = new Keystroke(this, mc.getOptions().attackKey(), "LMB", 0, 26, 17);
		rmb = new Keystroke(this, mc.getOptions().useKey(), "RMB", 27, 26, 17);
		space = new Keystroke(this, mc.getOptions().jumpKey(), null, 0, 53, 8);
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
	public void setAngles(CameraRotateEvent event) {
		mouseX += event.getYaw() / 40F;
		mouseY -= event.getPitch() / 40F;
		mouseX = Utils.clamp(mouseX, -(space.width / 2) + 5, space.width / 2 - 4);
		mouseY = Utils.clamp(mouseY, -34 / 2 + 5, 34 / 2 - 5);
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		mouseX *= 0.75F;
		mouseY *= 0.75F;
	}

	@Override
	public void render(Position position, boolean editMode) {
		int x = position.getX();
		int y = position.getY();
		float tickDelta = MinecraftClient.getInstance().getTimer().getTickDelta();

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
				DrawableHelper.fillRect(x, y, x + space.width, y + 34, backgroundColour.getValue());
			}

			if(border) {
				DrawableHelper.strokeRect(x, y, x + space.width, y + 34, borderColour.getValue());
			}

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.resetColour();

			mc.getTextureManager().bind(Identifier.minecraft("textures/gui/sol_client_keystrokes_mouse_ring_centre_" + Utils.getTextureScale() + ".png"));
			DrawableHelper.fillTexturedRect(x + (space.width / 2) - 4, y + (34 / 2) - 4, 0, 0, 8, 8, 8, 8);

			float calculatedMouseX = (lastMouseX + ((mouseX - lastMouseX) * tickDelta)) - 5;
			float calculatedMouseY = (lastMouseY + ((mouseY - lastMouseY) * tickDelta)) - 5;
			GlStateManager.translate(calculatedMouseX, calculatedMouseY, 0);

			mc.getTextureManager().bind(Identifier.minecraft("textures/gui/sol_client_keystrokes_mouse_ring_" + Utils.getTextureScale() + ".png"));
			DrawableHelper.fillTexturedRect(x + (space.width / 2), y + (34 / 2), 0, 0, 10, 10, 10, 10);

			GlStateManager.popMatrix();
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
