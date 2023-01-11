package io.github.solclient.client.mixin.client;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.extension.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.*;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen implements GuiScreenExtension {

	@Override
	public boolean canBeForceClosed() {
		return true;
	}

	@Redirect(method = "drawWorldBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawGradientRect(IIIIII)V"))
	public void getTopColour(GuiScreen guiScreen, int left, int top, int right, int bottom, int startColor,
			int endColor) {
		if (!Client.INSTANCE.bus.post(new RenderGuiBackgroundEvent()).cancelled) {
			Utils.drawGradientRect(left, top, right, bottom, startColor, endColor);
		} else {
			Utils.drawGradientRect(left, top, right, bottom, 0, 0);
		}
	}

	@Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;mousePressed(Lnet/minecraft/client/Minecraft;II)Z"))
	public boolean onActionPerformed(GuiButton instance, Minecraft mc, int mouseX, int mouseY) {
		return instance.mousePressed(mc, mouseX, mouseY)
				&& !Client.INSTANCE.bus.post(new ActionPerformedEvent((GuiScreen) (Object) this, instance)).cancelled;
	}

	@Redirect(method = "setWorldAndResolution", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui"
			+ "/GuiScreen;initGui()V"))
	public void guiInit(GuiScreen instance) {
		if (!Client.INSTANCE.bus.post(new PreGuiInitEvent(instance)).cancelled) {
			instance.initGui();
			Client.INSTANCE.bus.post(new PostGuiInitEvent(instance, buttonList));
		}
	}

	@Inject(method = "drawScreen", at = @At("HEAD"))
	public void preGuiRender(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PreGuiRenderEvent(partialTicks));
	}

	@Inject(method = "drawScreen", at = @At("RETURN"))
	public void postGuiRender(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
		GlStateManager.color(1, 1, 1, 1); // Prevent colour from leaking
		Client.INSTANCE.bus.post(new PostGuiRenderEvent(partialTicks));

		if (SolClientMod.instance.logoInInventory && (Object) this instanceof GuiContainer) {
			GlStateManager.enableBlend();

			mc.getTextureManager().bindTexture(
					new ResourceLocation("textures/gui/sol_client_logo_with_text_" + Utils.getTextureScale() + ".png"));

			Gui.drawModalRectWithCustomSizedTexture(width - 140, height - 40, 0, 0, 128, 32, 128, 32);
		}
	}

	@Redirect(method = "handleInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;"
			+ "handleMouseInput()V"))
	public void handleMouseInput(GuiScreen instance) throws IOException {
		if (!Client.INSTANCE.bus.post(new PreGuiMouseInputEvent()).cancelled) {
			instance.handleMouseInput();
		}
	}

	// Fix options not saving when "esc" is pressed.
	@Redirect(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen"
			+ "(Lnet/minecraft/client/gui/GuiScreen;)V"))
	public void saveFirst(Minecraft instance, GuiScreen screen) throws IOException {
		for (GuiButton button : buttonList) {
			if (button.displayString.equals(I18n.format("gui.done"))) {
				actionPerformed(button);
			}
		}
		instance.displayGuiScreen(null);
	}

	@Overwrite
	public void openWebLink(URI uri) {
		Utils.openUrl(uri.toString());
	}

	@Inject(method = "handleComponentClick", at = @At("HEAD"), cancellable = true)
	public void callReceiver(IChatComponent component, CallbackInfoReturnable<Boolean> callback) {
		if (component == null)
			return;

		ClickEventExtension event = ClickEventExtension.from(component.getChatStyle().getChatClickEvent());
		if (event == null || event.getReceiver() == null)
			return;

		event.getReceiver().run();
		callback.setReturnValue(true);
	}

	@Shadow
	protected List<GuiButton> buttonList;

	@Shadow
	public int width;

	@Shadow
	public int height;

	@Shadow
	protected abstract void actionPerformed(GuiButton button) throws IOException;

	@Shadow
	protected Minecraft mc;

}
