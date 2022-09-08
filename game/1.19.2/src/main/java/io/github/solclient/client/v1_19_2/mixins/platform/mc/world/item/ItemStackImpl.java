package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import io.github.solclient.client.platform.mc.world.item.ItemType;
import io.github.solclient.client.platform.mc.world.level.Level;
import io.github.solclient.client.platform.mc.world.level.block.BlockPos;
import io.github.solclient.client.platform.mc.world.level.block.BlockType;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

@Mixin(net.minecraft.item.ItemStack.class)
@Implements(@Interface(iface = ItemStack.class, prefix = "platform$"))
public abstract class ItemStackImpl {

	public int platform$getQuantity() {
		return getCount();
	}

	@Shadow
	public abstract int getCount();

	public void platform$setQuantity(int quantity) {
		setCount(quantity);
	}

	@Shadow
	public abstract void setCount(int count);

	public ItemType platform$getType() {
		return (ItemType) getItem();
	}

	@Shadow
	public abstract Item getItem();

	public int platform$getDamageValue() {
		return getDamage();
	}

	@Shadow
	public abstract int getDamage();

	public int platform$getMaxDamageValue() {
		return getMaxDamage();
	}

	@Shadow
	public abstract int getMaxDamage();

	public @NotNull Text platform$getDisplayName() {
		return (Text) getName();
	}

	@Shadow
	public abstract net.minecraft.text.Text getName();

	private static Registry<Block> registry(Level level) {
		return ((World) level).getRegistryManager().get(Registry.BLOCK_KEY);
	}

	private CachedBlockPosition pos(Level level, BlockPos pos) {
		return new CachedBlockPosition((WorldView) level, (net.minecraft.util.math.BlockPos) pos, false);
	}

	public boolean platform$canDestroy(@NotNull Level level, @NotNull BlockPos pos) {
		return canDestroy(registry(level), pos(level, pos));
	}

	@Shadow
	public abstract boolean canDestroy(Registry<Block> registry, CachedBlockPosition position);

	public boolean platform$canPlaceOn(@NotNull Level level, @NotNull BlockPos pos) {
		return canPlaceOn(registry(level), pos(level, pos));
	}

	@Shadow
	public abstract boolean canPlaceOn(Registry<Block> registry, CachedBlockPosition position);

	public int platform$getMaxItemUseTime() {
		return getMaxUseTime();
	}

	@Shadow
	public abstract int getMaxUseTime();

}

@Mixin(ItemStack.class)
interface ItemStackImpl$Static {

	@Overwrite(remap = false)
	static @NotNull ItemStack create(@Nullable ItemType type) {
		return (ItemStack) (Object) new net.minecraft.item.ItemStack((Item) type);
	}

	@Overwrite(remap = false)
	static @NotNull ItemStack create(@Nullable ItemType type, int quantity) {
		return (ItemStack) (Object) new net.minecraft.item.ItemStack((Item) type, quantity);
	}

	@Overwrite(remap = false)
	static @NotNull ItemStack create(@Nullable BlockType type) {
		return (ItemStack) (Object) new net.minecraft.item.ItemStack((Block) type);
	}

	@Overwrite(remap = false)
	static @NotNull ItemStack create(@Nullable BlockType type, int quantity) {
		return (ItemStack) (Object) new net.minecraft.item.ItemStack((Block) type, quantity);
	}

}