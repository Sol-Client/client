package io.github.solclient.client.mod.impl.hud;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PlayerHeadRotateEvent;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.mod.impl.SolClientHudMod;
import io.github.solclient.client.mod.option.ModOption;
import io.github.solclient.client.mod.option.ModOptionStorage;
import io.github.solclient.client.mod.option.annotation.AbstractTranslationKey;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.mod.option.impl.SliderOption;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;

import java.util.List;
import java.util.Optional;

// Originally from KronHUD
// Added here by DarkKronicle :)
// https://github.com/DarkKronicle/KronHUD/blob/master/src/main/java/io/github/darkkronicle/kronhud/gui/hud/PlayerHud.java
@AbstractTranslationKey("sol_client.mod.paperdoll")
public class PaperDollMod extends SolClientHudMod {

    @Expose
    @Option
    private boolean dynamicRotation = true;

    @Expose
    private float rotation = 0;

    private float lastYawOffset = 0;
    private float yawOffset = 0;

    @Override
    public Rectangle getBounds(Position position) {
        return new Rectangle(position.getX(), position.getY(), 62, 94);
    }

    @Override
    public String getDetail() {
        return I18n.translate("sol_client.mod.screen.by", "DarkKronicle"); // maybe also add original creator
    }

    @Override
    protected List<ModOption<?>> createOptions() {
        List<ModOption<?>> options = super.createOptions();
        Optional<String> format = Optional.empty();
        options.add(
                new SliderOption(
                        "sol_client.mod.paperdoll.option.rotation",
                        ModOptionStorage.of(Number.class, () -> rotation, (value) -> rotation = value.floatValue()),
                        format, 0, 360, 1
                )
        );
        return options;
    }

    public void renderPlayer(double x, double y, float delta) {
        if (mc.player == null) {
            return;
        }

        float deltaYaw = mc.player.prevYaw + (mc.player.yaw - mc.player.prevYaw) * delta;
        if (dynamicRotation) {
            deltaYaw -= (lastYawOffset + ((yawOffset - lastYawOffset) * delta));
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, 500.0F);
        GlStateManager.scale((float) (-40), (float) 40, (float) 40);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        DiffuseLighting.enableNormally();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(deltaYaw + rotation, 0.0F, 1.0F, 0.0F);

        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
        entityRenderDispatcher.setYaw(0);
        entityRenderDispatcher.setRenderShadows(false);
        entityRenderDispatcher.render(mc.player, 0.0, 0.0, 0.0, 0.0F, MinecraftUtils.getTickDelta());
        entityRenderDispatcher.setRenderShadows(true);

        GlStateManager.popMatrix();
        DiffuseLighting.disable();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.lightmapTextureUnit);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.textureUnit);

    }

    @EventHandler
    public void onPlayerRotate(PlayerHeadRotateEvent event) {
        if (event.yaw == 0 && event.pitch == 0) {
            return;
        }
        yawOffset += (event.yaw * .15) / 2;
    }

    @Override
    public void render(Position position, boolean editMode) {
        renderPlayer(position.getX() + 31, position.getY() + 86, MinecraftUtils.getTickDelta());
    }

    @EventHandler
    public void onTick(PreTickEvent event) {
        lastYawOffset = yawOffset;
        yawOffset *= .93f;
    }

}
