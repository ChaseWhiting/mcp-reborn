package net.minecraft.entity.axolotl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.UniformInt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.world.World;

import java.util.Optional;

public class AxolotlAi {
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(5, 16);
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 0.2f;
    private static final float SPEED_MULTIPLIER_ON_LAND = 0.15f;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING_IN_WATER = 0.5f;
    private static final float SPEED_MULTIPLIER_WHEN_CHASING_IN_WATER = 0.6f;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT_IN_WATER = 0.6f;



    protected static Brain<?> makeBrain(Brain<AxolotlEntity> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initFightActivity(brain);
        initPlayDeadActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }



    private static void initPlayDeadActivity(Brain<AxolotlEntity> brain) {
        brain.addActivityAndRemoveMemoriesWhenStopped(Activity.PLAY_DEAD, ImmutableList.of(Pair.of(0, PlayDead.playDead()), Pair.of(1, new EraseMemoryIf<AxolotlEntity>(AxolotlAi::isBreeding, MemoryModuleType.PLAY_DEAD_TICKS))), ImmutableSet.of(Pair.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleStatus.VALUE_PRESENT)), ImmutableSet.of(MemoryModuleType.PLAY_DEAD_TICKS));
    }

    private static void initFightActivity(Brain<AxolotlEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, ImmutableList.of(
                new StopAttackingIfTargetInvalid<AxolotlEntity>(AxolotlEntity::onStopAttacking),
                new SetWalkTargetFromAttackTargetIfTargetOutOfReach(AxolotlAi::getSpeedModifierChasing),
                new MeleeAttack(20),
                new EraseMemoryIf<AxolotlEntity>(AxolotlAi::isBreeding, MemoryModuleType.ATTACK_TARGET)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    private static void initCoreActivity(Brain<AxolotlEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new ValidatePlayDead(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
        ));
    }

    private static void initIdleActivity(Brain<AxolotlEntity> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(0, new SetEntityLookTargetSometimes(EntityType.PLAYER, 6f, 30, 60)),
                Pair.of(1, new RegularBreedTask(EntityType.AXOLOTL, 0.2f)),
                Pair.of(2, new FirstShuffledTask<AxolotlEntity>(ImmutableList.of(
                        Pair.of(new FollowTemptation(AxolotlAi::getSpeedModifier), 1),
                        Pair.of(new AxolotlFollowAdultTask<>(RangedInteger.of(5, 16), AxolotlAi::getSpeedModifierFollowingAdult), 1)))),
                Pair.of(3, new StartAttacking<AxolotlEntity>(AxolotlAi::findNearestValidAttackTarget)),
                Pair.of(3, new TryFindWater(6, 0.15f)),
                Pair.of(4, new GateBehavior<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT),
                        ImmutableSet.of(),
                        GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.TRY_ALL,
                        ImmutableList.of(
                                Pair.of(new RandomSwim(0.5F), 2),
                                Pair.of(new RandomStroll(0.15F), 2),
                                Pair.of(new AxolotlWalkTowardsLookTargetTask(AxolotlAi::getSpeedModifier, 3), 3),
                                Pair.of(new RunIf<LivingEntity>(Entity::isInWaterOrBubble, new DummyTask(30, 60)), 5),
                                Pair.of(new RunIf<LivingEntity>(Entity::isOnGround, new DummyTask(200, 400)), 5))))
        ));
    }



    private static boolean canSetWalkTargetFromLookTarget(LivingEntity livingEntity) {
        World world = livingEntity.level;
        Optional<IPosWrapper> optional = livingEntity.getBrain().getMemory(MemoryModuleType.LOOK_TARGET);
        if (optional.isPresent()) {
            BlockPos pos = optional.get().currentBlockPosition();
            return world.isWaterAt(pos) == livingEntity.isInWater();
        }
        return false;
    }

    public static void updateActivity(AxolotlEntity axolotl) {
        Brain<AxolotlEntity> brain = (Brain<AxolotlEntity>) axolotl.getBrain();
        Activity activity = brain.getActiveNonCoreActivity().orElse(null);
        if (activity != Activity.PLAY_DEAD) {
            brain.setActiveActivityToFirstValid(ImmutableList.of(
                    Activity.PLAY_DEAD, Activity.FIGHT, Activity.IDLE
            ));
            if (activity == Activity.FIGHT && brain.getActiveNonCoreActivity().orElse(null) != Activity.FIGHT) {
                brain.setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
            }
        }
    }

    private static float getSpeedModifierChasing(LivingEntity livingEntity) {
        return livingEntity.isInWater() ? SPEED_MULTIPLIER_WHEN_CHASING_IN_WATER : SPEED_MULTIPLIER_ON_LAND;
    }

    private static float getSpeedModifierFollowingAdult(LivingEntity livingEntity) {
        return livingEntity.isInWater() ? SPEED_MULTIPLIER_WHEN_FOLLOWING_ADULT_IN_WATER : SPEED_MULTIPLIER_ON_LAND;
    }

    private static float getSpeedModifier(LivingEntity livingEntity) {
        return livingEntity.isInWater() ? SPEED_MULTIPLIER_WHEN_IDLING_IN_WATER : SPEED_MULTIPLIER_ON_LAND;
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(AxolotlEntity axolotl) {
        if (isBreeding(axolotl)) {
            return Optional.empty();
        }
        return axolotl.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE);
    }

    private static boolean isBreeding(AxolotlEntity axolotl) {
        return axolotl.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET);
    }

    public static Ingredient getTemptations() {
        return Ingredient.of(Items.TROPICAL_FISH_BUCKET);
    }


}
