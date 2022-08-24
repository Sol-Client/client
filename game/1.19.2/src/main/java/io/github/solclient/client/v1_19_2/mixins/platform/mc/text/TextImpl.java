package io.github.solclient.client.v1_19_2.mixins.platform.mc.text;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.util.Utils;

@Mixin(net.minecraft.text.Text.class)
public interface TextImpl extends Text {

}

@Mixin(Text.class)
interface TextImpl$Static {

	@Overwrite(remap = false)
	static @NotNull Text literal(@NotNull String text) {
		return (Text) net.minecraft.text.Text.literal(text);
	}

	@Overwrite(remap = false)
	static @NotNull Text format(@NotNull String fmt, Object... args) {
		return (Text) net.minecraft.text.Text.literal(Utils.format(fmt, args));
	}

	@Overwrite(remap = false)
	static @NotNull Text translation(String key, Object... args) {
		return (Text) net.minecraft.text.Text.translatable(key, args);
	}

}