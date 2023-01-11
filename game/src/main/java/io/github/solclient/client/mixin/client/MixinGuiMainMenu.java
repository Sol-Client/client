package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import io.github.solclient.client.ui.ReplayButton;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.ActiveMainMenu;
import io.github.solclient.client.util.extension.GuiMainMenuExtension;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;

@Mixin(GuiMainMenu.class)
public abstract class MixinGuiMainMenu extends GuiScreen implements GuiMainMenuExtension {

	@Inject(method = "<init>", at = @At("RETURN"))
	public void setActiveMainMenu(CallbackInfo callback) {
		ActiveMainMenu.setInstance((GuiMainMenu) (Object) this);
	}

	@Inject(method = "addSingleplayerMultiplayerButtons", at = @At("RETURN"))
	public void getModsButton(int x, int y, CallbackInfo callback) {
		buttonList.remove(realmsButton);
		buttonList.add(new GuiButton(realmsButton.id, realmsButton.xPosition, realmsButton.yPosition,
				I18n.format("sol_client.mod.screen.title")));

		if (SCReplayMod.enabled) {
			buttonList.add(new ReplayButton(15, realmsButton.xPosition + 202, realmsButton.yPosition));
		}
	}

	@Redirect(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiMainMenu;"
			+ "switchToRealms()V"))
	public void openModsMenu(GuiMainMenu guiMainMenu) {
		mc.displayGuiScreen(new ModsScreen());
	}

	@Inject(method = "actionPerformed", at = @At("RETURN"))
	public void openReplayMenu(GuiButton button, CallbackInfo callback) {
		if (button.id == 15) {
			new GuiReplayViewer(ReplayModReplay.instance).display();
		}
	}

	@Shadow
	private GuiButton realmsButton;

	@Invoker("drawPanorama")
	@Override
	public abstract void renderPanorama(int mouseX, int mouseY, float partialTicks);

	@Invoker("rotateAndBlurSkybox")
	@Override
	public abstract void rotateAndBlurPanorama(float partialTicks);

}
