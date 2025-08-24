package net.minecraft.block.sculk;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SculkShriekerBlock extends Block implements IWaterLoggable {

    public static final BooleanProperty SHRIEKING = BlockStateProperties.SHRIEKING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty CAN_SUMMON = BlockStateProperties.CAN_SUMMON;
    protected static final VoxelShape COLLIDER = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    public static final double TOP_Y = COLLIDER.max(Direction.Axis.Y);

    public SculkShriekerBlock() {
        super(AbstractBlock.Properties.of(Material.SCULK, MaterialColor.COLOR_BLACK).strength(3.0f, 3.0f).sound(SoundType.SCULK_SHRIEKER));
        this.registerDefaultState(this.getStateDefinition().any().setValue(SHRIEKING, false).setValue(WATERLOGGED, false).setValue(CAN_SUMMON, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SHRIEKING, WATERLOGGED, CAN_SUMMON);
    }

    @Override
    public void stepOn(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
        super.stepOn(p_176199_1_, p_176199_2_, p_176199_3_);
    }

    @Override
    public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
        super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
    }

    @Override
    public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
        //super.tick(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
        return COLLIDER;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
        return COLLIDER;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
        return true;
    }

    @Override
    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        if (p_196271_1_.getValue(WATERLOGGED)) {
            p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
        }

        return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState p_204507_1_) {
        if (p_204507_1_.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(p_204507_1_);
    }

    @Override
    public void spawnAfterBreak(BlockState p_220062_1_, ServerWorld p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
        super.spawnAfterBreak(p_220062_1_, p_220062_2_, p_220062_3_, p_220062_4_);

        this.popExperience(p_220062_2_, p_220062_3_, 5);
    }
}
