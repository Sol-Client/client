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
