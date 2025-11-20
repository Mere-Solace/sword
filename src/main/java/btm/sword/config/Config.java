package btm.sword.config;

import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;

// New Config Setup; Think it'll be a lot nicer than the old, class based one where I wasn't even able to peek the numbers.
@Getter
@Setter // private though, fill that in yourself
public class Config {
    public static class Angles {
        public static float UMBRAL_BLADE_IDLE_PERIOD = (float) Math.PI/8;
    }


    public static class Direction {
        private static final Vector UP = new Vector(0, 1, 0);
        public static Vector UP() { return UP.clone(); }

        private static final Vector DOWN = new Vector(0, -1, 0);
        public static Vector DOWN() { return DOWN.clone(); }

        private static final Vector NORTH = new Vector(0, 0, -1);
        public static Vector NORTH() { return NORTH.clone(); }

        private static final Vector SOUTH = new Vector(0, 0, 1);
        public static Vector SOUTH() { return SOUTH.clone(); }

        private static final Vector OUT_UP = new Vector(0, 1, 1);
        public static Vector OUT_UP() { return OUT_UP.clone(); }

        private static final Vector OUT_DOWN = new Vector(0, -1, 1);
        public static Vector OUT_DOWN() { return OUT_DOWN.clone(); }
    }
}
