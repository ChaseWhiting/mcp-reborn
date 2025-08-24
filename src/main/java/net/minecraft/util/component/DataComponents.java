package net.minecraft.util.component;

import net.minecraft.item.Rarity;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.util.codec.ByteBufCodecs;
import net.minecraft.util.registry.Registry;

import java.util.function.UnaryOperator;

public class DataComponents {
    static final EncoderCache ENCODER_CACHE = new EncoderCache(512);
    public static final DataComponentType<Integer> MAX_STACK_SIZE = DataComponents.register("max_stack_size", builder -> builder.persistent(ExtraCodecs.intRange(1, 99)).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DataComponentType<Integer> MAX_DAMAGE = DataComponents.register("max_damage", builder -> builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DataComponentType<Integer> DAMAGE = DataComponents.register("damage", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DataComponentType<Unit> UNBREAKABLE = DataComponents.register("unbreakable", builder -> builder.persistent(Unit.CODEC).networkSynchronized(Unit.STREAM_CODEC));
//    public static final DataComponentType<ITextComponent> CUSTOM_NAME = DataComponents.register("custom_name", builder -> builder.persistent(ComponentSerialization.CODEC).networkSynchronized(ComponentSerialization.STREAM_CODEC).cacheEncoding());
//    public static final DataComponentType<ITextComponent> ITEM_NAME = DataComponents.register("item_name", builder -> builder.persistent(ComponentSerialization.CODEC).networkSynchronized(ComponentSerialization.STREAM_CODEC).cacheEncoding());
//    public static final DataComponentType<ItemLore> LORE = DataComponents.register("lore", builder -> builder.persistent(ItemLore.CODEC).networkSynchronized(ItemLore.STREAM_CODEC).cacheEncoding());
    public static final DataComponentType<Rarity> RARITY = DataComponents.register("rarity", builder -> builder.persistent(Rarity.CODEC).networkSynchronized(Rarity.STREAM_CODEC));
//    public static final DataComponentType<ItemEnchantments> ENCHANTMENTS = DataComponents.register("enchantments", builder -> builder.persistent(ItemEnchantments.CODEC).networkSynchronized(ItemEnchantments.STREAM_CODEC).cacheEncoding());
























    public static DataComponentType<?> bootstrap(Registry<DataComponentType<?>> registry) {
        return MAX_STACK_SIZE;
    }

    private static <T> DataComponentType<T> register(String string, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        return Registry.register(Registry.DATA_COMPONENT_TYPE, string, (unaryOperator.apply(DataComponentType.builder())).build());
    }
}
