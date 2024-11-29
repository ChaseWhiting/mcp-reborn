package net.minecraft.item.tool;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRideable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class OnAStickItem<T extends Entity & IRideable> extends Item {
   private final EntityType<T> canInteractWith;
   private final int consumeItemDamage;

   public OnAStickItem(Item.Properties p_i231594_1_, EntityType<T> p_i231594_2_, int p_i231594_3_) {
      super(p_i231594_1_);
      this.canInteractWith = p_i231594_2_;
      this.consumeItemDamage = p_i231594_3_;
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (world.isClientSide) {
         return ActionResult.pass(itemstack);
      } else {
         Entity entity = player.getVehicle();
         if (player.isPassenger() && entity instanceof IRideable && entity.getType() == this.canInteractWith) {
            IRideable irideable = (IRideable)entity;
            if (irideable.boost()) {
               itemstack.hurtAndBreak(this.consumeItemDamage, player, (p_234682_1_) -> {
                  p_234682_1_.broadcastBreakEvent(hand);
               });
               if (itemstack.isEmpty()) {
                  ItemStack itemstack1 = new ItemStack(Items.FISHING_ROD);
                  itemstack1.setTag(itemstack.getTag());
                  return ActionResult.success(itemstack1);
               }

               return ActionResult.success(itemstack);
            }
         }

         player.awardStat(Stats.ITEM_USED.get(this));
         return ActionResult.pass(itemstack);
      }
   }
}