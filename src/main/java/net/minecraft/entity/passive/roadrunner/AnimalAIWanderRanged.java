package net.minecraft.entity.passive.roadrunner;

import net.minecraft.entity.Creature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class AnimalAIWanderRanged extends RandomWalkingGoal {
    protected final float probability;
    protected final int xzRange;
    protected final int yRange;

    public AnimalAIWanderRanged(Creature creature, int chance, double speedIn, int xzRange, int yRange) {
        this(creature, chance, speedIn, 0.001F, xzRange, yRange);
    }

    public boolean canUse() {
        {
            if (!this.forceTrigger) {
                if ( this.mob.getNoActionTime() >= 100) {
                    return false;
                }

                if (this.mob.getRandom().nextInt(this.interval) != 0) {
                    return false;
                }
            }

            Vector3d lvt_1_1_ = this.getPosition();
            if (lvt_1_1_ == null) {
                return false;
            } else {
                this.wantedX = lvt_1_1_.x;
                this.wantedY = lvt_1_1_.y;
                this.wantedZ = lvt_1_1_.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    public AnimalAIWanderRanged(Creature creature, int chance, double speedIn, float probabilityIn, int xzRange, int yRange) {
        super(creature, speedIn, chance);
        this.probability = probabilityIn;
        this.xzRange = xzRange;
        this.yRange = yRange;
    }

    @Nullable
    protected Vector3d getPosition() {
        if (this.mob.isInWaterOrBubble()) {
            Vector3d vector3d = RandomPositionGenerator.getLandPos(this.mob, xzRange, yRange);
            return vector3d == null ? super.getPosition() : vector3d;
        } else {
            return this.mob.getRandom().nextFloat() >= this.probability ? RandomPositionGenerator.getLandPos(this.mob, xzRange, yRange) : super.getPosition();
        }
    }
}