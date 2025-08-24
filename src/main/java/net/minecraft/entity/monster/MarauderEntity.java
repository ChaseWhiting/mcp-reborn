package net.minecraft.entity.monster;

import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.tool.AxeItem;
import net.minecraft.item.tool.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class MarauderEntity extends AbstractIllagerEntity{
   private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = (p_213678_0_) -> {
      return p_213678_0_ == Difficulty.NORMAL || p_213678_0_ == Difficulty.HARD;
   };
   private boolean droppedAxe;
   private int stunnedTick;
   public boolean shieldDisabled;

   public MarauderEntity(EntityType<? extends MarauderEntity> entity, World world) {
      super(entity, world);
   }

   public static ItemStack AXE_ITEM = new ItemStack(Items.IRON_AXE);
   static {
      Random random = new Random();
      AXE_ITEM.setDamageValue(random.nextInt(AXE_ITEM.getMaxDamage()));
   }



   private void dropItem(ItemStack item) {
      if (!item.isEmpty() && !this.level.isClientSide) {
         ItemEntity itementity = new ItemEntity(this.level, this.getX() + this.getLookAngle().x, this.getY() + 1.0D, this.getZ() + this.getLookAngle().z, item);
         itementity.setPickUpDelay(40);
         itementity.setThrower(this.getUUID());
         this.level.addFreshEntity(itementity);
      }
   }


   private List<Item> importantItems = Arrays.asList(
           Items.DIAMOND_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.NETHERITE_AXE, Items.WOODEN_AXE, Items.SHIELD, Items.NETHERITE_SHIELD
   );

   public void dealWithItems() {
      ItemStack mainHandItem = this.getMainHandItem();
      ItemStack offHandItem = this.getOffhandItem();

      handleMainHand(mainHandItem, offHandItem);
      handleOffHand(mainHandItem, offHandItem);
   }

   private void handleMainHand(ItemStack mainHandItem, ItemStack offHandItem) {
      if (!mainHandItem.isEmpty() && offHandItem.isEmpty()) {
         if (!isShieldOrAxe(mainHandItem) && !isImportantItem(mainHandItem)) {
            dropItem(mainHandItem);
            this.setItemSlot(EquipmentSlotType.MAINHAND, Items.AIR.getDefaultInstance());
         } else if (mainHandItem.getItem() instanceof ShieldItem) {
            swapSlots(EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
         }
      }
   }

   private void handleOffHand(ItemStack mainHandItem, ItemStack offHandItem) {
      if (offHandItem.getItem() instanceof ShieldItem && this.shieldDisabled) {
         dropItem(offHandItem);
         this.setItemSlot(EquipmentSlotType.OFFHAND, Items.AIR.getDefaultInstance());
      }

      if (mainHandItem.isEmpty() && offHandItem.getItem() instanceof AxeItem) {
         swapSlots(EquipmentSlotType.OFFHAND, EquipmentSlotType.MAINHAND);
      }

      if (mainHandItem.getItem() instanceof ShieldItem && offHandItem.getItem() instanceof AxeItem) {
         swapSlots(EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
      }
   }

   private boolean isShieldOrAxe(ItemStack itemStack) {
      return itemStack.getItem() instanceof ShieldItem || itemStack.getItem() instanceof AxeItem;
   }

   private boolean isImportantItem(ItemStack itemStack) {
      return importantItems.contains(itemStack.getItem());
   }

   public void swapSlots(EquipmentSlotType slot1, EquipmentSlotType slot2) {
      ItemStack item1 = this.getItemBySlot(slot1);
      ItemStack item2 = this.getItemBySlot(slot2);
      ItemStack item3 = item1.copy();
      this.setItemSlot(slot1, item2);
      this.setItemSlot(slot2, item3);
   }



   @Override
   public void tick() {
      super.tick();

      dealWithItems();


      List<ItemEntity> nearbyItems = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(7D));

      boolean hasDesiredItems = false;

      if (!nearbyItems.isEmpty() && this.getTarget() == null) {
         for (ItemEntity item : nearbyItems) {
            boolean isShieldItem = item.getItem().getItem() instanceof ShieldItem;
            boolean isAxeItem = item.getItem().getItem() instanceof AxeItem;
            boolean isTieredItem = item.getItem().getItem() instanceof TieredItem;
            boolean isHandItemTiered = this.getMainHandItem().getItem() instanceof TieredItem;

            if ((isShieldItem && !this.shieldDisabled && this.getOffhandItem().isEmpty()) ||
                    (isAxeItem && (this.getMainHandItem().isEmpty() || (isTieredItem && isHandItemTiered &&
                            ((TieredItem) item.getItem().getItem()).getTier().getLevel() > ((TieredItem) this.getMainHandItem().getItem()).getTier().getLevel())))) {
               hasDesiredItems = true;
               break;
            }
         }

         if (!hasDesiredItems) {
            this.setCanPickUpLoot(false);
         } else {
            for (ItemEntity item : nearbyItems) {
               boolean isShieldItem = item.getItem().getItem() instanceof ShieldItem;
               boolean isAxeItem = item.getItem().getItem() instanceof AxeItem;
               boolean isTieredItem = item.getItem().getItem() instanceof TieredItem;
               boolean isHandItemTiered = this.getMainHandItem().getItem() instanceof TieredItem;

               if ((isShieldItem && !this.shieldDisabled && this.getOffhandItem().isEmpty()) ||
                       (isAxeItem && (this.getMainHandItem().isEmpty() || (isTieredItem && isHandItemTiered &&
                               ((TieredItem) item.getItem().getItem()).getTier().getLevel() > ((TieredItem) this.getMainHandItem().getItem()).getTier().getLevel())))) {
                  this.setCanPickUpLoot(true);
                  if (this.getTarget() == null) {
                     this.navigation.moveTo(item, 0.74F);
                     if (item != null) {
                        this.lookControl.setLookAt(item.position());
                     }
                  }
               }
            }
         }
      }


   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new MarauderEntity.BreakDoorGoal(this));
      this.goalSelector.addGoal(2, new RaidOpenDoorGoal(this));
      this.goalSelector.addGoal(3, new FindTargetGoal(this, 10.0F));
      this.goalSelector.addGoal(4, new MarauderEntity.AttackGoal(this));

      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, Mob.class, 8.0F));
   }

   protected void customServerAiStep() {
      if (!this.isNoAi() && GroundPathHelper.hasGroundPathNavigation(this)) {
         boolean flag = ((ServerWorld)this.level).isRaided(this.blockPosition());
         ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(flag);
      }

      super.customServerAiStep();
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.35F).add(Attributes.FOLLOW_RANGE, 20.0D).add(Attributes.MAX_HEALTH, 45.0D).add(Attributes.ATTACK_DAMAGE, 7.0D).add(Attributes.ARMOR, 12D).add(Attributes.ARMOR_TOUGHNESS, 6D).add(Attributes.KNOCKBACK_RESISTANCE);
   }

   @OnlyIn(Dist.CLIENT)
   public int getStunnedTick() {
      return this.stunnedTick;
   }

   public boolean canSee(Entity entity) {
      return this.stunnedTick <= 0 ? super.canSee(entity) : false;
   }

   public void aiStep() {
      super.aiStep();
      if (this.getTarget() != null) {
         float speed;

         if (this.getTarget() instanceof VillagerEntity) {
            speed = (this.getOffhandItem().getItem() instanceof ShieldItem) ? 0.45F : 0.5F;
         } else if (this.getTarget() instanceof IronGolemEntity) {
            speed = (this.getOffhandItem().getItem() instanceof ShieldItem) ? 0.4F : 0.45F;
         } else if (this.getTarget() instanceof PlayerEntity) {
            speed = (this.getOffhandItem().getItem() instanceof ShieldItem) ? 0.35F : 0.42F;
         } else {
            speed = 0.35F;
         }

         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);
      }

      if (!this.shieldDisabled) {
         this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5F);
      } else {
         this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0F);
      }




      if (this.stunnedTick > 0) {
         --this.stunnedTick;

         if (this.stunnedTick == 0 && shieldDisabled) {
            shieldDisabled = false;
         }
      }

      if (shieldDisabled) {
         this.stopUsingItem();
      }

      if (this.getItemBySlot(EquipmentSlotType.OFFHAND).getItem() instanceof ShieldItem && !shieldDisabled) {
         this.startUsingItem(Hand.OFF_HAND);
      }
   }

   public void addAdditionalSaveData(CompoundNBT nbt) {
         nbt.putBoolean("DroppedAxe", droppedAxe);
      nbt.putBoolean("ShieldDisabled", shieldDisabled);
      nbt.putInt("StunTick", this.stunnedTick);

      super.addAdditionalSaveData(nbt);
   }

   protected boolean isHoldingMeleeWeapon() {
      return this.getMainHandItem().getItem() instanceof TieredItem;
   }

   @Override
   protected void blockUsingShield(LivingEntity entity) {
      if (entity instanceof IronGolemEntity) {
         if (this.random.nextFloat() < 0.8F) {
            this.disableShield();
            return;
         }
      }
      entity.push(this);
      entity.hurtMarked = true;

      if (entity.getItemBySlot(EquipmentSlotType.MAINHAND).getItem() instanceof AxeItem) {
         ItemStack axe = entity.getItemBySlot(EquipmentSlotType.MAINHAND);
         ItemStack shield = this.getOffhandItem();
         float chance = getAxeDisableChance(axe.getItem());

         if (this.random.nextFloat() < chance) {
            this.disableShield();
         }
         Item item = entity.getMainHandItem().getItem();;
         float damage = 0f;
         if (item instanceof TieredItem) {
            TieredItem tieredItem = (TieredItem) item;
            damage = tieredItem.getTier().getAttackDamageBonus();
         }
         hurtCurrentlyUsedShield((float) damage + random.nextInt(8) + random.nextInt(8));
      } else {
         ItemStack shield = this.getOffhandItem();
         Item item = entity.getMainHandItem().getItem();;
         float damage = 0f;
         if (item instanceof TieredItem) {
            TieredItem tieredItem = (TieredItem) item;
            damage = tieredItem.getTier().getAttackDamageBonus();
         }


         hurtCurrentlyUsedShield((float) damage + random.nextInt(5));
      }

      if (shouldDropAxe(entity)) {
         dropItem(entity, this.getMainHandItem(), EquipmentSlotType.MAINHAND, SoundEvents.SHIELD_BLOCK, true);
      }
   }

   private float getAxeDisableChance(Item item) {
      if (item == Items.DIAMOND_AXE) {
         return 0.7F;
      } else if (item == Items.NETHERITE_AXE) {
         return 0.9F;
      } else if (item == Items.IRON_AXE) {
         return 0.6F;
      } else if (item == Items.STONE_AXE){
         return 0.2F;
      } else if (item == Items.WOODEN_AXE) {
         return 0.1f;
      } else {
         return 0.05f;
      }
   }

   private boolean shouldDropAxe(LivingEntity entity) {
      return !this.level.isClientSide /*&& !this.droppedAxe*/ &&
              this.random.nextFloat() < 0.1F &&
              this.getMainHandItem().getItem() instanceof AxeItem;
   }

   public void dropItem(@Nullable LivingEntity entity, ItemStack item, EquipmentSlotType type, @Nullable SoundEvent sound, boolean disable) {
      if (entity != null) {
         this.push(entity);
         entity.hurtMarked = true;
      }
      droppedAxe = disable;
      this.setItemSlot(type, Items.AIR);
      this.spawnAtLocation(item);
      if(sound != null)
      this.playSound(sound, 1.0F, 1.0F);
   }

   public void disableShield() {
      shieldDisabled = true;
      this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
      this.stunnedTick = 165 + this.random.nextInt(100) + 80;
      dropItem(null, this.getOffhandItem(), EquipmentSlotType.OFFHAND, null, false);
   }



   protected void hurtCurrentlyUsedShield(float damage) {
      if (this.useItem.getItem() == Items.SHIELD || this.useItem.getItem() == Items.NETHERITE_SHIELD) {
         if (damage >= 3.0F) {
            int i = 1 + MathHelper.floor(damage);
            Hand hand = Hand.OFF_HAND;
            this.useItem.hurtAndBreak(i, this, (marauderEntity) -> {
               marauderEntity.broadcastBreakEvent(hand);
            });
            if (this.useItem.isEmpty()) {
               this.setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);

               this.useItem = ItemStack.EMPTY;
               this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
            }
         }

      }
   }

   @Override
   protected void blockedByShield(LivingEntity entity) {
      entity.knockback(0.5F, entity.getX() - this.getX(), entity.getZ() - this.getZ());
   }

   @OnlyIn(Dist.CLIENT)
   public ArmPose getArmPose() {
      if (this.isAggressive() && this.isHoldingMeleeWeapon()) {
         return AbstractIllagerEntity.ArmPose.ATTACKING;
      } else if (this.isAggressive()){
         return AbstractIllagerEntity.ArmPose.ATTACKING;
      } else {
         return this.isCelebrating() ? AbstractIllagerEntity.ArmPose.CELEBRATING : ArmPose.ATTACKING;

      }
   }

   protected boolean isImmobile() {
      return super.isImmobile()/* || this.stunnedTick > 0*/;
   }

   public void readAdditionalSaveData(CompoundNBT nbt) {
      super.readAdditionalSaveData(nbt);
         this.droppedAxe = nbt.getBoolean("DroppedAxe");
         this.shieldDisabled = nbt.getBoolean("ShieldDisabled");
         this.stunnedTick = nbt.getInt("StunTick");

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.VINDICATOR_CELEBRATE;
   }

   public float getVoicePitch() {
      return super.getVoicePitch() - 0.2F;
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld iServerWorld, DifficultyInstance difficultyInstance, SpawnReason spawnReason, @Nullable ILivingEntityData iLivingEntityData, @Nullable CompoundNBT compoundNBT) {
      ILivingEntityData ilivingentitydata = super.finalizeSpawn(iServerWorld, difficultyInstance, spawnReason, iLivingEntityData, compoundNBT);
      ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(true);
      this.setLeftHanded(false);
      this.setCanPickUpLoot(true);
      this.stunnedTick = -1;
      this.populateDefaultEquipmentSlots(difficultyInstance);
      this.populateDefaultEquipmentEnchantments(difficultyInstance);
      this.setDropChance(EquipmentSlotType.MAINHAND, 0.045F);
      this.setDropChance(EquipmentSlotType.OFFHAND, 0.01F);

      return ilivingentitydata;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      if (this.getCurrentRaid() == null) {
         this.setItemSlot(EquipmentSlotType.MAINHAND, AXE_ITEM);
         this.setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.SHIELD));
      }

   }

   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof LivingEntity && ((LivingEntity)entity).getMobType() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && entity.getTeam() == null;
      } else {
         return false;
      }
   }


   protected SoundEvent getAmbientSound() {
      return SoundEvents.VINDICATOR_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VINDICATOR_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      if (source.isBypassInvul()) {
         return SoundEvents.VINDICATOR_HURT;
      } else if (source.getEntity() != null && !shieldDisabled && this.getUseItem().getItem() instanceof ShieldItem && facingTarget(source.getEntity())) {
         return SoundEvents.SHIELD_BLOCK;
      }
      return SoundEvents.VINDICATOR_HURT;
   }

   private boolean facingTarget(Entity entity) {

      float entityYaw = this.yBodyRot;
      Vector3d lookVec = Vector3d.directionFromRotation(0, entityYaw);

      Vector3d vecToTarget = new Vector3d(entity.getX() - this.getX(), 0, entity.getZ() - this.getZ()).normalize();

      double dotProduct = lookVec.dot(vecToTarget);

      double threshold = Math.cos(Math.toRadians(50));

      return dotProduct > threshold;
   }

   public boolean hurt(DamageSource source, float damage) {
      if (source.isBypassInvul() || source.isBypassArmor() || source.isBypassMagic() || source.isProjectile() || source.isFire() || source == DamageSource.FALL) {
         return super.hurt(source, damage);
      }

      if (this.getMainHandItem().getItem() instanceof AxeItem) {
         return super.hurt(source, damage);
      }

      return super.hurt(source, damage);
   }

   public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {
      ItemStack itemstack = new ItemStack(Items.IRON_AXE);
      Raid raid = this.getCurrentRaid();
      int i = 1;
      if (p_213660_1_ > raid.getNumGroups(Difficulty.NORMAL)) {
         i = 2;
      }

      boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
      if (flag) {
         Map<Enchantment, Integer> map = Maps.newHashMap();
         map.put(Enchantments.SHARPNESS, i);
         EnchantmentHelper.setEnchantments(map, itemstack);
      }

      this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
      this.setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.SHIELD));
   }

   class AttackGoal extends MeleeAttackGoal {
      MarauderEntity marauderEntity;
      public AttackGoal(MarauderEntity marauderEntity) {
         super(marauderEntity, 0.78D, false);
         this.marauderEntity = marauderEntity;
      }

      public boolean canUse() {
         return super.canUse() && marauderEntity.getTarget() != null /*&& marauderEntity.stunnedTick < 0*/;
      }

      protected double getAttackReachSqr(LivingEntity p_179512_1_) {
         if (this.mob.getVehicle() != null && this.mob.getVehicle() instanceof RavagerEntity) {
            float f = this.mob.getVehicle().getBbWidth() - 0.1F;
            return (double)(f * 2.0F * f * 2.0F + p_179512_1_.getBbWidth());
         } else {
            return super.getAttackReachSqr(p_179512_1_);
         }
      }
   }

   static class BreakDoorGoal extends net.minecraft.entity.ai.goal.BreakDoorGoal {
      public BreakDoorGoal(Mob p_i50578_1_) {
         super(p_i50578_1_, 6, MarauderEntity.DOOR_BREAKING_PREDICATE);
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canContinueToUse() {
         MarauderEntity vindicatorentity = (MarauderEntity)this.mob;
         return vindicatorentity.hasActiveRaid() && super.canContinueToUse();
      }

      public boolean canUse() {
         MarauderEntity vindicatorentity = (MarauderEntity)this.mob;
         return vindicatorentity.hasActiveRaid() && vindicatorentity.random.nextInt(10) == 0 && super.canUse();
      }

      public void start() {
         super.start();
         this.mob.setNoActionTime(0);
      }
   }

}