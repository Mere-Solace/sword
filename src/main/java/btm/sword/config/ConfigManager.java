package btm.sword.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;

import btm.sword.Sword;

/**
 * Centralized configuration manager for Sword: Combat Evolved.
 * <p>
 * Loads configuration values from config.yaml into the static {@link Config} class.
 * Provides hot-reload capabilities via {@code /sword reload} without server restart.
 * </p>
 * <p>
 * Thread-safe singleton pattern ensures consistent access across the plugin.
 * Uses generic loading methods to minimize boilerplate and improve maintainability.
 * </p>
 *
 * @see <a href="https://github.com/Mere-Solace/Sword-Combat-Plugin/issues/116">Issue #116</a>
 */
public final class ConfigManager {
    private static ConfigManager instance;

    private final Sword plugin;
    private File configFile;
    private FileConfiguration config;

    /**
     * Private constructor for singleton pattern.
     *
     * @param plugin The main plugin instance
     */
    private ConfigManager(Sword plugin) {
        this.plugin = plugin;
    }

    /**
     * Initializes the configuration manager.
     * <p>
     * Should be called during plugin initialization (onEnable).
     * Creates default config file if it doesn't exist and loads all values.
     * </p>
     *
     * @param plugin The main plugin instance
     */
    public static void initialize(Sword plugin) {
        if (instance == null) {
            instance = new ConfigManager(plugin);
            instance.setupConfig();
            instance.loadConfig();
            plugin.getLogger().info("ConfigManager initialized successfully");
        }
    }

