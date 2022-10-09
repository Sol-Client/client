package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.inventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.world.inventory.Inventory;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.collection.DefaultedList;

@Mixin(PlayerInventory.class)
public abstract class InventoryImpl implements Inventory {

	// needed because 1.19 item stacks are non-null.
	private static @Nullable ItemStack cast(@NotNull net.minecraft.item.ItemStack item) {
		if(/* this looks stupid, but it's good to be safe */ item == null || item.isEmpty()) {
			return null;
		}

		return (ItemStack) (Object) item;
	}

	@Override
	public @Nullable ItemStack getArmour(int slot) {
		return cast(armor.get(slot));
	}

	@Shadow
	public @Final DefaultedList<net.minecraft.item.ItemStack> armor;

	@Override
	public @Nullable ItemStack getMainHand() {
		return cast(getMainHandStack());
	}

	@Shadow
	public abstract net.minecraft.item.ItemStack getMainHandStack();

	@Override
	public @Nullable ItemStack getItem(int slot) {
		return cast(main.get(slot));
	}

	@Shadow
	public @Final DefaultedList<net.minecraft.item.ItemStack> main;

}

