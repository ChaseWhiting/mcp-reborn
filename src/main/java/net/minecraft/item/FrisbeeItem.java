package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FrisbeeEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.util.Arrays;

public class FrisbeeItem extends Item {
   private final FrisbeeData data;
   private final int orignalData;
   private final int cooldown;

   public FrisbeeData getData(FrisbeeItem item) {
      return item.data;
   }


   public FrisbeeItem(Properties properties, FrisbeeData data) {
      super(data.isFireResistant() ? properties.fireResistant() : properties);
      if (data == null) {
         throw new IllegalArgumentException("FrisbeeData cannot be null");
      }
      this.data = data;
      this.cooldown = data.cooldown;
      orignalData = data.getDistanceToComeBack();
   }

   @Override
   public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
      RegistryKey<World> level = world.dimension();
      if (!Arrays.asList(data.getDimensions()).contains(level)) {
         return ActionResult.fail(playerEntity.getItemInHand(hand));
      }
      ItemStack itemstack = playerEntity.getItemInHand(hand);
      world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
              SoundEvents.ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F,
              0.4F / (random.nextFloat() * 0.4F + 0.8F));
      playerEntity.getCooldowns().addCooldown(this, cooldown);

      if (!world.isClientSide) {
         ItemStack stack = new ItemStack(this);
         getDistance(playerEntity, data);
         FrisbeeEntity frisbeeEntity = new FrisbeeEntity(EntityType.FRISBEE, playerEntity, world, data, itemstack);
         frisbeeEntity.setOwner(playerEntity);
         frisbeeEntity.shootFromRotation(playerEntity, playerEntity.xRot, playerEntity.yRot, 0.0F,
                 getVelocity(playerEntity), 1.0F);
         stack.setDamageValue(itemstack.getDamageValue());

         TransferEnchantments.transferEnchantments(itemstack, stack);
         frisbeeEntity.setItemStack(stack);
         frisbeeEntity.setItem(stack);
         frisbeeEntity.setNoGravity(true);
         data.triggerOnThrow(frisbeeEntity, playerEntity);
         world.addFreshEntity(frisbeeEntity);
      }

      playerEntity.awardStat(Stats.ITEM_USED.get(this));
      if (!playerEntity.abilities.instabuild) {
         itemstack.shrink(1);
      }

      return ActionResult.sidedSuccess(itemstack, world.isClientSide());
   }


   public float getVelocity(PlayerEntity player) {
      return !player.isShiftKeyDown() ? 1.5F + 0.01F * data.getSpeed() : 0.7F + 0.005F * data.getSpeed();
   }

   public FrisbeeData getData() {
      return this.data;
   }

   public void getDistance(PlayerEntity player, FrisbeeData data) {
      if (player.isShiftKeyDown()) {
         data.distanceToComeBack = data.getDistanceHalved();
      } else {
         data.distanceToComeBack = orignalData;
      }
   }
}
