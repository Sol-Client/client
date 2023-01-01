package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import io.github.solclient.client.mod.impl.cosmetica.CosmeticaMod;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends EntityPlayer {

	public MixinAbstractClientPlayer(World worldIn, GameProfile gameProfileIn) {
		super(worldIn, gameProfileIn);
	}

	@Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
	public void overrideCapeLocation(CallbackInfoReturnable<ResourceLocation> callback) {
		if (!CosmeticaMod.enabled) {
			return;
		}

		CosmeticaMod.instance.getCapeTexture(this).ifPresent((texture) -> callback.setReturnValue(texture));
	}

}
