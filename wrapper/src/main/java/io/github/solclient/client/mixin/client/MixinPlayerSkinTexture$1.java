package io.github.solclient.client.mixin.client;

import java.net.HttpURLConnection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(targets = "net.minecraft.client.texture.PlayerSkinTexture$1")
public class MixinPlayerSkinTexture$1 {

	@Inject(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/HttpURLConnection;setDoOutput(Z)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void setUserAgent(CallbackInfo callback, HttpURLConnection connection) {
		connection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
	}

}
