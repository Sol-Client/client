package io.github.solclient.client.mod;

/**
 * Represents a mod option.
 */
public interface ModOption<T> {

	String getName();

	Class<T> getType();

	/**
	 * Casts the option.
	 *
	 * @param <N>  the new type.
	 * @param type the type class.
	 * @return the (safely) casted option.
	 */
	default <N> ModOption<N> cast(Class<N> type) {
		if (!getType().equals(type))
			throw new ClassCastException();

		return (ModOption<N>) this;
	}

	default <N> ModOption<N> unsafeCast() {
		return (ModOption<N>) this;
	}

	/**
	 * Gets the option value.
	 *
	 * @return the value.
	 */
	T getValue();

	/**
	 * Sets the option value.
	 *
	 * @param value the value.
	 */
	void setValue(T value);

	boolean canApplyToAll();

	void applyToAll();

	/**
	 * Gets whether the option should be set from another option when clicking
	 * "apply to all".
	 *
	 * @param option the option.
	 * @return <code>true</code> if the options are deemed equivalent.
	 */
	boolean isEquivalent(ModOption<T> option);

	/**
	 * Sets the option from another option.
	 *
	 * @param option the option.
	 */
	void setFrom(ModOption<T> option);

	int getPriority();

}
