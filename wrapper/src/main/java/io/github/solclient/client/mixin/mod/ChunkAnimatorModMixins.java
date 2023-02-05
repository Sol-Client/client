/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mixin.mod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.mod.impl.chunkanimator.*;
import lombok.*;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class ChunkAnimatorModMixins {

	@Getter
	@Setter
	@Mixin(BuiltChunk.class)
	public static class BuiltChunkMixin implements BuiltChunkData {

		private long animationStart = -1;
		private boolean animationComplete;

		@Override
		public void skipAnimation() {
			animationComplete = true;
		}

	}

	@Mixin(ClientPlayerInteractionManager.class)
	public static class ClientPlayerInteractionManagerMixin {

		@Redirect(method = "onRightClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;canPlaceItemBlock(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Z"))
		public boolean notifyPlace(BlockItem item, World world, BlockPos pos, Direction direction, PlayerEntity player, ItemStack stack) {
			boolean result = item.canPlaceItemBlock(world, pos, direction, player, stack);

			if (result) {
				ChunkAnimatorMod.instance.notifyPlace(pos.add(direction.getVector()));
			}

			return result;
		}

	}

}
