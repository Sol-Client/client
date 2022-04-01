package me.mcblueparrot.client.extension;

public class InvalidExtensionException extends Exception {

	public InvalidExtensionException(String message) {
		super(message);
	}

	public InvalidExtensionException(Throwable cause) {
		super(cause);
	}

	public InvalidExtensionException(String message, Throwable cause) {
		super(message, cause);
	}

}
