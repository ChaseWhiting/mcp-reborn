package net.minecraft.block.sculk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.UniformInt;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class SculkPatchConfiguration implements IFeatureConfig {
    public static final Codec<SculkPatchConfiguration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.intRange(1, 32)
                    .fieldOf("charge_count").forGetter(SculkPatchConfiguration::chargeCount), Codec.intRange(1, 500)
                    .fieldOf("amount_per_charge").forGetter(SculkPatchConfiguration::amountPerCharge), Codec.intRange(1, 64)
                    .fieldOf("spread_attempts").forGetter(SculkPatchConfiguration::spreadAttempts), Codec.intRange((int)0, (int)8)
                    .fieldOf("growth_rounds").forGetter(SculkPatchConfiguration::growthRounds), Codec.intRange((int)0, (int)8)
                    .fieldOf("spread_rounds").forGetter(SculkPatchConfiguration::spreadRounds), UniformInt.CODEC.fieldOf("extra_rare_growths")
                    .forGetter(SculkPatchConfiguration::extraRareGrowths), Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("catalyst_chance")
                    .forGetter(SculkPatchConfiguration::catalystChance)).apply(instance, SculkPatchConfiguration::new));

    private final int chargeCount; private final int amountPerCharge; private final int spreadAttempts; private final int growthRounds; private final int spreadRounds;
    private final UniformInt extraRareGrowths; private final float catalystChance;

    public SculkPatchConfiguration(int chargeCount, int amountPerCharge, int spreadAttempts, int growthRounds, int spreadRounds, UniformInt extraRareGrowths, float catalystChance) {
        this.chargeCount = chargeCount;
        this.amountPerCharge = amountPerCharge;
        this.spreadAttempts = spreadAttempts;
        this.growthRounds = growthRounds;
        this.spreadRounds = spreadRounds;
        this.catalystChance = catalystChance;
        this.extraRareGrowths = extraRareGrowths;
    }

    public int chargeCount() {
        return chargeCount;
    }

    public int amountPerCharge() {
        return amountPerCharge;
    }

    public int spreadAttempts() {
        return spreadAttempts;
    }

    public int growthRounds() {
        return growthRounds;
    }

    public int spreadRounds() {
        return spreadRounds;
    }

    public UniformInt extraRareGrowths() {
        return extraRareGrowths;
    }

    public float catalystChance() {
        return catalystChance;
    }
}
