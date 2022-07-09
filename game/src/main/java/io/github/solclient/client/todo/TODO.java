package io.github.solclient.client.todo;

import io.github.solclient.abstraction.mc.RuntimeDetermined;

/**
 * Used to mark critical todo values. Should be paired with a comment.
 */
@Deprecated
public class TODO {

	static {
		System.err.println("Warning: TODO class loaded");
	}

	public static final Object L = null;
	public static final boolean Z = RuntimeDetermined.value();

}
