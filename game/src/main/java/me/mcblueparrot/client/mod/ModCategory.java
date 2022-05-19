package me.mcblueparrot.client.mod;

import java.util.List;
import java.util.stream.Collectors;

import me.mcblueparrot.client.Client;
import net.minecraft.util.EnumChatFormatting;

/**
 * Categories of Sol Client mods.
 */
public enum ModCategory {
	/**
	 * All mods.
	 */
	ALL(null),
	/**
	 * General uncategorisable mods.
	 */
	GENERAL("General"),
	/**
	 * HUD widgets.
	 */
	HUD("HUD"),
	/**
	 * Utility mods.
	 */
	UTILITY("Utility"),
	/**
	 * Aesthetic/graphical mods.
	 */
	VISUAL("Visual");

	private String name;
	private List<Mod> mods;

	private ModCategory(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
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

		return mods.stream().filter((mod) -> mod.getName().toLowerCase().contains(filter.toLowerCase()))
				.sorted((o1, o2) -> {
					return Integer.compare(o1.getName().toLowerCase()
							.startsWith(filter.toLowerCase()) ? 0 : 1, o2.getName().toLowerCase()
							.startsWith(filter.toLowerCase()) ? 0 : 1);
				}).collect(Collectors.toList());
	}

}
