package io.github.solclient.client.culling;

import com.logisticscraft.occlusionculling.DataProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;

public class DataProviderImpl implements DataProvider {

	private WorldClient world;

	@Override
	public boolean prepareChunk(int x, int z) {
		world = Minecraft.getMinecraft().theWorld;
		return world != null;
	}

	@Override
	public boolean isOpaqueFullCube(int x, int y, int z) {
		if (world == null)
			return false;

		return world.isBlockNormalCube(new BlockPos(x, y, z), false);
	}

	@Override
	public void cleanup() {
		world = null;
	}

}
