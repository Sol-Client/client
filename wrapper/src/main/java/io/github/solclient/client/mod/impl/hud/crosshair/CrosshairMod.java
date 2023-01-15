package io.github.solclient.client.mod.impl.hud.crosshair;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.HudMod;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult.Type;
import net.minecraft.world.level.LevelInfo.GameMode;

public class CrosshairMod extends HudMod {

	private static final Identifier CLIENT_CROSSHAIRS = new Identifier("textures/gui" + "/sol_client_crosshairs.png");

	@Expose
	@Option
	private CrosshairStyle style = CrosshairStyle.DEFAULT;
	@Expose
	@Option
	private boolean thirdPerson = true;
	@Expose
	@Option
	private boolean spectatorAlways = false;
	@Expose
	@Option
	private boolean debug = false;
	@Expose
	@Option
	private boolean blending = true;
	@Expose
	@Option
	private Colour crosshairColour = Colour.WHITE;
	@Expose
	@Option
	private boolean highlightEntities = false;
	@Expose
	@Option
	private Colour entityColour = Colour.PURE_RED;

	@Override
	public String getId() {
		return "crosshair";
	}

	@EventHandler
	public void onCrosshairRender(PreGameOverlayRenderEvent event) {
		if (event.type == GameOverlayElement.CROSSHAIRS) {
			event.cancelled = true;
			if ((!debug && mc.options.debugEnabled)
					|| (!spectatorAlways && (mc.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR
							&& mc.result.type != Type.ENTITY))
					|| (!thirdPerson && mc.options.perspective != 0)) {
				return;
			}
			Window window = new Window(mc);

			GlStateManager.pushMatrix();
			GlStateManager.scale(getScale(), getScale(), getScale());
			GlStateManager.translate((int) (window.getScaledWidth() / getScale() / 2 - 7),
					(int) (window.getScaledHeight() / getScale() / 2 - 7), 0);

			crosshairColour.bind();

			GlStateManager.enableBlend();
			GlStateManager.enableAlphaTest();
			GlStateManager.blendFuncSeparate(770, 771, 1, 0);

			if (highlightEntities && mc.result != null && mc.result.entity != null
					&& !(mc.result.entity.isInvisible() || mc.result.entity.isInvisibleTo(mc.player)))
				entityColour.bind();
			else if (blending) {
				GlStateManager.blendFuncSeparate(775, 769, 1, 0);
				GlStateManager.enableAlphaTest();
			}

			if (style == CrosshairStyle.DEFAULT) {
				mc.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
				Utils.drawTexture(0, 0, 0, 0, 16, 16, 0);
			} else {
				mc.getTextureManager().bindTexture(CLIENT_CROSSHAIRS);
				int v = (style.ordinal() - 2) * 16;
				Utils.drawTexture(0, 0, 0, v, 16, 16, 0);
				mc.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
			}

			GlStateManager.color(1, 1, 1, 1);

			GlStateManager.popMatrix();
		}
	}

}
