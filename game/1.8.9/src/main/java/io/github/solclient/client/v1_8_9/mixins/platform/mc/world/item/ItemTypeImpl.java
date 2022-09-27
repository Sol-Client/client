package io.github.solclient.client.v1_8_9.mixins.platform.mc.world.item;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.mc.world.item.ItemType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

@Mixin(Item.class)
@Implements(@Interface(iface = ItemType.class, prefix = "platform$"))
public class ItemTypeImpl {

}

@Mixin(ItemType.class)
interface ItemTypeImpl$Static {

	@Overwrite(remap = false)
	static @NotNull ItemType get(@NotNull String name) {
		switch(name) {
			case "BLAZE_POWDER":
				return (ItemType) Items.BLAZE_POWDER;
			case "BOW":
				return (ItemType) Items.BOW;
			case "ARROW":
				return (ItemType) Items.ARROW;
			case "DIAMOND":
				return (ItemType) Items.DIAMOND;
			case "DIAMOND_SWORD":
				return (ItemType) Items.DIAMOND_SWORD;
			case "EMERALD":
				return (ItemType) Items.EMERALD;
			case "ENDER_EYE":
				return (ItemType) Items.EYE_OF_ENDER;
			case "FISHING_ROD":
				return (ItemType) Items.FISHING_ROD;
			case "GOLDEN_APPLE":
				return (ItemType) Items.GOLDEN_APPLE;
			case "IRON_BOOTS":
				return (ItemType) Items.IRON_BOOTS;
			case "IRON_CHESTPLATE":
				return (ItemType) Items.IRON_CHESTPLATE;
			case "IRON_HELMET":
				return (ItemType) Items.IRON_HELMET;
			case "IRON_LEGGINGS":
				return (ItemType) Items.IRON_LEGGINGS;
			case "LAVA_BUCKET":
				return (ItemType) Items.LAVA_BUCKET;
			case "RED_BED":
				return (ItemType) Items.BED;
			case "SLIMEBALL":
				return (ItemType) Items.SLIME_BALL;
			case "STONE_AXE":
				return (ItemType) Items.STONE_AXE;
			case "COMPASS":
				return (ItemType) Items.COMPASS;
		}

		throw new IllegalArgumentException(name);
	}

}