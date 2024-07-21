package net.minecraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class ElytraItem extends Item implements IArmorVanishable {
   public ElytraItem(Item.Properties p_i48507_1_) {
      super(p_i48507_1_);
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public static boolean isFlyEnabled(ItemStack p_185069_0_) {
      return p_185069_0_.getDamageValue() < p_185069_0_.getMaxDamage() - 1;
   }

   public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return p_82789_2_.getItem() == Items.PHANTOM_MEMBRANE;
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      EquipmentSlotType equipmentslottype = Mob.getEquipmentSlotForItem(itemstack);
      ItemStack itemstack1 = p_77659_2_.getItemBySlot(equipmentslottype);
      if (itemstack1.isEmpty()) {
         p_77659_2_.setItemSlot(equipmentslottype, itemstack.copy());
         itemstack.setCount(0);
         return ActionResult.sidedSuccess(itemstack, p_77659_1_.isClientSide());
      } else {
         return ActionResult.fail(itemstack);
      }
   }

   public static double getHorizontalDistanceSqr(Vector3d position) {
      return position.x * position.x + position.z * position.z;
   }

   public static void glide(LivingEntity entity, double d0) {

      Vector3d vector3d = entity.getDeltaMovement(); // Get the current movement vector

      if (vector3d.y > -0.5D) {
         entity.fallDistance = 1.0F; // If the player is not falling too fast, set fall distance to 1.0
      }

      Vector3d vector3d1 = entity.getLookAngle(); // Get the direction the player is looking
      float f = entity.xRot * ((float)Math.PI / 180F); // Convert the player's rotation to radians
      double d1 = Math.sqrt(vector3d1.x * vector3d1.x + vector3d1.z * vector3d1.z); // Calculate horizontal component of look angle
      double d3 = Math.sqrt(getHorizontalDistanceSqr(vector3d)); // Calculate horizontal speed
      double d4 = vector3d1.length(); // Get the length of the look angle vector
      float f1 = MathHelper.cos(f); // Get the cosine of the rotation angle
      f1 = (float)((double)f1 * (double)f1 * Math.min(1.0D, d4 / 0.4D)); // Adjust the cosine value based on look angle

      // Modify the vertical speed based on the adjusted cosine value
      vector3d = entity.getDeltaMovement().add(0.0D, d0 * (-1.0D + (double)f1 * 0.75D), 0.0D);

      // Adjust movement if the player is moving downward and has horizontal speed
      if (vector3d.y < 0.0D && d1 > 0.0D) {
         double d5 = vector3d.y * -0.1D * (double)f1; // Calculate a downward force
         vector3d = vector3d.add(vector3d1.x * d5 / d1, d5, vector3d1.z * d5 / d1); // Apply the force to the movement vector
      }

      // Further adjustments if the player's rotation is negative and they have horizontal speed
      if (f < 0.0F && d1 > 0.0D) {
         double d9 = d3 * (double)(-MathHelper.sin(f)) * 0.04D; // Calculate a horizontal force
         vector3d = vector3d.add(-vector3d1.x * d9 / d1, d9 * 3.2D, -vector3d1.z * d9 / d1); // Apply the force to the movement vector
      }

      // Smooth out the movement by adjusting towards the look direction
      if (d1 > 0.0D) {
         vector3d = vector3d.add((vector3d1.x / d1 * d3 - vector3d.x) * 0.1D, 0.0D, (vector3d1.z / d1 * d3 - vector3d.z) * 0.1D);
      }

      // Apply some friction to the movement vector

      entity.setDeltaMovement(vector3d.multiply((double)0.99F, (double)0.98F, (double)0.99F));

      // Move the player according to the modified movement vector
      entity.move(MoverType.SELF, entity.getDeltaMovement());

      // Handle collision with walls
      if (entity.horizontalCollision && !entity.level.isClientSide) {
         double d10 = Math.sqrt(getHorizontalDistanceSqr(entity.getDeltaMovement())); // Get the new horizontal speed
         double d6 = d3 - d10; // Calculate the change in speed
         float f2 = (float)(d6 * 10.0D - 3.0D); // Calculate the damage amount
         if (f2 > 0.0F) {
            entity.playSound(getFallDamageSound((int)f2), 1.0F, 1.0F); // Play the fall damage sound
            entity.hurt(DamageSource.FLY_INTO_WALL, f2); // Apply the damage to the player
         }
      }

      // Check if the player is on the ground and not on the client side
      if (entity.isOnGround() && !entity.level.isClientSide) {
         entity.setAFlag(7, false); // Reset the flying flag
      }

   }

   protected static SoundEvent getFallDamageSound(int damage) {
      return damage > 4 ? SoundEvents.GENERIC_BIG_FALL : SoundEvents.GENERIC_SMALL_FALL;
   }
}