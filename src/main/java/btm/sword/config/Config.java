package btm.sword.config;

import java.util.List;

import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

/**
 * Static configuration class for Sword: Combat Evolved.
 * <p>
 * Provides centralized, type-safe access to all configuration values.
 * Values are loaded from config.yaml by {@link ConfigManager} and can be
 * hot-reloaded at runtime using /sword reload.
 * </p>
 * <p>
 * All setters are package-private, only accessible from ConfigManager.
 * Reference types (Vector) return clones to prevent external mutation.
 * </p>
 */
public class Config {

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
        static void setUMBRAL_BLADE_IDLE_PERIOD(float value) {
            UMBRAL_BLADE_IDLE_PERIOD = value;
        }
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
    // PHYSICS - Projectile motion, gravity, and velocity
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
        static void setTHROWN_ITEMS_GRAVITY_DAMPER(double value) { THROWN_ITEMS_GRAVITY_DAMPER = value; }

        public static double THROWN_ITEMS_TRAJECTORY_ROTATION = 0.03696; // radians/tick
        static void setTHROWN_ITEMS_TRAJECTORY_ROTATION(double value) { THROWN_ITEMS_TRAJECTORY_ROTATION = value; }

        public static float THROWN_ITEMS_DISPLAY_OFFSET_X = -0.5f;
        static void setTHROWN_ITEMS_DISPLAY_OFFSET_X(float value) { THROWN_ITEMS_DISPLAY_OFFSET_X = value; }

        public static float THROWN_ITEMS_DISPLAY_OFFSET_Y = 0.1f;
        static void setTHROWN_ITEMS_DISPLAY_OFFSET_Y(float value) { THROWN_ITEMS_DISPLAY_OFFSET_Y = value; }

        public static float THROWN_ITEMS_DISPLAY_OFFSET_Z = 0.5f;
        static void setTHROWN_ITEMS_DISPLAY_OFFSET_Z(float value) { THROWN_ITEMS_DISPLAY_OFFSET_Z = value; }

        public static double THROWN_ITEMS_ORIGIN_OFFSET_FORWARD = 0.5;
        static void setTHROWN_ITEMS_ORIGIN_OFFSET_FORWARD(double value) { THROWN_ITEMS_ORIGIN_OFFSET_FORWARD = value; }

        public static double THROWN_ITEMS_ORIGIN_OFFSET_UP = 0.1;
        static void setTHROWN_ITEMS_ORIGIN_OFFSET_UP(double value) { THROWN_ITEMS_ORIGIN_OFFSET_UP = value; }

        public static double THROWN_ITEMS_ORIGIN_OFFSET_BACK = -0.25;
        static void setTHROWN_ITEMS_ORIGIN_OFFSET_BACK(double value) { THROWN_ITEMS_ORIGIN_OFFSET_BACK = value; }

        // Thrown items rotation speed configuration
        public static double THROWN_ITEMS_ROTATION_SPEED_SWORD = 0.0; // radians/tick
        static void setTHROWN_ITEMS_ROTATION_SPEED_SWORD(double value) { THROWN_ITEMS_ROTATION_SPEED_SWORD = value; }

        public static double THROWN_ITEMS_ROTATION_SPEED_AXE = -Math.PI / 8; // radians/tick
        static void setTHROWN_ITEMS_ROTATION_SPEED_AXE(double value) { THROWN_ITEMS_ROTATION_SPEED_AXE = value; }

        public static double THROWN_ITEMS_ROTATION_SPEED_HOE = -Math.PI / 8; // radians/tick
        static void setTHROWN_ITEMS_ROTATION_SPEED_HOE(double value) { THROWN_ITEMS_ROTATION_SPEED_HOE = value; }

        public static double THROWN_ITEMS_ROTATION_SPEED_PICKAXE = -Math.PI / 8; // radians/tick
        static void setTHROWN_ITEMS_ROTATION_SPEED_PICKAXE(double value) { THROWN_ITEMS_ROTATION_SPEED_PICKAXE = value; }

        public static double THROWN_ITEMS_ROTATION_SPEED_SHOVEL = -Math.PI / 8; // radians/tick
        static void setTHROWN_ITEMS_ROTATION_SPEED_SHOVEL(double value) { THROWN_ITEMS_ROTATION_SPEED_SHOVEL = value; }

        public static double THROWN_ITEMS_ROTATION_SPEED_SHIELD = -Math.PI / 8; // radians/tick
        static void setTHROWN_ITEMS_ROTATION_SPEED_SHIELD(double value) { THROWN_ITEMS_ROTATION_SPEED_SHIELD = value; }

        public static double THROWN_ITEMS_ROTATION_SPEED_DEFAULT_SPEED = Math.PI / 32; // radians/tick
        static void setTHROWN_ITEMS_ROTATION_SPEED_DEFAULT_SPEED(double value) { THROWN_ITEMS_ROTATION_SPEED_DEFAULT_SPEED = value; }

        // Attack velocity configuration
        public static double ATTACK_VELOCITY_GROUNDED_DAMPING_HORIZONTAL = 0.3; // multiplier (0-1)
        static void setATTACK_VELOCITY_GROUNDED_DAMPING_HORIZONTAL(double value) { ATTACK_VELOCITY_GROUNDED_DAMPING_HORIZONTAL = value; }

        public static double ATTACK_VELOCITY_GROUNDED_DAMPING_VERTICAL = 0.4; // multiplier (0-1)
        static void setATTACK_VELOCITY_GROUNDED_DAMPING_VERTICAL(double value) { ATTACK_VELOCITY_GROUNDED_DAMPING_VERTICAL = value; }

