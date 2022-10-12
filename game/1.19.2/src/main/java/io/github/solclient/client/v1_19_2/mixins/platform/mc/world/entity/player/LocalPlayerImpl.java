package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.entity.player;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.network.Connection;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.world.entity.player.LocalPlayer;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
@Implements(@Interface(iface = LocalPlayer.class, prefix = "platform$"))
public class LocalPlayerImpl {

	public @NotNull Connection platform$getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	public void platform$sendSystemMessage(@NotNull String text) {
		// TODO Auto-generated method stub

	}

	public void platform$sendSystemMessage(@NotNull Text text) {
		// TODO Auto-generated method stub

	}

	public void platform$chat(@NotNull String text) {
		// TODO Auto-generated method stub

	}

	public boolean platform$isSpectatorMode() {
		// TODO Auto-generated method stub
		return false;
	}

	public void platform$clientSwing() {
		// TODO Auto-generated method stub

	}

}

