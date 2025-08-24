package net.minecraft.block;

import net.minecraft.tileentity.BrushableBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class SimpleBrushableBlock extends BrushableBlock {

    public SimpleBrushableBlock(Block block, Properties properties, SoundEvent soundEvent, SoundEvent soundEvent2) {
        super(block, properties, soundEvent, soundEvent2);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Object object;
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof BrushableBlockEntity) {
            object = (BrushableBlockEntity) tileEntity;
            ((BrushableBlockEntity)object).checkReset();
        }
    }

    public long coolDownEndsAtTick(long l) {
        if (this == Blocks.SNOW_BLOCK) return l + 2;

        if (!(this == Blocks.SUSPICIOUS_CLAY)) return super.coolDownEndsAtTick(l);

        return l + 2L;
    }

    public int brushCountToComplete(BlockPos worldPos, World level) {
        if (this == Blocks.CRACKED_NETHER_BRICKS) {
            BrushableBlockEntity brushableBlock = (BrushableBlockEntity) level.getBlockEntity(worldPos);
            if (brushableBlock.getItem().isEmpty()) {
                return 4;
            }
        }

        if (this == Blocks.SNOW_BLOCK) return 4;

        if (!(this == Blocks.SUSPICIOUS_CLAY)) return super.brushCountToComplete(worldPos, level);

        return 7;
    }

    @Override
    public int getCompletionState(int brushCount, BlockPos worldPos, World level) {
        if (this.brushCountToComplete(worldPos, level) == 4) {
            return switch (brushCount) {
                case 0 -> 1;
                case 1 -> 2;
                case 2, 3 -> 3;
                default -> 3;
            };
        }

        if (this.brushCountToComplete(worldPos, level) == 7) {
            return switch (brushCount) {
                case 0 -> 0;
                case 1, 2 -> 1;
                case 3, 4 -> 2;
                case 5, 6, 7 -> 3;
                default -> 3;
            };
        }


        return super.getCompletionState(brushCount, worldPos, level);
    }
}
