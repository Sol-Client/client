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
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.extension.KeyBindingExtension;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.client.gui.screen.options.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Formatting;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public class KeyBindingEntryMixin {

	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;message:Ljava/lang/String;", ordinal = 0, shift = Shift.AFTER))
	public void addModifiersToLabel(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
			boolean isSelected, CallbackInfo callback) {
		keyBindingButton.message = KeyBindingExtension.from(keyBinding).getPrefix() + keyBindingButton.message;
	}

	@Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;active:Z"))
	public void resetEnabledWithMods(ButtonWidget instance, boolean modifiedKey) {
		instance.active = modifiedKey || KeyBindingExtension.from(keyBinding).getMods() != 0;
	}

	// :'(
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;getCode()I", ordinal = 2))
	public int disableConflictChecking(KeyBinding instance) {
		return 0;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/MinecraftClient;II)V"))
	public void checkConflicts(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
			boolean isSelected, CallbackInfo callback) {
		if (MinecraftUtils.isConflicting(this.keyBinding))
			keyBindingButton.message = Formatting.RED + keyBindingButton.message;
	}

	@Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;setKeyBindingCode(Lnet/minecraft/client/option/KeyBinding;I)V"))
	public void reset(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_,
			int p_148278_6_, CallbackInfoReturnable<Boolean> callback) {
		KeyBindingExtension.from(keyBinding).setMods(0);
	}

	@Shadow
	private @Final KeyBinding keyBinding;
	@Shadow
	private @Final ButtonWidget keyBindingButton;

}
