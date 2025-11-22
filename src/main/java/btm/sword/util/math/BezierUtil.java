package btm.sword.util.math;

import java.util.function.Function;

import org.bukkit.util.Vector;

/**
 * Utility class for working with Bézier curves and control vectors in 3D space.
 * Methods are designed for generating and manipulating {@link Vector} points that
 * follow cubic Bézier curves or adjusted bases for spatial computation or animation.
 * <p>
 * Used to interpolate 3D paths, transitions, or geometric forms within the Sword plugin.
 * </p>
 */
public class BezierUtil {
    /**
     * Constructs a cubic Bézier curve function in 3D space.
     * Returns a function mapping parameter {@code t} (from 0 to 1) to a point on the curve
     * using the given start/end points and two control points.
     *
     * @param start the starting {@link Vector} of the curve
     * @param end the ending {@link Vector} of the curve
     * @param c1 the first control {@link Vector}
     * @param c2 the second control {@link Vector}
     * @return a function from {@code t} to a {@link Vector} point on the curve
     */
    public static Function<Double, Vector> cubicBezier3D(Vector start, Vector end, Vector c1, Vector c2) {
        return t -> {
            double t2 = t*t;
            double t3 = t*t2;
            double mt = 1-t;
            double mt2 = mt*mt;
            double mt3 = mt*mt2;

            Vector p0 = start.clone().multiply(mt3);
            Vector p1 = c1.clone().multiply(3*mt2*t);
            Vector p2 = c2.clone().multiply(3*mt*t2);
            Vector p3 = end.clone().multiply(t3);

            // performed 4 multiplications, 4 scaler multiplications of vectors, and then 3 vector additions.
            // Not extremely intensive.

            return p0.add(p1).add(p2).add(p3);
        };
    }

    public static Function<Double, Vector> cubicBezier3D(ControlVectors ctrl) {
        return t -> {
            double t2 = t*t;
            double t3 = t*t2;
            double mt = 1-t;
            double mt2 = mt*mt;
            double mt3 = mt*mt2;

            Vector p0 = ctrl.start().multiply(mt3);
            Vector p1 = ctrl.c1().multiply(3*mt2*t);
            Vector p2 = ctrl.c2().multiply(3*mt*t2);
            Vector p3 = ctrl.end().multiply(t3);

            return p0.add(p1).add(p2).add(p3);
        };
    }
}
