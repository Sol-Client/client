package me.mcblueparrot.client.mixin.mod;

import com.replaymod.core.KeyBindingRegistry;
import com.replaymod.core.ReplayMod;
import com.replaymod.core.events.*;
import com.replaymod.core.gui.GuiReplaySettings;
import com.replaymod.core.versions.MCVer;
import com.replaymod.lib.de.johni0702.minecraft.gui.GuiRenderer;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiScreen;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiButton;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.advanced.AbstractGuiTimeline;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.lwjgl.ReadableDimension;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.MatrixStack;
import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.camera.CameraEntity;
import com.replaymod.replay.events.RenderHotbarCallback;
import com.replaymod.replay.gui.screen.GuiReplayViewer;
import com.replaymod.simplepathing.gui.GuiKeyframeTimeline;
import com.replaymod.simplepathing.gui.GuiPathing;
import me.mcblueparrot.client.mod.impl.SCReplayMod;
import me.mcblueparrot.client.replaymod.SCSettingsRegistry;
import me.mcblueparrot.client.tweak.Tweaker;
import me.mcblueparrot.client.ui.JGuiPreviousScreen;
import me.mcblueparrot.client.ui.ModsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class MixinSCReplayMod {

    @Mixin(Minecraft.class)
    public static class MixinMinecraft {

        @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;" +
                "setKeyBindState(IZ)V"))
        public void keyPressEvent(CallbackInfo callback) {
            if(!SCReplayMod.enabled) return;

            KeyBindingEventCallback.EVENT.invoker().onKeybindingEvent();
        }

    }

    @Mixin(EntityRenderer.class)
    public static class MixinEntityRenderer {

        @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
        public void skipRenderHand(float partialTicks, int xOffset, CallbackInfo callback) {
            if(!SCReplayMod.enabled) return;

            if(PreRenderHandCallback.EVENT.invoker().preRenderHand()) {
                callback.cancel();
            }
        }

        @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;" +
                "endStartSection(Ljava/lang/String;)V", ordinal = 18, shift = At.Shift.BEFORE))
        public void postRenderWorld(int pass, float partialTicks, long finishTimeNano, CallbackInfo callback) {
            if(!SCReplayMod.enabled) return;

            PostRenderWorldCallback.EVENT.invoker().postRenderWorld(new MatrixStack());
        }

    }

    @Mixin(GuiIngame.class)
    public static class MixinGuiIngame {

        @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
        public void skipHotbar(ScaledResolution res, float partialTicks, CallbackInfo callback) {
            if(!SCReplayMod.enabled) return;

            if(RenderHotbarCallback.EVENT.invoker().shouldRenderHotbar() == Boolean.FALSE) {
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
            return Tweaker.optifine;
        }

    }

    @Mixin(CameraEntity.class)
    public static abstract class MixinCameraEntity extends EntityPlayerSP {

        public MixinCameraEntity(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatFileWriter statFile) {
            super(mcIn, worldIn, netHandler, statFile);
        }

        /**
         * @author TheKodeToad
         * @reason No comment.
         */
        @Overwrite
        public void addChatMessage(IChatComponent message) {
            super.addChatMessage(message);
        }

    }

    @Mixin(GuiReplayViewer.class)
    public static class MixinGuiReplayViewer extends GuiScreen {

        @Inject(method = "<init>", at = @At("RETURN"), remap = false)
        public void overrideSettings(ReplayModReplay mod, CallbackInfo callback) {
            settingsButton.onClick(() -> Minecraft.getMinecraft().displayGuiScreen(
                    new ModsScreen(new JGuiPreviousScreen(this),
                    SCReplayMod.instance)));
        }

        public void display() {
            if(!SCReplayMod.enabled) {
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
            else {
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
            registry.registerKeyBinding("replaymod.input.settings", 0, () -> mc.displayGuiScreen(
                    new ModsScreen(null, SCReplayMod.instance)), false);
        }

        @Final
        @Shadow
        private static Minecraft mc;

    }

}
