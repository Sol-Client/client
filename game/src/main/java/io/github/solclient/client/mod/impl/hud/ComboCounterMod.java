package io.github.solclient.client.mod.impl.hud;

import io.github.solclient.abstraction.mc.lang.I18n;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.world.entity.EntityAttackEvent;
import io.github.solclient.client.event.impl.world.entity.EntityDamageEffectEvent;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.util.data.Position;

public class ComboCounterMod extends SimpleHudMod {

	private long hitTime = -1;
	private int combo;
	private int possibleTarget;

	@Override
	public String getId() {
		return "combo_counter";
	}

	@Override
	public void render(Position position, boolean editMode) {
		super.render(position, editMode);
		if((System.currentTimeMillis() - hitTime) > 2000) {
			combo = 0;
		}
	}

	@Override
	public String getText(boolean editMode) {
		if(editMode || combo == 0) {
			return I18n.translate("sol_client.mod.combo_counter.no_hits");
		}
		else if(combo == 1) {
			return I18n.translate("sol_client.mod.combo_counter.one_hit");
		}
		else {
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
		possibleTarget = event.getEntity().getNumericId();
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEffectEvent event) {
		if(event.getEntity().getNumericId() == possibleTarget) {
			dealHit();
		}
		else if(event.getEntity() == mc.getPlayer()) {
			takeHit();
		}
	}

	public void takeHit() {
		combo = 0;
	}

}
