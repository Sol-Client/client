package io.github.solclient.client.mod.option;

import java.util.Objects;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;

/**
 * Represents a mod option.
 */
public interface ModOption<T> {

	/**
	 * Gets the option name.
	 *
	 * @return the name key.
	 */
	String getName();

	/**
	 * Gets the option type.
	 *
	 * @return the type class.
	 */
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

	/**
	 * Unsafely casts the option. If you haven't checked the type, this may cause
	 * problems when you get the value.
	 *
	 * @param <N> the new type.
	 * @return the casted option.
	 */
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

	/**
	 * Gets whether the option can have an apply to all button.
	 *
	 * @return <code>true</code> to show "apply to all".
	 */
	default boolean canApplyToAll() {
		return getApplyToAllClass() != null && !getApplyToAllClass().isEmpty();
	}

	/**
	 * Copies the options value to all equivalent options.
	 */
	default void applyToAll() {
		for (Mod mod : Client.INSTANCE.getMods()) {
			for (ModOption<?> option : mod.getOptions()) {
				if (!(option != null && option.canApplyToAll() && option.isEquivalent(this)))
					continue;
				ModOption<T> typedOption = option.unsafeCast();
				typedOption.setFrom(this);
			}
		}
	}

	/**
	 * Gets the apply to all class. This is used to determine equivalents.
	 *
	 * @return the class.
	 */
	String getApplyToAllClass();

	/**
	 * Gets whether the option should be set from another option when clicking
	 * "apply to all".
	 *
	 * @param option the option.
	 * @return <code>true</code> if the options are deemed equivalent.
	 */
	default boolean isEquivalent(ModOption<?> option) {
		return option.getType() == getType() && Objects.equals(option.getApplyToAllClass(), getApplyToAllClass());
	}

	/**
	 * Sets the option from another option.
	 *
	 * @param option the option.
	 */
	default void setFrom(ModOption<T> option) {
		setValue(option.getValue());
	}

	/**
	 * Gets the option priority. This will change its order.
	 *
	 * @return the priority.
	 */
	int getPriority();

}
