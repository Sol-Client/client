package io.github.solclient.api;

public interface Identifier {

	static Identifier parse(String path) {
		throw new UnsupportedOperationException();
	}

	static Identifier create(String namespace, String path) {
		throw new UnsupportedOperationException();
	}

	static Identifier create(String path) {
		throw new UnsupportedOperationException();
	}

	String getNamespace();

	String getPath();

}
