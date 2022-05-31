package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import io.github.solclient.client.CapeManager;
import io.github.solclient.client.Client;
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
		CapeManager manager = Client.INSTANCE.getCapeManager();

		if(manager == null) {
			return;
		}

		ResourceLocation cape = manager.getForPlayer(this);

		if(cape != null) {
			callback.setReturnValue(cape);
		}
	}

}
