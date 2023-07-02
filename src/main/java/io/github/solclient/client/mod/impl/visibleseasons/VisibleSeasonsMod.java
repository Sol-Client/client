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

package io.github.solclient.client.mod.impl.visibleseasons;

import com.google.gson.annotations.Expose;
import io.github.solclient.client.mod.impl.StandardMod;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.mod.option.annotation.Slider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

import java.util.ArrayList;
import java.util.List;

public class VisibleSeasonsMod extends StandardMod {
	public static VisibleSeasonsMod instance;
	protected final MinecraftClient mc = MinecraftClient.getInstance();
	public List<Snowflake> snowflakes = new ArrayList<>();

	@Expose
	@Option
	public boolean forceVisibleSeasons;

	@Expose
	@Option
	@Slider(min = 1, max = 50, step = 1)
	public float visibleSeasonsAmount = 25;

	@Expose
	@Option
	public boolean visibleSeasonsLowDetail;


	@Override
	public String getDetail() {
		return I18n.translate("sol_client.mod.screen.by", "ArikSquad");
	}

	@Override
	public void init() {
		super.init();
		instance = this;
	}

}
