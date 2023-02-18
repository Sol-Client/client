/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mod.impl.replay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.replaymod.recording.gui.GuiRecordingControls;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class RecordingIndicator implements HudElement {

	private static final Identifier RECORDING = new Identifier("replay_mod", "recording.png");
	private static final Identifier PAUSED = new Identifier("replay_mod", "paused.png");

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
	public Position getConfiguredPosition() {
		return mod.recordingIndicatorPosition;
	}

	@Override
	public void setPosition(Position position) {
		mod.recordingIndicatorPosition = position;
	}

	@Override
	public float getScale() {
		return mod.recordingIndicatorScale / 100F;
	}

	@Override
	public Rectangle getBounds(Position position) {
		return new Rectangle(position.getX() - 2, position.getY() - 2, 75, 20);
	}

	@Override
	public Position determineDefaultPosition(int width, int height) {
		return new Position(width - 80, 5);
	}

}
