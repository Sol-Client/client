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

package io.github.solclient.client.mod.impl.hud.tablist;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.impl.StandardMod;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.util.data.Colour;

public class TabListMod extends StandardMod {

	public static boolean enabled;
	public static TabListMod instance;

	@Expose
	@Option
	public boolean hideHeader;
	@Expose
	@Option
	public boolean hideFooter;
	@Expose
	@Option
	public PingType pingType = PingType.NUMERAL;
	@Expose
	@Option
	public Colour backgroundColour = new Colour(Integer.MIN_VALUE);
	@Expose
	@Option
	public Colour entryBackgroundColour = new Colour(553648127);
	@Expose
	@Option
	public boolean playerHeads = true;
	@Expose
	@Option
	public boolean textShadow = true;
	@Expose
	@Option
	public boolean compactColumns;

	@Override
	public void init() {
		super.init();
		instance = this;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		enabled = true;
	}

	@Override
	public void onDisable() {
		super.onDisable();
		enabled = false;
	}

}
