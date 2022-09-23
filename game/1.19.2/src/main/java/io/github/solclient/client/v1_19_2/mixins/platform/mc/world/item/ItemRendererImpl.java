package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.item;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.world.item.ItemRenderer;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import net.minecraft.client.font.TextRenderer;

@Mixin(net.minecraft.client.render.item.ItemRenderer.class)
@Implements(@Interface(iface = ItemRenderer.class, prefix = "platform$"))
public abstract class ItemRendererImpl {

	public void platform$render(ItemStack item, int x, int y) {
		renderInGuiWithOverrides((net.minecraft.item.ItemStack) (Object) item, x, y);
	}

	@Shadow
	public abstract void renderInGuiWithOverrides(net.minecraft.item.ItemStack stack, int x, int y);

	public void platform$renderOverlays(Font font, ItemStack item, int x, int y) {
		renderGuiItemOverlay((TextRenderer) font, (net.minecraft.item.ItemStack) (Object) item, x, y);
	}

	@Shadow
	public abstract void renderGuiItemOverlay(TextRenderer renderer, net.minecraft.item.ItemStack stack, int x, int y);

}