    /**
     * Gets the singleton instance of ConfigManager.
     *
     * @return The ConfigManager instance
     * @throws IllegalStateException if ConfigManager hasn't been initialized
     */
    public static ConfigManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ConfigManager not initialized! Call initialize() first.");
        }
        return instance;
    }

    /**
     * Sets up the config file, creating it from default resource if needed.
     */
    @SuppressWarnings("all")
    private void setupConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yaml");

        if (!configFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try (InputStream defaultConfig = plugin.getResource("config.yaml")) {
                if (defaultConfig != null) {
                    Files.copy(defaultConfig, configFile.toPath());
                    plugin.getLogger().info("Created default config.yaml");
                } else {
                    plugin.getLogger().warning("Default config.yaml not found in resources!");
                    configFile.createNewFile();
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create config.yaml", e);
            }
        }
    }

    /**
     * Loads (or reloads) the configuration from disk into static Config class.
     * <p>
     * This method can be called at runtime to hot-reload configuration changes.
     * All static fields in Config are updated with new values from disk.
     * </p>
     *
     * @return true if reload was successful, false if errors occurred
     */
    public boolean loadConfig() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);

            // Load all configuration sections into static Config class
            loadPhysicsConfig();
            loadCombatConfig();
            loadTimingConfig();
            loadDisplayConfig();
            loadDetectionConfig();
            loadAudioConfig();
            loadEntityConfig();
            loadMovementConfig();
            loadWorldConfig();
            loadDebugConfig();

            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load config.yaml! Using previous values.", e);
            return false;
        }
    }

    // ==============================================================================
    // GENERIC LOADING METHODS
    // ==============================================================================

    /**
     * Generic method to load a value from config with type safety and default fallback.
     *
     * @param <T> The type of value to load
     * @param path The configuration path (e.g., "physics.thrown_items.gravity_damper")
     * @param defaultValue The default value if path doesn't exist
     * @return The loaded value or default
     */
    @SuppressWarnings("unchecked")
    private <T> T get(String path, T defaultValue) {
        if (defaultValue instanceof Double) {
            return (T) Double.valueOf(config.getDouble(path, (Double) defaultValue));
        } else if (defaultValue instanceof Integer) {
            return (T) Integer.valueOf(config.getInt(path, (Integer) defaultValue));
        } else if (defaultValue instanceof Float) {
            return (T) Float.valueOf((float) config.getDouble(path, (Float) defaultValue));
        } else if (defaultValue instanceof Boolean) {
            return (T) Boolean.valueOf(config.getBoolean(path, (Boolean) defaultValue));
        } else if (defaultValue instanceof String) {
            return (T) config.getString(path, (String) defaultValue);
        } else if (defaultValue instanceof List) {
            return (T) config.getStringList(path);
        }
        return defaultValue;
    }

    // ==============================================================================
    // SECTION LOADERS
    // ==============================================================================

    private void loadPhysicsConfig() {
        ConfigurationSection physics = config.getConfigurationSection("physics");
        if (physics == null) return;

        // Thrown Items
        ConfigurationSection thrownItems = physics.getConfigurationSection("thrown_items");
        if (thrownItems != null) {
            Config.Physics.setTHROWN_ITEMS_GRAVITY_DAMPER(get("physics.thrown_items.gravity_damper", 46.0));
            Config.Physics.setTHROWN_ITEMS_TRAJECTORY_ROTATION(get("physics.thrown_items.trajectory_rotation", 0.03696));

            Config.Physics.setTHROWN_ITEMS_DISPLAY_OFFSET_X(get("physics.thrown_items.display_offset.x", -0.5f));
            Config.Physics.setTHROWN_ITEMS_DISPLAY_OFFSET_Y(get("physics.thrown_items.display_offset.y", 0.1f));
            Config.Physics.setTHROWN_ITEMS_DISPLAY_OFFSET_Z(get("physics.thrown_items.display_offset.z", 0.5f));

            Config.Physics.setTHROWN_ITEMS_ORIGIN_OFFSET_FORWARD(get("physics.thrown_items.origin_offset.forward", 0.5));
            Config.Physics.setTHROWN_ITEMS_ORIGIN_OFFSET_UP(get("physics.thrown_items.origin_offset.up", 0.1));
            Config.Physics.setTHROWN_ITEMS_ORIGIN_OFFSET_BACK(get("physics.thrown_items.origin_offset.back", -0.25));

            Config.Physics.setTHROWN_ITEMS_ROTATION_SPEED_SWORD(get("physics.thrown_items.rotation_speed.sword", 0.0));
            Config.Physics.setTHROWN_ITEMS_ROTATION_SPEED_AXE(get("physics.thrown_items.rotation_speed.axe", -Math.PI / 8));
            Config.Physics.setTHROWN_ITEMS_ROTATION_SPEED_HOE(get("physics.thrown_items.rotation_speed.hoe", -Math.PI / 8));
            Config.Physics.setTHROWN_ITEMS_ROTATION_SPEED_PICKAXE(get("physics.thrown_items.rotation_speed.pickaxe", -Math.PI / 8));
            Config.Physics.setTHROWN_ITEMS_ROTATION_SPEED_SHOVEL(get("physics.thrown_items.rotation_speed.shovel", -Math.PI / 8));
            Config.Physics.setTHROWN_ITEMS_ROTATION_SPEED_SHIELD(get("physics.thrown_items.rotation_speed.shield", -Math.PI / 8));
            Config.Physics.setTHROWN_ITEMS_ROTATION_SPEED_DEFAULT_SPEED(get("physics.thrown_items.rotation_speed.default", Math.PI / 32));
        }

        // Attack Velocity
        ConfigurationSection attackVel = physics.getConfigurationSection("attack_velocity");
        if (attackVel != null) {
            Config.Physics.setATTACK_VELOCITY_GROUNDED_DAMPING_HORIZONTAL(get("physics.attack_velocity.grounded_damping.horizontal", 0.3));
            Config.Physics.setATTACK_VELOCITY_GROUNDED_DAMPING_VERTICAL(get("physics.attack_velocity.grounded_damping.vertical", 0.4));

            Config.Physics.setATTACK_VELOCITY_KNOCKBACK_VERTICAL_BASE(get("physics.attack_velocity.knockback.vertical_base", 0.25));
            Config.Physics.setATTACK_VELOCITY_KNOCKBACK_HORIZONTAL_MODIFIER(get("physics.attack_velocity.knockback.horizontal_modifier", 0.1));
            Config.Physics.setATTACK_VELOCITY_KNOCKBACK_NORMAL_MULTIPLIER(get("physics.attack_velocity.knockback.normal_multiplier", 0.7));
        }
    }

    private void loadCombatConfig() {
        ConfigurationSection combat = config.getConfigurationSection("combat");
        if (combat == null) return;

        // Attacks
        Config.Combat.setATTACKS_BASE_DAMAGE(get("combat.attacks.base_damage", 20.0));
        Config.Combat.setATTACKS_DOWN_AIR_THRESHOLD(get("combat.attacks.down_air_threshold", -0.85));

        // Cast timing
        Config.Combat.setATTACKS_CAST_TIMING_MIN_DURATION(get("combat.attacks.cast_timing.min_duration", 1L));
        Config.Combat.setATTACKS_CAST_TIMING_MAX_DURATION(get("combat.attacks.cast_timing.max_duration", 3L));
        Config.Combat.setATTACKS_CAST_TIMING_REDUCTION_RATE(get("combat.attacks.cast_timing.reduction_rate", 0.2));
        Config.Combat.setATTACKS_DURATION_MULTIPLIER(get("combat.attacks.duration_multiplier", 500));

        Config.Combat.setATTACKS_RANGE_MULTIPLIERS_BASIC_1(get("combat.attacks.range_multipliers.basic_1", 1.4));
        Config.Combat.setATTACKS_RANGE_MULTIPLIERS_BASIC_2(get("combat.attacks.range_multipliers.basic_2", 1.4));
        Config.Combat.setATTACKS_RANGE_MULTIPLIERS_BASIC_3(get("combat.attacks.range_multipliers.basic_3", 1.4));
        Config.Combat.setATTACKS_RANGE_MULTIPLIERS_NEUTRAL_AIR(get("combat.attacks.range_multipliers.neutral_air", 1.3));
        Config.Combat.setATTACKS_RANGE_MULTIPLIERS_DOWN_AIR(get("combat.attacks.range_multipliers.down_air", 1.2));

        // Hitboxes
        Config.Combat.setHITBOXES_BASIC_REACH(get("combat.hitboxes.basic.reach", 1.5));
        Config.Combat.setHITBOXES_BASIC_WIDTH(get("combat.hitboxes.basic.width", 1.5));
        Config.Combat.setHITBOXES_BASIC_HEIGHT(get("combat.hitboxes.basic.height", 1.5));

        Config.Combat.setHITBOXES_DOWN_AIR_REACH(get("combat.hitboxes.down_air.reach", 1.6));
        Config.Combat.setHITBOXES_DOWN_AIR_WIDTH(get("combat.hitboxes.down_air.width", 1.4));
        Config.Combat.setHITBOXES_DOWN_AIR_HEIGHT(get("combat.hitboxes.down_air.height", 2.5));

        Config.Combat.setHITBOXES_SECANT_RADIUS(get("combat.hitboxes.secant_radius", 0.4));

        // Thrown Damage - Sword/Axe
        Config.Combat.setTHROWN_DAMAGE_SWORD_AXE_INVULNERABILITY_TICKS(get("combat.thrown_damage.sword_axe.invulnerability_ticks", 0));
        Config.Combat.setTHROWN_DAMAGE_SWORD_AXE_BASE_SHARDS(get("combat.thrown_damage.sword_axe.base_shards", 2));
        Config.Combat.setTHROWN_DAMAGE_SWORD_AXE_TOUGHNESS_DAMAGE(get("combat.thrown_damage.sword_axe.toughness_damage", 75.0f));
        Config.Combat.setTHROWN_DAMAGE_SWORD_AXE_SOULFIRE_REDUCTION(get("combat.thrown_damage.sword_axe.soulfire_reduction", 50.0f));
        Config.Combat.setTHROWN_DAMAGE_SWORD_AXE_KNOCKBACK_GROUNDED(get("combat.thrown_damage.sword_axe.knockback_grounded", 0.7));
        Config.Combat.setTHROWN_DAMAGE_SWORD_AXE_KNOCKBACK_AIRBORNE(get("combat.thrown_damage.sword_axe.knockback_airborne", 1.0));

        // Thrown Damage - Other
        Config.Combat.setTHROWN_DAMAGE_OTHER_INVULNERABILITY_TICKS(get("combat.thrown_damage.other.invulnerability_ticks", 0));
        Config.Combat.setTHROWN_DAMAGE_OTHER_BASE_SHARDS(get("combat.thrown_damage.other.base_shards", 2));
        Config.Combat.setTHROWN_DAMAGE_OTHER_TOUGHNESS_DAMAGE(get("combat.thrown_damage.other.toughness_damage", 75.0f));
        Config.Combat.setTHROWN_DAMAGE_OTHER_SOULFIRE_REDUCTION(get("combat.thrown_damage.other.soulfire_reduction", 50.0f));
        Config.Combat.setTHROWN_DAMAGE_OTHER_KNOCKBACK_MULTIPLIER(get("combat.thrown_damage.other.knockback_multiplier", 0.7));
        Config.Combat.setTHROWN_DAMAGE_OTHER_EXPLOSION_POWER(get("combat.thrown_damage.other.explosion_power", 1.0f));

        Config.Combat.setTHROWN_DAMAGE_SWORD_DAMAGE_MULTIPLIER(get("combat.thrown_damage.sword_damage_multiplier", 1.0));
        Config.Combat.setTHROWN_DAMAGE_ITEM_VELOCITY_MULTIPLIER(get("combat.thrown_damage.item_velocity_multiplier", 1.5));
        Config.Combat.setTHROWN_DAMAGE_BASE_THROWN_DAMAGE(get("combat.thrown_damage.base_thrown_damage", 12.0));

        // Impalement
        Config.Combat.setIMPALEMENT_DAMAGE_PER_TICK(get("combat.impalement.damage_per_tick", 2.0));
        Config.Combat.setIMPALEMENT_TICKS_BETWEEN_DAMAGE(get("combat.impalement.ticks_between_damage", 10));
        Config.Combat.setIMPALEMENT_MAX_IMPALEMENTS(get("combat.impalement.max_impalements", 3));
        Config.Combat.setIMPALEMENT_HEAD_ZONE_RATIO(get("combat.impalement.head_zone_ratio", 0.8));
        Config.Combat.setIMPALEMENT_PIN_MAX_ITERATIONS(get("combat.impalement.pin_max_iterations", 50));
        Config.Combat.setIMPALEMENT_PIN_CHECK_INTERVAL(get("combat.impalement.pin_check_interval", 2));

        List<String> headFollowExceptions = config.getStringList("combat.impalement.head_follow_exceptions");
        if (!headFollowExceptions.isEmpty()) {
            List<EntityType> exceptions = headFollowExceptions.stream()
                .map(s -> {
                    try {
                        return EntityType.valueOf(s);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
            if (!exceptions.isEmpty()) {
                Config.Combat.setIMPALEMENT_HEAD_FOLLOW_EXCEPTIONS(exceptions);
            }
        }

        // Attack Class
        List<String> exempt = config.getStringList("combat.attack_class.exempt_from_combat");
        if (!exempt.isEmpty()) {
            Config.Combat.setATTACK_CLASS_EXEMPT_FROM_COMBAT(exempt);
        }

        // Attack Class Timing
        Config.Combat.setATTACK_CLASS_TIMING_ATTACK_DURATION(get("combat.attack_class.timing.attack_duration", 750));
        Config.Combat.setATTACK_CLASS_TIMING_ATTACK_ITERATIONS(get("combat.attack_class.timing.attack_iterations", 5));
        Config.Combat.setATTACK_CLASS_TIMING_ATTACK_START_VALUE(get("combat.attack_class.timing.attack_start_value", 0.0));
        Config.Combat.setATTACK_CLASS_TIMING_ATTACK_END_VALUE(get("combat.attack_class.timing.attack_end_value", 1.0));

        // Attack Class Modifiers
        Config.Combat.setATTACK_CLASS_MODIFIERS_RANGE_MULTIPLIER(get("combat.attack_class.modifiers.range_multiplier", 2.0));
    }

    private void loadTimingConfig() {
        ConfigurationSection timing = config.getConfigurationSection("timing");
        if (timing == null) return;

        // Thrown Items
        Config.Timing.setTHROWN_ITEMS_CATCH_GRACE_PERIOD(get("timing.thrown_items.catch_grace_period", 3));
        Config.Timing.setTHROWN_ITEMS_DISPOSAL_TIMEOUT(get("timing.thrown_items.disposal_timeout", 200));
        Config.Timing.setTHROWN_ITEMS_DISPOSAL_CHECK_INTERVAL(get("timing.thrown_items.disposal_check_interval", 10));
        Config.Timing.setTHROWN_ITEMS_PIN_DELAY(get("timing.thrown_items.pin_delay", 2));
        Config.Timing.setTHROWN_ITEMS_THROW_COMPLETION_DELAY(get("timing.thrown_items.throw_completion_delay", 6));

        // Intervals
        Config.Timing.setINTERVALS_ENTITY_TICK(get("timing.intervals.entity_tick", 1));
        Config.Timing.setINTERVALS_STATUS_DISPLAY_UPDATE(get("timing.intervals.status_display_update", 5));
        Config.Timing.setINTERVALS_COMBAT_CLEANUP(get("timing.intervals.combat_cleanup", 20));

        // Attacks
        Config.Timing.setATTACKS_COMBO_WINDOW_BASE(get("timing.attacks.combo_window_base", 3));
    }

    private void loadDisplayConfig() {
        ConfigurationSection display = config.getConfigurationSection("display");
        if (display == null) return;

        // Default Teleport Duration
        Config.Display.setDEFAULT_TELEPORT_DURATION(get("display.default_teleport_duration", 2));

        // Status Display
        Config.Display.setSTATUS_DISPLAY_ENABLED(get("display.status_display.enabled", true));
        Config.Display.setSTATUS_DISPLAY_HEIGHT_OFFSET(get("display.status_display.height_offset", 2.0));
        Config.Display.setSTATUS_DISPLAY_UPDATE_INTERVAL(get("display.status_display.update_interval", 5));
        Config.Display.setSTATUS_DISPLAY_BLOCK_BRIGHTNESS(get("display.status_display_block_brightness", 15));
        Config.Display.setSTATUS_DISPLAY_SKY_BRIGHTNESS(get("display.status_display_sky_brightness", 15));

        // Item Display Follow
        Config.Display.setITEM_DISPLAY_FOLLOW_UPDATE_INTERVAL(get("display.item_display_follow.update_interval", 2));
        Config.Display.setITEM_DISPLAY_FOLLOW_PARTICLE_INTERVAL(get("display.item_display_follow.particle_interval", 4));
        String billboardStr = get("display.item_display_follow.billboard_mode", "FIXED");
        Config.Display.setITEM_DISPLAY_FOLLOW_BILLBOARD_MODE(Display.Billboard.valueOf(billboardStr));

        // Particles
        Config.Display.setPARTICLES_ENABLED(get("display.particles.enabled", true));
        Config.Display.setPARTICLES_DENSITY(get("display.particles.density", 10));
    }

    private void loadDetectionConfig() {
        ConfigurationSection detection = config.getConfigurationSection("detection");
        if (detection == null) return;

        // Ground Check
        Config.Detection.setGROUND_CHECK_MAX_DISTANCE(get("detection.ground_check.max_distance", 0.3));

        // Raytrace
        Config.Detection.setRAYTRACE_MAX_DISTANCE(get("detection.raytrace.max_distance", 50.0));
        Config.Detection.setRAYTRACE_STEP_SIZE(get("detection.raytrace.step_size", 0.1));
        Config.Detection.setRAYTRACE_IGNORE_PASSABLE_BLOCKS(get("detection.raytrace.ignore_passable_blocks", true));

        // Entity Detection
        Config.Detection.setENTITY_DETECTION_SEARCH_RADIUS(get("detection.entity_detection.search_radius", 10.0));
        Config.Detection.setENTITY_DETECTION_INCLUDE_SPECTATORS(get("detection.entity_detection.include_spectators", false));
    }

    private void loadAudioConfig() {
        ConfigurationSection audio = config.getConfigurationSection("audio");
        if (audio == null) return;

        // Sounds
        Config.Audio.setSOUNDS_ENABLED(get("audio.sounds.enabled", true));
        Config.Audio.setSOUNDS_GLOBAL_VOLUME(get("audio.sounds.global_volume", 1.0f));
        Config.Audio.setSOUNDS_GLOBAL_PITCH(get("audio.sounds.global_pitch", 1.0f));

        // Throw Sound
        String throwSoundStr = get("audio.throw.sound", "ENTITY_ENDER_DRAGON_FLAP");
        Config.Audio.setTHROW_SOUND(btm.sword.util.sound.SoundType.valueOf(throwSoundStr));
        Config.Audio.setTHROW_VOLUME(get("audio.throw.volume", 0.35f));
        Config.Audio.setTHROW_PITCH(get("audio.throw.pitch", 0.4f));

        // Attack Sound
        String attackSoundStr = get("audio.attack.sound", "ITEM_TRIDENT_THROW");
        Config.Audio.setATTACK_SOUND(btm.sword.util.sound.SoundType.valueOf(attackSoundStr));
        Config.Audio.setATTACK_VOLUME(get("audio.attack.volume", 0.055f));
        Config.Audio.setATTACK_PITCH(get("audio.attack.pitch", 1.5f));
    }

    private void loadEntityConfig() {
        ConfigurationSection entities = config.getConfigurationSection("entities");
        if (entities == null) return;

        // Player
        Config.Entity.setPLAYER_BASE_HEALTH(get("entities.player.base_health", 100.0));
        Config.Entity.setPLAYER_BASE_TOUGHNESS(get("entities.player.base_toughness", 20.0));
        Config.Entity.setPLAYER_BASE_SOULFIRE(get("entities.player.base_soulfire", 100.0));

        // Hostile
        Config.Entity.setHOSTILE_HEALTH_MULTIPLIER(get("entities.hostile.health_multiplier", 1.0));
        Config.Entity.setHOSTILE_DAMAGE_MULTIPLIER(get("entities.hostile.damage_multiplier", 1.0));

        // Combat Profile
        Config.Entity.setCOMBAT_PROFILE_SHARDS_CURRENT(get("entities.combat_profile.shards.current", 10.0f));
        Config.Entity.setCOMBAT_PROFILE_SHARDS_REGEN_PERIOD(get("entities.combat_profile.shards.regen_period", 50));
        Config.Entity.setCOMBAT_PROFILE_SHARDS_REGEN_AMOUNT(get("entities.combat_profile.shards.regen_amount", 1.0f));

        Config.Entity.setCOMBAT_PROFILE_TOUGHNESS_CURRENT(get("entities.combat_profile.toughness.current", 20.0f));
        Config.Entity.setCOMBAT_PROFILE_TOUGHNESS_REGEN_PERIOD(get("entities.combat_profile.toughness.regen_period", 20));
        Config.Entity.setCOMBAT_PROFILE_TOUGHNESS_REGEN_AMOUNT(get("entities.combat_profile.toughness.regen_amount", 0.5f));

        Config.Entity.setCOMBAT_PROFILE_SOULFIRE_CURRENT(get("entities.combat_profile.soulfire.current", 100.0f));
        Config.Entity.setCOMBAT_PROFILE_SOULFIRE_REGEN_PERIOD(get("entities.combat_profile.soulfire.regen_period", 5));
        Config.Entity.setCOMBAT_PROFILE_SOULFIRE_REGEN_AMOUNT(get("entities.combat_profile.soulfire.regen_amount", 0.2f));

        Config.Entity.setCOMBAT_PROFILE_FORM_CURRENT(get("entities.combat_profile.form.current", 10.0f));
        Config.Entity.setCOMBAT_PROFILE_FORM_REGEN_PERIOD(get("entities.combat_profile.form.regen_period", 60));
        Config.Entity.setCOMBAT_PROFILE_FORM_REGEN_AMOUNT(get("entities.combat_profile.form.regen_amount", 1.0f));

        Config.Entity.setCOMBAT_PROFILE_MAX_AIR_DODGES(get("entities.combat_profile.max_air_dodges", 1));
    }

    private void loadMovementConfig() {
        ConfigurationSection movement = config.getConfigurationSection("movement");
        if (movement == null) return;

        // Dash mechanics
        Config.Movement.setDASH_MAX_DISTANCE(get("movement.dash.max_distance", 10.0));
        Config.Movement.setDASH_CAST_DURATION(get("movement.dash.cast_duration", 5));
        Config.Movement.setDASH_BASE_POWER(get("movement.dash.base_power", 0.7));
        Config.Movement.setDASH_INITIAL_OFFSET_Y(get("movement.dash.initial_offset_y", 0.3));
        Config.Movement.setDASH_IMPEDANCE_CHECK_OFFSET_Y(get("movement.dash.impedance_check_offset_y", 0.5));
        Config.Movement.setDASH_FORWARD_MULTIPLIER(get("movement.dash.forward_multiplier", 0.5));
        Config.Movement.setDASH_UPWARD_MULTIPLIER(get("movement.dash.upward_multiplier", 0.15));
        Config.Movement.setDASH_UPWARD_BOOST(get("movement.dash.upward_boost", 0.05));
        Config.Movement.setDASH_RAY_HITBOX_RADIUS(get("movement.dash.ray_hitbox_radius", 0.7));
        Config.Movement.setDASH_SECANT_RADIUS(get("movement.dash.secant_radius", 0.3));
        Config.Movement.setDASH_GRAB_DISTANCE_SQUARED(get("movement.dash.grab_distance_squared", 8.5));
        Config.Movement.setDASH_BLOCK_CHECK_OFFSET_Y(get("movement.dash.block_check_offset_y", -0.75));
        Config.Movement.setDASH_VELOCITY_DAMPING(get("movement.dash.velocity_damping", 0.6));
        Config.Movement.setDASH_PARTICLE_TASK_DELAY(get("movement.dash.particle_task_delay", 0L));
        Config.Movement.setDASH_PARTICLE_TASK_PERIOD(get("movement.dash.particle_task_period", 2L));
        Config.Movement.setDASH_PARTICLE_TIMER_INCREMENT(get("movement.dash.particle_timer_increment", 2));
        Config.Movement.setDASH_PARTICLE_TIMER_THRESHOLD(get("movement.dash.particle_timer_threshold", 4));
        Config.Movement.setDASH_GRAB_CHECK_DELAY(get("movement.dash.grab_check_delay", 4L));
        Config.Movement.setDASH_VELOCITY_TASK_DELAY(get("movement.dash.velocity_task_delay", 0L));
        Config.Movement.setDASH_VELOCITY_TASK_PERIOD(get("movement.dash.velocity_task_period", 1L));
        Config.Movement.setDASH_PARTICLE_COUNT(get("movement.dash.particle_count", 100));
        Config.Movement.setDASH_PARTICLE_SPREAD_X(get("movement.dash.particle_spread_x", 1.25));
        Config.Movement.setDASH_PARTICLE_SPREAD_Y(get("movement.dash.particle_spread_y", 1.25));
        Config.Movement.setDASH_PARTICLE_SPREAD_Z(get("movement.dash.particle_spread_z", 1.25));
        Config.Movement.setDASH_FLAP_SOUND_VOLUME(get("movement.dash.flap_sound_volume", 0.6f));
        Config.Movement.setDASH_FLAP_SOUND_PITCH(get("movement.dash.flap_sound_pitch", 1.0f));
        Config.Movement.setDASH_SWEEP_SOUND_VOLUME(get("movement.dash.sweep_sound_volume", 0.3f));
        Config.Movement.setDASH_SWEEP_SOUND_PITCH(get("movement.dash.sweep_sound_pitch", 0.6f));

        // Toss
        Config.Movement.setTOSS_BASE_FORCE(get("movement.toss.base_force", 1.5));
        Config.Movement.setTOSS_MIGHT_MULTIPLIER_BASE(get("movement.toss.might_multiplier_base", 2.5));
        Config.Movement.setTOSS_MIGHT_MULTIPLIER_INCREMENT(get("movement.toss.might_multiplier_increment", 0.1));
        Config.Movement.setTOSS_UPWARD_PHASE_ITERATIONS(get("movement.toss.upward_phase_iterations", 2));
        Config.Movement.setTOSS_UPWARD_VELOCITY_Y(get("movement.toss.upward_velocity_y", 0.25));
        Config.Movement.setTOSS_FORWARD_PHASE_ITERATIONS(get("movement.toss.forward_phase_iterations", 3));
        Config.Movement.setTOSS_ANIMATION_ITERATIONS(get("movement.toss.animation_iterations", 15));
        Config.Movement.setTOSS_LOCATION_OFFSET_MULTIPLIER(get("movement.toss.location_offset_multiplier", 0.3));
        Config.Movement.setTOSS_PARTICLE_HEIGHT_MULTIPLIER(get("movement.toss.particle_height_multiplier", 0.5));
        Config.Movement.setTOSS_RAY_TRACE_DISTANCE_MULTIPLIER(get("movement.toss.ray_trace_distance_multiplier", 0.6));
        Config.Movement.setTOSS_ENTITY_DETECTION_RADIUS(get("movement.toss.entity_detection_radius", 0.4));
        Config.Movement.setTOSS_KNOCKBACK_MULTIPLIER(get("movement.toss.knockback_multiplier", 0.3));
        Config.Movement.setTOSS_EXPLOSION_POWER(get("movement.toss.explosion_power", 2.0f));
        Config.Movement.setTOSS_HIT_INVULNERABILITY_TICKS(get("movement.toss.hit_invulnerability_ticks", 3));
        Config.Movement.setTOSS_HIT_SHARD_DAMAGE(get("movement.toss.hit_shard_damage", 2));
        Config.Movement.setTOSS_HIT_TOUGHNESS_DAMAGE(get("movement.toss.hit_toughness_damage", 30.0f));
        Config.Movement.setTOSS_HIT_SOULFIRE_REDUCTION(get("movement.toss.hit_soulfire_reduction", 5.0f));

        // Grab
        Config.Movement.setGRAB_PULL_STRENGTH(get("movement.grab.pull_strength", 0.8));
        Config.Movement.setGRAB_MAX_RANGE(get("movement.grab.max_range", 3.0));
        Config.Movement.setGRAB_HOLD_DURATION(get("movement.grab.hold_duration", 40));
    }

    private void loadWorldConfig() {
        ConfigurationSection world = config.getConfigurationSection("world");
        if (world == null) return;

        // Block Interaction
        Config.World.setBLOCK_INTERACTION_ALLOW_BLOCK_BREAKING(get("world.block_interaction.allow_block_breaking", false));
        Config.World.setBLOCK_INTERACTION_RESPECT_WORLD_GUARD(get("world.block_interaction.respect_world_guard", true));

        // Explosions
        Config.World.setEXPLOSIONS_SET_FIRE(get("world.explosions.set_fire", false));
        Config.World.setEXPLOSIONS_BREAK_BLOCKS(get("world.explosions.break_blocks", false));
    }

    private void loadDebugConfig() {
        ConfigurationSection debug = config.getConfigurationSection("debug");
        if (debug == null) return;

        // Logging
        Config.Debug.setLOGGING_VERBOSE_COMBAT(get("debug.logging.verbose_combat", false));
        Config.Debug.setLOGGING_VERBOSE_MOVEMENT(get("debug.logging.verbose_movement", false));
        Config.Debug.setLOGGING_VERBOSE_CONFIG(get("debug.logging.verbose_config", false));

        // Visualization
        Config.Debug.setVISUALIZATION_SHOW_HITBOXES(get("debug.visualization.show_hitboxes", false));
        Config.Debug.setVISUALIZATION_SHOW_RAYTRACES(get("debug.visualization.show_raytraces", false));
    }

    /**
     * Reloads the configuration from disk (hot reload).
     * <p>
     * Intended for use with reload commands during runtime testing.
     * </p>
     *
     * @return true if reload was successful
     */
    public boolean reload() {
        plugin.getLogger().info("Reloading configuration...");
        boolean success = loadConfig();
        if (success) {
            plugin.getLogger().info("Configuration reloaded successfully!");
        }
        return success;
    }

    /**
     * Saves the current configuration to disk.
     * <p>
     * Note: This saves the in-memory config state from the YAML file.
     * To persist changes made to static Config fields, they must first
     * be written back to the config object.
     * </p>
     */
    public void saveConfig() {
        try {
            config.save(configFile);
            plugin.getLogger().info("Configuration saved to disk");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config.yaml", e);
        }
    }

    /**
     * Gets the raw Bukkit FileConfiguration object.
     * <p>
     * For advanced use cases. Prefer using static Config class when possible.
     * </p>
     *
     * @return The underlying FileConfiguration
     */
    public FileConfiguration getRawConfig() {
        return config;
    }

    /**
     * Resets configuration to default values.
     * <p>
     * Backs up existing config and replaces with default from resources.
     * </p>
     *
     * @return true if reset was successful
     */
    @SuppressWarnings("all")
    public boolean resetToDefaults() {
        try {
            // Backup current config
            File backup = new File(plugin.getDataFolder(), "config.yaml.backup");
            Files.copy(configFile.toPath(), backup.toPath());

            // Delete current and recreate from defaults
            configFile.delete();
            setupConfig();
            loadConfig();

            plugin.getLogger().info("Configuration reset to defaults (backup saved as config.yaml.backup)");
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to reset configuration", e);
            return false;
        }
    }

    /**
     * Gets the plugin instance.
     *
     * @return The Sword plugin instance
     */
    public Sword getPlugin() {
        return plugin;
    }
}
