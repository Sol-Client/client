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

package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SoundPlayEvent {

	public String soundName;
	public float volume;
	public float pitch;
	public float originalVolume;
	public float originalPitch;

	/**
	 * Sets volume to <code>0</code>.
	 */
	public void cancel() {
		volume = 0;
	}

}
