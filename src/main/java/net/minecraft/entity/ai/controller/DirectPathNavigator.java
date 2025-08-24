package net.minecraft.entity.ai.controller;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Mob;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.world.World;

public class DirectPathNavigator extends GroundPathNavigator {

    private final Mob mob;
    private float yMobOffset = 0;

    public DirectPathNavigator(Mob mob, World world) {
        this(mob, world, 0);
    }

    public DirectPathNavigator(Mob mob, World world, float yMobOffset) {
        super(mob, world);
        this.mob = mob;
        this.yMobOffset = yMobOffset;
    }

    public void tick() {
        ++this.tick;
    }

    public boolean moveTo(double x, double y, double z, double speedIn) {
        mob.getMoveControl().setWantedPosition(x, y, z, speedIn);
        return true;
    }

    public boolean moveTo(Entity entityIn, double speedIn) {
        mob.getMoveControl().setWantedPosition(entityIn.getX(), entityIn.getY() + yMobOffset, entityIn.getZ(), speedIn);
        return true;
    }

}