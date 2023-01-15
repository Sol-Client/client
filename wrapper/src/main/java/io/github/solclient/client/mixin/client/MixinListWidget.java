package io.github.solclient.client.mixin.client;

import net.minecraft.client.gui.widget.ListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ListWidget.class)
public class MixinListWidget {

	// just a tweak to make scrolling a bit faster
	@ModifyConstant(method = "handleMouse", constant = @Constant(intValue = 2, ordinal = 4))
	public int getScrollDivisor(int original) {
		return 1;
	}

}
