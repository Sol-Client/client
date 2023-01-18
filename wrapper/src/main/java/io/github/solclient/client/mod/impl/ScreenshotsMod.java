package io.github.solclient.client.mod.impl;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

import com.google.gson.*;

import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.extension.ClickEventExtension;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.*;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;

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
		Text screenshotName = new LiteralText(screenshot.getName());

		if (!view)
			screenshotName.setStyle(new Style().setClickEvent(ClickEventExtension.createStyleWithReceiver(viewReceiver))
					.setUnderline(true));

		mc.inGameHud.getChatHud().addMessage(new TranslatableText("screenshot.success", screenshotName));

		if (view || folder || upload) {
			Text secondaryText = new LiteralText("");

			if (view) {
				Text viewText = new LiteralText('[' + I18n.translate(getTranslationKey() + ".view") + ']');
				viewText.setStyle(new Style().setClickEvent(ClickEventExtension.createStyleWithReceiver(viewReceiver))
						.setFormatting(Formatting.BLUE));

				secondaryText.append(viewText);
				secondaryText.append(" ");
			}

			if (folder) {
				Text folderText = new LiteralText('[' + I18n.translate(getTranslationKey() + ".open_folder") + ']');
				folderText.setStyle(new Style()
						.setClickEvent(ClickEventExtension
								.createStyleWithReceiver(() -> Utils.revealUrl(screenshot.toURI().toString())))
						.setFormatting(Formatting.YELLOW));
				secondaryText.append(folderText);
				secondaryText.append(" ");
			}

			if (upload) {
				Text uploadText = new LiteralText('[' + I18n.translate(getTranslationKey() + ".upload") + ']');
				uploadText.setStyle(new Style()
						.setClickEvent(ClickEventExtension.createStyleWithReceiver(() -> uploadToImgur(screenshot)))
						.setFormatting(Formatting.GREEN));
				secondaryText.append(uploadText);
				secondaryText.append(" ");
			}

			mc.inGameHud.getChatHud().addMessage(secondaryText);
		}
	}

	private void uploadToImgur(File screenshot) {
		new Thread(() -> {
			try {
				if (!screenshot.exists()) {
					mc.inGameHud.getChatHud().addMessage(new TranslatableText(getTranslationKey() + ".deleted")
							.setStyle(new Style().setFormatting(Formatting.RED)));
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

					Text linkComponent = new LiteralText(link);
					linkComponent.setStyle(
							new Style().setUnderline(true).setClickEvent(new ClickEvent(Action.OPEN_URL, link)));

					mc.inGameHud.getChatHud()
							.addMessage(new TranslatableText(getTranslationKey() + ".link", linkComponent));
				}

			} catch (Throwable error) {
				logger.error("Could not upload screenshot", error);
				mc.inGameHud.getChatHud().addMessage(new TranslatableText(getTranslationKey() + ".upload_error", error)
						.setStyle(new Style().setFormatting(Formatting.RED)));
			}
		}).start();
	}

}
