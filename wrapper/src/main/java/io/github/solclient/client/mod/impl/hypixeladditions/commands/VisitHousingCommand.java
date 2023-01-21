package io.github.solclient.client.mod.impl.hypixeladditions.commands;

import java.util.*;

import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import net.minecraft.command.*;

public class VisitHousingCommand extends HypixelAdditionsCommand {

	public VisitHousingCommand(HypixelAdditionsMod mod) {
		super(mod);
	}

	@Override
	public void execute(CommandSource sender, String[] args) throws CommandException {
		if (args.length == 1) {
			if (mod.isHousing()) {
				mc.player.sendChatMessage("/visit " + args[0]);
			} else {
				mc.player.sendChatMessage("/lobby housing");
				new Thread(() -> {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mc.player.sendChatMessage("/visit " + args[0]);
				}).start();
			}
			return;
		}
		throw new IncorrectUsageException("Usage: " + getUsageTranslationKey(sender), new Object[0]);
	}

	@Override
	public String getUsageTranslationKey(CommandSource sender) {
		return "/visithousing <player>";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("housing");
	}

	@Override
	public String getCommandName() {
		return null;
	}

}
