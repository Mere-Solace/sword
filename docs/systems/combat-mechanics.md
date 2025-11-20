# Combat Mechanics

This document explains the combat systems in Sword: Combat Evolved, including damage calculations, hitbox detection, attack state machine, impalement mechanics, and the combat profile system.

## Table of Contents

1. [Damage System](#damage-system)
2. [Hitbox Detection](#hitbox-detection)
3. [Attack State Machine](#attack-state-machine)
4. [Impalement Mechanics](#impalement-mechanics)
5. [Combat Profile System](#combat-profile-system)
6. [Configuration Reference](#configuration-reference)

---

## Damage System

### Base Damage

All attacks start with a base damage value:

```
base_damage = Config.Combat.ATTACKS_BASE_DAMAGE  // 20.0 HP (10 hearts)
```

This is modified by:
1. Attack type multipliers
2. Range multipliers
3. Combo progression
4. Target armor/toughness

### Attack Type Damage

Different attack types apply damage multipliers:

```
final_damage = base_damage * attack_type_multiplier * range_multiplier
```

#### Range Multipliers

Attacks have range-based damage scaling:

| Attack Type | Range Multiplier | Effective Damage |
|-------------|------------------|------------------|
| Basic 1     | 1.4              | 28.0 HP (14 hearts) |
| Basic 2     | 1.4              | 28.0 HP (14 hearts) |
| Basic 3     | 1.4              | 28.0 HP (14 hearts) |
| Neutral Air | 1.3              | 26.0 HP (13 hearts) |
| Down Air    | 1.2              | 24.0 HP (12 hearts) |

**Design**: Basic attacks reward consistency, aerial attacks trade damage for mobility.

### Thrown Item Damage

Thrown items use a **velocity-based damage model**:

```
thrown_damage = (
    base_thrown_damage +
    (item_velocity * velocity_multiplier) *
    sword_damage_multiplier
)

Where:
    base_thrown_damage = Config.Combat.THROWN_DAMAGE_BASE_THROWN_DAMAGE  // 12.0 HP
    velocity_multiplier = Config.Combat.THROWN_DAMAGE_ITEM_VELOCITY_MULTIPLIER  // 1.5
    sword_damage_multiplier = Config.Combat.THROWN_DAMAGE_SWORD_DAMAGE_MULTIPLIER  // 1.0
```

**Example Calculation**:
```
velocity = 3.0 blocks/tick
thrown_damage = 12.0 + (3.0 * 1.5) * 1.0
              = 12.0 + 4.5
              = 16.5 HP (8.25 hearts)
```

### Armor Interactions

The combat system uses a **custom armor model** with three layers:

#### 1. Shards (First Layer)
```
shards_damage = attack_shards_cost
remaining_shards -= shards_damage

if (remaining_shards < 0):
    overflow to toughness layer
```

Default shard cost:
- Sword/Axe throws: 2 shards
- Other item throws: 2 shards
- Toss hits: 2 shards

#### 2. Toughness (Second Layer)
```
toughness_damage = attack_toughness_damage
remaining_toughness -= toughness_damage

if (remaining_toughness < 0):
    overflow to health
```

Toughness damage by attack type:
- Sword/Axe throws: 75.0 HP
- Other item throws: 75.0 HP
- Toss hits: 30.0 HP

#### 3. Health (Final Layer)
```
health_damage = final_attack_damage - (shards_absorbed + toughness_absorbed)
remaining_health -= health_damage
```

### Soulfire Reduction

All attacks reduce the target's soulfire resource:

```
soulfire -= soulfire_reduction_cost
```

Soulfire costs by attack type:
- Sword/Axe throws: 50.0 points
- Other item throws: 50.0 points
- Toss hits: 5.0 points

---

## Hitbox Detection

### Hitbox Shapes

The combat system uses **oriented bounding boxes (OBBs)** for hit detection:

```
hitbox = OrientedBoundingBox(
    center = attacker_eye_location + (facing_direction * reach / 2),
    dimensions = (reach, width, height),
    orientation = attacker_facing_direction
)
```

### Hitbox Dimensions

Different attacks use different hitbox sizes:

#### Basic Attack Hitbox
```
reach = Config.Combat.HITBOXES_BASIC_REACH  // 1.5 blocks
width = Config.Combat.HITBOXES_BASIC_WIDTH  // 1.5 blocks
height = Config.Combat.HITBOXES_BASIC_HEIGHT  // 1.5 blocks

Volume = 1.5 × 1.5 × 1.5 = 3.375 cubic blocks
```

#### Down Air Attack Hitbox
```
reach = Config.Combat.HITBOXES_DOWN_AIR_REACH  // 1.6 blocks
width = Config.Combat.HITBOXES_DOWN_AIR_WIDTH  // 1.4 blocks
height = Config.Combat.HITBOXES_DOWN_AIR_HEIGHT  // 2.5 blocks

Volume = 1.6 × 1.4 × 2.5 = 5.6 cubic blocks
```

**Design**: Down air has 60% larger hitbox with extended vertical reach for aerial combat.

### Secant Radius

For fine collision detection, the system uses a **secant sphere test**:

```
secant_radius = Config.Combat.HITBOXES_SECANT_RADIUS  // 0.4 blocks
```

This provides:
1. Initial OBB test (fast, broad phase)
2. Secant sphere test (precise, narrow phase)
3. Final entity bounds check

### Exempt Entities

Certain entities are exempt from combat:

```java
exempt_entities = [
    "ARMOR_STAND", "ITEM_FRAME", "GLOW_ITEM_FRAME", "PAINTING",
    "ITEM_DISPLAY", "BLOCK_DISPLAY", "TEXT_DISPLAY", "INTERACTION"
]
```

These are purely decorative and should not be damageable.

---

## Attack State Machine

### Attack States

The attack system uses a state machine with the following states:

```
IDLE → CASTING → EXECUTING → RECOVERING → IDLE
```

#### IDLE State
- Waiting for attack input
- Can transition to CASTING

#### CASTING State
```
duration = Config.Combat.ATTACKS_CAST_TIMING_MIN_DURATION  // 1 tick (minimum)
         to Config.Combat.ATTACKS_CAST_TIMING_MAX_DURATION  // 3 ticks (maximum)
```

Cast duration decreases with combo count:
```
cast_duration = max_duration - (combo_count * reduction_rate)
              = 3 - (combo_count * 0.2)
```

| Combo Count | Cast Duration |
|-------------|---------------|
| 0           | 3 ticks       |
| 1           | 2.8 ticks     |
| 2           | 2.6 ticks     |
| 3           | 2.4 ticks     |
| 4           | 2.2 ticks     |
| 5+          | 2.0 ticks     |

**Design**: Rewards combo consistency with faster attacks.

#### EXECUTING State
```
duration = Config.Combat.ATTACKS_DURATION_MULTIPLIER * attack_speed
         = 500ms * attack_speed
```

Attack iterations:
```
iterations = Config.Combat.ATTACK_CLASS_TIMING_ATTACK_ITERATIONS  // 5

progress_per_iteration = (end_value - start_value) / iterations
                       = (1.0 - 0.0) / 5
                       = 0.2 (20% per iteration)
```

Hitbox detection is performed at **each iteration**, allowing for multiple hits per attack.

#### RECOVERING State
- Cannot perform new attacks
- Can transition to CASTING if input buffered

### Combo System

Combos are maintained through a timing window:

```
combo_window = Config.Timing.ATTACKS_COMBO_WINDOW_BASE  // 3 ticks
```

If a new attack is initiated within the combo window:
```
combo_count++
```

Otherwise:
```
combo_count = 0
```

### Down Air Detection

Down air attacks are triggered based on player velocity:

```
down_air_threshold = Config.Combat.ATTACKS_DOWN_AIR_THRESHOLD  // -0.85

dot_product = normalize(player_velocity) · DOWN_VECTOR

if (dot_product < down_air_threshold):
    trigger_down_air_attack()
```

Where `dot_product = -0.85` means the player must be moving at least 85% downward.

---

## Impalement Mechanics

### Impalement System

Impalement occurs when a thrown item pins an entity:

```
if (item_collides_with_entity && velocity_high_enough):
    impale(entity, item)
```

### Damage Over Time

Impaled entities take periodic damage:

```
damage_per_tick = Config.Combat.IMPALEMENT_DAMAGE_PER_TICK  // 2.0 HP
ticks_between_damage = Config.Combat.IMPALEMENT_TICKS_BETWEEN_DAMAGE  // 10 ticks

DPS = damage_per_tick / ticks_between_damage * 20
    = 2.0 / 10 * 20
    = 4.0 HP/second
```

### Maximum Impalements

Each entity can be impaled by multiple items:

```
max_impalements = Config.Combat.IMPALEMENT_MAX_IMPALEMENTS  // 3
```

After reaching the limit, new impalements replace the oldest.

### Head Detection

Head impalements may cause entities to "look at" the item:

```
head_zone_ratio = Config.Combat.IMPALEMENT_HEAD_ZONE_RATIO  // 0.8

head_zone_height = entity_height * head_zone_ratio
                 = entity_height * 0.8

if (impale_y > entity_y + head_zone_height):
    force_look_at(item_location)
```

**Exceptions**: Some entities don't have distinct heads:
```java
head_follow_exceptions = [EntityType.SPIDER]
```

### Pin Checking

Impaled items check for ground collision:

```
check_interval = Config.Combat.IMPALEMENT_PIN_CHECK_INTERVAL  // 2 ticks
max_iterations = Config.Combat.IMPALEMENT_PIN_MAX_ITERATIONS  // 50

total_pin_duration = check_interval * max_iterations
                   = 2 * 50
                   = 100 ticks (5 seconds)
```

After max iterations, the item is automatically removed.

---

## Combat Profile System

### Combat Profile Structure

Each combat entity has a profile with four aspects:

```java
profile = {
    shards: Aspect,     // Defensive resource
    toughness: Aspect,  // Damage reduction
    soulfire: Aspect,   // Ability resource
    form: Aspect        // Technique points
}
```

### Aspect System

Each aspect has:
- **Current Value**: Current amount
- **Max Value**: Maximum capacity
- **Regen Period**: Ticks between regeneration
- **Regen Amount**: Amount restored per period

### Shards

Defensive resource depleted by incoming attacks:

```
current = Config.Entity.COMBAT_PROFILE_SHARDS_CURRENT  // 10.0
regen_period = Config.Entity.COMBAT_PROFILE_SHARDS_REGEN_PERIOD  // 50 ticks (2.5s)
regen_amount = Config.Entity.COMBAT_PROFILE_SHARDS_REGEN_AMOUNT  // 1.0

Regen Rate = regen_amount / regen_period * 20
           = 1.0 / 50 * 20
           = 0.4 shards/second
```

### Toughness

Damage reduction layer:

```
current = Config.Entity.COMBAT_PROFILE_TOUGHNESS_CURRENT  // 20.0 HP
regen_period = Config.Entity.COMBAT_PROFILE_TOUGHNESS_REGEN_PERIOD  // 20 ticks (1s)
regen_amount = Config.Entity.COMBAT_PROFILE_TOUGHNESS_REGEN_AMOUNT  // 0.5 HP

Regen Rate = 0.5 / 20 * 20 = 0.5 HP/second
```

### Soulfire

Special ability resource:

```
current = Config.Entity.COMBAT_PROFILE_SOULFIRE_CURRENT  // 100.0 points
regen_period = Config.Entity.COMBAT_PROFILE_SOULFIRE_REGEN_PERIOD  // 5 ticks (0.25s)
regen_amount = Config.Entity.COMBAT_PROFILE_SOULFIRE_REGEN_AMOUNT  // 0.2 points

Regen Rate = 0.2 / 5 * 20 = 0.8 points/second
```

### Form

Combat stance/technique points:

```
current = Config.Entity.COMBAT_PROFILE_FORM_CURRENT  // 10.0 points
regen_period = Config.Entity.COMBAT_PROFILE_FORM_REGEN_PERIOD  // 60 ticks (3s)
regen_amount = Config.Entity.COMBAT_PROFILE_FORM_REGEN_AMOUNT  // 1.0 point

Regen Rate = 1.0 / 60 * 20 = 0.33 points/second
```

### Regen Summary Table

| Aspect    | Current | Regen Period | Regen Amount | Regen Rate/Second |
|-----------|---------|--------------|--------------|-------------------|
| Shards    | 10.0    | 50 ticks     | 1.0          | 0.4/s             |
| Toughness | 20.0 HP | 20 ticks     | 0.5 HP       | 0.5 HP/s          |
| Soulfire  | 100.0   | 5 ticks      | 0.2          | 0.8/s             |
| Form      | 10.0    | 60 ticks     | 1.0          | 0.33/s            |

---

## Configuration Reference

All combat mechanics are configured via [`Config.Combat`](../../src/main/java/btm/sword/config/Config.java) and [`Config.Entity`](../../src/main/java/btm/sword/config/Config.java):

### Base Combat
- `ATTACKS_BASE_DAMAGE` (20.0 HP) - Base attack damage
- `ATTACKS_DOWN_AIR_THRESHOLD` (-0.85) - Downward velocity threshold
- `ATTACKS_CAST_TIMING_MIN_DURATION` (1 tick) - Minimum cast time
- `ATTACKS_CAST_TIMING_MAX_DURATION` (3 ticks) - Maximum cast time
- `ATTACKS_CAST_TIMING_REDUCTION_RATE` (0.2 ticks) - Cast time reduction per combo
- `ATTACKS_DURATION_MULTIPLIER` (500 ms) - Attack duration scaling

### Hitboxes
- `HITBOXES_BASIC_REACH/WIDTH/HEIGHT` (1.5 blocks) - Basic attack dimensions
- `HITBOXES_DOWN_AIR_REACH` (1.6 blocks) - Down air reach
- `HITBOXES_DOWN_AIR_WIDTH` (1.4 blocks) - Down air width
- `HITBOXES_DOWN_AIR_HEIGHT` (2.5 blocks) - Down air height
- `HITBOXES_SECANT_RADIUS` (0.4 blocks) - Precise collision sphere

### Impalement
- `IMPALEMENT_DAMAGE_PER_TICK` (2.0 HP) - DoT damage
- `IMPALEMENT_TICKS_BETWEEN_DAMAGE` (10 ticks) - Damage interval
- `IMPALEMENT_MAX_IMPALEMENTS` (3) - Max items per entity
- `IMPALEMENT_HEAD_ZONE_RATIO` (0.8) - Head detection threshold
- `IMPALEMENT_PIN_MAX_ITERATIONS` (50) - Max pin duration
- `IMPALEMENT_PIN_CHECK_INTERVAL` (2 ticks) - Pin check frequency

### Combat Profile
- `PLAYER_BASE_HEALTH` (100.0 HP) - Player starting health
- `PLAYER_BASE_TOUGHNESS` (20.0 HP) - Player starting toughness
- `PLAYER_BASE_SOULFIRE` (100.0 points) - Player starting soulfire
- `COMBAT_PROFILE_*_CURRENT` - Starting values for each aspect
- `COMBAT_PROFILE_*_REGEN_PERIOD` - Regeneration intervals
- `COMBAT_PROFILE_*_REGEN_AMOUNT` - Regeneration amounts

For implementation details, see:
- [`Attack.java`](../../src/main/java/btm/sword/system/attack/Attack.java) - Attack execution and damage
- [`AttackAction.java`](../../src/main/java/btm/sword/system/action/AttackAction.java) - Attack state machine
- [`CombatProfile.java`](../../src/main/java/btm/sword/system/entity/base/CombatProfile.java) - Combat stat management
