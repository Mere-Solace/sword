package btm.sword.system.action;

import btm.sword.system.entity.types.Combatant;
import btm.sword.system.entity.umbral.UmbralBlade;
import btm.sword.system.entity.umbral.input.BladeRequest;
import btm.sword.system.entity.umbral.statemachine.state.LodgedState;
import btm.sword.system.entity.umbral.statemachine.state.WieldState;

public class UmbralBladeAction extends SwordAction {
    // TODO: #122 - Wielding when not holding blade should attack
    public static void wield(Combatant wielder) {
        UmbralBlade blade = wielder.getUmbralBlade();
        if (blade == null) return;

        if (wielder.holdingUmbralItemInMainHand()) {
            if (blade.inState(WieldState.class)) {
                blade.request(BladeRequest.ATTACK_QUICK);
            }
            blade.request(BladeRequest.WIELD);
        }
        else {
            blade.request(BladeRequest.ATTACK_QUICK);
        }
    }

    public static void toggle(Combatant wielder) {
        UmbralBlade blade = wielder.getUmbralBlade();
        if (blade == null) return;

        blade.request(BladeRequest.TOGGLE);
    }

    public static void lunge(Combatant wielder) {
        UmbralBlade blade = wielder.getUmbralBlade();
        if (blade == null) return;

        if (blade.inState(LodgedState.class)) {
            blade.request(BladeRequest.RECALL);
        }
        else {
            blade.request(BladeRequest.LUNGE);
        }
    }

    public static void recall(Combatant wielder) {
        UmbralBlade blade = wielder.getUmbralBlade();
        if (blade == null) return;

        blade.request(BladeRequest.RECALL);
    }
}
