package btm.sword.system.entity.umbral.statemachine.state;


import org.bukkit.Location;
import org.bukkit.util.Vector;

import btm.sword.system.entity.umbral.UmbralBlade;
import btm.sword.system.entity.umbral.input.BladeRequest;
import btm.sword.system.entity.umbral.statemachine.UmbralStateFacade;

/**
 * State where the UmbralBlade is automatically returning to sheath.
 * <p>
 * Similar to RECALLING but triggered automatically when the blade has been
 * idle for too long or the wielder moves too far away.
 * </p>
 * <p>
 * <b>Entry Actions:</b>
 * <ul>
 *   <li>Set display transformation for return animation</li>
 *   <li>Stop idle movement</li>
 *   <li>Begin automatic return to sheath</li>
 * </ul>
 * </p>
 * <p>
 * <b>Typical Transitions:</b>
 * <ul>
 *   <li>RETURNING → SHEATHED (when return completes)</li>
 * </ul>
 * </p>
 *
 */
public class ReturningState extends UmbralStateFacade {
    private Location previousBladeLocation;
    private int t = 0;
    private int stationaryCount = 0;
    private static final double EPS_SQ = 0.0004; // tune: 0.02^2  (very small)
    private static final int REQUIRED_STATIONARY_TICKS = 3;

    @Override
    public String name() {
        return "RETURNING";
    }

    @Override
    public void onEnter(UmbralBlade blade) {
        blade.returnToWielderAndRequestState(BladeRequest.STANDBY);
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
