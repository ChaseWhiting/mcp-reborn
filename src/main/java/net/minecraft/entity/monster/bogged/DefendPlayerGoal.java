package net.minecraft.entity.monster.bogged;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

class DefendPlayerGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private final BoggedEntity boggedEntity;
    @Nullable
    private LivingEntity trustedLastHurtBy;
    private LivingEntity trustedLastHurt;
    private int timestamp;

    public DefendPlayerGoal(BoggedEntity boggedEntity, Class<LivingEntity> p_i50743_2_, boolean p_i50743_3_, boolean p_i50743_4_, @Nullable Predicate<LivingEntity> p_i50743_5_) {
        super(boggedEntity, p_i50743_2_, 10, p_i50743_3_, p_i50743_4_, p_i50743_5_);
        this.boggedEntity = boggedEntity;
    }

    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            for (UUID uuid : boggedEntity.getTrustedPlayer()) {
                if (uuid != null && boggedEntity.level instanceof ServerWorld) {
                    Entity entity = ((ServerWorld) boggedEntity.level).getEntity(uuid);
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingentity = (LivingEntity) entity;
                        this.trustedLastHurt = livingentity;
                        this.trustedLastHurtBy = livingentity.getLastHurtByMob();
                        int i = livingentity.getLastHurtByMobTimestamp();
                        return i != this.timestamp && this.canAttack(this.trustedLastHurtBy, this.targetConditions);
                    }
                }
            }

            return false;
        }
    }

    public void start() {
        this.setTarget(this.trustedLastHurtBy);
        this.target = this.trustedLastHurtBy;
        if (this.trustedLastHurt != null) {
            this.timestamp = this.trustedLastHurt.getLastHurtByMobTimestamp();
        }
        super.start();
    }
}
