package btm.sword.system.entity.umbral.statemachine.state;


import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import btm.sword.system.entity.umbral.UmbralBlade;
import btm.sword.system.entity.umbral.input.BladeRequest;
import btm.sword.system.entity.umbral.statemachine.UmbralStateFacade;

// TODO: while recalling or returning, allow for dashing to the blade.
public class ReturningState extends UmbralStateFacade {
    private Location previousBladeLocation;
    private int t = 0;
    private int stationaryCount = 0;
    private static final double EPS_SQ = 0.0004; // tune: 0.02^2  (very small)
    private static final int REQUIRED_STATIONARY_TICKS = 3;

    private BukkitTask returnTask;

    @Override
    public String name() {
        return "RETURNING";
    }

    @Override
    public void onEnter(UmbralBlade blade) {
        blade.getDisplay().setGlowing(true);
        blade.getDisplay().setGlowColorOverride(Color.fromRGB(1, 1, 1));

        previousBladeLocation = blade.getDisplay().getLocation();
        returnTask = blade.returnToWielderAndRequestState(BladeRequest.STANDBY);
    }

    @Override
    public void onExit(UmbralBlade blade) {
        blade.getDisplay().setGlowing(false);

        if (returnTask != null && !returnTask.isCancelled() && returnTask.getTaskId() != -1) {
            returnTask.cancel();
        }
    }

    @Override
    public void onTick(UmbralBlade blade) {
        t++;

        // wait initial grace period for return animation to run
        if (t <= 15) return;

        Location cur = blade.getDisplay().getLocation();

        if (previousBladeLocation == null) {
            previousBladeLocation = cur.clone();
            stationaryCount = 0;
            return;
        }

        Vector delta = cur.toVector().clone().subtract(previousBladeLocation.toVector());
        if (delta.lengthSquared() < EPS_SQ) {
            stationaryCount++;
            if (stationaryCount >= REQUIRED_STATIONARY_TICKS) {
                blade.request(BladeRequest.STANDBY);
            }
        } else {
            stationaryCount = 0;
        }

        previousBladeLocation = cur.clone();
    }
}
