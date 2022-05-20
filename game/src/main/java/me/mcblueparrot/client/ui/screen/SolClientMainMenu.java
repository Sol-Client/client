package me.mcblueparrot.client.ui.screen;

import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.mod.impl.replay.SCReplayMod;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.Screen;
import me.mcblueparrot.client.ui.component.controller.AnimatedColourController;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.ui.component.impl.ButtonComponent;
import me.mcblueparrot.client.ui.component.impl.ButtonType;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.access.AccessGuiMainMenu;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import me.mcblueparrot.client.util.font.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class SolClientMainMenu extends Screen {

	private GuiMainMenu base;

	public SolClientMainMenu(GuiMainMenu base) {
		super(new MainMenuComponent());
		this.base = base;
		background = false;
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		base.setWorldAndResolution(mc, width, height);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawPanorama(mouseX, mouseY, partialTicks);

		Font font = SolClientMod.getFont();

		String copyrightString = "Copyright Mojang AB. Do not distribute!";
		font.renderString(copyrightString, (int) (width - font.getWidth(copyrightString) - 10), height - 15, -1);
		String versionString = "Minecraft 1.8.9";
		font.renderString(versionString, (int) (width - font.getWidth(versionString) - 10), height - 25, -1);

		font.renderString("Copyright TheKodeToad and contributors.", 10, height - 15, -1);
		font.renderString(Client.NAME, 10, height - 25, -1);

		mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_logo_with_text_" +
						Utils.getTextureScale() + ".png"));
		Gui.drawModalRectWithCustomSizedTexture(width / 2 - 64, 50, 0, 0, 128, 32, 128, 32);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawPanorama(int mouseX, int mouseY, float partialTicks) {
		AccessGuiMainMenu access = (AccessGuiMainMenu) (Object) base;

		this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        access.renderPanorama(mouseX, mouseY, partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        access.rotateAndBlurPanorama(partialTicks);
        mc.getFramebuffer().bindFramebuffer(true);

        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);

        float uvBase = width > height ? 120.0F / width : 120.0F / height;
        float uBase = height * uvBase / 256.0F;
        float vBase = width * uvBase / 256.0F;

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		renderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		renderer.pos(0.0D, height, zLevel).tex((0.5F - uBase), (0.5F + vBase)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		renderer.pos(width, height, zLevel).tex(0.5F - uBase, 0.5F - vBase).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		renderer.pos(width, 0.0D, zLevel).tex(0.5F + uBase, 0.5F - vBase).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		renderer.pos(0.0D, 0.0D, zLevel).tex(0.5F + uBase, 0.5F + vBase).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();

		drawRect(0, 0, width, height, new Colour(0, 0, 0, 100).getValue());

		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		base.updateScreen();
	}

	private static class MainMenuComponent extends Component {

		private int buttonsX;

		public MainMenuComponent() {
			Controller<Colour> defaultColourController =
					(component, defaultColour) -> component.isHovered() ? SolClientMod.instance.uiHover
							: SolClientMod.instance.uiColour;

			add(new ButtonComponent((component, defaultText) -> "Singleplayer",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_player")
							.type(ButtonType.LARGE).onClick((info, button) -> {
								if (button == 0) {
									Utils.playClickSound(true);
									mc.displayGuiScreen(new GuiSelectWorld(screen));
									return true;
								}

								return false;
							}),
					(component, defaultBounds) -> new Rectangle(screen.width / 2 - 100, screen.height / 4 + 48,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "Multiplayer", new AnimatedColourController(defaultColourController))
					.withIcon("sol_client_players").type(ButtonType.LARGE).onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new GuiMultiplayer(screen));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(screen.width / 2 - 100, screen.height / 4 + 73,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_language").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new GuiLanguage(screen, mc.gameSettings, mc.getLanguageManager()));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> {
						int buttonsCount = 3;

						if(SCReplayMod.enabled) {
							buttonsCount++;
						}

						buttonsX = screen.width / 2 - (12 * buttonsCount);

						return new Rectangle(buttonsX, screen.height / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight());
					});

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_settings_small").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new GuiOptions(screen, mc.gameSettings));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(buttonsX + 26, screen.height / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_mods").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new ModsScreen());
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(buttonsX + 52, screen.height / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_replay_button")
							.type(ButtonType.SMALL).onClick((info, button) -> {
								if(button == 0) {
									Utils.playClickSound(true);
									new GuiReplayViewer(ReplayModReplay.instance).display();
									return true;
								}

								return false;
							}).visibilityController((component, defaultVisibility) -> SCReplayMod.enabled),
					(component, defaultBounds) -> new Rectangle(buttonsX + 78, screen.height / 4 + 48 + 70,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(
							(component, defaultColour) -> component.isHovered() ? Colour.RED_HOVER : Colour.RED)).onClick((info, button) -> {
								if(button == 0) {
									Utils.playClickSound(true);
									mc.shutdown();
									return true;
								}

								return false;
							})
									.type(ButtonType.SMALL).withIcon("sol_client_exit"),
					(component, defaultBounds) -> new Rectangle(getBounds().getWidth() - 30, 10,
							defaultBounds.getWidth(), defaultBounds.getHeight()));
		}

	}

}
