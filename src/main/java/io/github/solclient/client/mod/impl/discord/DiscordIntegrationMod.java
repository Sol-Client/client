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

package io.github.solclient.client.mod.impl.discord;

import java.io.File;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

import com.google.gson.annotations.Expose;
import com.jagrosh.discordipc.*;
import com.jagrosh.discordipc.entities.RichPresence;
import com.replaymod.replay.ReplayModReplay;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.impl.discord.socket.DiscordSocket;
import io.github.solclient.client.mod.option.ModOptionStorage;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.SolClientMainMenu;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;

public class DiscordIntegrationMod extends StandardMod {

	public static DiscordIntegrationMod instance;

	private IPCClient client;
	protected DiscordSocket socket;
	private RichPresence activity;
	private boolean state;

	@Expose
	@Option
	private boolean ip;
	@Expose
	@Option
	boolean voiceChatHud;
	@Expose
	@Option
	@Slider(min = 50, max = 150, step = 1, format = "sol_client.slider.percent")
	float voiceChatHudScale = 100;
	@Expose
	@Option
	VerticalAlignment voiceChatHudAlignment = VerticalAlignment.TOP;
	@Expose
	Position voiceChatHudPosition;
	@Expose
	@Option
	Colour usernameColour = Colour.WHITE;
	@Expose
	@Option
	Colour mutedColour = new Colour(255, 80, 80);
	@Expose
	@Option
	Colour speakingColour = new Colour(20, 255, 20);
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	boolean shadow = true;
	@Expose
	@Option
	@TextField("sol_client.mod.screen.default")
	private String applicationId = "";
	@Expose
	@Option
	@TextField("sol_client.mod.screen.default")
	private String icon = "";
	@Expose
	private String button1Text = "", button1Url = "";
	@Expose
	private String button2Text = "", button2Url = "";

	private final DiscordVoiceChatHud discordVoiceChatHud = new DiscordVoiceChatHud(this);

	@Override
	public List<HudElement> getHudElements() {
		return Arrays.asList(discordVoiceChatHud);
	}

	@Override
	public void init() {
		instance = this;
		super.init();
	}

	@Override
	public ListComponent createConfigComponent() {
		ListComponent result = super.createConfigComponent();
		ListComponent buttons = new ListComponent() {

			@Override
			protected Rectangle getDefaultBounds() {
				return Rectangle.ofDimensions(result.getBounds().getWidth(), 50);
			}

		};
		Component labelContainer = Component.withBounds((component, defaultBounds) -> Rectangle.ofDimensions(230,
				component.getSubComponents().get(0).getBounds().getHeight()));
		labelContainer.add(new LabelComponent(getTranslationKey("buttons")), Controller.none());
		buttons.add(labelContainer);
		buttons.add(createButton(ensureField("button1Text"), ensureField("button1Url")));
		buttons.add(createButton(ensureField("button2Text"), ensureField("button2Url")));
		result.add(result.getSubComponents().size(), buttons);
		return result;
	}

	private Component createButton(ModOptionStorage<String> label, ModOptionStorage<String> url) {
		Component result = Component.withBounds(Controller.of(Rectangle.ofDimensions(230, 15)));

		TextFieldComponent labelComponent = new TextFieldComponent(110, 32, false).autoFlush()
				.withPlaceholder(getTranslationKey("label")).onUpdate(value -> {
					label.set(value);
					return true;
				});
		labelComponent.setText(label.get());
		result.add(labelComponent, Controller.none());

		TextFieldComponent urlComponent = new TextFieldComponent(110, 32, false).autoFlush()
				.withPlaceholder(getTranslationKey("url")).onUpdate(value -> {
					url.set(value);
					return true;
				});
		urlComponent.setText(url.get());
		result.add(urlComponent, (component, defaultBounds) -> defaultBounds.offset(115, 0));

		return result;
	}

	@Override
	public void postOptionChange(String key, Object value) {
		if (key.equals("voiceChatHud")) {
			closeSocket();
			if ((boolean) value) {
				connectSocket();
			}
		}
	}

	@Override
	public void lateInit() {
		super.lateInit();
		discordVoiceChatHud.setFont(mc.textRenderer);
	}

	@Override
	protected void onEnable() {
		super.onEnable();

		try {
			long id = GlobalConstants.DISCORD_APPLICATION;

			if (!applicationId.isEmpty()) {
				try {
					id = Long.parseLong(MinecraftUtils.onlyKeepDigits(applicationId));
				} catch (NumberFormatException ignored) {
				}
			}

			client = new IPCClient(id);
			client.connect();

			startActivity(mc.world);
		} catch (Throwable error) {
			logger.warn("Could not start Discord IPC", error);
		}

		if (voiceChatHud) {
			connectSocket();
		}
	}

	private void connectSocket() {
		socket = new DiscordSocket(this);
		socket.connect();
	}

	private void closeSocket() {
		if (socket != null && !socket.isClosed()) {
			socket.close();
			socket = null;
		}
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		close();
	}

	@EventHandler
	public void onGameQuit(GameQuitEvent event) {
		if (isEnabled() && client != null) {
			close();
		}
	}

	private void close() {
		if (client != null) {
			client.close();
			client = null;
		}

		closeSocket();
	}

	@EventHandler
	public void onGuiChange(OpenGuiEvent event) {
		if (client == null) {
			return;
		}

		if ((event.screen == null || event.screen instanceof TitleScreen || event.screen instanceof SolClientMainMenu
				|| event.screen instanceof MultiplayerScreen) && state && mc.world == null) {
			startActivity(null);
		}
	}

	@EventHandler
	public void onWorldChange(WorldLoadEvent event) {
		if (client == null) {
			return;
		}

		if (!state && event.world != null) {
			startActivity(event.world);
		}
	}

	private void startActivity(ClientWorld world) {
		if (world != null) {
			if (mc.isIntegratedServerRunning()) {
				setActivity(I18n.translate("menu.singleplayer"));
			} else {
				if (ReplayModReplay.instance.getReplayHandler() != null) {
					setActivity(I18n.translate("replaymod.gui.replayviewer"));
				} else {
					String server = mc.getCurrentServerEntry().name;
					if (ip)
						server += " (" + mc.getCurrentServerEntry().address + ')';

					setActivity(I18n.translate("sol_client.mod.discord_integration.multiplayer", server));
				}
			}
		} else {
			setActivity(I18n.translate("sol_client.mod.discord_integration.main_menu"));
			state = false;
		}
	}

	private void setActivity(String text) {
		// @formatter:off
		activity = ExtendedRichPresence.builder()
				.setState(text)
				.setLargeImage(!icon.isEmpty() ? icon : "large_logo")
				.setStartTimestamp(OffsetDateTime.now())
				.setButton1Label(button1Text.isEmpty() ? null : button1Text)
				.setButton1Url(button1Url.isEmpty() ? null : button1Url)
				.setButton2Label(button2Text.isEmpty() ? null : button2Text)
				.setButton2Url(button2Url.isEmpty() ? null : button2Url)
				.build();
		// @formatter:on

		client.sendRichPresence(activity);
		state = true;
	}

	public void socketError(Exception error) {
		logger.error("Discord socket error", error);

		closeSocket();
	}

}
