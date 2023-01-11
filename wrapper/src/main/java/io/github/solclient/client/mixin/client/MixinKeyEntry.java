package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.extension.KeyBindingExtension;
import net.minecraft.client.gui.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;

@Mixin(GuiKeyBindingList.KeyEntry.class)
public class MixinKeyEntry {

	@Inject(method = "drawEntry", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiButton;displayString:Ljava/lang/String;", ordinal = 0, shift = Shift.AFTER))
	public void addModifiersToLabel(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
			boolean isSelected, CallbackInfo callback) {
		btnChangeKeyBinding.displayString = KeyBindingExtension.from(keybinding).getPrefix()
				+ btnChangeKeyBinding.displayString;
	}

	@Redirect(method = "drawEntry", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiButton;enabled:Z"))
	public void resetEnabledWithMods(GuiButton instance, boolean modifiedKey) {
		instance.enabled = modifiedKey || KeyBindingExtension.from(keybinding).getMods() != 0;
	}

	// :'(
	@Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCode()I", ordinal = 2))
	public int disableConflictChecking(KeyBinding instance) {
		return 0;
	}

	@Inject(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;drawButton(Lnet/minecraft/client/Minecraft;II)V"))
	public void checkConflicts(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
			boolean isSelected, CallbackInfo callback) {
		if (Utils.isConflicting(this.keybinding))
			btnChangeKeyBinding.displayString = EnumChatFormatting.RED + btnChangeKeyBinding.displayString;
	}

	@Inject(method = "mousePressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;setOptionKeyBinding(Lnet/minecraft/client/settings/KeyBinding;I)V"))
	public void reset(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_,
			int p_148278_6_, CallbackInfoReturnable<Boolean> callback) {
		KeyBindingExtension.from(keybinding).setMods(0);
	}

	@Shadow
	private @Final KeyBinding keybinding;
	@Shadow
	private @Final GuiButton btnChangeKeyBinding;
	@Shadow
	private @Final GuiButton btnReset;

}
