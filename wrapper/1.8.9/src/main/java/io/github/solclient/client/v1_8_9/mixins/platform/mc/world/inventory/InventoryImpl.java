package io.github.solclient.client.v1_8_9.mixins.platform.mc.world.inventory;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.world.inventory.Inventory;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import net.minecraft.entity.player.PlayerInventory;

@Mixin(PlayerInventory.class)
public abstract class InventoryImpl implements Inventory {

	@Override
	public @Nullable ItemStack getArmour(int slot) {
		return (ItemStack) (Object) armor[slot];
	}

	@Shadow
	public net.minecraft.item.ItemStack[] armor;

	@Override
	public @Nullable ItemStack getMainHand() {
		return (ItemStack) (Object) getMainHandStack();
	}

	@Shadow
	public abstract net.minecraft.item.ItemStack getMainHandStack();

	@Override
	public @Nullable ItemStack getItem(int slot) {
		return (ItemStack) (Object) main[slot];
	}

	@Shadow
	public net.minecraft.item.ItemStack[] main;

}

