package io.github.solclient.client.mod.option;

/**
 * Represents a slider option.
 */
public interface SliderOption extends ModOption<Float> {

	boolean shouldShowValue();

	String getFormat();

	float getMin();

	float getMax();

	float getStep();

}
