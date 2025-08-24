package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Mob;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.EnumSet;


/**
 * @Author ChasePixel_
 */
public class WaterSaveGoal extends Goal {

    private final Mob entity;

    public WaterSaveGoal(Mob entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity instanceof AbstractSkeletonEntity) return false;

        boolean hasWater = false;
        for (Hand hand : Hand.values()) {
            ItemStack water = entity.getItemInHand(hand);
            if (water.getItem() == Items.WATER_BUCKET) hasWater = true;
            if (!hasWater && water.getItem() == Items.BUCKET) {
                hasWater = maybeFillBucket(hand, false);
                break;
            }
        }
        return this.entity.getDeltaMovement().y < -0.68 && hasWater && this.entity.isAlive();
    }

    public boolean maybeFillBucket(Hand hand, boolean hasWater) {
        if (!hasWater && entity.level.isServerSide) {
            if (entity.getRandom().nextFloat() < 0.05f) {
                BlockPos pos = entity.blockPosition();
                ServerWorld serverWorld = (ServerWorld) entity.level;
                if (serverWorld.getFluidState(pos).is(FluidTags.WATER) && serverWorld.getFluidState(pos).isSource()) {
                    entity.swing(hand);
                    entity.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
                    serverWorld.playSound(null, pos, SoundEvents.BUCKET_FILL, entity.getSoundSource(), 1.0F, 1.0F);
                    serverWorld.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return entity.isAlive() && entity.getDeltaMovement().y < -0.68;
    }

    @Override
    public void tick() {
        for (Hand hand : Hand.values()) {
            ItemStack stack = entity.getItemInHand(hand);
            if (stack.getItem() == Items.WATER_BUCKET && entity.level.isServerSide) {
                ServerWorld serverWorld = (ServerWorld) entity.level;
                BlockPos position = entity.blockPosition().below();

                if (serverWorld.getBlockState(position).canBeReplaced(Fluids.WATER) && serverWorld.getBlockState(position.below()).isSolidRender(serverWorld, position.below())) {
                    entity.swing(hand);

                    if (placeWater(serverWorld, position)) {
                        serverWorld.playSound(null, position, SoundEvents.BUCKET_EMPTY, entity.getSoundSource(), 1.0F, 1.0F);
                        this.entity.setItemInHand(hand, new ItemStack(Items.BUCKET, 1));

                    }
                }
            }
        }
    }

    private boolean placeWater(ServerWorld serverWorld, BlockPos position) {
        BlockState blockState = serverWorld.getBlockState(position);
        if (blockState.canBeReplaced(Fluids.WATER)) {
            serverWorld.setBlock(position, Fluids.WATER.defaultFluidState().createLegacyBlock(), 3);
            return true;
        }
        return false;
    }

}