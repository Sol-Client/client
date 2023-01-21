package io.github.solclient.client.mod;

import java.util.List;
import java.util.stream.Collectors;

import io.github.solclient.client.Client;
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

	@Getter
	private final boolean showName;
	private List<Mod> mods;

	public boolean shouldShowName() {
		return !getMods().isEmpty() && showName;
	}

	@Override
	public String toString() {
		return I18n.translate("sol_client.mod.category." + name().toLowerCase() + ".name");
	}

	public List<Mod> getMods() {
		if (this == PINNED) {
			return Client.INSTANCE.getPins().getMods();
		}

		if (mods == null) {
			mods = Client.INSTANCE.getMods().stream().filter((mod) -> mod.getCategory() == this)
					.collect(Collectors.toList());
		}

		return mods;
	}

}
