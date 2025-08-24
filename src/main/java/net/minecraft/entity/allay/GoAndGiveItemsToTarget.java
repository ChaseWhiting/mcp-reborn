package net.minecraft.entity.allay;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;


public class GoAndGiveItemsToTarget<E extends LivingEntity>
extends Task<E> {
    private static final int CLOSE_ENOUGH_DISTANCE_TO_TARGET = 3;
    private static final int ITEM_PICKUP_COOLDOWN_AFTER_THROWING = 60;
    private final Function<LivingEntity, Optional<IPosWrapper>> targetPositionGetter;
    private final float speedModifier;

    public GoAndGiveItemsToTarget(Function<LivingEntity, Optional<IPosWrapper>> function, float f, int n) {
        super(Map.of(MemoryModuleType.LOOK_TARGET, MemStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemStatus.REGISTERED, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemStatus.REGISTERED), n);
        this.targetPositionGetter = function;
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverWorld, E e) {
        return this.canThrowItemToTarget(e);
    }

    @Override
    protected boolean canStillUse(ServerWorld serverWorld, E e, long l) {
        return this.canThrowItemToTarget(e);
    }

    @Override
    protected void start(ServerWorld serverWorld, E e, long l) {
        this.targetPositionGetter.apply((LivingEntity)e).ifPresent(positionTracker -> BrainUtil.setWalkAndLookTargetMemories(e, positionTracker.currentBlockPosition(), this.speedModifier, 3));
    }

    @Override
    protected void tick(ServerWorld serverWorld, E e, long l) {
        ItemStack itemStack;
        Optional<IPosWrapper> optional = this.targetPositionGetter.apply((LivingEntity)e);
        if (optional.isEmpty()) {
            return;
        }
        IPosWrapper positionTracker = optional.get();
        double d = positionTracker.currentPosition().distanceTo((e).getEyePosition(1.0F));
        if (d < 3.0 && !(itemStack = ((InventoryCarrier)e).getInventory().removeItem(0, 1)).isEmpty()) {
            GoAndGiveItemsToTarget.throwItem(e, itemStack, GoAndGiveItemsToTarget.getThrowPosition(positionTracker));
            if (e instanceof AllayEntity) {
                AllayEntity allay = (AllayEntity) e;
                AllayAi.getLikedPlayer(allay).ifPresent(serverPlayer -> this.triggerDropItemOnBlock(positionTracker, itemStack, (ServerPlayerEntity)serverPlayer));
            }
            ((LivingEntity)e).getBrain().setMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, 60);
        }
    }

    private void triggerDropItemOnBlock(IPosWrapper positionTracker, ItemStack itemStack, ServerPlayerEntity serverPlayerEntity) {
        BlockPos blockPos = positionTracker.currentBlockPosition().below();
        //CriteriaTriggers.ALLAY_DROP_ITEM_ON_BLOCK.trigger(serverPlayerEntity, blockPos, itemStack);
    }

    private boolean canThrowItemToTarget(E e) {
        if (((InventoryCarrier)e).getInventory().isEmpty()) {
            return false;
        }
        Optional<IPosWrapper> optional = this.targetPositionGetter.apply((LivingEntity)e);
        return optional.isPresent();
    }

    private static Vector3d getThrowPosition(IPosWrapper positionTracker) {
        return positionTracker.currentPosition().add(0.0, 1.0, 0.0);
    }

    public static void throwItem(LivingEntity livingEntity, ItemStack itemStack, Vector3d vector3D) {
        Vector3d vector32D = new Vector3d(0.2f, 0.3f, 0.2f);
        BrainUtil.throwItem(livingEntity, itemStack, vector3D, vector32D, 0.2f);
        World level = livingEntity.level;
        if (level.getGameTime() % 7L == 0L && level.random.nextDouble() < 0.9) {
            float f = Util.getRandom(AllayEntity.THROW_SOUND_PITCHES, level.getRandom());
            level.playSound(null, livingEntity, SoundEvents.ALLAY_THROW, SoundCategory.NEUTRAL, 1.0f, f);
        }
    }
}

