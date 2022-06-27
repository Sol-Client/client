package io.github.solclient.client.mod.impl.hud;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;

import io.github.solclient.abstraction.mc.DrawableHelper;
import io.github.solclient.abstraction.mc.text.Text;
import io.github.solclient.abstraction.mc.world.scoreboard.PlayerTeam;
import io.github.solclient.abstraction.mc.world.scoreboard.Score;
import io.github.solclient.abstraction.mc.world.scoreboard.Scoreboard;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.hud.PreSidebarRenderEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.util.data.Colour;

public class ScoreboardMod extends Mod {

	public static ScoreboardMod instance;
	public static boolean enabled;

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

		Scoreboard scoreboard = event.getObjective().getScoreboard();
		Collection<Score> collection = scoreboard.getScores(event.getObjective());
		List<Score> list = Lists.newArrayList(Iterables.filter(collection,
				p_apply_1_ -> p_apply_1_.getOwner() != null && !p_apply_1_.getOwner().startsWith("#")));

		if(list.size() > 15) {
			collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
		}
		else {
			collection = list;
		}

		int i = mc.getFont().getWidth(event.getObjective().getDisplayName());

		for(Score score : collection) {
			PlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
			Text text = team.formatText();
			i = Math.max(i, mc.getFont().getWidth(text));
		}

		int i1 = collection.size() * mc.getFont().getHeight();
		int j1 = mc.getWindow().getScaledHeight() / 2 + i1 / 3;
		int k1 = 3;
		int l1 = mc.getWindow().getScaledWidth() - i - k1;
		int j = 0;

		for(Score score : collection) {
			++j;
			PlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
			Text text = team.formatText();
			String s2 = "" + score.getValue();
			int k = j1 - j * mc.getFont().getHeight();
			int l = mc.getWindow().getScaledWidth() - k1 + 2;

			if(background) {
				DrawableHelper.fillRect(l1 - 2, k, l, k + mc.getFont().getHeight(), backgroundColour.getValue());
			}

			mc.getFont().render(text, l1, k, textColour.getValue(), shadow);

			if(numbers) {
				mc.getFont().render(s2, l - mc.getFont().getWidth(s2) - (border ? 1 : 0), k,
						numbersColour.getValue(), shadow);
			}

			if(j == collection.size()) {
				Text s3 = event.getObjective().getDisplayName();
				if (background) {
					DrawableHelper.fillRect(l1 - 2, k - mc.getFont().getHeight() - 1, l, k - 1,
							backgroundColourTop.getValue());
					DrawableHelper.fillRect(l1 - 2, k - 1, l, k, backgroundColour.getValue());
				}
				mc.getFont().render(s3, l1 + i / 2 - mc.getFont().getWidth(s3) / 2, 1, textColour.getValue(),
						shadow);
			}
		}

		if (border) {
			int top = ((j1 - j * mc.getFont().getHeight()) - mc.getFont().getHeight()) - 2;
			DrawableHelper.strokeRect(l1 - 3, top, mc.getWindow().getScaledWidth() - k1 + 2,
					top + mc.getFont().getHeight() + 3 + i1, borderColour.getValue());
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
