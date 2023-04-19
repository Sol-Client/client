package io.github.solclient.client.mod.impl.replay.mixins;

import java.util.Collection;

import org.spongepowered.asm.mixin.*;

import com.replaymod.replay.handler.GuiHandler;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

@Mixin(GuiHandler.class)
public class GuiHandlerMixin {

	/**
	 * @author TheKodeToad
	 * @reason we do this ourselves
	 */
	@Overwrite(remap = false)
	private void injectIntoIngameMenu(Screen screen, Collection<ButtonWidget> buttonList) {
	}

	/**
	 * @author TheKodeToad
	 * @reason we do this ourselves
	 */
	@Overwrite(remap = false)
	private void injectIntoMainMenu(Screen screen, Collection<ButtonWidget> buttonList) {
	}

}
