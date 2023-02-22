package io.github.solclient.client.mod.impl.hud;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.util.Window;
import net.minecraft.entity.boss.BossBar;

public final class BossBarMod extends SolClientHudMod {

	@Expose
	@Option
	private boolean hide;
	@Expose
	@Option
	private boolean text = true;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private Colour textColour = Colour.WHITE;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean shadow = true;
	@Expose
	@Option
	private boolean bar = true;
	@Expose
	@Option
	@Slider(min = 0, max = 15, step = 1, format = "sol_client.slider.pixels")
	private int offset = 2;

	@Override
	public String getId() {
		return "boss_bar";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.HUD;
	}

	@EventHandler
	public void onBossBarRender(PreGameOverlayRenderEvent event) {
		if (event.type != GameOverlayElement.BOSSHEALTH)
			return;
		event.cancelled = true;

		if (hide)
			return;

		if (BossBar.name == null || BossBar.framesToLive <= 0)
			return;

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, offset, 0);
		GlStateManager.scale(getScale(), getScale(), 0);

		Window window = new Window(mc);

		int barWidth = 182;
		int barX = (int) (window.getWidth() / getScale() / 2 - 91);
		int barY = 0;
		int fillWidth = (int) (BossBar.percent * (barWidth + 1));

		if (text)
			barY += font.fontHeight + 1;

		if (bar) {
			for (int i = 0; i < 2; i++)
				MinecraftUtils.drawTexture(barX, barY, 0, 74, barWidth, 5, 0);

			if (fillWidth > 0)
				MinecraftUtils.drawTexture(barX, barY, 0, 79, fillWidth, 5, 0);
		}

		// prevent texture bleeding
		if (text)
			font.draw(BossBar.name, window.getWidth() / getScale() / 2 - font.getStringWidth(BossBar.name) / 2, 0,
					textColour.getValue(), shadow);

		GlStateManager.popMatrix();
	}

}
