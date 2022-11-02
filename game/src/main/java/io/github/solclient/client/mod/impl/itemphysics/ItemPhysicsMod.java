/*
 * Original code by TheKodeToad,
 * but was modified to be more similar to CreativeMD's original mod.
 */

package io.github.solclient.client.mod.impl.itemphysics;

import java.util.*;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.world.entity.render.ItemEntityRenderEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.platform.mc.world.entity.Entity;
import io.github.solclient.client.platform.mc.world.item.*;

public class ItemPhysicsMod extends Mod implements PrimaryIntegerSettingMod {

	public static final ItemPhysicsMod INSTANCE = new ItemPhysicsMod();

	@Option
	@Slider(min = 0, max = 100, step = 1, format = "sol_client.slider.percent")
	private float rotationSpeed = 100;
	private final Map<Entity, ItemData> dataMap = new WeakHashMap<>(); // May cause a few small bugs, but memory
																	   // usage is prioritised.
	@Override
	public String getId() {
		return "item_physics";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@EventHandler
	public void onItemEntityRenderEvent(ItemEntityRenderEvent event) {
		ItemStack item = event.getEntity().getItem();
		ItemType type = item.getType();

		if(type != null) {
			boolean is3d = event.getModel().isGui3d();
			int clumpSize = getClumpSize(item.getQuantity());
			GlStateManager.translate(event.getX(), event.getY() + 0.1, event.getZ());

			long now = System.nanoTime();

			ItemData data = dataMap.computeIfAbsent(event.getEntity(), (itemStack) -> new ItemData(System.nanoTime()));

			long since = now - data.getLastUpdate();

			GlStateManager.rotate(180, 0, 1, 1);
			GlStateManager.rotate(event.getEntity().yaw(), 0, 0, 1);

			if(!mc.isGamePaused()) {
				if(!event.getEntity().isEntityOnGround()) {
					int divisor = 2500000;
					if(event.getEntity().isInWeb()) {
						divisor *= 10;
					}
					data.setRotation(data.getRotation() + (((float) since) / ((float) divisor) * (rotationSpeed / 100F)));
				}
				else if(data.getRotation() != 0) {
					data.setRotation(0);
				}
			}

			GlStateManager.rotate(data.getRotation(), 0, 1, 0);

			data.setLastUpdate(now);

			if(!is3d) {
				float rotationXAndY = -0.0F * (clumpSize - 1) * 0.5F;
				float rotationZ = -0.046875F * (clumpSize - 1) * 0.5F;
				GlStateManager.translate(rotationXAndY, rotationXAndY, rotationZ);
			}

			GlStateManager.resetColour();

			event.setReturnValue(clumpSize);
		}
		else {
			event.setReturnValue(0);
		}
	}

	@Override
	public void decrement() {
		rotationSpeed = Math.max(0, rotationSpeed - 10);
	}

	@Override
	public void increment() {
		rotationSpeed = Math.min(100, rotationSpeed + 10);
	}

	private static int getClumpSize(int size) {
		if(size > 48) {
			return 5;
		}
		else if(size > 32) {
			return 4;
		}
		else if(size > 16) {
			return 3;
		}
		else if(size > 1) {
			return 2;
		}
		return 1;
	}

}
