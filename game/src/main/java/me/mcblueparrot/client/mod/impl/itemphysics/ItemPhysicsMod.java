/*
 * Original code by mcblueparrot,
 * but was modified to be more similar to CreativeMD's original mod.
 */

package me.mcblueparrot.client.mod.impl.itemphysics;

import java.util.Map;
import java.util.WeakHashMap;

import lombok.Data;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.ItemEntityRenderEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.PrimaryIntegerSettingMod;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.util.access.AccessEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class ItemPhysicsMod extends Mod implements PrimaryIntegerSettingMod {

	@Option
	@Slider(min = 0, max = 100, step = 1, format = "sol_client.slider.percent")
	private float rotationSpeed = 100;
	private final Map<EntityItem, ItemData> dataMap = new WeakHashMap<>(); // May cause a few small bugs, but memory
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

		ItemStack itemstack = event.entity.getEntityItem();
		Item item = itemstack.getItem();

		if(item != null) {
			boolean is3d = event.model.isGui3d();
			int clumpSize = getClumpSize(itemstack.stackSize);
			GlStateManager.translate((float) event.x, (float) event.y + 0.1, (float) event.z);

			long now = System.nanoTime();

			ItemData data = dataMap.computeIfAbsent(event.entity, (itemStack) -> new ItemData(System.nanoTime()));

			long since = now - data.getLastUpdate();

			GlStateManager.rotate(180, 0, 1, 1);
			GlStateManager.rotate(event.entity.rotationYaw, 0, 0, 1);

			if(!Minecraft.getMinecraft().isGamePaused()) {
				if(!event.entity.onGround) {
					int divisor = 2500000;
					if(((AccessEntity) event.entity).getIsInWeb()) {
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

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			event.result = clumpSize;
		}
		else {
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
