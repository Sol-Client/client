package io.github.solclient.client.mod.impl;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.BlockHighlightRenderEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.PrimaryIntegerSettingMod;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldSettings;

public class BlockSelectionMod extends Mod implements PrimaryIntegerSettingMod {

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

	private boolean canRender(MovingObjectPosition movingObjectPositionIn) {
		Entity entity = this.mc.getRenderViewEntity();
		boolean result = entity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;

		if(result && !((EntityPlayer)entity).capabilities.allowEdit && !persistent) {
			ItemStack itemstack = ((EntityPlayer)entity).getCurrentEquippedItem();

			if(this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				BlockPos selectedBlock = this.mc.objectMouseOver.getBlockPos();
				Block block = this.mc.theWorld.getBlockState(selectedBlock).getBlock();

				if(this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
					result = block.hasTileEntity() && this.mc.theWorld.getTileEntity(selectedBlock) instanceof IInventory;
				}
				else {
					result = itemstack != null && (itemstack.canDestroy(block) || itemstack.canPlaceOn(block));
				}
			}
		}

		result = result && movingObjectPositionIn.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK;

		return result;
	}

	@EventHandler
	public void onBlockHighlightRenderEvent(BlockHighlightRenderEvent event) {
		event.cancelled = true;

		if(!canRender(event.movingObjectPosition)) {
			return;
		}

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

		if(!depth) {
			GlStateManager.disableDepth();
		}

		GlStateManager.disableTexture2D();

		GlStateManager.depthMask(false);
		BlockPos blockpos = event.movingObjectPosition.getBlockPos();
		Block block = mc.theWorld.getBlockState(blockpos).getBlock();

		if(block.getMaterial() != Material.air && mc.theWorld.getWorldBorder().contains(blockpos)) {
			block.setBlockBoundsBasedOnState(mc.theWorld, blockpos);
			double x = mc.getRenderViewEntity().lastTickPosX
					+ (mc.getRenderViewEntity().posX - mc.getRenderViewEntity().lastTickPosX) * (double) event.partialTicks;
			double y = mc.getRenderViewEntity().lastTickPosY
					+ (mc.getRenderViewEntity().posY - mc.getRenderViewEntity().lastTickPosY) * (double) event.partialTicks;
			double z = mc.getRenderViewEntity().lastTickPosZ
					+ (mc.getRenderViewEntity().posZ - mc.getRenderViewEntity().lastTickPosZ) * (double) event.partialTicks;

			AxisAlignedBB selectedBox = block.getSelectedBoundingBox(mc.theWorld, blockpos);
			selectedBox = selectedBox.expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D)
					.offset(-x, -y, -z);

			if(fill) {
				fillColour.bind();
				Utils.fillBox(selectedBox);
			}

			if(outline) {
				outlineColour.bind();
				GL11.glLineWidth(outlineWidth);
				RenderGlobal.drawSelectionBoundingBox(selectedBox);
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();

		GlStateManager.disableBlend();

		if(!depth) {
			GlStateManager.enableDepth();
		}

		Utils.resetLineWidth();
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
