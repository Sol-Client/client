/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mod.impl;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

import com.google.gson.*;

import io.github.solclient.client.extension.ClickEventExtension;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.util.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.*;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;

public class ScreenshotsMod extends SolClientMod {

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
	public void init() {
		super.init();
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
		Runnable viewReceiver = () -> MinecraftUtils.openUrl(screenshot.toURI().toString());
		Text screenshotName = new LiteralText(screenshot.getName());

		if (!view)
			screenshotName.setStyle(new Style().setClickEvent(ClickEventExtension.createEventWithReceiver(viewReceiver))
					.setUnderline(true));

		mc.inGameHud.getChatHud().addMessage(new TranslatableText("screenshot.success", screenshotName));

		if (view || folder || upload) {
			Text secondaryText = new LiteralText("");

			if (view) {
				Text viewText = new LiteralText('[' + I18n.translate(getTranslationKey("view")) + ']');
				viewText.setStyle(new Style().setClickEvent(ClickEventExtension.createEventWithReceiver(viewReceiver))
						.setFormatting(Formatting.BLUE));

				secondaryText.append(viewText);
				secondaryText.append(" ");
			}

			if (folder) {
				Text folderText = new LiteralText('[' + I18n.translate(getTranslationKey("open_folder")) + ']');
				folderText.setStyle(new Style()
						.setClickEvent(ClickEventExtension
								.createEventWithReceiver(() -> MinecraftUtils.revealUrl(screenshot.toURI().toString())))
						.setFormatting(Formatting.YELLOW));
				secondaryText.append(folderText);
				secondaryText.append(" ");
			}

			if (upload) {
				Text uploadText = new LiteralText('[' + I18n.translate(getTranslationKey("upload")) + ']');
				uploadText.setStyle(new Style()
						.setClickEvent(ClickEventExtension.createEventWithReceiver(() -> uploadToImgur(screenshot)))
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
					mc.inGameHud.getChatHud().addMessage(new TranslatableText(getTranslationKey("deleted"))
							.setStyle(new Style().setFormatting(Formatting.RED)));
					return;
				}

				String base64 = new String(Base64.getEncoder().encode(FileUtils.readFileToByteArray(screenshot)),
						StandardCharsets.US_ASCII);
				String boundary = MinecraftUtils.generateHttpBoundary();

				HttpURLConnection connection = (HttpURLConnection) Utils.getConnection(GlobalConstants.USER_AGENT,
						IMGUR_URL);
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
					JsonObject object = new JsonParser().parse(in).getAsJsonObject();
					if (!object.get("success").isJsonPrimitive() || !object.get("success").getAsBoolean()) {
						throw new IllegalStateException("success is false");
					}

					JsonObject data = object.get("data").getAsJsonObject();
					String link = data.get("link").getAsString();
					String hash = data.get("deletehash").getAsString();

					Text linkComponent = new LiteralText(link);
					linkComponent.setStyle(
							new Style().setUnderline(true).setClickEvent(new ClickEvent(Action.OPEN_URL, link)));

					mc.inGameHud.getChatHud()
							.addMessage(new TranslatableText(getTranslationKey("link"), linkComponent));
					mc.inGameHud.getChatHud()
							.addMessage(new LiteralText('[' + I18n.translate(getTranslationKey("delete")) + ']')
									.setStyle(new Style().setFormatting(Formatting.RED).setClickEvent(
											ClickEventExtension.createEventWithReceiver(() -> deleteFromImgur(hash)))));
				}

			} catch (Throwable error) {
				logger.error("Could not upload screenshot", error);
				mc.inGameHud.getChatHud().addMessage(new TranslatableText(getTranslationKey("upload_error"), error)
						.setStyle(new Style().setFormatting(Formatting.RED)));
			}
		}).start();
	}

	private void deleteFromImgur(String hash) {
		new Thread(() -> {
			try {
				HttpURLConnection connection = (HttpURLConnection) Utils.getConnection(GlobalConstants.USER_AGENT,
						new URL(IMGUR_URL + "/" + hash));
				connection.setRequestMethod("DELETE");
				connection.setRequestProperty("Authorization", "Client-ID " + GlobalConstants.IMGUR_APPLICATION);
				connection.getInputStream();
				mc.inGameHud.getChatHud().addMessage(new TranslatableText(getTranslationKey("deleted"))
						.setStyle(new Style().setFormatting(Formatting.RED)));
			} catch (Throwable error) {
				logger.error("Could not delete screenshot", error);

				String url = "https://imgur.com/delete/" + hash;
				Text linkComponent = new LiteralText(url);
				linkComponent
						.setStyle(new Style().setUnderline(true).setClickEvent(new ClickEvent(Action.OPEN_URL, url)));
				mc.inGameHud.getChatHud()
						.addMessage(new TranslatableText(getTranslationKey("delete_error"), error, linkComponent)
								.setStyle(new Style().setFormatting(Formatting.RED)));
			}
		}).start();
	}

}
