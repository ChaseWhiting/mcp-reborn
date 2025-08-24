package net.minecraft.tags;

import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class AddEntityTags {

    public static void addEntityTags(Map<ResourceLocation, ITag.Builder> entityTagBuilders) {
        entityTagBuilders.put(new ResourceLocation("minecraft", "leashable"), ITag.Builder.tag()
                .add(  EntityType.SLIME, EntityType.MAGMA_CUBE, EntityType.HAPPY_GHAST,
                        EntityType.ALLAY, EntityType.AXOLOTL, EntityType.BEE,
                        EntityType.BOAT, EntityType.CAMEL, EntityType.CAT, EntityType.CHICKEN,
                        EntityType.COW, EntityType.DOLPHIN, EntityType.DONKEY, EntityType.FOX,
                        EntityType.FROG, EntityType.GOAT, EntityType.HOGLIN, EntityType.HORSE,
                        EntityType.IRON_GOLEM, EntityType.LLAMA, EntityType.MOOSHROOM, EntityType.MULE,
                        EntityType.OCELOT, EntityType.PARROT, EntityType.PIG, EntityType.POLAR_BEAR,
                        EntityType.RABBIT, EntityType.SHEEP, EntityType.SKELETON_HORSE, EntityType.SNOW_GOLEM,
                        EntityType.SQUID, EntityType.STRIDER, EntityType.TRADER_LLAMA, EntityType.WOLF,
                        EntityType.ZOGLIN, EntityType.ZOMBIE_HORSE));

        entityTagBuilders.put(new ResourceLocation("minecraft", "followable_friendly_mobs"), ITag.Builder.tag()
                //.add(EntityType.ARMADILLO)
                .add(EntityType.BEE)
                .add(EntityType.CAMEL)
                .add(EntityType.CAT)
                .add(EntityType.CHICKEN)
                .add(EntityType.COW)
                .add(EntityType.DONKEY)
                .add(EntityType.FOX)
                .add(EntityType.GOAT)
                .add(EntityType.HAPPY_GHAST)
                .add(EntityType.HORSE)
                .add(EntityType.LLAMA)
                .add(EntityType.MULE)
                .add(EntityType.OCELOT)
                .add(EntityType.PANDA)
                .add(EntityType.PARROT)
                .add(EntityType.PIG)
                .add(EntityType.POLAR_BEAR)
                .add(EntityType.RABBIT)
                .add(EntityType.SHEEP)
                .add(EntityType.SKELETON_HORSE)
                //.add(EntityType.SNIFFER)
                .add(EntityType.STRIDER)
                .add(EntityType.VILLAGER)
                .add(EntityType.WOLF)
        );
    }
}
