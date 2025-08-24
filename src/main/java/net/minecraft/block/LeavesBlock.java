package net.minecraft.block;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.random.RandomSource;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LeavesBlock extends Block {
    public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    private final IParticleData particle = ParticleTypes.TINTED_LEAVES;
    private final int chance = 100;

    public LeavesBlock(AbstractBlock.Properties p_i48370_1_) {
        super(p_i48370_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, Integer.valueOf(7)).setValue(PERSISTENT, Boolean.valueOf(false)));
    }

    public VoxelShape getBlockSupportShape(BlockState p_230335_1_, IBlockReader p_230335_2_, BlockPos p_230335_3_) {
        return VoxelShapes.empty();
    }

    public boolean isRandomlyTicking(BlockState p_149653_1_) {
        return p_149653_1_.getValue(DISTANCE) == 7 && !p_149653_1_.getValue(PERSISTENT);
    }

    public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
        if (!p_225542_1_.getValue(PERSISTENT) && p_225542_1_.getValue(DISTANCE) == 7) {
            dropResources(p_225542_1_, p_225542_2_, p_225542_3_);
            p_225542_2_.removeBlock(p_225542_3_, false);
        }

    }

    public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
        p_225534_2_.setBlock(p_225534_3_, updateDistance(p_225534_1_, p_225534_2_, p_225534_3_), 3);
    }

    public int getLightBlock(BlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
        return 1;
    }

    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        int i = getDistanceAt(p_196271_3_) + 1;
        if (i != 1 || p_196271_1_.getValue(DISTANCE) != i) {
            p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 1);
        }

        return p_196271_1_;
    }

    private static BlockState updateDistance(BlockState p_208493_0_, IWorld p_208493_1_, BlockPos p_208493_2_) {
        int i = 7;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (Direction direction : Direction.values()) {
            blockpos$mutable.setWithOffset(p_208493_2_, direction);
            i = Math.min(i, getDistanceAt(p_208493_1_.getBlockState(blockpos$mutable)) + 1);
            if (i == 1) {
                break;
            }
        }

        return p_208493_0_.setValue(DISTANCE, Integer.valueOf(i));
    }

    private static int getDistanceAt(BlockState p_208492_0_) {
        if (BlockTags.LOGS.contains(p_208492_0_.getBlock())) {
            return 0;
        } else {
            return p_208492_0_.getBlock() instanceof LeavesBlock ? p_208492_0_.getValue(DISTANCE) : 7;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos blockPos, Random random) {
        if (world.isRainingAt(blockPos.above())) {
            if (random.nextInt(15) == 1) {
                BlockPos blockpos = blockPos.below();
                BlockState blockstate = world.getBlockState(blockpos);
                if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(world, blockpos, Direction.UP)) {
                    double d0 = (double) blockPos.getX() + random.nextDouble();
                    double d1 = (double) blockPos.getY() - 0.05D;
                    double d2 = (double) blockPos.getZ() + random.nextDouble();
                    world.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        // Check if the particle should spawn based on the chance
        if (this instanceof ParticleLeavesBlock || this == Blocks.APPLE_LEAVES) return;
        if (random.nextInt(this.chance) != 0) {
            return;
        }

        BlockPos belowPos = blockPos.below();
        BlockState belowState = world.getBlockState(belowPos);

        // Check if the block below is solid
        if (!belowState.canOcclude() || !belowState.isFaceSturdy(world, belowPos, Direction.UP)) {
            spawnParticleBelow(world, blockPos, random, this.particle);
        }
    }

    private static void spawnParticleBelow(World world, BlockPos pos, Random random, IParticleData particle) {
        double x = (double) pos.getX() + random.nextDouble();
        double y = (double) pos.getY() - 0.05;
        double z = (double) pos.getZ() + random.nextDouble();
        world.addAlwaysVisibleParticle(particle, x, y, z, 0.0, 0.0, 0.0);
    }



    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, PERSISTENT);
    }

    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        return updateDistance(this.defaultBlockState().setValue(PERSISTENT, Boolean.valueOf(true)), p_196258_1_.getLevel(), p_196258_1_.getClickedPos());
    }
}