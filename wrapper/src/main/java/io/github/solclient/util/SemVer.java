package io.github.solclient.util;

import lombok.*;

@Data
@RequiredArgsConstructor
public class SemVer {

	private final int major, minor, patch;

	public static SemVer parseOrNull(String string) {
		try {
			return parse(string);
		} catch (IllegalArgumentException error) {
			return null;
		}
	}

	public static SemVer parse(String string) {
		String initial = string;

		if (string.indexOf('.') == -1) {
			throw new IllegalArgumentException(initial);
		}

		String majorString = string.substring(0, string.indexOf('.'));
		string = string.substring(string.indexOf('.') + 1);

		if (string.indexOf('.') == -1) {
			throw new IllegalArgumentException(initial);
		}

		String minorString = string.substring(0, string.indexOf('.'));
		string = string.substring(string.indexOf('.') + 1);

		if (string.indexOf('.') != -1) {
			throw new IllegalArgumentException(initial);
		}

		String patchString = string;

		if (string.isEmpty()) {
			throw new IllegalArgumentException(initial);
		}

		try {
			return new SemVer(Integer.parseInt(majorString), Integer.parseInt(minorString),
					Integer.parseInt(patchString));
		} catch (NumberFormatException error) {
			throw new NumberFormatException(error.getMessage() + ", full string: \"" + initial + "\"");
		}
	}

	public boolean isNewerThan(SemVer version) {
		if (version == null) {
			return false;
		}

		if (major > version.major) {
			return true;
		} else if (major < version.major) {
			return false;
		}

		if (minor > version.minor) {
			return true;
		} else if (minor < version.minor) {
			return false;
		}

		return patch > version.patch;
	}

	@Override
	public String toString() {
		return "" + major + '.' + minor + '.' + patch;
	}

}
