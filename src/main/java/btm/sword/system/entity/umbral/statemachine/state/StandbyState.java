package btm.sword.system.entity.umbral.statemachine.state;

import org.bukkit.Color;
import org.bukkit.scheduler.BukkitTask;

import btm.sword.system.entity.types.SwordPlayer;
import btm.sword.system.entity.umbral.UmbralBlade;
import btm.sword.system.entity.umbral.statemachine.UmbralStateFacade;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class StandbyState extends UmbralStateFacade {
    private BukkitTask followTask;

    @Override
    public String name() { return "STANDBY"; }

    @Override
    public void onEnter(UmbralBlade blade) {
        blade.getDisplay().setGlowing(true);
        blade.getDisplay().setGlowColorOverride(Color.fromRGB(1, 1, 1));

        followTask = blade.hoverBehindWielder();
        blade.startIdleMovement();
        if (blade.getThrower() instanceof SwordPlayer swordPlayer) {
            swordPlayer.displayTitle(null, Component.text("< Returned >").color(TextColor.color(140, 0, 0)),
                50, 500, 50);
        }
    }

    @Override
    public void onExit(UmbralBlade blade) {
        blade.getDisplay().setGlowing(false);

        blade.endIdleMovement();
        if (followTask != null && followTask.getTaskId() != -1 && !followTask.isCancelled())
            followTask.cancel();
    }

    // TODO: move idle movement into this onTick method!
    @Override
    public void onTick(UmbralBlade blade) {
        // Idle movement handled by BukkitRunnable; tick may monitor attack triggers
    }
}
