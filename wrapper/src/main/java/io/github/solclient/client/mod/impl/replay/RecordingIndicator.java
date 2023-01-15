package io.github.solclient.client.mod.impl.replay;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.replaymod.recording.gui.GuiRecordingControls;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class RecordingIndicator extends BaseHudElement {

	private static final Identifier RECORDING = new Identifier("textures/gui/sol_client_recording.png");
	private static final Identifier PAUSED = new Identifier("textures/gui/sol_client_paused.png");

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
		if (!editMode && (guiControls == null || guiControls.isStopped())) {
			return;
		}

		MinecraftClient mc = MinecraftClient.getInstance();

		boolean paused = !editMode && guiControls.isPaused();

		String text = paused ? I18n.translate("replaymod.gui.paused") : I18n.translate("replaymod.gui.recording");

		mc.textRenderer.draw(text, 20 + position.getX(), position.getY() + 8 - (mc.textRenderer.fontHeight / 2),
				mod.recordingIndicatorTextColour.getValue(), mod.recordingIndicatorTextShadow);

		mc.getTextureManager().bindTexture(paused ? PAUSED : RECORDING);

		GlStateManager.enableBlend();
		SCReplayMod.instance.recordingIndicatorColour.bind();

		DrawableHelper.drawTexture(position.getX(), position.getY(), 0, 0, 16, 16, 16, 16);

		GlStateManager.color(1, 1, 1, 1);
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
