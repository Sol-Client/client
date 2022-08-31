package io.github.solclient.client.v1_19_2.mixins.platform.mc.text;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.VirtualEnum;
import io.github.solclient.client.platform.mc.text.TextColour;
import io.github.solclient.client.platform.mc.text.TextFormatting;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

@Mixin(Formatting.class)
public class TextFormattingImpl implements TextFormatting {

	@Override
	public String getName() {
		return toEnum().name();
	}

	@Override
	public int getOrdinal() {
		return toEnum().ordinal();
	}

	@Override
	public Enum<?> toEnum() {
		return (Enum<?>) (Object) this;
	}

	@Override
	public VirtualEnum[] getValues() {
		return (VirtualEnum[]) (Object) Formatting.values();
	}

	@Override
	public Enum<?>[] getEnumValues() {
		return Formatting.values();
	}

	@Override
	public @Nullable TextColour getColour() {
		return (TextColour) (Object) TextColor.fromFormatting((Formatting) (Object) this);
	}
}

@Mixin(TextFormatting.class)
interface TextFormattingImpl$Static {

	@Overwrite(remap = false)
	static String strip(String message) {
		return Formatting.strip(message);
	}

}