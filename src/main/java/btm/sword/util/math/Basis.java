package btm.sword.util.math;

import java.util.List;

import org.bukkit.util.Vector;

// TODO: Implement new, shiny basis class of my own imagining! I miss simplicity...
public class Basis {
    private final Vector right;
    private final Vector up;
    private final Vector forward;

    public Basis(Vector right, Vector up, Vector forward) {
        this.right = right.normalize();
        this.up = up.normalize();
        this.forward = forward.normalize();
    }

    public Basis(List<Vector> legacyBasis) {
        assert legacyBasis.size() == 3;

        this.right = legacyBasis.getFirst().normalize();
        this.up = legacyBasis.get(1).normalize();
        this.forward = legacyBasis.getLast().normalize();
    }

    public Vector right() {
        return right.clone();
    }

    public Vector up() {
        return up.clone();
    }

    public Vector forward() {
        return forward.clone();
    }
}
