package io.github.solclient.abstraction.mc;

public interface Identifier {

	static Identifier parse(String path) {
		throw new UnsupportedOperationException();
	}

	static Identifier create(String namespace, String path) {
		throw new UnsupportedOperationException();
	}

	static Identifier minecraft(String path) {
		throw new UnsupportedOperationException();
	}

	static Identifier solClient(String path) {
		return create("solclient", path);
	}

	static Identifier replayMod(String path) {
		return create("replaymod", path);
	}

	String namespace();

	String path();

}
