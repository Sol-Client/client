package io.github.solclient.client.event;

public interface Cancellable {

	boolean isCancelled();

	void setCancelled(boolean cancelled);

	default void cancel() {
		setCancelled(true);
	}

}
