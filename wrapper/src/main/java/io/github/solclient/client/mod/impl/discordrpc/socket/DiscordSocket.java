package io.github.solclient.client.mod.impl.discordrpc.socket;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.*;

import io.github.solclient.client.mod.impl.discordrpc.DiscordIntegrationMod;
import io.github.solclient.client.util.Utils;

/**
 * Socket connection to Discord client.
 * Uses StreamKit.
 */
public final class DiscordSocket extends WebSocketClient {

	private static final String STREAMKIT = "https://streamkit.discord.com";
	private static final URL STREAMKIT_TOKEN = Utils.sneakyParse(STREAMKIT + "/overlay/token");
	private static final String CLIENT_ID = "207646673902501888";
	private static final String NONCE = "deadbeef";
	private static final URI SOCKET_URI = URI.create("ws://127.0.0.1:6463/?v=1&client_id=" + CLIENT_ID);

	private DiscordIntegrationMod mod;

	private String currentVoiceChannel;

	private Map<String, User> voiceCallUsers = new LinkedHashMap<>();

	public DiscordSocket(DiscordIntegrationMod mod) {
		super(SOCKET_URI, new HashMap<String, String>() {

			private static final long serialVersionUID = 1L;

			{
				put("Origin", STREAMKIT);
			}
		});

		this.mod = mod;
	}

	@Override
	public void onOpen(ServerHandshake handshake) {}

	@Override
	public void onMessage(String message) {
		try {
			JsonObject obj = JsonParser.parseString(message).getAsJsonObject();

			if(obj.has("cmd")) {
				String cmd = obj.get("cmd").getAsString();
				String evt = null;
				JsonObject data = null;
				JsonObject userData = null;
				String userId = null;

				if(obj.has("evt") && !obj.get("evt").isJsonNull()) {
					evt = obj.get("evt").getAsString();
				}

				if(obj.has("data") && obj.get("data").isJsonObject()) {
					data = obj.get("data").getAsJsonObject();
				}

				if(data != null && data.has("user") && data.get("user").isJsonObject()) {
					userData = data.get("user").getAsJsonObject();
					userId = userData.get("id").getAsString();
				}

				if(cmd.equals("DISPATCH") && evt != null) {
					if(evt.equals("READY")) {
						obtainAccessToken1();
					}
					else if(evt.equals("VOICE_CHANNEL_SELECT") && data != null) {
						if(data.get("channel_id").isJsonNull()) {
							if(currentVoiceChannel != null) {
								channelDisconnected();
							}

							currentVoiceChannel = null;
						}
						else {
							currentVoiceChannel = data.get("channel_id").getAsString();
							channelConnected();
						}
					}
					else if(evt.equals("VOICE_STATE_CREATE") || evt.equals("VOICE_STATE_UPDATE")) {
						if(currentVoiceChannel == null) {
							findUser();
						}

						voiceCallUsers.computeIfAbsent(userId, User::new).update(data, userData);
					}
					else if(evt.equals("VOICE_STATE_DELETE") && userId != null) {
						voiceCallUsers.remove(userId).deleteTexture();
					}
					else if((evt.equals("SPEAKING_START") || evt.equals("SPEAKING_STOP")) && data != null) {
						User user = voiceCallUsers.get(data.get("user_id").getAsString());

						if(user != null) {
							user.setSpeaking(evt.equals("SPEAKING_START"));
						}
					}
				}
				else if(cmd.equals("GET_SELECTED_VOICE_CHANNEL") && data != null && !data.get("id").isJsonNull()) {
					currentVoiceChannel = data.get("id").getAsString();

					for(JsonElement elem : data.get("voice_states").getAsJsonArray()) {
						JsonObject voiceState = elem.getAsJsonObject();
						User user = new User(userId);
						user.update(voiceState, userData);
					}

					channelConnected();
				}
				else if(cmd.equals("AUTHORIZE")) {
					obtainAccessToken2(obj.get("data").getAsJsonObject().get("code").getAsString());
				}
				else if(cmd.equals("AUTHENTICATE")) {
					if("ERROR".equals(evt)) {
						obtainAccessToken1();
					}
					else if(obj.has("data")) {
						authCompleted();
					}
				}
			}
		}
		catch(IOException | IllegalArgumentException error) {
			mod.socketError(error);
		}
	}

	private void authCompleted() {
		subscribe("VOICE_CHANNEL_SELECT");
		subscribe("VOICE_CONNECTION_STATUS");
		findUser();
	}

