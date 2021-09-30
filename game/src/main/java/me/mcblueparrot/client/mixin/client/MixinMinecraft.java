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

    @Shadow public abstract void updateDisplay();

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

    // region Splash Screen Rendering

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/SkinManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V"))
    public void splashSkinManager(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/storage/AnvilSaveConverter;<init>(Ljava/io/File;)V"))
    public void splashSaveLoader(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/audio/SoundHandler;<init>(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/settings/GameSettings;)V"))
    public void splashSoundHandler(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/audio/MusicTicker;<init>(Lnet/minecraft/client/Minecraft;)V"))
    public void splashMusicTicker(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;<init>(Lnet/minecraft/client/settings/GameSettings;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/TextureManager;Z)V"))
    public void splashFontRenderer(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/MouseHelper;<init>()V"))
    public void splashMouseHelper(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureMap;<init>(Ljava/lang/String;)V"))
    public void splashTextureMap(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/ModelManager;<init>(Lnet/minecraft/client/renderer/texture/TextureMap;)V"))
    public void splashModelManager(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/RenderItem;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/resources/model/ModelManager;)V"))
    public void splashRenderItem(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/RenderManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/entity/RenderItem;)V"))
    public void splashRenderManager(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemRenderer;<init>(Lnet/minecraft/client/Minecraft;)V"))
    public void splashItemRenderer(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/EntityRenderer;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V"))
    public void splashEntityRenderer(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;<init>(Lnet/minecraft/client/renderer/BlockModelShapes;Lnet/minecraft/client/settings/GameSettings;)V"))
    public void splashBlockRenderDispatcher(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;<init>(Lnet/minecraft/client/Minecraft;)V"))
    public void splashRenderGlobal(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/achievement/GuiAchievement;<init>(Lnet/minecraft/client/Minecraft;)V"))
    public void splashGuiAchivement(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/particle/EffectRenderer;<init>(Lnet/minecraft/world/World;Lnet/minecraft/client/renderer/texture/TextureManager;)V"))
    public void splashEffectRenderer(CallbackInfo callback) {
        draw();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiIngame;<init>(Lnet/minecraft/client/Minecraft;)V"))
    public void splashGuiIngame(CallbackInfo callback) {
        draw();
    }

    @SneakyThrows
    private void draw() {
        drawSplashScreen(renderEngine);
    }

    @Inject(method = "drawSplashScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;" +
            "updateDisplay()V", shift = At.Shift.BEFORE))
    public void drawProgress(TextureManager textureManagerInstance, CallbackInfo callback) {
        SplashScreen.INSTANCE.draw();
    }

    // endregion

    @Override
    @Accessor
    public abstract boolean isRunning();

    @Override
    @Accessor
    public abstract Timer getTimer();

}
