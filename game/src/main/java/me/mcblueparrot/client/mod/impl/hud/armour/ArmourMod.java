package me.mcblueparrot.client.mod.impl.hud.armour;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.hud.HudMod;
import me.mcblueparrot.client.mod.hud.SimpleHudMod;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderHelper;
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
		RenderHelper.enableGUIStandardItemLighting();

		if(mc.thePlayer != null && !editMode) {
			EntityPlayerSP player = mc.thePlayer;
			if(armour) {
				for(int i = 0; i < 4; i++) {
					ItemStack stack = player.inventory.armorInventory[3 - i];
					if(stack != null) {
						renderStack(stack, position.getX(), position.getY() + (i * 15));
					}
				}
			}
			if(hand && player.inventory.getCurrentItem() != null) renderStack(player.inventory.getCurrentItem(),
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

		RenderHelper.disableStandardItemLighting();
	}

	private void renderStack(ItemStack stack, int x, int y) {
		mc.getRenderItem().renderItemIntoGUI(stack, x, y);
		if(stack.getMaxDamage() > 0) {
			String text;
			switch(durability) {
				case FRACTION:
					text = stack.getMaxDamage() - stack.getItemDamage() + " / " + (stack.getMaxDamage());
					break;
				case REMAINING:
					text = Integer.toString(stack.getMaxDamage() - stack.getItemDamage());
					break;
				case PERCENTAGE:
					text = ((int) (((double) stack.getMaxDamage() - stack.getItemDamage()) / (stack.getMaxDamage()) * 100)) + "%";
					break;
				default:
					text = "Invalid mode";
			}
			font.drawString(text, x + 20, y + 5, textColour.getValue(), shadow);
		}
	}

}
