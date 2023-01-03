package io.github.solclient.client.util.data;

/**
 * Alignment not aware of its axis.
 */
public enum Alignment {
	/**
	 * Aligned to left or top.
	 */
	START,
	/**
	 * Centred.
	 */
	CENTRE,
	/**
	 * Aligned to right or bottom.
	 */
	END;

	public int getPosition(int areaSize, int objectSize) {
		switch (this) {
		case CENTRE:
			return (areaSize / 2) - (objectSize / 2);
		case END:
			return areaSize - objectSize;
		default:
			return 0;
		}
	}
}
