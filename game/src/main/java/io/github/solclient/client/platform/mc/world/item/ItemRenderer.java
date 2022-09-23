package io.github.solclient.client.platform.mc.world.item;

import io.github.solclient.client.platform.mc.text.Font;

public interface ItemRenderer {

	void render(ItemStack item, int x, int y);

	void renderOverlays(Font font, ItemStack item, int x, int y);

}
