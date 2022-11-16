package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.level;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.world.level.*;
import io.github.solclient.client.platform.mc.world.level.block.*;
import io.github.solclient.client.platform.mc.world.level.chunk.*;
import net.minecraft.scoreboard.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(World.class)
@Implements(@Interface(iface = Level.class, prefix = "platform$"))
public abstract class LevelImpl {

	public boolean platform$isOpaqueFullCube(@NotNull BlockPos pos) {
		return getBlockState((net.minecraft.util.math.BlockPos) pos).isOpaqueFullCube((BlockView) this,
				(net.minecraft.util.math.BlockPos) pos);
	}

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
	public abstract WorldChunk getChunk(int x, int y);

	public @NotNull WorldBorder platform$getWorldBorder() {
		return (WorldBorder) getWorldBorder();
	}

	@Shadow
	public abstract net.minecraft.world.border.WorldBorder getWorldBorder();

	public @Nullable Text platform$getScoreboardTitle() {
		ScoreboardObjective objective = getScoreboard().getObjectiveForSlot(1);

		if (objective == null) {
			return null;
		}

		return (Text) objective.getDisplayName();
	}

	@Shadow
	public abstract Scoreboard getScoreboard();

}
