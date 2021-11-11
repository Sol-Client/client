package me.mcblueparrot.client.ui;

import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiScreen;
import me.mcblueparrot.client.mixin.mod.MixinSCReplayMod;

public class JGuiPreviousScreen extends net.minecraft.client.gui.GuiScreen {

    private GuiScreen previous;

    public JGuiPreviousScreen(GuiScreen previous) {
        this.previous = previous;
    }

    @Override
    public void initGui() {
        super.initGui();
        previous.display();
    }

}
