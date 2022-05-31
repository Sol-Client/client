package io.github.solclient.client.mixin.mod;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.mod.impl.TweaksMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class MixinTweaksMod {

	@Mixin(GuiIngame.class)
	public static abstract class MixinGuiIngame {

		@Inject(method = "renderSelectedItem", at = @At("HEAD"), cancellable = true)
		public void drawExtraLines(ScaledResolution scaledRes, CallbackInfo callback) {
			if(TweaksMod.enabled && TweaksMod.instance.betterTooltips) {
				callback.cancel();

				mc.mcProfiler.startSection("selectedItemName");

				if(remainingHighlightTicks > 0 && highlightingItemStack != null) {
					List<String> lines = highlightingItemStack.getTooltip(mc.thePlayer, false);

					int y = scaledRes.getScaledHeight() - 59;

					int height = getFontRenderer().FONT_HEIGHT + 2;

					y -= (height * (lines.size() - 1)) - 2;

					if(!this.mc.playerController.shouldDrawHUD()) {
						y += 14;
					}

					int opacity = (int)(this.remainingHighlightTicks * 256.0F / 10.0F);
					opacity = Math.min(opacity, 255);

					if(opacity > 0) {
						GlStateManager.pushMatrix();
						GlStateManager.enableBlend();
						GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
						for(String line : lines) {
							int x = (scaledRes.getScaledWidth() - getFontRenderer().getStringWidth(line)) / 2;
							getFontRenderer().drawStringWithShadow(line, x, y,
									16777215 + (opacity << 24));
							y += height;
						}
						GlStateManager.disableBlend();
						GlStateManager.popMatrix();
					}
				}

				mc.mcProfiler.endSection();
			}
		}

		@Shadow
		@Final
		private Minecraft mc;

		@Shadow
		private int remainingHighlightTicks;

		@Shadow
		private ItemStack highlightingItemStack;

		@Shadow
		public abstract FontRenderer getFontRenderer();


	}

	@Mixin(Enchantment.class)
	public static abstract class MixinEnchantment {

		@Inject(method = "getTranslatedName", at = @At("HEAD"), cancellable = true)
		public void overrideName(int level, CallbackInfoReturnable<String> callback) {
			if(TweaksMod.enabled && TweaksMod.instance.arabicNumerals) {
				callback.setReturnValue(StatCollector.translateToLocal(getName()) + " " + level);
			}
		}

		@Shadow
		public abstract String getName();

	}

	@Mixin(InventoryEffectRenderer.class)
	public static class MixinInventoryEffectRenderer {

		@Redirect(method = "drawActivePotionEffects", at = @At(value = "INVOKE",
				target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
		public String overrideLevel(String translateKey, Object[] parameters) {
			if(TweaksMod.enabled && TweaksMod.instance.arabicNumerals && translateKey.startsWith("enchantment.level.")) {
				return Integer.toString(Integer.parseInt(translateKey.substring(18)));
			}

			return I18n.format(translateKey, parameters);
		}

	}

	@Mixin(ItemPotion.class)
	public static class MixinItemPotion {

		@Redirect(method = "addInformation", at = @At(value = "INVOKE",
				target = "Lnet/minecraft/util/StatCollector;translateToLocal(Ljava/lang/String;)Ljava/lang/String;",
				ordinal = 1))
		public String overrideAmplifier(String key) {
			if(TweaksMod.enabled && TweaksMod.instance.arabicNumerals && key.startsWith("potion.potency.")) {
				return Integer.toString(Integer.parseInt(key.substring(15)) + 1);
			}
			return StatCollector.translateToLocal(key);
		}

	}

	@Mixin(RendererLivingEntity.class)
	public static class MixinRendererLivingEntity {

		@Redirect(method = "canRenderName", at = @At(value = "FIELD",
				target = "Lnet/minecraft/client/renderer/entity/RenderManager;livingPlayer:Lnet/minecraft/entity/Entity;"))
		public Entity renderOwnName(RenderManager manager) {
			if(TweaksMod.enabled && TweaksMod.instance.showOwnTag) {
				return null;
			}
			return manager.livingPlayer;
		}

	}

	@Mixin(EntityRenderer.class)
	public static abstract class MixinEntityRenderer {

		@Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/EntityRenderer;setupViewBobbing(F)V"))
		public void cancelWorldBobbing(EntityRenderer instance, float partialTicks) {
			if(TweaksMod.enabled && TweaksMod.instance.minimalViewBobbing) {
				return;
			}

			setupViewBobbing(partialTicks);
		}

		@Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/EntityRenderer;hurtCameraEffect(F)V"))
		public void cancelWorldRotation(EntityRenderer instance, float partialTicks) {
			if(TweaksMod.enabled && TweaksMod.instance.minimalDamageShake) {
				return;
			}

			hurtCameraEffect(partialTicks);
		}

		@Redirect(method = "hurtCameraEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V"))
		public void adjustRotation(float angle, float x, float y, float z) {
			if(TweaksMod.enabled) {
				angle *= TweaksMod.instance.getDamageShakeIntensity();
			}
			
			GlStateManager.rotate(angle, x, y, z);
		}


		@Shadow
		protected abstract void setupViewBobbing(float partialTicks);

		@Shadow
		protected abstract void hurtCameraEffect(float partialTicks);

	}

	@Mixin(GuiIngameMenu.class)
	public static class MixinGuiIngameMenu {

		private boolean disconnect;

		@Inject(method = "initGui", at = @At("HEAD"))
		public void initGui(CallbackInfo callback) {
			disconnect = !isConfirmEnabled();
		}

		@Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
		public void overrideButton(GuiButton button, CallbackInfo callback) {
			if(button.id == 1 && !disconnect) {
				callback.cancel();
				button.displayString = EnumChatFormatting.GREEN + I18n.format("sol_client.mod.tweaks.confirm_disconnect");
				disconnect = true;
			}
		}

		private boolean isConfirmEnabled() {
			return TweaksMod.enabled && TweaksMod.instance.confirmDisconnect;
		}

	}

	@Mixin(Minecraft.class)
	public static class MixinMinecraft {

		@Inject(method = "setIngameFocus", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;grabMouseCursor()V"))
		public void afterLock(CallbackInfo callback) {
			if(TweaksMod.enabled && TweaksMod.instance.betterKeyBindings) {
				for(KeyBinding keyBinding : gameSettings.keyBindings) {
					try {
						KeyBinding.setKeyBindState(keyBinding.getKeyCode(), keyBinding.getKeyCode() < 256 && Keyboard.isKeyDown(keyBinding.getKeyCode()));
					}
					catch (IndexOutOfBoundsException error) {
					}
				}
			}
		}

		@Shadow
		public GameSettings gameSettings;

	}

}
