/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mixin.client;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.extension.*;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(Screen.class)
public abstract class ScreenMixin implements ScreenExtension {

	@Override
	public boolean canBeForceClosed() {
		return true;
	}

	@Redirect(method = "renderBackground(I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient(IIIIII)V"))
	public void getTopColour(Screen screen, int left, int top, int right, int bottom, int startColor, int endColor) {
		if (!Client.INSTANCE.getEvents().post(new RenderGuiBackgroundEvent()).cancelled)
			MinecraftUtils.drawGradientRect(left, top, right, bottom, startColor, endColor);
		else
			MinecraftUtils.drawGradientRect(left, top, right, bottom, 0, 0);
	}

	@Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;isMouseOver(Lnet/minecraft/client/MinecraftClient;II)Z"))
	public boolean onActionPerformed(ButtonWidget instance, MinecraftClient mc, int mouseX, int mouseY) {
		return instance.isMouseOver(mc, mouseX, mouseY) && !Client.INSTANCE.getEvents()
				.post(new ActionPerformedEvent((Screen) (Object) this, instance)).cancelled;
	}

	@Redirect(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init()V"))
	public void init(Screen instance) {
		if (!Client.INSTANCE.getEvents().post(new PreGuiInitEvent(instance)).cancelled) {
			instance.init();
			Client.INSTANCE.getEvents().post(new PostGuiInitEvent(instance, buttons));
		}
	}

	@Inject(method = "render", at = @At("HEAD"))
	public void preGuiRender(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
		Client.INSTANCE.getEvents().post(new PreGuiRenderEvent(partialTicks));
	}

	@Inject(method = "render", at = @At("RETURN"))
	public void postGuiRender(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
		GlStateManager.color(1, 1, 1, 1); // Prevent colour from leaking
		Client.INSTANCE.getEvents().post(new PostGuiRenderEvent(partialTicks));

		if (SolClientConfig.instance.logoInInventory && (Object) this instanceof HandledScreen) {
			GlStateManager.enableBlend();

			client.getTextureManager().bindTexture(
					new Identifier("sol_client", "textures/gui/sol_client_logo_with_text_" + MinecraftUtils.getTextureScale() + ".png"));

			DrawableHelper.drawTexture(width - 140, height - 40, 0, 0, 128, 32, 128, 32);
		}
	}

	@Redirect(method = "handleInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;handleMouse()V"))
	public void handleMouseInput(Screen instance) throws IOException {
		if (!Client.INSTANCE.getEvents().post(new PreGuiMouseInputEvent()).cancelled)
			instance.handleMouse();
	}

	// Fix options not saving when "esc" is pressed.
	@Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
	public void saveFirst(MinecraftClient instance, Screen screen) throws IOException {
		for (ButtonWidget button : buttons)
			if (button.message != null && button.message.equals(I18n.translate("gui.done")))
				buttonClicked(button);

		instance.setScreen(null);
	}

	@Overwrite
	public void openLink(URI uri) {
		MinecraftUtils.openUrl(uri.toString());
	}

	@Inject(method = "handleTextClick", at = @At("HEAD"), cancellable = true)
	public void callReceiver(Text text, CallbackInfoReturnable<Boolean> callback) {
		if (text == null)
			return;

		ClickEventExtension event = ClickEventExtension.from(text.getStyle().getClickEvent());
		if (event == null || event.getReceiver() == null)
			return;

		event.getReceiver().run();
		callback.setReturnValue(true);
	}

	@Shadow
	public int width;

	@Shadow
	public int height;

	@Shadow
	protected abstract void buttonClicked(ButtonWidget button) throws IOException;

	@Shadow
	protected MinecraftClient client;

	@Shadow
	protected @Final List<ButtonWidget> buttons;

}
