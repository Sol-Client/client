package io.github.solclient.client.v1_8_9.mixins.platform.mc.world.entity.player;

import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.world.entity.player.Abilities;
import net.minecraft.entity.player.PlayerAbilities;

@Mixin(PlayerAbilities.class)
public class AbilitiesImpl implements Abilities {

	@Override
	public boolean canBuild() {
		return allowModifyWorld;
	}

	@Shadow
	public boolean allowModifyWorld;

}

