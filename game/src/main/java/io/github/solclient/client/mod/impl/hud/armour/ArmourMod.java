package io.github.solclient.client.mod.impl.hud.armour;

import com.google.gson.annotations.Expose;

import io.github.solclient.abstraction.mc.world.entity.player.LocalPlayer;
import io.github.solclient.abstraction.mc.world.item.ItemStack;
import io.github.solclient.abstraction.mc.world.item.ItemType;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.HudMod;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;

public class ArmourMod extends HudMod {

	private static final ItemStack HELMET = ItemStack.create(ItemType.IRON_HELMET);
	private static final ItemStack CHESTPLATE = ItemStack.create(ItemType.IRON_CHESTPLATE);
	private static final ItemStack LEGGINGS = ItemStack.create(ItemType.IRON_LEGGINGS);
	private static final ItemStack BOOTS = ItemStack.create(ItemType.IRON_BOOTS);
	private static final ItemStack HAND = ItemStack.create(ItemType.DIAMOND_SWORD);

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
		int height = 1;
		if(armour) height += 15 * 4;
		if(hand) height += 15;
		return new Rectangle(position.getX() - 1, position.getY(), durability.getWidth() + 1, height);
	}

	@Override
	public void render(Position position, boolean editMode) {
		if(mc.hasPlayer() && !editMode) {
			LocalPlayer player = mc.getPlayer();
			if(armour) {
				for(int i = 0; i < 4; i++) {
					ItemStack stack = player.getInventory().getArmour(3 - i);
					if(stack != null) {
						renderStack(stack, position.getX(), position.getY() + (i * 15));
					}
				}
			}
			if(hand && player.getInventory().getMainHand() != null) renderStack(player.getInventory().getMainHand(),
					position.getX(), position.getY() + (armour ? 60 : 0));
		}
		else if(editMode) {
			if(armour) {
				renderStack(HELMET, position.getX(), position.getY());
				renderStack(CHESTPLATE, position.getX(), position.getY() + 15);
				renderStack(LEGGINGS, position.getX(), position.getY() + 30);
				renderStack(BOOTS, position.getX(), position.getY() + 45);
			}

			if(hand) renderStack(HAND, position.getX(), position.getY() + (armour ? 60 : 0));
		}
	}

	private void renderStack(ItemStack stack, int x, int y) {
		mc.getItemRenderer().render(stack, x, y);
		if(stack.getMaxDamage() > 0) {
			String text;
			switch(durability) {
				case FRACTION:
					text = stack.getMaxDamage() - stack.getDamage() + " / " + (stack.getMaxDamage());
					break;
				case REMAINING:
					text = Integer.toString(stack.getMaxDamage() - stack.getDamage());
					break;
				case PERCENTAGE:
					text = ((int) (((double) stack.getMaxDamage() - stack.getDamage()) / (stack.getMaxDamage()) * 100)) + "%";
					break;
				default:
					text = "Invalid mode";
			}
			font.render(text, x + 20, y + 5, textColour.getValue(), shadow);
		}
	}

}
