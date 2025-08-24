package net.minecraft.entity.ai.brain.sensor;

import java.util.Random;
import java.util.Set;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TargetingConditions;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public abstract class Sensor<E extends LivingEntity> {
   private static final Random RANDOM = new Random();
   private static final EntityPredicate TARGET_CONDITIONS = (new EntityPredicate()).range(16.0D).allowSameTeam().allowNonAttackable();
   private static final EntityPredicate TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = (new EntityPredicate()).range(16.0D).allowSameTeam().allowNonAttackable().ignoreInvisibilityTesting();
   private final int scanRate;
   private long timeToTick;


   private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = TargetingConditions.forCombat().range(16.0).ignoreInvisibilityTesting();
   private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT = TargetingConditions.forCombat().range(16.0).ignoreLineOfSight();
   private static final TargetingConditions ATTACK_TARGET_CONDITIONS = TargetingConditions.forCombat().range(16.0);


   public Sensor(int p_i50301_1_) {
      this.scanRate = p_i50301_1_;
      this.timeToTick = (long)RANDOM.nextInt(p_i50301_1_);
   }

   public static boolean isEntityAttackable(ServerWorld serverLevel, LivingEntity livingEntity, LivingEntity livingEntity2) {
      if (livingEntity.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, livingEntity2)) {
         return ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test(serverLevel, livingEntity, livingEntity2);
      }
      return ATTACK_TARGET_CONDITIONS.test(serverLevel, livingEntity, livingEntity2);
   }

   public Sensor() {
      this(20);
   }

   public final void tick(ServerWorld p_220973_1_, E p_220973_2_) {
      if (--this.timeToTick <= 0L) {
         this.timeToTick = (long)this.scanRate;
         this.doTick(p_220973_1_, p_220973_2_);
      }

   }

   protected abstract void doTick(ServerWorld p_212872_1_, E p_212872_2_);

   public abstract Set<MemoryModuleType<?>> requires();

   protected static boolean isEntityTargetable(LivingEntity p_242316_0_, LivingEntity p_242316_1_) {
      return p_242316_0_.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, p_242316_1_) ? TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test(p_242316_0_, p_242316_1_) : TARGET_CONDITIONS.test(p_242316_0_, p_242316_1_);
   }

   public static boolean isEntityTargetable(ServerWorld serverLevel, LivingEntity livingEntity, LivingEntity livingEntity2) {
      if (livingEntity.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, livingEntity2)) {
         return TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test(livingEntity, livingEntity2);
      }
      return TARGET_CONDITIONS.test(livingEntity, livingEntity2);
   }
}