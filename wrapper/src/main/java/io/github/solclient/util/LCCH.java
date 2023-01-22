package io.github.solclient.util;

import java.util.*;

import net.minecraft.util.math.MathHelper;

/**
 * Implementation of Lunar Client's crosshair format (LCCH).
 */
public final class LCCH {

	public static final int SIZE = 37;

	public static BitSet parse(String input) throws IllegalArgumentException {
		if (!input.startsWith("LCCH-"))
			throw new IllegalArgumentException("Missing prefix");

		input = input.substring(input.indexOf('-') + 1);
		if (input.indexOf('-') == -1)
			throw new IllegalArgumentException("Missing '-'");

		int size = Integer.parseInt(input.substring(0, input.indexOf('-')));
		input = input.substring(input.indexOf('-') + 1);

		byte[] decoded = Base64.getDecoder().decode(input);

		int bits = SIZE * SIZE;
		BitSet result = new BitSet(bits);
		int offset = MathHelper.floor((SIZE - size) / 2F);

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int pixelIndex = y * size + x;
				int srcIndex = pixelIndex / 8;
				int dstIndex = x + offset + ((y + offset) * SIZE);
				int mask = 1 << pixelIndex % 8;

				if (srcIndex < 0 || srcIndex >= bits || dstIndex < 0 || dstIndex >= bits)
					continue;

				result.set(dstIndex, (decoded[srcIndex] & mask) != 0);
			}
		}

		return result;
	}

}
