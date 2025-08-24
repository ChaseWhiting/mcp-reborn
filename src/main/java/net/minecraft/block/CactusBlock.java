package net.minecraft.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CactusBlock extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    protected static final VoxelShape OUTLINE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    private static final int MAX_CACTUS_GROWING_HEIGHT = 3;
    private static final int ATTEMPT_GROW_CACTUS_FLOWER_AGE = 8;
    private static final double ATTEMPT_GROW_CACTUS_FLOWER_SMALL_CACTUS_CHANCE = 0.1;
    private static final double ATTEMPT_GROW_CACTUS_FLOWER_TALL_CACTUS_CHANCE = 0.25;

    protected CactusBlock(AbstractBlock.Properties p_i48435_1_) {
        super(p_i48435_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
    }

    public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
        if (!p_225534_1_.canSurvive(p_225534_2_, p_225534_3_)) {
            p_225534_2_.destroyBlock(p_225534_3_, true);
        }

    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos blockpos = pos.above();
        if (!world.isEmptyBlock(blockpos)) {
            return;
        }
        int height = 1;
        while (world.getBlockState(pos.below(height)).is(this)) {
            height++;
            if (height >= MAX_CACTUS_GROWING_HEIGHT) {
                break;
            }
        }
        int age = state.getValue(AGE);
        if (age >= 15) {
            age = 15;
        }
        double flowerChance = (height >= 3) ? 0.25 : 0.10;
        if (random.nextDouble() <= flowerChance) {
            world.setBlockAndUpdate(blockpos, Blocks.CACTUS_FLOWER.defaultBlockState());
            return;
        }
        if (age == 15 && height < 3) {
            world.setBlockAndUpdate(blockpos, this.defaultBlockState());
            BlockState newState = state.setValue(AGE, 0);
            world.setBlock(pos, newState, 4);
        } else {
            world.setBlock(pos, state.setValue(AGE, Math.min(15, age + 1)), 4);
        }
    }


    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
        return COLLISION_SHAPE;
    }

    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return OUTLINE_SHAPE;
    }

    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        if (!p_196271_1_.canSurvive(p_196271_4_, p_196271_5_)) {
            p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
        }

        return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
    }

    public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockstate = p_196260_2_.getBlockState(p_196260_3_.relative(direction));
            Material material = blockstate.getMaterial();
            if (material.isSolid() || p_196260_2_.getFluidState(p_196260_3_.relative(direction)).is(FluidTags.LAVA)) {
                return false;
            }
        }

        BlockState blockstate1 = p_196260_2_.getBlockState(p_196260_3_.below());
        return (blockstate1.is(Blocks.CACTUS) || blockstate1.is(Blocks.SAND) || blockstate1.is(Blocks.RED_SAND)) && !p_196260_2_.getBlockState(p_196260_3_.above()).getMaterial().isLiquid();
    }

    public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
        boolean hurt = true;

        if (p_196262_4_ instanceof LivingEntity livingEntity) {
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, livingEntity) > 0) {
                hurt = false;
            }
        }

        if (hurt) {
            p_196262_4_.hurt(DamageSource.CACTUS, p_196262_4_.veryHardmode() ? 3f : 1f);
        }
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        return false;
    }
}