package me.mcblueparrot.client.mixin.client;

import java.util.ConcurrentModificationException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import lombok.SneakyThrows;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.SplashScreen;
import me.mcblueparrot.client.events.MouseClickEvent;
import me.mcblueparrot.client.events.OpenGuiEvent;
import me.mcblueparrot.client.events.ScrollEvent;
import me.mcblueparrot.client.events.TickEvent;
import me.mcblueparrot.client.events.WorldLoadEvent;
import me.mcblueparrot.client.util.access.AccessGuiNewChat;
import me.mcblueparrot.client.util.access.AccessMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Timer;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements AccessMinecraft {

    private boolean debugPressed;
    private boolean cancelDebug;

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

    @Redirect(method = "createDisplay",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V"))
    public void overrideTitle(String oldTitle) {
        Display.setTitle(Client.NAME + " on " + oldTitle);
    }

    @Inject(method = "setServerData", at = @At("HEAD"))
    public void onDisconnect(ServerData serverDataIn, CallbackInfo callback) {
        if(serverDataIn == null) {
            Client.INSTANCE.onServerChange(null);
        }
    }

    @Override
    @Accessor
    public abstract boolean isRunning();

    @Override
    @Accessor
    public abstract Timer getTimer();


    @Shadow
    public GuiScreen currentScreen;

    @Shadow
    protected abstract void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException;

    @Shadow
    private TextureManager renderEngine;

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

    // region Better F3
    // Backport 1.9 F3 fixes

    @Redirect(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;" +
            "showDebugInfo:Z",
            ordinal = 1))
    public void preventDefaultF3(GameSettings settings, boolean f3) {
    }

    @Inject(method = "dispatchKeypresses", at = @At(value = "HEAD"))
    public void updateF3(CallbackInfo callback) {
        boolean debugPressed = Keyboard.isKeyDown(61);
        if(this.debugPressed && !debugPressed) {
            if(cancelDebug) {
                cancelDebug = false;
            }
            else {
                gameSettings.showDebugInfo = !gameSettings.showDebugInfo;
            }
        }
        this.debugPressed = debugPressed;
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;" +
            "clearChatMessages()V"))
    public void betterClearMessages(GuiNewChat guiNewChat) {
        ((AccessGuiNewChat) guiNewChat).clearChat();
        cancelDebug = true;
    }

    @Inject(method = "runTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;refreshResources()V"))
    public void betterRefreshResources(CallbackInfo callback) {
        cancelDebug = true;
    }

    @Inject(method = "runTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;setOptionValue(Lnet/minecraft/client/settings/GameSettings$Options;I)V"))
    public void betterRenderDistance(CallbackInfo callback) {
        cancelDebug = true;
    }

    @Inject(method = "runTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;loadRenderers()V"))
    public void betterLoadRenderers(CallbackInfo callback) {
        cancelDebug = true;
    }

    @Inject(method = "runTick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/settings/GameSettings;saveOptions()V"))
    public void betterTootipsAndPauseOnLostFocus(CallbackInfo callback) {
        cancelDebug = true;
    }

    @Inject(method = "runTick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/RenderManager;setDebugBoundingBox(Z)V"))
    public void betterHitboxes(CallbackInfo callback) {
        cancelDebug = true;
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;setKeyBindState(IZ)V"))
    public void nextKey(int keyCode, boolean pressed) {
        if(pressed && debugPressed) {
            if(Keyboard.getEventKey() == 32
                    || Keyboard.getEventKey() == 31
                    || Keyboard.getEventKey() == 20
                    || Keyboard.getEventKey() == 33
                    || Keyboard.getEventKey() == 30
                    || Keyboard.getEventKey() == 35
                    || Keyboard.getEventKey() == 48
                    || Keyboard.getEventKey() == 25) {
                return;
            }
        }

        KeyBinding.setKeyBindState(keyCode, pressed);
    }

    // endregion

    private boolean hadWorld;

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("RETURN"))
    public void onWorldLoad(WorldClient world, String loadingText, CallbackInfo callback) {
    	if(world == null && hadWorld) {
            hadWorld = false;
    	}
    	else if(world != null && !hadWorld) {
    	    hadWorld = true;
    	    Client.INSTANCE.onServerChange(currentServerData);
    	}
    }

    @Shadow
    public String debug;

    @Shadow
    public GuiAchievement guiAchievement;

    @Shadow
    public GameSettings gameSettings;

    @Shadow
    public ServerData currentServerData;

}
