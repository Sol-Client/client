package me.mcblueparrot.client.mixin.client;

import java.io.IOException;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.ui.screen.BetterResourcePackList;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiResourcePackAvailable;
import net.minecraft.client.gui.GuiResourcePackSelected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

@Mixin(GuiScreenResourcePacks.class)
public class MixinGuiScreenResourcePacks extends GuiScreen {

	private GuiTextField searchField;

	private int availablePacksX;
	private int availablePacksY;
	private Rectangle arrowBounds;
	private BetterResourcePackList betterList;
	private int arrowX;

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
		return list.add(new GuiButton(button.id, width / 2 + 29, height - 26, 150, 20, "Pack " + "Folder"));
	}

	@Inject(method = "initGui", at = @At("RETURN"))
	public void addSearch(CallbackInfo callback) {
		searchField = new GuiTextField(3, mc.fontRendererObj, width / 2 - 203, height - 47, 198,
				20);

		availablePacksX = width / 2 - 4 - 200;
		availablePacksY = 36;
		arrowX = availablePacksX + 8;
		arrowBounds = new Rectangle(availablePacksX, availablePacksY - 5, 200, 20);
	}

	@Inject(method = "drawScreen", at = @At("RETURN"))
	public void drawSearch(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
		searchField.drawTextBox();
	}

	@Inject(method = "mouseClicked", at = @At("RETURN"))
	public void onMouseClick(int mouseX, int mouseY, int mouseButton, CallbackInfo callback) {
		searchField.mouseClicked(mouseX, mouseY, mouseButton);

		if(arrowBounds.contains(mouseX, mouseY) && mouseButton == 0) {
			betterList.up();
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if(typedChar > 31 && !searchField.isFocused()) {
			searchField.setText("");
			searchField.setFocused(true);
		}

		searchField.textboxKeyTyped(typedChar, keyCode);
	}

	@Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui" +
			"/GuiScreenResourcePacks;drawCenteredString(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;III)" +
			"V", ordinal = 1))
	public void removeText(GuiScreenResourcePacks guiScreenResourcePacks, FontRenderer fontRendererIn, String text,
						   int x, int y, int color) {
	}

	@Inject(method = "drawScreen", at = @At("RETURN"))
	public void drawHeaders(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
		String text = betterList.getFolder().getName();

		if(betterList.delegate == null) {
			text = EnumChatFormatting.BOLD + I18n.format("resourcePack.available.title");
		}
		else {
			GlStateManager.color(1, 1, 1);
			mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/resource_pack_up.png"));

			Gui.drawModalRectWithCustomSizedTexture(arrowX, arrowBounds.getY(), 0,
					arrowBounds.contains(mouseX, mouseY) ? 16 : 0, 16, 16, 16, 32);
		}

		mc.fontRendererObj.drawStringWithShadow(text, availablePacksX + 100 - (mc.fontRendererObj.getStringWidth(text) / 2), availablePacksY - 1, -1);

		String selectedText = EnumChatFormatting.BOLD + I18n.format("resourcePack.selected.title");

		int selectedPacksX = width / 2 + 4;

		mc.fontRendererObj.drawStringWithShadow(selectedText,
				selectedPacksX + 100 - (mc.fontRendererObj.getStringWidth(selectedText) / 2), availablePacksY - 1, -1);
	}

	@Redirect(method = "initGui", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui" +
			"/GuiScreenResourcePacks;availableResourcePacksList:Lnet/minecraft/client/gui/GuiResourcePackAvailable;",
			ordinal = 0))
	public void searchableList(GuiScreenResourcePacks screen, GuiResourcePackAvailable availableResourcePacksList) {
		try {
			this.availableResourcePacksList = betterList = new BetterResourcePackList(mc, (GuiScreenResourcePacks) (Object) this,
					availableResourcePacksList.getListWidth(), height, mc.getResourcePackRepository(),
					() -> searchField.getText());
		}
		catch(IOException error) {
		}
	}

	@Shadow
	private List<ResourcePackListEntry> availableResourcePacks;

	@Shadow
	private GuiResourcePackAvailable availableResourcePacksList;

	@Shadow
	private GuiResourcePackSelected selectedResourcePacksList;

}
