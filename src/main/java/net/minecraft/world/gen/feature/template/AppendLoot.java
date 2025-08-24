package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.random.RandomSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class AppendLoot
implements RuleBlockEntityModifier {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<AppendLoot> CODEC = RecordCodecBuilder.create(instance -> instance.group(ResourceLocation.CODEC.fieldOf("loot_table").forGetter(appendLoot -> appendLoot.lootTable)).apply(instance, AppendLoot::new));
    private final ResourceLocation lootTable;

    public AppendLoot(ResourceLocation resourceLocation) {
        this.lootTable = resourceLocation;
    }

    @Override
    public CompoundNBT apply(RandomSource randomSource, @Nullable CompoundNBT compoundTag) {
        CompoundNBT compoundTag2 = compoundTag == null ? new CompoundNBT() : compoundTag.copy();
        ResourceLocation.CODEC.encodeStart(NBTDynamicOps.INSTANCE, this.lootTable).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(tag -> compoundTag2.put("LootTable", tag));
        compoundTag2.putLong("LootTableSeed", randomSource.nextLong());
        return compoundTag2;
    }

    @Override
    public RuleBlockEntityModifierType<?> getType() {
        return RuleBlockEntityModifierType.APPEND_LOOT;
    }
}
