package btm.sword.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import btm.sword.util.sound.SoundType;

/**
 * Static configuration class for Sword: Combat Evolved.
 * <p>
 * Provides centralized, type-safe access to all configuration values.
 * Values are loaded from config.yaml by {@link ConfigManager} and can be
 * hot-reloaded at runtime using /sword reload.
 * </p>
 * <p>
 * Uses a self-registering ConfigEntry pattern where each field registers itself
 * in a static initializer block. ConfigManager loops through the ENTRIES list
 * for reload/save operations.
 * </p>
 */
public class Config {

    // ==============================================================================
    // CONFIG ENTRY REGISTRATION SYSTEM
    // ==============================================================================

    /**
     * ConfigEntry represents a single configuration value with metadata for loading and saving.
     * <p>
     * Each entry contains:
     * <ul>
     *   <li><b>path</b> - YAML path (e.g., "angles.umbral_blade_idle_period")</li>
     *   <li><b>defaultValue</b> - Default value if not in config.yaml</li>
     *   <li><b>type</b> - Java class type for type safety</li>
     *   <li><b>assign</b> - Consumer lambda to update the static field</li>
     *   <li><b>loader</b> - Custom loader for type-specific YAML parsing</li>
     * </ul>
     * </p>
     */
    public static final class ConfigEntry<T> {
        public final String path;
        public final T defaultValue;
        public final Class<T> type;
        public final Consumer<T> assign;
        public final Loader<T> loader;

        /**
         * Functional interface for custom YAML loading logic.
         * @param <T> The type of value to load
         */
        @FunctionalInterface
        public interface Loader<T> {
            T load(ConfigurationSection section, String path, T defaultValue);
        }

        public ConfigEntry(String path, T defaultValue, Class<T> type, Consumer<T> assign, Loader<T> loader) {
            this.path = path;
            this.defaultValue = defaultValue;
            this.type = type;
            this.assign = assign;
            this.loader = loader;
        }
    }

    /**
     * List of all registered configuration entries.
     * <p>
     * Populated by static initializer blocks in each config section.
     * ConfigManager iterates this list for reload/save operations.
     * </p>
     */
    public static final List<ConfigEntry<?>> ENTRIES = new ArrayList<>();

    /**
     * Register a configuration entry.
     * <p>
     * Called from static initializer blocks to add entries to the ENTRIES list.
     * </p>
     */
    public static <T> void register(String path, T defaultValue, Class<T> type, Consumer<T> assign, ConfigEntry.Loader<T> loader) {
        ENTRIES.add(new ConfigEntry<>(path, defaultValue, type, assign, loader));
    }

    // ==============================================================================
    // HELPER METHODS FOR COMMON TYPES
    // ==============================================================================

    /**
     * Loader for List<String> configuration values.
     */
    public static List<String> loadStringList(ConfigurationSection section, String path, List<String> defaultValue) {
        return section.contains(path) ? section.getStringList(path) : defaultValue;
    }

