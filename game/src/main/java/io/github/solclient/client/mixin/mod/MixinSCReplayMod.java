package io.github.solclient.client.mixin.mod;

import java.util.Collection;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.compat.optifine.OptifineReflection;
import com.replaymod.core.*;
import com.replaymod.core.events.*;
import com.replaymod.core.versions.MCVer;
import com.replaymod.lib.de.johni0702.minecraft.gui.GuiRenderer;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiScreen;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.AbstractGuiSlider;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiButton;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.MatrixStack;
import com.replaymod.recording.gui.*;
import com.replaymod.recording.packet.PacketListener;
import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.camera.*;
import com.replaymod.replay.events.RenderHotbarCallback;
import com.replaymod.replay.gui.screen.GuiReplayViewer;
import com.replaymod.replay.handler.GuiHandler;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.ReceiveChatMessageEvent;
import io.github.solclient.client.mod.impl.replay.*;
import io.github.solclient.client.mod.impl.replay.fix.SCSettingsRegistry;
import io.github.solclient.client.tweak.Tweaker;
import io.github.solclient.client.ui.screen.JGuiPreviousScreen;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class MixinSCReplayMod {

	@Mixin(Minecraft.class)
	public static class MixinMinecraft {

		@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;"
				+ "setKeyBindState(IZ)V"))
		public void keyPressEvent(CallbackInfo callback) {
			if (!SCReplayMod.enabled)
				return;

			KeyBindingEventCallback.EVENT.invoker().onKeybindingEvent();
		}

	}

	@Mixin(EntityRenderer.class)
	public static class MixinEntityRenderer {

		@Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
		public void skipRenderHand(float partialTicks, int xOffset, CallbackInfo callback) {
			if (!SCReplayMod.enabled)
				return;

			if (PreRenderHandCallback.EVENT.invoker().preRenderHand()) {
				callback.cancel();
			}
		}

		@Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;"
				+ "endStartSection(Ljava/lang/String;)V", ordinal = 18, shift = At.Shift.BEFORE))
		public void postRenderWorld(int pass, float partialTicks, long finishTimeNano, CallbackInfo callback) {
			if (!SCReplayMod.enabled)
				return;

			PostRenderWorldCallback.EVENT.invoker().postRenderWorld(new MatrixStack());
		}

	}

	@Mixin(GuiIngame.class)
	public static class MixinGuiIngame {

		@Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
		public void skipHotbar(ScaledResolution res, float partialTicks, CallbackInfo callback) {
			if (!SCReplayMod.enabled)
				return;

			if (RenderHotbarCallback.EVENT.invoker().shouldRenderHotbar() == Boolean.FALSE) {
				callback.cancel();
			}
		}

	}

	@Mixin(MCVer.class)
	public static class MixinMCVer {

		/**
		 * @author TheKodeToad - blame me
		 * @reason Why not?
		 */
		@Overwrite
		public static boolean hasOptifine() {
			return Tweaker.optiFine;
		}

	}

	@Mixin(CameraEntity.class)
	public static abstract class MixinCameraEntity extends EntityPlayerSP {

		public MixinCameraEntity(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler,
				StatFileWriter statFile) {
			super(mcIn, worldIn, netHandler, statFile);
		}

		/**
		 * @author TheKodeToad
		 * @reason No comment.
		 */
		@Override
		@Overwrite
		public void addChatMessage(IChatComponent message) {
			super.addChatMessage(message);
		}

	}

	@Mixin(GuiReplayViewer.class)
	public static class MixinGuiReplayViewer extends GuiScreen {

		@Inject(method = "<init>", at = @At("RETURN"), remap = false)
		public void overrideSettings(ReplayModReplay mod, CallbackInfo callback) {
			Minecraft.getMinecraft().currentScreen = new JGuiPreviousScreen(this);
			settingsButton
					.onClick(() -> Minecraft.getMinecraft().displayGuiScreen(new ModsScreen(SCReplayMod.instance)));
		}

		@Override
		public void display() {
			if (!SCReplayMod.enabled) {
				Minecraft.getMinecraft().displayGuiScreen(null);
			} else {
				super.display();
			}
		}

		@Final
		@Shadow
		public GuiButton settingsButton;

	}

	@Mixin(ReplayMod.class)
	public static class MixinReplayMod {

		/**
		 * @author TheKodeToad
		 * @reason Overwrites are not always a crime.
		 */
		@Overwrite(remap = false)
		public void registerKeyBindings(KeyBindingRegistry registry) {
			registry.registerKeyBinding("replaymod.input.settings", 0,
					() -> mc.displayGuiScreen(new ModsScreen(SCReplayMod.instance)), false);
		}

		@Final
		@Shadow
		private static Minecraft mc;

	}

	@Mixin(GuiSavingReplay.class)
	public static class MixinGuiSavingReplay {

		@Redirect(method = "presentRenameDialog", at = @At(value = "INVOKE", target = "Lio/github/solclient/client/mod/impl/replay/fix"
				+ "/SCSettingsRegistry;get(Lio/github/solclient/client/mod/impl/replay/fix/SCSettingsRegistry$SettingKey;)"
				+ "Ljava/lang/Object;"), remap = false)
		public Object saveAnyway(SCSettingsRegistry instance, SCSettingsRegistry.SettingKey settingKey) {
			return SCReplayMod.instance.renameDialog && SCReplayMod.enabled
					&& !(SCReplayMod.deferedState == Boolean.FALSE && Minecraft.getMinecraft().theWorld == null);
		}

	}

	@Mixin(ClassicCameraController.class)
	public static class MixinClassicCameraController {

		private static final double SPEED_MODIFIER = 1;

		@ModifyConstant(method = "decreaseSpeed", constant = @Constant(doubleValue = 0.5D), remap = false)
		public double getDecreaseSpeedModifier(double original) {
			return SPEED_MODIFIER;
		}

		@ModifyConstant(method = "increaseSpeed", constant = @Constant(doubleValue = 0.5D), remap = false)
		public double getIncreaseSpeedModifier(double original) {
			return SPEED_MODIFIER;
		}

	}

	@Mixin(VanillaCameraController.class)
	public static class MixinVanillaCameraController {

		private static final int SPEED_MODIFIER = 64;

		@ModifyConstant(method = "decreaseSpeed", constant = @Constant(intValue = 1), remap = false)
		public int getDecreaseSpeedModifier(int original) {
			return SPEED_MODIFIER;
		}

		@ModifyConstant(method = "increaseSpeed", constant = @Constant(intValue = 1), remap = false)
		public int getIncreaseSpeedModifier(int original) {
			return SPEED_MODIFIER;
		}

	}

	@Mixin(OptifineReflection.class)
	public static class MixinOptifineReflection {

		@ModifyConstant(method = "<clinit>", constant = @Constant(stringValue = "Config"), remap = false)
		private static String getConfigClass(String clazz) {
			return "net.minecraft.src." + clazz;
		}

	}

	@Mixin(GuiRecordingOverlay.class)
	public static class MixinGuiRecordingOverlay {

		@Inject(method = "<init>", at = @At("RETURN"))
		public void postInit(CallbackInfo callback) {
			RecordingIndicator.guiControls = guiControls;
		}

		/**
		 * @author TheKodeToad
		 */
		@Overwrite(remap = false)
		private void renderRecordingIndicator(MatrixStack stack) {
			// Overwritten by the HUD.
		}

		@Shadow
		private @Final GuiRecordingControls guiControls;

	}

	@Mixin(AbstractGuiSlider.class)
	public static class MixinAbstractGuiSlider {

		@Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lcom/replaymod/lib/de/johni0702/minecraft/gui/GuiRenderer;drawCenteredString(IIILjava/lang/String;)I"), remap = false)
		public int useShadow(GuiRenderer instance, int x, int y, int colour, String text) {
			return instance.drawCenteredString(x, y, colour, text, true);
		}

	}

	@Mixin(GuiHandler.class)
	public static class MixinGuiHandler {

		/**
		 * @author TheKodeToad
		 */
		@Overwrite(remap = false)
		private void injectIntoMainMenu(net.minecraft.client.gui.GuiScreen guiScreen,
				Collection<net.minecraft.client.gui.GuiButton> buttonList) {
		}

	}

	@Mixin(PacketListener.class)
	public static class MixinPacketListener {

		@Inject(method = "save", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Ljava/lang/System;currentTimeMillis()J"), cancellable = true)
		public void handleChat(Packet packet, CallbackInfo callback) {
			if (packet instanceof S02PacketChat) {
				String messageString = EnumChatFormatting.getTextWithoutFormattingCodes(
						((S02PacketChat) packet).getChatComponent().getUnformattedText());

				if (Client.INSTANCE.bus.post(new ReceiveChatMessageEvent(((S02PacketChat) packet).getType() == 2,
						messageString, true)).cancelled) {
					callback.cancel();
				}
			}
		}

	}

}
