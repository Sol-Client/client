package me.mcblueparrot.client.util;//package me.mcblueparrot.client.util;
//
//import java.util.UUID;
//
//import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.gui.GuiButton;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.client.resources.DefaultPlayerSkin;
//import net.minecraft.entity.player.EnumPlayerModelParts;
//import net.minecraft.util.ResourceLocation;
//
//public class AccountButton extends GuiButton {
//
//    private ResourceLocation skinLocation;
//    private UUID lastUUID;
//
//    public AccountButton(int buttonId, int x, int y) {
//        super(buttonId, x, y, 100, 20, "");
//    }
//
////    @Override
////    protected int getHorizontalOffset() {
////        return 5;
////    }
//
//    @Override
//    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
//        displayString = mc.session.getUsername();
//        super.drawButton(mc, mouseX, mouseY);
//        GlStateManager.color(1, 1, 1);
//        if(!mc.session.getProfile().getId().equals(lastUUID)) {
//            skinLocation = DefaultPlayerSkin.getDefaultSkin(lastUUID = mc.session.getProfile().getId());
//            mc.getSkinManager().loadProfileTextures(mc.session.getProfile(), (type, location, texture) -> {
//                if(type == Type.SKIN) {
//                    skinLocation = location;
//                }
//            }, true);
//        }
//        if(visible) {
//            mc.getTextureManager().bindTexture(skinLocation);
//
//            // We're just going to assume Dinnerbone or Grumm won't be using this client any time soon.
//            drawScaledCustomSizeModalRect(xPosition + 5, yPosition + 5, 8, 8, 8, 8, 10, 10, 64.0F, 64.0F);
//
//            if(mc.gameSettings.getModelParts().contains(EnumPlayerModelParts.HAT)) {
//                Gui.drawScaledCustomSizeModalRect(xPosition + 5, yPosition + 5, 40.0F, 8, 8, 8, 10, 10, 64.0F, 64.0F);
//            }
//        }
//    }
//
//}
