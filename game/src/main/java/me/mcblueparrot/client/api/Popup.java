package me.mcblueparrot.client.api;

public class Popup {

	private String text;
	private String command;
	private long time;

	public Popup(String text, String command) {
		this.text = text;
		this.command = command;
	}

	public String getText() {
		return text;
	}

	public String getCommand() {
		return command;
	}

	public long getTime() {
		return time;
	}

	public void setTime() {
		this.time = System.currentTimeMillis();
	}

}
