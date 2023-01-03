package io.github.solclient.client.mod.keybinding;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostTickEvent;
import io.github.solclient.client.mod.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;

public abstract class ToggledKeyBinding<ModType extends Mod> extends KeyBinding {
    public final ModType mod;
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean wasDown;
    private long startTime;

    public ToggledKeyBinding(ModType mod, String description, int keyCode, String category) {
        super(description, keyCode, category);
        this.mod = mod;
    }

    @EventHandler
    public void tickBinding(PostTickEvent event) {
        boolean down = super.isKeyDown();
        if (mod.isEnabled()) {
            if (down) {
                if (!wasDown) {
                    startTime = System.currentTimeMillis();
                    if (getState() == ToggleState.TOGGLED) {
                        postStateUpdate(ToggleState.HELD);
                    } else {
                        postStateUpdate(ToggleState.TOGGLED);
                    }
                } else if ((System.currentTimeMillis() - startTime) > 250) {
                    postStateUpdate(ToggleState.HELD);
                }
            } else if (getState() == ToggleState.HELD) {
                postStateUpdate(null);
            }

            wasDown = down;
        }
    }

    @Override
    public boolean isKeyDown() {
        if (mod.isEnabled()) {
            return mc.currentScreen == null && getState() != null;
        }
        return super.isKeyDown();
    }

    public String getText(boolean editMode) {
        String translationId;
        if (editMode) {
            translationId = String.format("sol_client.mod.%s.%s", mod.getId(), ToggleState.TOGGLED.name().toLowerCase());
        } else {
            translationId = String.format("sol_client.mod.%s.%s", mod.getId(), getState().name().toLowerCase());
        }
        return I18n.format(translationId);
    }

    public abstract void postStateUpdate(ToggleState newState);

    public abstract ToggleState getState();
}
