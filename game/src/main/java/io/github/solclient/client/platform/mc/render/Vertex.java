package io.github.solclient.client.platform.mc.render;

import org.jetbrains.annotations.NotNull;

public interface Vertex {

	@NotNull Vertex color(int r, int g, int b, int a);

	@NotNull BufferBuilder endVertex();

}
