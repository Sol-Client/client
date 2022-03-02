package me.mcblueparrot.client.ui.component.handler;

import me.mcblueparrot.client.ui.component.ComponentRenderInfo;

@FunctionalInterface
public interface ClickHandler {

	boolean onClick(ComponentRenderInfo info, int button);

}
