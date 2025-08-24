package net.minecraft.util;

import java.util.Optional;

import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;


public class SpawnUtil {
    /**
     * Attempts to spawn a mob at a random position within a specified area.
     *
     * @param entityType   The type of entity to spawn.
     * @param spawnReason  The reason for the spawn.
     * @param world        The server level in which to spawn the entity.
     * @param centerPos    The center position for the spawn area.
     * @param attempts     Number of spawn attempts.
     * @param range        Horizontal range for spawn attempts.
     * @param verticalOffset Vertical offset for spawn attempts.
     * @param strategy     Spawn strategy to determine valid spawn locations.
     * @param checkCollision Whether to check collision before spawning.
     * @return Optional of the spawned mob if successful, otherwise empty.
     */
    public static <T extends Mob> Optional<T> trySpawnMob(EntityType<T> entityType, SpawnReason spawnReason, ServerWorld world, BlockPos centerPos, int attempts, int range, int verticalOffset, Strategy strategy, boolean checkCollision) {
        BlockPos.Mutable mutablePos = centerPos.mutable();

        for (int i = 0; i < attempts; ++i) {
            int offsetX = MathHelper.randomBetweenInclusive(world.random, -range, range);
            int offsetZ = MathHelper.randomBetweenInclusive(world.random, -range, range);
            mutablePos.setWithOffset(centerPos, offsetX, verticalOffset, offsetZ);

            if (!world.getWorldBorder().isWithinBounds(mutablePos)) continue;

            if (!moveToPossibleSpawnPosition(world, verticalOffset, mutablePos, strategy)) continue;

            if (checkCollision && !world.noCollision(entityType.getSpawnAABB((double) mutablePos.getX() + 0.5, mutablePos.getY(), (double) mutablePos.getZ() + 0.5))) {
                continue;
            }

            T mob = entityType.create(world, null, null, null, mutablePos, spawnReason, false, false);

            if (mob != null && mob.checkSpawnRules(world, spawnReason) && mob.checkSpawnObstruction(world)) {
                world.addFreshEntityWithPassengers(mob);
                mob.playAmbientSound();
                return Optional.of(mob);
            }

            if (mob != null) {
                mob.discard();
            }
        }

        return Optional.empty();
    }


    public static Optional<BlockPos> getTeleportingPosition(EntityType<?> entityType, SpawnReason spawnReason, ServerWorld world, BlockPos centerPos, int attempts, int range, int verticalOffset, Strategy strategy, boolean checkCollision) {
        BlockPos.Mutable mutablePos = centerPos.mutable();

        for (int i = 0; i < attempts; ++i) {
            int offsetX = MathHelper.randomBetweenInclusive(world.random, -range, range);
            int offsetZ = MathHelper.randomBetweenInclusive(world.random, -range, range);
            mutablePos.setWithOffset(centerPos, offsetX, verticalOffset, offsetZ);

            if (!world.getWorldBorder().isWithinBounds(mutablePos)) continue;

            if (!moveToPossibleSpawnPosition(world, verticalOffset, mutablePos, strategy)) continue;

            if (checkCollision && !world.noCollision(entityType.getSpawnAABB((double) mutablePos.getX() + 0.5, mutablePos.getY(), (double) mutablePos.getZ() + 0.5))) {
                continue;
            }

            Creature mob = (Creature) entityType.create(world);

            if (mob != null && mob.checkSpawnRules(world, spawnReason) && mob.checkSpawnObstruction(world)) {
                return Optional.of(mutablePos.immutable());
            }

            if (mob != null) {
                mob.discard();
            }
        }

        return Optional.empty();
    }

    /**
     * Adjusts a mutable position to a valid spawn position based on the given strategy.
     *
     * @param world        The server level.
     * @param verticalRange The range to search vertically.
     * @param mutablePos   The mutable position to adjust.
     * @param strategy     The spawn strategy to determine valid positions.
     * @return True if a valid spawn position was found, otherwise false.
     */
    private static boolean moveToPossibleSpawnPosition(ServerWorld world, int verticalRange, BlockPos.Mutable mutablePos, Strategy strategy) {
        BlockPos.Mutable abovePos = new BlockPos.Mutable().set(mutablePos);
        BlockState currentBlock = world.getBlockState(abovePos);

        for (int i = verticalRange; i >= -verticalRange; --i) {
            mutablePos.move(Direction.DOWN);
            abovePos.setWithOffset(mutablePos, Direction.UP);
            BlockState belowBlock = world.getBlockState(mutablePos);

            if (strategy.canSpawnOn(world, mutablePos, belowBlock, abovePos, currentBlock)) {
                mutablePos.move(Direction.UP);
                return true;
            }

            currentBlock = belowBlock;
        }

        return false;
    }

    /**
     * Strategy interface for determining valid spawn positions.
     */
    public interface Strategy {
        @Deprecated
        Strategy LEGACY_IRON_GOLEM = (world, pos, currentBlock, abovePos, aboveBlock) -> {
            if (currentBlock.is(Blocks.COBWEB) || currentBlock.is(Blocks.CACTUS) || currentBlock.is(Blocks.GLASS_PANE) ||
                    currentBlock.getBlock() instanceof StainedGlassPaneBlock || currentBlock.getBlock() instanceof StainedGlassBlock ||
                    currentBlock.getBlock() instanceof LeavesBlock || currentBlock.is(Blocks.CONDUIT) || currentBlock.is(Blocks.ICE) ||
                    currentBlock.is(Blocks.TNT) || currentBlock.is(Blocks.GLOWSTONE) || currentBlock.is(Blocks.BEACON) ||
                    currentBlock.is(Blocks.SEA_LANTERN) || currentBlock.is(Blocks.FROSTED_ICE) ||
                    currentBlock.is(Blocks.GLASS) || currentBlock.is(Blocks.TINTED_GLASS)) {
                return false;
            }
            return aboveBlock.isAir() || !aboveBlock.getFluidState().isEmpty() || currentBlock.isSolidRender(world, pos);
        };

        Strategy ON_TOP_OF_COLLIDER = (world, pos, currentBlock, abovePos, aboveBlock) ->
                aboveBlock.getCollisionShape(world, abovePos).isEmpty() &&
                        Block.isFaceFull(currentBlock.getCollisionShape(world, pos), Direction.UP);

        Strategy ON_TOP_OF_COLLIDER_NO_LEAVES = (world, pos, currentBlock, abovePos, aboveBlock) ->
                aboveBlock.getCollisionShape(world, abovePos).isEmpty() &&
                        !currentBlock.is(BlockTags.LEAVES) &&
                        Block.isFaceFull(currentBlock.getCollisionShape(world, pos), Direction.UP);

        boolean canSpawnOn(ServerWorld world, BlockPos pos, BlockState currentBlock, BlockPos abovePos, BlockState aboveBlock);
    }
}
