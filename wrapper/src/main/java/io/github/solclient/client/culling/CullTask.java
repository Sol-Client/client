/**
 * Ported from https://github.com/tr7zw/EntityCulling-Fabric.
 *
 * MIT License

 * Copyright (c) 2021 tr7zw
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.github.solclient.client.culling;

import java.util.*;
import java.util.Map.Entry;

import org.apache.logging.log4j.*;

import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import com.logisticscraft.occlusionculling.util.Vec3d;

import io.github.solclient.client.util.extension.MinecraftClientExtension;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.chunk.Chunk;

public class CullTask implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger();

	public static boolean requestCull = false;

	private final OcclusionCullingInstance culling = new OcclusionCullingInstance(128, new DataProviderImpl());
	private final MinecraftClient mc = MinecraftClient.getInstance();
	private final int hitboxLimit = 15;

	// reused preallocated vars
	private Vec3d lastPos = new Vec3d(0, 0, 0);
	private Vec3d aabbMin = new Vec3d(0, 0, 0);
	private Vec3d aabbMax = new Vec3d(0, 0, 0);

	public long lastTime = 0;

	@Override
	public void run() {
		while (((MinecraftClientExtension) mc).isRunning()) {
			try {
				Thread.sleep(10);

				if (mc.player != null && mc.getCameraEntity() != null) {
					net.minecraft.util.math.Vec3d cameraMC = Camera.getEntityPos(mc.getCameraEntity(),
							((MinecraftClientExtension) mc).getTicker().tickDelta);

					if (requestCull
							|| !(cameraMC.x == lastPos.x && cameraMC.y == lastPos.y && cameraMC.z == lastPos.z)) {
						long start = System.currentTimeMillis();
						requestCull = false;
						lastPos.set(cameraMC.x, cameraMC.y, cameraMC.z);
						Vec3d camera = lastPos;
						culling.resetCache();
						boolean spectator = mc.player.isSpectator();

						for (int x = -8; x <= 8; x++) {
							for (int z = -8; z <= 8; z++) {
								Chunk chunk = mc.world.getChunk(mc.player.chunkX + x, mc.player.chunkZ + z);
								Iterator<Entry<BlockPos, BlockEntity>> iterator = chunk.getBlockEntities().entrySet()
										.iterator();
								Entry<BlockPos, BlockEntity> entry;
								while (iterator.hasNext()) {
									try {
										entry = iterator.next();
									} catch (NullPointerException | ConcurrentModificationException ex) {
										break; // We are not synced to the main thread, so NPE's/CME are allowed here
										// and way less
										// overhead probably than trying to sync stuff up for no really good reason
									}

									if (entry.getValue().getBlock() == Blocks.BEACON)
										continue;

									BlockEntity tile = entry.getValue();

									if (spectator) {
										((Cullable) tile).setCulled(false);
										continue;
									}

									BlockPos pos = entry.getKey();

									if (pos.squaredDistanceTo(cameraMC.x, cameraMC.y, cameraMC.z) < 4096.0D) {
										aabbMin.set(pos.getX(), pos.getY(), pos.getZ());
										aabbMax.set(pos.getX() + 1d, pos.getY() + 1d, pos.getZ() + 1d);

										boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
										((Cullable) tile).setCulled(!visible);
									}
								}

							}
						}

						Entity entity = null;
						Iterator<Entity> iterable = mc.world.loadedEntities.iterator();

						while (iterable.hasNext()) {
							try {
								entity = iterable.next();
							} catch (NullPointerException | ConcurrentModificationException ex) {
								break; // We are not synced to the main thread, so NPE's/CME are allowed here and way
								// less
								// overhead probably than trying to sync stuff up for no really good reason
							}

							if (spectator || isSkippableArmorstand(entity)) {
								((Cullable) entity).setCulled(false);
								continue;
							}

							if (!(entity.getPos().distanceTo(cameraMC) < 128)) {
								((Cullable) entity).setCulled(false); // If your entity view distance is larger than
								// tracingDistance just render it
								continue;
							}

							Box boundingBox = entity.getBoundingBox();
							if (boundingBox.maxX - boundingBox.minX > hitboxLimit
									|| boundingBox.maxY - boundingBox.minY > hitboxLimit
									|| boundingBox.maxZ - boundingBox.minZ > hitboxLimit) {
								((Cullable) entity).setCulled(false); // To big to bother to cull
								continue;
							}

							aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
							aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);

							boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
							((Cullable) entity).setCulled(!visible);
						}
						lastTime = (System.currentTimeMillis() - start);
					}
				}
			} catch (Exception error) {
				LOGGER.error("Error culling", error);
			}
		}
	}

	private boolean isSkippableArmorstand(Entity entity) {
		return entity instanceof ArmorStandEntity && ((ArmorStandEntity) entity).shouldShowName();
	}

}
