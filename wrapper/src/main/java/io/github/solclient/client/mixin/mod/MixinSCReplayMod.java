package io.github.solclient.client.mixin.mod;

import java.util.Collection;

import io.github.solclient.client.launch.ClassWrapper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
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
import io.github.solclient.client.ui.screen.JGuiPreviousScreen;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.network.Packet;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class MixinSCReplayMod {

	@Mixin(MinecraftClient.class)
	public static class MixinMinecraftClient {

		@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(IZ)V"))
		public void keyPressEvent(CallbackInfo callback) {
			if (!SCReplayMod.enabled)
				return;

			KeyBindingEventCallback.EVENT.invoker().onKeybindingEvent();
		}

	}

	@Mixin(GameRenderer.class)
	public static class MixinGameRenderer {

		@Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
		public void skipRenderHand(float partialTicks, int xOffset, CallbackInfo callback) {
			if (!SCReplayMod.enabled)
				return;

			if (PreRenderHandCallback.EVENT.invoker().preRenderHand())
				callback.cancel();
		}

		@Inject(method = "renderWorld(IFJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;"
				+ "swap(Ljava/lang/String;)V", ordinal = 18, shift = At.Shift.BEFORE))
		public void postRenderWorld(int pass, float partialTicks, long finishTimeNano, CallbackInfo callback) {
			if (!SCReplayMod.enabled)
				return;

			PostRenderWorldCallback.EVENT.invoker().postRenderWorld(new MatrixStack());
		}

	}

	@Mixin(InGameHud.class)
	public static class MixinInGameHud {

		@Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
		public void skipHotbar(Window window, float partialTicks, CallbackInfo callback) {
			if (!SCReplayMod.enabled)
				return;

			if (RenderHotbarCallback.EVENT.invoker().shouldRenderHotbar() == Boolean.FALSE)
				callback.cancel();
		}

	}

	@Mixin(MCVer.class)
	public static class MixinMCVer {

		/**
		 * @author TheKodeToad - blame me
		 * @reason no one else will be messing with this
		 */
		@Overwrite
		public static boolean hasOptifine() {
			return ClassWrapper.OPTIFINE;
		}

	}

	@Mixin(CameraEntity.class)
	public static abstract class MixinCameraEntity extends ClientPlayerEntity {

		public MixinCameraEntity(MinecraftClient client, World world, ClientPlayNetworkHandler networkHandler, StatHandler stats) {
			super(client, world, networkHandler, stats);
		}

		/**
		 * @author TheKodeToad
		 * @reason No comment.
		 */
		@Override
		@Overwrite
		public void sendMessage(Text message) {
			super.sendMessage(message);
		}

	}

	@Mixin(GuiReplayViewer.class)
	public static class MixinGuiReplayViewer extends GuiScreen {

		@Inject(method = "<init>", at = @At("RETURN"), remap = false)
		public void overrideSettings(ReplayModReplay mod, CallbackInfo callback) {
			MinecraftClient.getInstance().currentScreen = new JGuiPreviousScreen(this);
			settingsButton
					.onClick(() -> MinecraftClient.getInstance().setScreen(new ModsScreen(SCReplayMod.instance)));
		}

		@Override
		public void display() {
			if (!SCReplayMod.enabled)
				MinecraftClient.getInstance().setScreen(null);
			else
				super.display();
		}

		@Shadow
		public @Final GuiButton settingsButton;

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
					() -> mc.setScreen(new ModsScreen(SCReplayMod.instance)), false);
		}

		@Final
		@Shadow
		private static MinecraftClient mc;

	}

	@Mixin(GuiSavingReplay.class)
	public static class MixinGuiSavingReplay {

		@Redirect(method = "presentRenameDialog", at = @At(value = "INVOKE", target = "Lio/github/solclient/client/mod/impl/replay/fix"
				+ "/SCSettingsRegistry;get(Lio/github/solclient/client/mod/impl/replay/fix/SCSettingsRegistry$SettingKey;)"
				+ "Ljava/lang/Object;"), remap = false)
		public Object saveAnyway(SCSettingsRegistry instance, SCSettingsRegistry.SettingKey settingKey) {
			return SCReplayMod.instance.renameDialog && SCReplayMod.enabled
					&& !(SCReplayMod.deferedState == Boolean.FALSE && MinecraftClient.getInstance().world == null);
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

	@Mixin(GuiRecordingOverlay.class)
	public static class MixinGuiRecordingOverlay {

		@Inject(method = "<init>", at = @At("RETURN"))
		public void postInit(CallbackInfo callback) {
			RecordingIndicator.guiControls = guiControls;
		}

		/**
		 * @author TheKodeToad
		 * @reason we do this anyway
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
		 * @reason we do this ourselves
		 */
		@Overwrite(remap = false)
		private void injectIntoIngameMenu(Screen screen, Collection<ButtonWidget> buttonList) {
		}

	}

	@Mixin(PacketListener.class)
	public static class MixinPacketListener {

		@Inject(method = "save(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
		public void handleChat(Packet<?> packet, CallbackInfo callback) {
			if (packet instanceof ChatMessageS2CPacket) {
				String messageString = Formatting.strip(
						((ChatMessageS2CPacket) packet).getMessage().asUnformattedString());

				if (Client.INSTANCE.getEvents().post(new ReceiveChatMessageEvent(
						((ChatMessageS2CPacket) packet).getType() == 2, messageString, true)).cancelled)
					callback.cancel();
			}
		}

	}

}
