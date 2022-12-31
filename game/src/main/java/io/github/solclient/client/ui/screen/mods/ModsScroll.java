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
					if(category.shouldShowName()) {
						add(new LabelComponent(category.toString()));
					}

					for(Mod mod : category.getMods()) {
						add(new ModListing(mod, screen, category == ModCategory.PINNED));
					}
				}
			}
			else {
				String filter = screen.getQuery();
				List<Mod> filtered = Client.INSTANCE.getMods().stream()
						.filter((mod) -> mod.getName().toLowerCase().contains(filter.toLowerCase())
								|| mod.getDescription().toLowerCase().contains(filter.toLowerCase())
								|| mod.getCredit().toLowerCase().contains(filter.toLowerCase()))
						.sorted(Comparator.comparing((Mod mod) -> mod.getName().toLowerCase()
								.startsWith(filter.toLowerCase())).reversed())
						.collect(Collectors.toList());

				for(Mod mod : filtered) {
					add(new ModListing(mod, screen, false));
				}
			}
		}
		else {
			for(ModOption option : screen.getMod().getOptions()) {
				add(new ModOptionComponent(option));
			}
		}
	}

	void notifyAddPin(Mod mod) {
		if(!screen.getQuery().isEmpty()) {
			return;
		}

		int scroll = 0;

		if(Client.INSTANCE.getPins().getMods().size() == 0) {
			// this is the first one
			add(0, new LabelComponent(ModCategory.PINNED.toString()));
			scroll += font.getHeight() + getSpacing();
		}

		add(Client.INSTANCE.getPins().getMods().size() + 1, new ModListing(mod, screen, true));

		scroll += getScrollStep();

		snapTo(getScroll() + scroll);
	}

	void notifyRemovePin(Mod mod) {
		if(!screen.getQuery().isEmpty()) {
			return;
		}

		int scroll = 0;
		int index = Client.INSTANCE.getPins().getMods().indexOf(mod);

		if(Client.INSTANCE.getPins().getMods().size() == 1) {
			// this is the last one
			remove(0);
			scroll += font.getHeight() + getSpacing();
		}
		else {
			index++;
		}

		remove(index);

		scroll += getScrollStep();
		scroll = getScroll() - scroll;

		if(scroll < 0) {
			scroll = 0;
		}

		snapTo(scroll);
	}

	@Override
	public int getSpacing() {
		return 5;
	}

}
