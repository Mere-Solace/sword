package btm.sword.util.math;

import org.bukkit.util.Vector;

public record Basis(Vector right, Vector up, Vector forward) {
    public Basis(Vector right, Vector up, Vector forward) {
        this.right = right.normalize();
        this.up = up.normalize();
        this.forward = forward.normalize();
    }

    @Override
    public Vector right() {
        return right.clone();
    }

    @Override
    public Vector up() {
        return up.clone();
    }

    @Override
    public Vector forward() {
        return forward.clone();
    }
}
