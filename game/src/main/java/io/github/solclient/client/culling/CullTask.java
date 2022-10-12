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
 * This should be replaced, since tr7zw has changed their license to a less permissive one, or I should at least ask permission as I think they are mainly concerned with it being used commercially.
 */

package io.github.solclient.client.culling;

import java.util.*;
import java.util.Map.Entry;

import org.apache.logging.log4j.*;

import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import com.logisticscraft.occlusionculling.util.Vec3d;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.maths.Box;
import io.github.solclient.client.platform.mc.util.MinecraftUtil;
import io.github.solclient.client.platform.mc.world.entity.Entity;
import io.github.solclient.client.platform.mc.world.entity.decoration.ArmourStand;
import io.github.solclient.client.platform.mc.world.level.block.*;
import io.github.solclient.client.platform.mc.world.level.chunk.Chunk;

public class CullTask implements Runnable {

	public static boolean requestCull = false;

	private static final Logger LOGGER = LogManager.getLogger();
	private final OcclusionCullingInstance culling;
	private final MinecraftClient mc = MinecraftClient.getInstance();
	private final int hitboxLimit = 15;
	public long lastTime = 0;

	// reused preallocated vars
	private Vec3d lastPos = new Vec3d(0, 0, 0);
	private Vec3d aabbMin = new Vec3d(0, 0, 0);
	private Vec3d aabbMax = new Vec3d(0, 0, 0);

	public CullTask(OcclusionCullingInstance culling) {
		this.culling = culling;
	}

	@Override
	public void run() {
		try {
			if (mc.hasPlayer() && mc.getCameraEntity() != null) {
				io.github.solclient.client.platform.mc.maths.Vec3d cameraVec = MinecraftUtil.getCameraPos();

				if (requestCull || !(cameraVec.x() == lastPos.x && cameraVec.y() == lastPos.y
						&& cameraVec.z() == lastPos.z)) {
					long start = System.currentTimeMillis();
					requestCull = false;
					lastPos.set(cameraVec.x(), cameraVec.y(), cameraVec.z());
					Vec3d camera = lastPos;
					culling.resetCache();
					boolean spectator = mc.getPlayer().isSpectatorMode();

					for (int x = -8; x <= 8; x++) {
						for (int z = -8; z <= 8; z++) {
							Chunk chunk = mc.getLevel().getChunk(mc.getPlayer().getChunkX() + x,
									mc.getPlayer().getChunkZ() + z);
							Iterator<Entry<BlockPos, BlockEntity>> iterator = chunk.getBlockEntityMap().entrySet()
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

								if (entry.getValue().getBlockType() == BlockType.BEACON) {
									continue;
								}

								BlockEntity tile = entry.getValue();

								if (spectator) {
									((Cullable) tile).setCulled(false);
									continue;
								}

								BlockPos pos = entry.getKey();

								if(pos.distanceSquared(cameraVec.x(), cameraVec.y(), cameraVec.z()) < 4096.0D) {
									aabbMin.set(pos.x(), pos.y(), pos.z());
									aabbMax.set(pos.x() + 1d, pos.y() + 1d, pos.z() + 1d);

									boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
									((Cullable) tile).setCulled(!visible);
								}
							}

						}
					}

					Entity entity = null;
					Iterator<Entity> iterable = mc.getLevel().getRenderedEntities().iterator();

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

						if (!(entity.getPosition().distanceSquared(cameraVec) < 128)) {
							((Cullable) entity).setCulled(false); // If your entity view distance is larger than
							// tracingDistance just render it
							continue;
						}

						Box boundingBox = entity.getBounds();
						if (boundingBox.maxX() - boundingBox.minX() > hitboxLimit
								|| boundingBox.maxY() - boundingBox.minY() > hitboxLimit
								|| boundingBox.maxZ() - boundingBox.minZ() > hitboxLimit) {
							((Cullable) entity).setCulled(false); // To big to bother to cull
							continue;
						}

						aabbMin.set(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
						aabbMax.set(boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ());

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

	private boolean isSkippableArmorstand(Entity entity) {
		return entity instanceof ArmourStand && ((ArmourStand) entity).isMarker();
	}

}
