package io.github.solclient.client.mod.impl.quickplay.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.solclient.abstraction.mc.world.item.ItemStack;
import io.github.solclient.abstraction.mc.world.item.ItemType;
import io.github.solclient.abstraction.mc.world.level.block.BlockType;
import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayOption;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayPalette;
import lombok.Getter;
import lombok.SneakyThrows;

public class QuickPlayGame implements QuickPlayOption {

	@Getter
	private final String id;
	@Getter
	private final String name;
	private final Map<String, QuickPlayGameMode> modes;
	@Getter
	private final ItemStack item;

	@SneakyThrows
	public QuickPlayGame(JsonObject object) {
		id = object.get("unlocalizedName").getAsString();
		name = object.get("name").getAsString();
		modes = new LinkedHashMap<>();

		for(JsonElement modeElement : object.get("modes").getAsJsonArray()) {
			QuickPlayGameMode mode = new QuickPlayGameMode(this, modeElement.getAsJsonObject());
			modes.put(mode.getCommand(), mode);
		}

		ItemType itemType = ItemType.SLIMEBALL;

		switch(id) {
			case "mainLobby":
				itemType = BlockType.OAK_DOOR.toItem();
				break;
			case "arcade":
				itemType = ItemType.SLIMEBALL;
				break;
			case "bedwars":
				itemType = ItemType.RED_BED;
				break;
			case "blitz":
				itemType = ItemType.DIAMOND_SWORD;
				break;
			case "buildBattle":
				itemType = BlockType.CRAFTING_TABLE.toItem();
				break;
			case "classic":
				itemType = BlockType.JUKEBOX.toItem();
				break;
			case "cvc":
				itemType = BlockType.IRON_BARS.toItem();
				break;
			case "duels":
				itemType = ItemType.FISHING_ROD;
				break;
			case "housing":
				itemType = BlockType.DARK_OAK_DOOR.toItem();
				break;
			case "mw":
				itemType = BlockType.SOUL_SAND.toItem();
				break;
			case "murder":
				itemType = ItemType.BOW;
				break;
			case "prototype":
				itemType = BlockType.ANVIL.toItem();
				break;
			case "skyblock":
				itemType = BlockType.GRASS_BLCOK.toItem();
//				item = new ItemStack(Items.skull);
//				item.setItemDamage(3);
//				item.setTagCompound(JsonToNBT.getTagFromJson(
//						"{overrideMeta:1b,HideFlags:254,SkullOwner:{Id:\"e70f48d9-56b0-2023-b32e-f48bbdd063af\",hypixelPopulated:1b,Properties:{textures:[0:{Value:\"eyJ0aW1lc3RhbXAiOjE1NTkyMTU0MTY5MDksInByb2ZpbGVJZCI6IjQxZDNhYmMyZDc0OTQwMGM5MDkwZDU0MzRkMDM4MzFiIiwicHJvZmlsZU5hbWUiOiJNZWdha2xvb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q3Y2M2Njg3NDIzZDA1NzBkNTU2YWM1M2UwNjc2Y2I1NjNiYmRkOTcxN2NkODI2OWJkZWJlZDZmNmQ0ZTdiZjgifX19\"}]}}}"));
				break;
			case "skywars":
				itemType = ItemType.ENDER_EYE;
				break;
			case "smashHeroes":
//				item = new ItemStack(Items.skull);
//				item.setItemDamage(3);
//				item.setTagCompound(JsonToNBT.getTagFromJson(
//						"{ench:[],overrideMeta:1b,HideFlags:254,SkullOwner:{Id:\"a83ccfb7-3672-281f-90da-0f78dcf95378\",hypixelPopulated:1b,Properties:{textures:[0:{Value:\"eyJ0aW1lc3RhbXAiOjE0NTIwNTgzNTA4MDcsInByb2ZpbGVJZCI6ImFhZDIzYTUwZWVkODQ3MTA5OWNmNjRiZThmZjM0ZWY0IiwicHJvZmlsZU5hbWUiOiIxUm9ndWUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QyOWE5ZjU3MjY3ZWQzNDJhMTNlM2FkM2EyNDBjNGM1YWY1YTFhMzZhYjJkZTBkNmMyYTMxYWYwZTNjZGRlIn19fQ==\"}]}}}"));
				break;
			case "tnt":
				itemType = BlockType.TNT.toItem();
				break;
			case "uhc":
				itemType = ItemType.GOLDEN_APPLE;
				break;
			case "warlords":
				itemType = ItemType.STONE_AXE;
				break;
			case "thePit":
				itemType = BlockType.DIRT.toItem();
				break;
			case "tournament":
				itemType = ItemType.BLAZE_POWDER;
				break;
			default:
				itemType = ItemType.LAVA_BUCKET;
				break;
		}

//		if(item == null) {
		this.item = ItemStack.create(itemType);
//		}
//		else {
//			this.item = item;
//		}
	}

	public QuickPlayGameMode getMode(String command) {
		return modes.get(command);
	}

	public List<QuickPlayGameMode> getModes() {
		return new ArrayList<>(modes.values());
	}

	public List<QuickPlayOption> getModeOptions() {
		return new ArrayList<>(modes.values());
	}

	@Override
	public String getText() {
		if(modes.size() == 1) {
			return name;
		}

		return name + " >";
	}

	@Override
	public void onClick(QuickPlayPalette palette, QuickPlayMod mod) {
		if(modes.size() == 1) {
			modes.values().stream().findFirst().get().onClick(palette, mod);
		}
		else {
			palette.selectGame(this);
		}
	}

	@Override
	public ItemStack getIcon() {
		return item;
	}

}
