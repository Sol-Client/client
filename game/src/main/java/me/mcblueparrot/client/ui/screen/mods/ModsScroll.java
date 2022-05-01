package me.mcblueparrot.client.ui.screen.mods;

import me.mcblueparrot.client.mod.ModOption;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.impl.LabelComponent;
import me.mcblueparrot.client.ui.component.impl.ScrollListComponent;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen.ModsScreenComponent;

public class ModsScroll extends ScrollListComponent {

	private ModsScreenComponent screen;

	public ModsScroll(ModsScreenComponent screen) {
		this.screen = screen;
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
					if(category.toString() != null && !category.getMods("").isEmpty()) {
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
