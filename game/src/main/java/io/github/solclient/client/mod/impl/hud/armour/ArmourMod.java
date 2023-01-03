package io.github.solclient.client.mod.impl.hud.armour;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArmourMod extends HudMod {

	private static final ItemStack HELMET = new ItemStack(Items.iron_helmet);
	private static final ItemStack CHESTPLATE = new ItemStack(Items.iron_chestplate);
	private static final ItemStack LEGGINGS = new ItemStack(Items.iron_leggings);
	private static final ItemStack BOOTS = new ItemStack(Items.iron_boots);
	private static final ItemStack HAND = new ItemStack(Items.diamond_sword);

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
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	private boolean shadow = true;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY, applyToAllClass = Option.TEXT_COLOUR_CLASS)
	private Colour textColour = Colour.WHITE;

	@Override
	public String getId() {
		return "armour";
	}

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
		RenderHelper.enableGUIStandardItemLighting();

		ScaledResolution resolution = new ScaledResolution(mc);
		boolean rtl = !horizontal && position.getX() > resolution.getScaledWidth() / 2;

		int x = position.getX(), y = position.getY();

		if (horizontal) {
			y++;
		}

		ItemStack[] playerArmour;
		ItemStack handItem;

		if (mc.thePlayer == null || editMode) {
			playerArmour = new ItemStack[] { BOOTS, LEGGINGS, CHESTPLATE, HELMET };
			handItem = HAND;
		} else {
			playerArmour = mc.thePlayer.inventory.armorInventory;
			handItem = mc.thePlayer.inventory.getCurrentItem();
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

		RenderHelper.disableStandardItemLighting();
	}

	private int renderStack(ItemStack stack, int x, int y, boolean rtl) {
		boolean hasDurability = durability != DurabilityDisplay.OFF;
		int itemX = x;

		if (rtl && hasDurability) {
			itemX += durability.getWidth() - 1;
		}

		mc.getRenderItem().renderItemIntoGUI(stack, itemX, y);
		mc.getRenderItem().renderItemOverlays(font, stack, itemX, y);
		GlStateManager.disableLighting();

		if (hasDurability && stack.getMaxDamage() > 0) {
			String text;
			switch (durability) {
			case FRACTION:
				text = stack.getMaxDamage() - stack.getItemDamage() + " / " + (stack.getMaxDamage());
				break;
			case REMAINING:
				text = Integer.toString(stack.getMaxDamage() - stack.getItemDamage());
				break;
			case PERCENTAGE:
				text = ((int) (((double) stack.getMaxDamage() - stack.getItemDamage()) / (stack.getMaxDamage()) * 100))
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

			font.drawString(text, x, y + 4, textColour.getValue(), shadow);

			return font.getStringWidth(text);
		}

		return 0;
	}

}
