package net.minecraft.item;

import com.google.common.collect.Lists;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.bundle.BundleItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class GildedCrossbowItem extends ShootableItem implements IVanishable, ICrossbowItem {
   private boolean startSoundPlayed = false;
   private boolean midLoadSoundPlayed = false;

   public GildedCrossbowItem(Properties properties) {
      super(properties);
   }

   public int getEnchantmentValue() {
      return 20;
   }

   public int getWeight(ItemStack bundle) {
      return 12;
   }

   public Predicate<ItemStack> getSupportedHeldProjectiles() {
      return FIRE_WORK_OR_BONE_ARROW;
   }

   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return ARROW_OR_BONE_ARROW;
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack crossbow = player.getItemInHand(hand);
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
      int i = this.getUseDuration(stack) - i1;
      float f = getPowerForTime(i, stack);
      if (f >= 1.0F && !isCharged(stack) && tryLoadProjectiles(livingEntity, stack)) {
         setCharged(stack, true);
         SoundCategory soundcategory = livingEntity instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
         world.playSound((PlayerEntity)null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundcategory, 1.0F, (1.0F / (random.nextFloat() * 0.5F + 1.0F)) - 0.1F);      }

   }

   private static boolean tryLoadProjectiles(LivingEntity shooter, ItemStack crossbow) {
      int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, crossbow);
      int j = switch (i) {
         case 2 -> 4;
         case 3 -> 5;
         case 4 -> 6;
         default -> (i == 0) ? 1 : Math.min(6 + (i - 4), 16);
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

   private static boolean tryAddProjectile(ItemStack crossbow, ItemStack arrow, int index) {
      if(arrow.isEmpty()) {
         return false;
      }
      if (!setProjectileAtIndex(crossbow, arrow, index)) {
         return false;
      }


      return true;
   }

   private static boolean loadProjectile(LivingEntity livingEntity, ItemStack crossbow, ItemStack arrow, boolean p_220023_3_, boolean p_220023_4_) {
      if (arrow.isEmpty()) {
         return false;
      } else {
         boolean flag = p_220023_4_ && arrow.getItem() instanceof ArrowItem;
         ItemStack itemstack;
         if (!flag && !p_220023_4_ && !p_220023_3_) {
            itemstack = arrow.split(1);
            if (arrow.isEmpty() && livingEntity instanceof PlayerEntity) {
               ((PlayerEntity)livingEntity).inventory.removeItem(arrow);
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

   public static boolean setProjectileAtIndex(ItemStack crossbow, ItemStack newProjectile, int index) {
      CompoundNBT nbt = crossbow.getOrCreateTag();
      ListNBT list;
      if (nbt.contains("ChargedProjectiles", 9)) {
         list = nbt.getList("ChargedProjectiles", 10);
      } else {
         list = new ListNBT();
      }

      int multishotLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, crossbow);

      int maxProjectiles = switch(multishotLevel) {
         case 1 -> 3;
         case 2 -> 4;
         case 3 -> 5;
         case 4 -> 6;
         default -> Math.min(6 + (multishotLevel - 4), 16);
      };

      // Ensure the index is within the valid range
      if (index < 0 || index >= maxProjectiles || index >= list.size()) {
         return false;
      }

      // Create the new projectile NBT
      CompoundNBT newProjectileNBT = new CompoundNBT();
      newProjectile.save(newProjectileNBT);

      // Replace the projectile at the specified index
      list.set(index, newProjectileNBT);

      // Save the updated list back to the crossbow's NBT
      nbt.put("ChargedProjectiles", list);
      crossbow.setTag(nbt);

      return true;
   }

   private String getChargedProjectile(ItemStack crossbow, int index) {
      CompoundNBT nbt = crossbow.getOrCreateTag();
      ListNBT list;
      if (nbt.contains("ChargedProjectiles", 9)) {
         list = nbt.getList("ChargedProjectiles", 10);
      } else {
         list = new ListNBT();
      }
      String string = list.get(index).getAsString();
      return string != null ? string : "null";
   }

   private String getProjectiles(ItemStack crossbow) {
      CompoundNBT nbt = crossbow.getOrCreateTag();
      ListNBT list;
      if (nbt.contains("ChargedProjectiles", 9)) {
         list = nbt.getList("ChargedProjectiles", 10);
      } else {
         list = new ListNBT();
      }
      CompoundNBT nbt1 = new CompoundNBT();
      StringBuilder string = new StringBuilder();
      for (int i = 0; i < list.size(); i++) {
         string.append(list.get(i));
         string.append("\n");
      }
      return string.toString();
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
            ((AbstractArrowEntity)projectileEntity).setBaseDamage(((AbstractArrowEntity) projectileEntity).getBaseDamage() + 1);
         }

         world.addFreshEntity(projectileEntity);
         world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, soundPitch);
      }
   }


   private static AbstractArrowEntity getArrow(World p_220024_0_, LivingEntity p_220024_1_, ItemStack p_220024_2_, ItemStack p_220024_3_) {
      ArrowItem arrowitem = (ArrowItem)(p_220024_3_.getItem() instanceof ArrowItem ? p_220024_3_.getItem() : Items.ARROW);
      AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(p_220024_0_, p_220024_3_, p_220024_1_);
      if (p_220024_1_ instanceof PlayerEntity) {
         abstractarrowentity.setCritArrow(false);
      }

      abstractarrowentity.setSoundEvent(SoundEvents.CROSSBOW_HIT);
      abstractarrowentity.setShotFromCrossbow(true);
      int x = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.RICOCHET, p_220024_2_);
      int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, p_220024_2_);
      int g = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.GRAVITY, p_220024_2_);
      if (i > 0) {
         abstractarrowentity.setPierceLevel((byte)i);
      }
      if(x > 0) {
         abstractarrowentity.setRicochetLevel((byte) (1 + 1 * x));
      }
      if(g > 0) {
         abstractarrowentity.setGravityLevel((byte) (1 + 1 * g));
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
               case 5:
                  inaccuracyOffset = 15.0F;
                  break;
               case 6:
                  inaccuracyOffset = -15.0F;
                  break;
               case 7:
                  inaccuracyOffset = 20.0F;
                  break;
               case 8:
                  inaccuracyOffset = -20.0F;
                  break;
               case 9:
                  inaccuracyOffset = 25.0F;
                  break;
               case 10:
                  inaccuracyOffset = -25.0F;
                  break;
               case 11:
                  inaccuracyOffset = 30.0F;
                  break;
               case 12:
                  inaccuracyOffset = -30.0F;
                  break;
               case 13:
                  inaccuracyOffset = 35.0F;
                  break;
               case 14:
                  inaccuracyOffset = -35.0F;
                  break;
               case 15:
                  inaccuracyOffset = 40.0F;
                  break;
               case 16:
                  inaccuracyOffset = -40.0F;
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
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag),
              getRandomShotPitch(!flag),
              getRandomShotPitch(flag)
      };
   }

   private static float getRandomShotPitch(boolean flag) {
      float base = flag ? 0.63F : 0.43F;
      return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + base - 0.1F;
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
            p_219972_1_.playSound((PlayerEntity)null, p_219972_2_.getX(), p_219972_2_.getY(), p_219972_2_.getZ(), soundevent, SoundCategory.PLAYERS, 0.5F, 0.95F); // Adjusted pitch to 0.8F
         }

         if (f >= 0.5F && soundevent1 != null && !this.midLoadSoundPlayed) {
            this.midLoadSoundPlayed = true;
            p_219972_1_.playSound((PlayerEntity)null, p_219972_2_.getX(), p_219972_2_.getY(), p_219972_2_.getZ(), soundevent1, SoundCategory.PLAYERS, 0.5F, 0.95F); // Adjusted pitch to 0.8F
         }
      }

   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return getChargeDuration(p_77626_1_) + 3;
   }

   public static int getChargeDuration(ItemStack crossbow) {
      int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, crossbow);
      return i == 0 ? 20 : i == 4 ? 3 : 17 - 4 * i;
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
      if (crossbow.getItem() == Items.GILDED_CROSSBOW && containsChargedProjectile(crossbow, Items.FIREWORK_ROCKET)) {
         return 2.0F; // Higher than 1.6F but not excessively so
      } else {
         return 3.5F; // Higher than 3.15F for regular projectiles, but you can adjust this as needed
      }
   }

   public int getDefaultProjectileRange() {
      return 16;
   }
}