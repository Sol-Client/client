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

package io.github.solclient.client.mod.impl.hypixeladditions.commands;

import java.util.*;

import io.github.solclient.client.mod.impl.api.chat.ChatApiMod;
import io.github.solclient.client.mod.impl.api.chat.channel.ChatChannelSystem;
import io.github.solclient.client.mod.impl.hypixeladditions.*;
import net.minecraft.command.*;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class ChatChannelCommand extends HypixelAdditionsCommand {

	public ChatChannelCommand(HypixelAdditionsMod mod) {
		super(mod);
	}

	@Override
	public void execute(CommandSource sender, String[] args) throws CommandException {
		ChatChannelSystem system = ChatApiMod.instance.getChannelSystem();
		if (args.length == 1) {
			if (args[0].equals("c") || args[0].equals("coop") || args[0].equals("co-op")
					|| args[0].equals("skyblock_coop") || args[0].equals("skyblock_co-op")) {
				system.setChannel(HypixelChatChannels.COOP);
			} else if (args[0].equals("a") || args[0].equals("all")) {
				system.setChannel(ChatChannelSystem.ALL);
			} else if (args[0].equals("p") || args[0].equals("party")) {
				system.setChannel(HypixelChatChannels.PARTY);
			} else if (args[0].equals("o") || args[0].equals("officer") || args[0].equals("officers")
					|| args[0].equals("guild_officer") || args[0].equals("guild_officers")) {
				system.setChannel(HypixelChatChannels.OFFICER);
			} else if (args[0].equals("g") || args[0].equals("guild")) {
				system.setChannel(HypixelChatChannels.GUILD);
			} else {
				throw new IncorrectUsageException(getUsageTranslationKey(sender));
			}
		} else if (args.length == 2 && (args[0].equals("2") || args[0].equals("t") || args[0].equals("to"))) {
			system.setChannel(ChatChannelSystem.getPrivateChannel(args[1]));
		} else {
			throw new IncorrectUsageException("Usage: " + getUsageTranslationKey(sender));
		}
		sender.sendMessage(new LiteralText(Formatting.GREEN + "Chat Channel: " + system.getChannelName()));
	}

	@Override
	public String getUsageTranslationKey(CommandSource sender) {
		return "/chat (all|party|guild|officer|coop)";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("channel");
	}

	@Override
	public String getCommandName() {
		return null;
	}

}
