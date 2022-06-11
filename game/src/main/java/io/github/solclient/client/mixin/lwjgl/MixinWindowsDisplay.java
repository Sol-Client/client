package io.github.solclient.client.mixin.lwjgl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

@Mixin(targets = "org.lwjgl.opengl.WindowsDisplay")
public abstract class MixinWindowsDisplay {

    @Inject(method = "doHandleMessage", at = @At("HEAD"), cancellable = true, remap = false)
    private void doHandleMessage(long hwnd, int msg, long wParam, long lParam, long millis, CallbackInfoReturnable<Long> callback) {
        if(msg == 0x020B) {
            handleMouseButton(wParam >> 16 == 1L ? 3 : 4, 1, millis);
            callback.setReturnValue(defWindowProc(hwnd, msg, wParam, lParam));
        }
    }

    @Shadow
    private static native long defWindowProc(long hwnd, int msg, long wParam, long lParam);

    @Shadow
    protected abstract void handleMouseButton(int button, int state, long millis);

}