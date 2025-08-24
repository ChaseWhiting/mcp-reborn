package net.minecraft.block.sculk;

import com.clearspring.analytics.util.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.VisibleForTesting;

import javax.annotation.Nullable;
import java.util.*;

public class SculkSpreader {

    final boolean isWorldGeneration;
    private final List<Block> replaceableBlocks;
    private final int growthSpawnCost;
    private final int noGrowthRadius;
    private final int chargeDecayRate;
    private final int additionalDecayRate;
    private List<ChargeCursor> cursors = new ArrayList<ChargeCursor>();
    private static final Logger LOGGER = LogManager.getLogger("SculkSpreader");
    public static final List<Block> SCULK_REPLACEABLE = Util.make(Lists.newArrayList(), list -> {
        list.add(Blocks.STONE);
        list.add(Blocks.GRANITE);
        list.add(Blocks.DIORITE);
        list.add(Blocks.ANDESITE);

        list.add(Blocks.DIRT);
        list.add(Blocks.GRASS_BLOCK);
        list.add(Blocks.PODZOL);
        list.add(Blocks.COARSE_DIRT);
        list.add(Blocks.MYCELIUM);
        list.add(Blocks.MUD);

        list.addAll(List.of(Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA,
                Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA,
                Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA,
                Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA,
                Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA,
                Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA,
                Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA,
                Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA,
                Blocks.BLACK_TERRACOTTA));

        list.add(Blocks.CRIMSON_NYLIUM);
        list.add(Blocks.WARPED_NYLIUM);
        list.add(Blocks.NETHERRACK);
        list.add(Blocks.BLACKSTONE);
        list.add(Blocks.BASALT);

        list.add(Blocks.SAND);
        list.add(Blocks.RED_SAND);
        list.add(Blocks.GRAVEL);
        list.add(Blocks.SOUL_SAND);
        list.add(Blocks.SOUL_SOIL);
        list.add(Blocks.POLISHED_BASALT);
        list.add(Blocks.CLAY);
        list.add(Blocks.END_STONE);
        list.add(Blocks.SANDSTONE);
        list.add(Blocks.RED_SANDSTONE);
    });

    public static final List<Block> SCULK_REPLACEABLE_WORLD_GEN = new ArrayList<>(SCULK_REPLACEABLE);

    public SculkSpreader(boolean bl, List<Block> tagKey, int n, int n2, int n3, int n4) {
        this.isWorldGeneration = bl;
        this.replaceableBlocks = tagKey;
        this.growthSpawnCost = n;
        this.noGrowthRadius = n2;
        this.chargeDecayRate = n3;
        this.additionalDecayRate = n4;
    }

    public static SculkSpreader createLevelSpreader() {
        return new SculkSpreader(false, SCULK_REPLACEABLE, 10, 4, 10, 5);
    }

    public static SculkSpreader createWorldGenSpreader() {
        return new SculkSpreader(true, SCULK_REPLACEABLE_WORLD_GEN, 50, 1, 5, 10);
    }

    public void addCursors(BlockPos blockPos, int n) {
        while (n > 0) {
            int n2 = Math.min(n, 1000);
            this.addCursor(new ChargeCursor(blockPos, n2));
            n -= n2;
        }
    }

    private void addCursor(ChargeCursor chargeCursor) {
        if (this.cursors.size() >= 32) {
            return;
        }
        this.cursors.add(chargeCursor);
    }

    public List<Block> replaceableBlocks() {
        return this.replaceableBlocks;
    }

    public int growthSpawnCost() {
        return this.growthSpawnCost;
    }

    public int noGrowthRadius() {
        return this.noGrowthRadius;
    }

    public int chargeDecayRate() {
        return this.chargeDecayRate;
    }

    public int additionalDecayRate() {
        return this.additionalDecayRate;
    }

    public boolean isWorldGeneration() {
        return this.isWorldGeneration;
    }

    @VisibleForTesting
    public List<ChargeCursor> getCursors() {
        return this.cursors;
    }

    public void clear() {
        this.cursors.clear();
    }




