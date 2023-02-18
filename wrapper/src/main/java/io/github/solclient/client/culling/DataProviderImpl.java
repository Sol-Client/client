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
