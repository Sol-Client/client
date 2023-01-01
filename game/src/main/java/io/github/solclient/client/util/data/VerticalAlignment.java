package io.github.solclient.client.util.data;

import net.minecraft.client.resources.I18n;

public enum VerticalAlignment {
	TOP, MIDDLE, BOTTOM;

	@Override
	public String toString() {
		return I18n.format("sol_client.vertical_alignment." + name().toLowerCase());
	}

}
