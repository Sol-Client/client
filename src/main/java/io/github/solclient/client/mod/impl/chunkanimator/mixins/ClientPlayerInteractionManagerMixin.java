package io.github.solclient.client.mod.impl.chunkanimator.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.mod.impl.chunkanimator.ChunkAnimatorMod;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

	@Redirect(method = "onRightClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;canPlaceItemBlock(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Z"))
	public boolean notifyPlace(BlockItem item, World world, BlockPos pos, Direction direction, PlayerEntity player,
			ItemStack stack) {
		boolean result = item.canPlaceItemBlock(world, pos, direction, player, stack);

		if (result) {
			ChunkAnimatorMod.instance.notifyPlace(pos.add(direction.getVector()));
		}

		return result;
	}

}