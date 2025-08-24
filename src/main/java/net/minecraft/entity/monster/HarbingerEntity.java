package net.minecraft.entity.monster;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.raid.Raid;

import javax.annotation.Nullable;
import java.util.Map;

public class HarbingerEntity extends BasicCrossbowIllagerEntity implements ICrossbowUser, WarmColdVariantHolder {
   private static final DataParameter<WarmColdVariant> VARIANT = EntityDataManager.defineId(HarbingerEntity.class, DataSerializers.WARM_COLD_VARIANT);

   private final Inventory inventory = new Inventory(5);

   public HarbingerEntity(EntityType<? extends HarbingerEntity> p_i50198_1_, World p_i50198_2_) {
      super(p_i50198_1_, p_i50198_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(2, new FindTargetGoal(this, 10.0F));
      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 15.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, Mob.class, 15.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.35F).add(Attributes.MAX_HEALTH, 24.0D).add(Attributes.ATTACK_DAMAGE, 5.0D).add(Attributes.FOLLOW_RANGE, 32.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      //this.entityData.define(IS_CHARGING_CROSSBOW, false);
      this.entityData.define(VARIANT, WarmColdVariant.TEMPERATE);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      ListNBT listnbt = new ListNBT();

      for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
         ItemStack itemstack = this.inventory.getItem(i);
         if (!itemstack.isEmpty()) {
            listnbt.add(itemstack.save(new CompoundNBT()));
         }
      }

      p_213281_1_.put("Inventory", listnbt);
      this.putVariantToTag(p_213281_1_);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      ListNBT listnbt = p_70037_1_.getList("Inventory", 10);
      this.setVariant(getVariantFromTag(p_70037_1_));
      for(int i = 0; i < listnbt.size(); ++i) {
         ItemStack itemstack = ItemStack.of(listnbt.getCompound(i));
         if (!itemstack.isEmpty()) {
            this.inventory.addItem(itemstack);
         }
      }

      this.setCanPickUpLoot(true);
   }

   public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      BlockState blockstate = p_205022_2_.getBlockState(p_205022_1_.below());
      return !blockstate.is(Blocks.GRASS_BLOCK) && !blockstate.is(Blocks.SAND) ? 0.5F - p_205022_2_.getBrightness(p_205022_1_) : 10.0F;
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.populateDefaultEquipmentSlots(p_213386_2_);
      this.populateDefaultEquipmentEnchantments(p_213386_2_);
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public boolean isAlliedTo(Entity p_184191_1_) {
      if (super.isAlliedTo(p_184191_1_)) {
         return true;
      } else if (p_184191_1_ instanceof LivingEntity && ((LivingEntity)p_184191_1_).getMobType() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && p_184191_1_.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PILLAGER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PILLAGER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.PILLAGER_HURT;
   }

   protected void pickUpItem(ItemEntity p_175445_1_) {
      ItemStack itemstack = p_175445_1_.getItem();
      if (itemstack.getItem() instanceof BannerItem) {
         super.pickUpItem(p_175445_1_);
      } else {
         Item item = itemstack.getItem();
         if (this.wantsItem(item)) {
            this.onItemPickup(p_175445_1_);
            ItemStack itemstack1 = this.inventory.addItem(itemstack);
            if (itemstack1.isEmpty()) {
               p_175445_1_.remove();
            } else {
               itemstack.setCount(itemstack1.getCount());
            }
         }
      }

   }

   private boolean wantsItem(Item p_213672_1_) {
      return this.hasActiveRaid() && p_213672_1_ == Items.WHITE_BANNER;
   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      if (super.setSlot(p_174820_1_, p_174820_2_)) {
         return true;
      } else {
         int i = p_174820_1_ - 300;
         if (i >= 0 && i < this.inventory.getContainerSize()) {
            this.inventory.setItem(i, p_174820_2_);
            return true;
         } else {
            return false;
         }
      }
   }

   public void tick() {
      super.tick();
   }



   public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {
      Raid raid = this.getCurrentRaid();
      boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
      if (flag) {
         ItemStack itemstack = new ItemStack(Items.CROSSBOW);
         Map<Enchantment, Integer> map = Maps.newHashMap();
         if (p_213660_1_ > raid.getNumGroups(Difficulty.NORMAL)) {
            map.put(Enchantments.QUICK_CHARGE, 2);
         } else if (p_213660_1_ > raid.getNumGroups(Difficulty.EASY)) {
            map.put(Enchantments.QUICK_CHARGE, 1);
         }

         map.put(Enchantments.MULTISHOT, 1);
         EnchantmentHelper.setEnchantments(map, itemstack);
         this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
      }

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.PILLAGER_CELEBRATE;
   }

   @Override
   public void setVariant(WarmColdVariant variant) {
      entityData.set(VARIANT, variant);
   }

   @Override
   public WarmColdVariant getVariant() {
      return entityData.get(VARIANT);
   }
}