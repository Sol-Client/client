package io.github.solclient.client.v1_19_2.mixins.platform.mc.lang;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.mc.lang.I18n;
import lombok.experimental.UtilityClass;

@UtilityClass
@Mixin(I18n.class)
public class I18nImpl {

	@Overwrite(remap = false)
	public @NotNull String translate(@NotNull String key) {
		return net.minecraft.client.resource.language.I18n.translate(key);
	}

	@Overwrite(remap = false)
	public @NotNull String translate(@NotNull String key, @NotNull Object... values) {
		return net.minecraft.client.resource.language.I18n.translate(key, values);
	}

	@Overwrite(remap = false)
	public @NotNull Optional<String> translateOpt(@NotNull String key) {
		return translateOpt(key, new Object[0]);
	}

	@Overwrite(remap = false)
	public @NotNull Optional<String> translateOpt(@NotNull String key, @NotNull Object... values) {
		return net.minecraft.client.resource.language.I18n.hasTranslation(key)
				? Optional.of(net.minecraft.client.resource.language.I18n.translate(key, values))
				: Optional.empty();
	}

}
