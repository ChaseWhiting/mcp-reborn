package net.minecraft.entity.monster;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class BasicCrossbowIllagerEntity extends AbstractIllagerEntity implements ICrossbowUser {
   protected static final DataParameter<Boolean> IS_CHARGING_CROSSBOW = EntityDataManager.defineId(BasicCrossbowIllagerEntity.class, DataSerializers.BOOLEAN);

   public BasicCrossbowIllagerEntity(EntityType<? extends BasicCrossbowIllagerEntity> p_i50198_1_, World p_i50198_2_) {
      super(p_i50198_1_, p_i50198_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0D, 8.0F));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(IS_CHARGING_CROSSBOW, false);
   }

   public boolean canFireProjectileWeapon(ShootableItem item) {
      return item instanceof ICrossbowItem;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isChargingCrossbow() {
      return this.entityData.get(IS_CHARGING_CROSSBOW);
   }

   public void setChargingCrossbow(boolean p_213671_1_) {
      this.entityData.set(IS_CHARGING_CROSSBOW, p_213671_1_);
   }

   public void onCrossbowAttackPerformed() {
      this.noActionTime = 0;
   }


   @OnlyIn(Dist.CLIENT)
   public ArmPose getArmPose() {
      if (this.isChargingCrossbow()) {
         return ArmPose.CROSSBOW_CHARGE;
      } else if (this.isHolding(Items.CROSSBOW) || this.isHolding(Items.GILDED_CROSSBOW) || AbstractCrossbowItem.isHoldingAbstractCrossbowItem(this)) {
         return ArmPose.CROSSBOW_HOLD;
      } else {
         return this.isAggressive() ? ArmPose.ATTACKING : ArmPose.NEUTRAL;
      }
   }



   public int getMaxSpawnClusterSize() {
      return 1;
   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.populateDefaultEquipmentSlots(p_213386_2_);
      this.populateDefaultEquipmentEnchantments(p_213386_2_);
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.CROSSBOW));
   }

   protected void enchantSpawnedWeapon(float p_241844_1_) {
      super.enchantSpawnedWeapon(p_241844_1_);
      if (this.random.nextInt(300) == 0) {
         ItemStack itemstack = this.getMainHandItem();
         if (itemstack.getItem() == Items.CROSSBOW) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);
            map.putIfAbsent(Enchantments.PIERCING, 1);
            EnchantmentHelper.setEnchantments(map, itemstack);
            this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
         }
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

   public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      this.performCrossbowAttack(this, 1.6F);
   }

   public void shootCrossbowProjectile(LivingEntity p_230284_1_, ItemStack p_230284_2_, ProjectileEntity p_230284_3_, float p_230284_4_) {
      this.shootCrossbowProjectile(this, p_230284_1_, p_230284_3_, p_230284_4_, 1.6F);
   }

}