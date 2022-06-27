/*
 * Includes modified decompiled Replay Mod class files.
 *
 * License for Replay Mod:
 *
 *     Copyright (C) <year>  <name of author>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mod.impl.replay.fix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.replaymod.core.KeyBindingRegistry;
import com.replaymod.core.Module;
import com.replaymod.core.ReplayMod;
import com.replaymod.recording.Setting;
import com.replaymod.recording.handler.ConnectionEventHandler;
import com.replaymod.recording.handler.GuiHandler;
import com.replaymod.recording.mixin.NetworkManagerAccessor;
import com.replaymod.recording.packet.PacketListener;

import io.github.solclient.client.Client;
import io.netty.channel.Channel;
import net.minecraft.network.NetworkManager;

public class SCReplayModRecording implements Module {
	private static final Logger LOGGER = LogManager.getLogger();
	public static SCReplayModRecording instance;
	private final ReplayMod core;
	private ConnectionEventHandler connectionEventHandler;

	public SCReplayModRecording(ReplayMod mod) {
		instance = this;
		core = mod;
		core.getSettingsRegistry().register(Setting.class);
		Client.INSTANCE.getBus().register(this);
	}

	@Override
	public void registerKeyBindings(KeyBindingRegistry registry) {
		registry.registerKeyBinding("replaymod.input.marker", 50, () -> {
			PacketListener packetListener = connectionEventHandler.getPacketListener();
			if(packetListener != null) {
				packetListener.addMarker(null);
				core.printInfoToChat("replaymod.chat.addedmarker");
			}

		}, false);
	}

	@Override
	public void initClient() {
		this.connectionEventHandler = new ConnectionEventHandler(LOGGER, this.core);
		new GuiHandler(this.core).register();
	}

	public void initiateRecording(NetworkManager networkManager) {
		Channel channel = ((NetworkManagerAccessor) networkManager).getChannel();
		if(channel.pipeline().get("ReplayModReplay_replaySender") == null) {
			this.connectionEventHandler.onConnectedToServerEvent(networkManager);
		}
	}

	public ConnectionEventHandler getConnectionEventHandler() {
		return this.connectionEventHandler;
	}

}
