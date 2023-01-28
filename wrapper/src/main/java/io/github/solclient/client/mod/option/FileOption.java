package io.github.solclient.client.mod.option;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents a file option.
 */
public interface FileOption extends ModOption<String> {

	Path getPath();

	String getEditText();

	void readFile() throws IOException;

}
