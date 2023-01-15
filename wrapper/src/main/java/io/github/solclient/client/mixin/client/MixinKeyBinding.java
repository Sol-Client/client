package io.github.solclient.client.mixin.client;

import java.util.*;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.util.extension.KeyBindingExtension;
import lombok.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.collection.IntObjectStorage;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements KeyBindingExtension {

	@Getter
	@Setter
	private int mods;

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IntObjectStorage;set(ILjava/lang/Object;)V"))
	public void rewireAdd(IntObjectStorage<?> instance, int key, Object value) {
		add((KeyBinding) (Object) this);
	}

	@Overwrite
	@SuppressWarnings("unchecked")
	public static void onKeyPressed(int keyCode) {
		if (keyCode != 0) {
			List<KeyBindingExtension> keybindings = (List<KeyBindingExtension>) (Object) KEY_MAP.get(keyCode);
			if (keybindings == null)
				return;

			for (KeyBindingExtension keybinding : keybindings) {
				if (!keybinding.areModsPressed())
					continue;

				keybinding.increaseTimesPressed();
			}
		}
	}

	@Overwrite
	@SuppressWarnings("unchecked")
	public static void setKeyPressed(int keyCode, boolean pressed) {
		if (keyCode != 0) {
			List<KeyBindingExtension> keybindings = (List<KeyBindingExtension>) (Object) KEY_MAP.get(keyCode);
			if (keybindings == null)
				return;

			for (KeyBindingExtension keybinding : keybindings) {
				if (pressed && !keybinding.areModsPressed())
					continue;

				keybinding.setPressed(pressed);
			}
		}
	}

	@Overwrite
	public static void updateKeysByCode() {
		KEY_MAP.clear();

		for (KeyBinding keybinding : KEYS)
			add(keybinding);
	}

	private static void add(KeyBinding keybinding) {
		List<KeyBinding> existing = KEY_MAP.get(keybinding.getCode());
		if (existing == null) {
			LinkedList<KeyBinding> list = new LinkedList<>();
			list.add(keybinding);
			KEY_MAP.set(keybinding.getCode(), list);
		} else
			existing.add(keybinding);
	}

	@Override
	public void increaseTimesPressed() {
		timesPressed++;
	}

	@Accessor
	public abstract void setPressed(boolean pressed);

	// haha naughty!
	@Shadow
	private static @Final IntObjectStorage<List<KeyBinding>> KEY_MAP;
	@Shadow
	private static @Final List<KeyBinding> KEYS;

	@Shadow
	private int timesPressed;

}
