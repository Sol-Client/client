package me.mcblueparrot.client.mod.impl.replay;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SCInterpolatorType {
	CATMULL("Catmull-Rom Spline"),
	CUBIC("Cubic Spline"),
	LINEAR("Linear");

	private String name;

	@Override
	public String toString() {
		return name;
	}
}