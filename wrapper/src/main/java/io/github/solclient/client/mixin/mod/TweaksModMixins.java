/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mixin.mod;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.extension.MinecraftClientExtension;
import io.github.solclient.client.mod.impl.TweaksMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.*;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Formatting;

public class TweaksModMixins {

	@Mixin(InGameHud.class)
	public static abstract class InGameHudMixin {

		@Inject(method = "renderHeldItemName", at = @At("HEAD"), cancellable = true)
		public void drawExtraLines(Window window, CallbackInfo callback) {
			if (TweaksMod.enabled && TweaksMod.instance.betterTooltips) {
				callback.cancel();

				client.profiler.push("selectedItemName");

				if (heldItemTooltipFade > 0 && heldItem != null) {
					List<String> lines = heldItem.getTooltip(client.player, false);

					int y = window.getHeight() - 59;

					int height = getFontRenderer().fontHeight + 2;

					y -= (height * (lines.size() - 1)) - 2;

					if (!client.interactionManager.hasStatusBars())
						y += 14;

					int opacity = (int) (this.heldItemTooltipFade * 256.0F / 10.0F);
					opacity = Math.min(opacity, 255);

					if (opacity > 0) {
						GlStateManager.pushMatrix();
						GlStateManager.enableBlend();
						GlStateManager.blendFuncSeparate(770, 771, 1, 0);
						for (String line : lines) {
							int x = (window.getWidth() - getFontRenderer().getStringWidth(line)) / 2;
							getFontRenderer().drawWithShadow(line, x, y, 16777215 + (opacity << 24));
							y += height;
						}
						GlStateManager.disableBlend();
						GlStateManager.popMatrix();
					}
				}

				client.profiler.pop();
			}
		}

		@Final
		private @Shadow MinecraftClient client;

		@Shadow
		private int heldItemTooltipFade;

		@Shadow
		private ItemStack heldItem;

		@Shadow
		public abstract TextRenderer getFontRenderer();

	}

	@Mixin(Enchantment.class)
	public static abstract class EnchantmentMixin {

		@Inject(method = "getTranslatedName", at = @At("HEAD"), cancellable = true)
		public void overrideName(int level, CallbackInfoReturnable<String> callback) {
			if (TweaksMod.enabled && TweaksMod.instance.arabicNumerals)
				callback.setReturnValue(I18n.translate(getTranslationKey()) + ' ' + level);
		}

		@Shadow
		public abstract String getTranslationKey();

	}

	@Mixin(InventoryScreen.class)
	public static abstract class InventoryScreenMixin extends HandledScreen {

		public InventoryScreenMixin(ScreenHandler screenHandler) {
			super(screenHandler);
		}

