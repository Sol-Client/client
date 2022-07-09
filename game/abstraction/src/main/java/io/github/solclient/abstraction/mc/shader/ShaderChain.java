package io.github.solclient.abstraction.mc.shader;

import java.io.IOException;
import java.util.List;

import io.github.solclient.abstraction.Helper;
import io.github.solclient.abstraction.mc.Identifier;

public interface ShaderChain {

	@Helper
	static ShaderChain create(String content) throws IOException {
		throw new UnsupportedOperationException();
	}

	void updateWindowSize(int width, int height);

	List<Shader> getShaders();

}
