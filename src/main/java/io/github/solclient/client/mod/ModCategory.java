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

package io.github.solclient.client.mod;

import java.util.*;
import java.util.stream.Collectors;

import io.github.solclient.client.SolClient;
import lombok.*;
import net.minecraft.client.resource.language.I18n;

/**
 * Categories of Sol Client mods.
 */
@RequiredArgsConstructor
public enum ModCategory {
	/**
	 * User-pinned mods.
	 */
	PINNED,
	/**
	 * General uncategorisable mods.
	 */
	GENERAL,
	/**
	 * HUD widgets.
	 */
	HUD,
	/**
	 * Utility mods.
	 */
	UTILITY,
	/**
	 * Aesthetic/graphical mods.
	 */
	VISUAL,
	/**
	 * Integration mods.
	 */
	INTEGRATION,
	/**
	 * User-installed mods.
	 */
	INSTALLED,
	HIDDEN;

	private static final Map<String, ModCategory> BY_NAME = new HashMap<>();
	private List<Mod> mods;

	public boolean shouldShowName() {
		return !getMods().isEmpty();
	}

	@Override
	public String toString() {
		return I18n.translate("sol_client.mod.category." + name().toLowerCase() + ".name");
	}

	public List<Mod> getMods() {
		if (this == PINNED)
			return ModUiStateManager.INSTANCE.getPins();

		if (mods == null) {
			mods = SolClient.INSTANCE.modStream().filter((mod) -> mod.getCategory() == this)
					.collect(Collectors.toList());
		}

		return mods;
	}

	public static ModCategory getByName(String name) {
		if (!BY_NAME.containsKey(name))
			throw new IllegalArgumentException(name);

		return BY_NAME.get(name);
	}

	static {
		BY_NAME.put("general", GENERAL);
		BY_NAME.put("hud", HUD);
		BY_NAME.put("utility", UTILITY);
		BY_NAME.put("visual", VISUAL);
		BY_NAME.put("integration", INTEGRATION);
		BY_NAME.put("addons", INSTALLED);
		BY_NAME.put("hidden", HIDDEN);
	}

}
