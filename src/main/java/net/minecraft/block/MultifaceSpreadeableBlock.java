package net.minecraft.block;

import net.minecraft.block.sculk.MultifaceSpreader;

public abstract class MultifaceSpreadeableBlock
extends MultifaceBlock {
    public MultifaceSpreadeableBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    public abstract MultifaceSpreader getSpreader();
}

