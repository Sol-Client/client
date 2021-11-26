package me.mcblueparrot.client.mod.impl.hypixeladditions.request;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Request {

	public final String message;
	public final String command;
	public long time;

	public static Request fromMessage(String message) {
		for(RequestType type : RequestType.values()) {
			Request request = type.getRequest(message);

			if(request != null) {
				return request;
			}
		}

		return null;
	}
}
