/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mixin.client;

import java.io.IOException;
import java.util.List;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.ui.screen.BetterResourcePackList;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.resourcepack.*;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.*;

@Mixin(ResourcePackScreen.class)
public class ResourcePackScreenMixin extends Screen {

	private TextFieldWidget searchField;

	private int availablePacksX;
	private int availablePacksY;
	private Rectangle arrowBounds;
	private BetterResourcePackList betterList;
	private int arrowX;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Redirect(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1))
	public <E> boolean moveApplyButton(List list, E e) {
		ButtonWidget button = (ButtonWidget) e;
		return list.add(new ButtonWidget(button.id, button.x + 25, button.y, 150, 20,
				I18n.translate("sol_client.packs.apply")));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Redirect(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
	public <E> boolean moveOpenButton(List list, E e) {
		ButtonWidget button = (ButtonWidget) e;
		return list.add(new ButtonWidget(button.id, width / 2 + 29, height - 26, 150, 20,
				I18n.translate("sol_client.packs.open_folder")));
	}

	@Inject(method = "init", at = @At("RETURN"))
	public void addSearch(CallbackInfo callback) {
		searchField = new TextFieldWidget(3, client.textRenderer, width / 2 - 203, height - 47, 198, 20);

		availablePacksX = width / 2 - 4 - 200;
		availablePacksY = 36;
		arrowX = availablePacksX + 8;
		arrowBounds = new Rectangle(availablePacksX, availablePacksY - 5, 200, 20);
	}

	@Inject(method = "render", at = @At("RETURN"))
	public void drawSearch(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
		searchField.render();
	}

	@Inject(method = "mouseClicked", at = @At("RETURN"))
	public void onMouseClick(int mouseX, int mouseY, int mouseButton, CallbackInfo callback) {
		searchField.mouseClicked(mouseX, mouseY, mouseButton);

		if (arrowBounds.contains(mouseX, mouseY) && mouseButton == 0)
			betterList.up();
	}

	@Override
	protected void keyPressed(char character, int code) {
		super.keyPressed(character, code);

		if (character > 31 && !searchField.isFocused()) {
			searchField.setText("");
			searchField.setFocused(true);
		}

		searchField.keyPressed(character, code);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen"
			+ "/ResourcePackScreen;drawCenteredString(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)"
			+ "V", ordinal = 1))
	public void removeText(ResourcePackScreen instance, TextRenderer textRenderer, String text, int x, int y,
			int color) {
	}

	@Inject(method = "render", at = @At("RETURN"))
	public void drawHeaders(int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
		String text = betterList.getFolder().getName();

		if (betterList.delegate == null)
			text = Formatting.BOLD + I18n.translate("resourcePack.available.title");
		else {
			GlStateManager.color(1, 1, 1);
			client.getTextureManager().bindTexture(new Identifier("textures/gui/resource_pack_up.png"));

			DrawableHelper.drawTexture(arrowX, arrowBounds.getY(), 0, arrowBounds.contains(mouseX, mouseY) ? 16 : 0, 16,
					16, 16, 32);
		}

		client.textRenderer.drawWithShadow(text, availablePacksX + 100 - (client.textRenderer.getStringWidth(text) / 2),
				availablePacksY - 1, -1);

		String selectedText = Formatting.BOLD + I18n.translate("resourcePack.selected.title");

		int selectedPacksX = width / 2 + 4;

		client.textRenderer.drawWithShadow(selectedText,
				selectedPacksX + 100 - (client.textRenderer.getStringWidth(selectedText) / 2), availablePacksY - 1, -1);
	}

	@Redirect(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen"
			+ "/ResourcePackScreen;availablePacks:Lnet/minecraft/client/gui/screen/resourcepack/AvailableResourcePackListWidget;", ordinal = 0))
	public void searchableList(ResourcePackScreen instance, AvailableResourcePackListWidget value) {
		try {
			availablePacks = betterList = new BetterResourcePackList(client, (ResourcePackScreen) (Object) this,
					value.getRowWidth(), height, client.getResourcePackLoader(), () -> searchField.getText());
		} catch (IOException error) {
		}
	}

	@Shadow
	private List<ResourcePackWidget> availablePackList;

	@Shadow
	private AvailableResourcePackListWidget availablePacks;

	@Shadow
	private SelectedResourcePackListWidget selectedPacks;

}
