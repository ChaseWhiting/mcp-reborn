package net.minecraft.block;

import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class RedstoneReceiverBlock extends Block {
    public static final IntegerProperty FREQUENCY = IntegerProperty.create("frequency", 1, 10);

    public RedstoneReceiverBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FREQUENCY, 1));
    }


    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return null; // No TileEntity needed for this simple receiver
    }




    public void neighborChanged(BlockState state, IBlockReader world, BlockPos pos, BlockPos neighbor) {
        if (world instanceof World && !((World) world).isClientSide) {
            if (((World) world).getBlockState(neighbor).getValue(BlockStateProperties.POWERED)) {
                // Output redstone signal based on receiving signal from transmitter
                ((World) world).setBlock(pos, state.setValue(BlockStateProperties.POWERED, true), 3);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FREQUENCY);
    }
}