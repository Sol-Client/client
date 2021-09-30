package me.mcblueparrot.client.mixin.client;

import lombok.SneakyThrows;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.SplashScreen;
import me.mcblueparrot.client.events.*;
import me.mcblueparrot.client.util.access.AccessMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Timer;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ConcurrentModificationException;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements AccessMinecraft {

    @Shadow public GuiScreen currentScreen;

    @Shadow protected abstract void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException;

    @Shadow private TextureManager renderEngine;

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiMainMenu;<init>()V"))
    public void init(CallbackInfo callback) {
        Client.INSTANCE.init();
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    public void runTick(CallbackInfo callback) {
        Client.INSTANCE.bus.post(new TickEvent());
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;next()Z"))
    public boolean next() {
        boolean next = Mouse.next();

        if(next && Mouse.getEventButtonState()) {
            if(Client.INSTANCE.bus.post(new MouseClickEvent(Mouse.getEventButton())).cancelled) {
                next = next(); // Skip
            }
        }

        return next;
    }

    /**
     * Particles mod causes crashes.
     * This seems better than trying to fix the synchronisation issues - that would cause performance issues.
     */
    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;updateEffects()V"))
    public void concurrencyHack(EffectRenderer effectRenderer) {
        try {
            effectRenderer.updateEffects();
        }
        catch(NullPointerException | ConcurrentModificationException ignored) {
        }
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    public void loadWorld(WorldClient world, String message, CallbackInfo callback) {
        Client.INSTANCE.bus.post(new WorldLoadEvent(world));
    }

    @Inject(method = "displayGuiScreen", at = @At(value = "HEAD"))
    public void openGui(GuiScreen guiScreenIn, CallbackInfo callback) {
        if(currentScreen == null && guiScreenIn != null) {
            Client.INSTANCE.bus.post(new OpenGuiEvent(guiScreenIn));
        }
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"))
    public int onScroll() {
        int dWheel = Mouse.getEventDWheel();

        if(dWheel != 0) {
            if(Client.INSTANCE.bus.post(new ScrollEvent(dWheel)).cancelled) {
                return 0;
            }
        }


        return dWheel;
    }



    @Override
    @Accessor
    public abstract boolean isRunning();

    @Override
    @Accessor
    public abstract Timer getTimer();

}
