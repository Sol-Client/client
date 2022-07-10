package io.github.solclient.client.mod.impl.hypixeladditions.commands;

import java.util.List;

import io.github.solclient.client.command.CommandException;
import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import io.github.solclient.client.platform.mc.world.entity.player.LocalPlayer;
import io.github.solclient.client.util.Utils;

public class VisitHousingCommand extends HypixelAdditionsCommand {

	public VisitHousingCommand(HypixelAdditionsMod mod) {
		super(mod);
	}

	@Override
	public void execute(LocalPlayer player, List<String> args) throws CommandException {
		if(args.size() == 1) {
			if(mod.isHousing()) {
				mc.getPlayer().executeCommand("visit " + args.get(0));
			}
			else {
				mc.getPlayer().executeCommand("lobby housing");
				Utils.MAIN_EXECUTOR.execute(() -> {
					try {
						Thread.sleep(300);
					}
					catch(InterruptedException ignored) {
					}
					mc.getPlayer().executeCommand("visit ".concat(args.get(0)));
				});
			}
			return;
		}
		throw new CommandException("Usage: /visithousing <player>");
	}

	@Override
	public String[] getAliases() {
		return new String[] { "housing" };
	}

}
