package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import io.github.solclient.client.mod.impl.cosmetica.CosmeticaMod;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

	public AbstractClientPlayerEntityMixin(World world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "getSkinId" /* TODO try to get legacy yarn to fix this! */, at = @At("HEAD"), cancellable = true)
	public void overrideCapeLocation(CallbackInfoReturnable<Identifier> callback) {
		if (!CosmeticaMod.enabled)
			return;

		CosmeticaMod.instance.getCapeTexture(this).ifPresent(callback::setReturnValue);
	}

}
