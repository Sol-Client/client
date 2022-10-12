package io.github.solclient.client.v1_8_9.mixins.platform.mc.world.level;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.world.level.*;
import io.github.solclient.client.platform.mc.world.level.block.*;
import io.github.solclient.client.platform.mc.world.level.chunk.*;
import net.minecraft.scoreboard.*;
import net.minecraft.world.World;

@Mixin(World.class)
@Implements(@Interface(iface = Level.class, prefix = "platform$"))
public abstract class LevelImpl {

	public boolean platform$isOpaqueFullCube(@NotNull BlockPos pos) {
		return method_8565((net.minecraft.util.math.BlockPos) pos);
	}

	@Shadow
	public abstract boolean method_8565(net.minecraft.util.math.BlockPos pos);

	public @NotNull BlockState platform$getBlockState(@NotNull BlockPos pos) {
		return (BlockState) getBlockState((net.minecraft.util.math.BlockPos) pos);
	}

	@Shadow
	public abstract net.minecraft.block.BlockState getBlockState(net.minecraft.util.math.BlockPos pos);

	public @NotNull Chunk platform$getChunk(@NotNull ChunkPos pos) {
		return platform$getChunk(pos.x(), pos.z());
	}

	public @NotNull Chunk platform$getChunk(int x, int z) {
		return (Chunk) getChunk(x, z);
	}

	@Shadow
	public abstract net.minecraft.world.chunk.Chunk getChunk(int chunkX, int chunkZ);

	public @NotNull WorldBorder platform$getWorldBorder() {
		return (WorldBorder) getWorldBorder();
	}

	@Shadow
	public abstract net.minecraft.world.border.WorldBorder getWorldBorder();

	public @Nullable Text platform$getScoreboardTitle() {
		ScoreboardObjective objective = scoreboard.getObjectiveForSlot(1);

		if(objective == null) {
			return null;
		}

		String displayName = objective.getDisplayName();

		if(displayName == null) {
			return null;
		}

		return Text.literal(displayName);
	}

	@Shadow
	protected Scoreboard scoreboard;


}
