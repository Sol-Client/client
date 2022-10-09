package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.item;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.world.item.ItemRenderer;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

@Mixin(net.minecraft.client.render.item.ItemRenderer.class)
@Implements(@Interface(iface = ItemRenderer.class, prefix = "platform$"))
public abstract class ItemRendererImpl {

	@SuppressWarnings("resource")
	public void platform$render(ItemStack item, int x, int y) {
		renderInGuiWithOverrides((net.minecraft.item.ItemStack) (Object) item, x, y);
		renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, (net.minecraft.item.ItemStack) (Object) item, x, y);
	}

	@Shadow
	public abstract void renderInGuiWithOverrides(net.minecraft.item.ItemStack stack, int x, int y);

	@Shadow
	public abstract void renderGuiItemOverlay(TextRenderer renderer, net.minecraft.item.ItemStack stack, int x, int y);

}