    public void updateCursors(ISeedReader levelAccessor, BlockPos blockPos, Random randomSource, boolean bl) {
        if (this.cursors.isEmpty()) {
            return;
        }

        List<ChargeCursor> updatedCursors = new ArrayList<>();
        Map<BlockPos, ChargeCursor> cursorMap = new HashMap<>();
        Object2IntOpenHashMap<BlockPos> positionChargeMap = new Object2IntOpenHashMap<>();

        for (ChargeCursor chargeCursor : this.cursors) {
            chargeCursor.update(levelAccessor, blockPos, randomSource, this, bl);

            if (chargeCursor.charge <= 0) {
                //levelAccessor.levelEvent(3006, chargeCursor.getPos(), 0);
                continue;
            }

            BlockPos cursorPos = chargeCursor.getPos();
            positionChargeMap.addTo(cursorPos, chargeCursor.charge);

            ChargeCursor existingCursor = cursorMap.get(cursorPos);
            if (existingCursor == null) {
                cursorMap.put(cursorPos, chargeCursor);
                updatedCursors.add(chargeCursor);
            } else {
                if (!this.isWorldGeneration() && (chargeCursor.charge + existingCursor.charge) <= 1000) {
                    existingCursor.mergeWith(chargeCursor);
                } else {
                    updatedCursors.add(chargeCursor);
                    if (chargeCursor.charge > existingCursor.charge) {
                        cursorMap.put(cursorPos, chargeCursor);
                    }
                }
            }
        }

        for (Map.Entry<BlockPos, Integer> entry : positionChargeMap.object2IntEntrySet()) {
            BlockPos pos = entry.getKey();
            int chargeAmount = entry.getValue();
            ChargeCursor chargeCursor = cursorMap.get(pos);
            Set<Direction> facings = (chargeCursor != null) ? chargeCursor.getFacingData() : null;

            if (chargeAmount > 0 && facings != null) {
                int effectStrength = (int) (Math.log1p(chargeAmount) / 2.3) + 1;
                int effectData = (effectStrength << 6) + MultifaceBlock.pack(facings);
                levelAccessor.levelEvent(3006, pos, effectData);
            }
        }

        this.cursors = updatedCursors;
    }




