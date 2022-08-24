package io.github.solclient.client.v1_8_9.mixins.platform.mc.option;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;

@Mixin(net.minecraft.client.options.KeyBinding.class)
@Implements(@Interface(iface = KeyBinding.class, prefix = "platform$"))
public abstract class KeyBindingImpl {

	public @NotNull String platform$getKeyCategory() {
		return getCategory();
	}

	@Shadow
	public abstract String getCategory();

	public @NotNull String platform$getName() {
		return getTranslationKey();
	}

	@Shadow
	public abstract String getTranslationKey();

	public @Nullable String platform$getBoundKeyName() {
		return GameOptions.getFormattedNameForKeyCode(getCode());
	}

	public int platform$getKeyCode() {
		return getCode();
	}

	@Shadow
	public abstract int getCode();

	public boolean platform$isHeld() {
		return isPressed();
	}

	@Shadow
	public abstract boolean isPressed();

	public boolean platform$consumePress() {
		return wasPressed();
	}

	@Shadow
	public abstract boolean wasPressed();

	private Stream<net.minecraft.client.options.KeyBinding> conflictingStream() {
		return Arrays.stream(MinecraftClient.getInstance().options.keysAll)
				.filter((key) -> key != (Object) this && key.getCode() == getCode());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public @NotNull List<KeyBinding> platform$getConflictingKeys() {
		if(getCode() == 0) {
			return Collections.emptyList();
		}

		return (List) conflictingStream().collect(Collectors.toList());
	}

	public boolean platform$conflicts() {
		return conflictingStream().findAny().isPresent();
	}

}

@Mixin(KeyBinding.class)
interface KeyBindingImpl$Static {

	@Overwrite(remap = false)
	static @NotNull KeyBinding create(@NotNull String name, int initialKey, @NotNull String category) {
		return (KeyBinding) new net.minecraft.client.options.KeyBinding(name, initialKey, category);
	}

}
