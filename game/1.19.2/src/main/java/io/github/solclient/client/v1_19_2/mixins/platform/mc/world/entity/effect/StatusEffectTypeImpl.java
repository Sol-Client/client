package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.entity.effect;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.world.entity.effect.StatusEffectType;
import io.github.solclient.client.v1_19_2.SharedObjects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;

@Mixin(StatusEffect.class)
@Implements(@Interface(iface = StatusEffectType.class, prefix = "platform$"))
public abstract class StatusEffectTypeImpl {

	private Sprite getSprite() {
		return MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite((StatusEffect) (Object) this);
	}

	public void platform$render(int x, int y) {
		DrawableHelper.drawSprite(SharedObjects.primary2dMatrixStack, x, y, 0, 18, 18, getSprite());
	}

	public @NotNull String platform$getName() {
		return getTranslationKey();
	}

	@Shadow
	public abstract String getTranslationKey();

}

@Mixin(StatusEffectType.class)
interface StatusEffectTypeImpl$Static {

	@Overwrite(remap = false)
	static StatusEffectType get(String name) {
		switch(name) {
			case "SPEED":
				return (StatusEffectType) StatusEffects.SPEED;
			case "STRENGTH":
				return (StatusEffectType) StatusEffects.STRENGTH;
			case "BLINDNESS":
				return (StatusEffectType) StatusEffects.BLINDNESS;
			case "DARKNESS":
				return (StatusEffectType) StatusEffects.DARKNESS;
		}

		throw new IllegalArgumentException(name);
	}

}
