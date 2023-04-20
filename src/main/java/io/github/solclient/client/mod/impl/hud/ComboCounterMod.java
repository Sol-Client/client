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

package io.github.solclient.client.mod.impl.hud;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.impl.SolClientSimpleHudMod;
import io.github.solclient.client.util.data.Position;
import net.minecraft.client.resource.language.I18n;

public class ComboCounterMod extends SolClientSimpleHudMod {

	private long hitTime = -1;
	private int combo;
	private int possibleTarget;

	@Override
	public void render(Position position, boolean editMode) {
		super.render(position, editMode);
		if ((System.currentTimeMillis() - hitTime) > 2000) {
			combo = 0;
		}
	}

	@Override
	public String getText(boolean editMode) {
		if (editMode || combo == 0) {
			return I18n.translate("sol_client.mod.combo_counter.no_hits");
		} else if (combo == 1) {
			return I18n.translate("sol_client.mod.combo_counter.one_hit");
		} else {
			return I18n.translate("sol_client.mod.combo_counter.n_hits", combo);
		}
	}

	public void dealHit() {
		combo++;
		possibleTarget = -1;
		hitTime = System.currentTimeMillis();
	}

	@EventHandler
	public void onEntityAttack(EntityAttackEvent event) {
		possibleTarget = event.victim.getEntityId();
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.entity.getEntityId() == possibleTarget) {
			dealHit();
		} else if (event.entity == mc.player) {
			takeHit();
		}
	}

	public void takeHit() {
		combo = 0;
	}

}
