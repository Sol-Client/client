package io.github.solclient.client.ui.screen.mods;

import io.github.solclient.client.mod.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen.ModsScreenComponent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ModsScroll extends ScrollListComponent {

	private final ModsScreenComponent screen;

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