	private void findUser() {
		JsonObject command = new JsonObject();
		command.addProperty("cmd", "GET_SELECTED_VOICE_CHANNEL");
		command.addProperty("nonce", NONCE);
		send(command.toString());
	}

	private void channelConnected() {
		subscribe("VOICE_STATE_CREATE", currentVoiceChannel);
		subscribe("VOICE_STATE_UPDATE", currentVoiceChannel);
        subscribe("VOICE_STATE_DELETE", currentVoiceChannel);
        subscribe("SPEAKING_START", currentVoiceChannel);
        subscribe("SPEAKING_STOP", currentVoiceChannel);
	}

	private void channelDisconnected() {
		unsubscribe("VOICE_STATE_CREATE", currentVoiceChannel);
		unsubscribe("VOICE_STATE_UPDATE", currentVoiceChannel);
        unsubscribe("VOICE_STATE_DELETE", currentVoiceChannel);
        unsubscribe("SPEAKING_START", currentVoiceChannel);
        unsubscribe("SPEAKING_STOP", currentVoiceChannel);

        voiceCallUsers.values().forEach(User::deleteTexture);
        voiceCallUsers.clear();
	}

	private void subscribe(String event) {
		subscribe(event, (JsonObject) null);
	}

	@SuppressWarnings("unused")
	private void unsubscribe(String event) {
		subscribe(event, (JsonObject) null, false);
	}

	private void subscribe(String event, String channel) {
		subscribe(event, channel, true);
	}

	private void unsubscribe(String event, String channel) {
		subscribe(event, channel, false);
	}

	private void subscribe(String event, String channel, boolean value) {
		JsonObject args = new JsonObject();
		args.addProperty("channel_id", channel);

		subscribe(event, args, value);
	}

	private void subscribe(String event, JsonObject args) {
		subscribe(event, args, true);
	}

	@SuppressWarnings("unused")
	private void unsubscribe(String event, JsonObject args) {
		subscribe(event, args, false);
	}

	private void subscribe(String event, JsonObject args, boolean value) {
		JsonObject command = new JsonObject();
		command.addProperty("cmd", value ? "SUBSCRIBE" : "UNSUBSCRIBE");

		if(args != null) {
			command.add("args", args);
		}

		command.addProperty("evt", event);
		command.addProperty("nonce", NONCE);

		send(command.toString());
	}

	private void obtainAccessToken1() {
		JsonObject command = new JsonObject();
		command.addProperty("cmd", "AUTHORIZE");

		JsonObject args = new JsonObject();
		args.addProperty("client_id", CLIENT_ID);

		JsonArray scopes = new JsonArray();
		scopes.add("rpc");

		args.add("scopes", scopes);
		args.addProperty("prompt", "none");

		command.add("args", args);
		command.addProperty("nonce", NONCE);

		send(command.toString());
	}

	private void obtainAccessToken2(String code) throws IOException {
		JsonObject command = new JsonObject();
		command.addProperty("code", code);

		HttpURLConnection connection = (HttpURLConnection) STREAMKIT_TOKEN.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Accept", "application/json");

		try(OutputStream out = connection.getOutputStream(); InputStream in = connection.getInputStream()) {
			out.write(command.toString().getBytes());

			JsonObject result = JsonParser.parseString(IOUtils.toString(in, StandardCharsets.UTF_8)).getAsJsonObject();

			auth(result.has("access_token") ? result.get("access_token").getAsString() : "none");
		}
	}

	private void auth(String accessToken) {
		JsonObject command = new JsonObject();
		command.addProperty("cmd", "AUTHENTICATE");

		JsonObject args = new JsonObject();
		args.addProperty("access_token", accessToken);

		command.add("args", args);
		// Honestly don't know if you need this
		command.addProperty("nonce", NONCE);

		send(command.toString());
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
        voiceCallUsers.values().forEach(User::deleteTexture);

		if(code != CloseFrame.NORMAL) {
			mod.socketError(new IllegalStateException("Socket closed abnormally (error " + code + ": " + (reason == null || reason.isEmpty() ? "no message" : reason) + ")"));
		}
	}

	@Override
	public void onError(Exception error) {
		mod.socketError(error);
	}

	public Collection<User> getVoiceCallUsers() {
		return voiceCallUsers.values();
	}

	public User getVoiceCallUser(String id) {
		return voiceCallUsers.get(id);
	}

}
