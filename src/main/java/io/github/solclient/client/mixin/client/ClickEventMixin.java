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

package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.extension.ClickEventExtension;
import lombok.Getter;
import net.minecraft.text.ClickEvent;

@Mixin(ClickEvent.class)
public class ClickEventMixin implements ClickEventExtension {

	@Getter
	private Runnable receiver;

	@Override
	public ClickEventExtension setReceiver(Runnable receiver) {
		this.receiver = receiver;
		return this;
	}

}
