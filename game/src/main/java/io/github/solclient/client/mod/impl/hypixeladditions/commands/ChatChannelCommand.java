package io.github.solclient.client.mod.impl.hypixeladditions.commands;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.Client;
import io.github.solclient.client.chat.ChatChannelSystem;
import io.github.solclient.client.command.CommandException;
import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import io.github.solclient.client.mod.impl.hypixeladditions.HypixelChatChannels;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.world.entity.player.LocalPlayer;

public class ChatChannelCommand extends HypixelAdditionsCommand {

	private static final String USAGE = "Usage: /chat (all|party|guild|officer|coop)";

	public ChatChannelCommand(HypixelAdditionsMod mod) {
		super(mod);
	}

	@Override
	public String[] getAliases() {
		return new String[] { "channel" };
	}

	@Override
	public void execute(@NotNull LocalPlayer player, @NotNull List<String> args) throws CommandException {
		ChatChannelSystem system = Client.INSTANCE.getChatChannelSystem();
		if(args.size() == 1) {
			if(args.get(0).equals("c") || args.get(0).equals("coop") || args.get(0).equals("co-op") || args.get(0).equals("skyblock_coop") || args.get(0).equals("skyblock_co-op")) {
				system.setChannel(HypixelChatChannels.COOP);
			}
			else if(args.get(0).equals("a") || args.get(0).equals("all")) {
				system.setChannel(ChatChannelSystem.ALL);
			}
			else if(args.get(0).equals("p") || args.get(0).equals("party")) {
				system.setChannel(HypixelChatChannels.PARTY);
			}
			else if(args.get(0).equals("o") || args.get(0).equals("officer") || args.get(0).equals("officers") || args.get(0).equals("guild_officer") || args.get(0).equals("guild_officers")) {
				system.setChannel(HypixelChatChannels.OFFICER);
			}
			else if(args.get(0).equals("g") || args.get(0).equals("guild")) {
				system.setChannel(HypixelChatChannels.GUILD);
			}
			else {
				throw new CommandException(USAGE);
			}
		}
		else if(args.size() == 2 && (args.get(0).equals("2") || args.get(0).equals("t") || args.get(0).equals("to"))) {
			system.setChannel(ChatChannelSystem.getPrivateChannel(args.get(1)));
		}
		else {
			throw new CommandException(USAGE);
		}
		player.sendSystemMessage(Text.format("Chat channel: %s", system.getChannelName()));
	}

}
