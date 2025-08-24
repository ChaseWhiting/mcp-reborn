package net.minecraft.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import net.minecraft.util.IStringSerializable;

public interface WeatheringCopper
extends ChangeOverTimeBlock<WeatheringCopper.WeatherState> {


    public static final Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
            .put(
            Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER)
            .put(Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER)
            .put(Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER).put(

            Blocks.CUT_COPPER,
            Blocks.EXPOSED_CUT_COPPER).put(
            Blocks.EXPOSED_CUT_COPPER,
            Blocks.WEATHERED_CUT_COPPER).put(
            Blocks.WEATHERED_CUT_COPPER,
            Blocks.OXIDIZED_CUT_COPPER).put(

            Blocks.CUT_COPPER_SLAB,
            Blocks.EXPOSED_CUT_COPPER_SLAB)

            .put(Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS)
            .put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS)
            .put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS)

            .put(Blocks.CHISELED_COPPER, Blocks.EXPOSED_CHISELED_COPPER)
            .put(Blocks.EXPOSED_CHISELED_COPPER, Blocks.WEATHERED_CHISELED_COPPER)
            .put(Blocks.WEATHERED_CHISELED_COPPER, Blocks.OXIDIZED_CHISELED_COPPER)

            .put(Blocks.COPPER_DOOR, Blocks.EXPOSED_COPPER_DOOR)
            .put(Blocks.EXPOSED_COPPER_DOOR, Blocks.WEATHERED_COPPER_DOOR)
            .put(Blocks.WEATHERED_COPPER_DOOR, Blocks.OXIDIZED_COPPER_DOOR)

            .put(Blocks.COPPER_BUTTON, Blocks.EXPOSED_COPPER_BUTTON)
            .put(Blocks.EXPOSED_COPPER_BUTTON, Blocks.WEATHERED_COPPER_BUTTON)
            .put(Blocks.WEATHERED_COPPER_BUTTON, Blocks.OXIDIZED_COPPER_BUTTON)

            .put(Blocks.COPPER_TRAPDOOR, Blocks.EXPOSED_COPPER_TRAPDOOR)
            .put(Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WEATHERED_COPPER_TRAPDOOR)
            .put(Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.OXIDIZED_COPPER_TRAPDOOR)


            .put(
            Blocks.EXPOSED_CUT_COPPER_SLAB,
            Blocks.WEATHERED_CUT_COPPER_SLAB).put(
            Blocks.WEATHERED_CUT_COPPER_SLAB,
            Blocks.OXIDIZED_CUT_COPPER_SLAB).put(

            Blocks.COPPER_GRATE,
            Blocks.EXPOSED_COPPER_GRATE).put(
            Blocks.EXPOSED_COPPER_GRATE,
            Blocks.WEATHERED_COPPER_GRATE).put(
            Blocks.WEATHERED_COPPER_GRATE,
            Blocks.OXIDIZED_COPPER_GRATE).put(

                    Blocks.COPPER_BULB, Blocks.EXPOSED_COPPER_BULB)
            .put(Blocks.EXPOSED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB)
            .put(Blocks.WEATHERED_COPPER_BULB, Blocks.OXIDIZED_COPPER_BULB).build());
    public static final Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> NEXT_BY_BLOCK.get().inverse());

    public static Optional<Block> getPrevious(Block block) {
        return Optional.ofNullable((Block)PREVIOUS_BY_BLOCK.get().get((Object)block));
    }

    public static Block getFirst(Block block) {
        Block block2 = block;
        Block block3 = (Block)PREVIOUS_BY_BLOCK.get().get((Object)block2);
        while (block3 != null) {
            block2 = block3;
            block3 = (Block)PREVIOUS_BY_BLOCK.get().get((Object)block2);
        }
        return block2;
    }

    public static Optional<BlockState> getPrevious(BlockState blockState) {
        return WeatheringCopper.getPrevious(blockState.getBlock()).map(block -> block.withPropertiesOf(blockState));
    }

    public static Optional<Block> getNext(Block block) {
        return Optional.ofNullable((Block)NEXT_BY_BLOCK.get().get((Object)block));
    }

    public static BlockState getFirst(BlockState blockState) {
        return WeatheringCopper.getFirst(blockState.getBlock()).withPropertiesOf(blockState);
    }

    @Override
    default public Optional<BlockState> getNext(BlockState blockState) {
        return WeatheringCopper.getNext(blockState.getBlock()).map(block -> block.withPropertiesOf(blockState));
    }

    @Override
    default public float getChanceModifier() {
        if (this.getAge() == WeatherState.UNAFFECTED) {
            return 0.75f;
        }
        return 1.0f;
    }

    public static enum WeatherState implements IStringSerializable
    {
        UNAFFECTED("unaffected"),
        EXPOSED("exposed"),
        WEATHERED("weathered"),
        OXIDIZED("oxidized");

        public static final Codec<WeatherState> CODEC;

        private final String name;

        private WeatherState(String string2) {
            this.name = string2;
        }

        public String getName() {
            return name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static WeatherState fromName(String name) {
            for (WeatherState s : WeatherState.values()) {
                if (s.name.equals(name)) {
                    return s;
                }
            }
            return UNAFFECTED;
        }

        static {
            CODEC = IStringSerializable.fromEnum(WeatherState::values);
        }
    }
}

