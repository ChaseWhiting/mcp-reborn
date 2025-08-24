package net.minecraft.entity.neighbor;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class TheodorePetersonEntity extends Monster {

    public TheodorePetersonEntity(EntityType<? extends TheodorePetersonEntity> type, World world) {
        super(type, world);
    }





    public static void playChaseSound(PlayerEntity player) {
        World world = player.level;
        if (world != null) {

        }
    }

    public static enum NeighborState implements IStringSerializable {
        IDLE("idle"),
        PLACE_TRAPS("place_traps"),

        FIGHT("fight");

        private final String name;

        NeighborState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public boolean hasIdleState() {
            return this == IDLE;
        }

        public enum IdleState implements IStringSerializable {
            WALK("walk"),
            SLEEP("sleep");

            private final String name;

            IdleState(String name) {
                this.name = name;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }
    }
}
