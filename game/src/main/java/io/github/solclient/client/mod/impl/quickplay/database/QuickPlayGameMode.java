package io.github.solclient.client.mod.impl.quickplay.database;

import com.google.gson.JsonObject;

import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayOption;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayPalette;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import lombok.Getter;

public class QuickPlayGameMode implements QuickPlayOption {

	@Getter
	private final QuickPlayGame parent;
	@Getter
	private final String name;
	@Getter
	private final String command;

	public QuickPlayGameMode(QuickPlayGame parent, JsonObject object) {
		this.parent = parent;
		name = object.get("name").getAsString();
		String command = object.get("command").getAsString();

		if(command.equals("/quickplay limbo")) {
			command = "/achat §";
		}
		else if(command.equals("/quickplay delivery")) {
			command = "/delivery";
		}

		this.command = command;
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
