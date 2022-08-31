package io.github.solclient.client.v1_19_2.mixins.platform.mc.text;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.text.MutableText;
import io.github.solclient.client.platform.mc.text.Style;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.util.Utils;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Formatting;

@Mixin(net.minecraft.text.Text.class)
@Implements(@Interface(iface = Text.class, prefix = "platform$"))
public interface TextImpl extends StringVisitable {

	@NotNull
	@SuppressWarnings({ "rawtypes", "unchecked" })
	default List<Text> platform$getSiblings() {
		return (List) getSiblings();
	}

	@Shadow
	List<net.minecraft.text.Text> getSiblings();

	default @NotNull Text platform$clone() {
		return (Text) copy();
	}

	@Shadow
	net.minecraft.text.MutableText copy();

	default @NotNull String platform$getPlain() {
		return Formatting.strip(getString());
	}

	default @NotNull Style platform$getStyle() {
		return (Style) getStyle();
	}

	@Shadow
	net.minecraft.text.Style getStyle();

}

@Mixin(Text.class)
interface TextImpl$Static {

	@Overwrite(remap = false)
	static @NotNull MutableText literal(@NotNull String text) {
		return (MutableText) net.minecraft.text.Text.literal(text);
	}

	@Overwrite(remap = false)
	static @NotNull MutableText format(@NotNull String fmt, Object... args) {
		return (MutableText) net.minecraft.text.Text.literal(Utils.format(fmt, args));
	}

	@Overwrite(remap = false)
	static @NotNull MutableText translation(String key, Object... args) {
		return (MutableText) net.minecraft.text.Text.translatable(key, args);
	}

}