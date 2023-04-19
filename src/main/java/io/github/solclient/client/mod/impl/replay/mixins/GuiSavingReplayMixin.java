package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import com.replaymod.recording.gui.GuiSavingReplay;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import io.github.solclient.client.mod.impl.replay.fix.SCSettingsRegistry;
import net.minecraft.client.MinecraftClient;

@Mixin(GuiSavingReplay.class)
public class GuiSavingReplayMixin {

	@Redirect(method = "presentRenameDialog", at = @At(value = "INVOKE", target = "Lio/github/solclient/client/mod/impl/replay/fix"
			+ "/SCSettingsRegistry;get(Lio/github/solclient/client/mod/impl/replay/fix/SCSettingsRegistry$SettingKey;)"
			+ "Ljava/lang/Object;"), remap = false)
	public Object saveAnyway(SCSettingsRegistry instance, SCSettingsRegistry.SettingKey settingKey) {
		return SCReplayMod.instance.renameDialog && SCReplayMod.enabled
				&& !(SCReplayMod.deferedState == Boolean.FALSE && MinecraftClient.getInstance().world == null);
	}

}
