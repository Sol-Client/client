package me.mcblueparrot.client.mixin.client;

import java.io.IOException;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.ui.screen.SearchableResourcePackList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiResourcePackAvailable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.ResourcePackListEntry;

@Mixin(GuiScreenResourcePacks.class)
public class MixinGuiScreenResourcePacks extends GuiScreen {

	private GuiTextField searchField;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Redirect(method = "initGui", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
			ordinal = 1))
	public <E> boolean moveApplyButton(List list, E e) {
		GuiButton button = (GuiButton) e;
		return list.add(new GuiButton(button.id, button.xPosition + 25, button.yPosition, 150, 20,
				"Apply"));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Redirect(method = "initGui", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
			ordinal = 0))
	public <E> boolean moveOpenButton(List list, E e) {
		GuiButton button = (GuiButton) e;
		return list.add(new GuiButton(button.id, width / 2 + 29, height - 26, 150, 20,
				"Pack " +
				"Folder"));
	}

	@Inject(method = "initGui", at = @At("RETURN"))
	public void addSearch(CallbackInfo callback) {
		searchField = new GuiTextField(3, mc.fontRendererObj, width / 2 - 203, height - 47, 198,
				20);
	}

	@Inject(method = "drawScreen", at = @At("RETURN"))
	public void drawSearch(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
		searchField.drawTextBox();
	}

	@Inject(method = "mouseClicked", at = @At("RETURN"))
	public void handleSearchMouseInput(int mouseX, int mouseY, int mouseButton, CallbackInfo callback) {
		searchField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		searchField.textboxKeyTyped(typedChar, keyCode);
	}
	
	@Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui" +
			"/GuiScreenResourcePacks;drawCenteredString(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;III)" +
			"V", ordinal = 1))
	public void removeText(GuiScreenResourcePacks guiScreenResourcePacks, FontRenderer fontRendererIn, String text,
						   int x, int y, int color) {
	}

	@Redirect(method = "initGui", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui" +
			"/GuiScreenResourcePacks;availableResourcePacksList:Lnet/minecraft/client/gui/GuiResourcePackAvailable;",
			ordinal = 0))
	public void searchableList(GuiScreenResourcePacks screen, GuiResourcePackAvailable availableResourcePacksList) {
		this.availableResourcePacksList = new SearchableResourcePackList(mc, availableResourcePacksList.getListWidth(),
			height,
				availableResourcePacks, () -> searchField.getText());
	}

	@Shadow
	private List<ResourcePackListEntry> availableResourcePacks;

	@Shadow
	private GuiResourcePackAvailable availableResourcePacksList;

}
