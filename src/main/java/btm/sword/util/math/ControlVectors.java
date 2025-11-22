package btm.sword.util.math;

import java.util.function.Function;

import org.bukkit.util.Vector;

public record ControlVectors(Vector start, Vector end, Vector c1, Vector c2) {
    public ControlVectors(Vector start, Vector end, Vector c1, Vector c2) {
        this.start = start;
        this.end = end;
        this.c1 = c1;
        this.c2 = c2;
    }

    public static ControlVectors of(Vector start, Vector end, Vector c1, Vector c2) {
        return new ControlVectors(
            start.clone(),
            end.clone(),
            c1.clone(),
            c2.clone()
        );
    }

    public ControlVectors adjustToBasis(Basis basis, double multiplier) {
        Function<Vector, Vector> adjust = v -> VectorUtil.transformWithNewBasis(basis, v).multiply(multiplier);
        return of(
            adjust.apply(start()),
            adjust.apply(end()),
            adjust.apply(c1()),
            adjust.apply(c2())
        );
    }

    @Override
    public Vector start() {
        return start.clone();
    }

    @Override
    public Vector end() {
        return end.clone();
    }

    @Override
    public Vector c1() {
        return c1.clone();
    }

    @Override
    public Vector c2() {
        return c2.clone();
    }
}
