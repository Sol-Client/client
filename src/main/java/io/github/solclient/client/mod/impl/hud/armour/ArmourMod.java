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

package io.github.solclient.client.mod.impl.hud.armour;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.Window;
import net.minecraft.item.*;

public class ArmourMod extends SolClientHudMod {

	private static final ItemStack HELMET = new ItemStack(Items.IRON_HELMET);
	private static final ItemStack CHESTPLATE = new ItemStack(Items.IRON_CHESTPLATE);
	private static final ItemStack LEGGINGS = new ItemStack(Items.IRON_LEGGINGS);
	private static final ItemStack BOOTS = new ItemStack(Items.IRON_BOOTS);
	private static final ItemStack HAND = new ItemStack(Items.DIAMOND_SWORD);

	@Expose
	@Option
	private DurabilityDisplay durability = DurabilityDisplay.REMAINING;
	@Expose
	@Option
	private boolean armour = true;
	@Expose
	@Option
	private boolean hand = true;
	@Expose
	@Option
	private boolean horizontal;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean shadow = true;
	@Expose
	@ColourKey(ColourKey.TEXT_COLOUR)
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private Colour textColour = Colour.WHITE;

	@Override
	public Rectangle getBounds(Position position) {
		if (horizontal) {
			int width = 1;

			if (armour) {
				switch (durability) {
					case FRACTION:
						width += 296;
						break;
					case REMAINING:
						width += 168;
						break;
					case PERCENTAGE:
						width += 192;
						break;
					default:
						width += 73;
						break;
				}
			}

			if (hand) {
				switch (durability) {
					case FRACTION:
						width += 85;
						break;
					case REMAINING:
						width += 47;
						break;
					case PERCENTAGE:
						width += 48;
						break;
					default:
						width += 17;
						break;
				}
			}

			return new Rectangle(position.getX() - 1, position.getY(), width, 18);
		}
		int height = 1;

		if (armour) {
			height += 15 * 4;
		}

		if (hand) {
			height += 15;
		}

		return new Rectangle(position.getX() - 1, position.getY(), durability.getWidth() + 18, height);
	}

	@Override
	public void render(Position position, boolean editMode) {
		DiffuseLighting.enable();

		Window window = new Window(mc);
		boolean rtl = !horizontal && position.getX() > window.getWidth() / 2;

		int x = position.getX(), y = position.getY();

		if (horizontal) {
			y++;
		}

		ItemStack[] playerArmour;
		ItemStack handItem;

		if (mc.player == null || editMode) {
			playerArmour = new ItemStack[] { BOOTS, LEGGINGS, CHESTPLATE, HELMET };
			handItem = HAND;
		} else {
			playerArmour = mc.player.inventory.armor;
			handItem = mc.player.inventory.getMainHandStack();
		}

		if (armour) {
			for (int i = 0; i < 4; i++) {
				ItemStack stack = playerArmour[3 - i];
				if (stack != null) {
					int width = renderStack(stack, x, y, rtl);
					if (horizontal) {
						if (width != 0) {
							x += 24 + width;
						} else {
							x += 18;
						}
					}
				}
				if (!horizontal) {
					y += 15;
				}
			}
		}

		if (hand && handItem != null) {
			renderStack(handItem, x, y, rtl);
		}

		DiffuseLighting.disable();
	}

	private int renderStack(ItemStack stack, int x, int y, boolean rtl) {
		boolean hasDurability = durability != DurabilityDisplay.OFF;
		int itemX = x;

		if (rtl && hasDurability) {
			itemX += durability.getWidth() - 1;
		}

		mc.getItemRenderer().renderInGuiWithOverrides(stack, itemX, y);
		mc.getItemRenderer().renderGuiItemOverlay(font, stack, itemX, y);
		GlStateManager.disableLighting();

		if (hasDurability && stack.getMaxDamage() > 0) {
			String text;
			switch (durability) {
				case FRACTION:
					text = stack.getMaxDamage() - stack.getDamage() + " / " + (stack.getMaxDamage());
					break;
				case REMAINING:
					text = Integer.toString(stack.getMaxDamage() - stack.getDamage());
					break;
				case PERCENTAGE:
					text = ((int) (((double) stack.getMaxDamage() - stack.getDamage()) / (stack.getMaxDamage()) * 100))
							+ "%";
					break;
				default:
					text = "??";
					break;
			}

			if (rtl) {
				x += durability.getWidth() - 4 - font.getStringWidth(text);
			} else {
				x += 20;
			}

			font.draw(text, x, y + 4, textColour.getValue(), shadow);

			return font.getStringWidth(text);
		}

		return 0;
	}

}
