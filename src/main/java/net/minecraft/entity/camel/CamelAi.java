package net.minecraft.entity.camel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Creature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.declarative.TaskBuilder;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.UniformInt;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class CamelAi {
    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 4.0f;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 2.0f;
    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 2.5f;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 2.5f;
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 1.0f;
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
    private static final ImmutableList<SensorType<? extends Sensor<? super CamelEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.CAMEL_TEMPTATIONS, SensorType.NEAREST_ADULT);
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.IS_PANICKING, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.GAZE_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_VISIBLE_ADULT);



    protected static void initMemories(CamelEntity camel, Random random) {}


    public static Brain.BrainCodec<CamelEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    protected static Brain<CamelEntity> makeBrain(Brain<CamelEntity> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<CamelEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new SwimTask(0.8f),
                new CamelPanic(4.0f),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.GAZE_COOLDOWN_TICKS)
        ));
    }

    private static void initIdleActivity(Brain<CamelEntity> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(0, new SetEntityLookTargetSometimes(EntityType.PLAYER, 6, 30, 60)),
                Pair.of(1, new RegularBreedTask(EntityType.CAMEL, 1.25F)),
                Pair.of(2, new FirstShuffledTask<CamelEntity>(ImmutableList.of(
                        Pair.of(new FollowTemptation(livingEntity -> 2.5F), 1),
                        Pair.of(new ChildFollowNearestAdultTask<CamelEntity>(RangedInteger.of(ADULT_FOLLOW_RANGE.getMinValue(), ADULT_FOLLOW_RANGE.getMaxValue()), 2.5f), 3)
                ))),
                Pair.of(3, new RandomLookAround(UniformInt.of(150, 250), 30f, 0f, 0f)),
                Pair.of(4, new FirstShuffledTask<CamelEntity>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT),
                        ImmutableList.of(Pair.of(createSimpleWalkingTask(), 1),
                                        Pair.of(createSimpleTargetLookTask(), 1),
                                Pair.of(new RandomSitting(), 1),
                                Pair.of(new DummyTask(30, 60), 1))))
        ));
    }


    private static Task<CamelEntity> createSimpleWalkingTask() {
        Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemory = new HashMap<>();
        requiredMemory.put(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT);
        float speed = 2.0F;
        Predicate<CamelEntity> condition = Predicate.not(CamelEntity::refuseToMove);
        Function<CamelEntity, Vector3d> posFinder = entity -> RandomPositionGenerator.getLandPos(entity, 10, 7);
        return new TaskBuilder.Builder<CamelEntity>()
                .extraStartConditionsBehavior((world, entity) -> condition.test(entity))
                .startBehavior((world, entity, time) -> {
                    Optional<Vector3d> targetPos = Optional.ofNullable(posFinder.apply(entity));
                    targetPos.ifPresent(pos -> entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, speed, 0)));
                })
                .build(requiredMemory);
    }

    private static Task<CamelEntity> createSimpleTargetLookTask() {
        Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemory = new HashMap<>();
        requiredMemory.put(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT);
        requiredMemory.put(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_PRESENT);
        Predicate<CamelEntity> condition = Predicate.not(CamelEntity::refuseToMove);
        return new TaskBuilder.Builder<CamelEntity>()
                .extraStartConditionsBehavior((world, entity) -> condition.test(entity))
                .startBehavior((world, entity, time) -> {
                    Brain<CamelEntity> brain = entity.getBrain();
                    IPosWrapper iposwrapper = brain.getMemory(MemoryModuleType.LOOK_TARGET).get();
                    brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(iposwrapper, 2.0F, 3));
                })
                .build(requiredMemory);
    }


    public static void updateActivity(CamelEntity camel) {
        camel.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }


    public static Ingredient getTemptations() {
        return Ingredient.of(Items.CACTUS_FLOWER);
    }


    public static class CamelPanic extends AnimalPanicTask {

        public CamelPanic(float speed) {
            super(speed);
        }

        @Override
        protected void start(ServerWorld world, Creature animal, long time) {
            if (animal instanceof CamelEntity) {
                animal.as(CamelEntity.class).standUpInstantly();
            }
            super.start(world, animal, time);
        }
    }

    public static class RandomSitting extends Task<CamelEntity> {
        private final int minimalPoseTicks = 20 * 20;
        public RandomSitting() {
            super(ImmutableMap.of());
        }

        @Override
        protected boolean checkExtraStartConditions(ServerWorld world, CamelEntity camel) {
            return !camel.isInWater() && camel.getPoseTime() >= this.minimalPoseTicks && !camel.isLeashed()
                    && camel.isOnGround() && !camel.hasOnePlayerPassenger() && camel.canCamelChangePose();
        }

        @Override
        protected void start(ServerWorld level, CamelEntity camel, long time) {
            if (camel.isCamelSitting()) {
                camel.standUp();
            } else if (!camel.isPanicking()) {
                camel.sitDown();
            }
        }
    }

}
