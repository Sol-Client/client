package me.mcblueparrot.client.mod.impl.quickplay.database;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.SneakyThrows;
import me.mcblueparrot.client.mod.impl.quickplay.QuickPlayMod;
import me.mcblueparrot.client.mod.impl.quickplay.ui.QuickPlayOption;
import me.mcblueparrot.client.mod.impl.quickplay.ui.QuickPlayPalette;
import me.mcblueparrot.client.util.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

public class QuickPlayGame implements QuickPlayOption {

    @Getter
    private String id;
    @Getter
    private String name;
    private Map<String, QuickPlayGameMode> modes;
    private URL imageURL;
    private BufferedImage image;
    @Getter
    private ItemStack item;

    @SneakyThrows
    public QuickPlayGame(JsonObject object) {
        id = object.get("unlocalizedName").getAsString();
        name = object.get("name").getAsString();
        modes = new LinkedHashMap<>();
        imageURL = Utils.sneakyParse(object.get("imageURL").getAsString());

        for(JsonElement modeElement : object.get("modes").getAsJsonArray()) {
            QuickPlayGameMode mode = new QuickPlayGameMode(this, modeElement.getAsJsonObject());
            modes.put(mode.getCommand(), mode);
        }

        Item itemType = null;

        switch(id) {
            case "mainLobby":
                itemType = Items.oak_door;
                break;
            case "arcade":
                itemType =  Items.slime_ball;
                break;
            case "bedwars":
                itemType =  Items.bed;
                break;
            case "blitz":
                itemType =  Items.diamond_sword;
                break;
            case "buildBattle":
                itemType = Item.getItemFromBlock(Blocks.crafting_table);
                break;
            case "classic":
                itemType = Item.getItemFromBlock(Blocks.jukebox);
                break;
            case "cvc":
                itemType = Item.getItemFromBlock(Blocks.iron_bars);
                break;
            case "duels":
                itemType = Items.fishing_rod;
                break;
            case "housing":
                itemType = Items.dark_oak_door;
                break;
            case "mw":
                itemType = Item.getItemFromBlock(Blocks.soul_sand);
                break;
            case "murder":
                itemType = Items.bow;
                break;
            case "prototype":
                itemType = Item.getItemFromBlock(Blocks.anvil);
                break;
            case "skyblock":
                item = new ItemStack(Items.skull);
                item.setItemDamage(3);
                item.setTagCompound(JsonToNBT.getTagFromJson(
                        "{overrideMeta:1b,HideFlags:254,SkullOwner:{Id:\"e70f48d9-56b0-2023-b32e-f48bbdd063af\",hypixelPopulated:1b,Properties:{textures:[0:{Value:\"eyJ0aW1lc3RhbXAiOjE1NTkyMTU0MTY5MDksInByb2ZpbGVJZCI6IjQxZDNhYmMyZDc0OTQwMGM5MDkwZDU0MzRkMDM4MzFiIiwicHJvZmlsZU5hbWUiOiJNZWdha2xvb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q3Y2M2Njg3NDIzZDA1NzBkNTU2YWM1M2UwNjc2Y2I1NjNiYmRkOTcxN2NkODI2OWJkZWJlZDZmNmQ0ZTdiZjgifX19\"}]}}}"));
                break;
            case "skywars":
                itemType = Items.ender_eye;
                break;
            case "smashHeroes":
                item = new ItemStack(Items.skull);
                item.setItemDamage(3);
                item.setTagCompound(JsonToNBT.getTagFromJson(
                        "{ench:[],overrideMeta:1b,HideFlags:254,SkullOwner:{Id:\"a83ccfb7-3672-281f-90da-0f78dcf95378\",hypixelPopulated:1b,Properties:{textures:[0:{Value:\"eyJ0aW1lc3RhbXAiOjE0NTIwNTgzNTA4MDcsInByb2ZpbGVJZCI6ImFhZDIzYTUwZWVkODQ3MTA5OWNmNjRiZThmZjM0ZWY0IiwicHJvZmlsZU5hbWUiOiIxUm9ndWUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QyOWE5ZjU3MjY3ZWQzNDJhMTNlM2FkM2EyNDBjNGM1YWY1YTFhMzZhYjJkZTBkNmMyYTMxYWYwZTNjZGRlIn19fQ==\"}]}}}"));
                break;
            case "tnt":
                itemType = Item.getItemFromBlock(Blocks.tnt);
                break;
            case "uhc":
                itemType = Items.golden_apple;
                break;
            case "warlords":
                itemType = Items.stone_axe;
                break;
            case "thePit":
                itemType = Item.getItemFromBlock(Blocks.dirt);
        }

        if(itemType != null) {
            item = new ItemStack(itemType);
        }
    }

    public QuickPlayGameMode getMode(String command) {
        return modes.get(command);
    }

    public BufferedImage getImage() {
        if(image == null) {
            try(InputStream in = imageURL.openStream()) {
                image = ImageIO.read(in);
            }
            catch(IOException error) {
                throw new IllegalStateException(error);
            }
        }
        return image;
    }

    public List<QuickPlayGameMode> getModes() {
        return new ArrayList<>(modes.values());
    }

    public List<QuickPlayOption> getModeOptions() {
        return new ArrayList<>(modes.values());
    }

    @Override
    public String getText() {
        return name + " >";
    }

    @Override
    public void onClick(QuickPlayPalette palette, QuickPlayMod mod) {
        palette.selectGame(this);
    }

    @Override
    public ItemStack getIcon() {
        return item;
    }

}
