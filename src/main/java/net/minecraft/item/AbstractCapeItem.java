package net.minecraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class AbstractCapeItem extends Item implements IArmorVanishable {
   private final String capeName;
   private final ResourceLocation capeID;

   public AbstractCapeItem(Properties properties, String capeName, String capeID) {
      super(properties);
      this.capeName = capeName;
      this.capeID = makeResourceLocation(capeID);

      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      super.use(world, player, hand);
      ItemStack itemstack = player.getItemInHand(hand);
      EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(itemstack);
      ItemStack itemstack1 = player.getItemBySlot(equipmentslottype);

      if (itemstack1.isEmpty()) {
         player.setItemSlot(equipmentslottype, itemstack.copy());
         itemstack.setCount(0);
      } else {
         ItemStack temp = itemstack1.copy();
         player.setItemSlot(equipmentslottype, itemstack.copy());
         player.setItemInHand(hand, temp);
         player.playSound(SoundEvents.ARMOR_EQUIP_ELYTRA, 1.0F, 1.0F);
      }
      player.awardStat(Stats.ITEM_USED.get(this), 1);
      return ActionResult.sidedSuccess(itemstack, world.isClientSide());
   }

   private ResourceLocation makeResourceLocation(String capeID) {
      String path = "textures/entity/cape/" + capeID + ".png";
      return new ResourceLocation(path);
   }

   public String getCapeName() {
      return capeName;
   }

   public ResourceLocation getCapeID() {
      return capeID;
   }
}
