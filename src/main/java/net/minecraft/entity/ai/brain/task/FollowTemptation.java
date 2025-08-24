package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.util.math.EntityPosWrapper;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class FollowTemptation
extends Task<Mob> {
    public static final int TEMPTATION_COOLDOWN = 100;
    public static final double CLOSE_ENOUGH_DIST = 2.5;
    private final Function<LivingEntity, Float> speedModifier;
    private final Function<LivingEntity, Float> closeEnoughDistance;
    private boolean lookInTheEyes;


    public FollowTemptation(Function<LivingEntity, Float> function) {
        this(function, entity -> 2.5F);
    }

    public FollowTemptation(Function<LivingEntity, Float> function, Function<LivingEntity, Float> function2) {
        super(net.minecraft.util.Util.make(() -> {
            ImmutableMap.Builder<MemoryModuleType<?>, MemoryModuleStatus> builder = ImmutableMap.builder();
            builder.put(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED);
            builder.put(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED);
            builder.put(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleStatus.VALUE_ABSENT);
            builder.put(MemoryModuleType.IS_TEMPTED, MemoryModuleStatus.REGISTERED);
            builder.put(MemoryModuleType.TEMPTING_PLAYER, MemoryModuleStatus.VALUE_PRESENT);
            builder.put(MemoryModuleType.BREED_TARGET, MemoryModuleStatus.VALUE_ABSENT);
            builder.put(MemoryModuleType.IS_PANICKING, MemoryModuleStatus.VALUE_ABSENT);
            return builder.build();
        }));
        this.speedModifier = function;
        this.closeEnoughDistance = function2;
        lookInTheEyes = false;
    }

    public FollowTemptation(Function<LivingEntity, Float> function, Function<LivingEntity, Float> function2, boolean lookInTheEyes) {
        this(function, function2);
        this.lookInTheEyes = lookInTheEyes;
    }

    protected float getSpeedModifier(Mob pathfinderMob) {
        return this.speedModifier.apply(pathfinderMob).floatValue();
    }

    private Optional<PlayerEntity> getTemptingPlayer(Mob pathfinderMob) {
        return pathfinderMob.getBrain().getMemory(MemoryModuleType.TEMPTING_PLAYER);
    }

    @Override
    protected boolean timedOut(long l) {
        return false;
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, Mob pathfinderMob, long l) {
        return this.getTemptingPlayer(pathfinderMob).isPresent() && !pathfinderMob.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET) && !pathfinderMob.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING);
    }

    @Override
    protected void start(ServerWorld serverLevel, Mob pathfinderMob, long l) {
        pathfinderMob.getBrain().setMemory(MemoryModuleType.IS_TEMPTED, true);
    }

    @Override
    protected void stop(ServerWorld serverLevel, Mob pathfinderMob, long l) {
        Brain<?> brain = pathfinderMob.getBrain();
        brain.setMemory(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, 100);
        brain.setMemory(MemoryModuleType.IS_TEMPTED, false);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void tick(ServerWorld serverLevel, Mob pathfinderMob, long l) {
        PlayerEntity player = this.getTemptingPlayer(pathfinderMob).get();
        Brain<?> brain = pathfinderMob.getBrain();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(player, true));
        double d = this.closeEnoughDistance.apply(pathfinderMob);
        if (pathfinderMob.distanceToSqr(player) < MathHelper.square(d)) {
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        } else {
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(player, lookInTheEyes), this.getSpeedModifier(pathfinderMob), 2));
        }
    }
}

