package me.mcblueparrot.client.util.data;

public enum Alignment {
	TOP("Top"),
	MIDDLE("Centre"),
	BOTTOM("Bottom");

	private String name;

	Alignment(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