		@Redirect(method = "drawStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/language/I18n;translate(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
		public String overrideLevel(String key, Object[] parameters) {
			if (TweaksMod.enabled && TweaksMod.instance.arabicNumerals && key.startsWith("enchantment.level.")) {
				return Integer.toString(Integer.parseInt(key.substring(18)));
			}

			return I18n.translate(key, parameters);
		}

		@Redirect(method = "applyStatusEffectOffset", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;x:I", ordinal = 0))
		public void shiftLeft(InventoryScreen instance, int value) {
			if (TweaksMod.enabled && TweaksMod.instance.centredInventory) {
				x = (width - backgroundWidth) / 2;
				return;
			}

			x = value;
		}

	}

	@Mixin(PotionItem.class)
	public static class PotionItemMixin {

		@Redirect(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/CommonI18n;translate(Ljava/lang/String;)Ljava/lang/String;", ordinal = 1))
		public String overrideAmplifier(String key) {
			if (TweaksMod.enabled && TweaksMod.instance.arabicNumerals && key.startsWith("potion.potency.")) {
				return Integer.toString(Integer.parseInt(key.substring(15)) + 1);
			}
			return I18n.translate(key);
		}

	}

	@Mixin(LivingEntityRenderer.class)
	public static class RendererLivingEntityMixin {

		@Redirect(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;field_11098:Lnet/minecraft/entity/Entity;"))
		public Entity renderOwnName(EntityRenderDispatcher dispatcher) {
			if (TweaksMod.enabled && TweaksMod.instance.showOwnTag)
				return null;

			return dispatcher.field_11098;
		}

	}

	@Mixin(GameRenderer.class)
	public static abstract class GameRendererMixin {

		@Redirect(method = "setupCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(F)V"))
		public void cancelWorldBobbing(GameRenderer instance, float tickDelta) {
			if (TweaksMod.enabled && TweaksMod.instance.minimalViewBobbing)
				return;

			bobView(tickDelta);
		}

		@Redirect(method = "setupCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobViewWhenHurt(F)V"))
		public void cancelWorldRotation(GameRenderer instance, float tickDelta) {
			if (TweaksMod.enabled && TweaksMod.instance.minimalDamageShake)
				return;

			bobViewWhenHurt(tickDelta);
		}

		@Redirect(method = "bobViewWhenHurt", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;rotate(FFFF)V"))
		public void adjustRotation(float angle, float x, float y, float z) {
			if (TweaksMod.enabled)
				angle *= TweaksMod.instance.getDamageShakeIntensity();

			GlStateManager.rotate(angle, x, y, z);
		}

		@Shadow
		protected abstract void bobView(float partialTicks);

		@Shadow
		protected abstract void bobViewWhenHurt(float partialTicks);

	}

	@Mixin(GameMenuScreen.class)
	public static class GameMenuScreenMixin {

		private boolean disconnect;

		@Inject(method = "init", at = @At("HEAD"))
		public void init(CallbackInfo callback) {
			disconnect = !isConfirmEnabled();
		}

		@Inject(method = "buttonClicked", at = @At("HEAD"), cancellable = true)
		public void overrideButton(ButtonWidget button, CallbackInfo callback) {
			if (button.id == 1 && !disconnect) {
				callback.cancel();
				button.message = Formatting.GREEN + I18n.translate("sol_client.mod.tweaks.confirm_disconnect");
				disconnect = true;
			}
		}

		private boolean isConfirmEnabled() {
			return TweaksMod.enabled && TweaksMod.instance.confirmDisconnect;
		}

	}

	@Mixin(MinecraftClient.class)
	public static class MinecraftClientMixin {

		@Inject(method = "closeScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseInput;lockMouse()V"))
		public void afterLock(CallbackInfo callback) {
			if (TweaksMod.enabled && TweaksMod.instance.betterKeyBindings) {
				for (KeyBinding keyBinding : options.allKeys) {
					try {
						KeyBinding.setKeyPressed(keyBinding.getCode(),
								keyBinding.getCode() < 256 && Keyboard.isKeyDown(keyBinding.getCode())); // TODO
																											// modifier
																											// support
					} catch (IndexOutOfBoundsException ignored) {
					}
				}
			}
		}

		@Shadow
		public GameOptions options;

	}

	@Mixin(HeldItemRenderer.class)
	public static class ItemRendererMixin {

		@Inject(method = "renderFireOverlay", at = @At("HEAD"))
		public void transformFire(float tickDelta, CallbackInfo callback) {
			if (TweaksMod.enabled && TweaksMod.instance.lowerFireBy != 0) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, -TweaksMod.instance.lowerFireBy, 0);
			}
		}

		@Inject(method = "renderFireOverlay", at = @At("RETURN"))
		public void popFire(float tickDelta, CallbackInfo callback) {
			if (TweaksMod.enabled && TweaksMod.instance.lowerFireBy != 0)
				GlStateManager.popMatrix();
		}

	}

	@Mixin(DisconnectedScreen.class)
	public static class DisconnectedScreenMixin extends Screen {

		@Inject(method = "init", at = @At("RETURN"))
		public void postInit(CallbackInfo callback) {
			if (!(TweaksMod.enabled && TweaksMod.instance.reconnectButton))
				return;

			// whaat
			if (buttons.isEmpty())
				return;

			ButtonWidget last = buttons.get(buttons.size() - 1);
			int y = last.y;
			last.y += 24;
			buttons.add(new ButtonWidget(100, last.x, y,
					I18n.translate("sol_client.mod.tweaks.reconnect")));
		}

		@Inject(method = "buttonClicked", at = @At("HEAD"))
		public void reconnect(ButtonWidget button, CallbackInfo callback) {
			if (button.id != 100)
				return;

			ServerInfo server = ((MinecraftClientExtension) client).getPreviousServer();
			if (server == null)
				return;

			client.setScreen(new ConnectScreen(parent, client, server));
		}

		@Shadow
		private @Final Screen parent;

	}

}
