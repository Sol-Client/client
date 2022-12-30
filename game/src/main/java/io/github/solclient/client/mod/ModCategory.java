package io.github.solclient.client.mod;

import java.util.List;
import java.util.stream.Collectors;

import io.github.solclient.client.Client;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

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
	 * User-pinned mods.
	 */
	PINNED(true),
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
		if(getMods().isEmpty() || !showName) {
			return null;
		}

		return I18n.format("sol_client.mod.category." + name().toLowerCase() + ".name");
	}

	public List<Mod> getMods() {
		if(mods == null) {
			mods = Client.INSTANCE.getMods();
			if(this != ALL) {
				mods = mods.stream().filter((mod) -> {
					if(this == PINNED) {
						return mod.isPinned();
					}

					return mod.getCategory() == this;
				}).collect(Collectors.toList());
			}
		}

		return mods;
	}

}
