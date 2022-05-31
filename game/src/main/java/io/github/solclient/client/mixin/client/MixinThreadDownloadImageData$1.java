package io.github.solclient.client.mixin.client;

import java.net.HttpURLConnection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "net.minecraft.client.renderer.ThreadDownloadImageData$1")
public class MixinThreadDownloadImageData$1 {

	@Inject(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/HttpURLConnection;setDoOutput(Z)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void setUserAgent(CallbackInfo callback, HttpURLConnection  connection) {
		connection.setRequestProperty("User-Agent", System.getProperty("http.agent"));
	}

}
