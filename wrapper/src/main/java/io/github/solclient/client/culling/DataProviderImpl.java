package io.github.solclient.client.culling;

import com.logisticscraft.occlusionculling.DataProvider;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

public class DataProviderImpl implements DataProvider {

	private ClientWorld world;

	@Override
	public boolean prepareChunk(int x, int z) {
		world = MinecraftClient.getInstance().world;
		return world != null;
	}

	@Override
	public boolean isOpaqueFullCube(int x, int y, int z) {
		if (world == null)
			return false;

		return world.renderAsNormalBlock(new BlockPos(x, y, z), false);
	}

	@Override
	public void cleanup() {
		world = null;
	}

}
