package net.minecraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.tool.ShieldItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class AbstractShieldItem extends ShieldItem {
   private final ShieldType shieldType;

   public AbstractShieldItem(Properties properties, ShieldType type) {
      super(properties);
      this.shieldType = type;
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public int getWeight(ItemStack bundle) {
      return switch (shieldType) {
          case DIAMOND -> 30;
          case NETHERITE -> 40;
          case CTHULHU -> 12;
      };
   }

   public String getShieldType() {
      return shieldType.getType();
   }

   @Override
   public String getDescriptionId(ItemStack itemStack) {
      return super.getDescriptionId(itemStack);
   }

   @Override
   public UseAction getUseAnimation(ItemStack itemStack) {
      return UseAction.BLOCK;
   }

   @Override
   public int getUseDuration(ItemStack itemStack) {
      return 72000;
   }

   @Override
   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      player.startUsingItem(hand);
      return ActionResult.consume(itemStack);
   }

   @Override
   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return false;
   }

   public enum ShieldType {
      DIAMOND("diamond", 450),
      NETHERITE("netherite", 503),
      CTHULHU("cthulhu", 2600);

      private final String name;
      private final int durability;

      ShieldType(String name, int durability) {
         this.name = name;
         this.durability = durability;
      }

      public String getType() {
         return name;
      }

      public int getDurability() {
         return durability;
      }
   }
}
