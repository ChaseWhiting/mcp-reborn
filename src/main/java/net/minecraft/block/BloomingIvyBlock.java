package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BloomingIvyBlock extends VineBlock {

    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;

    public BloomingIvyBlock(AbstractBlock.Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(UP, false).setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(STAGE, 1).setValue(LOCKED, false));
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (random.nextInt(25) == 1 && !state.getValue(LOCKED) && state.getValue(STAGE) < 3) {
            BlockState bsn = state.setValue(STAGE, state.getValue(STAGE) + 1);
            world.setBlock(pos, bsn, 3);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        ItemStack item = player.getItemInHand(hand);

        if (item.get() == Items.SHEARS) {
            BlockState b = state.setValue(LOCKED, true);
            world.setBlock(pos, b, 3);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, b));
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!player.level.isClientSide) {
                if (!player.abilities.instabuild) {
                    item.hurt(1, player);
                }
            }

            return ActionResultType.sidedSuccess(player.level.isClientSide);
        }


        return super.use(state, world, pos, player, hand, result);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH, EAST, SOUTH, WEST, STAGE, LOCKED);
    }
}
