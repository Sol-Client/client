package me.mcblueparrot.client.mixin.mod;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.main.GameConfiguration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.mod.impl.BorderlessFullscreenMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

@Mixin(BorderlessFullscreenMod.class)
public class MixinBorderlessFullscreenMod {

	@Mixin(Minecraft.class)
	public static abstract class MixinMinecraft {

		@Shadow
		protected abstract void updateDisplayMode() throws LWJGLException;

		@Shadow
		public int displayWidth;
		@Shadow
		public int displayHeight;

		@Shadow
		protected abstract void resize(int width, int height);

		@Shadow
		public GuiScreen currentScreen;

		@Shadow
		protected abstract void updateFramebufferSize();

		@Expose
		private GameSettings gameSettings;

		@Expose
		private boolean fullscreen;

		@Final
		@Expose
		private static Logger logger;

		private int previousWidth;
		private int previousHeight;

		@Inject(method = "toggleFullscreen", at = @At("HEAD"), cancellable = true)
		public void borderlessFullscreen(CallbackInfo callback) {
			if(!BorderlessFullscreenMod.enabled) return;

			callback.cancel();

			fullscreen = !fullscreen;
			gameSettings.fullScreen = fullscreen;
			gameSettings.saveOptions();

			if(fullscreen) {
				System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

				previousWidth = displayWidth;
				previousHeight = displayHeight;

				if(currentScreen != null) {
					resize(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight());
				}
				else {
					displayWidth = Display.getDesktopDisplayMode().getWidth();
					displayHeight = Display.getDesktopDisplayMode().getHeight();

					updateFramebufferSize();
				}

				try {
					updateDisplayMode();
				}
				catch(LWJGLException error) {
					logger.error("Could not go into fullscreen", error);
				}
			}
			else {
				System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");

				if(currentScreen != null) {
					resize(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight());
				}
				else {
					displayWidth = previousWidth;
					displayHeight = previousHeight;

					updateFramebufferSize();
				}

				try {
					updateDisplayMode();
				}
				catch(LWJGLException error) {
					logger.error("Could not go out of fullscreen", error);
				}
			}
		}
	}

}
