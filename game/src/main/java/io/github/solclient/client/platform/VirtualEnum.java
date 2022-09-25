package io.github.solclient.client.platform;

/**
 * A virtual enum object.
 */
public interface VirtualEnum {

	/**
	 * @see Enum#name()
	 */
	String enumName();

	/**
	 * @see Enum#toString()
	 */
	@Override
	String toString();

	/**
	 * @see Enum#ordinal()
	 */
	int enumOrdinal();

	/**
	 * Gets the "real" enum object.
	 * @return The real deal.
	 */
	Enum<?> toEnum();

	/**
	 * Return the other values in this virtual enum.
	 * @return The other values.
	 */
	VirtualEnum[] getValues();

	/**
	 * Return the other values in the actual enum.
	 * @return The values.
	 */
	Enum<?>[] getEnumValues();

}
