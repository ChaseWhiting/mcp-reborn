package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.entity.monster.*;
import net.minecraft.entity.monster.bogged.BoggedEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.Heightmap;

public class EntitySpawnPlacementRegistry {
   private static final Map<EntityType<?>, EntitySpawnPlacementRegistry.Entry> DATA_BY_TYPE = Maps.newHashMap();

   private static <T extends Mob> void register(EntityType<T> entity, EntitySpawnPlacementRegistry.PlacementType placementType, Heightmap.Type type, EntitySpawnPlacementRegistry.IPlacementPredicate<T> tiPlacementPredicate) {
      EntitySpawnPlacementRegistry.Entry entityspawnplacementregistry$entry = DATA_BY_TYPE.put(entity, new EntitySpawnPlacementRegistry.Entry(type, placementType, tiPlacementPredicate));
      if (entityspawnplacementregistry$entry != null) {
         throw new IllegalStateException("Duplicate registration for type " + Registry.ENTITY_TYPE.getKey(entity));
      }
   }

   private static <T extends Mob> void register(EntityType<T>[] entities, EntitySpawnPlacementRegistry.PlacementType placementType, Heightmap.Type type, EntitySpawnPlacementRegistry.IPlacementPredicate<T> tiPlacementPredicate) {
      for (EntityType<T> entity : entities) {
         register(entity, placementType, type, tiPlacementPredicate);
      }
   }



   public static EntitySpawnPlacementRegistry.PlacementType getPlacementType(EntityType<?> p_209344_0_) {
      EntitySpawnPlacementRegistry.Entry entityspawnplacementregistry$entry = DATA_BY_TYPE.get(p_209344_0_);
      return entityspawnplacementregistry$entry == null ? EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS : entityspawnplacementregistry$entry.placement;
   }

   public static Heightmap.Type getHeightmapType(@Nullable EntityType<?> p_209342_0_) {
      EntitySpawnPlacementRegistry.Entry entityspawnplacementregistry$entry = DATA_BY_TYPE.get(p_209342_0_);
      return entityspawnplacementregistry$entry == null ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : entityspawnplacementregistry$entry.heightMap;
   }

   public static <T extends Entity> boolean checkSpawnRules(EntityType<T> p_223515_0_, IServerWorld p_223515_1_, SpawnReason p_223515_2_, BlockPos p_223515_3_, Random p_223515_4_) {
      EntitySpawnPlacementRegistry.Entry entityspawnplacementregistry$entry = DATA_BY_TYPE.get(p_223515_0_);
      return entityspawnplacementregistry$entry == null || entityspawnplacementregistry$entry.predicate.test((EntityType)p_223515_0_, p_223515_1_, p_223515_2_, p_223515_3_, p_223515_4_);
   }

   static {
      register(EntityType.COD, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
      register(EntityType.DOLPHIN, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DolphinEntity::checkDolphinSpawnRules);
      register(EntityType.DROWNED, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DrownedEntity::checkDrownedSpawnRules);
      register(EntityType.GUARDIAN, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GuardianEntity::checkGuardianSpawnRules);
      register(EntityType.PUFFERFISH, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
      register(EntityType.SALMON, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
      register(EntityType.SQUID, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SquidEntity::checkSquidSpawnRules);
      register(EntityType.TROPICAL_FISH, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AbstractFishEntity::checkFishSpawnRules);
      register(EntityType.BAT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BatEntity::checkBatSpawnRules);
      register(EntityType.BLAZE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
      register(EntityType.CAVE_SPIDER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.CHICKEN, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.COW, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.CREEPER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.DONKEY, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.ENDERMAN, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.ENDERMITE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndermiteEntity::checkEndermiteSpawnRules);
      register(EntityType.ENDER_DRAGON, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.GHAST, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GhastEntity::checkGhastSpawnRules);
      register(EntityType.GIANT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.HORSE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.HUSK, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HuskEntity::checkHuskSpawnRules);
      register(EntityType.IRON_GOLEM, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.LLAMA, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.MAGMA_CUBE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MagmaCubeEntity::checkMagmaCubeSpawnRules);
      register(EntityType.MOOSHROOM, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MooshroomEntity::checkMushroomSpawnRules);
      register(EntityType.MULE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.OCELOT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, OcelotEntity::checkOcelotSpawnRules);
      register(EntityType.PARROT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, ParrotEntity::checkParrotSpawnRules);
      register(EntityType.PIG, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.HOGLIN, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HoglinEntity::checkHoglinSpawnRules);
      register(EntityType.PIGLIN, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PiglinEntity::checkPiglinSpawnRules);
      register(EntityType.PILLAGER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PatrollerEntity::checkPatrollingMonsterSpawnRules);
      register(EntityType.POLAR_BEAR, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PolarBearEntity::checkPolarBearSpawnRules);
      register(EntityType.RABBIT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RabbitEntity::checkRabbitSpawnRules);
      register(EntityType.SHEEP, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.SILVERFISH, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SilverfishEntity::checkSliverfishSpawnRules);
      register(EntityType.SKELETON, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.BOGGED, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BoggedEntity::checkBoggedRules);
      register(EntityType.GREAT_HUNGER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GreatHungerEntity::checkSpawnRules);


      register(EntityType.SKELETON_HORSE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.SLIME, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SlimeEntity::checkSlimeSpawnRules);
      register(EntityType.SNOW_GOLEM, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.SPIDER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.STRAY, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, StrayEntity::checkStraySpawnRules);
      register(EntityType.STRIDER, EntitySpawnPlacementRegistry.PlacementType.IN_LAVA, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, StriderEntity::checkStriderSpawnRules);
      register(EntityType.TURTLE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TurtleEntity::checkTurtleSpawnRules);
      register(EntityType.VILLAGER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.WITCH, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.WITHER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.WITHER_SKELETON, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.WOLF, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.ZOMBIE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.ZOMBIE_HORSE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.ZOMBIFIED_PIGLIN, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ZombifiedPiglinEntity::checkZombifiedPiglinSpawnRules);
      register(EntityType.ZOMBIE_VILLAGER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.CAT, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.ELDER_GUARDIAN, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GuardianEntity::checkGuardianSpawnRules);
      register(EntityType.EVOKER, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.SHAMAN, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.FOX, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.ILLUSIONER, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.PANDA, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.PHANTOM, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.RAVAGER, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.SHULKER, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
      register(EntityType.TRADER_LLAMA, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      register(EntityType.VEX, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.MARAUDER, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.VINDICATOR, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
      register(EntityType.WANDERING_TRADER, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
   }

   static class Entry {
      private final Heightmap.Type heightMap;
      private final EntitySpawnPlacementRegistry.PlacementType placement;
      private final EntitySpawnPlacementRegistry.IPlacementPredicate<?> predicate;

      public Entry(Heightmap.Type p_i51522_1_, EntitySpawnPlacementRegistry.PlacementType p_i51522_2_, EntitySpawnPlacementRegistry.IPlacementPredicate<?> p_i51522_3_) {
         this.heightMap = p_i51522_1_;
         this.placement = p_i51522_2_;
         this.predicate = p_i51522_3_;
      }
   }

   @FunctionalInterface
   public interface IPlacementPredicate<T extends Entity> {
      boolean test(EntityType<T> p_test_1_, IServerWorld p_test_2_, SpawnReason p_test_3_, BlockPos p_test_4_, Random p_test_5_);
   }

   public static enum PlacementType {
      ON_GROUND,
      IN_WATER,
      NO_RESTRICTIONS,
      IN_LAVA;
   }
}
