package me.mcblueparrot.client.mixin.client;

import com.replaymod.core.gui.GuiReplayButton;
import com.replaymod.core.versions.MCVer;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.VanillaGuiScreen;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiElement;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiTooltip;
import com.replaymod.lib.de.johni0702.minecraft.gui.layout.CustomLayout;
import com.replaymod.lib.de.johni0702.minecraft.gui.layout.LayoutData;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.lwjgl.Point;
import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.Setting;
import com.replaymod.replay.gui.screen.GuiReplayViewer;
import com.replaymod.replay.handler.GuiHandler;
import me.mcblueparrot.client.ui.ReplayButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.ui.ModsScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

import java.util.Optional;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {

    @Inject(method = "addSingleplayerMultiplayerButtons", at = @At("RETURN"))
    public void getModsButton(int x, int y, CallbackInfo callback) {
        buttonList.remove(realmsButton);
        buttonList.add(new GuiButton(realmsButton.id, realmsButton.xPosition, realmsButton.yPosition, "Mods"));

//        buttonList.add(new ReplayButton(15, realmsButton.xPosition + 202, realmsButton.yPosition));
    }

    @Redirect(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiMainMenu;" +
            "switchToRealms()V"))
    public void openModsMenu(GuiMainMenu guiMainMenu) {
        mc.displayGuiScreen(new ModsScreen(guiMainMenu));
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void openReplayMenu(GuiButton button, CallbackInfo callback) {
        if(button.id == 15) {
            new GuiReplayViewer(ReplayModReplay.instance).display();
        }
    }

    @Shadow
    private GuiButton realmsButton;

}
