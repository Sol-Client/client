package me.mcblueparrot.client.events;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;

public class SoundPlayEvent {

    /**
     * The sound being played. To override volume and pitch, see {@link #volume} and {@link #pitch}
     */
    public ISound sound;

    /**
     * The sound category.
     */
    public SoundCategory category;

    /**
     * If the value is not <code>-1</code>, the volume will be overridden.
     */
    public float volume;

    /**
     * If the value is not <code>-1</code>, the pitch will be overridden.
     */
    public float pitch;

    /**
     * Sets volume to <code>0</code>.
     */
    public void cancel() {
        volume = 0;
    }

    public SoundPlayEvent(ISound sound, SoundCategory category, float volume, float pitch) {
        this.sound = sound;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
    }

}
