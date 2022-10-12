package io.github.solclient.client.platform.mc.shader;

import java.io.IOException;
import java.util.List;

import io.github.solclient.client.platform.Helper;

public interface ShaderChain {

	@Helper
	static ShaderChain create(String content) throws IOException {
		throw new UnsupportedOperationException();
	}

	void updateWindowSize(int width, int height);

	List<Shader> getShaders();

}
