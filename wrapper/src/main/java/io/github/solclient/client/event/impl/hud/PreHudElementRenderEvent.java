package io.github.solclient.client.event.impl.hud;

import io.github.solclient.client.event.Cancellable;
import io.github.solclient.client.util.VanillaHudElement;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class PreHudElementRenderEvent extends AbstractHudElementRenderEvent implements Cancellable {

	private boolean cancelled;

	public PreHudElementRenderEvent(VanillaHudElement element, float tickDelta) {
		super(element, tickDelta);
	}

}
