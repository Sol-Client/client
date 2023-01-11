package io.github.solclient.client.mod.impl;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

import com.google.gson.*;

import io.github.solclient.client.GlobalConstants;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.extension.ClickEventExtension;
import net.minecraft.client.resources.I18n;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.*;

public class ScreenshotsMod extends Mod {

	private static final URL IMGUR_URL = Utils.sneakyParse("https://api.imgur.com/3/image");

	public static boolean enabled;
	public static ScreenshotsMod instance;

	@Option
	private boolean view = true;
	@Option
	private boolean folder = true;
	@Option
	private boolean upload = true;

	@Override
	public String getId() {
		return "screenshots";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@Override
	public void onRegister() {
		super.onRegister();
		instance = this;
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
	}

	public void postShot(File screenshot) {

		Runnable viewReceiver = () -> Utils.openUrl(screenshot.toURI().toString());
		IChatComponent screenshotName = new ChatComponentText(screenshot.getName());

		if (!view)
			screenshotName.setChatStyle(new ChatStyle()
					.setChatClickEvent(ClickEventExtension.createStyleWithReceiver(viewReceiver)).setUnderlined(true));

		mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("screenshot.success", screenshotName));

		if (view || folder || upload) {
			IChatComponent secondaryText = new ChatComponentText("");

			if (view) {
				IChatComponent viewText = new ChatComponentText('[' + I18n.format(getTranslationKey() + ".view") + ']');
				viewText.setChatStyle(
						new ChatStyle().setChatClickEvent(ClickEventExtension.createStyleWithReceiver(viewReceiver))
								.setColor(EnumChatFormatting.BLUE));

				secondaryText.appendSibling(viewText);
				secondaryText.appendText(" ");
			}

			if (folder) {
				IChatComponent folderText = new ChatComponentText(
						'[' + I18n.format(getTranslationKey() + ".open_folder") + ']');
				folderText.setChatStyle(new ChatStyle()
						.setChatClickEvent(ClickEventExtension
								.createStyleWithReceiver(() -> Utils.revealUrl(screenshot.toURI().toString())))
						.setColor(EnumChatFormatting.YELLOW));
				secondaryText.appendSibling(folderText);

				secondaryText.appendText(" ");
			}

			if (upload) {
				IChatComponent uploadText = new ChatComponentText(
						'[' + I18n.format(getTranslationKey() + ".upload") + ']');
				uploadText.setChatStyle(new ChatStyle()
						.setChatClickEvent(ClickEventExtension.createStyleWithReceiver(() -> uploadToImgur(screenshot)))
						.setColor(EnumChatFormatting.GREEN));
				secondaryText.appendSibling(uploadText);

				secondaryText.appendText(" ");
			}

			mc.ingameGUI.getChatGUI().printChatMessage(secondaryText);
		}
	}

	private void uploadToImgur(File screenshot) {
		new Thread(() -> {
			try {
				if (!screenshot.exists()) {
					mc.ingameGUI.getChatGUI()
							.printChatMessage(new ChatComponentTranslation(getTranslationKey() + ".deleted")
									.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
					return;
				}

				String base64 = new String(Base64.getEncoder().encode(FileUtils.readFileToByteArray(screenshot)),
						StandardCharsets.US_ASCII);
				String boundary = Utils.generateHttpBoundary();

				HttpURLConnection connection = (HttpURLConnection) IMGUR_URL.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Authorization", "Client-ID " + GlobalConstants.IMGUR_APPLICATION);
				connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=\"" + boundary + "\"");
				connection.setDoOutput(true);

				try (Writer out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.US_ASCII)) {
					out.write("--");
					out.write(boundary);
					out.write("\r\nContent-Disposition: form-data; name=\"image\"\r\n\r\n");
					out.write(base64);
					out.write("\r\n");
					out.write("--");
					out.write(boundary);
					out.write("--\r\n");
				}

				try (Reader in = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
					JsonObject object = JsonParser.parseReader(in).getAsJsonObject();
					if (!object.get("success").isJsonPrimitive() || !object.get("success").getAsBoolean()) {
						throw new IllegalStateException("success is false");
					}

					JsonObject data = object.get("data").getAsJsonObject();
					String link = data.get("link").getAsString();

					IChatComponent linkComponent = new ChatComponentText(link);
					linkComponent.setChatStyle(new ChatStyle().setUnderlined(true)
							.setChatClickEvent(new ClickEvent(Action.OPEN_URL, link)));

					mc.ingameGUI.getChatGUI().printChatMessage(
							new ChatComponentTranslation(getTranslationKey() + ".link", linkComponent));
				}

			} catch (Throwable error) {
				logger.error("Could not upload screenshot", error);
				mc.ingameGUI.getChatGUI()
						.printChatMessage(new ChatComponentTranslation(getTranslationKey() + ".upload_error", error)
								.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
			}
		}).start();
	}

}
