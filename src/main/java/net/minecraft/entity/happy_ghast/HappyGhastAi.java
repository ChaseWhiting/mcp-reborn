package net.minecraft.entity.happy_ghast;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.declarative.BabyFollowAdult;
import net.minecraft.entity.ai.brain.declarative.RandomStroll;
import net.minecraft.entity.ai.brain.declarative.SetWalkTargetFromLookTarget;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.UniformInt;

import java.util.List;
import java.util.Set;

public class HappyGhastAi {
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0f;
    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.25f;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 1.1f;
    private static final double BABY_GHAST_CLOSE_ENOUGH_DIST = 3.0;
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(3, 16);
    private static final ImmutableList<SensorType<? extends Sensor<? super HappyGhastEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.HAPPY_GHAST_TEMPTATIONS, SensorType.NEAREST_ADULT_ANY_TYPE, SensorType.NEAREST_PLAYERS_SENSOR);
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES =
            ImmutableList.of(
                    MemoryModuleType.WALK_TARGET,
                    MemoryModuleType.LOOK_TARGET,
                    MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                    MemoryModuleType.PATH,
                    MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                    MemoryModuleType.TEMPTING_PLAYER,
                    MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
                    MemoryModuleType.IS_TEMPTED,
                    MemoryModuleType.BREED_TARGET,
                    MemoryModuleType.IS_PANICKING,
                    MemoryModuleType.HURT_BY,
                    MemoryModuleType.NEAREST_VISIBLE_ADULT,
                    MemoryModuleType.NEAREST_VISIBLE_ADULT_NON_AGEABLE,
                    MemoryModuleType.NEAREST_PLAYERS,
                    MemoryModuleType.NEAREST_VISIBLE_PLAYER,
                    MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
                    MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYERS);

    public static Brain.BrainCodec<HappyGhastEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected static Brain<?> makeBrain(Brain<HappyGhastEntity> brain) {
        HappyGhastAi.initCoreActivity(brain);
        HappyGhastAi.initIdleActivity(brain);
        HappyGhastAi.initPanicActivity(brain);
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<HappyGhastEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new SwimTask(0.8f),
                new AnimalPanic<>(2.0f, 0),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)));
    }

    private static void initIdleActivity(Brain<HappyGhastEntity> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(1, new FollowTemptation(livingEntity -> Float.valueOf(1.25f), livingEntity -> 3.0F, true)),
                Pair.of(2, BabyFollowAdult.create(ADULT_FOLLOW_RANGE, livingEntity -> Float.valueOf(1.1f), MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, true)),
                Pair.of(3, BabyFollowAdult.create(ADULT_FOLLOW_RANGE, livingEntity -> Float.valueOf(1.1f), MemoryModuleType.NEAREST_VISIBLE_ADULT_NON_AGEABLE, true)),
                Pair.of(4, new FirstShuffledTask<>(ImmutableList.of(
                                Pair.of(RandomStroll.flyTest(1.0f), 1),
                                Pair.of(SetWalkTargetFromLookTarget.create(1.0f, 3), 1)
                                )
                        ))));
    }

    private static void initPanicActivity(Brain<HappyGhastEntity> brain) {
        brain.addActivityWithConditions(Activity.PANIC,
                ImmutableList.of(), Set.of(Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleStatus.VALUE_PRESENT)));
    }

    public static void updateActivity(HappyGhastEntity happyGhast) {
        happyGhast.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.PANIC, Activity.IDLE));
    }
}

