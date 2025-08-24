package net.minecraft.entity.goat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.declarative.TaskBuilder;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.UniformInt;

import java.util.*;

public class GoatAi {
    public static final int RAM_PREPARE_TIME = 20;
    public static final int RAM_MAX_DISTANCE = 7;
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 1.0f;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0f;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT = 1.25f;
    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.25f;
    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.0f;
    private static final float SPEED_MULTIPLIER_WHEN_PREPARING_TO_RAM = 1.25f;
    private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.of(600, 1200);
    public static final int MAX_LONG_JUMP_HEIGHT = 5;
    public static final int MAX_LONG_JUMP_WIDTH = 5;
    public static final float MAX_JUMP_VELOCITY = 1.5f;
    private static final UniformInt TIME_BETWEEN_RAMS = UniformInt.of(600, 6000);
    private static final UniformInt TIME_BETWEEN_RAMS_SCREAMER = UniformInt.of(100, 300);
    private static final TargetingConditions RAM_TARGET_CONDITIONS = TargetingConditions.forCombat().selector((livingEntity, world) -> !livingEntity.getType().equals(EntityType.GOAT) && livingEntity.level.getWorldBorder().isWithinBounds(livingEntity.getBoundingBox()));
    private static final float SPEED_MULTIPLIER_WHEN_RAMMING = 3.0f;
    public static final int RAM_MIN_DISTANCE = 4;
    public static final float ADULT_RAM_KNOCKBACK_FORCE = 2.5f;
    public static final float BABY_RAM_KNOCKBACK_FORCE = 1.0f;

    public static Set<Block> SNAPS_GOAT_HORN = Sets.newConcurrentHashSet();



    static {
        SNAPS_GOAT_HORN.addAll(BlockTags.BASE_STONE_OVERWORLD.getValues());
        SNAPS_GOAT_HORN.addAll(BlockTags.LOGS.getValues());
        SNAPS_GOAT_HORN.addAll(Arrays.asList( // Use Arrays.asList() instead of ImmutableList
                Blocks.STONE,
                Blocks.PACKED_ICE,
                Blocks.IRON_ORE,
                Blocks.COAL_ORE,
                Blocks.EMERALD_ORE
        ));
    }



    private static final Task<LivingEntity> setWalkTargetFromLookTarget = new TaskBuilder.Builder<LivingEntity>().createNewBuilder()
            .extraStartConditionsBehavior(((world, entity) -> {
                Brain<?> brain = entity.getBrain();
                if (brain.getMemory(MemoryModuleType.LOOK_TARGET).isEmpty()) {
                    return false;
                }
                brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(brain.getMemory(MemoryModuleType.LOOK_TARGET).get(), 1.0F, 3));
                return true;
            })).build(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_PRESENT))
            ;

    protected static void initMemories(GoatEntity goat, Random random) {
        goat.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, TIME_BETWEEN_LONG_JUMPS.sample(random));
        goat.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, TIME_BETWEEN_RAMS.sample(random));
    }

    protected static Brain<?> makeBrain(Brain<GoatEntity> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initLongJumpActivity(brain);
        initRamActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<GoatEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new SwimTask(0.8f),
                new AnimalPanicTask(2.0f),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.RAM_COOLDOWN_TICKS)));
    }

    private static void initIdleActivity(Brain<GoatEntity> brain) {
        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(
                        Pair.of(0, new SetEntityLookTargetSometimes(EntityType.PLAYER, 6.0f, 30, 60)),
                        Pair.of(0, new RegularBreedTask(EntityType.GOAT, 1.0f)),
                        Pair.of(1, new FollowTemptation(entity -> 1.25f)),
                        Pair.of(2, new ChildFollowNearestAdultTask<>(RangedInteger.of(5, 16), 1.25f)),
                        Pair.of(3, new FirstShuffledTask<>(ImmutableList.of(
                                Pair.of(new FindWalkTargetTask(1.0F), 2),
                                Pair.of(new WalkTowardsLookTargetTask(1.0F, 3), 2),
                                Pair.of(new DummyTask(30, 60), 1)
                        )))),
                ImmutableSet.of(
                        Pair.of(MemoryModuleType.RAM_TARGET, MemoryModuleStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleStatus.VALUE_ABSENT))
        );
    }

    private static FirstShuffledTask<GoatEntity> createIdleMovementBehaviors() {
        return new FirstShuffledTask<>(ImmutableList.of(
                Pair.of(new FindWalkTargetTask(1.0F), 2),
                Pair.of(setWalkTargetFromLookTarget, 2),
                Pair.of(new DummyTask(30, 60), 1)
        ));
    }



    private static void initLongJumpActivity(Brain<GoatEntity> brain) {
        brain.addActivityWithConditions(Activity.LONG_JUMP, ImmutableList.of(
                Pair.of(0, new LongJumpMidJump(TIME_BETWEEN_LONG_JUMPS, SoundEvents.GOAT_STEP)),
                Pair.of(1, new LongJumpToRandomPos<GoatEntity>(TIME_BETWEEN_LONG_JUMPS, 5, 5, 1.5f, goat -> goat.isScreamingGoat() ?
                        SoundEvents.GOAT_SCREAMING_LONG_JUMP : SoundEvents.GOAT_LONG_JUMP))
        ),
                ImmutableSet.of(
                        Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryModuleStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.BREED_TARGET, MemoryModuleStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleStatus.VALUE_ABSENT)));
    }



    private static void initRamActivity(Brain<GoatEntity> brain) {
        brain.addActivityWithConditions(Activity.RAM, ImmutableList.of(
                Pair.of(0, new RamTarget(
                        goat -> goat.isScreamingGoat() ?
                                TIME_BETWEEN_RAMS_SCREAMER : TIME_BETWEEN_RAMS,
                        RAM_TARGET_CONDITIONS, 3.0f, goat-> goat.isBaby() ? 1.0 : 2.5,
                        goat -> goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_RAM_IMPACT : SoundEvents.GOAT_RAM_IMPACT,
                        goat -> goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_HORN_BREAK : SoundEvents.GOAT_HORN_BREAK)),
                        Pair.of(1, new PrepareRamNearestTarget<GoatEntity>(
                                goat -> goat.isScreamingGoat() ? TIME_BETWEEN_RAMS_SCREAMER.getMinValue() : TIME_BETWEEN_RAMS.getMinValue(), 4, 7, 1.25f,
                                RAM_TARGET_CONDITIONS, 20, goat -> goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_PREPARE_RAM : SoundEvents.GOAT_PREPARE_RAM
                        ))
                ), ImmutableSet.of(Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryModuleStatus.VALUE_ABSENT), Pair.of(MemoryModuleType.BREED_TARGET, MemoryModuleStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleStatus.VALUE_ABSENT))
                );
    }

    public static void updateActivity(GoatEntity goat) {
        goat.getBrain()
                .setActiveActivityToFirstValid(
                        ImmutableList.of(Activity.RAM, Activity.LONG_JUMP, Activity.IDLE)
                );
    }

    public static Ingredient getTemptations() {
        return Ingredient.of(Items.WHEAT);
    }

}
