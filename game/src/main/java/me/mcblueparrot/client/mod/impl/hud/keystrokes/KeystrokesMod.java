package me.mcblueparrot.client.mod.impl.hud.keystrokes;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.CpsMonitor;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PlayerHeadRotateEvent;
import me.mcblueparrot.client.event.impl.PostRenderTickEvent;
import me.mcblueparrot.client.event.impl.PreTickEvent;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.hud.HudMod;
import me.mcblueparrot.client.mod.hud.SimpleHudMod;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.access.AccessMinecraft;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

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

	private float mouseX;
	private float mouseY;
	private float lastMouseX;
	private float lastMouseY;

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
		float partialTicks = AccessMinecraft.getInstance().getTimerSC().renderPartialTicks;

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

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.color(1, 1, 1);

			mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_keystrokes_mouse_ring_centre_" + Utils.getTextureScale() + ".png"));
			Gui.drawModalRectWithCustomSizedTexture(x + (space.width / 2) - 4, y + (34 / 2) - 4, 0, 0, 8, 8, 8, 8);

			float calculatedMouseX = (lastMouseX + ((mouseX - lastMouseX) * partialTicks)) - 5;
			float calculatedMouseY = (lastMouseY + ((mouseY - lastMouseY) * partialTicks)) - 5;
			GL11.glTranslatef(calculatedMouseX, calculatedMouseY, 0);

			mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_keystrokes_mouse_ring_" + Utils.getTextureScale() + ".png"));
			Gui.drawModalRectWithCustomSizedTexture(x + (space.width / 2), y + (34 / 2), 0, 0, 10, 10, 10, 10);
//			Utils.drawCircle(x + space.width / 2F + calculatedMouseX, y + (34F / 2F) + calculatedMouseY, 4, textColour.getValue());

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
