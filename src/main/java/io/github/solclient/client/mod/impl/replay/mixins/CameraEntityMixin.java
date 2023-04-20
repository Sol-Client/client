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

package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.*;

import com.replaymod.replay.camera.CameraEntity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import net.minecraft.world.World;

@Mixin(CameraEntity.class)
public abstract class CameraEntityMixin extends ClientPlayerEntity {

	public CameraEntityMixin(MinecraftClient client, World world, ClientPlayNetworkHandler networkHandler,
			StatHandler stats) {
		super(client, world, networkHandler, stats);
	}

	/**
	 * @author TheKodeToad
	 * @reason No comment.
	 */
	@Override
	@Overwrite
	public void sendMessage(Text message) {
		super.sendMessage(message);
	}

}
