package btm.sword.util.math;

import java.util.List;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

// TODO: Implement this everywhere
public class BezierCurve {
    private final Vector start;
    private final Vector c1;
    private final Vector c2;
    private final Vector end;

    private Vector adjStart;
    private Vector adjC1;
    private Vector adjC2;
    private Vector adjEnd;

    private Function<Double, Vector> curveFunction;

    private Basis basis;

    public BezierCurve(Vector start, Vector c1, Vector c2, Vector end) {
        this.start = start;
        this.c1 = c1;
        this.c2 = c2;
        this.end = end;
    }

    /**
     * Generates basis, adjusts control points to basis, and creates the bezier function.
     *
     * @param attackingEntity    entity providing orientation
     * @param orientWithPitch    whether to include pitch in basis
     * @param rangeMultiplier    scaling for control point transformation
     */
    public void generate(Entity attackingEntity, boolean orientWithPitch, double rangeMultiplier) {
        Location eye = attackingEntity.getLocation();
        if (orientWithPitch) {
            this.basis = VectorUtil.getBasis(eye, eye.getDirection());
        } else {
            this.basis = VectorUtil.getBasisWithoutPitch(attackingEntity);
        }

        List<Vector> adjusted = BezierUtil.adjustCtrlToBasis(
            basis,
            List.of(start, c1, c2, end),
            rangeMultiplier
        );

        this.adjStart = adjusted.get(0);
        this.adjC1 = adjusted.get(1);
        this.adjC2 = adjusted.get(2);
        this.adjEnd = adjusted.get(3);

        this.curveFunction = BezierUtil.cubicBezier3D(adjStart, adjC1, adjC2, adjEnd);
    }

    public Vector evaluate(double t) {
        if (curveFunction == null)
            throw new IllegalStateException("BezierCurve not generated yet.");
        return curveFunction.apply(t);
    }

    public Vector getRight() { return basis.right(); }
    public Vector getUp() { return basis.up(); }
    public Vector getForward() { return basis.forward(); }

    public Vector getAdjustedStart() { return adjStart.clone(); }
    public Vector getAdjustedC1() { return adjC1.clone(); }
    public Vector getAdjustedC2() { return adjC2.clone(); }
    public Vector getAdjustedEnd() { return adjEnd.clone(); }
}
