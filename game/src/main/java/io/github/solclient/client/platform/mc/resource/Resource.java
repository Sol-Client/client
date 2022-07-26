package io.github.solclient.client.platform.mc.resource;

import java.io.IOException;
import java.io.InputStream;

import org.jetbrains.annotations.NotNull;

public interface Resource {

	@NotNull InputStream getInput() throws IOException;

}
