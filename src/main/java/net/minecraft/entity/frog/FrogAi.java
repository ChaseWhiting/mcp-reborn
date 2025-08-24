package net.minecraft.entity.frog;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.UniformInt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class FrogAi {
    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.0f;
    private static final float SPEED_MULTIPLIER_WHEN_MAKING_LOVE = 1.0f;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0f;
    private static final float SPEED_MULTIPLIER_ON_LAND = 1.0f;
    private static final float SPEED_MULTIPLIER_IN_WATER = 0.75f;
    private static final UniformInt TIME_BETWEEN_LONG_JUMPS = UniformInt.of(100, 140);
    private static final int MAX_LONG_JUMP_HEIGHT = 2;
    private static final int MAX_LONG_JUMP_WIDTH = 4;
    private static final float MAX_JUMP_VELOCITY = 1.5f;
    private static final float SPEED_MULTIPLIER_WHEN_TEMPTED = 1.25f;


    protected static Brain<?> makeBrain(Brain<FrogEntity> brain) {
        FrogAi.initCoreActivity(brain);
        FrogAi.initIdleActivity(brain);
        FrogAi.initSwimActivity(brain);
        FrogAi.initLaySpawnActivity(brain);
        FrogAi.initTongueActivity(brain);
        FrogAi.initJumpActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    protected static void initMemories(FrogEntity frog, Random randomSource) {
        frog.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, TIME_BETWEEN_LONG_JUMPS.sample(randomSource));
    }

    public static Ingredient getTemptations() {
        return FrogEntity.TEMPTATION_ITEM;
    }

    private static <E extends Mob> boolean isAcceptableLandingSpot(E e, BlockPos blockPos) {
        World level = e.level;
        BlockPos blockPos2 = blockPos.below();
        if (!(level.getFluidState(blockPos).isEmpty() && level.getFluidState(blockPos2).isEmpty() && level.getFluidState(blockPos.above()).isEmpty())) {
            return false;
        }
        BlockState blockState = level.getBlockState(blockPos);
        BlockState blockState2 = level.getBlockState(blockPos2);
        if (blockState.is(FrogEntity.FROG_PREFER_JUMP_TO) || blockState2.is(FrogEntity.FROG_PREFER_JUMP_TO)) {
            return true;
        }
        PathNodeType blockPathTypes = WalkNodeProcessor.getBlockPathTypeStatic(level, blockPos.mutable());
        PathNodeType blockPathTypes2 = WalkNodeProcessor.getBlockPathTypeStatic(level, blockPos2.mutable());
        if (blockPathTypes == PathNodeType.TRAPDOOR || blockState.isAir() && blockPathTypes2 == PathNodeType.TRAPDOOR) {
            return true;
        }
        return LongJumpToRandomPos.defaultAcceptableLandingSpot(e, blockPos);
    }


    private static void initCoreActivity(Brain<FrogEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new AnimalPanicTask(2.0F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS)
        ));
    }

    private static void initTongueActivity(Brain<FrogEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.TONGUE, 0,
                ImmutableList.of(new StopAttackingIfTargetInvalid<>(),
                        new ShootTongue(SoundEvents.FROG_TONGUE, SoundEvents.FROG_EAT)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void initJumpActivity(Brain<FrogEntity> brain) {
        brain.addActivityWithConditions(Activity.LONG_JUMP, ImmutableList.of(
                Pair.of(0, new LongJumpMidJump(TIME_BETWEEN_LONG_JUMPS, SoundEvents.FROG_STEP)),
                Pair.of(1, new LongJumpToPreferredBlock<FrogEntity>(TIME_BETWEEN_LONG_JUMPS, 2, 4, 1.5f, frog ->
                        SoundEvents.FROG_LONG_JUMP, FrogEntity.FROG_PREFER_JUMP_TO, 0.5f, FrogAi::isAcceptableLandingSpot)
                )), ImmutableSet.of(
                        Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryModuleStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.BREED_TARGET, MemoryModuleStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.IS_IN_WATER, MemoryModuleStatus.VALUE_ABSENT)
                ));
    }

    private static void initIdleActivity(Brain<FrogEntity> brain) {
        brain.addActivityWithConditions(Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, new SetEntityLookTargetSometimes(EntityType.PLAYER, 6.0f, 30, 60)),
                        Pair.of(0, new RegularBreedTask(EntityType.FROG, 1.0F)),
                        Pair.of(1, new FollowTemptation(livingEntity -> 1.25F)),
                        Pair.of(2, new StartAttacking<FrogEntity>(FrogAi::canAttack, frog -> frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
                        Pair.of(3, new TryFindLand(6, 1.0F)),
                        Pair.of(4, new FirstShuffledTask<FrogEntity>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT),
                                ImmutableList.of(
                                        Pair.of(RandomStrollTask.stroll(1.0F), 1),
                                        Pair.of(new WalkTowardsLookTargetTask(1.0f, 3), 1),
                                        Pair.of(new Croak(), 3),
                                        Pair.of(new DummyTask(30, 60), 2))))
                ),
                ImmutableSet.of(
                        Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.IS_IN_WATER, MemoryModuleStatus.VALUE_ABSENT)));
    }

    private static void initSwimActivity(Brain<FrogEntity> brain) {
        brain.addActivityWithConditions(Activity.SWIM, ImmutableList.of(
                Pair.of(0, new SetEntityLookTargetSometimes(EntityType.PLAYER, 6f, 30, 60)),
                Pair.of(0, new FollowTemptation(livingEntity -> 1.25f)),
                Pair.of(2, new StartAttacking<FrogEntity>(FrogAi::canAttack, frog -> frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
                Pair.of(3, new TryFindLand(8, 1.5F)),
                Pair.of(5, new GateBehavior<FrogEntity>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT),
                        ImmutableSet.of(), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.TRY_ALL, ImmutableList.of(
                                Pair.of(RandomStrollTask.swim(0.75F), 1),
                                Pair.of(RandomStrollTask.stroll(1.0F, true), 1),
                                Pair.of(new WalkTowardsLookTargetTask(1.0F, 3), 1),
                                Pair.of(new DummyTask(30, 60), 5)
                )))
        ), ImmutableSet.of(
                Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleStatus.VALUE_ABSENT),
                Pair.of(MemoryModuleType.IS_IN_WATER, MemoryModuleStatus.VALUE_PRESENT)));
    }

    private static boolean canAttack(FrogEntity frog) {
        return !BrainUtil.isBreeding(frog);
    }

    public static void updateActivity(FrogEntity frog) {
        frog.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.TONGUE, Activity.LAY_SPAWN, Activity.LONG_JUMP, Activity.SWIM, Activity.IDLE));
    }

    private static void initLaySpawnActivity(Brain<FrogEntity> brain) {
        brain.addActivityWithConditions(Activity.LAY_SPAWN,
                ImmutableList.of(
                        Pair.of(0, new SetEntityLookTargetSometimes(EntityType.PLAYER, 6f, 30, 60)),
                        Pair.of(1, new StartAttacking<FrogEntity>(FrogAi::canAttack, frogEntity -> frogEntity.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
                        Pair.of(2, new TryFindLandNearWater(8, 1.0f)),
                        Pair.of(3, new TryLaySpawnOnWaterNearLand(Blocks.FROGSPAWN)),
                        Pair.of(4, new FirstShuffledTask<FrogEntity>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT),
                                ImmutableList.of(
                                        Pair.of(RandomStrollTask.stroll(1.0F), 1),
                                        Pair.of(new WalkTowardsLookTargetTask(1.0f, 3), 1),
                                        Pair.of(new Croak(), 3),
                                        Pair.of(new DummyTask(30, 60), 2))))), ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleStatus.VALUE_ABSENT),
                        Pair.of(MemoryModuleType.IS_PREGNANT, MemoryModuleStatus.VALUE_PRESENT)));
    }


    public static class TryLaySpawnOnWaterNearLand extends Task<Mob> {
        private final Block spawnBlock;
        private long nextCheckTime;

        public TryLaySpawnOnWaterNearLand(Block block) {
            super(ImmutableMap.of(
                    MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_ABSENT,
                    MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_PRESENT,
                    MemoryModuleType.IS_PREGNANT, MemoryModuleStatus.VALUE_PRESENT
            ));
            this.spawnBlock = block;
        }

        @Override
        protected boolean checkExtraStartConditions(ServerWorld world, Mob mob) {
            return !mob.isInWater() && mob.isOnGround();
        }

        @Override
        protected void start(ServerWorld world, Mob mob, long gameTime) {
            if (gameTime < this.nextCheckTime) {
                return;
            }

            BlockPos entityPos = mob.blockPosition().below();
            for (BlockPos blockPos : BlockPos.betweenClosed(entityPos.offset(-1, 0, -1), entityPos.offset(1, 0, 1))) {
                if (!world.getFluidState(blockPos).is(FluidTags.WATER)) {
                    continue;
                }

                BlockPos aboveWaterPos = blockPos.above();
                if (!world.getBlockState(aboveWaterPos).isAir()) {
                    continue;
                }

                // Place the spawn block above the water
                world.setBlock(aboveWaterPos, spawnBlock.defaultBlockState(), 3);
                world.playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                        SoundEvents.FROG_LAY_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.0f);

                mob.getBrain().eraseMemory(MemoryModuleType.IS_PREGNANT);
                this.nextCheckTime = gameTime + 40L; // Prevents frequent spawning
                return;
            }
        }
    }
}
