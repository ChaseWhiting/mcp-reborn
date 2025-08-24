package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;

import java.util.*;

public class AxolotlAttackablesSensor extends NearestVisibleLivingEntitySensor {
   public static final float TARGET_DETECTION_DISTANCE = 8.0f;

   private final List<EntityType<?>> AXOLOTL_ALWAYS_HOSTILES = List.of(EntityType.DROWNED, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN);
   private final List<EntityType<?>> AXOLOTL_HUNT_TARGETS = List.of(EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.COD, EntityType.SQUID);

   @Override
   protected boolean isMatchingEntity(LivingEntity livingEntity, LivingEntity livingEntity2) {
      if (isEntityTargetable(livingEntity, livingEntity2) && (this.isHostileTarget(livingEntity2) || this.isHuntTarget(livingEntity, livingEntity2))) {
         return this.isClose(livingEntity, livingEntity2) && livingEntity2.isInWaterOrBubble();
      }
      return false;
   }

   private boolean isHuntTarget(LivingEntity livingEntity, LivingEntity livingEntity2) {
      return !livingEntity.getBrain().hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN) && AXOLOTL_HUNT_TARGETS.contains(livingEntity2.getType());
   }

   private boolean isHostileTarget(LivingEntity livingEntity) {
      return AXOLOTL_ALWAYS_HOSTILES.contains(livingEntity.getType());
   }

   private boolean isClose(LivingEntity livingEntity, LivingEntity livingEntity2) {
      return livingEntity2.distanceToSqr(livingEntity) <= 64.0;
   }

   @Override
   protected MemoryModuleType<LivingEntity> getMemory() {
      return MemoryModuleType.NEAREST_ATTACKABLE;
   }


}