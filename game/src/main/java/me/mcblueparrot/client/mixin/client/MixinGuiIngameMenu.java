
package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.replay.ReplayModReplay;

import me.mcblueparrot.client.ui.screen.IngameServerList;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

@Mixin(GuiIngameMenu.class)
public class MixinGuiIngameMenu extends GuiScreen {

	@Inject(method = "initGui", at = @At("RETURN"))
	public void addButtons(CallbackInfo callback) {
		boolean replay = ReplayModReplay.instance.getReplayHandler() != null;

		buttonList.add(new GuiButton(5000, replay ? buttonList.get(2).xPosition + 102 : width / 2 - 100,
				replay ? buttonList.get(2).yPosition : height / 4 + 56
				, 98, 20,
				I18n.format("sol_client.mod.screen.title")));

		if(!replay) {
			buttonList.add(new GuiButton(5001, width / 2 + 2, height / 4 + 56, 98, 20,
					I18n.format("sol_client.servers")));
		}
	}

	@Inject(method = "actionPerformed", at = @At("RETURN"))
	public void actionPerformed(GuiButton button, CallbackInfo callback) {
		if(button.id == 5000) {
			mc.displayGuiScreen(new ModsScreen());
		}
		else if(button.id == 5001) {
			mc.displayGuiScreen(new IngameServerList(this));
		}
	}

}
