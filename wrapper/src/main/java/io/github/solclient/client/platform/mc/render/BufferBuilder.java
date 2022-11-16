package io.github.solclient.client.platform.mc.render;

public interface BufferBuilder {

	static BufferBuilder getInstance() {
		throw new UnsupportedOperationException();
	}

	void begin(int mode, VertexFormat format);

	Vertex vertex(double x, double y, double z);

}
