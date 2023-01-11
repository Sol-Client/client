package io.github.solclient.client.mod.impl.quickplay.ui;

import java.util.Comparator;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.mod.impl.quickplay.database.*;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayPalette.QuickPlayPaletteComponent;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.data.Alignment;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.EnumChatFormatting;

@RequiredArgsConstructor
public class QuickPlayScroll extends ScrollListComponent {

	private final QuickPlayPaletteComponent screen;

	public void load() {
		clear();
		snapTo(0);

		if (!screen.getFilter().isEmpty()) {
			screen.getMod().getGames().stream().flatMap((entry) -> entry.getModes().stream())
					.filter((
							mode) -> EnumChatFormatting
									.getTextWithoutFormattingCodes(mode.getText().toLowerCase()).contains(
											screen.getFilter().toLowerCase()))
					.sorted(Comparator.comparing((QuickPlayGameMode mode) -> EnumChatFormatting
							.getTextWithoutFormattingCodes(mode.getText().toLowerCase())
							.startsWith(screen.getFilter().toLowerCase())).reversed())
					.forEach((mode) -> add(mode.component(screen)));
		} else if (screen.isAllGames()) {
			add(new BackOption().component(screen));

			if (screen.getGame() != null) {
				for (QuickPlayGameMode mode : screen.getGame().getModes())
					add(mode.component(screen));
			} else
				for (QuickPlayGame game : screen.getMod().getGames())
					add(game.component(screen));
		} else {
			for (QuickPlayOption game : screen.getMod().getRecentlyPlayed())
				add(game.component(screen));

			add(new AllGamesOption().component(screen));
		}

		if (!getSubComponents().isEmpty())
			screen.setSelected((QuickPlayOptionComponent) getSubComponents().get(0));
		else {
			screen.setSelected(null);
			add(new LabelComponent("sol_client.no_results"),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));
		}
	}

	@Override
	public int getSpacing() {
		return 2;
	}

	@Override
	public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
		if (getSubComponents().get(0) instanceof QuickPlayOptionComponent) {
			if (keyCode == Keyboard.KEY_HOME)
				screen.setSelected((QuickPlayOptionComponent) getSubComponents().get(0));
			else if (keyCode == Keyboard.KEY_END)
				screen.setSelected((QuickPlayOptionComponent) getSubComponents().get(getSubComponents().size() - 1));
		}

		return super.keyPressed(info, keyCode, character);
	}

}
