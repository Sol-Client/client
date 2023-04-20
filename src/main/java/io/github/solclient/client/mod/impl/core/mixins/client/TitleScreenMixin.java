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

package io.github.solclient.client.mod.impl.core.mixins.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import io.github.solclient.client.ui.ReplayButton;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.ActiveMainMenu;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

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

}
