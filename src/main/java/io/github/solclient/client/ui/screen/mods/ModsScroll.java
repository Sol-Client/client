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

package io.github.solclient.client.ui.screen.mods;

import java.util.*;
import java.util.stream.Collectors;

import io.github.solclient.client.SolClient;
import io.github.solclient.client.mod.*;
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

		if (screen.getFilter().isEmpty()) {
			for (ModCategory category : ModCategory.values()) {
				if (category == ModCategory.HIDDEN)
					return;
				if (category.getMods().isEmpty())
					continue;

				ModCategoryComponent component = new ModCategoryComponent(category, screen);
				if (category == ModCategory.PINNED)
					pinned = component;

				add(component);
			}
		} else {
			String filter = screen.getFilter();
			List<Mod> filtered = SolClient.INSTANCE.modStream().filter((mod) -> {
				String credit = mod.getDetail();
				if (credit == null)
					credit = "";

				return I18n.translate(mod.getName()).toLowerCase().contains(filter.toLowerCase())
						|| I18n.translate(mod.getDescription()).toLowerCase().contains(filter.toLowerCase())
						|| I18n.translate(credit).toLowerCase().contains(filter.toLowerCase());
			}).sorted(Comparator
					.comparing(
							(Mod mod) -> I18n.translate(mod.getName()).toLowerCase().startsWith(filter.toLowerCase()))
					.reversed()).collect(Collectors.toList());

			if (filtered.isEmpty())
				add(new LabelComponent("sol_client.no_results"),
						new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));

			for (Mod mod : filtered)
				add(new ModEntry(mod, screen, false));
		}
	}

	void notifyAddPin(Mod mod) {
		if (!screen.getFilter().isEmpty()) {
			return;
		}

		int scroll = 0;

		if (ModUiStateManager.INSTANCE.getPins().size() == 0) {
			// this is the first one
			add(0, pinned = new ModCategoryComponent(ModCategory.PINNED, screen));
			scroll += 13;
		}

		pinned.add(ModUiStateManager.INSTANCE.getPins().size() + 1, new ModEntry(mod, screen, true));

		if (ModUiStateManager.INSTANCE.isExpanded(ModCategory.PINNED))
			scroll += getScrollStep();
		snapTo(getScroll() + scroll);
	}

	void notifyRemovePin(Mod mod) {
		if (!screen.getFilter().isEmpty()) {
			return;
		}

		int scroll = 0;
		int index = ModUiStateManager.INSTANCE.getPins().indexOf(mod);

		if (ModUiStateManager.INSTANCE.getPins().size() == 1) {
			// this is the last one
			remove(0);
			scroll += 13;
		}

		pinned.remove(index + 1);

		if (ModUiStateManager.INSTANCE.isExpanded(ModCategory.PINNED))
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
