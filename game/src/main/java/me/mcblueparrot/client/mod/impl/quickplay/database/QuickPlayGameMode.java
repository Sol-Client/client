package me.mcblueparrot.client.mod.impl.quickplay.database;

import com.google.gson.JsonObject;

import lombok.Getter;
import me.mcblueparrot.client.mod.impl.quickplay.QuickPlayMod;
import me.mcblueparrot.client.mod.impl.quickplay.ui.QuickPlayOption;
import me.mcblueparrot.client.mod.impl.quickplay.ui.QuickPlayPalette;
import net.minecraft.item.ItemStack;

public class QuickPlayGameMode implements QuickPlayOption {

	@Getter
	private QuickPlayGame parent;
	@Getter
	private String name;
	@Getter
	private String command;

	public QuickPlayGameMode(QuickPlayGame parent, JsonObject object) {
		this.parent = parent;
		name = object.get("name").getAsString();
		command = object.get("command").getAsString();

		if(command.equals("/quickplay limbo")) {
			command = "/achat §";
		}
		else if(command.equals("/quickplay delivery")) {
			command = "/delivery";
		}
	}

	public String getFullId() {
		return parent.getId() + "." + command;
	}

	@Override
	public String getText() {
		if(parent.getModes().size() == 1) {
			return parent.getName();
		}
		return parent.getName() + " - " + name;
	}

	@Override
	public void onClick(QuickPlayPalette palette, QuickPlayMod mod) {
		mod.playGame(this);
	}

	@Override
	public ItemStack getIcon() {
		return parent.getIcon();
	}

}
