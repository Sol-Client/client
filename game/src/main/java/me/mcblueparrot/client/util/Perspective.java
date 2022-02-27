package me.mcblueparrot.client.util;

public enum Perspective {
	FIRST_PERSON("First Person"),
	THIRD_PERSON_BACK("Third Person Back"),
	THIRD_PERSON_FRONT("Third Person Front");

	private String name;

	private Perspective(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}