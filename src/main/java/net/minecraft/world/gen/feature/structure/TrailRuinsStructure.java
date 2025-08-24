package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;

public class TrailRuinsStructure extends JigsawStructure {
    public TrailRuinsStructure(Codec<VillageConfig> codec) {
        super(codec, /*startY*/ -15, /*expansionHack*/ false, /*projectToHeightmap*/ true);
    }


}
