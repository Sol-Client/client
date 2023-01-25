package io.github.solclient.client.ui.screen.mods;

import java.util.*;
import java.util.stream.Collectors;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen.ModsScreenComponent;
import io.github.solclient.client.util.data.Alignment;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModsScroll extends ScrollListComponent {

	private final ModsScreenComponent screen;

	@Override
	protected int getScrollStep() {
		return 30 + getSpacing();
	}

	@Override
	public void setParent(Component parent) {
		super.setParent(parent);
	}

	public void load() {
		clear();

		if (screen.getMod() == null) {
			if (screen.getFilter().isEmpty()) {
				for (ModCategory category : ModCategory.values()) {
					if (category.shouldShowName())
						add(new LabelComponent(category.toString()));

					for (Mod mod : category.getMods())
						add(new ModEntry(mod, screen, category == ModCategory.PINNED));
				}
			} else {
				String filter = screen.getFilter();
				List<Mod> filtered = Client.INSTANCE.getMods().stream()
						.filter((mod) -> mod.getName().toLowerCase().contains(filter.toLowerCase())
								|| mod.getDescription().toLowerCase().contains(filter.toLowerCase())
								|| mod.getCredit().toLowerCase().contains(filter.toLowerCase()))
						.sorted(Comparator
								.comparing((Mod mod) -> mod.getName().toLowerCase().startsWith(filter.toLowerCase()))
								.reversed())
						.collect(Collectors.toList());

				if (filtered.isEmpty())
					add(new LabelComponent("sol_client.no_results"),
							new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));

				for (Mod mod : filtered)
					add(new ModEntry(mod, screen, false));
			}
		} else {
			for (ModOption option : screen.getMod().getOptions()) {
				add(new ModOptionComponent(option));
			}
		}
	}

	void notifyAddPin(Mod mod) {
		if (!screen.getFilter().isEmpty()) {
			return;
		}

		int scroll = 0;

		if (Client.INSTANCE.getPins().getMods().size() == 0) {
			// this is the first one
			add(0, new LabelComponent(ModCategory.PINNED.toString()));
			scroll += regularFont.getLineHeight(nvg) + 2 + getSpacing();
		}

		add(Client.INSTANCE.getPins().getMods().size() + 1, new ModEntry(mod, screen, true));

		scroll += getScrollStep();

		snapTo(getScroll() + scroll);
	}

	void notifyRemovePin(Mod mod) {
		if (!screen.getFilter().isEmpty()) {
			return;
		}

		int scroll = 0;
		int index = Client.INSTANCE.getPins().getMods().indexOf(mod);

		if (Client.INSTANCE.getPins().getMods().size() == 1) {
			// this is the last one
			remove(0);
			scroll += regularFont.getLineHeight(nvg) + 2 + getSpacing();
		} else {
			index++;
		}

		remove(index);

		scroll += getScrollStep();
		scroll = getScroll() - scroll;

		if (scroll < 0) {
			scroll = 0;
		}

		snapTo(scroll);
	}

	@Override
	public int getSpacing() {
		return 5;
	}

}
