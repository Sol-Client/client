package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.inventory;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.world.inventory.Inventory;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.collection.DefaultedList;

@Mixin(PlayerInventory.class)
public abstract class InventoryImpl implements Inventory {

	@Override
	public @NotNull ItemStack getArmour(int slot) {
		return armor.get(slot);
	}

	@Shadow
	public @Final DefaultedList<ItemStack> armor;

	@Override
	public @NotNull ItemStack getMainHand() {
		return (ItemStack) (Object) getMainHandStack();
	}

	@Shadow
	public abstract net.minecraft.item.ItemStack getMainHandStack();

	@Override
	public @NotNull ItemStack getItem(int slot) {
		return main.get(slot);
	}

	@Shadow
	public @Final DefaultedList<ItemStack> main;

}

