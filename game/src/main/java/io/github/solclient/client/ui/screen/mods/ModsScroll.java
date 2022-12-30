package io.github.solclient.client.ui.screen.mods;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.ModOption;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.impl.LabelComponent;
import io.github.solclient.client.ui.component.impl.ScrollListComponent;
import io.github.solclient.client.ui.screen.mods.ModsScreen.ModsScreenComponent;

public class ModsScroll extends ScrollListComponent {

	private ModsScreenComponent screen;

	public ModsScroll(ModsScreenComponent screen) {
		this.screen = screen;
	}

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

		if(screen.getMod() == null) {
			if(screen.getQuery().isEmpty()) {
				for(ModCategory category : ModCategory.values()) {
					if(category == ModCategory.ALL) {
						continue;
					}

					if(category.toString() != null) {
						add(new LabelComponent(category.toString()));
					}

					for(Mod mod : category.getMods()) {
						add(new ModListing(mod, screen));
					}
				}
			}
			else {
				String filter = screen.getQuery();
				List<Mod> filtered = Client.INSTANCE.getMods().stream()
						.filter((mod) -> mod.getName().toLowerCase().contains(filter.toLowerCase())
								|| mod.getDescription().toLowerCase().contains(filter.toLowerCase())
								|| mod.getCredit().contains(filter.toLowerCase()))
						.sorted(Comparator.comparing((Mod mod) -> mod.getName().toLowerCase()
								.startsWith(filter.toLowerCase())).reversed())
						.collect(Collectors.toList());

				for(Mod mod : filtered) {
					add(new ModListing(mod, screen));
				}
			}
		}
		else {
			for(ModOption option : screen.getMod().getOptions()) {
				add(new ModOptionComponent(option));
			}
		}
	}

	@Override
	public int getSpacing() {
		return 5;
	}

}
