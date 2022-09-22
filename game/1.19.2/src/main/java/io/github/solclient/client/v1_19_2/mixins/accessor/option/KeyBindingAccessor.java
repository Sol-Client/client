package io.github.solclient.client.v1_19_2.mixins.accessor.option;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.option.KeyBinding;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {

	@Accessor("CATEGORY_ORDER_MAP")
	static Map<String, Integer> getCategoryOrderMap() {
		throw new UnsupportedOperationException();
	}

}
