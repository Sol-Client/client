package io.github.solclient.client.event.impl.hud;

import io.github.solclient.client.platform.mc.world.scoreboard.Objective;
import io.github.solclient.client.util.VanillaHudElement;
import lombok.Getter;

public class PreSidebarRenderEvent extends PreHudElementRenderEvent {

	@Getter
	private Objective objective;

	public PreSidebarRenderEvent(VanillaHudElement element, float tickDelta) {
		super(element, tickDelta);
	}

}
