package io.github.solclient.client.mixin.client;

import java.net.HttpURLConnection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.util.GlobalConstants;

@Mixin(targets = "net.minecraft.client.texture.PlayerSkinTexture$1")
public class MixinPlayerSkinTexture$1 {

	@Inject(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/HttpURLConnection;setDoOutput(Z)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void setUserAgent(CallbackInfo callback, HttpURLConnection connection) {
		// TODO maybe only do this when necessary
		connection.setRequestProperty("User-Agent", GlobalConstants.USER_AGENT);
	}

}
