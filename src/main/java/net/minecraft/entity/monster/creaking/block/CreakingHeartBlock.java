package net.minecraft.entity.monster.creaking.block;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.*;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CreakingHeartBlock extends ContainerBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final EnumProperty<CreakingHeartState> CREAKING = EnumProperty.create("creaking", CreakingHeartState.class);
    public static final BooleanProperty ACTIVE = BlockStateProperties.ACTIVE;
    public static final BooleanProperty NATURAL = BlockStateProperties.NATURAL;
    public CreakingHeartBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y).setValue(CREAKING, CreakingHeartState.DISABLED).setValue(ACTIVE, false).setValue(NATURAL, true));
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new CreakingHeartBlockEntity();
    }

    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    public static boolean isNaturalNight(World $$0) {
        return $$0.dimensionType().natural() && $$0.isNight();
    }

    @Override
    public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
        super.tick(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
        if (p_225534_2_.getBlockEntity(p_225534_3_) instanceof CreakingHeartBlockEntity) {
            //CreakingHeartBlockEntity.serverTick(p_225534_2_, p_225534_3_, p_225534_1_, (CreakingHeartBlockEntity) Objects.requireNonNull(p_225534_2_.getBlockEntity(p_225534_3_)));
        }
    }

    public static boolean hasRequiredLogs(BlockState $$0, IWorldReader $$1, BlockPos $$2) {
        Direction.Axis $$3 = $$0.getValue(AXIS);
        for (Direction $$4 : Direction.values()) {
            BlockState $$5 = $$1.getBlockState($$2.relative($$4));
            if ($$5.is(Blocks.PALE_OAK_LOG) && $$5.getValue(AXIS) == $$3) continue;
            return false;
        }
        return true;
    }

    private static boolean isSurroundedByLogs(World $$0, BlockPos $$1) {
        for (Direction $$2 : Direction.values()) {
            BlockPos $$3 = $$1.relative($$2);
            BlockState $$4 = $$0.getBlockState($$3);
            if ($$4.is(Blocks.PALE_OAK_LOG)) continue;
            return false;
        }
        return true;
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        if (canSummonCreaking(world)) {
            if (state.getValue(CREAKING) != CreakingHeartState.DISABLED && random.nextInt(16) == 0 && isSurroundedByLogs(world, pos)) {
                double x = pos.getX() + (double) random.nextInt(16) - 8;
                double y = pos.getY() + (double) random.nextInt(8) - 4;
                double z = pos.getZ() + (double) random.nextInt(16) - 8;
                world.playSound(null, x, y, z, SoundEvents.BELL_RESONATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public static boolean canSummonCreaking(World world) {
        return world.dimensionType().natural() && world.isNight();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS, CREAKING, ACTIVE, NATURAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
    }

    @Override
    public void destroy(IWorld worldIn, BlockPos pos, BlockState state) {
        TileEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof CreakingHeartBlockEntity) {
            ((CreakingHeartBlockEntity) tileEntity).removeProtector(null);
        }
        super.destroy(worldIn, pos, state);
    }

    public enum CreakingHeartState implements IStringSerializable {
        DISABLED("disabled"),
        DORMANT("dormant"),
        ACTIVE("active");

        private final String name;

        private CreakingHeartState(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
