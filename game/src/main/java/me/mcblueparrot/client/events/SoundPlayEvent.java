package me.mcblueparrot.client.events;

import lombok.AllArgsConstructor;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.util.ResourceLocation;

@AllArgsConstructor
public class SoundPlayEvent {

    public String soundName;

    public float volume;

    public float pitch;

    public float originalVolume;

    public float originalPitch;

    /**
     * Sets volume to <code>0</code>.
     */
    public void cancel() {
        volume = 0;
    }

}
