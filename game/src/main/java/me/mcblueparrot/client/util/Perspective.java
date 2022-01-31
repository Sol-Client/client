package me.mcblueparrot.client.util;

public enum Perspective {
	FIRST_PERSON("First"),
	THIRD_PERSON_BACK("Third Back"),
	THIRD_PERSON_FRONT("Third Front");

	private String name;

	private Perspective(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}