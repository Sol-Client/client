package me.mcblueparrot.client.ui.screen.mods;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.ui.component.impl.LabelComponent;
import me.mcblueparrot.client.ui.component.impl.ScrollListComponent;

public class ModsScroll extends ScrollListComponent {

	public ModsScroll() {
		for(ModCategory category : ModCategory.values()) {
			if(category == ModCategory.ALL) {
				continue;
			}

			if(category.toString() != null) {
				add(new LabelComponent(category.toString()));
			}

			for(Mod mod : category.getMods("")) {
				add(new ModListing(mod));
			}
		}
	}

	@Override
	public int getSpacing() {
		return 5;
	}

}
