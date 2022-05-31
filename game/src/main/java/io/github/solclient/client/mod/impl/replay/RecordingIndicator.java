package io.github.solclient.client.mod.impl.replay;

import org.lwjgl.opengl.GL11;

import com.replaymod.core.ReplayMod;
import com.replaymod.recording.gui.GuiRecordingControls;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.hud.BaseHudElement;
import io.github.solclient.client.mod.hud.HudPosition;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class RecordingIndicator extends BaseHudElement {

	private static final ResourceLocation RECORDING = new ResourceLocation(
			"textures/gui/sol_client_recording.png");
	private static final ResourceLocation PAUSED = new ResourceLocation(
			"textures/gui/sol_client_paused.png");

	public static GuiRecordingControls guiControls;
	private final SCReplayMod mod;

	public RecordingIndicator(SCReplayMod mod) {
		this.mod = mod;
	}

	@Override
	public boolean isVisible() {
		return (SCReplayMod.instance.isEnabled() || SCReplayMod.enabled) && mod.recordingIndicator;
	}

	@Override
	public void render(Position position, boolean editMode) {
		if(!editMode && (guiControls == null || guiControls.isStopped())) {
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();

		boolean paused = !editMode && guiControls.isPaused();

		String text = paused ? I18n.format("replaymod.gui.paused") : I18n.format("replaymod.gui.recording");

		mc.fontRendererObj.drawString(text, 20 + position.getX(),
				position.getY() + 8 - (mc.fontRendererObj.FONT_HEIGHT / 2), mod.recordingIndicatorTextColour.getValue(),
				mod.recordingIndicatorTextShadow);

		mc.getTextureManager().bindTexture(paused ? PAUSED : RECORDING);

		GlStateManager.enableBlend();
		Utils.glColour(SCReplayMod.instance.recordingIndicatorColour);
		Gui.drawModalRectWithCustomSizedTexture(position.getX(), position.getY(), 0, 0, 16, 16, 16, 16);
	}

	@Override
	public Mod getMod() {
		return mod;
	}

	@Override
	public boolean isShownInReplay() {
		return false;
	}

	@Override
	public HudPosition getHudPosition() {
		return mod.recordingIndicatorPosition;
	}

	@Override
	public void setHudPosition(HudPosition position) {
		mod.recordingIndicatorPosition = position;
	}

	@Override
	public float getHudScale() {
		return mod.recordingIndicatorScale / 100F;
	}

	@Override
	public Rectangle getBounds(Position position) {
		return new Rectangle(position.getX() - 2, position.getY() - 2, 75, 20);
	}

}
