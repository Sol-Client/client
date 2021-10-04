package me.mcblueparrot.client.hud;

import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Position;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.util.Rectangle;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ArmourHud extends Hud {

    @Expose
    @ConfigOption("Mode")
    private Mode mode = Mode.REMAINING;
    @Expose
    @ConfigOption("Armour")
    private boolean armour = true;
    @Expose
    @ConfigOption("Hand")
    private boolean hand = true;
    @Expose
    @ConfigOption("Shadow")
    private boolean shadow = true;
    @Expose
    @ConfigOption("Text Colour")
    private Colour textColour = Colour.WHITE;
    private static final ItemStack HELMET = new ItemStack(Items.iron_helmet);
    private static final ItemStack CHESTPLATE = new ItemStack(Items.iron_chestplate);
    private static final ItemStack LEGGINGS = new ItemStack(Items.iron_leggings);
    private static final ItemStack BOOTS = new ItemStack(Items.iron_boots);
    private static final ItemStack HAND = new ItemStack(Items.iron_sword);

    public ArmourHud() {
        super("Equipment", "armour", "Display your armour and equipment.");
    }

    @Override
    public Rectangle getBounds(Position position) {
        int height = 0;
        if(armour) height += 15 * 4;
        if(hand) height += 15;
        return new Rectangle(position.getX(), position.getY(), mode.getWidth(), height);
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
            switch(mode) {
                case FRACTION:
                    text = stack.getMaxDamage() - stack.getItemDamage() + " / " + (stack.getMaxDamage());
                    break;
                case REMAINING:
                    text = Integer.toString(stack.getMaxDamage() - stack.getItemDamage());
                    break;
                case PERCENTAGE:
                    text = ((int) (((double) stack.getMaxDamage() - stack.getItemDamage()) / ((double) stack.getMaxDamage()) * 100)) + "%";
                    break;
                default:
                    text = "Invalid mode";
            }
            font.drawString(text, x + 20, y + 5, textColour.getValue(), shadow);
        }
    }

    public enum Mode {
        FRACTION("Fraction"),
        REMAINING("Remaining"),
        PERCENTAGE("Percentage");

        private String name;

        private Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public int getWidth() {
            switch(this) {
                case FRACTION:
                    return 73;
                case REMAINING:
                    return 40;
                case PERCENTAGE:
                    return 45;
                default:
                    return 0;
            }
        }

    }

}
