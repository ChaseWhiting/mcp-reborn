package net.minecraft.item;

import com.google.common.collect.Lists;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class AbstractCrossbowItem extends ShootableItem implements IVanishable {
   private boolean startSoundPlayed = false;
   private boolean midLoadSoundPlayed = false;
   private final CrossbowConfig config;


   // Add this method to check if the attacker is holding an instance of AbstractCrossbowItem
   public static boolean isHoldingAbstractCrossbowItem(LivingEntity entity) {
      for (Hand hand : Hand.values()) {
         if (entity.getItemInHand(hand).getItem() instanceof AbstractCrossbowItem) {
            return true;
         }
      }
      return false;
   }

   // Add this method to get the hand holding an instance of AbstractCrossbowItem
   public static Hand getHandHoldingAbstractCrossbowItem(LivingEntity entity) {
      for (Hand hand : Hand.values()) {
         if (entity.getItemInHand(hand).getItem() instanceof AbstractCrossbowItem) {
            return hand;
         }
      }
      return null;
   }




   public AbstractCrossbowItem(Item.Properties properties, CrossbowConfig config) {
      super(properties);
      this.config = config;
     // setConfig(new ItemStack(this), config);
   }

   @Override
   public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      super.inventoryTick(stack, world, entity, itemSlot, isSelected);
      if (!stack.hasTag()) {
         setConfig(stack, this.config);
      } else {
         CrossbowConfig config = getConfig(stack);
         if (config == null || config.getChargeDuration().length == 0 || config.getShootingPower().length == 0) {
            setConfig(stack, this.config);
         }
      }
   }

   public void tryConfig(ItemStack stack) {
      setConfig(stack, this.config);
   }

   public static void setConfig(ItemStack stack, CrossbowConfig config) {
      CompoundNBT nbt = stack.getOrCreateTag();
      nbt.putIntArray("shootingPower", toIntArray(config.getShootingPower()));
      nbt.putIntArray("chargeDuration", config.getChargeDuration());
      nbt.putBoolean("isCrit", config.isCrit());
      nbt.putInt("extraDamage", config.getExtraDamage());
      nbt.putInt("range", config.getRange());

      ListNBT effectsNBT = new ListNBT();
      for (EffectInstance effect : config.getEffectInstances()) {
         CompoundNBT effectNBT = new CompoundNBT();
         effect.save(effectNBT);
         effectsNBT.add(effectNBT);
      }
      nbt.put("effectInstances", effectsNBT);
   }

   public static CrossbowConfig getConfig(ItemStack stack) {
      CompoundNBT nbt = stack.getTag();
      if (nbt == null) {
         return null;
      }

      float[] shootingPower = toFloatArray(nbt.getIntArray("shootingPower"));
      int[] chargeDuration = nbt.getIntArray("chargeDuration");
      boolean isCrit = nbt.getBoolean("isCrit");
      int extraDamage = nbt.getInt("extraDamage");
      int range = nbt.getInt("range");

      List<EffectInstance> effects = new ArrayList<>();
      ListNBT effectsNBT = nbt.getList("effectInstances", 10);
      for (INBT effectNBT : effectsNBT) {
         effects.add(EffectInstance.load((CompoundNBT) effectNBT));
      }

      return new CrossbowConfig(shootingPower, chargeDuration, isCrit, range, extraDamage, effects.toArray(new EffectInstance[0]));
   }

   private static int[] toIntArray(float[] floatArray) {
      int[] intArray = new int[floatArray.length];
      for (int i = 0; i < floatArray.length; i++) {
         intArray[i] = Float.floatToIntBits(floatArray[i]);
      }
      return intArray;
   }

   private static float[] toFloatArray(int[] intArray) {
      float[] floatArray = new float[intArray.length];
      for (int i = 0; i < intArray.length; i++) {
         floatArray[i] = Float.intBitsToFloat(intArray[i]);
      }
      return floatArray;
   }


   public int getEnchantmentValue() {
      return 20;
   }

   public Predicate<ItemStack> getSupportedHeldProjectiles() {
      return FIRE_WORK_OR_BONE_ARROW;
   }

   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return ARROW_OR_BONE_ARROW;
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack crossbow = player.getItemInHand(hand);
      tryConfig(crossbow);
      if (isCharged(crossbow)) {
         performShooting(world, player, hand, crossbow, getShootingPower(crossbow), 1.0F);
         setCharged(crossbow, false);
         return ActionResult.consume(crossbow);
      } else if (!player.getProjectile(crossbow).isEmpty()) {
         if (!isCharged(crossbow)) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
            player.startUsingItem(hand);
         }

         return ActionResult.consume(crossbow);
      } else {
         return ActionResult.fail(crossbow);
      }
   }

   public void releaseUsing(ItemStack stack, World world, LivingEntity livingEntity, int i1) {
      tryConfig(stack);
      int i = this.getUseDuration(stack) - i1;
      float f = getPowerForTime(i, stack);
      if (f >= 1.0F && !isCharged(stack) && tryLoadProjectiles(livingEntity, stack)) {
         setCharged(stack, true);
         SoundCategory soundcategory = livingEntity instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
         world.playSound((PlayerEntity)null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundcategory, 1.0F, (1.0F / (random.nextFloat() * 0.5F + 1.0F)));
      }

   }

   private static boolean tryLoadProjectiles(LivingEntity shooter, ItemStack crossbow) {
      if(crossbow.getItem() instanceof AbstractCrossbowItem) {
         ((AbstractCrossbowItem)crossbow.getItem()).tryConfig(crossbow);
      }
      int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, crossbow);
      int j = switch (i) {
          case 2 -> 4;
          case 3 -> 5;
          default -> (i == 0) ? 1 : 3;
      };

       boolean flag = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).abilities.instabuild;
      ItemStack itemstack = shooter.getProjectile(crossbow);
      ItemStack itemstack1 = itemstack.copy();


      for (int k = 0; k < j; ++k) {
         if (k > 0) {
            itemstack = itemstack1.copy();
         }

         if (itemstack.isEmpty() && flag) {
            itemstack = new ItemStack(Items.ARROW);
            itemstack1 = itemstack.copy();
         }

         if (!loadProjectile(shooter, crossbow, itemstack, k > 0, flag)) {
            return false;
         }
      }

      return true;
   }

   public CrossbowConfig getConfig() {
      return config;
   }


   private static boolean loadProjectile(LivingEntity livingEntity, ItemStack crossbow, ItemStack arrow, boolean p_220023_3_, boolean p_220023_4_) {
      CrossbowConfig config = AbstractCrossbowItem.getConfig(crossbow);
      if(crossbow.getItem() instanceof AbstractCrossbowItem) {
         ((AbstractCrossbowItem)crossbow.getItem()).tryConfig(crossbow);
      }
      if (config == null || arrow.isEmpty()) {
         return false;
      } else {
         boolean flag = p_220023_4_ && arrow.getItem() instanceof ArrowItem;
         ItemStack itemstack;

         if (config.getEffectInstances().length > 0 && arrow.getItem() == Items.ARROW) {
            ArrowItem arrowItem = (ArrowItem) Items.TIPPED_ARROW;
            itemstack = new ItemStack(arrowItem);

            PotionUtils.setCustomEffects(itemstack, Arrays.asList(config.getEffectInstances()));

            IFormattableTextComponent displayName = new TranslationTextComponent("item.minecraft.tipped_arrow").append(" (");

            List<EffectInstance> effects = PotionUtils.getCustomEffects(itemstack);
            for (int i = 0; i < effects.size(); i++) {
               EffectInstance effect = effects.get(i);
               if (i > 0) {
                  displayName.append(", ");
               }
               displayName
                       .append(new TranslationTextComponent(effect.getDescriptionId()))
                       .append(" ")
                       .append(String.valueOf(effect.getAmplifier() + 1)); // Amplifier is zero-based, so add 1
            }

            displayName.append(")");
            itemstack.setHoverName(displayName.setStyle(Style.EMPTY.withItalic(false)));

            if (livingEntity instanceof PlayerEntity) {
               ((PlayerEntity) livingEntity).inventory.removeItem(arrow.split(1));
            }
         } else if (!flag && !p_220023_4_ && !p_220023_3_) {
            itemstack = arrow.split(1);
            if (arrow.isEmpty() && livingEntity instanceof PlayerEntity) {
               ((PlayerEntity) livingEntity).inventory.removeItem(arrow);
            }
         } else {
            itemstack = arrow.copy();
         }

         addChargedProjectile(crossbow, itemstack);
         return true;
      }
   }



   public static boolean isCharged(ItemStack p_220012_0_) {
      CompoundNBT compoundnbt = p_220012_0_.getTag();
      return compoundnbt != null && compoundnbt.getBoolean("Charged");
   }

   public static void setCharged(ItemStack p_220011_0_, boolean p_220011_1_) {
      CompoundNBT compoundnbt = p_220011_0_.getOrCreateTag();
      compoundnbt.putBoolean("Charged", p_220011_1_);
   }

   private static void addChargedProjectile(ItemStack crossbow, ItemStack arrow) {
      CompoundNBT compoundnbt = crossbow.getOrCreateTag();
      ListNBT listnbt;
      if (compoundnbt.contains("ChargedProjectiles", 9)) {
         listnbt = compoundnbt.getList("ChargedProjectiles", 10);
      } else {
         listnbt = new ListNBT();
      }

      CompoundNBT compoundnbt1 = new CompoundNBT();
      arrow.save(compoundnbt1);
      listnbt.add(compoundnbt1);
      compoundnbt.put("ChargedProjectiles", listnbt);
   }



   private static List<ItemStack> getChargedProjectiles(ItemStack p_220018_0_) {
      List<ItemStack> list = Lists.newArrayList();
      CompoundNBT compoundnbt = p_220018_0_.getTag();
      if (compoundnbt != null && compoundnbt.contains("ChargedProjectiles", 9)) {
         ListNBT listnbt = compoundnbt.getList("ChargedProjectiles", 10);
         if (listnbt != null) {
            for(int i = 0; i < listnbt.size(); ++i) {
               CompoundNBT compoundnbt1 = listnbt.getCompound(i);
               list.add(ItemStack.of(compoundnbt1));
            }
         }
      }

      return list;
   }

   private static void clearChargedProjectiles(ItemStack p_220027_0_) {
      CompoundNBT compoundnbt = p_220027_0_.getTag();
      if (compoundnbt != null) {
         ListNBT listnbt = compoundnbt.getList("ChargedProjectiles", 9);
         listnbt.clear();
         compoundnbt.put("ChargedProjectiles", listnbt);
      }

   }

   public static boolean containsChargedProjectile(ItemStack p_220019_0_, Item p_220019_1_) {
      return getChargedProjectiles(p_220019_0_).stream().anyMatch((p_220010_1_) -> {
         return p_220010_1_.getItem() == p_220019_1_;
      });
   }

   private static void shootProjectile(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectileItem, float soundPitch, boolean isCreative, float velocity, float inaccuracy, float rollAngle) {
      CrossbowConfig config = AbstractCrossbowItem.getConfig(crossbow);
      if (!world.isClientSide) {
         boolean isFirework = projectileItem.getItem() == Items.FIREWORK_ROCKET;
         ProjectileEntity projectileEntity;

         if (isFirework) {
            projectileEntity = new FireworkRocketEntity(world, projectileItem, shooter, shooter.getX(), shooter.getEyeY() - 0.15F, shooter.getZ(), true);
         } else {
            projectileEntity = getArrow(world, shooter, crossbow, projectileItem);
            if (isCreative || rollAngle != 0.0F) {
               ((AbstractArrowEntity) projectileEntity).pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
            }
         }

         if (shooter instanceof ICrossbowUser) {
            ICrossbowUser crossbowUser = (ICrossbowUser) shooter;
            crossbowUser.shootCrossbowProjectile(crossbowUser.getTarget(), crossbow, projectileEntity, rollAngle);
         } else {
            Vector3d upVector = shooter.getUpVector(1.0F);
            Quaternion quaternion = new Quaternion(new Vector3f(upVector), rollAngle, true);
            Vector3d viewVector = shooter.getViewVector(1.0F);
            Vector3f direction = new Vector3f(viewVector);
            direction.transform(quaternion);
            projectileEntity.shoot(direction.x(), direction.y(), direction.z(), velocity, inaccuracy);
         }

         crossbow.hurtAndBreak(isFirework ? 5 : 1, shooter, (entity) -> {
            entity.broadcastBreakEvent(hand);
         });
         if(projectileEntity instanceof AbstractArrowEntity) {
            ((AbstractArrowEntity)projectileEntity).setBaseDamage(((AbstractArrowEntity) projectileEntity).getBaseDamage() + config.getExtraDamage());
         }

         world.addFreshEntity(projectileEntity);
         world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, soundPitch);
      }
   }


   private static AbstractArrowEntity getArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrowItem) {
      CrossbowConfig config = AbstractCrossbowItem.getConfig(crossbow);
      ArrowItem arrowitem;
      ItemStack arrow;
      Collection<EffectInstance> effectInstancesCollection = Arrays.asList(config.getEffectInstances());
      AbstractArrowEntity abstractarrowentity;

      if (config.getEffectInstances().length > 0 && arrowItem.getItem() == Items.ARROW) {
         arrowitem = (ArrowItem) Items.TIPPED_ARROW;
         arrow = new ItemStack(arrowitem);
         PotionUtils.setCustomEffects(arrow, effectInstancesCollection);
         abstractarrowentity = arrowitem.createArrow(world, arrow, entity);
         abstractarrowentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
      } else {
         arrowitem = (ArrowItem)(arrowItem.getItem() instanceof ArrowItem ? arrowItem.getItem() : Items.ARROW);
         abstractarrowentity = arrowitem.createArrow(world, arrowItem, entity);
      }

      if (entity instanceof PlayerEntity) {
         abstractarrowentity.setCritArrow(config.isCrit());
      }

      abstractarrowentity.setSoundEvent(SoundEvents.CROSSBOW_HIT);
      abstractarrowentity.setShotFromCrossbow(true);
      int x = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.RICOCHET, crossbow);
      int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, crossbow);
      if (i > 0) {
         abstractarrowentity.setPierceLevel((byte)i);
      }
      if(x > 0) {
         abstractarrowentity.setRicochetLevel((byte) (1 + 1 * x));
      }

      return abstractarrowentity;
   }

   public static void performShooting(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, float velocity, float inaccuracy) {
      List<ItemStack> projectiles = getChargedProjectiles(crossbow);
      float[] shotPitches = getShotPitches(shooter.getRandom());

      for (int i = 0; i < projectiles.size(); ++i) {
         ItemStack projectile = projectiles.get(i);
         boolean isCreativeMode = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).abilities.instabuild;
         if (!projectile.isEmpty()) {
            float inaccuracyOffset = 0.0F;
            switch (i) {
               case 1:
                  inaccuracyOffset = -10.0F;
                  break;
               case 2:
                  inaccuracyOffset = 10.0F;
                  break;
               case 3:
                  inaccuracyOffset = 5.0F;
                  break;
               case 4:
                  inaccuracyOffset = -5.0F;
                  break;
               default:
                  break;
            }
            shootProjectile(world, shooter, hand, crossbow, projectile, shotPitches[i], isCreativeMode, velocity, inaccuracy, inaccuracyOffset);
         }
      }

      onCrossbowShot(world, shooter, crossbow);
   }

   private static float[] getShotPitches(Random random) {
      // Ensure this array has enough elements for all possible projectiles
      boolean flag = random.nextBoolean();
      return new float[]{
              1.0F,
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag)
      };
   }

   private static float getRandomShotPitch(boolean flag) {
      float base = flag ? 0.63F : 0.43F;
      return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + base;
   }

   private static void onCrossbowShot(World p_220015_0_, LivingEntity p_220015_1_, ItemStack p_220015_2_) {
      if (p_220015_1_ instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_220015_1_;
         if (!p_220015_0_.isClientSide) {
            CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayerentity, p_220015_2_);
         }

         serverplayerentity.awardStat(Stats.ITEM_USED.get(p_220015_2_.getItem()));
      }

      clearChargedProjectiles(p_220015_2_);
   }

   public void onUseTick(World p_219972_1_, LivingEntity p_219972_2_, ItemStack p_219972_3_, int p_219972_4_) {
      if (!p_219972_1_.isClientSide) {
         int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, p_219972_3_);
         SoundEvent soundevent = this.getStartSound(i);
         SoundEvent soundevent1 = i == 0 ? SoundEvents.CROSSBOW_LOADING_MIDDLE : null;
         float f = (float)(p_219972_3_.getUseDuration() - p_219972_4_) / (float)getChargeDuration(p_219972_3_);
         if (f < 0.2F) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
         }

         if (f >= 0.2F && !this.startSoundPlayed) {
            this.startSoundPlayed = true;
            p_219972_1_.playSound((PlayerEntity)null, p_219972_2_.getX(), p_219972_2_.getY(), p_219972_2_.getZ(), soundevent, SoundCategory.PLAYERS, 0.5F, 1F); // Adjusted pitch to 0.8F
         }

         if (f >= 0.5F && soundevent1 != null && !this.midLoadSoundPlayed) {
            this.midLoadSoundPlayed = true;
            p_219972_1_.playSound((PlayerEntity)null, p_219972_2_.getX(), p_219972_2_.getY(), p_219972_2_.getZ(), soundevent1, SoundCategory.PLAYERS, 0.5F, 1F); // Adjusted pitch to 0.8F
         }
      }

   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return getChargeDuration(p_77626_1_) + 3;
   }

   public static int getChargeDuration(ItemStack crossbow) {
      if(crossbow.getItem() instanceof AbstractCrossbowItem) {
         ((AbstractCrossbowItem)crossbow.getItem()).tryConfig(crossbow);
      }
      CrossbowConfig config = AbstractCrossbowItem.getConfig(crossbow);
      int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, crossbow);
      return i == 0 ? config.getChargeDuration()[0] : config.getChargeDuration()[0] - config.getChargeDuration()[1] * i;
   }

   public UseAction getUseAnimation(ItemStack crossbow) {
      return UseAction.CROSSBOW;
   }

   private SoundEvent getStartSound(int p_220025_1_) {
      switch(p_220025_1_) {
      case 1:
         return SoundEvents.CROSSBOW_QUICK_CHARGE_1;
      case 2:
         return SoundEvents.CROSSBOW_QUICK_CHARGE_2;
      case 3:
         return SoundEvents.CROSSBOW_QUICK_CHARGE_3;
      default:
         return SoundEvents.CROSSBOW_LOADING_START;
      }
   }

   private static float getPowerForTime(int p_220031_0_, ItemStack p_220031_1_) {
      float f = (float)p_220031_0_ / (float)getChargeDuration(p_220031_1_);
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack crossbow, @Nullable World world, List<ITextComponent> text, ITooltipFlag iTooltipFlag) {
      List<ItemStack> list = getChargedProjectiles(crossbow);
      if (isCharged(crossbow) && !list.isEmpty()) {
         ItemStack itemstack = list.get(0);
         text.add((new TranslationTextComponent("item.minecraft.crossbow.projectile")).append(" ").append(itemstack.getDisplayName()));
         if (iTooltipFlag.isAdvanced() && itemstack.getItem() == Items.FIREWORK_ROCKET) {
            List<ITextComponent> list1 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendHoverText(itemstack, world, list1, iTooltipFlag);
            if (!list1.isEmpty()) {
               for(int i = 0; i < list1.size(); ++i) {
                  list1.set(i, (new StringTextComponent("  ")).append(list1.get(i)).withStyle(TextFormatting.GRAY));
               }

               text.addAll(list1);
            }
         }

      }
   }

   private static float getShootingPower(ItemStack crossbow) {
      if(crossbow.getItem() instanceof AbstractCrossbowItem) {
         ((AbstractCrossbowItem)crossbow.getItem()).tryConfig(crossbow);
      }
      CrossbowConfig config = AbstractCrossbowItem.getConfig(crossbow);
      if (crossbow.getItem() instanceof AbstractCrossbowItem && containsChargedProjectile(crossbow, Items.FIREWORK_ROCKET)) {
         return config.getShootingPower()[0];
      } else {
         return config.getShootingPower()[1];
      }
   }

   public int getDefaultProjectileRange() {
      return this.getConfig().getRange();
   }
}