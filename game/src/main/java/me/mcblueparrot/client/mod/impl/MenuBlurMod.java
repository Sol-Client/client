/*
 * Original mod by tterrag1098.
 */

package me.mcblueparrot.client.mod.impl;

import java.io.IOException;
import java.io.InputStream;

import com.replaymod.extras.playeroverview.PlayerOverviewGui;
import com.replaymod.replay.ReplayModReplay;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.events.InitialOpenGuiEvent;
import me.mcblueparrot.client.events.PostProcessingEvent;
import me.mcblueparrot.client.events.RenderGuiBackgroundEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.access.AccessShaderGroup;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;

public class MenuBlurMod extends Mod {

    @Expose
    @ConfigOption("Blur")
    @Slider(min = 0, max = 100, step = 1)
    public float blur = 8;
    @Expose
    @ConfigOption("Fade Time")
    @Slider(min = 0, max = 1, step = 0.1F)
    private float fadeTime = 0.1F;
    @Expose
    @ConfigOption("Background Colour")
    public Colour backgroundColour = new Colour(0, 0, 0, 100);
    public ShaderGroup group;
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation("minecraft:shaders/post/menu_blur.json");
    private long openTime;

    public MenuBlurMod() {
        super("Menu Blur", "menu_blur", "Blurs the background of all menus.", ModCategory.VISUAL);
        Client.INSTANCE.addResource(RESOURCE_LOCATION, new MenuBlurShader());
    }

    @EventHandler
    public void onOpenGui(InitialOpenGuiEvent event) {
        openTime = System.currentTimeMillis();
    }

    @EventHandler
    public void onPostProcessing(PostProcessingEvent event) {
        if(event.type == PostProcessingEvent.Type.UPDATE || (blur != 0
                && (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)
                && !(mc.currentScreen.getClass().getName().startsWith("com.replaymod.lib.de.johni0702.minecraft.gui" +
                ".container." +
                "AbstractGuiOverlay$") && ReplayModReplay.instance.getReplayHandler() != null && mc.theWorld != null)))) {
            update();
            event.groups.add(group);
        }
    }

    @EventHandler
    public void onRenderGuiBackground(RenderGuiBackgroundEvent event) {
        event.cancelled = true;
        ScaledResolution resolution = new ScaledResolution(mc);
        Gui.drawRect(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), Utils.blendColor(0x00000000,
                backgroundColour.getValue(), getProgress()));
    }

    public void update() {
        if(group == null) {
            try {
                group = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(),
                       RESOURCE_LOCATION);
                group.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            }
            catch(JsonSyntaxException | IOException error) {
                logger.error("Could not load menu blur", error);
            }
        }

        ((AccessShaderGroup) group).getListShaders().forEach((shader) -> {
            ShaderUniform radius = shader.getShaderManager().getShaderUniform("Radius");
            ShaderUniform progress = shader.getShaderManager().getShaderUniform("Progress");

            if(radius != null) {
                radius.set(blur);
            }

            if(progress != null) {
                if(fadeTime > 0) {
                    progress.set(getProgress());
                }
                else {
                    progress.set(1);
                }
            }
        });
    }

    public float getProgress() {
        return Math.min((System.currentTimeMillis() - openTime) / (fadeTime * 1000F), 1);
    }

//    @Override
//    public void onEnabledChange(boolean enabled) {
//        super.onEnabledChange(enabled);
//        MenuBlurMod.enabled = enabled;
//    }

    public class MenuBlurShader implements IResource {

        @Override
        public ResourceLocation getResourceLocation() {
            return null;
        }

        @Override
        public InputStream getInputStream() {
//            JsonObject object = new JsonObject();
//
//            JsonArray targets = new JsonArray();
//            targets.add(new JsonPrimitive("swap"));
//            object.add("targets", targets);
//
//            JsonArray passes = new JsonArray();
//            for(int i = 0; i < 2; i++) {
//                JsonObject pass = new JsonObject();
//
//                pass.addProperty("name", "fast_blur");
//
//                if(i % 2 == 0) {
//                    pass.addProperty("intarget", "minecraft:main");
//                    pass.addProperty("outtarget", "swap");
//                }
//                else {
//                    pass.addProperty("intarget", "swap");
//                    pass.addProperty("outtarget", "minecraft:main");
//                }
//
//                JsonArray uniforms = new JsonArray();
//
////                JsonObject blurDir = new JsonObject();
////                blurDir.addProperty("name", "BlurDir");
////
////                JsonArray blurDirValues = new JsonArray();
////                if(i % 2 == 0) {
////                    blurDirValues.add(new JsonPrimitive(0.1F));
////                    blurDirValues.add(new JsonPrimitive(0F));
////                }
////                else {
////                    blurDirValues.add(new JsonPrimitive(0F));
////                    blurDirValues.add(new JsonPrimitive(0.1F));
////                }
////                blurDir.add("values", blurDirValues);
//
////                uniforms.add(blurDir);
//
////                JsonObject blurRadius = new JsonObject();
////                blurRadius.addProperty("name", "Radius");
////
////                JsonArray blurRadiusValues = new JsonArray();
////                blurRadiusValues.add(new JsonPrimitive(blur));
////                blurRadius.add("values", blurRadiusValues);
////
////                uniforms.add(blurRadius);
//
//                pass.add("uniforms", uniforms);
//
//                passes.add(pass);
//            }
//            object.add("passes", passes);
//            System.out.println(object);
//            return IOUtils.toInputStream(object.toString());
            return IOUtils.toInputStream("{\n" +
                    "    \"targets\": [\n" +
                    "        \"swap\"\n" +
                    "    ],\n" +
                    "    \"passes\": [\n" +
                    "        {\n" +
                    "            \"name\": \"menu_blur\",\n" +
                    "            \"intarget\": \"minecraft:main\",\n" +
                    "            \"outtarget\": \"swap\",\n" +
                    "            \"uniforms\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"BlurDir\",\n" +
                    "                    \"values\": [ 1.0, 0.0 ]\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"name\": \"Radius\",\n" +
                    "                    \"values\": [ 0.0 ]\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"name\": \"menu_blur\",\n" +
                    "            \"intarget\": \"swap\",\n" +
                    "            \"outtarget\": \"minecraft:main\",\n" +
                    "            \"uniforms\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"BlurDir\",\n" +
                    "                    \"values\": [ 0.0, 1.0 ]\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"name\": \"Radius\",\n" +
                    "                    \"values\": [ 0.0 ]\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"name\": \"menu_blur\",\n" +
                    "            \"intarget\": \"minecraft:main\",\n" +
                    "            \"outtarget\": \"swap\",\n" +
                    "            \"uniforms\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"BlurDir\",\n" +
                    "                    \"values\": [ 1.0, 0.0 ]\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"name\": \"Radius\",\n" +
                    "                    \"values\": [ 0.0 ]\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"name\": \"menu_blur\",\n" +
                    "            \"intarget\": \"swap\",\n" +
                    "            \"outtarget\": \"minecraft:main\",\n" +
                    "            \"uniforms\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"BlurDir\",\n" +
                    "                    \"values\": [ 0.0, 1.0 ]\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"name\": \"Radius\",\n" +
                    "                    \"values\": [ 0.0 ]\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");
        }

        @Override
        public boolean hasMetadata() {
            return false;
        }

        @Override
        public <T extends IMetadataSection> T getMetadata(String p_110526_1_) {
            return null;
        }

        @Override
        public String getResourcePackName() {
            return null;
        }

    }

}
