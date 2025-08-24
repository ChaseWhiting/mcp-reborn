package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class AbstractCandleBlock extends Block {
    public static final int LIGHT_PER_CANDLE = 3;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;


    protected AbstractCandleBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    protected abstract Iterable<Vector3d> getParticleOffsets(BlockState var1);

    // TODO Add all candle blocks in list check
    public static boolean isLit(BlockState blockState) {
        return blockState.hasProperty(LIT) && (blockState.is(Registry.BLOCK.stream().filter(block -> block instanceof CandleBlock).toList()) /*|| blockState.is(BlockTags.CANDLE_CAKES)*/) && blockState.getValue(LIT);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockRayTraceResult result, ProjectileEntity projectile) {
        if (!world.isClientSide) {
            AbstractCandleBlock.setLit(world, state, result.getBlockPos(), true);
        }
    }

    protected boolean canBeLit(BlockState blockState) {
        return !blockState.getValue(LIT);
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!isLit(state)) {
            return;
        }

        this.getParticleOffsets(state).forEach(vec -> AbstractCandleBlock.addParticlesAndSound(world, vec.add(pos.getX(), pos.getY(), pos.getZ()), random));
    }

    private static void addParticlesAndSound(World level, Vector3d vec3, Random randomSource) {
        float f = randomSource.nextFloat();
        if (f < 0.3f) {
            level.addParticle(ParticleTypes.SMOKE, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
            if (f < 0.17f) {
                level.playLocalSound(vec3.x + 0.5, vec3.y + 0.5, vec3.z + 0.5, SoundEvents.CANDLE_AMBIENT, SoundCategory.BLOCKS, 1.0f + randomSource.nextFloat(), randomSource.nextFloat() * 0.7f + 0.3f, false);
            }
        }
        level.addParticle(ParticleTypes.SMALL_FLAME, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
    }

    public static void extinguish(@Nullable PlayerEntity player, BlockState blockState, IWorld levelAccessor, BlockPos blockPos) {
        AbstractCandleBlock.setLit(levelAccessor, blockState, blockPos, false);
        if (blockState.getBlock() instanceof AbstractCandleBlock) {
            ((AbstractCandleBlock)blockState.getBlock()).getParticleOffsets(blockState).forEach(vec3 -> levelAccessor.addParticle(ParticleTypes.SMOKE, (double)blockPos.getX() + vec3.x(), (double)blockPos.getY() + vec3.y(), (double)blockPos.getZ() + vec3.z(), 0.0, 0.1f, 0.0));
        }
        levelAccessor.playSound(null, blockPos, SoundEvents.CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 1.0f);
        levelAccessor.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
    }

    private static void setLit(IWorld levelAccessor, BlockState blockState, BlockPos blockPos, boolean bl) {
        levelAccessor.setBlock(blockPos, blockState.setValue(LIT, bl), 11);
    }
}
