package io.github.solclient.client.mod.impl.hud;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;

import io.github.solclient.abstraction.mc.DrawableHelper;
import io.github.solclient.abstraction.mc.render.GlStateManager;
import io.github.solclient.abstraction.mc.text.Text;
import io.github.solclient.abstraction.mc.world.scoreboard.PlayerTeam;
import io.github.solclient.abstraction.mc.world.scoreboard.Score;
import io.github.solclient.abstraction.mc.world.scoreboard.Scoreboard;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.hud.PreSidebarRenderEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import io.github.solclient.client.mod.hud.HudMod;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;

public class ScoreboardMod extends Mod {

	public static ScoreboardMod instance;
	public static boolean enabled;

	@Expose
	@Option(translationKey = HudMod.TRANSLATION_KEY)
	@Slider(min = 50, max = 150, step = 1, format = "sol_client.slider.percent")
	public float scale = 100;
	@Expose
	@Option
	public boolean hide;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	public boolean background = true;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY, applyToAllClass = Option.BACKGROUND_COLOUR_CLASS)
	public Colour backgroundColour = new Colour(1342177280);
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	public boolean border = false;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY, applyToAllClass = Option.BORDER_COLOUR_CLASS)
	public Colour borderColour = Colour.BLACK;
	@Expose
	@Option
	public Colour backgroundColourTop = new Colour(1610612736);
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	public Colour textColour = Colour.WHITE;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	public boolean shadow = true;
	@Expose
	@Option
	public boolean numbers = true;
	@Expose
	@Option
	public Colour numbersColour = new Colour(-43691);

	@Override
	public String getId() {
		return "scoreboard";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.HUD;
	}

	@Override
	public void onRegister() {
		super.onRegister();
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
	public void onSidebarRender(PreSidebarRenderEvent event) {
		event.cancel();

		if(hide) {
			return;
		}

		Scoreboard scoreboard = event.getObjective().getScoreboard();
		Collection<Score> scores = scoreboard.getScores(event.getObjective());
		List<Score> filteredScores = Lists.newArrayList(Iterables.filter(scores,
				p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")));
		Collections.reverse(filteredScores);

		if(filteredScores.size() > 15) {
			scores = Lists.newArrayList(Iterables.skip(filteredScores, scores.size() - 15));
		}
		else {
			scores = filteredScores;
		}

		int nameWidth = mc.getFont().getWidth(event.getObjective().getDisplayName());

		for(Score score : scores) {
			PlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
			Text teamText = team.formatText(score.getPlayerName(), numbers);
			nameWidth = Math.max(nameWidth, mc.getFont().getWidth(teamText));
		}

		int scoresHeight = (scores.size() + 1) * mc.getFont().getHeight() + 1;

		int scaledWidth = mc.getWindow().getScaledWidth();
		int scaledHeight = mc.getWindow().getScaledHeight();

		GlStateManager.pushMatrix();
		GlStateManager.translate(-3, 0, 0);
		GlStateManager.scale(scale / 100F, scale / 100F, scale / 100F);

		scaledWidth /= scale / 100;
		scaledHeight /= scale / 100;

		GlStateManager.translate(0, (scaledHeight / 2) - (scoresHeight / 2), 0);

		int k1 = 0;
		int l1 = scaledWidth - nameWidth - k1;

		int j = 0;

		for(Score score : scores) {
			++j;
			PlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
			Text text = team.formatText(score.getPlayerName(), false);
			String points = Integer.toString(score.getValue());
			int k = (j * mc.getFont().getHeight()) + 1;
			int l = scaledWidth - k1 + 2;

			if(background) {
				DrawableHelper.fillRect(l1 - 2, k, l, k + mc.getFont().getHeight(), backgroundColour.getValue());
			}

			mc.getFont().render(text, l1, k, textColour.getValue(), shadow);

			if(numbers) {
				mc.getFont().render(points, l - mc.getFont().getWidth(points) - (border ? 1 : 0), k,
						numbersColour.getValue(), shadow);
			}

			if(j == scores.size()) {
				Text name = event.getObjective().getDisplayName();
				if(background) {
					DrawableHelper.fillRect(l1 - 2, 0, l, mc.getFont().getHeight(), backgroundColourTop.getValue());
					DrawableHelper.fillRect(l1 - 2, mc.getFont().getHeight(), l, mc.getFont().getHeight() + 1,
							backgroundColour.getValue());
				}
				mc.getFont().render(name, l1 + nameWidth / 2 - mc.getFont().getWidth(name) / 2, 1,
						textColour.getValue(), shadow);
			}
		}

		if(border) {
			int top = ((0 - j * mc.getFont().getHeight()) - mc.getFont().getHeight()) - 2;
			DrawableHelper.strokeRect(l1 - 3, top, scaledWidth - k1 + 2,
					top + mc.getFont().getHeight() + 3 + scoresHeight, borderColour.getValue());
		}

		GlStateManager.popMatrix();
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
