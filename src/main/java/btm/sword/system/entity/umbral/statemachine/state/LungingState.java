package btm.sword.system.entity.umbral.statemachine.state;

import org.bukkit.Color;

import btm.sword.system.attack.AttackType;
import btm.sword.system.entity.umbral.UmbralBlade;
import btm.sword.system.entity.umbral.statemachine.UmbralStateFacade;

// TODO: n # of lunges allowed before returning. Still want to keep combat centered around the player.
// Also, make umbral attacks consume soulfire, and have a lunge slash too that doesn't impale.
public class LungingState extends UmbralStateFacade {
    @Override
    public String name() {
        return "LUNGING";
    }

    @Override
    public void onEnter(UmbralBlade blade) {
        blade.setHitEntity(null);
        blade.setFinishedLunging(false);
        blade.setTimeCutoff(1.2);
        blade.setTimeScalingFactor(9);
        blade.setCtrlPointsForLunge(AttackType.LUNGE1.controlVectors());
        blade.onRelease(3);

        blade.getDisplay().setGlowing(true);
        blade.getDisplay().setGlowColorOverride(Color.fromRGB(1, 1, 1));
    }

    @Override
    public void onExit(UmbralBlade blade) {
        blade.setFinishedLunging(false);
        blade.getDisplay().setGlowing(false);
    }

    @Override
    public void onTick(UmbralBlade blade) {

    }
}
