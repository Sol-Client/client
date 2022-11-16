package io.github.solclient.client.v1_8_9.mixins.platform.mc.world.entity.player;

import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.world.entity.player.*;
import io.github.solclient.client.platform.mc.world.inventory.Inventory;
import net.minecraft.entity.player.*;

@Mixin(PlayerEntity.class)
@Implements(@Interface(iface = Player.class, prefix = "platform$"))
public abstract class PlayerImpl {

	public Inventory platform$getInventory() {
		return (Inventory) inventory;
	}

	@Shadow
	public PlayerInventory inventory;

	public Abilities platform$getAbilities() {
		return (Abilities) abilities;
	}

	@Shadow
	public PlayerAbilities abilities;

}

