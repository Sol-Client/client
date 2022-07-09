package io.github.solclient.abstraction.mc.render;

import org.jetbrains.annotations.NotNull;

public interface Tessellator {

	static @NotNull Tessellator getInstance() {
		throw new UnsupportedOperationException();
	}

	@NotNull BufferBuilder getBufferBuilder();

	void end();

}
