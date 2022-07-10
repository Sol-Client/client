package io.github.solclient.client.platform.mc.render;

import org.jetbrains.annotations.NotNull;

public interface Tessellator {

	static @NotNull Tessellator getInstance() {
		throw new UnsupportedOperationException();
	}

	@NotNull BufferBuilder getBufferBuilder();

	void end();

}