    /**
     * Loader for List<EntityType> configuration values.
     */
    public static List<EntityType> loadEntityTypeList(ConfigurationSection section, String path, List<EntityType> defaultValue) {
        if (!section.contains(path)) return defaultValue;
        List<String> names = section.getStringList(path);
        return names.stream()
            .map(name -> {
                try {
                    return EntityType.valueOf(name.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Loader for Enum configuration values.
     */
    public static <E extends Enum<E>> E loadEnum(ConfigurationSection section, String path, E defaultValue, Class<E> enumClass) {
        if (!section.contains(path)) return defaultValue;
        String value = section.getString(path);
        if (value == null) return defaultValue;
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public static Float loadFloat(ConfigurationSection section, String path, Float defaultValue) {
        return (float) section.getDouble(path, defaultValue);
    }

    /**
     * Loader for SoundType enum values.
     */
    public static SoundType loadSoundType(ConfigurationSection section, String path, SoundType defaultValue) {
        if (!section.contains(path)) return defaultValue;
        String value = section.getString(path);
        if (value == null) return defaultValue;
        try {
            return SoundType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    // ==============================================================================
    // ANGLES - Common angle constants
    // ==============================================================================
    /**
     * Angle constants used throughout the combat system.
     * <p>
     * All angle values are in <b>radians</b> (π = 180°). Used primarily for
     * entity rotation, attack arcs, and visual effects.
     * </p>
     *
     * @see btm.sword.system.entity.umbral.UmbralBlade Umbral blade rotation behavior
     */
    public static class Angles {
        public static float UMBRAL_BLADE_IDLE_PERIOD = (float) Math.PI / 8; // radians (22.5°)
        static { register(
            "angles.umbral_blade_idle_period",
            UMBRAL_BLADE_IDLE_PERIOD,
            Float.class,
            v -> UMBRAL_BLADE_IDLE_PERIOD = v,
            (ConfigurationSection s, String p, Float d) -> (float) s.getDouble(p, d)
        ); }
    }

    // ==============================================================================
    // DIRECTION - Directional vector constants (immutable via cloning)
    // ==============================================================================
    /**
     * Directional vector constants for common 3D directions.
     * <p>
     * All vectors are <b>unit vectors</b> (length = 1.0). Methods return clones
     * to prevent external mutation of the cached vectors.
     * </p>
     *
     * <h2>Coordinate System</h2>
     * <ul>
     *   <li><b>+X</b>: East</li>
     *   <li><b>-X</b>: West</li>
     *   <li><b>+Y</b>: Up</li>
     *   <li><b>-Y</b>: Down</li>
     *   <li><b>+Z</b>: South</li>
     *   <li><b>-Z</b>: North</li>
     * </ul>
     *
     * @see org.bukkit.util.Vector Bukkit vector implementation
     */
    public static class Direction {
        private static final Vector UP = new Vector(0, 1, 0);
        public static Vector UP() { return UP.clone(); }

        private static final Vector DOWN = new Vector(0, -1, 0);
        public static Vector DOWN() { return DOWN.clone(); }

        private static final Vector NORTH = new Vector(0, 0, -1);
        public static Vector NORTH() { return NORTH.clone(); }

        private static final Vector SOUTH = new Vector(0, 0, 1);
        public static Vector SOUTH() { return SOUTH.clone(); }

        private static final Vector OUT_UP = new Vector(0, 1, 1);
        public static Vector OUT_UP() { return OUT_UP.clone(); }

        private static final Vector OUT_DOWN = new Vector(0, -1, 1);
        public static Vector OUT_DOWN() { return OUT_DOWN.clone(); }
    }

    // ==============================================================================
    //region PHYSICS - Projectile motion, gravity, and velocity
    // ==============================================================================
    /**
     * Physics simulation parameters for projectiles and combat movement.
     * <p>
     * Controls thrown item behavior (gravity, rotation, offsets) and attack knockback
     * physics. All distance values are in <b>blocks</b>, angles in <b>radians</b>.
     * </p>
     *
     * <h2>Key Subsystems</h2>
     * <ul>
     *   <li><b>Thrown Items</b> - Gravity damping, rotation speeds, visual offsets</li>
     *   <li><b>Attack Velocity</b> - Knockback vectors, grounded damping, vertical boost</li>
     * </ul>
     *
     * @see btm.sword.system.action.utility.thrown.ThrownItem Thrown item physics implementation
     * @see btm.sword.system.attack.Attack Attack knockback application
     */
    public static class Physics {
        // Thrown items configuration
        public static double THROWN_ITEMS_GRAVITY_DAMPER = 46.0; // damping factor (higher = less gravity effect)
        static { register(
            "physics.thrown_items_gravity_damper",
            THROWN_ITEMS_GRAVITY_DAMPER, Double.class,
            v -> THROWN_ITEMS_GRAVITY_DAMPER = v,
            ConfigurationSection::getDouble); }

        public static double THROWN_ITEMS_TRAJECTORY_ROTATION = 0.03696; // radians/tick
        static { register(
            "physics.thrown_items_trajectory_rotation",
            THROWN_ITEMS_TRAJECTORY_ROTATION, Double.class,
            v -> THROWN_ITEMS_TRAJECTORY_ROTATION = v,
            ConfigurationSection::getDouble); }

        public static float THROWN_ITEMS_DISPLAY_OFFSET_X = -0.5f;
        static { register(
            "physics.thrown_items_display_offset_x",
            THROWN_ITEMS_DISPLAY_OFFSET_X, Float.class,
            v -> THROWN_ITEMS_DISPLAY_OFFSET_X = v,
            Config::loadFloat); }

        public static float THROWN_ITEMS_DISPLAY_OFFSET_Y = 0.1f;
        static { register(
            "physics.thrown_items_display_offset_y",
            THROWN_ITEMS_DISPLAY_OFFSET_Y, Float.class,
            v -> THROWN_ITEMS_DISPLAY_OFFSET_Y = v,
            Config::loadFloat); }

        public static float THROWN_ITEMS_DISPLAY_OFFSET_Z = 0.5f;
        static { register(
            "physics.thrown_items_display_offset_z",
            THROWN_ITEMS_DISPLAY_OFFSET_Z, Float.class,
            v -> THROWN_ITEMS_DISPLAY_OFFSET_Z = v,
            Config::loadFloat); }

        public static double THROWN_ITEMS_ORIGIN_OFFSET_FORWARD = 0.5;
        static { register(
            "physics.thrown_items_origin_offset_forward",
            THROWN_ITEMS_ORIGIN_OFFSET_FORWARD, Double.class,
            v -> THROWN_ITEMS_ORIGIN_OFFSET_FORWARD = v,
            ConfigurationSection::getDouble); }

        public static double THROWN_ITEMS_ORIGIN_OFFSET_UP = 0.1;
        static { register(
            "physics.thrown_items_origin_offset_up",
            THROWN_ITEMS_ORIGIN_OFFSET_UP, Double.class,
            v -> THROWN_ITEMS_ORIGIN_OFFSET_UP = v,
            ConfigurationSection::getDouble); }

        public static double THROWN_ITEMS_ORIGIN_OFFSET_BACK = -0.25;
        static { register(
            "physics.thrown_items_origin_offset_back",
            THROWN_ITEMS_ORIGIN_OFFSET_BACK, Double.class,
            v -> THROWN_ITEMS_ORIGIN_OFFSET_BACK = v,
            ConfigurationSection::getDouble
        ); }

        // Thrown items rotation speed configuration
        public static double THROWN_ITEMS_ROTATION_SPEED_SWORD = 0.0; // radians/tick
        static { register(
            "physics.thrown_items_rotation_speed_sword",
            THROWN_ITEMS_ROTATION_SPEED_SWORD, Double.class,
            v -> THROWN_ITEMS_ROTATION_SPEED_SWORD = v,
            ConfigurationSection::getDouble
        ); }

        public static double THROWN_ITEMS_ROTATION_SPEED_AXE = -Math.PI / 8; // radians/tick
        static { register(
            "physics.thrown_items_rotation_speed_axe",
            THROWN_ITEMS_ROTATION_SPEED_AXE, Double.class,
            v -> THROWN_ITEMS_ROTATION_SPEED_AXE = v,
            ConfigurationSection::getDouble
        ); }

        public static double THROWN_ITEMS_ROTATION_SPEED_HOE = -Math.PI / 8; // radians/tick
        static { register(
            "physics.thrown_items_rotation_speed_hoe",
            THROWN_ITEMS_ROTATION_SPEED_HOE, Double.class,
            v -> THROWN_ITEMS_ROTATION_SPEED_HOE = v,
            ConfigurationSection::getDouble
        ); }

        public static double THROWN_ITEMS_ROTATION_SPEED_PICKAXE = -Math.PI / 8; // radians/tick
        static { register(
            "physics.thrown_items_rotation_speed_pickaxe",
            THROWN_ITEMS_ROTATION_SPEED_PICKAXE, Double.class,
            v -> THROWN_ITEMS_ROTATION_SPEED_PICKAXE = v,
            ConfigurationSection::getDouble
        ); }

        public static double THROWN_ITEMS_ROTATION_SPEED_SHOVEL = -Math.PI / 8; // radians/tick
        static { register(
            "physics.thrown_items_rotation_speed_shovel",
            THROWN_ITEMS_ROTATION_SPEED_SHOVEL, Double.class,
            v -> THROWN_ITEMS_ROTATION_SPEED_SHOVEL = v,
            ConfigurationSection::getDouble
        ); }

        public static double THROWN_ITEMS_ROTATION_SPEED_SHIELD = -Math.PI / 8; // radians/tick
        static { register(
            "physics.thrown_items_rotation_speed_shield",
            THROWN_ITEMS_ROTATION_SPEED_SHIELD, Double.class,
            v -> THROWN_ITEMS_ROTATION_SPEED_SHIELD = v,
            ConfigurationSection::getDouble
        ); }

        public static double THROWN_ITEMS_ROTATION_SPEED_DEFAULT_SPEED = Math.PI / 32; // radians/tick
        static { register(
            "physics.thrown_items_rotation_speed_default_speed",
            THROWN_ITEMS_ROTATION_SPEED_DEFAULT_SPEED, Double.class,
            v -> THROWN_ITEMS_ROTATION_SPEED_DEFAULT_SPEED = v,
            ConfigurationSection::getDouble
        ); }

        // Attack velocity configuration
        public static double ATTACK_VELOCITY_GROUNDED_DAMPING_HORIZONTAL = 0.3; // multiplier (0-1)
        static { register(
            "physics.attack_velocity_grounded_damping_horizontal",
            ATTACK_VELOCITY_GROUNDED_DAMPING_HORIZONTAL, Double.class,
            v -> ATTACK_VELOCITY_GROUNDED_DAMPING_HORIZONTAL = v,
            ConfigurationSection::getDouble
        ); }

        public static double ATTACK_VELOCITY_GROUNDED_DAMPING_VERTICAL = 0.4; // multiplier (0-1)
        static { register(
            "physics.attack_velocity_grounded_damping_vertical",
            ATTACK_VELOCITY_GROUNDED_DAMPING_VERTICAL, Double.class,
            v -> ATTACK_VELOCITY_GROUNDED_DAMPING_VERTICAL = v,
            ConfigurationSection::getDouble
        ); }

        public static double ATTACK_VELOCITY_KNOCKBACK_VERTICAL_BASE = 0.25; // blocks/tick
        static { register(
            "physics.attack_velocity_knockback_vertical_base",
            ATTACK_VELOCITY_KNOCKBACK_VERTICAL_BASE, Double.class,
            v -> ATTACK_VELOCITY_KNOCKBACK_VERTICAL_BASE = v,
            ConfigurationSection::getDouble
        ); }

        public static double ATTACK_VELOCITY_KNOCKBACK_HORIZONTAL_MODIFIER = 0.1; // multiplier
        static { register(
            "physics.attack_velocity_knockback_horizontal_modifier",
            ATTACK_VELOCITY_KNOCKBACK_HORIZONTAL_MODIFIER, Double.class,
            v -> ATTACK_VELOCITY_KNOCKBACK_HORIZONTAL_MODIFIER = v,
            ConfigurationSection::getDouble); }

        public static double ATTACK_VELOCITY_KNOCKBACK_NORMAL_MULTIPLIER = 0.7; // multiplier
        static { register(
            "physics.attack_velocity_knockback_normal_multiplier",
            ATTACK_VELOCITY_KNOCKBACK_NORMAL_MULTIPLIER, Double.class,
            v -> ATTACK_VELOCITY_KNOCKBACK_NORMAL_MULTIPLIER = v,
            ConfigurationSection::getDouble); }
    }
    //endregion

    // ==============================================================================
    //region COMBAT - Damage, hitboxes, attack patterns, combat mechanics
    // ==============================================================================
    /**
     * Combat system configuration for damage, hitboxes, and attack mechanics.
     * <p>
     * Defines damage calculations, hitbox dimensions, attack timing, range multipliers,
     * impalement mechanics, and entity exemptions. Distances in <b>blocks</b>, time in
     * <b>ticks</b> (20 ticks/second), damage in <b>health points</b> (1 heart = 2 HP).
     * </p>
     *
     * <h2>Key Subsystems</h2>
     * <ul>
     *   <li><b>Attacks</b> - Base damage, cast timing, duration, range multipliers</li>
     *   <li><b>Hitboxes</b> - 3D collision box dimensions (reach/width/height)</li>
     *   <li><b>Thrown Damage</b> - Projectile damage, knockback, armor interactions</li>
     *   <li><b>Impalement</b> - Damage-over-time, pinning, head detection</li>
     * </ul>
     *
     * @see btm.sword.system.attack.Attack Attack execution and damage application
     * @see btm.sword.system.action.AttackAction Attack state machine
     */
    public static class Combat {
        // Attacks configuration
        public static double ATTACKS_BASE_DAMAGE = 20.0; // HP (1 heart = 2 HP)
        static { register("combat.attacks_base_damage",
                ATTACKS_BASE_DAMAGE, Double.class,
                v -> ATTACKS_BASE_DAMAGE = v,
                ConfigurationSection::getDouble
        ); }

        public static double ATTACKS_DOWN_AIR_THRESHOLD = -0.85; // dot product (-1 to 1)
        static { register("combat.attacks_down_air_threshold",
                ATTACKS_DOWN_AIR_THRESHOLD, Double.class,
                v -> ATTACKS_DOWN_AIR_THRESHOLD = v,
                ConfigurationSection::getDouble
        ); }

        public static long ATTACKS_CAST_TIMING_MIN_DURATION = 1L;
        static { register("combat.attacks_cast_timing_min_duration",
                ATTACKS_CAST_TIMING_MIN_DURATION, Long.class,
                v -> ATTACKS_CAST_TIMING_MIN_DURATION = v,
                ConfigurationSection::getLong
        ); }

        public static long ATTACKS_CAST_TIMING_MAX_DURATION = 3L;
        static { register("combat.attacks_cast_timing_max_duration",
                ATTACKS_CAST_TIMING_MAX_DURATION, Long.class,
                v -> ATTACKS_CAST_TIMING_MAX_DURATION = v,
                ConfigurationSection::getLong
        ); }

        public static double ATTACKS_CAST_TIMING_REDUCTION_RATE = 0.2; // ticks/combo_count
        static { register("combat.attacks_cast_timing_reduction_rate",
                ATTACKS_CAST_TIMING_REDUCTION_RATE, Double.class,
                v -> ATTACKS_CAST_TIMING_REDUCTION_RATE = v,
                ConfigurationSection::getDouble
        ); }

        public static int ATTACKS_DURATION_MULTIPLIER = 500; // milliseconds multiplier
        static { register("combat.attacks_duration_multiplier",
                ATTACKS_DURATION_MULTIPLIER, Integer.class,
                v -> ATTACKS_DURATION_MULTIPLIER = v,
                ConfigurationSection::getInt
        ); }

        // Attacks range multipliers configuration
        public static double ATTACKS_RANGE_MULTIPLIERS_BASIC_1 = 1.4;
        static { register("combat.attacks_range_multipliers_basic_1",
                ATTACKS_RANGE_MULTIPLIERS_BASIC_1, Double.class,
                v -> ATTACKS_RANGE_MULTIPLIERS_BASIC_1 = v,
                ConfigurationSection::getDouble
        ); }

        public static double ATTACKS_RANGE_MULTIPLIERS_BASIC_2 = 1.4;
        static { register("combat.attacks_range_multipliers_basic_2",
                ATTACKS_RANGE_MULTIPLIERS_BASIC_2, Double.class,
                v -> ATTACKS_RANGE_MULTIPLIERS_BASIC_2 = v,
                ConfigurationSection::getDouble
        ); }

        public static double ATTACKS_RANGE_MULTIPLIERS_BASIC_3 = 1.4;
        static { register("combat.attacks_range_multipliers_basic_3",
                ATTACKS_RANGE_MULTIPLIERS_BASIC_3, Double.class,
                v -> ATTACKS_RANGE_MULTIPLIERS_BASIC_3 = v,
                ConfigurationSection::getDouble
        ); }

        public static double ATTACKS_RANGE_MULTIPLIERS_NEUTRAL_AIR = 1.3;
        static { register("combat.attacks_range_multipliers_neutral_air",
                ATTACKS_RANGE_MULTIPLIERS_NEUTRAL_AIR, Double.class,
                v -> ATTACKS_RANGE_MULTIPLIERS_NEUTRAL_AIR = v,
                ConfigurationSection::getDouble
        ); }

        public static double ATTACKS_RANGE_MULTIPLIERS_DOWN_AIR = 1.2;
        static { register("combat.attacks_range_multipliers_down_air",
                ATTACKS_RANGE_MULTIPLIERS_DOWN_AIR, Double.class,
                v -> ATTACKS_RANGE_MULTIPLIERS_DOWN_AIR = v,
                ConfigurationSection::getDouble
        ); }

        // Hitboxes configuration
        public static double HITBOXES_BASIC_REACH = 1.5;
        static { register("combat.hitboxes_basic_reach",
                HITBOXES_BASIC_REACH, Double.class,
                v -> HITBOXES_BASIC_REACH = v,
                ConfigurationSection::getDouble

        ); }

        public static double HITBOXES_BASIC_WIDTH = 1.5;
        static { register("combat.hitboxes_basic_width",
                HITBOXES_BASIC_WIDTH, Double.class,
                v -> HITBOXES_BASIC_WIDTH = v,
                ConfigurationSection::getDouble
        ); }

        public static double HITBOXES_BASIC_HEIGHT = 1.5;
        static { register("combat.hitboxes_basic_height",
                HITBOXES_BASIC_HEIGHT, Double.class,
                v -> HITBOXES_BASIC_HEIGHT = v,
                ConfigurationSection::getDouble
        ); }

        public static double HITBOXES_DOWN_AIR_REACH = 1.6;
        static { register("combat.hitboxes_down_air_reach",
                HITBOXES_DOWN_AIR_REACH, Double.class,
                v -> HITBOXES_DOWN_AIR_REACH = v,
                ConfigurationSection::getDouble
        ); }

        public static double HITBOXES_DOWN_AIR_WIDTH = 1.4;
        static { register("combat.hitboxes_down_air_width",
                HITBOXES_DOWN_AIR_WIDTH, Double.class,
                v -> HITBOXES_DOWN_AIR_WIDTH = v,
                ConfigurationSection::getDouble
        ); }

        public static double HITBOXES_DOWN_AIR_HEIGHT = 2.5;
        static { register("combat.hitboxes_down_air_height",
                HITBOXES_DOWN_AIR_HEIGHT, Double.class,
                v -> HITBOXES_DOWN_AIR_HEIGHT = v,
                ConfigurationSection::getDouble
        ); }

        public static double HITBOXES_SECANT_RADIUS = 0.4;
        static { register("combat.hitboxes_secant_radius",
                HITBOXES_SECANT_RADIUS, Double.class,
                v -> HITBOXES_SECANT_RADIUS = v,
                ConfigurationSection::getDouble
        ); }

        // Thrown damage configuration
        public static double THROWN_DAMAGE_SWORD_DAMAGE_MULTIPLIER = 1.0;
        static { register("combat.thrown_damage_sword_damage_multiplier",
                THROWN_DAMAGE_SWORD_DAMAGE_MULTIPLIER, Double.class,
                v -> THROWN_DAMAGE_SWORD_DAMAGE_MULTIPLIER = v,
                ConfigurationSection::getDouble
        ); }

        public static double THROWN_DAMAGE_ITEM_VELOCITY_MULTIPLIER = 1.5;
        static {
            register("combat.thrown_damage_item_velocity_multiplier",
                THROWN_DAMAGE_ITEM_VELOCITY_MULTIPLIER, Double.class,
                v -> THROWN_DAMAGE_ITEM_VELOCITY_MULTIPLIER = v,
                ConfigurationSection::getDouble
        ); }

        public static double THROWN_DAMAGE_BASE_THROWN_DAMAGE = 12.0;
        static { register("combat.thrown_damage_base_thrown_damage",
                THROWN_DAMAGE_BASE_THROWN_DAMAGE, Double.class,
                v -> THROWN_DAMAGE_BASE_THROWN_DAMAGE = v,
                ConfigurationSection::getDouble
        ); }

        // Thrown damage sword/axe configuration
        public static int THROWN_DAMAGE_SWORD_AXE_INVULNERABILITY_TICKS = 0;
        static { register(
                "combat.thrown_damage_sword_axe_invulnerability_ticks",
                THROWN_DAMAGE_SWORD_AXE_INVULNERABILITY_TICKS, Integer.class,
                v -> THROWN_DAMAGE_SWORD_AXE_INVULNERABILITY_TICKS = v,
                ConfigurationSection::getInt
        ); }

        public static int THROWN_DAMAGE_SWORD_AXE_BASE_SHARDS = 2;
        static { register(
                "combat.thrown_damage_sword_axe_base_shards",
                THROWN_DAMAGE_SWORD_AXE_BASE_SHARDS, Integer.class,
                v -> THROWN_DAMAGE_SWORD_AXE_BASE_SHARDS = v,
                ConfigurationSection::getInt
        ); }

        public static float THROWN_DAMAGE_SWORD_AXE_TOUGHNESS_DAMAGE = 75.0f;
        static { register(
                "combat.thrown_damage_sword_axe_toughness_damage",
                THROWN_DAMAGE_SWORD_AXE_TOUGHNESS_DAMAGE, Float.class,
                v -> THROWN_DAMAGE_SWORD_AXE_TOUGHNESS_DAMAGE = v,
                Config::loadFloat
        ); }

        public static float THROWN_DAMAGE_SWORD_AXE_SOULFIRE_REDUCTION = 50.0f;
        static { register(
                "combat.thrown_damage_sword_axe_soulfire_reduction",
                THROWN_DAMAGE_SWORD_AXE_SOULFIRE_REDUCTION, Float.class,
                v -> THROWN_DAMAGE_SWORD_AXE_SOULFIRE_REDUCTION = v,
                Config::loadFloat
        ); }

        public static double THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_GROUNDED = 0.7;
        static { register(
                "combat.thrown_damage_sword_axe_knockback_grounded",
                THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_GROUNDED, Double.class,
                v -> THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_GROUNDED = v,
                ConfigurationSection::getDouble
        ); }

        public static double THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_AIRBORNE = 1.0;
        static { register(
                "combat.thrown_damage_sword_axe_knockback_airborne",
                THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_AIRBORNE, Double.class,
                v -> THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_AIRBORNE = v,
                ConfigurationSection::getDouble
        ); }

        // Thrown damage other items configuration
        public static int THROWN_DAMAGE_OTHER_INVULNERABILITY_TICKS = 0;
        static { register(
                "combat.thrown_damage_other_invulnerability_ticks",
                THROWN_DAMAGE_OTHER_INVULNERABILITY_TICKS, Integer.class,
                v -> THROWN_DAMAGE_OTHER_INVULNERABILITY_TICKS = v,
                ConfigurationSection::getInt
        ); }

        public static int THROWN_DAMAGE_OTHER_BASE_SHARDS = 2;
        static { register(
                "combat.thrown_damage_other_base_shards",
                THROWN_DAMAGE_OTHER_BASE_SHARDS, Integer.class,
                v -> THROWN_DAMAGE_OTHER_BASE_SHARDS = v,
                ConfigurationSection::getInt
        ); }

        public static float THROWN_DAMAGE_OTHER_TOUGHNESS_DAMAGE = 75.0f;
        static { register(
                "combat.thrown_damage_other_toughness_damage",
                THROWN_DAMAGE_OTHER_TOUGHNESS_DAMAGE, Float.class,
                v -> THROWN_DAMAGE_OTHER_TOUGHNESS_DAMAGE = v,
                Config::loadFloat
        ); }

        public static float THROWN_DAMAGE_OTHER_SOULFIRE_REDUCTION = 50.0f;
        static { register(
                "combat.thrown_damage_other_soulfire_reduction",
                THROWN_DAMAGE_OTHER_SOULFIRE_REDUCTION, Float.class,
                v -> THROWN_DAMAGE_OTHER_SOULFIRE_REDUCTION = v,
                Config::loadFloat
        ); }

        public static double THROWN_DAMAGE_OTHER_KNOCKBACK_MULTIPLIER = 0.7;
        static { register(
                "combat.thrown_damage_other_knockback_multiplier",
                THROWN_DAMAGE_OTHER_KNOCKBACK_MULTIPLIER, Double.class,
                v -> THROWN_DAMAGE_OTHER_KNOCKBACK_MULTIPLIER = v,
                ConfigurationSection::getDouble
        ); }

        public static float THROWN_DAMAGE_OTHER_EXPLOSION_POWER = 1.0f;
        static { register(
                "combat.thrown_damage_other_explosion_power",
                THROWN_DAMAGE_OTHER_EXPLOSION_POWER, Float.class,
                v -> THROWN_DAMAGE_OTHER_EXPLOSION_POWER = v,
                Config::loadFloat
        ); }

        // Impalement configuration
        public static double IMPALEMENT_DAMAGE_PER_TICK = 2.0;
        static { register(
                "combat.impalement_damage_per_tick",
                IMPALEMENT_DAMAGE_PER_TICK, Double.class,
                v -> IMPALEMENT_DAMAGE_PER_TICK = v,
                ConfigurationSection::getDouble
        ); }

        public static int IMPALEMENT_TICKS_BETWEEN_DAMAGE = 10;
        static { register(
                "combat.impalement_ticks_between_damage",
                IMPALEMENT_TICKS_BETWEEN_DAMAGE, Integer.class,
                v -> IMPALEMENT_TICKS_BETWEEN_DAMAGE = v,
                ConfigurationSection::getInt
        ); }

        public static int IMPALEMENT_MAX_IMPALEMENTS = 3;
        static { register(
                "combat.impalement_max_impalements",
                IMPALEMENT_MAX_IMPALEMENTS, Integer.class,
                v -> IMPALEMENT_MAX_IMPALEMENTS = v,
                ConfigurationSection::getInt
        ); }

        public static double IMPALEMENT_HEAD_ZONE_RATIO = 0.8; // 0-1 (fraction of entity height)
        static { register(
                "combat.impalement_head_zone_ratio",
                IMPALEMENT_HEAD_ZONE_RATIO, Double.class,
                v -> IMPALEMENT_HEAD_ZONE_RATIO = v,
                ConfigurationSection::getDouble
        ); }

        @SuppressWarnings("unchecked")
        static Class<List<EntityType>> entityListClass() {
            return (Class<List<EntityType>>) (Class<?>) List.class;
        }

        public static List<EntityType> IMPALEMENT_HEAD_FOLLOW_EXCEPTIONS = List.of(EntityType.SPIDER);
        static { register(
                "combat.impalement_head_follow_exceptions",
                IMPALEMENT_HEAD_FOLLOW_EXCEPTIONS, entityListClass(),
                v -> IMPALEMENT_HEAD_FOLLOW_EXCEPTIONS = v,
                Config::loadEntityTypeList
        ); }

        public static int IMPALEMENT_PIN_MAX_ITERATIONS = 50;
        static { register(
                "combat.impalement_pin_max_iterations",
                IMPALEMENT_PIN_MAX_ITERATIONS, Integer.class,
                v -> IMPALEMENT_PIN_MAX_ITERATIONS = v,
                ConfigurationSection::getInt
        ); }

        public static int IMPALEMENT_PIN_CHECK_INTERVAL = 2;
        static { register(
                "combat.impalement_pin_check_interval",
                IMPALEMENT_PIN_CHECK_INTERVAL, Integer.class,
                v -> IMPALEMENT_PIN_CHECK_INTERVAL = v,
                ConfigurationSection::getInt
        ); }

        @SuppressWarnings("unchecked")
        static Class<List<String>> stringListClass() {
            return (Class<List<String>>) (Class<?>) List.class;
        }

        // Attack class configuration
        public static List<String> ATTACK_CLASS_EXEMPT_FROM_COMBAT = List.of(
            "ARMOR_STAND", "ITEM_FRAME", "GLOW_ITEM_FRAME", "PAINTING",
            "ITEM_DISPLAY", "BLOCK_DISPLAY", "TEXT_DISPLAY", "INTERACTION"
        );
        static { register(
                "combat.attack_class_exempt_from_combat",
                ATTACK_CLASS_EXEMPT_FROM_COMBAT, stringListClass(),
                v -> ATTACK_CLASS_EXEMPT_FROM_COMBAT = v,
                Config::loadStringList
        ); }

        // Attack class timing configuration
        public static int ATTACK_CLASS_TIMING_ATTACK_DURATION = 750;
        static { register(
                "combat.attack_class_timing_attack_duration",
                ATTACK_CLASS_TIMING_ATTACK_DURATION, Integer.class,
                v -> ATTACK_CLASS_TIMING_ATTACK_DURATION = v,
                ConfigurationSection::getInt
        ); }

        public static int ATTACK_CLASS_TIMING_ATTACK_ITERATIONS = 5;
        static { register(
                "combat.attack_class_timing_attack_iterations",
                ATTACK_CLASS_TIMING_ATTACK_ITERATIONS, Integer.class,
                v -> ATTACK_CLASS_TIMING_ATTACK_ITERATIONS = v,
                ConfigurationSection::getInt
        ); }

        public static double ATTACK_CLASS_TIMING_ATTACK_START_VALUE = 0.0; // progress 0-1
        static { register(
                "combat.attack_class_timing_attack_start_value",
                ATTACK_CLASS_TIMING_ATTACK_START_VALUE, Double.class,
                v -> ATTACK_CLASS_TIMING_ATTACK_START_VALUE = v,
                ConfigurationSection::getDouble
        ); }

        public static double ATTACK_CLASS_TIMING_ATTACK_END_VALUE = 1.0; // progress 0-1
        static { register(
                "combat.attack_class_timing_attack_end_value",
                ATTACK_CLASS_TIMING_ATTACK_END_VALUE, Double.class,
                v -> ATTACK_CLASS_TIMING_ATTACK_END_VALUE = v,
                ConfigurationSection::getDouble
        ); }

        // Attack class modifiers configuration
        public static double ATTACK_CLASS_MODIFIERS_RANGE_MULTIPLIER = 2.0;
        static { register(
                "combat.attack_class_modifiers_range_multiplier",
                ATTACK_CLASS_MODIFIERS_RANGE_MULTIPLIER, Double.class,
                v -> ATTACK_CLASS_MODIFIERS_RANGE_MULTIPLIER = v,
                ConfigurationSection::getDouble
        ); }
    }
    //endregion

    // ==============================================================================
    //region TIMING - Cooldowns, durations, intervals
    // ==============================================================================
    /**
     * Timing configuration for cooldowns, durations, and update intervals.
     * <p>
     * Controls tick-based timing for thrown items, entity updates, combat cleanup,
     * and combo windows. All values in <b>ticks</b> (20 ticks = 1 second).
     * </p>
     *
     * <h2>Common Timing Patterns</h2>
     * <ul>
     *   <li><b>Grace Periods</b> - Short windows for forgiving input timing</li>
     *   <li><b>Disposal Timeouts</b> - Entity cleanup after inactivity</li>
     *   <li><b>Update Intervals</b> - Frequency of background tasks</li>
     * </ul>
     *
     * @see btm.sword.system.action.utility.thrown.ThrownItem Thrown item lifecycle
     */
    public static class Timing {
        // Thrown items configuration
        public static int THROWN_ITEMS_CATCH_GRACE_PERIOD = 3;
        static { register(
            "timing.thrown_items_catch_grace_period",
            THROWN_ITEMS_CATCH_GRACE_PERIOD, Integer.class,
            v -> THROWN_ITEMS_CATCH_GRACE_PERIOD = v,
            ConfigurationSection::getInt
        ); }

        public static int THROWN_ITEMS_DISPOSAL_TIMEOUT = 200;
        static { register(
            "timing.thrown_items_disposal_timeout",
            THROWN_ITEMS_DISPOSAL_TIMEOUT, Integer.class,
            v -> THROWN_ITEMS_DISPOSAL_TIMEOUT = v,
            ConfigurationSection::getInt
        ); }

        public static int THROWN_ITEMS_DISPOSAL_CHECK_INTERVAL = 10;
        static { register(
            "timing.thrown_items_disposal_check_interval",
            THROWN_ITEMS_DISPOSAL_CHECK_INTERVAL, Integer.class,
            v -> THROWN_ITEMS_DISPOSAL_CHECK_INTERVAL = v,
            ConfigurationSection::getInt
        ); }

        public static int THROWN_ITEMS_PIN_DELAY = 2;
        static { register(
            "timing.thrown_items_pin_delay",
            THROWN_ITEMS_PIN_DELAY, Integer.class,
            v -> THROWN_ITEMS_PIN_DELAY = v,
            ConfigurationSection::getInt
        ); }

        public static int THROWN_ITEMS_THROW_COMPLETION_DELAY = 6;
        static { register(
            "timing.thrown_items_throw_completion_delay",
            THROWN_ITEMS_THROW_COMPLETION_DELAY, Integer.class,
            v -> THROWN_ITEMS_THROW_COMPLETION_DELAY = v,
            ConfigurationSection::getInt
        ); }

        // Intervals configuration
        public static int INTERVALS_ENTITY_TICK = 1;
        static { register(
            "timing.intervals_entity_tick",
            INTERVALS_ENTITY_TICK, Integer.class,
            v -> INTERVALS_ENTITY_TICK = v,
            ConfigurationSection::getInt
        ); }

        public static int INTERVALS_STATUS_DISPLAY_UPDATE = 5;
        static { register(
            "timing.intervals_status_display_update",
            INTERVALS_STATUS_DISPLAY_UPDATE, Integer.class,
            v -> INTERVALS_STATUS_DISPLAY_UPDATE = v,
            ConfigurationSection::getInt
        ); }

        public static int INTERVALS_COMBAT_CLEANUP = 20;
        static { register(
            "timing.intervals_combat_cleanup",
            INTERVALS_COMBAT_CLEANUP, Integer.class,
            v -> INTERVALS_COMBAT_CLEANUP = v,
            ConfigurationSection::getInt
        ); }

        // Attacks configuration
        public static int ATTACKS_COMBO_WINDOW_BASE = 3;
        static { register(
            "timing.attacks_combo_window_base",
            ATTACKS_COMBO_WINDOW_BASE, Integer.class,
            v -> ATTACKS_COMBO_WINDOW_BASE = v,
            ConfigurationSection::getInt
        ); }
    }
    //endregion

    // ==============================================================================
    //region DISPLAY - Visual elements, particles, effects
    // ==============================================================================
    /**
     * Visual display configuration for particles, status indicators, and effects.
     * <p>
     * Controls particle effects, status display positioning, item display behavior,
     * and billboard modes. Distances in <b>blocks</b>, intervals in <b>ticks</b>,
     * brightness 0-15 (Minecraft light level).
     * </p>
     *
     * <h2>Key Subsystems</h2>
     * <ul>
     *   <li><b>Status Display</b> - Overhead health/stats text displays</li>
     *   <li><b>Item Display</b> - Floating item entities, billboard modes</li>
     *   <li><b>Particles</b> - Global particle toggles and density</li>
     * </ul>
     *
     * @see org.bukkit.entity.Display.Billboard Billboard rotation modes
     */
    public static class Display {
        public static int DEFAULT_TELEPORT_DURATION = 2;
        static { register(
            "display.default_teleport_duration",
            DEFAULT_TELEPORT_DURATION, Integer.class,
            v -> DEFAULT_TELEPORT_DURATION = v,
            ConfigurationSection::getInt
        ); }

        // Status display configuration
        public static boolean STATUS_DISPLAY_ENABLED = true;
        static { register(
            "display.status_display_enabled",
            STATUS_DISPLAY_ENABLED, Boolean.class,
            v -> STATUS_DISPLAY_ENABLED = v,
            ConfigurationSection::getBoolean
        ); }

        public static double STATUS_DISPLAY_HEIGHT_OFFSET = 2.0;
        static { register(
            "display.status_display_height_offset",
            STATUS_DISPLAY_HEIGHT_OFFSET, Double.class,
            v -> STATUS_DISPLAY_HEIGHT_OFFSET = v,
            ConfigurationSection::getDouble
        ); }

        public static int STATUS_DISPLAY_UPDATE_INTERVAL = 5;
        static { register(
            "display.status_display_update_interval",
            STATUS_DISPLAY_UPDATE_INTERVAL, Integer.class,
            v -> STATUS_DISPLAY_UPDATE_INTERVAL = v,
            ConfigurationSection::getInt
        ); }

        public static int STATUS_DISPLAY_BLOCK_BRIGHTNESS = 15; // 0-15 (light level)
        static { register(
            "display.status_display_block_brightness",
            STATUS_DISPLAY_BLOCK_BRIGHTNESS, Integer.class,
            v -> STATUS_DISPLAY_BLOCK_BRIGHTNESS = v,
            ConfigurationSection::getInt
        ); }

        public static int STATUS_DISPLAY_SKY_BRIGHTNESS = 15; // 0-15 (light level)
        static { register(
            "display.status_display_sky_brightness",
            STATUS_DISPLAY_SKY_BRIGHTNESS, Integer.class,
            v -> STATUS_DISPLAY_SKY_BRIGHTNESS = v,
            ConfigurationSection::getInt
        ); }

        // Item display follow configuration
        public static int ITEM_DISPLAY_FOLLOW_UPDATE_INTERVAL = 2;
        static { register(
            "display.item_display_follow_update_interval",
            ITEM_DISPLAY_FOLLOW_UPDATE_INTERVAL, Integer.class,
            v -> ITEM_DISPLAY_FOLLOW_UPDATE_INTERVAL = v,
            ConfigurationSection::getInt
        ); }

        public static int ITEM_DISPLAY_FOLLOW_PARTICLE_INTERVAL = 4;
        static { register(
            "display.item_display_follow_particle_interval",
            ITEM_DISPLAY_FOLLOW_PARTICLE_INTERVAL, Integer.class,
            v -> ITEM_DISPLAY_FOLLOW_PARTICLE_INTERVAL = v,
            ConfigurationSection::getInt
        ); }

        public static Billboard ITEM_DISPLAY_FOLLOW_BILLBOARD_MODE = Billboard.FIXED;
        static { register(
            "display.item_display_follow_billboard_mode",
            ITEM_DISPLAY_FOLLOW_BILLBOARD_MODE, Billboard.class,
            v -> ITEM_DISPLAY_FOLLOW_BILLBOARD_MODE = v,
            (s, p, d) -> loadEnum(s, p, d, Billboard.class)
        ); }

        // Particles configuration
        public static boolean PARTICLES_ENABLED = true;
        static { register(
            "display.particles_enabled",
            PARTICLES_ENABLED, Boolean.class,
            v -> PARTICLES_ENABLED = v,
            ConfigurationSection::getBoolean
        ); }

        public static int PARTICLES_DENSITY = 10;
        static { register(
            "display.particles_density",
            PARTICLES_DENSITY, Integer.class,
            v -> PARTICLES_DENSITY = v,
            ConfigurationSection::getInt
        ); }
    }
    //endregion

    // ==============================================================================
    //region DETECTION - Hitboxes, range detection, raytracing
    // ==============================================================================
    /**
     * Detection and collision configuration for raytracing and entity searches.
     * <p>
     * Controls ground checks, raytrace parameters, and entity detection radius.
     * All distance values in <b>blocks</b>. Used for collision detection, target
     * acquisition, and environmental queries.
     * </p>
     *
     * <h2>Detection Methods</h2>
     * <ul>
     *   <li><b>Ground Check</b> - Determines if entity is grounded (affects movement)</li>
     *   <li><b>Raytrace</b> - Line-of-sight checks for blocks and entities</li>
     *   <li><b>Entity Detection</b> - Radius-based entity searches</li>
     * </ul>
     */
    public static class Detection {
        // Ground check configuration
        public static double GROUND_CHECK_MAX_DISTANCE = 0.3;
        static { register(
            "detection.ground_check_max_distance",
            GROUND_CHECK_MAX_DISTANCE, Double.class,
            v -> GROUND_CHECK_MAX_DISTANCE = v,
            ConfigurationSection::getDouble
        ); }

        // Raytrace configuration
        public static double RAYTRACE_MAX_DISTANCE = 50.0;
        static { register(
            "detection.raytrace_max_distance",
            RAYTRACE_MAX_DISTANCE, Double.class,
            v -> RAYTRACE_MAX_DISTANCE = v,
            ConfigurationSection::getDouble
        ); }

        public static double RAYTRACE_STEP_SIZE = 0.1;
        static { register(
            "detection.raytrace_step_size",
            RAYTRACE_STEP_SIZE, Double.class,
            v -> RAYTRACE_STEP_SIZE = v,
            ConfigurationSection::getDouble
        ); }

        public static boolean RAYTRACE_IGNORE_PASSABLE_BLOCKS = true;
        static { register(
            "detection.raytrace_ignore_passable_blocks",
            RAYTRACE_IGNORE_PASSABLE_BLOCKS, Boolean.class,
            v -> RAYTRACE_IGNORE_PASSABLE_BLOCKS = v,
            ConfigurationSection::getBoolean
        ); }

        // Entity detection configuration
        public static double ENTITY_DETECTION_SEARCH_RADIUS = 10.0;
        static { register(
            "detection.entity_detection_search_radius",
            ENTITY_DETECTION_SEARCH_RADIUS, Double.class,
            v -> ENTITY_DETECTION_SEARCH_RADIUS = v,
            ConfigurationSection::getDouble
        ); }

        public static boolean ENTITY_DETECTION_INCLUDE_SPECTATORS = false;
        static { register(
            "detection.entity_detection_include_spectators",
            ENTITY_DETECTION_INCLUDE_SPECTATORS, Boolean.class,
            v -> ENTITY_DETECTION_INCLUDE_SPECTATORS = v,
            ConfigurationSection::getBoolean
        ); }
    }
    //endregion

    // ==============================================================================
    //region AUDIO - Sound effects and audio feedback
    // ==============================================================================
    public static class Audio {
        // Sounds configuration
        public static boolean SOUNDS_ENABLED = true;
        static { register(
            "audio.sounds_enabled",
            SOUNDS_ENABLED, Boolean.class,
            v -> SOUNDS_ENABLED = v,
            ConfigurationSection::getBoolean
        ); }

        public static float SOUNDS_GLOBAL_VOLUME = 1.0f; // 0.0-1.0
        static { register(
            "audio.sounds_global_volume",
            SOUNDS_GLOBAL_VOLUME, Float.class,
            v -> SOUNDS_GLOBAL_VOLUME = v,
            Config::loadFloat
        ); }

        public static float SOUNDS_GLOBAL_PITCH = 1.0f; // 0.5-2.0
        static { register(
            "audio.sounds_global_pitch",
            SOUNDS_GLOBAL_PITCH, Float.class,
            v -> SOUNDS_GLOBAL_PITCH = v,
            Config::loadFloat
        ); }

        // Throw sound configuration
        public static SoundType THROW_SOUND = SoundType.ENTITY_ENDER_DRAGON_FLAP;
        static { register(
            "audio.throw_sound",
            THROW_SOUND, SoundType.class,
            v -> THROW_SOUND = v,
            Config::loadSoundType
        ); }

        public static float THROW_VOLUME = 0.35f; // 0.0-1.0
        static { register(
            "audio.throw_volume",
            THROW_VOLUME, Float.class,
            v -> THROW_VOLUME = v,
            Config::loadFloat
        ); }

        public static float THROW_PITCH = 0.4f; // 0.5-2.0
        static { register(
            "audio.throw_pitch",
            THROW_PITCH, Float.class,
            v -> THROW_PITCH = v,
            Config::loadFloat
        ); }

        // Attack sound configuration
        public static SoundType ATTACK_SOUND = SoundType.ITEM_TRIDENT_THROW;
        static { register(
            "audio.attack_sound",
            ATTACK_SOUND, SoundType.class,
            v -> ATTACK_SOUND = v,
            Config::loadSoundType
        ); }

        public static float ATTACK_VOLUME = 0.055f; // 0.0-1.0
        static { register(
            "audio.attack_volume",
            ATTACK_VOLUME, Float.class,
            v -> ATTACK_VOLUME = v,
            Config::loadFloat
        ); }

        public static float ATTACK_PITCH = 1.5f; // 0.5-2.0
        static { register(
            "audio.attack_pitch",
            ATTACK_PITCH, Float.class,
            v -> ATTACK_PITCH = v,
            Config::loadFloat
        ); }
    }
    //endregion

    // ==============================================================================
    //region ENTITY - Entity stats, health, aspects
    // ==============================================================================
    /**
     * Entity attribute configuration for players, hostiles, and combat profiles.
     * <p>
     * Defines base stats (health/toughness/soulfire), regeneration rates, and
     * hostile entity multipliers. Health in <b>HP</b> (1 heart = 2 HP), time in
     * <b>ticks</b> (20 ticks/second).
     * </p>
     *
     * <h2>Combat Profile Aspects</h2>
     * <ul>
     *   <li><b>Shards</b> - Defensive resource depleted by attacks</li>
     *   <li><b>Toughness</b> - Damage reduction layer</li>
     *   <li><b>Soulfire</b> - Special ability resource</li>
     *   <li><b>Form</b> - Combat stance/technique points</li>
     * </ul>
     *
     * @see btm.sword.system.entity.base.CombatProfile Combat stat management
     * @see btm.sword.system.entity.base.SwordEntity Entity wrapper
     */
    public static class Entity {
        // Player configuration
        public static double PLAYER_BASE_HEALTH = 100.0; // HP (1 heart = 2 HP)
        static { register(
            "entity.player_base_health",
            PLAYER_BASE_HEALTH, Double.class,
            v -> PLAYER_BASE_HEALTH = v,
            ConfigurationSection::getDouble
        ); }

        public static double PLAYER_BASE_TOUGHNESS = 20.0; // HP
        static { register(
            "entity.player_base_toughness",
            PLAYER_BASE_TOUGHNESS, Double.class,
            v -> PLAYER_BASE_TOUGHNESS = v,
            ConfigurationSection::getDouble
        ); }

        public static double PLAYER_BASE_SOULFIRE = 100.0; // points
        static { register(
            "entity.player_base_soulfire",
            PLAYER_BASE_SOULFIRE, Double.class,
            v -> PLAYER_BASE_SOULFIRE = v,
            ConfigurationSection::getDouble
        ); }

        // Hostile configuration
        public static double HOSTILE_HEALTH_MULTIPLIER = 1.0;
        static { register(
            "entity.hostile_health_multiplier",
            HOSTILE_HEALTH_MULTIPLIER, Double.class,
            v -> HOSTILE_HEALTH_MULTIPLIER = v,
            ConfigurationSection::getDouble
        ); }

        public static double HOSTILE_DAMAGE_MULTIPLIER = 1.0;
        static { register(
            "entity.hostile_damage_multiplier",
            HOSTILE_DAMAGE_MULTIPLIER, Double.class,
            v -> HOSTILE_DAMAGE_MULTIPLIER = v,
            ConfigurationSection::getDouble
        ); }

        // Combat profile configuration
        public static int COMBAT_PROFILE_MAX_AIR_DODGES = 1;
        static { register(
            "entity.combat_profile_max_air_dodges",
            COMBAT_PROFILE_MAX_AIR_DODGES, Integer.class,
            v -> COMBAT_PROFILE_MAX_AIR_DODGES = v,
            ConfigurationSection::getInt
        ); }

        // Combat profile shards configuration
        public static float COMBAT_PROFILE_SHARDS_CURRENT = 10.0f;
        static { register(
            "entity.combat_profile_shards_current",
            COMBAT_PROFILE_SHARDS_CURRENT, Float.class,
            v -> COMBAT_PROFILE_SHARDS_CURRENT = v,
            Config::loadFloat
        ); }

        public static int COMBAT_PROFILE_SHARDS_REGEN_PERIOD = 50;
        static { register(
            "entity.combat_profile_shards_regen_period",
            COMBAT_PROFILE_SHARDS_REGEN_PERIOD, Integer.class,
            v -> COMBAT_PROFILE_SHARDS_REGEN_PERIOD = v,
            ConfigurationSection::getInt
        ); }

        public static float COMBAT_PROFILE_SHARDS_REGEN_AMOUNT = 1.0f;
        static { register(
            "entity.combat_profile_shards_regen_amount",
            COMBAT_PROFILE_SHARDS_REGEN_AMOUNT, Float.class,
            v -> COMBAT_PROFILE_SHARDS_REGEN_AMOUNT = v,
            Config::loadFloat
        ); }

        // Combat profile toughness configuration
        public static float COMBAT_PROFILE_TOUGHNESS_CURRENT = 20.0f;
        static { register(
            "entity.combat_profile_toughness_current",
            COMBAT_PROFILE_TOUGHNESS_CURRENT, Float.class,
            v -> COMBAT_PROFILE_TOUGHNESS_CURRENT = v,
            Config::loadFloat
        ); }

        public static int COMBAT_PROFILE_TOUGHNESS_REGEN_PERIOD = 20;
        static { register(
            "entity.combat_profile_toughness_regen_period",
            COMBAT_PROFILE_TOUGHNESS_REGEN_PERIOD, Integer.class,
            v -> COMBAT_PROFILE_TOUGHNESS_REGEN_PERIOD = v,
            ConfigurationSection::getInt
        ); }

        public static float COMBAT_PROFILE_TOUGHNESS_REGEN_AMOUNT = 0.5f;
        static { register(
            "entity.combat_profile_toughness_regen_amount",
            COMBAT_PROFILE_TOUGHNESS_REGEN_AMOUNT, Float.class,
            v -> COMBAT_PROFILE_TOUGHNESS_REGEN_AMOUNT = v,
            Config::loadFloat
        ); }

        // Combat profile soulfire configuration
        public static float COMBAT_PROFILE_SOULFIRE_CURRENT = 100.0f;
        static { register(
            "entity.combat_profile_soulfire_current",
            COMBAT_PROFILE_SOULFIRE_CURRENT, Float.class,
            v -> COMBAT_PROFILE_SOULFIRE_CURRENT = v,
            Config::loadFloat
        ); }

        public static int COMBAT_PROFILE_SOULFIRE_REGEN_PERIOD = 5;
        static { register(
            "entity.combat_profile_soulfire_regen_period",
            COMBAT_PROFILE_SOULFIRE_REGEN_PERIOD, Integer.class,
            v -> COMBAT_PROFILE_SOULFIRE_REGEN_PERIOD = v,
            ConfigurationSection::getInt
        ); }

        public static float COMBAT_PROFILE_SOULFIRE_REGEN_AMOUNT = 0.2f;
        static { register(
            "entity.combat_profile_soulfire_regen_amount",
            COMBAT_PROFILE_SOULFIRE_REGEN_AMOUNT, Float.class,
            v -> COMBAT_PROFILE_SOULFIRE_REGEN_AMOUNT = v,
            Config::loadFloat
        ); }

        // Combat profile form configuration
        public static float COMBAT_PROFILE_FORM_CURRENT = 10.0f;
        static { register(
            "entity.combat_profile_form_current",
            COMBAT_PROFILE_FORM_CURRENT, Float.class,
            v -> COMBAT_PROFILE_FORM_CURRENT = v,
            Config::loadFloat
        ); }

        public static int COMBAT_PROFILE_FORM_REGEN_PERIOD = 60;
        static { register(
            "entity.combat_profile_form_regen_period",
            COMBAT_PROFILE_FORM_REGEN_PERIOD, Integer.class,
            v -> COMBAT_PROFILE_FORM_REGEN_PERIOD = v,
            ConfigurationSection::getInt
        ); }

        public static float COMBAT_PROFILE_FORM_REGEN_AMOUNT = 1.0f;
        static { register(
            "entity.combat_profile_form_regen_amount",
            COMBAT_PROFILE_FORM_REGEN_AMOUNT, Float.class,
            v -> COMBAT_PROFILE_FORM_REGEN_AMOUNT = v,
            Config::loadFloat
        ); }
    }
    //endregion

    // ==============================================================================
    //region MOVEMENT - Dash, grab, mobility abilities
    // ==============================================================================
    /**
     * Movement ability configuration for dash, toss, and grab mechanics.
     * <p>
     * Controls player mobility abilities including directional dash (teleport),
     * sword toss (projectile), and entity grab (pull). Distances in <b>blocks</b>,
     * time in <b>ticks</b>, velocities in <b>blocks/tick</b>.
     * </p>
     *
     * <h2>Key Abilities</h2>
     * <ul>
     *   <li><b>Dash</b> - Directional teleport with collision detection, particle trail, grab on contact</li>
     *   <li><b>Toss</b> - Throw sword in arc trajectory with explosion on impact</li>
     *   <li><b>Grab</b> - Pull nearby entities toward player</li>
     * </ul>
     *
     * @see btm.sword.system.action.MovementAction Movement ability implementation
     * @see btm.sword.system.action.utility.thrown.ThrownItem Toss projectile physics
     */
    public static class Movement {
        // Dash configuration
        public static double DASH_MAX_DISTANCE = 10.0;
        static { register(
            "movement.dash_max_distance",
            DASH_MAX_DISTANCE, Double.class,
            v -> DASH_MAX_DISTANCE = v,
            ConfigurationSection::getDouble
        ); }

        public static int DASH_CAST_DURATION = 5;
        static { register(
            "movement.dash_cast_duration",
            DASH_CAST_DURATION, Integer.class,
            v -> DASH_CAST_DURATION = v,
            ConfigurationSection::getInt
        ); }

        public static double DASH_BASE_POWER = 0.7;
        static { register(
            "movement.dash_base_power",
            DASH_BASE_POWER, Double.class,
            v -> DASH_BASE_POWER = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_INITIAL_OFFSET_Y = 0.3;
        static { register(
            "movement.dash_initial_offset_y",
            DASH_INITIAL_OFFSET_Y, Double.class,
            v -> DASH_INITIAL_OFFSET_Y = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_IMPEDANCE_CHECK_OFFSET_Y = 0.5;
        static { register(
            "movement.dash_impedance_check_offset_y",
            DASH_IMPEDANCE_CHECK_OFFSET_Y, Double.class,
            v -> DASH_IMPEDANCE_CHECK_OFFSET_Y = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_FORWARD_MULTIPLIER = 0.5;
        static { register(
            "movement.dash_forward_multiplier",
            DASH_FORWARD_MULTIPLIER, Double.class,
            v -> DASH_FORWARD_MULTIPLIER = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_UPWARD_MULTIPLIER = 0.15;
        static { register(
            "movement.dash_upward_multiplier",
            DASH_UPWARD_MULTIPLIER, Double.class,
            v -> DASH_UPWARD_MULTIPLIER = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_UPWARD_BOOST = 0.05;
        static { register(
            "movement.dash_upward_boost",
            DASH_UPWARD_BOOST, Double.class,
            v -> DASH_UPWARD_BOOST = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_RAY_HITBOX_RADIUS = 0.7;
        static { register(
            "movement.dash_ray_hitbox_radius",
            DASH_RAY_HITBOX_RADIUS, Double.class,
            v -> DASH_RAY_HITBOX_RADIUS = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_SECANT_RADIUS = 0.3;
        static { register(
            "movement.dash_secant_radius",
            DASH_SECANT_RADIUS, Double.class,
            v -> DASH_SECANT_RADIUS = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_GRAB_DISTANCE_SQUARED = 8.5; // blocks² (sqrt(8.5) ≈ 2.9 blocks)
        static { register(
            "movement.dash_grab_distance_squared",
            DASH_GRAB_DISTANCE_SQUARED, Double.class,
            v -> DASH_GRAB_DISTANCE_SQUARED = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_BLOCK_CHECK_OFFSET_Y = -0.75;
        static { register(
            "movement.dash_block_check_offset_y",
            DASH_BLOCK_CHECK_OFFSET_Y, Double.class,
            v -> DASH_BLOCK_CHECK_OFFSET_Y = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_VELOCITY_DAMPING = 0.6; // multiplier (0-1)
        static { register(
            "movement.dash_velocity_damping",
            DASH_VELOCITY_DAMPING, Double.class,
            v -> DASH_VELOCITY_DAMPING = v,
            ConfigurationSection::getDouble
        ); }

        public static long DASH_PARTICLE_TASK_DELAY = 0L;
        static { register(
            "movement.dash_particle_task_delay",
            DASH_PARTICLE_TASK_DELAY, Long.class,
            v -> DASH_PARTICLE_TASK_DELAY = v,
            ConfigurationSection::getLong
        ); }

        public static long DASH_PARTICLE_TASK_PERIOD = 2L;
        static { register(
            "movement.dash_particle_task_period",
            DASH_PARTICLE_TASK_PERIOD, Long.class,
            v -> DASH_PARTICLE_TASK_PERIOD = v,
            ConfigurationSection::getLong
        ); }

        public static int DASH_PARTICLE_TIMER_INCREMENT = 2;
        static { register(
            "movement.dash_particle_timer_increment",
            DASH_PARTICLE_TIMER_INCREMENT, Integer.class,
            v -> DASH_PARTICLE_TIMER_INCREMENT = v,
            ConfigurationSection::getInt
        ); }

        public static int DASH_PARTICLE_TIMER_THRESHOLD = 4;
        static { register(
            "movement.dash_particle_timer_threshold",
            DASH_PARTICLE_TIMER_THRESHOLD, Integer.class,
            v -> DASH_PARTICLE_TIMER_THRESHOLD = v,
            ConfigurationSection::getInt
        ); }

        public static long DASH_GRAB_CHECK_DELAY = 4L;
        static { register(
            "movement.dash_grab_check_delay",
            DASH_GRAB_CHECK_DELAY, Long.class,
            v -> DASH_GRAB_CHECK_DELAY = v,
            ConfigurationSection::getLong
        ); }

        public static long DASH_VELOCITY_TASK_DELAY = 0L;
        static { register(
            "movement.dash_velocity_task_delay",
            DASH_VELOCITY_TASK_DELAY, Long.class,
            v -> DASH_VELOCITY_TASK_DELAY = v,
            ConfigurationSection::getLong
        ); }

        public static long DASH_VELOCITY_TASK_PERIOD = 1L;
        static { register(
            "movement.dash_velocity_task_period",
            DASH_VELOCITY_TASK_PERIOD, Long.class,
            v -> DASH_VELOCITY_TASK_PERIOD = v,
            ConfigurationSection::getLong
        ); }

        public static int DASH_PARTICLE_COUNT = 100;
        static { register(
            "movement.dash_particle_count",
            DASH_PARTICLE_COUNT, Integer.class,
            v -> DASH_PARTICLE_COUNT = v,
            ConfigurationSection::getInt
        ); }

        public static double DASH_PARTICLE_SPREAD_X = 1.25;
        static { register(
            "movement.dash_particle_spread_x",
            DASH_PARTICLE_SPREAD_X, Double.class,
            v -> DASH_PARTICLE_SPREAD_X = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_PARTICLE_SPREAD_Y = 1.25;
        static { register(
            "movement.dash_particle_spread_y",
            DASH_PARTICLE_SPREAD_Y, Double.class,
            v -> DASH_PARTICLE_SPREAD_Y = v,
            ConfigurationSection::getDouble
        ); }

        public static double DASH_PARTICLE_SPREAD_Z = 1.25;
        static { register(
            "movement.dash_particle_spread_z",
            DASH_PARTICLE_SPREAD_Z, Double.class,
            v -> DASH_PARTICLE_SPREAD_Z = v,
            ConfigurationSection::getDouble
        ); }

        public static float DASH_FLAP_SOUND_VOLUME = 0.6f; // 0.0-1.0
        static { register(
            "movement.dash_flap_sound_volume",
            DASH_FLAP_SOUND_VOLUME, Float.class,
            v -> DASH_FLAP_SOUND_VOLUME = v,
            Config::loadFloat
        ); }

        public static float DASH_FLAP_SOUND_PITCH = 1.0f; // 0.5-2.0
        static { register(
            "movement.dash_flap_sound_pitch",
            DASH_FLAP_SOUND_PITCH, Float.class,
            v -> DASH_FLAP_SOUND_PITCH = v,
            Config::loadFloat
        ); }

        public static float DASH_SWEEP_SOUND_VOLUME = 0.3f; // 0.0-1.0
        static { register(
            "movement.dash_sweep_sound_volume",
            DASH_SWEEP_SOUND_VOLUME, Float.class,
            v -> DASH_SWEEP_SOUND_VOLUME = v,
            Config::loadFloat
        ); }

        public static float DASH_SWEEP_SOUND_PITCH = 0.6f; // 0.5-2.0
        static { register(
            "movement.dash_sweep_sound_pitch",
            DASH_SWEEP_SOUND_PITCH, Float.class,
            v -> DASH_SWEEP_SOUND_PITCH = v,
            Config::loadFloat
        ); }

        // Toss configuration
        public static double TOSS_BASE_FORCE = 1.5; // blocks/tick
        static { register(
            "movement.toss_base_force",
            TOSS_BASE_FORCE, Double.class,
            v -> TOSS_BASE_FORCE = v,
            ConfigurationSection::getDouble
        ); }

        public static double TOSS_MIGHT_MULTIPLIER_BASE = 2.5; // multiplier
        static { register(
            "movement.toss_might_multiplier_base",
            TOSS_MIGHT_MULTIPLIER_BASE, Double.class,
            v -> TOSS_MIGHT_MULTIPLIER_BASE = v,
            ConfigurationSection::getDouble
        ); }

        public static double TOSS_MIGHT_MULTIPLIER_INCREMENT = 0.1; // per level
        static { register(
            "movement.toss_might_multiplier_increment",
            TOSS_MIGHT_MULTIPLIER_INCREMENT, Double.class,
            v -> TOSS_MIGHT_MULTIPLIER_INCREMENT = v,
            ConfigurationSection::getDouble
        ); }

        public static int TOSS_UPWARD_PHASE_ITERATIONS = 2;
        static { register(
            "movement.toss_upward_phase_iterations",
            TOSS_UPWARD_PHASE_ITERATIONS, Integer.class,
            v -> TOSS_UPWARD_PHASE_ITERATIONS = v,
            ConfigurationSection::getInt
        ); }

        public static double TOSS_UPWARD_VELOCITY_Y = 0.25; // blocks/tick
        static { register(
            "movement.toss_upward_velocity_y",
            TOSS_UPWARD_VELOCITY_Y, Double.class,
            v -> TOSS_UPWARD_VELOCITY_Y = v,
            ConfigurationSection::getDouble
        ); }

        public static int TOSS_FORWARD_PHASE_ITERATIONS = 3;
        static { register(
            "movement.toss_forward_phase_iterations",
            TOSS_FORWARD_PHASE_ITERATIONS, Integer.class,
            v -> TOSS_FORWARD_PHASE_ITERATIONS = v,
            ConfigurationSection::getInt
        ); }

        public static int TOSS_ANIMATION_ITERATIONS = 15;
        static { register(
            "movement.toss_animation_iterations",
            TOSS_ANIMATION_ITERATIONS, Integer.class,
            v -> TOSS_ANIMATION_ITERATIONS = v,
            ConfigurationSection::getInt
        ); }

        public static double TOSS_LOCATION_OFFSET_MULTIPLIER = 0.3;
        static { register(
            "movement.toss_location_offset_multiplier",
            TOSS_LOCATION_OFFSET_MULTIPLIER, Double.class,
            v -> TOSS_LOCATION_OFFSET_MULTIPLIER = v,
            ConfigurationSection::getDouble
        ); }

        public static double TOSS_PARTICLE_HEIGHT_MULTIPLIER = 0.5;
        static { register(
            "movement.toss_particle_height_multiplier",
            TOSS_PARTICLE_HEIGHT_MULTIPLIER, Double.class,
            v -> TOSS_PARTICLE_HEIGHT_MULTIPLIER = v,
            ConfigurationSection::getDouble
        ); }

        public static double TOSS_RAY_TRACE_DISTANCE_MULTIPLIER = 0.6;
        static { register(
            "movement.toss_ray_trace_distance_multiplier",
            TOSS_RAY_TRACE_DISTANCE_MULTIPLIER, Double.class,
            v -> TOSS_RAY_TRACE_DISTANCE_MULTIPLIER = v,
            ConfigurationSection::getDouble
        ); }

        public static double TOSS_ENTITY_DETECTION_RADIUS = 0.4;
        static { register(
            "movement.toss_entity_detection_radius",
            TOSS_ENTITY_DETECTION_RADIUS, Double.class,
            v -> TOSS_ENTITY_DETECTION_RADIUS = v,
            ConfigurationSection::getDouble
        ); }

        public static double TOSS_KNOCKBACK_MULTIPLIER = 0.3;
        static { register(
            "movement.toss_knockback_multiplier",
            TOSS_KNOCKBACK_MULTIPLIER, Double.class,
            v -> TOSS_KNOCKBACK_MULTIPLIER = v,
            ConfigurationSection::getDouble
        ); }

        public static float TOSS_EXPLOSION_POWER = 2.0f;
        static { register(
            "movement.toss_explosion_power",
            TOSS_EXPLOSION_POWER, Float.class,
            v -> TOSS_EXPLOSION_POWER = v,
            Config::loadFloat
        ); }

        public static int TOSS_HIT_INVULNERABILITY_TICKS = 3;
        static { register(
            "movement.toss_hit_invulnerability_ticks",
            TOSS_HIT_INVULNERABILITY_TICKS, Integer.class,
            v -> TOSS_HIT_INVULNERABILITY_TICKS = v,
            ConfigurationSection::getInt
        ); }

        public static int TOSS_HIT_SHARD_DAMAGE = 2;
        static { register(
            "movement.toss_hit_shard_damage",
            TOSS_HIT_SHARD_DAMAGE, Integer.class,
            v -> TOSS_HIT_SHARD_DAMAGE = v,
            ConfigurationSection::getInt); }

        public static float TOSS_HIT_TOUGHNESS_DAMAGE = 30.0f;
        static { register(
            "movement.toss_hit_toughness_damage",
            TOSS_HIT_TOUGHNESS_DAMAGE, Float.class,
            v -> TOSS_HIT_TOUGHNESS_DAMAGE = v,
            Config::loadFloat); }

        public static float TOSS_HIT_SOULFIRE_REDUCTION = 5.0f;
        static { register(
            "movement.toss_hit_soulfire_reduction",
            TOSS_HIT_SOULFIRE_REDUCTION, Float.class,
            v -> TOSS_HIT_SOULFIRE_REDUCTION = v,
            Config::loadFloat); }

        // Grab configuration
        public static double GRAB_PULL_STRENGTH = 0.8; // blocks/tick
        static { register(
            "movement.grab_pull_strength",
            GRAB_PULL_STRENGTH, Double.class,
            v -> GRAB_PULL_STRENGTH = v,
            ConfigurationSection::getDouble); }

        public static double GRAB_MAX_RANGE = 3.0;
        static { register(
            "movement.grab_max_range",
            GRAB_MAX_RANGE, Double.class,
            v -> GRAB_MAX_RANGE = v,
            ConfigurationSection::getDouble); }

        public static int GRAB_HOLD_DURATION = 40;
        static { register(
            "movement.grab_hold_duration",
            GRAB_HOLD_DURATION, Integer.class,
            v -> GRAB_HOLD_DURATION = v,
            ConfigurationSection::getInt); }
    }
    //endregion

    // ==============================================================================
    //region WORLD - World interaction, block breaking, environment
    // ==============================================================================
    /**
     * World interaction configuration for block breaking and explosions.
     * <p>
     * Controls whether combat actions can modify the environment, including block
     * breaking permissions and explosion behavior (fire/block damage).
     * </p>
     *
     * <h2>Protection Integration</h2>
     * <ul>
     *   <li><b>WorldGuard</b> - Respects region protection flags when enabled</li>
     *   <li><b>Block Breaking</b> - Master toggle for all block modifications</li>
     *   <li><b>Explosions</b> - Separate controls for fire and block damage</li>
     * </ul>
     */
    public static class World {
        // Block interaction configuration
        public static boolean BLOCK_INTERACTION_ALLOW_BLOCK_BREAKING = false;
        static { register(
            "world.block_interaction_allow_block_breaking",
            BLOCK_INTERACTION_ALLOW_BLOCK_BREAKING, Boolean.class,
            v -> BLOCK_INTERACTION_ALLOW_BLOCK_BREAKING = v,
            ConfigurationSection::getBoolean); }

        public static boolean BLOCK_INTERACTION_RESPECT_WORLD_GUARD = true;
        static { register(
            "world.block_interaction_respect_world_guard",
            BLOCK_INTERACTION_RESPECT_WORLD_GUARD, Boolean.class,
            v -> BLOCK_INTERACTION_RESPECT_WORLD_GUARD = v,
            ConfigurationSection::getBoolean); }

        // Explosions configuration
        public static boolean EXPLOSIONS_SET_FIRE = false;
        static { register(
            "world.explosions_set_fire",
            EXPLOSIONS_SET_FIRE, Boolean.class,
            v -> EXPLOSIONS_SET_FIRE = v,
            ConfigurationSection::getBoolean); }

        public static boolean EXPLOSIONS_BREAK_BLOCKS = false;
        static { register(
            "world.explosions_break_blocks",
            EXPLOSIONS_BREAK_BLOCKS, Boolean.class,
            v -> EXPLOSIONS_BREAK_BLOCKS = v,
            ConfigurationSection::getBoolean); }
    }
    //endregion

    // ==============================================================================
    //region DEBUG - Development and testing options
    // ==============================================================================
    /**
     * Debug and development configuration for logging and visualization.
     * <p>
     * Enables verbose logging and visual debugging tools. All options default to
     * {@code false} for production. Enable selectively for development/troubleshooting.
     * </p>
     *
     * <h2>Debug Tools</h2>
     * <ul>
     *   <li><b>Verbose Logging</b> - Detailed console output for combat/movement/config</li>
     *   <li><b>Visualization</b> - Particle-based hitbox and raytrace rendering</li>
     * </ul>
     *
     * <p><b>Warning:</b> Visualization features generate many particles and may impact performance.</p>
     */
    public static class Debug {
        // Logging configuration
        public static boolean LOGGING_VERBOSE_COMBAT = false;
        static { register(
            "debug.logging_verbose_combat",
            LOGGING_VERBOSE_COMBAT, Boolean.class,
            v -> LOGGING_VERBOSE_COMBAT = v,
            ConfigurationSection::getBoolean); }

        public static boolean LOGGING_VERBOSE_MOVEMENT = false;
        static { register(
            "debug.logging_verbose_movement",
            LOGGING_VERBOSE_MOVEMENT, Boolean.class,
            v -> LOGGING_VERBOSE_MOVEMENT = v,
            ConfigurationSection::getBoolean); }

        public static boolean LOGGING_VERBOSE_CONFIG = false;
        static { register(
            "debug.logging_verbose_config",
            LOGGING_VERBOSE_CONFIG, Boolean.class
            , v -> LOGGING_VERBOSE_CONFIG = v,
            ConfigurationSection::getBoolean); }

        // Visualization configuration
        public static boolean VISUALIZATION_SHOW_HITBOXES = false;
        static { register(
            "debug.visualization_show_hitboxes",
            VISUALIZATION_SHOW_HITBOXES, Boolean.class,
            v -> VISUALIZATION_SHOW_HITBOXES = v,
            ConfigurationSection::getBoolean); }

        public static boolean VISUALIZATION_SHOW_RAYTRACES = false;
        static { register(
            "debug.visualization_show_raytraces",
            VISUALIZATION_SHOW_RAYTRACES, Boolean.class,
            v -> VISUALIZATION_SHOW_RAYTRACES = v,
            ConfigurationSection::getBoolean); }
    }
    //endregion
}
