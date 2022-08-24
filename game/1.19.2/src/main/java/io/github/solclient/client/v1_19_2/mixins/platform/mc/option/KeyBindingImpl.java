package io.github.solclient.client.v1_19_2.mixins.platform.mc.option;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(net.minecraft.client.option.KeyBinding.class)
@Implements(@Interface(iface = KeyBinding.class, prefix = "platform$"))
public abstract class KeyBindingImpl {

	public @NotNull String platform$getKeyCategory() {
		return category;
	}

	@Shadow
	private @Final String category;

	public @NotNull String platform$getName() {
		return translationKey;
	}

	@Shadow
	private @Final String translationKey;

	public @Nullable String platform$getBoundKeyName() {
		return Formatting.strip(getBoundKeyLocalizedText().getString());
	}

	@Shadow
	public abstract Text getBoundKeyLocalizedText();

	public int platform$getKeyCode() {
		return boundKey.getCode();
	}

	@Shadow
	private InputUtil.Key boundKey;

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

	@SuppressWarnings("resource")
	private Stream<net.minecraft.client.option.KeyBinding> conflictingStream() {
		return Arrays.stream(MinecraftClient.getInstance().options.allKeys)
				.filter((key) -> key != (Object) this && ((KeyBinding) key).getKeyCode() == platform$getKeyCode());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public @NotNull List<KeyBinding> platform$getConflictingKeys() {
		if(platform$getKeyCode() == 0) {
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
		return (KeyBinding) new net.minecraft.client.option.KeyBinding(name, initialKey, category);
	}

}
