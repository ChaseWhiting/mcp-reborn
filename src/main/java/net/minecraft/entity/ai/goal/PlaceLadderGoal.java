package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.entity.Mob;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class PlaceLadderGoal extends Goal {

    private final Mob entity;
    private int cooldown;

    public PlaceLadderGoal(Mob entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (cooldown-- > 0 ) return false;
        return this.entity.getTarget() != null && entity.getTarget().getY() + 1 > entity.getY();
    }

    @Override
    public void tick() {
        for (Hand hand : Hand.values()) {
            ItemStack stack = entity.getItemInHand(hand);
            if (stack.getItem() == Items.LADDER && entity.level.isServerSide) {
                BlockPos position = entity.blockPosition();
                Direction direction = entity.getDirection();
                BlockState ladder = Blocks.LADDER.defaultBlockState();
                ladder.setValue(LadderBlock.FACING, direction.getOpposite());
                for (int i = 0; i < 3; i++) {
                    if (entity.level.isEmptyBlock(position.offset(0, i, 0)) && ladder.canSurvive(entity.level, position.offset(0, i, 0))) {
                        entity.level.setBlock(position.offset(0, i, 0), ladder, 3);
                        entity.level.playSound(position.offset(0, i, 0), SoundEvents.LADDER_PLACE);
                        entity.swing(hand);
                        this.cooldown = 20;
                        break;
                    }
                }
            }
        }
    }

}