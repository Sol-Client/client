package io.github.solclient.client.mod.impl.hypixeladditions.commands;

import java.util.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.chatextensions.channel.ChatChannelSystem;
import io.github.solclient.client.mod.impl.hypixeladditions.*;
import net.minecraft.command.*;
import net.minecraft.util.*;

public class ChatChannelCommand extends HypixelAdditionsCommand {

	public ChatChannelCommand(HypixelAdditionsMod mod) {
		super(mod);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		ChatChannelSystem system = Client.INSTANCE.getChatExtensions().getChannelSystem();
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
				throw new WrongUsageException(getCommandUsage(sender));
			}
		} else if (args.length == 2 && (args[0].equals("2") || args[0].equals("t") || args[0].equals("to"))) {
			system.setChannel(ChatChannelSystem.getPrivateChannel(args[1]));
		} else {
			throw new WrongUsageException("Usage: " + getCommandUsage(sender));
		}
		sender.addChatMessage(
				new ChatComponentText(EnumChatFormatting.GREEN + "Chat Channel: " + system.getChannelName()));
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/chat (all|party|guild|officer|coop)";
	}

	@Override
	public List<String> getCommandAliases() {
		return Arrays.asList("channel");
	}

	@Override
	public String getCommandName() {
		return null;
	}

}
