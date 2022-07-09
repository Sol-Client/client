package io.github.solclient.client.event.impl.hud;

import io.github.solclient.client.event.impl.RenderEvent;
import io.github.solclient.client.util.VanillaHudElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractHudElementRenderEvent extends RenderEvent {

	private final VanillaHudElement element;
	private int ticks;

	public AbstractHudElementRenderEvent(VanillaHudElement element, float tickDelta) {
		super(tickDelta);
		this.element = element;
	}

}
