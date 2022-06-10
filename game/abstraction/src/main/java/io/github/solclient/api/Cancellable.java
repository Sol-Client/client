package io.github.solclient.api;

public interface Cancellable {

	boolean isCancelled();

	void setCancelled(boolean cancelled);

	default void cancel() {
		setCancelled(true);
	}

}
