package net.minecraft.entity;

import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public enum EntityAttachment {
    PASSENGER(Fallback.AT_HEIGHT),
    VEHICLE(Fallback.AT_FEET),
    NAME_TAG(Fallback.AT_HEIGHT),
    WARDEN_CHEST(Fallback.AT_CENTER);

    private final Fallback fallback;

    private EntityAttachment(Fallback fallback) {
        this.fallback = fallback;
    }

    public List<Vector3d> createFallbackPoints(float f, float f2) {
        return this.fallback.create(f, f2);
    }

    public static interface Fallback {
        public static final List<Vector3d> ZERO = List.of(Vector3d.ZERO);
        public static final Fallback AT_FEET = (f, f2) -> ZERO;
        public static final Fallback AT_HEIGHT = (f, f2) -> List.of(new Vector3d(0.0, f2, 0.0));
        public static final Fallback AT_CENTER = (f, f2) -> List.of(new Vector3d(0.0, (double)f2 / 2.0, 0.0));

        public List<Vector3d> create(float var1, float var2);
    }
}

