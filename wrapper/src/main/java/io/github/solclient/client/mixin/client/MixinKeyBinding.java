package io.github.solclient.client.mixin.client;

import java.util.*;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.util.extension.KeyBindingExtension;
import lombok.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements KeyBindingExtension {

	@Getter
	@Setter
	private int mods;

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/IntHashMap;addKey(ILjava/lang/Object;)V"))
	public void rewireAdd(IntHashMap<?> instance, int key, Object value) {
		add((KeyBinding) (Object) this);
	}

	@Overwrite
	@SuppressWarnings("unchecked")
	public static void onTick(int keyCode) {
		if (keyCode != 0) {
			List<KeyBindingExtension> keybindings = (List<KeyBindingExtension>) (Object) hash.lookup(keyCode);
			if (keybindings == null)
				return;

			for (KeyBindingExtension keybinding : keybindings) {
				if (!keybinding.areModsPressed())
					continue;

				keybinding.increasePressTime();
			}
		}
	}

	@Overwrite
	@SuppressWarnings("unchecked")
	public static void setKeyBindState(int keyCode, boolean pressed) {
		if (keyCode != 0) {
			List<KeyBindingExtension> keybindings = (List<KeyBindingExtension>) (Object) hash.lookup(keyCode);
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
	public static void resetKeyBindingArrayAndHash() {
		hash.clearMap();

		for (KeyBinding keybinding : keybindArray)
			add(keybinding);
	}

	private static void add(KeyBinding keybinding) {
		List<KeyBinding> existing = hash.lookup(keybinding.getKeyCode());
		if (existing == null) {
			LinkedList<KeyBinding> list = new LinkedList<>();
			list.add(keybinding);
			hash.addKey(keybinding.getKeyCode(), list);
		} else
			existing.add(keybinding);
	}

	@Override
	public void increasePressTime() {
		pressTime++;
	}

	@Accessor
	public abstract void setPressed(boolean pressed);

	// haha naughty!
	@Shadow
	private static @Final IntHashMap<List<KeyBinding>> hash;
	@Shadow
	private static @Final List<KeyBinding> keybindArray;

	@Shadow
	private int pressTime;

}
