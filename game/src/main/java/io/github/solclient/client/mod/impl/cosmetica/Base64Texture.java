package io.github.solclient.client.mod.impl.cosmetica;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

final class Base64Texture extends SimpleTexture {

	private static Set<ResourceLocation> all = new HashSet<>();

	private final String base64;

	Base64Texture(String base64) {
		super(null);
		this.base64 = base64;
	}

	private static String strictParse(String input) {
		if (input.startsWith("data:image/png;base64,")) {
			return input.substring(22);
		}

		return null;
	}

	private static ResourceLocation target(String base64) {
		return new ResourceLocation("sol_client_base64", base64);
	}

	static void disposeAll() {
		all.forEach((location) -> Minecraft.getMinecraft().getTextureManager().deleteTexture(location));

		if(!all.isEmpty()) {
			all = new HashSet<>();
		}
	}

	static ResourceLocation load(String url) {
		String base64 = strictParse(url);
		if(base64 == null) {
			throw new IllegalArgumentException(url);
		}

		ResourceLocation target = target(base64);

		if(all.contains(target)) {
			return target;
		}

		all.add(target);

		Base64Texture texture = new Base64Texture(base64);
		Minecraft.getMinecraft().getTextureManager().loadTexture(target, texture);
		return target;
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		deleteGlTexture();
		// Yes! Writing Java 8 without fallbacks is so refreshing.
		try(ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(base64))) {
			BufferedImage image = ImageIO.read(in);
			TextureUtil.uploadTextureImageAllocate(getGlTextureId(), image, false, false);
		}
	}

}
