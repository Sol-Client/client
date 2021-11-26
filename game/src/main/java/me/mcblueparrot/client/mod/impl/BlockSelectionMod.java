package me.mcblueparrot.client.mod.impl;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.events.BlockHighlightRenderEvent;
import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.util.Colour;
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

public class BlockSelectionMod extends Mod {

	@Expose
	@ConfigOption("Outline")
	private boolean outline = true;
	@Expose
	@ConfigOption("Outline Width")
	@Slider(min = 1, max = 10, step = 1)
	private float outlineWidth = 4;
	@Expose
	@ConfigOption("Outline Colour")
	private Colour outlineColour = Colour.BLACK.withAlpha(130);
	@Expose
	@ConfigOption("Fill")
	private boolean fill = true;
	@Expose
	@ConfigOption("Fill Colour")
	private Colour fillColour = Colour.BLACK.withAlpha(50);
	@Expose
	@ConfigOption("Depth")
	private boolean depth = true;
	@Expose
	@ConfigOption("Persistant")
	private boolean persistant = true;

	public BlockSelectionMod() {
		super("Block Selection", "block_selection", "Customise block selection.", ModCategory.VISUAL);
	}

	private boolean canRender(MovingObjectPosition movingObjectPositionIn) {
		Entity entity = this.mc.getRenderViewEntity();
		boolean result = entity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;

		if (result && !((EntityPlayer)entity).capabilities.allowEdit && !persistant) {
			ItemStack itemstack = ((EntityPlayer)entity).getCurrentEquippedItem();

			if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
				Block block = this.mc.theWorld.getBlockState(blockpos).getBlock();

				if (this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
					result = block.hasTileEntity() && this.mc.theWorld.getTileEntity(blockpos) instanceof IInventory;
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
		float f = 0.002F;
		BlockPos blockpos = event.movingObjectPosition.getBlockPos();
		Block block = mc.theWorld.getBlockState(blockpos).getBlock();

		if(block.getMaterial() != Material.air && mc.theWorld.getWorldBorder().contains(blockpos)) {
			block.setBlockBoundsBasedOnState(mc.theWorld, blockpos);
			double d0 = mc.getRenderViewEntity().lastTickPosX
					+ (mc.getRenderViewEntity().posX - mc.getRenderViewEntity().lastTickPosX) * (double) event.partialTicks;
			double d1 = mc.getRenderViewEntity().lastTickPosY
					+ (mc.getRenderViewEntity().posY - mc.getRenderViewEntity().lastTickPosY) * (double) event.partialTicks;
			double d2 = mc.getRenderViewEntity().lastTickPosZ
					+ (mc.getRenderViewEntity().posZ - mc.getRenderViewEntity().lastTickPosZ) * (double) event.partialTicks;
			AxisAlignedBB axisalignedbb = block.getSelectedBoundingBox(mc.theWorld, blockpos);
			Block.EnumOffsetType block$enumoffsettype = block.getOffsetType();

			axisalignedbb = axisalignedbb.expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D)
					.offset(-d0, -d1, -d2);

			if(fill) {
				GL11.glColor4ub((byte) fillColour.getRed(), (byte) fillColour.getGreen(), (byte) fillColour.getBlue(),
						(byte) fillColour.getAlpha());

				GlStateManager.disableCull();
				Tessellator tessellator = Tessellator.getInstance();
				WorldRenderer worldrenderer = tessellator.getWorldRenderer();

				worldrenderer.begin(6, DefaultVertexFormats.POSITION);
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
				tessellator.draw();

				worldrenderer.begin(6, DefaultVertexFormats.POSITION);
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
				tessellator.draw();

				worldrenderer.begin(6, DefaultVertexFormats.POSITION);
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
				tessellator.draw();

				worldrenderer.begin(6, DefaultVertexFormats.POSITION);
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
				tessellator.draw();

				worldrenderer.begin(6, DefaultVertexFormats.POSITION);
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
				tessellator.draw();

				worldrenderer.begin(6, DefaultVertexFormats.POSITION);
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
				worldrenderer.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
				worldrenderer.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
				tessellator.draw();


				GlStateManager.enableCull();
			}

			if(outline) {
				GL11.glColor4ub((byte) outlineColour.getRed(), (byte) outlineColour.getGreen(), (byte) outlineColour.getBlue(),
						(byte) outlineColour.getAlpha());
				GL11.glLineWidth(outlineWidth);

				RenderGlobal.drawSelectionBoundingBox(axisalignedbb);
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();

		GlStateManager.disableBlend();

		if(!depth) {
			GlStateManager.enableDepth();
		}
	}

}
