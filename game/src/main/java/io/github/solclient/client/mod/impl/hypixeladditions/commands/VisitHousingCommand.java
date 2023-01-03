package io.github.solclient.client.mod.impl.hypixeladditions.commands;

import java.util.*;

import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import net.minecraft.command.*;

public class VisitHousingCommand extends HypixelAdditionsCommand {

	public VisitHousingCommand(HypixelAdditionsMod mod) {
		super(mod);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 1) {
			if (mod.isHousing()) {
				mc.thePlayer.sendChatMessage("/visit " + args[0]);
			} else {
				mc.thePlayer.sendChatMessage("/lobby housing");
				new Thread(() -> {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mc.thePlayer.sendChatMessage("/visit " + args[0]);
				}).start();
			}
			return;
		}
		throw new WrongUsageException("Usage: " + getCommandUsage(sender), new Object[0]);
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/visithousing <player>";
	}

	@Override
	public List<String> getCommandAliases() {
		return Arrays.asList("housing");
	}

	@Override
	public String getCommandName() {
		return null;
	}

}
