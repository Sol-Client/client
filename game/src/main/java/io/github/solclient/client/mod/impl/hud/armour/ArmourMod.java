package io.github.solclient.client.mod.impl.hud.armour;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.HudMod;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.platform.mc.Window;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import io.github.solclient.client.platform.mc.world.item.ItemType;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;

public class ArmourMod extends HudMod {

	public static final ArmourMod INSTANCE = new ArmourMod();

	private static final ItemStack HELMET = ItemStack.create(ItemType.IRON_HELMET);
	private static final ItemStack CHESTPLATE = ItemStack.create(ItemType.IRON_CHESTPLATE);
	private static final ItemStack LEGGINGS = ItemStack.create(ItemType.IRON_LEGGINGS);
	private static final ItemStack BOOTS = ItemStack.create(ItemType.IRON_BOOTS);
	private static final ItemStack HAND = ItemStack.create(ItemType.DIAMOND_SWORD);
	private static final ItemStack[] ARMOUR = {HELMET, CHESTPLATE, LEGGINGS, BOOTS};

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
		if(horizontal) {
			int width = 1;

			if(armour) {
				switch(durability) {
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

			if(hand) {
				switch(durability) {
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

		if(armour) {
			height += 15 * 4;
		}

		if(hand) {
			height += 15;
		}

		return new Rectangle(position.getX() - 1, position.getY(), durability.getWidth() + 18, height);
	}

	@Override
	public void render(Position position, boolean editMode) {
		Window window = mc.getWindow();
		boolean rtl = !horizontal && position.getX() > window.scaledWidth() / 2;

		int x = position.getX(), y = position.getY();

		if(horizontal) {
			y++;
		}

		if(armour) {
			for(int i = 0; i < 4; i++) {
				ItemStack stack;
				if(editMode) {
					stack = ARMOUR[i];
				}
				else {
					stack = mc.getPlayer().getInventory().getArmour(3 - i);
				}
				if(stack != null) {
					int width = renderStack(stack, x, y, rtl);
					if(horizontal) {
						if(width != 0) {
							x += 24 + width;
						}
						else {
							x += 18;
						}
					}
				}
				if(!horizontal) {
					y += 15;
				}
			}
		}

		ItemStack handItem;

		if(editMode) {
			handItem = HAND;
		}
		else {
			handItem = mc.getPlayer().getInventory().getMainHand();
		}

		if(hand && handItem != null) {
			renderStack(handItem, x, y, rtl);
		}
	}

	private int renderStack(ItemStack stack, int x, int y, boolean rtl) {
		boolean hasDurability = durability != DurabilityDisplay.OFF;
		int itemX = x;

		if(rtl && hasDurability) {
			itemX += durability.getWidth() - 1;
		}

		mc.getItemRenderer().render(stack, itemX, y);

		if(hasDurability && stack.getMaxDamageValue() > 0) {
			String text;
			switch(durability) {
				case FRACTION:
					text = stack.getMaxDamageValue() - stack.getDamageValue() + " / " + (stack.getMaxDamageValue());
					break;
				case REMAINING:
					text = Integer.toString(stack.getMaxDamageValue() - stack.getDamageValue());
					break;
				case PERCENTAGE:
					text = ((int) (((double) stack.getMaxDamageValue() - stack.getDamageValue()) / (stack.getMaxDamageValue()) * 100)) + "%";
					break;
				default:
					text = "??";
					break;
			}

			if(rtl) {
				x += durability.getWidth() - 4 - font.getTextWidth(text);
			}
			else {
				x += 20;
			}

			font.render(text, x, y + 4, textColour.getValue(), shadow);

			return font.getTextWidth(text);
		}

		return 0;
	}

}
