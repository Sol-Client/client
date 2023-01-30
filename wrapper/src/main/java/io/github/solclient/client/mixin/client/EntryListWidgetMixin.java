package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.*;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin extends ListWidget {

	public EntryListWidgetMixin(MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
		super(client, width, height, top, bottom, entryHeight);
	}

	@Inject(method = "renderEntry", at = @At("HEAD"), cancellable = true)
	public void cullEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY, CallbackInfo callback) {
		if (y + rowHeight < yStart)
			callback.cancel();
		else if (y > yEnd)
			callback.cancel();
	}

}