    public static class ChargeCursor {
        private static final ObjectArrayList<Vector3i> NON_CORNER_NEIGHBOURS = Util.make(new ObjectArrayList<>(18), objectArrayList -> BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1)).filter(blockPos -> (blockPos.getX() == 0 || blockPos.getY() == 0 || blockPos.getZ() == 0) && !blockPos.equals(BlockPos.ZERO)).map(BlockPos::immutable).forEach((objectArrayList)::add));
        public static final int MAX_CURSOR_DECAY_DELAY = 1;
        private BlockPos pos;
        int charge;
        private int updateDelay;
        private int decayDelay;
        @Nullable
        private Set<Direction> facings;
        private static final Codec<Set<Direction>> DIRECTION_SET = DirectionCodec.CODEC.listOf()
                .xmap(list -> Sets.newEnumSet(list, Direction.class),
                        Lists::newArrayList);
        public static final Codec<ChargeCursor> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BlockPos.CODEC.fieldOf("pos").forGetter(ChargeCursor::getPos),
                        Codec.intRange(0, 1000).fieldOf("charge").orElse(0).forGetter(ChargeCursor::getCharge),
                        Codec.intRange(0, 1).fieldOf("decay_delay").orElse(1).forGetter(ChargeCursor::getDecayDelay),
                        Codec.intRange(0, Integer.MAX_VALUE).fieldOf("update_delay").orElse(0).forGetter(chargeCursor -> chargeCursor.updateDelay),
                        DIRECTION_SET.optionalFieldOf("facings").forGetter(chargeCursor -> Optional.ofNullable(chargeCursor.getFacingData()))
                ).apply(instance, ChargeCursor::new)
        );


        private ChargeCursor(BlockPos blockPos, int n, int n2, int n3, Optional<Set<Direction>> optional) {
            this.pos = blockPos;
            this.charge = n;
            this.decayDelay = n2;
            this.updateDelay = n3;
            this.facings = optional.orElse(null);
        }

        public ChargeCursor(BlockPos blockPos, int n) {
            this(blockPos, n, 1, 0, Optional.empty());
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public int getCharge() {
            return this.charge;
        }

        public int getDecayDelay() {
            return this.decayDelay;
        }

        @Nullable
        public Set<Direction> getFacingData() {
            return this.facings;
        }

        private boolean shouldUpdate(ISeedReader levelAccessor, BlockPos blockPos, boolean bl) {
            if (this.charge <= 0) {
                return false;
            }
            if (bl) {
                return true;
            }
            if (levelAccessor instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)levelAccessor;
                return serverWorld.shouldTickBlocksAt(blockPos);
            }
            return false;
        }

        public void update(ISeedReader levelAccessor, BlockPos blockPos, Random randomSource, SculkSpreader sculkSpreader, boolean bl) {
            if (!this.shouldUpdate(levelAccessor, blockPos, sculkSpreader.isWorldGeneration)) {
                return;
            }
            if (this.updateDelay > 0) {
                --this.updateDelay;
                return;
            }
            BlockState blockState = levelAccessor.getBlockState(this.pos);
            SculkBehaviour sculkBehaviour = ChargeCursor.getBlockBehaviour(blockState);
            if (bl && sculkBehaviour.attemptSpreadVein(levelAccessor, this.pos, blockState, this.facings, sculkSpreader.isWorldGeneration())) {
                if (sculkBehaviour.canChangeBlockStateOnSpread()) {
                    blockState = levelAccessor.getBlockState(this.pos);
                    sculkBehaviour = ChargeCursor.getBlockBehaviour(blockState);
                }
                levelAccessor.playSound(null, this.pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            this.charge = sculkBehaviour.attemptUseCharge(this, levelAccessor, blockPos, randomSource, sculkSpreader, bl);
            if (this.charge <= 0) {
                sculkBehaviour.onDischarged(levelAccessor, blockState, this.pos, randomSource);
                return;
            }
            BlockPos blockPos2 = ChargeCursor.getValidMovementPos(levelAccessor, this.pos, randomSource);
            if (blockPos2 != null) {
                sculkBehaviour.onDischarged(levelAccessor, blockState, this.pos, randomSource);
                this.pos = blockPos2.immutable();
                if (sculkSpreader.isWorldGeneration() && !this.pos.closerThan(new Vector3i(blockPos.getX(), this.pos.getY(), blockPos.getZ()), 15.0)) {
                    this.charge = 0;
                    return;
                }
                blockState = levelAccessor.getBlockState(blockPos2);
            }
            if (blockState.getBlock() instanceof SculkBehaviour) {
                this.facings = MultifaceBlock.availableFaces(blockState);
            }
            this.decayDelay = sculkBehaviour.updateDecayDelay(this.decayDelay);
            this.updateDelay = sculkBehaviour.getSculkSpreadDelay();
        }

        void mergeWith(ChargeCursor chargeCursor) {
            this.charge += chargeCursor.charge;
            chargeCursor.charge = 0;
            this.updateDelay = Math.min(this.updateDelay, chargeCursor.updateDelay);
        }

        private static SculkBehaviour getBlockBehaviour(BlockState blockState) {
            SculkBehaviour sculkBehaviour;
            Block block = blockState.getBlock();
            return block instanceof SculkBehaviour ? (sculkBehaviour = (SculkBehaviour)(block)) : SculkBehaviour.DEFAULT;
        }

        private static List<Vector3i> getRandomizedNonCornerNeighbourOffsets(Random randomSource) {
            return Util.shuffledCopy(NON_CORNER_NEIGHBOURS, randomSource);
        }

        @Nullable
        private static BlockPos getValidMovementPos(ISeedReader levelAccessor, BlockPos blockPos, Random randomSource) {
            BlockPos.Mutable mutableBlockPos = blockPos.mutable();
            BlockPos.Mutable mutableBlockPos2 = blockPos.mutable();
            for (Vector3i vec3i : ChargeCursor.getRandomizedNonCornerNeighbourOffsets(randomSource)) {
                mutableBlockPos2.setWithOffset(blockPos, vec3i);
                BlockState blockState = levelAccessor.getBlockState(mutableBlockPos2);
                if (!(blockState.getBlock() instanceof SculkBehaviour) || !ChargeCursor.isMovementUnobstructed(levelAccessor, blockPos, mutableBlockPos2)) continue;
                mutableBlockPos.set(mutableBlockPos2);
                if (!SculkVeinBlock.hasSubstrateAccess(levelAccessor, blockState, mutableBlockPos2)) continue;
                break;
            }
            return mutableBlockPos.equals(blockPos) ? null : mutableBlockPos;
        }

        private static boolean isMovementUnobstructed(ISeedReader levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
            if (blockPos.distManhattan(blockPos2) == 1) {
                return true;
            }
            BlockPos blockPos3 = blockPos2.subtract(blockPos);
            Direction direction = Direction.fromAxisAndDirection(Direction.Axis.X, blockPos3.getX() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction direction2 = Direction.fromAxisAndDirection(Direction.Axis.Y, blockPos3.getY() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction direction3 = Direction.fromAxisAndDirection(Direction.Axis.Z, blockPos3.getZ() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            if (blockPos3.getX() == 0) {
                return ChargeCursor.isUnobstructed(levelAccessor, blockPos, direction2) || ChargeCursor.isUnobstructed(levelAccessor, blockPos, direction3);
            }
            if (blockPos3.getY() == 0) {
                return ChargeCursor.isUnobstructed(levelAccessor, blockPos, direction) || ChargeCursor.isUnobstructed(levelAccessor, blockPos, direction3);
            }
            return ChargeCursor.isUnobstructed(levelAccessor, blockPos, direction) || ChargeCursor.isUnobstructed(levelAccessor, blockPos, direction2);
        }

        private static boolean isUnobstructed(ISeedReader levelAccessor, BlockPos blockPos, Direction direction) {
            BlockPos blockPos2 = blockPos.relative(direction);
            return !levelAccessor.getBlockState(blockPos2).isFaceSturdy(levelAccessor, blockPos2, direction.getOpposite());
        }
    }
}
