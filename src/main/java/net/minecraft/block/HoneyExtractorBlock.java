package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class HoneyExtractorBlock extends CraftingTableBlock {
    protected HoneyExtractorBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result) {
        return ActionResultType.PASS;
    }
}