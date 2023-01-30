package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.client.gui.widget.ListWidget;

@Mixin(ResourcePackListWidget.class)
public abstract class ResourcePackListWidgetMixin extends ListWidget {

	public ResourcePackListWidgetMixin(MinecraftClient client, int width, int height, int top, int bottom,
			int entryHeight) {
		super(client, width, height, top, bottom, entryHeight);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	public void overrideTop(CallbackInfo callback) {
		yStart += 16;
		height -= 16;
		setHeader(false, 0);
	}

}
