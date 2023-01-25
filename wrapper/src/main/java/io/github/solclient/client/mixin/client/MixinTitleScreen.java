package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

import io.github.solclient.client.extension.TitleScreenExtension;
import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import io.github.solclient.client.ui.ReplayButton;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.ActiveMainMenu;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen implements TitleScreenExtension {

	@Inject(method = "<init>", at = @At("RETURN"))
	public void setActiveMainMenu(CallbackInfo callback) {
		ActiveMainMenu.setInstance((TitleScreen) (Object) this);
	}

	@Inject(method = "initWidgetsNormal", at = @At("RETURN"))
	public void getModsButton(int x, int y, CallbackInfo callback) {
		buttons.remove(realmsButton);
		buttons.add(new ButtonWidget(realmsButton.id, realmsButton.x, realmsButton.y,
				I18n.translate("sol_client.mod.screen.title")));

		if (SCReplayMod.enabled)
			buttons.add(new ReplayButton(15, realmsButton.x + 202, realmsButton.y));
	}

	@Redirect(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;switchToRealms()V"))
	public void openModsMenu(TitleScreen instance) {
		client.setScreen(new ModsScreen());
	}

	@Inject(method = "buttonClicked", at = @At("RETURN"))
	public void openReplayMenu(ButtonWidget button, CallbackInfo callback) {
		if (button.id == 15)
			new GuiReplayViewer(ReplayModReplay.instance).display();
	}

	@Shadow
	private ButtonWidget realmsButton;

	@Invoker("renderPanorama")
	@Override
	public abstract void drawPanorama(int mouseX, int mouseY, float partialTicks);

	@Invoker("transformPanorama")
	@Override
	public abstract void rotateAndBlurPanorama(float partialTicks);

}
