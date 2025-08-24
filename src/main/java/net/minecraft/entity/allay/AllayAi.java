package net.minecraft.entity.allay;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.UUID;

public class AllayAi {
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 1.0f;
    private static final float SPEED_MULTIPLIER_WHEN_FOLLOWING_DEPOSIT_TARGET = 2.25f;
    private static final float SPEED_MULTIPLIER_WHEN_RETRIEVING_ITEM = 1.75f;
    private static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.5f;
    private static final int CLOSE_ENOUGH_TO_TARGET = 4;
    private static final int TOO_FAR_FROM_TARGET = 16;
    private static final int MAX_LOOK_DISTANCE = 6;
    private static final int MIN_WAIT_DURATION = 30;
    private static final int MAX_WAIT_DURATION = 60;
    private static final int TIME_TO_FORGET_NOTEBLOCK = 600;
    private static final int DISTANCE_TO_WANTED_ITEM = 32;
    private static final int GIVE_ITEM_TIMEOUT_DURATION = 20;

    protected static Brain<?> makeBrain(Brain<AllayEntity> brain) {
        AllayAi.initCoreActivity(brain);
        AllayAi.initIdleActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }


    private static void initCoreActivity(Brain<AllayEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new SwimTask(0.8f),
                new AnimalPanicTask(2.5F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CountDownCooldownTicks(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS),
                new CountDownCooldownTicks(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)
        ));
    }

    private static void initIdleActivity(Brain<AllayEntity> brain) {
        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(
                Pair.of(0, new GoToWantedItem(allay -> true, 1.75F, true, 32)),
                Pair.of(1, new GoAndGiveItemsToTarget<AllayEntity>(AllayAi::getItemDepositPosition, 2.25f, 20)),
                Pair.of(2, new StayCloseToTarget(AllayAi::getItemDepositPosition, 4, 16, 2.25f)),
                Pair.of(3, new SetEntityLookTargetSometimes(EntityType.PLAYER, 6f, 30, 60)),
                Pair.of(4, new FirstShuffledTask<>(ImmutableList.of(
                        Pair.of(RandomStrollTask.fly(1.0f), 2),
                        Pair.of(new WalkTowardsLookTargetTask(1.0f, 3), 2),
                        Pair.of(new DummyTask(30, 60), 1)
                )))
        ), ImmutableSet.of());
    }

    public static void updateActivity(AllayEntity allay) {
        allay.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    public static void hearNoteblock(LivingEntity livingEntity, BlockPos blockPos) {
        Brain<?> brain = livingEntity.getBrain();
        GlobalPos globalPos = GlobalPos.of(livingEntity.level.dimension(), blockPos);
        Optional<GlobalPos> optional = brain.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
        if (optional.isEmpty()) {
            brain.setMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION, globalPos);
            brain.setMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, 600);
        } else if (optional.get().equals(globalPos)) {
            brain.setMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS, 600);
        }
    }

    private static Optional<IPosWrapper> getItemDepositPosition(LivingEntity livingEntity) {
        Brain<?> brain = livingEntity.getBrain();
        Optional<GlobalPos> optional = brain.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
        if (optional.isPresent()) {
            GlobalPos globalPos = optional.get();
            if (AllayAi.shouldDepositItemsAtLikedNoteblock(livingEntity, brain, globalPos)) {
                return Optional.of(new BlockPosWrapper(globalPos.pos().above()));
            }
            brain.eraseMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
        }
        return AllayAi.getLikedPlayerPositionTracker(livingEntity);
    }

    private static boolean shouldDepositItemsAtLikedNoteblock(LivingEntity livingEntity, Brain<?> brain, GlobalPos globalPos) {
        Optional<Integer> optional = brain.getMemory(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS);
        World level = livingEntity.level;
        return level.dimension() == globalPos.dimension() && level.getBlockState(globalPos.pos()).is(Blocks.NOTE_BLOCK) && optional.isPresent();
    }

    private static Optional<IPosWrapper> getLikedPlayerPositionTracker(LivingEntity livingEntity) {
        return AllayAi.getLikedPlayer(livingEntity).map(serverPlayer -> new EntityPosWrapper(serverPlayer, true));
    }

    public static Optional<ServerPlayerEntity> getLikedPlayer(LivingEntity livingEntity) {
        World level = livingEntity.level;
        if (!level.isClientSide && level instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)level;
            Optional<UUID> optional = livingEntity.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
            if (optional.isPresent()) {
                Entity entity = serverWorld.getEntity(optional.get());
                if (entity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                    if ((serverPlayerEntity.gameMode.isSurvival() || serverPlayerEntity.gameMode.isCreative()) && serverPlayerEntity.closerThan(livingEntity, 64.0)) {
                        return Optional.of(serverPlayerEntity);
                    }
                }
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
