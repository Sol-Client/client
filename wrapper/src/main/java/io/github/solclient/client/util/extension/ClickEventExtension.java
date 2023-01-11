package io.github.solclient.client.util.extension;

import net.minecraft.event.ClickEvent;

public interface ClickEventExtension {

	static ClickEvent createStyleWithReceiver(Runnable receiver) {
		return from(new ClickEvent(null, null)).setReceiver(receiver).vanilla();
	}

	static ClickEventExtension from(ClickEvent event) {
		return (ClickEventExtension) event;
	}

	Runnable getReceiver();

	ClickEventExtension setReceiver(Runnable receiver);

	default ClickEvent vanilla() {
		return (ClickEvent) this;
	}

}
