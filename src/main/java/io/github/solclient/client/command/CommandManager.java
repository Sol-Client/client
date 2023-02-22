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

package io.github.solclient.client.command;

import java.util.*;

import org.apache.logging.log4j.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.SendChatMessageEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.*;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

// TODO: replace with https://github.com/moehreag/LegacyClientCommands
public final class CommandManager {

	private static final Logger LOGGER = LogManager.getLogger();

	private final MinecraftClient mc = MinecraftClient.getInstance();
	private final Map<String, Command> commands = new HashMap<>();

	public void register(String name, Command command) {
		commands.put(name, command);

		for (String alias : command.getAliases())
			commands.put(alias, command);
	}

	public void unregister(String name) {
		Command command = commands.remove(name);
		if (command == null)
			return;

		for (String alias : command.getAliases())
			commands.remove(alias);
	}

	public boolean isRegistered(String name) {
		return commands.containsKey(name);
	}

	@EventHandler
	public void onSendMessage(SendChatMessageEvent event) {
		// TODO Tab completion. Skipped during port to mixin.

		if (event.message.startsWith("/")) {
			List<String> args = new ArrayList<>(Arrays.asList(event.message.split(" ")));
			String commandKey = args.get(0).substring(1);
			if (commands.containsKey(commandKey)) {
				event.cancelled = true;

				try {
					args.remove(0);
					commands.get(commandKey).execute(mc.player, args.toArray(new String[0]));
				} catch (CommandException error) {
					mc.inGameHud.getChatHud().addMessage(new LiteralText(Formatting.RED + error.getMessage()));
				} catch (Exception error) {
					mc.inGameHud.getChatHud().addMessage(new LiteralText(
							Formatting.RED + "Could " + "not execute client-sided command, see log for details"));
					LOGGER.info("Could not execute client-sided command: " + event.message + ", error: ", error);
				}
			}
		} else if (Client.INSTANCE.getChatExtensions().getChannelSystem() != null) {
			event.cancelled = true;
			Client.INSTANCE.getChatExtensions().getChannelSystem().getChannel().sendMessage(mc.player, event.message);
		}
	}

}
