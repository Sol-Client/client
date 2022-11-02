package io.github.solclient.client.util;

public interface ScopeGuard extends AutoCloseable {

	@Override
	void close(); // no exceptions

}
