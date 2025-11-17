package btm.sword.system.entity.umbral.statemachine.state;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import btm.sword.Sword;
import btm.sword.system.entity.umbral.UmbralBlade;
import btm.sword.system.entity.umbral.input.BladeRequest;
import btm.sword.system.entity.umbral.statemachine.UmbralStateFacade;

/**
 * State where the UmbralBlade is being recalled to the wielder.
 * <p>
 * In this state, the blade is traveling back to the player, typically after
 * being thrown, lodged in an enemy, or left in a waiting state.
 * </p>
 * <p>
 * <b>Entry Actions:</b>
 * <ul>
 *   <li>Set display transformation for returning animation</li>
 *   <li>Stop idle movement</li>
 *   <li>Begin lerp movement back to wielder</li>
 * </ul>
 * </p>
 * <p>
 * <b>Typical Transitions:</b>
 * <ul>
 *   <li>RECALLING → SHEATHED (when blade arrives)</li>
 * </ul>
 * </p>
 *
 */
public class RecallingState extends UmbralStateFacade {
    private Location previousBladeLocation;
    private int t = 0;
    private int stationaryCount = 0;
    private static final double EPS_SQ = 0.0004; // tune: 0.02^2  (very small)
    private static final int REQUIRED_STATIONARY_TICKS = 3;

    @Override
    public String name() {
        return "RECALLING";
    }

    @Override
    public void onEnter(UmbralBlade blade) {
        previousBladeLocation = blade.getDisplay().getLocation();
        new BukkitRunnable() {
            @Override
            public void run() {
                blade.returnToWielderAndRequestState(BladeRequest.STANDBY);
            }
        }.runTaskLater(Sword.getInstance(), 10);
    }

    @Override
    public void onExit(UmbralBlade blade) { }

    @Override
    public void onTick(UmbralBlade blade) {
        t++;

        // wait initial grace period for return animation to run
        if (t <= 15) return;

        Location nowLoc = blade.getDisplay().getLocation();

        if (previousBladeLocation == null) {
            previousBladeLocation = nowLoc.clone();
            stationaryCount = 0;
            return;
        }

        Vector delta = nowLoc.toVector().clone().subtract(previousBladeLocation.toVector());
        if (delta.lengthSquared() < EPS_SQ) {
            stationaryCount++;
            if (stationaryCount >= REQUIRED_STATIONARY_TICKS) {
                blade.request(BladeRequest.STANDBY);
            }
        } else {
            stationaryCount = 0;
        }

        // update previous each tick so delta is between adjacent samples
        previousBladeLocation = nowLoc.clone();
    }
}
