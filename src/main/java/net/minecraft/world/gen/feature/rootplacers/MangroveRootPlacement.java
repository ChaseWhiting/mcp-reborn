package net.minecraft.world.gen.feature.rootplacers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;

public class MangroveRootPlacement {
    public static final Codec<MangroveRootPlacement> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(Registry.BLOCK.listOf()
                                    .xmap(ImmutableSet::copyOf, ImmutableList::copyOf)
                                    .optionalFieldOf("can_grow_through",ImmutableSet.of())
                                    .forGetter(brp -> brp.canGrowThrough),
                            Registry.BLOCK.listOf()
                                    .xmap(ImmutableSet::copyOf, ImmutableList::copyOf)
                                    .optionalFieldOf("muddy_roots_in",ImmutableSet.of())
                                    .forGetter(brp -> brp.muddyRootsIn)
                            ,BlockStateProvider.CODEC.fieldOf("muddy_roots_provider")
                                    .forGetter(mangroveRootPlacement -> mangroveRootPlacement.muddyRootsProvider),
                            Codec.intRange((int)1, (int)12).fieldOf("max_root_width")
                                    .forGetter(mangroveRootPlacement -> mangroveRootPlacement.maxRootWidth),
                            Codec.intRange((int)1, (int)64).fieldOf("max_root_length").forGetter(mangroveRootPlacement -> mangroveRootPlacement.maxRootLength),
                            Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("random_skew_chance")
                                    .forGetter(mangroveRootPlacement -> Float.valueOf(mangroveRootPlacement.randomSkewChance)))
                            .apply(instance, MangroveRootPlacement::new));


    public MangroveRootPlacement(ImmutableSet<Block> canGrowThrough, ImmutableSet<Block> muddyRootsIn, BlockStateProvider muddyRootsProvider, int maxRootWidth, int maxRootLength, float randomSkewChance) {
        this.canGrowThrough = canGrowThrough;
        this.muddyRootsIn = muddyRootsIn;
        this.muddyRootsProvider = muddyRootsProvider;
        this.maxRootLength = maxRootLength;
        this.maxRootWidth = maxRootLength;
        this.randomSkewChance = randomSkewChance;
    }

    public int maxRootWidth;
    public int maxRootLength;
    public float randomSkewChance;
    public BlockStateProvider muddyRootsProvider;
    private ImmutableSet<Block> muddyRootsIn;
    private ImmutableSet<Block> canGrowThrough;




}
