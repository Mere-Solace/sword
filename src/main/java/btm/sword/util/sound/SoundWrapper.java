package btm.sword.util.sound;

import java.util.function.Supplier;

import org.bukkit.entity.LivingEntity;


/**
 * Wrapper class for handling sound effects with configuration system integration.
 * <p>
 * Similar to {@link btm.sword.util.display.ParticleWrapper}, this class provides
 * a prefab object pattern for playing sounds. Sound properties (type, volume, pitch)
 * are dynamically loaded from the configuration system at play time, enabling
 * hot-reload functionality.
 * </p>
 * <p>
 * Usage: {@code Prefab.Sounds.ATTACK.play(entity);}
 * </p>
 */
public class SoundWrapper {
    /** Supplier to get the sound type from Config */
    private final Supplier<SoundType> soundSupplier;
    /** Supplier to get the volume from Config */
    private final Supplier<Float> volumeSupplier;
    /** Supplier to get the pitch from Config */
    private final Supplier<Float> pitchSupplier;

    /**
     * Constructs a SoundWrapper with configuration suppliers.
     * <p>
     * The suppliers are called each time {@link #play(LivingEntity)} is invoked,
     * ensuring hot-reload compatibility by fetching fresh config values.
     * </p>
     *
     * @param soundSupplier supplier that provides the sound type from Config
     * @param volumeSupplier supplier that provides the volume from Config
     * @param pitchSupplier supplier that provides the pitch from Config
     */
    public SoundWrapper(Supplier<SoundType> soundSupplier, Supplier<Float> volumeSupplier, Supplier<Float> pitchSupplier) {
        this.soundSupplier = soundSupplier;
        this.volumeSupplier = volumeSupplier;
        this.pitchSupplier = pitchSupplier;
    }

    /**
     * Plays the sound effect at the specified entity's location.
     * <p>
     * Fetches sound properties (type, volume, pitch) from the configuration system
     * at call time, then delegates to {@link SoundUtil#playSound(LivingEntity, SoundType, float, float)}.
     * </p>
     *
     * @param entity the entity to play the sound at
     */
    public void play(LivingEntity entity) {
        SoundUtil.playSound(entity, soundSupplier.get(), volumeSupplier.get(), pitchSupplier.get());
    }
}
