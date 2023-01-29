package io.github.solclient.client.mod.impl;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.BlockHighlightRenderEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.BlockHitResult.Type;
import net.minecraft.util.math.*;
import net.minecraft.world.level.LevelInfo.GameMode;

public class BlockSelectionMod extends SolClientMod implements PrimaryIntegerSettingMod {

	@Expose
	@Option
	private boolean outline = true;
	@Expose
	@Option
	@Slider(min = 1, max = 10, step = 0.5F)
	private float outlineWidth = 4;
	@Expose
	@Option
	private Colour outlineColour = Colour.BLACK.withAlpha(130);
	@Expose
	@Option
	private boolean fill = true;
	@Expose
	@Option
	private Colour fillColour = Colour.BLACK.withAlpha(50);
	@Expose
	@Option
	private boolean depth = true;
	@Expose
	@Option
	private boolean persistent = true;

	@Override
	public String getId() {
		return "block_selection";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	private boolean canRender(BlockHitResult hit) {
		Entity entity = mc.getCameraEntity();
		boolean result = entity instanceof PlayerEntity && !mc.options.hudHidden;

		if (result && !((PlayerEntity) entity).abilities.allowModifyWorld && !persistent) {
			ItemStack itemstack = ((PlayerEntity) entity).getMainHandStack();

			if (mc.result != null && mc.result.type == Type.BLOCK) {
				BlockPos selectedBlock = mc.result.getBlockPos();
				Block block = mc.world.getBlockState(selectedBlock).getBlock();

				if (mc.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
					result = block.hasBlockEntity() && mc.world.getBlockEntity(selectedBlock) instanceof Inventory;
				} else {
					result = itemstack != null && (itemstack.canDestroy(block) || itemstack.canPlaceOn(block));
				}
			}
		}

		result = result && hit.type == Type.BLOCK;

		return result;
	}

	@EventHandler
	public void onBlockHighlightRenderEvent(BlockHighlightRenderEvent event) {
		event.cancelled = true;

		if (!canRender(event.hit))
			return;

		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);

		if (!depth)
			GlStateManager.disableDepthTest();

		GlStateManager.disableTexture();

		GlStateManager.depthMask(false);
		BlockPos blockpos = event.hit.getBlockPos();
		Block block = mc.world.getBlockState(blockpos).getBlock();

		if (block.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
			block.setBoundingBox(mc.world, blockpos);
			double x = mc.getCameraEntity().prevTickX
					+ (mc.getCameraEntity().x - mc.getCameraEntity().prevTickX) * event.partialTicks;
			double y = mc.getCameraEntity().prevTickY
					+ (mc.getCameraEntity().y - mc.getCameraEntity().prevTickY) * event.partialTicks;
			double z = mc.getCameraEntity().prevTickZ
					+ (mc.getCameraEntity().z - mc.getCameraEntity().prevTickZ) * event.partialTicks;

			Box selectedBox = block.getSelectionBox(mc.world, blockpos);
			selectedBox = selectedBox.expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D)
					.offset(-x, -y, -z);

			if (fill) {
				fillColour.bind();
				MinecraftUtils.fillBox(selectedBox);
			}

			if (outline) {
				outlineColour.bind();
				GL11.glLineWidth(outlineWidth);
				WorldRenderer.drawBox(selectedBox);
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();

		GlStateManager.disableBlend();

		if (!depth)
			GlStateManager.enableDepthTest();

		MinecraftUtils.resetLineWidth();
	}

	@Override
	public void decrement() {
		outlineWidth = Math.max(1, outlineWidth - 1);
	}

	@Override
	public void increment() {
		outlineWidth = Math.min(10, outlineWidth + 1);
	}

}
