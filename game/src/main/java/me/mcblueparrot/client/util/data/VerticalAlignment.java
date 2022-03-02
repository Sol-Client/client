package me.mcblueparrot.client.util.data;

public enum VerticalAlignment {
	TOP("Top"),
	MIDDLE("Centre"),
	BOTTOM("Bottom");

	private String name;

	VerticalAlignment(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
