# Physics Calculations

This document explains the physics simulation systems used in Sword: Combat Evolved, including thrown item physics, gravity damping, rotation mechanics, and knockback calculations.

## Table of Contents

1. [Thrown Item Physics](#thrown-item-physics)
2. [Gravity Simulation](#gravity-simulation)
3. [Rotation Mechanics](#rotation-mechanics)
4. [Knockback Physics](#knockback-physics)
5. [Configuration Reference](#configuration-reference)

---

## Thrown Item Physics

### Overview

Thrown items (swords, axes, tools) are simulated as physics-based projectiles with custom gravity, rotation, and collision detection. The system uses Minecraft's display entities for visual representation while maintaining separate physics state.

### Position Update

Each tick, the thrown item position is updated:

```
position_new = position_old + velocity * delta_time
```

Where `delta_time = 1 tick` in the game loop.

### Velocity Components

The velocity is decomposed into three components:

**Forward Velocity**:
```
velocity_forward = throw_direction * throw_force
```
Set at throw time based on [`Config.Movement.TOSS_BASE_FORCE`](../../src/main/java/btm/sword/config/Config.java) (1.5 blocks/tick).

**Gravity Velocity**:
```
velocity_gravity += gravity_acceleration * (1 / gravity_damper)
```
Applied every tick. See [Gravity Simulation](#gravity-simulation) for details.

**Trajectory Rotation**:
```
velocity = rotate(velocity, trajectory_rotation_angle)
```
Gradually curves the trajectory. See [Rotation Mechanics](#rotation-mechanics).

### Display Offset

The visual item display is offset from the physics position for better appearance:

```
display_position = physics_position + display_offset

display_offset = (
    Config.Physics.THROWN_ITEMS_DISPLAY_OFFSET_X,  // -0.5 blocks
    Config.Physics.THROWN_ITEMS_DISPLAY_OFFSET_Y,  //  0.1 blocks
    Config.Physics.THROWN_ITEMS_DISPLAY_OFFSET_Z   //  0.5 blocks
)
```

This prevents the item from appearing to intersect with the ground or player.

### Origin Offset

The throw origin is offset from the player position for realistic throw mechanics:

```
throw_origin = player_eye_location + origin_offset

origin_offset = (
    forward * Config.Physics.THROWN_ITEMS_ORIGIN_OFFSET_FORWARD,  // 0.5 blocks
    up * Config.Physics.THROWN_ITEMS_ORIGIN_OFFSET_UP,            // 0.1 blocks
    back * Config.Physics.THROWN_ITEMS_ORIGIN_OFFSET_BACK         // -0.25 blocks
)
```

This makes the throw feel like it comes from the player's hand rather than their eyes.

---

## Gravity Simulation

### Custom Gravity Model

Instead of using Minecraft's default gravity (0.08 blocks/tick²), the plugin uses a **damped gravity model**:

```
gravity_acceleration = minecraft_gravity / gravity_damper
                     = 0.08 / 46.0
                     = 0.001739 blocks/tick²
```

### Why Damped Gravity?

**Default Minecraft Gravity Issues**:
- Items fall too fast (< 1 second flight time)
- Limited throw range (< 10 blocks)
- Unrealistic arc for combat projectiles

**Damped Gravity Benefits**:
- Longer flight time (2-4 seconds)
- Extended throw range (20-40 blocks)
- More visible and trackable projectiles
- Satisfying arc trajectory

### Gravity Damper Value

The damper value of **46.0** was chosen through playtesting:

| Damper | Fall Time (10 blocks) | Feel |
|--------|-----------------------|------|
| 1.0    | 0.5 seconds          | Too fast, hard to track |
| 10.0   | 1.6 seconds          | Fast, limited range |
| 30.0   | 2.7 seconds          | Good, slightly quick |
| 46.0   | 3.4 seconds          | **Optimal** |
| 60.0   | 3.9 seconds          | Slow, floaty feel |

### Terminal Velocity

While the damped model prevents true terminal velocity, projectiles naturally slow down due to:

1. **Horizontal Drag**: No horizontal drag is applied (not realistic but feels better)
2. **Vertical Accumulation**: Gravity continuously accelerates downward
3. **Ground Collision**: Projectiles stop on impact

---

## Rotation Mechanics

### Visual Rotation

Each thrown item rotates based on its item type for visual flair:

```
rotation_angle += rotation_speed_per_type * delta_time
```

### Rotation Speeds by Type

Configured via [`Config.Physics.THROWN_ITEMS_ROTATION_SPEED_*`](../../src/main/java/btm/sword/config/Config.java):

| Item Type | Rotation Speed (rad/tick) | Degrees/Second | Visual Effect |
|-----------|---------------------------|----------------|---------------|
| Sword     | 0.0                       | 0°/s           | No rotation (blade forward) |
| Axe       | -π/8 (-0.393)             | -141°/s        | Counter-clockwise tumble |
| Hoe       | -π/8 (-0.393)             | -141°/s        | Counter-clockwise tumble |
| Pickaxe   | -π/8 (-0.393)             | -141°/s        | Counter-clockwise tumble |
| Shovel    | -π/8 (-0.393)             | -141°/s        | Counter-clockwise tumble |
| Shield    | -π/8 (-0.393)             | -141°/s        | Counter-clockwise tumble |
| Default   | π/32 (0.098)              | 35°/s          | Slow clockwise spin |

**Design Rationale**:
- **Swords** don't rotate to maintain "blade-forward" aesthetic
- **Tools** tumble to emphasize weight and momentum
- **Negative speeds** create satisfying counter-clockwise motion matching throw arc

### Trajectory Rotation

In addition to visual rotation, the velocity vector itself rotates slightly each tick:

```
rotation_angle = Config.Physics.THROWN_ITEMS_TRAJECTORY_ROTATION
               = 0.03696 radians/tick
               = 2.12 degrees/tick
               = 42.4 degrees/second
```

This creates a **curved trajectory** that:
- Looks more dynamic than a simple parabola
- Prevents perfectly straight throws
- Adds unpredictability for PvP balance

**Mathematical Implementation**:
```
velocity_new = rotate_around_axis(velocity_old, up_vector, rotation_angle)
```

---

## Knockback Physics

### Attack Knockback

When an attack hits, knockback is applied based on several factors:

#### Base Knockback Formula

```
knockback = attack_direction * knockback_strength

Where:
    knockback_strength = base_strength * grounded_damping * normal_multiplier
```

#### Grounded Damping

Players on the ground receive reduced knockback:

```
if (target.is_grounded()):
    knockback *= Config.Physics.ATTACK_VELOCITY_GROUNDED_DAMPING_HORIZONTAL  // 0.3
    knockback.y *= Config.Physics.ATTACK_VELOCITY_GROUNDED_DAMPING_VERTICAL   // 0.4
```

This prevents grounded players from being launched excessively.

#### Vertical Component

All knockback includes a vertical boost:

```
knockback.y += Config.Physics.ATTACK_VELOCITY_KNOCKBACK_VERTICAL_BASE  // 0.25 blocks/tick
```

This ensures targets are lifted off the ground, making combos possible.

#### Horizontal Modifier

Horizontal knockback is scaled:

```
knockback.xz *= Config.Physics.ATTACK_VELOCITY_KNOCKBACK_HORIZONTAL_MODIFIER  // 0.1
```

This prevents excessive horizontal displacement while maintaining vertical lift.

#### Normal Multiplier

The final knockback is scaled by attack normal:

```
knockback *= Config.Physics.ATTACK_VELOCITY_KNOCKBACK_NORMAL_MULTIPLIER  // 0.7
```

This ensures consistent knockback regardless of attack angle.

### Thrown Item Knockback

Thrown items apply different knockback based on player state:

```
if (target.is_grounded()):
    knockback = direction * Config.Combat.THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_GROUNDED  // 0.7
else:
    knockback = direction * Config.Combat.THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_AIRBORNE  // 1.0
```

Airborne targets receive **43% more knockback** (1.0 vs 0.7), rewarding aerial throws.

### Design Rationale

**Why Reduced Grounded Knockback?**
- Prevents infinite juggling
- Gives grounded players defensive advantage
- Encourages positioning and movement

**Why Vertical Boost?**
- Enables combo systems (launch → follow-up)
- Looks more dramatic and satisfying
- Prevents targets from immediately retaliating

**Why Separate Airborne Multiplier?**
- Rewards skillful aerial attacks
- Creates risk/reward for jump attacks
- Adds depth to combat positioning

---

## Configuration Reference

All physics parameters are configured via [`Config.Physics`](../../src/main/java/btm/sword/config/Config.java):

### Thrown Item Physics
- `THROWN_ITEMS_GRAVITY_DAMPER` (46.0) - Gravity reduction factor
- `THROWN_ITEMS_TRAJECTORY_ROTATION` (0.03696 rad/tick) - Velocity curve rate
- `THROWN_ITEMS_DISPLAY_OFFSET_X/Y/Z` - Visual position offset
- `THROWN_ITEMS_ORIGIN_OFFSET_FORWARD/UP/BACK` - Throw origin offset

### Rotation Speeds
- `THROWN_ITEMS_ROTATION_SPEED_SWORD` (0.0 rad/tick) - Sword rotation
- `THROWN_ITEMS_ROTATION_SPEED_AXE` (-π/8 rad/tick) - Axe rotation
- `THROWN_ITEMS_ROTATION_SPEED_HOE` (-π/8 rad/tick) - Hoe rotation
- `THROWN_ITEMS_ROTATION_SPEED_PICKAXE` (-π/8 rad/tick) - Pickaxe rotation
- `THROWN_ITEMS_ROTATION_SPEED_SHOVEL` (-π/8 rad/tick) - Shovel rotation
- `THROWN_ITEMS_ROTATION_SPEED_SHIELD` (-π/8 rad/tick) - Shield rotation
- `THROWN_ITEMS_ROTATION_SPEED_DEFAULT_SPEED` (π/32 rad/tick) - Default rotation

### Attack Knockback
- `ATTACK_VELOCITY_GROUNDED_DAMPING_HORIZONTAL` (0.3) - Horizontal damping for grounded targets
- `ATTACK_VELOCITY_GROUNDED_DAMPING_VERTICAL` (0.4) - Vertical damping for grounded targets
- `ATTACK_VELOCITY_KNOCKBACK_VERTICAL_BASE` (0.25 blocks/tick) - Base upward boost
- `ATTACK_VELOCITY_KNOCKBACK_HORIZONTAL_MODIFIER` (0.1) - Horizontal scaling
- `ATTACK_VELOCITY_KNOCKBACK_NORMAL_MULTIPLIER` (0.7) - Overall knockback scaling

### Thrown Item Knockback
- `THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_GROUNDED` (0.7) - Grounded knockback
- `THROWN_DAMAGE_SWORD_AXE_KNOCKBACK_AIRBORNE` (1.0) - Airborne knockback
- `THROWN_DAMAGE_OTHER_KNOCKBACK_MULTIPLIER` (0.7) - Non-sword/axe knockback

For implementation details, see:
- [`ThrownItem.java`](../../src/main/java/btm/sword/system/action/utility/thrown/ThrownItem.java) - Physics simulation
- [`Attack.java`](../../src/main/java/btm/sword/system/attack/Attack.java) - Knockback application
