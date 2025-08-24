
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TargetingConditions;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.goat.GoatAi;
import net.minecraft.entity.goat.GoatEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;


public class RamTarget
extends Task<GoatEntity> {
    public static final int TIME_OUT_DURATION = 200;
    public static final float RAM_SPEED_FORCE_FACTOR = 1.65f;
    private final Function<GoatEntity, UniformInt> getTimeBetweenRams;
    private final TargetingConditions ramTargeting;
    private final float speed;
    private final ToDoubleFunction<GoatEntity> getKnockbackForce;
    private Vector3d ramDirection;
    private final Function<GoatEntity, SoundEvent> getImpactSound;
    private final Function<GoatEntity, SoundEvent> getHornBreakSound;

    public RamTarget(Function<GoatEntity, UniformInt> function, TargetingConditions targetingConditions, float f, ToDoubleFunction<GoatEntity> toDoubleFunction, Function<GoatEntity, SoundEvent> function2, Function<GoatEntity, SoundEvent> function3) {
        super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryModuleStatus.VALUE_PRESENT), 200);
        this.getTimeBetweenRams = function;
        this.ramTargeting = targetingConditions;
        this.speed = f;
        this.getKnockbackForce = toDoubleFunction;
        this.getImpactSound = function2;
        this.getHornBreakSound = function3;
        this.ramDirection = Vector3d.ZERO;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, GoatEntity goat) {
        return goat.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, GoatEntity goat, long l) {
        return goat.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected void start(ServerWorld serverLevel, GoatEntity goat, long l) {
        BlockPos blockPos = goat.blockPosition();
        Brain<GoatEntity> brain = (Brain<GoatEntity>) goat.getBrain();
        Vector3d vec3 = brain.getMemory(MemoryModuleType.RAM_TARGET).get();
        this.ramDirection = new Vector3d((double)blockPos.getX() - vec3.x(), 0.0, (double)blockPos.getZ() - vec3.z()).normalize();
        brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, this.speed, 0));
    }

    @Override
    protected void tick(ServerWorld serverLevel, GoatEntity goat, long l) {
        List<LivingEntity> list = serverLevel.getNearbyEntities(LivingEntity.class, this.ramTargeting, goat, goat.getBoundingBox());
        Brain<GoatEntity> brain = (Brain<GoatEntity>) goat.getBrain();
        if (!list.isEmpty()) {
            LivingEntity livingEntity = list.get(0);
            livingEntity.hurt(DamageSource.mobAttack(goat), (float)goat.getAttributeValue(Attributes.ATTACK_DAMAGE));
            int n = goat.hasEffect(Effects.MOVEMENT_SPEED) ? goat.getEffect(Effects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
            int n2 = goat.hasEffect(Effects.MOVEMENT_SLOWDOWN) ? goat.getEffect(Effects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
            float f = 0.25f * (float)(n - n2);
            float f2 = MathHelper.clamp(goat.getSpeed() * 1.65f, 0.2f, 3.0f) + f;
            float f3 = livingEntity.isDamageSourceBlocked(DamageSource.mobAttack(goat)) ? 0.5f : 1.0f;
            livingEntity.knockback((float) ((double)(f3 * f2) * this.getKnockbackForce.applyAsDouble(goat)), this.ramDirection.x(), this.ramDirection.z());
            this.finishRam(serverLevel, goat);
            serverLevel.playSound(null, goat, this.getImpactSound.apply(goat), SoundCategory.NEUTRAL, 1.0f, 1.0f);
        } else if (this.hasRammedHornBreakingBlock(serverLevel, goat)) {
            serverLevel.playSound(null, goat, this.getImpactSound.apply(goat), SoundCategory.NEUTRAL, 1.0f, 1.0f);
            boolean bl = goat.dropHorn();
            if (bl) {
                serverLevel.playSound(null, goat, this.getHornBreakSound.apply(goat), SoundCategory.NEUTRAL, 1.0f, 1.0f);
            }
            this.finishRam(serverLevel, goat);
        } else {
            boolean bl;
            Optional<WalkTarget> optional = brain.getMemory(MemoryModuleType.WALK_TARGET);
            Optional<Vector3d> optional2 = brain.getMemory(MemoryModuleType.RAM_TARGET);
            boolean bl2 = bl = optional.isEmpty() || optional2.isEmpty() || optional.get().getTarget().currentPosition().closerThan(optional2.get(), 0.25);
            if (bl) {
                this.finishRam(serverLevel, goat);
            }
        }
    }

    private boolean hasRammedHornBreakingBlock(ServerWorld serverLevel, GoatEntity goat) {
        Vector3d vec3 = goat.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize();
        BlockPos blockPos = new BlockPos(goat.position().add(vec3));
        boolean flag = Util.containsEither(GoatAi.SNAPS_GOAT_HORN, serverLevel.getBlockState(blockPos).getBlock(), serverLevel.getBlockState(blockPos.above()).getBlock());
        return flag;
    }

    protected void finishRam(ServerWorld serverLevel, GoatEntity goat) {
        serverLevel.broadcastEntityEvent(goat, (byte)59);
        goat.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, this.getTimeBetweenRams.apply(goat).sample(serverLevel.random));
        goat.getBrain().eraseMemory(MemoryModuleType.RAM_TARGET);
    }
}

