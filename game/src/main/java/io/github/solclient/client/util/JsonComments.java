package io.github.solclient.client.util;

public class JsonComments {

	private static final int COMMENT_NONE = 0;
	private static final int COMMENT_UNILINE = 1;
	private static final int COMMENT_MULTILINE = 2;

	public static String swallowComments(String str) {
		char prevChar = 0;

		int comment = COMMENT_NONE;
		StringBuilder result = new StringBuilder();

		for(char character : str.toCharArray()) {
			main: {
				if(comment == COMMENT_NONE) {
					if(character == '/' && prevChar == '/') {
						comment = COMMENT_UNILINE;
					}
					else if(prevChar == '/' && character == '*') {
						comment = COMMENT_MULTILINE;
					}

					if(comment != COMMENT_NONE) {
						swallow(result);
						break main;
					}
				}
				else if(comment == COMMENT_UNILINE) {
					if(character == '\n') {
						comment = COMMENT_NONE;
					}
					else {
						break main;
					}
				}
				else if(comment == COMMENT_MULTILINE) {
					if(prevChar == '*' && character == '/') {
						comment = COMMENT_NONE;
						swallow(result);
					}
					break main;
				}

				result.append(character);
			}

			prevChar = character;
		}

		if(comment != COMMENT_NONE) {
			throw new IllegalArgumentException("Still in " + (comment == COMMENT_MULTILINE ? "multiline" : "single-line") + " comment");
		}

		return result.toString();
	}

	private static void swallow(StringBuilder result) {
		result.deleteCharAt(result.length() - 1);
	}

}
