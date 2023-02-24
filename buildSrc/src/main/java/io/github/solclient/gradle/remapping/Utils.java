package io.github.solclient.gradle.remapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.codec.digest.DigestUtils;

final class Utils {

	public static void download(URL url, Path destination, String sha1) throws IOException {
		if (Files.exists(destination) && checkHash(destination).equalsIgnoreCase(sha1))
			return;

		try (OutputStream out = Files.newOutputStream(destination)) {
			url.openStream().transferTo(out);
		}
	}

	private static String checkHash(Path file) throws IOException {
		try (InputStream in = Files.newInputStream(file)) {
			return DigestUtils.sha1Hex(in);
		}
	}

}
