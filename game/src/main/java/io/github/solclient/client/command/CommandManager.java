package io.github.solclient.client.command;

import java.util.*;

import org.apache.logging.log4j.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.SendChatMessageEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.command.*;
import net.minecraft.util.*;

// TODO: replace with https://github.com/moehreag/LegacyClientCommands
public final class CommandManager {

	private static final Logger LOGGER = LogManager.getLogger();

	private final Minecraft mc = Minecraft.getMinecraft();
	private final Map<String, ICommand> commands = new HashMap<>();

	public void register(String name, ICommand command) {
		commands.put(name, command);

		for (String alias : command.getCommandAliases())
			commands.put(alias, command);
	}

	public void unregister(String name) {
		ICommand command = commands.remove(name);
		if (command == null)
			return;

		for (String alias : command.getCommandAliases())
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
					commands.get(commandKey).processCommand(mc.thePlayer, args.toArray(new String[0]));
				} catch (CommandException error) {
					mc.ingameGUI.getChatGUI()
							.printChatMessage(new ChatComponentText(EnumChatFormatting.RED + error.getMessage()));
				} catch (Exception error) {
					mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could "
							+ "not execute client-sided command, see log for details"));
					LOGGER.info("Could not execute client-sided command: " + event.message + ", error: ", error);
				}
			}
		} else if (Client.INSTANCE.getChatExtensions().getChannelSystem() != null) {
			event.cancelled = true;
			Client.INSTANCE.getChatExtensions().getChannelSystem().getChannel().sendMessage(mc.thePlayer,
					event.message);
		}
	}

}
