package io.github.solclient.client.v1_19_2.mixins.platform.mc.lang;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.lang.LanguageManager;

@Mixin(net.minecraft.client.resource.language.LanguageManager.class)
@Implements(@Interface(iface = LanguageManager.class, prefix = "platform$"))
public class LanguageManagerImpl {

	public @Nullable String platform$getCode() {
		return currentLanguageCode;
	}

	@Shadow
	private String currentLanguageCode;

}

