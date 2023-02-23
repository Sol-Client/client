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

package io.github.solclient.client.mod.impl.hud;

import java.util.*;

import com.google.common.collect.*;
import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.ScoreboardRenderEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.scoreboard.*;
import net.minecraft.util.Formatting;

public class ScoreboardMod extends SolClientMod {

	public static ScoreboardMod instance;
	public static boolean enabled;

	@Expose
	@Option(translationKey = SolClientHudMod.TRANSLATION_KEY)
	@Slider(min = 50, max = 150, step = 1, format = "sol_client.slider.percent")
	private float scale = 100;
	@Expose
	@Option
	private boolean hide;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean background = true;
	@Expose
	@ColourKey(ColourKey.BACKGROUND_COLOUR)
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private Colour backgroundColour = new Colour(1342177280);
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean border = false;
	@Expose
	@ColourKey(ColourKey.BORDER_COLOUR)
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private Colour borderColour = Colour.BLACK;
	@Expose
	@Option
	private Colour backgroundColourTop = new Colour(1610612736);
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private Colour textColour = Colour.WHITE;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean shadow = true;
	@Expose
	@Option
	private boolean numbers = true;
	@Expose
	@Option
	private Colour numbersColour = new Colour(-43691);

	@Override
	public String getId() {
		return "scoreboard";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.HUD;
	}

	@Override
	public void init() {
		super.init();
		instance = this;
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
	}

	@EventHandler
	public void onScoreboardRender(ScoreboardRenderEvent event) {
		event.cancelled = true;

		if (hide) {
			return;
		}

		Scoreboard scoreboard = event.objective.getScoreboard();
		Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(event.objective);
		List<ScoreboardPlayerScore> filteredScores = Lists.newArrayList(Iterables.filter(scores,
				p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")));
		Collections.reverse(filteredScores);

		if (filteredScores.size() > 15) {
			scores = Lists.newArrayList(Iterables.skip(filteredScores, scores.size() - 15));
		} else {
			scores = filteredScores;
		}

		int i = mc.textRenderer.getStringWidth(event.objective.getDisplayName());

		for (ScoreboardPlayerScore score : scores) {
			Team team = scoreboard.getPlayerTeam(score.getPlayerName());
			String text = Team.decorateName(team, score.getPlayerName());

			if (numbers)
				text += ": " + Formatting.RED + score.getScore();

			i = Math.max(i, mc.textRenderer.getStringWidth(text));
		}

		int scoresHeight = (scores.size() + 1) * mc.textRenderer.fontHeight + 1;

		int scaledWidth = event.window.getWidth();
		int scaledHeight = event.window.getHeight();

		GlStateManager.pushMatrix();
		GlStateManager.translate(-3, 0, 0);
		GlStateManager.scale(scale / 100F, scale / 100F, scale / 100F);

		scaledWidth /= scale / 100;
		scaledHeight /= scale / 100;

		GlStateManager.translate(0, (scaledHeight / 2) - (scoresHeight / 2), 0);

		int k1 = 0;
		int l1 = scaledWidth - i - k1;

		int j = 0;

		for (ScoreboardPlayerScore score : scores) {
			++j;
			Team team = scoreboard.getPlayerTeam(score.getPlayerName());
			String text = Team.decorateName(team, score.getPlayerName());
			String points = "" + score.getScore();
			int k = (j * mc.textRenderer.fontHeight) + 1;
			int l = scaledWidth - k1 + 2;

			if (background) {
				DrawableHelper.fill(l1 - 2, k, l, k + mc.textRenderer.fontHeight, backgroundColour.getValue());
			}

			mc.textRenderer.draw(text, l1, k, textColour.getValue(), shadow);

			if (numbers) {
				mc.textRenderer.draw(points, l - mc.textRenderer.getStringWidth(points) - (border ? 1 : 0), k,
						numbersColour.getValue(), shadow);
			}

			if (j == scores.size()) {
				String s3 = event.objective.getDisplayName();
				if (background) {
					DrawableHelper.fill(l1 - 2, 0, l, mc.textRenderer.fontHeight, backgroundColourTop.getValue());
					DrawableHelper.fill(l1 - 2, mc.textRenderer.fontHeight, l, mc.textRenderer.fontHeight + 1,
							backgroundColour.getValue());
				}
				mc.textRenderer.draw(s3, l1 + i / 2 - mc.textRenderer.getStringWidth(s3) / 2, 1, textColour.getValue(),
						shadow);
			}
		}

		if (border) {
			int top = ((0 - j * mc.textRenderer.fontHeight) - mc.textRenderer.fontHeight) - 2;
			MinecraftUtils.drawOutline(l1 - 3, top, scaledWidth - k1 + 2, top + mc.textRenderer.fontHeight + 3 + scoresHeight,
					borderColour.getValue());
		}

		GlStateManager.popMatrix();
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
