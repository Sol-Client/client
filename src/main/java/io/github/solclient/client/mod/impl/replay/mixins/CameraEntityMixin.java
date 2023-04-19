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
