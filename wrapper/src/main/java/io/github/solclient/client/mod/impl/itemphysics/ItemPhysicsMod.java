/*
 * Original code by TheKodeToad,
 * but was modified to be more similar to CreativeMD's original mod.
 */

package io.github.solclient.client.mod.impl.itemphysics;

import java.util.*;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.ItemEntityRenderEvent;
import io.github.solclient.client.extension.EntityExtension;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.mod.impl.SolClientMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.*;

public class ItemPhysicsMod extends SolClientMod implements PrimaryIntegerSettingMod {

	@Option
	@Slider(min = 0, max = 100, step = 1, format = "sol_client.slider.percent")
	private float rotationSpeed = 100;
	private final Map<ItemEntity, ItemData> dataMap = new WeakHashMap<>(); // May cause a few small bugs, but memory
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
		event.cancelled = true;

		ItemStack itemstack = event.entity.getItemStack();
		Item item = itemstack.getItem();

		if (item != null) {
			boolean is3d = event.model.hasDepth();
			int clumpSize = getClumpSize(itemstack.count);
			GlStateManager.translate((float) event.x, (float) event.y + 0.1, (float) event.z);

			long now = System.nanoTime();

			ItemData data = dataMap.computeIfAbsent(event.entity, (itemStack) -> new ItemData(System.nanoTime()));

			long since = now - data.getLastUpdate();

			GlStateManager.rotate(180, 0, 1, 1);
			GlStateManager.rotate(event.entity.yaw, 0, 0, 1);

			if (!MinecraftClient.getInstance().isPaused()) {
				if (!event.entity.onGround) {
					int divisor = 2500000;
					if (((EntityExtension) event.entity).getIsInWeb()) {
						divisor *= 10;
					}
					data.setRotation(
							data.getRotation() + (((float) since) / ((float) divisor) * (rotationSpeed / 100F)));
				} else if (data.getRotation() != 0) {
					data.setRotation(0);
				}
			}

			GlStateManager.rotate(data.getRotation(), 0, 1, 0);

			data.setLastUpdate(now);

			if (!is3d) {
				float rotationXAndY = -0.0F * (clumpSize - 1) * 0.5F;
				float rotationZ = -0.046875F * (clumpSize - 1) * 0.5F;
				GlStateManager.translate(rotationXAndY, rotationXAndY, rotationZ);
			}

			GlStateManager.color(1, 1, 1, 1);

			event.result = clumpSize;
		} else {
			event.result = 0;
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

	private int getClumpSize(int size) {
		if (size > 48) {
			return 5;
		} else if (size > 32) {
			return 4;
		} else if (size > 16) {
			return 3;
		} else if (size > 1) {
			return 2;
		}
		return 1;
	}

}
