package io.github.solclient.client.ui.screen.mods;

import java.util.*;
import java.util.stream.Collectors;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.ModOption;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen.ModsScreenComponent;
import io.github.solclient.client.util.data.Alignment;
import lombok.*;
import net.minecraft.client.resource.language.I18n;

@RequiredArgsConstructor
public final class ModsScroll extends ScrollListComponent {

	private final ModsScreenComponent screen;
	@Getter
	private ModCategoryComponent pinned;

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
		pinned = null;

		if (screen.getMod() == null) {
			if (screen.getFilter().isEmpty()) {
				for (ModCategory category : ModCategory.values()) {
					if (category.getMods().isEmpty())
						continue;

					ModCategoryComponent component = new ModCategoryComponent(category, screen);
					if (category == ModCategory.PINNED)
						pinned = component;

					add(component);
				}
			} else {
				String filter = screen.getFilter();
				List<Mod> filtered = Client.INSTANCE.getMods().stream().filter((mod) -> {
					String credit = mod.getDetail();
					if (credit == null)
						credit = "";

					return I18n.translate(mod.getName()).toLowerCase().contains(filter.toLowerCase())
							|| I18n.translate(mod.getDescription()).toLowerCase().contains(filter.toLowerCase())
							|| I18n.translate(credit).toLowerCase().contains(filter.toLowerCase());
				}).sorted(Comparator.comparing(
						(Mod mod) -> I18n.translate(mod.getName()).toLowerCase().startsWith(filter.toLowerCase()))
						.reversed()).collect(Collectors.toList());

				if (filtered.isEmpty())
					add(new LabelComponent("sol_client.no_results"),
							new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));

				for (Mod mod : filtered)
					add(new ModEntry(mod, screen, false));
			}
		} else {
			for (ModOption<?> option : screen.getMod().getOptions())
				add(new ModOptionComponent(option));
		}
	}

	void notifyAddPin(Mod mod) {
		if (!screen.getFilter().isEmpty()) {
			return;
		}

		int scroll = 0;

		if (Client.INSTANCE.getModUiState().getPins().size() == 0) {
			// this is the first one
			add(0, pinned = new ModCategoryComponent(ModCategory.PINNED, screen));
			scroll += 13;
		}

		pinned.add(Client.INSTANCE.getModUiState().getPins().size() + 1, new ModEntry(mod, screen, true));

		if (Client.INSTANCE.getModUiState().isExpanded(ModCategory.PINNED))
			scroll += getScrollStep();
		snapTo(getScroll() + scroll);
	}

	void notifyRemovePin(Mod mod) {
		if (!screen.getFilter().isEmpty()) {
			return;
		}

		int scroll = 0;
		int index = Client.INSTANCE.getModUiState().getPins().indexOf(mod);

		if (Client.INSTANCE.getModUiState().getPins().size() == 1) {
			// this is the last one
			remove(0);
			scroll += 13;
		}

		pinned.remove(index + 1);

		if (Client.INSTANCE.getModUiState().isExpanded(ModCategory.PINNED))
			scroll += getScrollStep();
		scroll = getScroll() - scroll;

		if (scroll < 0)
			scroll = 0;

		snapTo(scroll);
	}

	@Override
	public int getSpacing() {
		return 5;
	}

}
