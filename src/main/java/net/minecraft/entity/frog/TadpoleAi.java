package net.minecraft.entity.frog;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Creature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.declarative.TaskBuilder;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class TadpoleAi {
    protected static Brain<?> makeBrain(Brain<TadpoleEntity> brain) {
        TadpoleAi.initCoreActivity(brain);
        TadpoleAi.initIdleActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }



    public static void updateActivity(TadpoleEntity tadpole) {
        tadpole.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    private static void initCoreActivity(Brain<TadpoleEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new AnimalPanicTask(2F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
                ));
    }

    private static void initIdleActivity(Brain<TadpoleEntity> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(0, new SetEntityLookTargetSometimes(EntityType.PLAYER, 6.0F, 30, 60)),
                Pair.of(1, new FollowTemptation(e -> 1.25F)),
                Pair.of(2, new GateBehavior<TadpoleEntity>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT), ImmutableSet.of(),
                        GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.TRY_ALL, ImmutableList.of(
                                Pair.of(createTask(), 2),
                                Pair.of(new WalkTowardTask(), 3)
                )))
        ));
    }

    private static Task<TadpoleEntity> createTask() {
        Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemory = new HashMap<>();
        requiredMemory.put(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT);
        float speed = 0.5F;
        Function<Creature, Vector3d> posFinder = RandomStrollTask::getTargetSwimPos;
        Predicate<TadpoleEntity> condition = Entity::isInWaterOrBubble;
        return new TaskBuilder.Builder<TadpoleEntity>()
                .extraStartConditionsBehavior((world, entity) -> condition.test(entity))
                .startBehavior((world, entity, time) -> {
                    Optional<Vector3d> targetPos = Optional.ofNullable(posFinder.apply(entity));
                    targetPos.ifPresent(pos -> entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, speed, 0)));
                })
                .build(requiredMemory);
    }

    static class WalkTowardTask extends WalkTowardsLookTargetTask {

        public WalkTowardTask() {
            super(0.5F, 3);
        }

        @Override
        protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
            return p_212832_2_.isInWaterOrBubble();
        }
    }
}
