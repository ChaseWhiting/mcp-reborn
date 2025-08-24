package net.minecraft.block.family;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleWoodFamily {
    private final String id;

    @Nullable
    private final Block planks;
    @Nullable
    private final Block stairs;
    @Nullable
    private final Block slab;
    @Nullable
    private final Block fence;
    @Nullable
    private final Block fenceGate;
    @Nullable
    private final Block door;
    @Nullable
    private final Block trapdoor;
    @Nullable
    private final Block pressurePlate;
    @Nullable
    private final Block button;
    @Nullable
    private final Block log;
    @Nullable
    private final Block strippedLog;
    @Nullable
    private final Block wood;
    @Nullable
    private final Block strippedWood;
    @Nullable
    private final Block signBlock;
    @Nullable
    private final Item boatItem;


    public List<Optional<Block>> getAllBlocks() {
        return new ArrayList<>(List.of(this.getPlankBlock(), this.getStairsBlock(), this.getSlabBlock(), this.getFenceBlock(), this.getFenceGateBlock(), this.getDoorBlock(), this.getTrapdoorBlock(), this.getPressurePlateBlock(), this.getButtonBlock(), this.getLogBlock(), this.getWoodBlock(), this.getStrippedLogBlock(), this.getStrippedWoodBlock(), this.getSignBlock()));
    }

    public Optional<Block> getPlankBlock() {
        return Optional.ofNullable(planks);
    }

    public Optional<Item> getBoatItem() {
        return Optional.ofNullable(boatItem);
    }

    public Optional<Block> getStairsBlock() {
        return Optional.ofNullable(stairs);
    }

    public Optional<Block> getSlabBlock() {
        return Optional.ofNullable(slab);
    }

    public Optional<Block> getFenceBlock() {
        return Optional.ofNullable(fence);
    }

    public Optional<Block> getFenceGateBlock() {
        return Optional.ofNullable(fenceGate);
    }

    public Optional<Block> getDoorBlock() {
        return Optional.ofNullable(door);
    }

    public Optional<Block> getTrapdoorBlock() {
        return Optional.ofNullable(trapdoor);
    }

    public Optional<Block> getPressurePlateBlock() {
        return Optional.ofNullable(pressurePlate);
    }

    public Optional<Block> getButtonBlock() {
        return Optional.ofNullable(button);
    }

    public Optional<Block> getLogBlock() {
        return Optional.ofNullable(log);
    }

    public Optional<Block> getStrippedLogBlock() {
        return Optional.ofNullable(strippedLog);
    }

    public Optional<Block> getWoodBlock() {
        return Optional.ofNullable(wood);
    }

    public Optional<Block> getStrippedWoodBlock() {
        return Optional.ofNullable(strippedWood);
    }

    public Optional<Block> getSignBlock() {
        return Optional.ofNullable(signBlock);
    }

    public String getWoodName() {
        return this.id;
    }

    private SimpleWoodFamily(String id, @Nullable Block planks, @Nullable Block stairs, @Nullable Block slab,
                         @Nullable Block fence, @Nullable Block fenceGate, @Nullable Block door,
                         @Nullable Block trapdoor, @Nullable Block pressurePlate, @Nullable Block button, @Nullable Block log, @Nullable Block wood, @Nullable Block strippedLog, @Nullable Block strippedWood, @Nullable Block signBlock, @Nullable Item boatItem) {
        this.planks = planks;
        this.stairs = stairs;
        this.slab = slab;
        this.fence = fence;
        this.fenceGate = fenceGate;
        this.door = door;
        this.trapdoor = trapdoor;
        this.pressurePlate = pressurePlate;
        this.button = button;
        this.log = log;
        this.wood = wood;
        this.strippedLog = strippedLog;
        this.strippedWood = strippedWood;
        this.id = id;
        this.signBlock = signBlock;
        this.boatItem = boatItem;
    }


    public static Builder builder(String id) {
        return new Builder(id);
    }



    public static class Builder {
        @Nullable
        private Block planks;
        @Nullable
        private Block stairs;
        @Nullable
        private Block slab;
        @Nullable
        private Block fence;
        @Nullable
        private Block fenceGate;
        @Nullable
        private Block door;
        @Nullable
        private Block trapdoor;
        @Nullable
        private Block pressurePlate;
        @Nullable
        private Block button;
        @Nullable
        private Block log;
        @Nullable
        private Block strippedLog;
        @Nullable
        private Block wood;
        @Nullable
        private Block strippedWood;
        @Nullable
        private Block sign;
        @Nullable
        private Item boatItem;

        private final String id;

        private Builder(String id) {
            this.id = id;
        }

        public Builder planks(Block planks) {
            this.planks = planks;
            return this;
        }

        public Builder stairs(Block stairs) {
            this.stairs = stairs;
            return this;
        }

        public Builder slab(Block slab) {
            this.slab = slab;
            return this;
        }

        public Builder fence(Block fence) {
            this.fence = fence;
            return this;
        }

        public Builder fenceGate(Block fenceGate) {
            this.fenceGate = fenceGate;
            return this;
        }

        public Builder boat(Item fenceGate) {
            this.boatItem = fenceGate;
            return this;
        }

        public Builder door(Block door) {
            this.door = door;
            return this;
        }

        public Builder trapdoor(Block trapdoor) {
            this.trapdoor = trapdoor;
            return this;
        }

        public Builder pressurePlate(Block pressurePlate) {
            this.pressurePlate = pressurePlate;
            return this;
        }

        public Builder button(Block button) {
            this.button = button;
            return this;
        }

        public Builder log(Block log) {
            this.log = log;
            return this;
        }

        public Builder strippedLog(Block strippedLog) {
            this.strippedLog = strippedLog;
            return this;
        }

        public Builder wood(Block wood) {
            this.wood = wood;
            return this;
        }

        public Builder strippedWood(Block strippedWood) {
            this.strippedWood = strippedWood;
            return this;
        }

        public Builder sign(Block sign) {
            this.sign = sign;
            return this;
        }

        public SimpleWoodFamily build() {
            return new SimpleWoodFamily(id, planks, stairs, slab, fence, fenceGate, door, trapdoor, pressurePlate, button, log, wood, strippedLog, strippedWood, sign, boatItem);
        }
    }

}
