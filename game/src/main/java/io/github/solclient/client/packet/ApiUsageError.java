package io.github.solclient.client.packet;

public final class ApiUsageError extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public ApiUsageError(String message) {
	    super(message);
	}

}
