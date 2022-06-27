package io.github.solclient.abstraction;

/**
 * A virtual enum object.
 */
public interface VirtualEnum {

	/**
	 * @see Enum#name()
	 */
	String name();

	/**
	 * @see Enum#toString()
	 */
	@Override
	String toString();

	/**
	 * @see Enum#ordinal()
	 */
	int ordinal();

	/**
	 * Gets the "real" enum object.
	 * @return The real deal.
	 */
	Enum<?> toEnum();

	/**
	 * Return the other values in this virtual enum.
	 * @return The other values.
	 */
	VirtualEnum[] values();

	/**
	 * Return the other values in the actual enum.
	 * @return The values.
	 */

	Enum<?>[] enumValues();

}
