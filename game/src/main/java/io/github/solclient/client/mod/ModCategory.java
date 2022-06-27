package io.github.solclient.client.mod;

import java.util.List;
import java.util.stream.Collectors;

import io.github.solclient.abstraction.mc.lang.I18n;
import io.github.solclient.client.Client;
import lombok.RequiredArgsConstructor;

/**
 * Categories of Sol Client mods.
 */
@RequiredArgsConstructor
public enum ModCategory {
	/**
	 * All mods.
	 */
	ALL(false),
	/**
	 * General uncategorisable mods.
	 */
	GENERAL(true),
	/**
	 * HUD widgets.
	 */
	HUD(true),
	/**
	 * Utility mods.
	 */
	UTILITY(true),
	/**
	 * Aesthetic/graphical mods.
	 */
	VISUAL(true),
	/**
	 * Integration mods.
	 */
	INTEGRATION(true);

	private final boolean showName;
	private List<Mod> mods;

	@Override
	public String toString() {
		return showName ? I18n.translate("sol_client.mod.category." + name().toLowerCase() + ".name") : null;
	}

	public List<Mod> getMods(String filter) {
		if(this == ALL) {
			mods = Client.INSTANCE.getMods();
		}
		else if(mods == null) {
			mods = Client.INSTANCE.getMods().stream().filter((mod) -> mod.getCategory() == ModCategory.this)
					.collect(Collectors.toList());
		}

		if(filter.isEmpty()) {
			return mods;
		}

		return mods.stream().filter((mod) -> mod.getName().toLowerCase().contains(filter.toLowerCase())
				|| mod.getDescription().toLowerCase().contains(filter.toLowerCase()))
				.sorted((o1, o2) -> {
					return Integer.compare(o1.getName().toLowerCase()
							.startsWith(filter.toLowerCase()) ? -1 : 1, o2.getName().toLowerCase()
							.startsWith(filter.toLowerCase()) ? -1 : 1);
				}).collect(Collectors.toList());
	}

}
