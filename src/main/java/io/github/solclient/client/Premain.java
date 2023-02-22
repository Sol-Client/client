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

package io.github.solclient.client;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixins;

import io.github.solclient.client.addon.AddonManager;
import io.github.solclient.util.GlobalConstants;
import io.github.solclient.wrapper.transformer.AccessWidenerTransformer;
import me.djtheredstoner.devauth.common.DevAuth;
import net.minecraft.client.main.Main;

/**
 * Used to add some mixin and access wideners.
 */
public final class Premain {

	public static void main(String[] args) throws IOException {
		// TODO this doesn't really work very well
		System.setProperty("http.agent", GlobalConstants.USER_AGENT);

		Mixins.addConfiguration("sol-client.mixins.json");

		Mixins.addConfiguration("mixins.core.replaymod.json");
		Mixins.addConfiguration("mixins.recording.replaymod.json");
		Mixins.addConfiguration("mixins.render.replaymod.json");
		Mixins.addConfiguration("mixins.render.blend.replaymod.json");
		Mixins.addConfiguration("mixins.replay.replaymod.json");
		if (GlobalConstants.optifine)
			Mixins.addConfiguration("mixins.compat.shaders.replaymod.json");
		Mixins.addConfiguration("mixins.extras.playeroverview.replaymod.json");

		AccessWidenerTransformer.addWideners("replay-mod.accesswidener");

		// load addons
		AddonManager.premain(args);

		if (GlobalConstants.DEV) {
			DevAuth auth = new DevAuth();
			args = auth.processArguments(args);
		}

		// run Minecraft main
		Main.main(args);
	}

}
