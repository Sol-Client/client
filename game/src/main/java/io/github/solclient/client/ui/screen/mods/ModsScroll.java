package io.github.solclient.client.ui.screen.mods;

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
			for(ModCategory category : ModCategory.values()) {
				if(category == ModCategory.ALL) {
					continue;
				}

				if(screen.getQuery().isEmpty()) {
					if(category.toString() != null) {
						add(new LabelComponent(category.toString()));
					}
				}

				for(Mod mod : category.getMods(screen.getQuery())) {
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
