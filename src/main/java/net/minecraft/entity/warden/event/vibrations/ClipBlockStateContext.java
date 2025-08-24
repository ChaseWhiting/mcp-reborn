package net.minecraft.entity.warden.event.vibrations;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Predicate;

public class ClipBlockStateContext {
    private final Vector3d from;
    private final Vector3d to;
    private final Predicate<BlockState> block;

    public ClipBlockStateContext(Vector3d vector3D, Vector3d vector32D, Predicate<BlockState> predicate) {
        this.from = vector3D;
        this.to = vector32D;
        this.block = predicate;
    }

    public Vector3d getTo() {
        return this.to;
    }

    public Vector3d getFrom() {
        return this.from;
    }

    public Predicate<BlockState> isTargetBlock() {
        return this.block;
    }
}