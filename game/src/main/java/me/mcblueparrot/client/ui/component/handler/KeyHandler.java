package me.mcblueparrot.client.ui.component.handler;

import me.mcblueparrot.client.ui.component.ComponentRenderInfo;

@FunctionalInterface
public interface KeyHandler {

	boolean keyPressed(ComponentRenderInfo info, int keyCode, char character);

}
