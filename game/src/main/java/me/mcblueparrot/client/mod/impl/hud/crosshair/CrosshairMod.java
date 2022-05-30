package me.mcblueparrot.client.mod.impl.hud.crosshair;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.GameOverlayElement;
import me.mcblueparrot.client.event.impl.PreGameOverlayRenderEvent;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.hud.HudMod;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings.GameType;

public class CrosshairMod extends HudMod {

	private static final ResourceLocation CLIENT_CROSSHAIRS = new ResourceLocation("textures/gui" +
			"/sol_client_crosshairs.png");

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
		if(event.type == GameOverlayElement.CROSSHAIRS) {
			event.cancelled = true;
			if ((!debug && mc.gameSettings.showDebugInfo) ||
					(!spectatorAlways && (mc.playerController.getCurrentGameType() == GameType.SPECTATOR && mc.objectMouseOver.typeOfHit != MovingObjectType.ENTITY)) ||
					(!thirdPerson && mc.gameSettings.thirdPersonView != 0)) {
				return;
			}
			ScaledResolution resolution = new ScaledResolution(mc);
			int x = (int) (resolution.getScaledWidth() / getScale() / 2 - 7);
			int y = (int) (resolution.getScaledHeight() / getScale() / 2 - 7);

			GlStateManager.pushMatrix();
			GlStateManager.scale(getScale(), getScale(), getScale());

			Utils.glColour(crosshairColour);

			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

			if(highlightEntities && mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null && !(mc.objectMouseOver.entityHit.isInvisible()
					|| mc.objectMouseOver.entityHit.isInvisibleToPlayer(mc.thePlayer))) {
				Utils.glColour(entityColour);
			}
			else if(blending) {
				GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
				GlStateManager.enableAlpha();
			}

			if (style == CrosshairStyle.DEFAULT) {
				mc.getTextureManager().bindTexture(Gui.icons);
				Utils.drawTexture(x, y, 0, 0, 16, 16, 0);
			}
			else {
				mc.getTextureManager().bindTexture(CLIENT_CROSSHAIRS);
				int v = (style.ordinal() - 2) * 16;
				Utils.drawTexture(x, y, 0, v, 16, 16, 0);
				mc.getTextureManager().bindTexture(Gui.icons);
			}
			GL11.glColor4f(1, 1, 1, 1);

			GlStateManager.popMatrix();
		}
	}

}
