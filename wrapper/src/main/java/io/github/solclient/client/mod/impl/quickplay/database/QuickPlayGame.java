package io.github.solclient.client.mod.impl.quickplay.database;

import java.util.*;

import com.google.gson.*;

import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayOption;
import io.github.solclient.client.mod.impl.quickplay.ui.QuickPlayPalette.QuickPlayPaletteComponent;
import lombok.*;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Formatting;

public class QuickPlayGame extends QuickPlayOption {

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

		for (JsonElement modeElement : object.get("modes").getAsJsonArray()) {
			QuickPlayGameMode mode = new QuickPlayGameMode(this, modeElement.getAsJsonObject());
			modes.put(mode.getCommand(), mode);
		}

		Item itemType = Items.IRON_INGOT;
		ItemStack item = null;

		switch (id) {
			case "mainLobby":
				itemType = Items.OAK_DOOR;
				break;
			case "arcade":
				itemType = Items.SLIME_BALL;
				break;
			case "bedwars":
				itemType = Items.BED;
				break;
			case "blitz":
				itemType = Items.DIAMOND_SWORD;
				break;
			case "buildBattle":
				itemType = Item.fromBlock(Blocks.CRAFTING_TABLE);
				break;
			case "classic":
				itemType = Item.fromBlock(Blocks.JUKEBOX);
				break;
			case "cvc":
				itemType = Item.fromBlock(Blocks.IRON_BARS);
				break;
			case "duels":
				itemType = Items.FISHING_ROD;
				break;
			case "housing":
				itemType = Items.DARK_OAK_DOOR;
				break;
			case "mw":
				itemType = Item.fromBlock(Blocks.SOULSAND);
				break;
			case "murder":
				itemType = Items.BOW;
				break;
			case "prototype":
				itemType = Item.fromBlock(Blocks.ANVIL);
				break;
			case "skyblock":
				item = new ItemStack(Items.SKULL);
				item.setDamage(3);
				item.setNbt(StringNbtReader.parse(
						"{overrideMeta:1b,HideFlags:254,SkullOwner:{Id:\"e70f48d9-56b0-2023-b32e-f48bbdd063af\",hypixelPopulated:1b,Properties:{textures:[0:{Value:\"eyJ0aW1lc3RhbXAiOjE1NTkyMTU0MTY5MDksInByb2ZpbGVJZCI6IjQxZDNhYmMyZDc0OTQwMGM5MDkwZDU0MzRkMDM4MzFiIiwicHJvZmlsZU5hbWUiOiJNZWdha2xvb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q3Y2M2Njg3NDIzZDA1NzBkNTU2YWM1M2UwNjc2Y2I1NjNiYmRkOTcxN2NkODI2OWJkZWJlZDZmNmQ0ZTdiZjgifX19\"}]}}}"));
				break;
			case "skywars":
				itemType = Items.EYE_OF_ENDER;
				break;
			case "smashHeroes":
				item = new ItemStack(Items.SKULL);
				item.setDamage(3);
				item.setNbt(StringNbtReader.parse(
						"{ench:[],overrideMeta:1b,HideFlags:254,SkullOwner:{Id:\"a83ccfb7-3672-281f-90da-0f78dcf95378\",hypixelPopulated:1b,Properties:{textures:[0:{Value:\"eyJ0aW1lc3RhbXAiOjE0NTIwNTgzNTA4MDcsInByb2ZpbGVJZCI6ImFhZDIzYTUwZWVkODQ3MTA5OWNmNjRiZThmZjM0ZWY0IiwicHJvZmlsZU5hbWUiOiIxUm9ndWUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QyOWE5ZjU3MjY3ZWQzNDJhMTNlM2FkM2EyNDBjNGM1YWY1YTFhMzZhYjJkZTBkNmMyYTMxYWYwZTNjZGRlIn19fQ==\"}]}}}"));
				break;
			case "tnt":
				itemType = Item.fromBlock(Blocks.TNT);
				break;
			case "uhc":
				itemType = Items.GOLDEN_APPLE;
				break;
			case "warlords":
				itemType = Items.STONE_AXE;
				break;
			case "thePit":
				itemType = Item.fromBlock(Blocks.DIRT);
				break;
			case "tournament":
				itemType = Items.BLAZE_POWDER;
				break;
			default:
				itemType = Items.LAVA_BUCKET;
				break;
		}

		if (item == null) {
			this.item = new ItemStack(itemType);
		} else {
			this.item = item;
		}
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
		if (modes.size() == 1)
			return name;

		return Formatting.strip(name) + " →";
	}

	@Override
	public void onClick(QuickPlayPaletteComponent palette, QuickPlayMod mod) {
		if (modes.size() == 1)
			modes.values().stream().findFirst().get().onClick(palette, mod);
		else
			palette.selectGame(this);
	}

	@Override
	public ItemStack getIcon() {
		return item;
	}

}
