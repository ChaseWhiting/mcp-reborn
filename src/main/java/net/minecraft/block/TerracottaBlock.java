package net.minecraft.block;

import net.minecraft.block.sounds.AmbientDesertBlockSoundsPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class TerracottaBlock extends Block{
    public TerracottaBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
        AmbientDesertBlockSoundsPlayer.playAmbientBlockSounds(p_180655_1_, p_180655_2_, p_180655_3_, p_180655_4_);
    }
}
