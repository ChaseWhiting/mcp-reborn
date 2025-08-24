package net.minecraft.entity.warden;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class WardenAi {
    private static final int DIGGING_DURATION = MathHelper.ceil(100.0f);
    public static final int EMERGE_DURATION = MathHelper.ceil(133.59999f);
    public static final int ROAR_DURATION = MathHelper.ceil(84.0f);
    private static final int SNIFFING_DURATION = MathHelper.ceil(83.2f);
    private static final List<SensorType<? extends Sensor<? super WardenEntity>>> SENSOR_TYPES = List.of(SensorType.NEAREST_PLAYERS, SensorType.WARDEN_ENTITY_SENSOR);
    private static final List<MemoryModuleType<?>> MEMORY_TYPES = List.of(MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.ROAR_TARGET, MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleType.RECENT_PROJECTILE, MemoryModuleType.IS_SNIFFING, MemoryModuleType.IS_EMERGING, MemoryModuleType.ROAR_SOUND_DELAY, MemoryModuleType.DIG_COOLDOWN, MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleType.TOUCH_COOLDOWN, MemoryModuleType.VIBRATION_COOLDOWN, MemoryModuleType.SONIC_BOOM_COOLDOWN, MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryModuleType.SONIC_BOOM_SOUND_DELAY);



    public static void updateActivity(WardenEntity warden) {
        warden.getBrain().setActiveActivityToFirstValid(
                ImmutableList.of(Activity.EMERGE, Activity.DIG, Activity.ROAR, Activity.FIGHT, Activity.INVESTIGATE, Activity.SNIFF, Activity.IDLE));
    }

    protected static Brain<WardenEntity> makeBrain(WardenEntity warden, Dynamic<?> dynamic) {
        Brain.BrainCodec<WardenEntity> provider = Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
        Brain<WardenEntity> brain = provider.makeBrain(dynamic);
        WardenAi.initCoreActivity(brain);
        WardenAi.initEmergeActivity(brain);
        WardenAi.initDiggingActivity(brain);
        WardenAi.initIdleActivity(brain);
        WardenAi.initRoarActivity(brain);
        WardenAi.initFightActivity(warden, brain);
        WardenAi.initInvestigateActivity(brain);
        WardenAi.initSniffingActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initDiggingActivity(Brain<WardenEntity> brain) {
        brain.addActivityWithConditions(Activity.DIG, ImmutableList.of(
                Pair.of(0, new ForceUnmount()),
                Pair.of(1, new Digging(DIGGING_DURATION))), ImmutableSet.of(Pair.of(MemoryModuleType.ROAR_TARGET, MemoryModuleStatus.VALUE_ABSENT),
                Pair.of(MemoryModuleType.DIG_COOLDOWN, MemoryModuleStatus.VALUE_ABSENT)));
    }

    private static void initRoarActivity(Brain<WardenEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.ROAR, 10, ImmutableList.of(
                new Roar()
        ), MemoryModuleType.ROAR_TARGET);
    }

    private static void initCoreActivity(Brain<WardenEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new SwimTask(0.8f),
                new SetWardenLookTarget(),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    private static void initIdleActivity(Brain<WardenEntity> brain) {
        brain.addActivity(Activity.IDLE, 10,
                ImmutableList.of(new SetRoarTarget(WardenEntity::getEntityAngryAt), new TryToSniff(),
                        new FirstShuffledTask<>(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryModuleStatus.VALUE_ABSENT), ImmutableList.of(
                                Pair.of(RandomStrollTask.stroll(0.5f), 2),
                                Pair.of(new DummyTask(30, 60), 1)
                        ))));

    }

    private static void initSniffingActivity(Brain<WardenEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.SNIFF, 5, ImmutableList.of(
                new SetRoarTarget(WardenEntity::getEntityAngryAt),
                new Sniffing<>(SNIFFING_DURATION)
        ), MemoryModuleType.IS_SNIFFING);
    }

    private static void initFightActivity(WardenEntity warden, Brain<WardenEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                new DigCooldownSetter(),

                new StopAttackingIfTargetInvalidTask<WardenEntity>(entity -> !warden.getAngerLevel().isAngry() || !warden.canTargetEntity(entity),
                        WardenAi::onTargetInvalid, false),

                new SetEntityLookTarget(livingEntity -> WardenAi.isTarget(warden, livingEntity), (float)warden.getAttributeValue(Attributes.FOLLOW_RANGE)),
                new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.2F),
                new SonicBoom(),
                new WardenMeleeAttack(18)

        ), MemoryModuleType.ATTACK_TARGET);
    }


    private static boolean isTarget(WardenEntity warden, LivingEntity livingEntity) {
        return warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter(livingEntity2 -> livingEntity2 == livingEntity).isPresent();
    }

    private static void initInvestigateActivity(Brain<WardenEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.INVESTIGATE, 5, ImmutableList.of(
                new SetRoarTarget(WardenEntity::getEntityAngryAt),
                new GoToTargetLocation(MemoryModuleType.DISTURBANCE_LOCATION, 2, 0.7F)
        ), MemoryModuleType.DISTURBANCE_LOCATION);
    }

    private static void initEmergeActivity(Brain<WardenEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.EMERGE, 5,
                ImmutableList.of((Task<WardenEntity>) new Emerging(EMERGE_DURATION)),
                MemoryModuleType.IS_EMERGING);
    }

    public static void setDigCooldown(LivingEntity livingEntity) {
        if (livingEntity.getBrain().hasMemoryValue(MemoryModuleType.DIG_COOLDOWN)) {
            livingEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
        }
    }

    private static void onTargetInvalid(WardenEntity warden, LivingEntity livingEntity) {
        if (!warden.canTargetEntity(livingEntity)) {
            warden.clearAnger(livingEntity);
        }
        WardenAi.setDigCooldown(warden);
    }

    public static void setDisturbanceLocation(WardenEntity warden, BlockPos blockPos) {
        if (!warden.level.getWorldBorder().isWithinBounds(blockPos) || warden.getEntityAngryAt().isPresent() || warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
            return;
        }
        WardenAi.setDigCooldown(warden);
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 100L);
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(blockPos), 100L);
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.DISTURBANCE_LOCATION, blockPos, 100L);
        warden.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }
}