        public static double ATTACK_VELOCITY_KNOCKBACK_VERTICAL_BASE = 0.25; // blocks/tick
        static void setATTACK_VELOCITY_KNOCKBACK_VERTICAL_BASE(double value) { ATTACK_VELOCITY_KNOCKBACK_VERTICAL_BASE = value; }

        public static double ATTACK_VELOCITY_KNOCKBACK_HORIZONTAL_MODIFIER = 0.1; // multiplier
        static void setATTACK_VELOCITY_KNOCKBACK_HORIZONTAL_MODIFIER(double value) { ATTACK_VELOCITY_KNOCKBACK_HORIZONTAL_MODIFIER = value; }

        public static double ATTACK_VELOCITY_KNOCKBACK_NORMAL_MULTIPLIER = 0.7; // multiplier
        static void setATTACK_VELOCITY_KNOCKBACK_NORMAL_MULTIPLIER(double value) { ATTACK_VELOCITY_KNOCKBACK_NORMAL_MULTIPLIER = value; }
    }

    // ==============================================================================
    // COMBAT - Damage, hitboxes, attack patterns, combat mechanics
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
        static void setATTACKS_BASE_DAMAGE(double value) { ATTACKS_BASE_DAMAGE = value; }

        public static double ATTACKS_DOWN_AIR_THRESHOLD = -0.85; // dot product (-1 to 1)
        static void setATTACKS_DOWN_AIR_THRESHOLD(double value) { ATTACKS_DOWN_AIR_THRESHOLD = value; }

        public static long ATTACKS_CAST_TIMING_MIN_DURATION = 1L;
        static void setATTACKS_CAST_TIMING_MIN_DURATION(long value) { ATTACKS_CAST_TIMING_MIN_DURATION = value; }

        public static long ATTACKS_CAST_TIMING_MAX_DURATION = 3L;
        static void setATTACKS_CAST_TIMING_MAX_DURATION(long value) { ATTACKS_CAST_TIMING_MAX_DURATION = value; }

        public static double ATTACKS_CAST_TIMING_REDUCTION_RATE = 0.2; // ticks/combo_count
        static void setATTACKS_CAST_TIMING_REDUCTION_RATE(double value) { ATTACKS_CAST_TIMING_REDUCTION_RATE = value; }

        public static int ATTACKS_DURATION_MULTIPLIER = 500; // milliseconds multiplier
        static void setATTACKS_DURATION_MULTIPLIER(int value) { ATTACKS_DURATION_MULTIPLIER = value; }

        // Attacks range multipliers configuration
        public static double ATTACKS_RANGE_MULTIPLIERS_BASIC_1 = 1.4;
        static void setATTACKS_RANGE_MULTIPLIERS_BASIC_1(double value) { ATTACKS_RANGE_MULTIPLIERS_BASIC_1 = value; }

        public static double ATTACKS_RANGE_MULTIPLIERS_BASIC_2 = 1.4;
        static void setATTACKS_RANGE_MULTIPLIERS_BASIC_2(double value) { ATTACKS_RANGE_MULTIPLIERS_BASIC_2 = value; }

        public static double ATTACKS_RANGE_MULTIPLIERS_BASIC_3 = 1.4;
        static void setATTACKS_RANGE_MULTIPLIERS_BASIC_3(double value) { ATTACKS_RANGE_MULTIPLIERS_BASIC_3 = value; }

        public static double ATTACKS_RANGE_MULTIPLIERS_NEUTRAL_AIR = 1.3;
        static void setATTACKS_RANGE_MULTIPLIERS_NEUTRAL_AIR(double value) { ATTACKS_RANGE_MULTIPLIERS_NEUTRAL_AIR = value; }

        public static double ATTACKS_RANGE_MULTIPLIERS_DOWN_AIR = 1.2;
        static void setATTACKS_RANGE_MULTIPLIERS_DOWN_AIR(double value) { ATTACKS_RANGE_MULTIPLIERS_DOWN_AIR = value; }

        // Hitboxes configuration
        public static double HITBOXES_BASIC_REACH = 1.5;
        static void setHITBOXES_BASIC_REACH(double value) { HITBOXES_BASIC_REACH = value; }

        public static double HITBOXES_BASIC_WIDTH = 1.5;
        static void setHITBOXES_BASIC_WIDTH(double value) { HITBOXES_BASIC_WIDTH = value; }

        public static double HITBOXES_BASIC_HEIGHT = 1.5;
        static void setHITBOXES_BASIC_HEIGHT(double value) { HITBOXES_BASIC_HEIGHT = value; }

        public static double HITBOXES_DOWN_AIR_REACH = 1.6;
        static void setHITBOXES_DOWN_AIR_REACH(double value) { HITBOXES_DOWN_AIR_REACH = value; }

        public static double HITBOXES_DOWN_AIR_WIDTH = 1.4;
        static void setHITBOXES_DOWN_AIR_WIDTH(double value) { HITBOXES_DOWN_AIR_WIDTH = value; }

        public static double HITBOXES_DOWN_AIR_HEIGHT = 2.5;
        static void setHITBOXES_DOWN_AIR_HEIGHT(double value) { HITBOXES_DOWN_AIR_HEIGHT = value; }

        public static double HITBOXES_SECANT_RADIUS = 0.4;
        static void setHITBOXES_SECANT_RADIUS(double value) { HITBOXES_SECANT_RADIUS = value; }

        // Thrown damage configuration
        public static double THROWN_DAMAGE_SWORD_DAMAGE_MULTIPLIER = 1.0;
        static void setTHROWN_DAMAGE_SWORD_DAMAGE_MULTIPLIER(double value) { THROWN_DAMAGE_SWORD_DAMAGE_MULTIPLIER = value; }

        public static double THROWN_DAMAGE_ITEM_VELOCITY_MULTIPLIER = 1.5;
        static void setTHROWN_DAMAGE_ITEM_VELOCITY_MULTIPLIER(double value) { THROWN_DAMAGE_ITEM_VELOCITY_MULTIPLIER = value; }

        public static double THROWN_DAMAGE_BASE_THROWN_DAMAGE = 12.0;
        static void setTHROWN_DAMAGE_BASE_THROWN_DAMAGE(double value) { THROWN_DAMAGE_BASE_THROWN_DAMAGE = value; }

        // Thrown damage sword/axe configuration
        public static int THROWN_DAMAGE_SWORD_AXE_INVULNERABILITY_TICKS = 0;
        static void setTHROWN_DAMAGE_SWORD_AXE_INVULNERABILITY_TICKS(int value) { THROWN_DAMAGE_SWORD_AXE_INVULNERABILITY_TICKS = value; }

        public static int THROWN_DAMAGE_SWORD_AXE_BASE_SHARDS = 2;
        static void setTHROWN_DAMAGE_SWORD_AXE_BASE_SHARDS(int value) { THROWN_DAMAGE_SWORD_AXE_BASE_SHARDS = value; }

        public static float THROWN_DAMAGE_SWORD_AXE_TOUGHNESS_DAMAGE = 75.0f;
        static void setTHROWN_DAMAGE_SWORD_AXE_TOUGHNESS_DAMAGE(float value) { THROWN_DAMAGE_SWORD_AXE_TOUGHNESS_DAMAGE = value; }

        public static float THROWN_DAMAGE_SWORD_AXE_SOULFIRE_REDUCTION = 50.0f;
        static void setTHROWN_DAMAGE_SWORD_AXE_SOULFIRE_REDUCTION(float value) { THROWN_DAMAGE_SWORD_AXE_SOULFIRE_REDUCTION = value; }

        public static double THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_GROUNDED = 0.7;
        static void setTHROWN_DAMAGE_SWORD_AXE_KNOCKBACK_GROUNDED(double value) { THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_GROUNDED = value; }

        public static double THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_AIRBORNE = 1.0;
        static void setTHROWN_DAMAGE_SWORD_AXE_KNOCKBACK_AIRBORNE(double value) { THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_AIRBORNE = value; }

        // Thrown damage other items configuration
        public static int THROWN_DAMAGE_OTHER_INVULNERABILITY_TICKS = 0;
        static void setTHROWN_DAMAGE_OTHER_INVULNERABILITY_TICKS(int value) { THROWN_DAMAGE_OTHER_INVULNERABILITY_TICKS = value; }

        public static int THROWN_DAMAGE_OTHER_BASE_SHARDS = 2;
        static void setTHROWN_DAMAGE_OTHER_BASE_SHARDS(int value) { THROWN_DAMAGE_OTHER_BASE_SHARDS = value; }

        public static float THROWN_DAMAGE_OTHER_TOUGHNESS_DAMAGE = 75.0f;
        static void setTHROWN_DAMAGE_OTHER_TOUGHNESS_DAMAGE(float value) { THROWN_DAMAGE_OTHER_TOUGHNESS_DAMAGE = value; }

        public static float THROWN_DAMAGE_OTHER_SOULFIRE_REDUCTION = 50.0f;
        static void setTHROWN_DAMAGE_OTHER_SOULFIRE_REDUCTION(float value) { THROWN_DAMAGE_OTHER_SOULFIRE_REDUCTION = value; }

        public static double THROWN_DAMAGE_OTHER_KNOCKBACK_MULTIPLIER = 0.7;
        static void setTHROWN_DAMAGE_OTHER_KNOCKBACK_MULTIPLIER(double value) { THROWN_DAMAGE_OTHER_KNOCKBACK_MULTIPLIER = value; }

        public static float THROWN_DAMAGE_OTHER_EXPLOSION_POWER = 1.0f;
        static void setTHROWN_DAMAGE_OTHER_EXPLOSION_POWER(float value) { THROWN_DAMAGE_OTHER_EXPLOSION_POWER = value; }

        // Impalement configuration
        public static double IMPALEMENT_DAMAGE_PER_TICK = 2.0;
        static void setIMPALEMENT_DAMAGE_PER_TICK(double value) { IMPALEMENT_DAMAGE_PER_TICK = value; }

        public static int IMPALEMENT_TICKS_BETWEEN_DAMAGE = 10;
        static void setIMPALEMENT_TICKS_BETWEEN_DAMAGE(int value) { IMPALEMENT_TICKS_BETWEEN_DAMAGE = value; }

        public static int IMPALEMENT_MAX_IMPALEMENTS = 3;
        static void setIMPALEMENT_MAX_IMPALEMENTS(int value) { IMPALEMENT_MAX_IMPALEMENTS = value; }

        public static double IMPALEMENT_HEAD_ZONE_RATIO = 0.8; // 0-1 (fraction of entity height)
        static void setIMPALEMENT_HEAD_ZONE_RATIO(double value) { IMPALEMENT_HEAD_ZONE_RATIO = value; }

        public static List<EntityType> IMPALEMENT_HEAD_FOLLOW_EXCEPTIONS = List.of(EntityType.SPIDER);
        static void setIMPALEMENT_HEAD_FOLLOW_EXCEPTIONS(List<EntityType> value) { IMPALEMENT_HEAD_FOLLOW_EXCEPTIONS = value; }

        public static int IMPALEMENT_PIN_MAX_ITERATIONS = 50;
        static void setIMPALEMENT_PIN_MAX_ITERATIONS(int value) { IMPALEMENT_PIN_MAX_ITERATIONS = value; }

        public static int IMPALEMENT_PIN_CHECK_INTERVAL = 2;
        static void setIMPALEMENT_PIN_CHECK_INTERVAL(int value) { IMPALEMENT_PIN_CHECK_INTERVAL = value; }

        // Attack class configuration
        public static List<String> ATTACK_CLASS_EXEMPT_FROM_COMBAT = List.of(
            "ARMOR_STAND", "ITEM_FRAME", "GLOW_ITEM_FRAME", "PAINTING",
            "ITEM_DISPLAY", "BLOCK_DISPLAY", "TEXT_DISPLAY", "INTERACTION"
        );
        static void setATTACK_CLASS_EXEMPT_FROM_COMBAT(List<String> value) { ATTACK_CLASS_EXEMPT_FROM_COMBAT = value; }

        // Attack class timing configuration
        public static int ATTACK_CLASS_TIMING_ATTACK_DURATION = 750;
        static void setATTACK_CLASS_TIMING_ATTACK_DURATION(int value) { ATTACK_CLASS_TIMING_ATTACK_DURATION = value; }

        public static int ATTACK_CLASS_TIMING_ATTACK_ITERATIONS = 5;
        static void setATTACK_CLASS_TIMING_ATTACK_ITERATIONS(int value) { ATTACK_CLASS_TIMING_ATTACK_ITERATIONS = value; }

        public static double ATTACK_CLASS_TIMING_ATTACK_START_VALUE = 0.0; // progress 0-1
        static void setATTACK_CLASS_TIMING_ATTACK_START_VALUE(double value) { ATTACK_CLASS_TIMING_ATTACK_START_VALUE = value; }

        public static double ATTACK_CLASS_TIMING_ATTACK_END_VALUE = 1.0; // progress 0-1
        static void setATTACK_CLASS_TIMING_ATTACK_END_VALUE(double value) { ATTACK_CLASS_TIMING_ATTACK_END_VALUE = value; }

        // Attack class modifiers configuration
        public static double ATTACK_CLASS_MODIFIERS_RANGE_MULTIPLIER = 2.0;
        static void setATTACK_CLASS_MODIFIERS_RANGE_MULTIPLIER(double value) { ATTACK_CLASS_MODIFIERS_RANGE_MULTIPLIER = value; }
    }

    // ==============================================================================
    // TIMING - Cooldowns, durations, intervals
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
        static void setTHROWN_ITEMS_CATCH_GRACE_PERIOD(int value) { THROWN_ITEMS_CATCH_GRACE_PERIOD = value; }

        public static int THROWN_ITEMS_DISPOSAL_TIMEOUT = 200;
        static void setTHROWN_ITEMS_DISPOSAL_TIMEOUT(int value) { THROWN_ITEMS_DISPOSAL_TIMEOUT = value; }

        public static int THROWN_ITEMS_DISPOSAL_CHECK_INTERVAL = 10;
        static void setTHROWN_ITEMS_DISPOSAL_CHECK_INTERVAL(int value) { THROWN_ITEMS_DISPOSAL_CHECK_INTERVAL = value; }

        public static int THROWN_ITEMS_PIN_DELAY = 2;
        static void setTHROWN_ITEMS_PIN_DELAY(int value) { THROWN_ITEMS_PIN_DELAY = value; }

        public static int THROWN_ITEMS_THROW_COMPLETION_DELAY = 6;
        static void setTHROWN_ITEMS_THROW_COMPLETION_DELAY(int value) { THROWN_ITEMS_THROW_COMPLETION_DELAY = value; }

        // Intervals configuration
        public static int INTERVALS_ENTITY_TICK = 1;
        static void setINTERVALS_ENTITY_TICK(int value) { INTERVALS_ENTITY_TICK = value; }

        public static int INTERVALS_STATUS_DISPLAY_UPDATE = 5;
        static void setINTERVALS_STATUS_DISPLAY_UPDATE(int value) { INTERVALS_STATUS_DISPLAY_UPDATE = value; }

        public static int INTERVALS_COMBAT_CLEANUP = 20;
        static void setINTERVALS_COMBAT_CLEANUP(int value) { INTERVALS_COMBAT_CLEANUP = value; }

        // Attacks configuration
        public static int ATTACKS_COMBO_WINDOW_BASE = 3;
        static void setATTACKS_COMBO_WINDOW_BASE(int value) { ATTACKS_COMBO_WINDOW_BASE = value; }
    }

    // ==============================================================================
    // DISPLAY - Visual elements, particles, effects
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
        static void setDEFAULT_TELEPORT_DURATION(int value) { DEFAULT_TELEPORT_DURATION = value; }

        // Status display configuration
        public static boolean STATUS_DISPLAY_ENABLED = true;
        static void setSTATUS_DISPLAY_ENABLED(boolean value) { STATUS_DISPLAY_ENABLED = value; }

        public static double STATUS_DISPLAY_HEIGHT_OFFSET = 2.0;
        static void setSTATUS_DISPLAY_HEIGHT_OFFSET(double value) { STATUS_DISPLAY_HEIGHT_OFFSET = value; }

        public static int STATUS_DISPLAY_UPDATE_INTERVAL = 5;
        static void setSTATUS_DISPLAY_UPDATE_INTERVAL(int value) { STATUS_DISPLAY_UPDATE_INTERVAL = value; }

        public static int STATUS_DISPLAY_BLOCK_BRIGHTNESS = 15; // 0-15 (light level)
        static void setSTATUS_DISPLAY_BLOCK_BRIGHTNESS(int value) { STATUS_DISPLAY_BLOCK_BRIGHTNESS = value; }

        public static int STATUS_DISPLAY_SKY_BRIGHTNESS = 15; // 0-15 (light level)
        static void setSTATUS_DISPLAY_SKY_BRIGHTNESS(int value) { STATUS_DISPLAY_SKY_BRIGHTNESS = value; }

        // Item display follow configuration
        public static int ITEM_DISPLAY_FOLLOW_UPDATE_INTERVAL = 2;
        static void setITEM_DISPLAY_FOLLOW_UPDATE_INTERVAL(int value) { ITEM_DISPLAY_FOLLOW_UPDATE_INTERVAL = value; }

        public static int ITEM_DISPLAY_FOLLOW_PARTICLE_INTERVAL = 4;
        static void setITEM_DISPLAY_FOLLOW_PARTICLE_INTERVAL(int value) { ITEM_DISPLAY_FOLLOW_PARTICLE_INTERVAL = value; }

        public static Billboard ITEM_DISPLAY_FOLLOW_BILLBOARD_MODE = Billboard.FIXED;
        static void setITEM_DISPLAY_FOLLOW_BILLBOARD_MODE(Billboard value) { ITEM_DISPLAY_FOLLOW_BILLBOARD_MODE = value; }

        // Particles configuration
        public static boolean PARTICLES_ENABLED = true;
        static void setPARTICLES_ENABLED(boolean value) { PARTICLES_ENABLED = value; }

        public static int PARTICLES_DENSITY = 10;
        static void setPARTICLES_DENSITY(int value) { PARTICLES_DENSITY = value; }
    }

    // ==============================================================================
    // DETECTION - Hitboxes, range detection, raytracing
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
        static void setGROUND_CHECK_MAX_DISTANCE(double value) { GROUND_CHECK_MAX_DISTANCE = value; }

        // Raytrace configuration
        public static double RAYTRACE_MAX_DISTANCE = 50.0;
        static void setRAYTRACE_MAX_DISTANCE(double value) { RAYTRACE_MAX_DISTANCE = value; }

        public static double RAYTRACE_STEP_SIZE = 0.1;
        static void setRAYTRACE_STEP_SIZE(double value) { RAYTRACE_STEP_SIZE = value; }

        public static boolean RAYTRACE_IGNORE_PASSABLE_BLOCKS = true;
        static void setRAYTRACE_IGNORE_PASSABLE_BLOCKS(boolean value) { RAYTRACE_IGNORE_PASSABLE_BLOCKS = value; }

        // Entity detection configuration
        public static double ENTITY_DETECTION_SEARCH_RADIUS = 10.0;
        static void setENTITY_DETECTION_SEARCH_RADIUS(double value) { ENTITY_DETECTION_SEARCH_RADIUS = value; }

        public static boolean ENTITY_DETECTION_INCLUDE_SPECTATORS = false;
        static void setENTITY_DETECTION_INCLUDE_SPECTATORS(boolean value) { ENTITY_DETECTION_INCLUDE_SPECTATORS = value; }
    }

    // ==============================================================================
    // AUDIO - Sound effects and audio feedback
    // ==============================================================================
    /**
     * Audio configuration for sound effects and audio feedback.
     * <p>
     * Controls sound types, volumes, and pitches for combat actions. Volume range
     * 0.0-1.0 (0% to 100%), pitch range 0.5-2.0 (half speed to double speed).
     * </p>
     *
     * <h2>Volume Guidelines</h2>
     * <ul>
     *   <li><b>0.0-0.2</b>: Subtle background effects</li>
     *   <li><b>0.2-0.5</b>: Normal game sounds</li>
     *   <li><b>0.5-1.0</b>: Prominent/important sounds</li>
     * </ul>
     *
     * @see btm.sword.util.sound.SoundType Custom sound type enum
     * @see btm.sword.util.Prefab.Sounds Prefabricated sound wrappers
     */
    public static class Audio {
        // Sounds configuration
        public static boolean SOUNDS_ENABLED = true;
        static void setSOUNDS_ENABLED(boolean value) { SOUNDS_ENABLED = value; }

        public static float SOUNDS_GLOBAL_VOLUME = 1.0f; // 0.0-1.0
        static void setSOUNDS_GLOBAL_VOLUME(float value) { SOUNDS_GLOBAL_VOLUME = value; }

        public static float SOUNDS_GLOBAL_PITCH = 1.0f; // 0.5-2.0
        static void setSOUNDS_GLOBAL_PITCH(float value) { SOUNDS_GLOBAL_PITCH = value; }

        // Throw sound configuration
        public static btm.sword.util.sound.SoundType THROW_SOUND = btm.sword.util.sound.SoundType.ENTITY_ENDER_DRAGON_FLAP;
        static void setTHROW_SOUND(btm.sword.util.sound.SoundType value) { THROW_SOUND = value; }

        public static float THROW_VOLUME = 0.35f; // 0.0-1.0
        static void setTHROW_VOLUME(float value) { THROW_VOLUME = value; }

        public static float THROW_PITCH = 0.4f; // 0.5-2.0
        static void setTHROW_PITCH(float value) { THROW_PITCH = value; }

        // Attack sound configuration
        public static btm.sword.util.sound.SoundType ATTACK_SOUND = btm.sword.util.sound.SoundType.ITEM_TRIDENT_THROW;
        static void setATTACK_SOUND(btm.sword.util.sound.SoundType value) { ATTACK_SOUND = value; }

        public static float ATTACK_VOLUME = 0.055f; // 0.0-1.0
        static void setATTACK_VOLUME(float value) { ATTACK_VOLUME = value; }

        public static float ATTACK_PITCH = 1.5f; // 0.5-2.0
        static void setATTACK_PITCH(float value) { ATTACK_PITCH = value; }
    }

    // ==============================================================================
    // ENTITY - Entity stats, health, aspects
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
        static void setPLAYER_BASE_HEALTH(double value) { PLAYER_BASE_HEALTH = value; }

        public static double PLAYER_BASE_TOUGHNESS = 20.0; // HP
        static void setPLAYER_BASE_TOUGHNESS(double value) { PLAYER_BASE_TOUGHNESS = value; }

        public static double PLAYER_BASE_SOULFIRE = 100.0; // points
        static void setPLAYER_BASE_SOULFIRE(double value) { PLAYER_BASE_SOULFIRE = value; }

        // Hostile configuration
        public static double HOSTILE_HEALTH_MULTIPLIER = 1.0;
        static void setHOSTILE_HEALTH_MULTIPLIER(double value) { HOSTILE_HEALTH_MULTIPLIER = value; }

        public static double HOSTILE_DAMAGE_MULTIPLIER = 1.0;
        static void setHOSTILE_DAMAGE_MULTIPLIER(double value) { HOSTILE_DAMAGE_MULTIPLIER = value; }

        // Combat profile configuration
        public static int COMBAT_PROFILE_MAX_AIR_DODGES = 1;
        static void setCOMBAT_PROFILE_MAX_AIR_DODGES(int value) { COMBAT_PROFILE_MAX_AIR_DODGES = value; }

        // Combat profile shards configuration
        public static float COMBAT_PROFILE_SHARDS_CURRENT = 10.0f;
        static void setCOMBAT_PROFILE_SHARDS_CURRENT(float value) { COMBAT_PROFILE_SHARDS_CURRENT = value; }

        public static int COMBAT_PROFILE_SHARDS_REGEN_PERIOD = 50;
        static void setCOMBAT_PROFILE_SHARDS_REGEN_PERIOD(int value) { COMBAT_PROFILE_SHARDS_REGEN_PERIOD = value; }

        public static float COMBAT_PROFILE_SHARDS_REGEN_AMOUNT = 1.0f;
        static void setCOMBAT_PROFILE_SHARDS_REGEN_AMOUNT(float value) { COMBAT_PROFILE_SHARDS_REGEN_AMOUNT = value; }

        // Combat profile toughness configuration
        public static float COMBAT_PROFILE_TOUGHNESS_CURRENT = 20.0f;
        static void setCOMBAT_PROFILE_TOUGHNESS_CURRENT(float value) { COMBAT_PROFILE_TOUGHNESS_CURRENT = value; }

        public static int COMBAT_PROFILE_TOUGHNESS_REGEN_PERIOD = 20;
        static void setCOMBAT_PROFILE_TOUGHNESS_REGEN_PERIOD(int value) { COMBAT_PROFILE_TOUGHNESS_REGEN_PERIOD = value; }

        public static float COMBAT_PROFILE_TOUGHNESS_REGEN_AMOUNT = 0.5f;
        static void setCOMBAT_PROFILE_TOUGHNESS_REGEN_AMOUNT(float value) { COMBAT_PROFILE_TOUGHNESS_REGEN_AMOUNT = value; }

        // Combat profile soulfire configuration
        public static float COMBAT_PROFILE_SOULFIRE_CURRENT = 100.0f;
        static void setCOMBAT_PROFILE_SOULFIRE_CURRENT(float value) { COMBAT_PROFILE_SOULFIRE_CURRENT = value; }

        public static int COMBAT_PROFILE_SOULFIRE_REGEN_PERIOD = 5;
        static void setCOMBAT_PROFILE_SOULFIRE_REGEN_PERIOD(int value) { COMBAT_PROFILE_SOULFIRE_REGEN_PERIOD = value; }

        public static float COMBAT_PROFILE_SOULFIRE_REGEN_AMOUNT = 0.2f;
        static void setCOMBAT_PROFILE_SOULFIRE_REGEN_AMOUNT(float value) { COMBAT_PROFILE_SOULFIRE_REGEN_AMOUNT = value; }

        // Combat profile form configuration
        public static float COMBAT_PROFILE_FORM_CURRENT = 10.0f;
        static void setCOMBAT_PROFILE_FORM_CURRENT(float value) { COMBAT_PROFILE_FORM_CURRENT = value; }

        public static int COMBAT_PROFILE_FORM_REGEN_PERIOD = 60;
        static void setCOMBAT_PROFILE_FORM_REGEN_PERIOD(int value) { COMBAT_PROFILE_FORM_REGEN_PERIOD = value; }

        public static float COMBAT_PROFILE_FORM_REGEN_AMOUNT = 1.0f;
        static void setCOMBAT_PROFILE_FORM_REGEN_AMOUNT(float value) { COMBAT_PROFILE_FORM_REGEN_AMOUNT = value; }
    }

    // ==============================================================================
    // MOVEMENT - Dash, grab, mobility abilities
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
        static void setDASH_MAX_DISTANCE(double value) { DASH_MAX_DISTANCE = value; }

        public static int DASH_CAST_DURATION = 5;
        static void setDASH_CAST_DURATION(int value) { DASH_CAST_DURATION = value; }

        public static double DASH_BASE_POWER = 0.7;
        static void setDASH_BASE_POWER(double value) { DASH_BASE_POWER = value; }

        public static double DASH_INITIAL_OFFSET_Y = 0.3;
        static void setDASH_INITIAL_OFFSET_Y(double value) { DASH_INITIAL_OFFSET_Y = value; }

        public static double DASH_IMPEDANCE_CHECK_OFFSET_Y = 0.5;
        static void setDASH_IMPEDANCE_CHECK_OFFSET_Y(double value) { DASH_IMPEDANCE_CHECK_OFFSET_Y = value; }

        public static double DASH_FORWARD_MULTIPLIER = 0.5;
        static void setDASH_FORWARD_MULTIPLIER(double value) { DASH_FORWARD_MULTIPLIER = value; }

        public static double DASH_UPWARD_MULTIPLIER = 0.15;
        static void setDASH_UPWARD_MULTIPLIER(double value) { DASH_UPWARD_MULTIPLIER = value; }

        public static double DASH_UPWARD_BOOST = 0.05;
        static void setDASH_UPWARD_BOOST(double value) { DASH_UPWARD_BOOST = value; }

        public static double DASH_RAY_HITBOX_RADIUS = 0.7;
        static void setDASH_RAY_HITBOX_RADIUS(double value) { DASH_RAY_HITBOX_RADIUS = value; }

        public static double DASH_SECANT_RADIUS = 0.3;
        static void setDASH_SECANT_RADIUS(double value) { DASH_SECANT_RADIUS = value; }

        public static double DASH_GRAB_DISTANCE_SQUARED = 8.5; // blocks² (sqrt(8.5) ≈ 2.9 blocks)
        static void setDASH_GRAB_DISTANCE_SQUARED(double value) { DASH_GRAB_DISTANCE_SQUARED = value; }

        public static double DASH_BLOCK_CHECK_OFFSET_Y = -0.75;
        static void setDASH_BLOCK_CHECK_OFFSET_Y(double value) { DASH_BLOCK_CHECK_OFFSET_Y = value; }

        public static double DASH_VELOCITY_DAMPING = 0.6; // multiplier (0-1)
        static void setDASH_VELOCITY_DAMPING(double value) { DASH_VELOCITY_DAMPING = value; }

        public static long DASH_PARTICLE_TASK_DELAY = 0L;
        static void setDASH_PARTICLE_TASK_DELAY(long value) { DASH_PARTICLE_TASK_DELAY = value; }

        public static long DASH_PARTICLE_TASK_PERIOD = 2L;
        static void setDASH_PARTICLE_TASK_PERIOD(long value) { DASH_PARTICLE_TASK_PERIOD = value; }

        public static int DASH_PARTICLE_TIMER_INCREMENT = 2;
        static void setDASH_PARTICLE_TIMER_INCREMENT(int value) { DASH_PARTICLE_TIMER_INCREMENT = value; }

        public static int DASH_PARTICLE_TIMER_THRESHOLD = 4;
        static void setDASH_PARTICLE_TIMER_THRESHOLD(int value) { DASH_PARTICLE_TIMER_THRESHOLD = value; }

        public static long DASH_GRAB_CHECK_DELAY = 4L;
        static void setDASH_GRAB_CHECK_DELAY(long value) { DASH_GRAB_CHECK_DELAY = value; }

        public static long DASH_VELOCITY_TASK_DELAY = 0L;
        static void setDASH_VELOCITY_TASK_DELAY(long value) { DASH_VELOCITY_TASK_DELAY = value; }

        public static long DASH_VELOCITY_TASK_PERIOD = 1L;
        static void setDASH_VELOCITY_TASK_PERIOD(long value) { DASH_VELOCITY_TASK_PERIOD = value; }

        public static int DASH_PARTICLE_COUNT = 100;
        static void setDASH_PARTICLE_COUNT(int value) { DASH_PARTICLE_COUNT = value; }

        public static double DASH_PARTICLE_SPREAD_X = 1.25;
        static void setDASH_PARTICLE_SPREAD_X(double value) { DASH_PARTICLE_SPREAD_X = value; }

        public static double DASH_PARTICLE_SPREAD_Y = 1.25;
        static void setDASH_PARTICLE_SPREAD_Y(double value) { DASH_PARTICLE_SPREAD_Y = value; }

        public static double DASH_PARTICLE_SPREAD_Z = 1.25;
        static void setDASH_PARTICLE_SPREAD_Z(double value) { DASH_PARTICLE_SPREAD_Z = value; }

        public static float DASH_FLAP_SOUND_VOLUME = 0.6f; // 0.0-1.0
        static void setDASH_FLAP_SOUND_VOLUME(float value) { DASH_FLAP_SOUND_VOLUME = value; }

        public static float DASH_FLAP_SOUND_PITCH = 1.0f; // 0.5-2.0
        static void setDASH_FLAP_SOUND_PITCH(float value) { DASH_FLAP_SOUND_PITCH = value; }

        public static float DASH_SWEEP_SOUND_VOLUME = 0.3f; // 0.0-1.0
        static void setDASH_SWEEP_SOUND_VOLUME(float value) { DASH_SWEEP_SOUND_VOLUME = value; }

        public static float DASH_SWEEP_SOUND_PITCH = 0.6f; // 0.5-2.0
        static void setDASH_SWEEP_SOUND_PITCH(float value) { DASH_SWEEP_SOUND_PITCH = value; }

        // Toss configuration
        public static double TOSS_BASE_FORCE = 1.5; // blocks/tick
        static void setTOSS_BASE_FORCE(double value) { TOSS_BASE_FORCE = value; }

        public static double TOSS_MIGHT_MULTIPLIER_BASE = 2.5; // multiplier
        static void setTOSS_MIGHT_MULTIPLIER_BASE(double value) { TOSS_MIGHT_MULTIPLIER_BASE = value; }

        public static double TOSS_MIGHT_MULTIPLIER_INCREMENT = 0.1; // per level
        static void setTOSS_MIGHT_MULTIPLIER_INCREMENT(double value) { TOSS_MIGHT_MULTIPLIER_INCREMENT = value; }

        public static int TOSS_UPWARD_PHASE_ITERATIONS = 2;
        static void setTOSS_UPWARD_PHASE_ITERATIONS(int value) { TOSS_UPWARD_PHASE_ITERATIONS = value; }

        public static double TOSS_UPWARD_VELOCITY_Y = 0.25; // blocks/tick
        static void setTOSS_UPWARD_VELOCITY_Y(double value) { TOSS_UPWARD_VELOCITY_Y = value; }

        public static int TOSS_FORWARD_PHASE_ITERATIONS = 3;
        static void setTOSS_FORWARD_PHASE_ITERATIONS(int value) { TOSS_FORWARD_PHASE_ITERATIONS = value; }

        public static int TOSS_ANIMATION_ITERATIONS = 15;
        static void setTOSS_ANIMATION_ITERATIONS(int value) { TOSS_ANIMATION_ITERATIONS = value; }

        public static double TOSS_LOCATION_OFFSET_MULTIPLIER = 0.3;
        static void setTOSS_LOCATION_OFFSET_MULTIPLIER(double value) { TOSS_LOCATION_OFFSET_MULTIPLIER = value; }

        public static double TOSS_PARTICLE_HEIGHT_MULTIPLIER = 0.5;
        static void setTOSS_PARTICLE_HEIGHT_MULTIPLIER(double value) { TOSS_PARTICLE_HEIGHT_MULTIPLIER = value; }

        public static double TOSS_RAY_TRACE_DISTANCE_MULTIPLIER = 0.6;
        static void setTOSS_RAY_TRACE_DISTANCE_MULTIPLIER(double value) { TOSS_RAY_TRACE_DISTANCE_MULTIPLIER = value; }

        public static double TOSS_ENTITY_DETECTION_RADIUS = 0.4;
        static void setTOSS_ENTITY_DETECTION_RADIUS(double value) { TOSS_ENTITY_DETECTION_RADIUS = value; }

        public static double TOSS_KNOCKBACK_MULTIPLIER = 0.3;
        static void setTOSS_KNOCKBACK_MULTIPLIER(double value) { TOSS_KNOCKBACK_MULTIPLIER = value; }

        public static float TOSS_EXPLOSION_POWER = 2.0f;
        static void setTOSS_EXPLOSION_POWER(float value) { TOSS_EXPLOSION_POWER = value; }

        public static int TOSS_HIT_INVULNERABILITY_TICKS = 3;
        static void setTOSS_HIT_INVULNERABILITY_TICKS(int value) { TOSS_HIT_INVULNERABILITY_TICKS = value; }

        public static int TOSS_HIT_SHARD_DAMAGE = 2;
        static void setTOSS_HIT_SHARD_DAMAGE(int value) { TOSS_HIT_SHARD_DAMAGE = value; }

        public static float TOSS_HIT_TOUGHNESS_DAMAGE = 30.0f;
        static void setTOSS_HIT_TOUGHNESS_DAMAGE(float value) { TOSS_HIT_TOUGHNESS_DAMAGE = value; }

        public static float TOSS_HIT_SOULFIRE_REDUCTION = 5.0f;
        static void setTOSS_HIT_SOULFIRE_REDUCTION(float value) { TOSS_HIT_SOULFIRE_REDUCTION = value; }

        // Grab configuration
        public static double GRAB_PULL_STRENGTH = 0.8; // blocks/tick
        static void setGRAB_PULL_STRENGTH(double value) { GRAB_PULL_STRENGTH = value; }

        public static double GRAB_MAX_RANGE = 3.0;
        static void setGRAB_MAX_RANGE(double value) { GRAB_MAX_RANGE = value; }

        public static int GRAB_HOLD_DURATION = 40;
        static void setGRAB_HOLD_DURATION(int value) { GRAB_HOLD_DURATION = value; }
    }

    // ==============================================================================
    // WORLD - World interaction, block breaking, environment
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
        static void setBLOCK_INTERACTION_ALLOW_BLOCK_BREAKING(boolean value) { BLOCK_INTERACTION_ALLOW_BLOCK_BREAKING = value; }

        public static boolean BLOCK_INTERACTION_RESPECT_WORLD_GUARD = true;
        static void setBLOCK_INTERACTION_RESPECT_WORLD_GUARD(boolean value) { BLOCK_INTERACTION_RESPECT_WORLD_GUARD = value; }

        // Explosions configuration
        public static boolean EXPLOSIONS_SET_FIRE = false;
        static void setEXPLOSIONS_SET_FIRE(boolean value) { EXPLOSIONS_SET_FIRE = value; }

        public static boolean EXPLOSIONS_BREAK_BLOCKS = false;
        static void setEXPLOSIONS_BREAK_BLOCKS(boolean value) { EXPLOSIONS_BREAK_BLOCKS = value; }
    }

    // ==============================================================================
    // DEBUG - Development and testing options
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
        static void setLOGGING_VERBOSE_COMBAT(boolean value) { LOGGING_VERBOSE_COMBAT = value; }

        public static boolean LOGGING_VERBOSE_MOVEMENT = false;
        static void setLOGGING_VERBOSE_MOVEMENT(boolean value) { LOGGING_VERBOSE_MOVEMENT = value; }

        public static boolean LOGGING_VERBOSE_CONFIG = false;
        static void setLOGGING_VERBOSE_CONFIG(boolean value) { LOGGING_VERBOSE_CONFIG = value; }

        // Visualization configuration
        public static boolean VISUALIZATION_SHOW_HITBOXES = false;
        static void setVISUALIZATION_SHOW_HITBOXES(boolean value) { VISUALIZATION_SHOW_HITBOXES = value; }

        public static boolean VISUALIZATION_SHOW_RAYTRACES = false;
        static void setVISUALIZATION_SHOW_RAYTRACES(boolean value) { VISUALIZATION_SHOW_RAYTRACES = value; }
    }
}
