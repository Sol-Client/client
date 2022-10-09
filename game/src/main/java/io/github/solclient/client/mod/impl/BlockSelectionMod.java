package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.world.level.BlockSelectionRenderEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.PrimaryIntegerSettingMod;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import io.github.solclient.client.platform.mc.raycast.HitResult;
import io.github.solclient.client.platform.mc.raycast.HitType;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.platform.mc.world.entity.Entity;
import io.github.solclient.client.platform.mc.world.entity.player.GameMode;
import io.github.solclient.client.platform.mc.world.entity.player.LocalPlayer;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import io.github.solclient.client.platform.mc.world.level.Level;
import io.github.solclient.client.platform.mc.world.level.block.BlockPos;
import io.github.solclient.client.platform.mc.world.level.block.BlockType;
import io.github.solclient.client.util.data.Colour;

public class BlockSelectionMod extends Mod implements PrimaryIntegerSettingMod {

	public static final BlockSelectionMod INSTANCE = new BlockSelectionMod();

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

	private boolean canRender(HitResult hit) {
		Entity entity = mc.getCameraEntity();

		if(!(entity instanceof LocalPlayer) || hit.getType() != HitType.BLOCK || hit.getBlockPos() != null
				|| mc.getOptions().hideGui()) {
			return false;
		}

		LocalPlayer player = (LocalPlayer) entity;
		ItemStack item = player.getInventory().getMainHand();
		Level level = mc.getLevel();

		if(!player.getAbilities().canBuild() && !persistent) {
			if(mc.getPlayerState().getGameMode() == GameMode.SPECTATOR) {
				return level.getBlockState(hit.getBlockPos()).hasMenu(mc.getLevel(), hit.getBlockPos());
			}
			else {
				return item != null && item.canDestroy(level, hit.getBlockPos()) || item.canPlaceOn(level, hit.getBlockPos());
			}
		}

		return true;
	}

	@EventHandler
	public void onBlockHighlightRenderEvent(BlockSelectionRenderEvent event) {
		event.cancel();

		if(!canRender(event.getHit())) {
			return;
		}

		GlStateManager.enableBlend();
		GlStateManager.blendFunction(770, 771, 1, 0);

		if(!depth) {
			GlStateManager.disableDepth();
		}

		GlStateManager.disableTexture2d();

		GlStateManager.depthMask(false);
		BlockPos pos = event.getHit().getBlockPos();
		BlockType block = mc.getLevel().getBlockState(pos).getType();

		if(block != BlockType.AIR && mc.getLevel().getWorldBorder().contains(pos)) {
			if(fill) {
				fillColour.bind();
				block.fillBox();
			}

			if(outline) {
				outlineColour.bind();
				GlStateManager.lineWidth(outlineWidth);
				block.strokeBox();
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2d();

		GlStateManager.disableBlend();

		if(!depth) {
			GlStateManager.enableDepth();
		}

		GlStateManager.resetLineWidth();
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
