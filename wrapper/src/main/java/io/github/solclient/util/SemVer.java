/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
