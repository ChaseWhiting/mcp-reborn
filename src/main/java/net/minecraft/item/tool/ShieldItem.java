package net.minecraft.item.tool;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShieldItem extends Item implements Equipable {
   public ShieldItem(Item.Properties p_i48470_1_) {
      super(p_i48470_1_);
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   @Override
   public EquipmentSlotType getEquipmentSlot() {
      return EquipmentSlotType.OFFHAND;
   }

   public String getDescriptionId(ItemStack p_77667_1_) {
      return p_77667_1_.getTagElement("BlockEntityTag") != null ? this.getDescriptionId() + '.' + getColor(p_77667_1_).getName() : super.getDescriptionId(p_77667_1_);
   }

   public int getWeight(ItemStack bundle) {
      return 24;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      BannerItem.appendHoverTextFromBannerBlockEntityTag(p_77624_1_, p_77624_3_);
   }

   public UseAction getUseAnimation(ItemStack p_77661_1_) {
      return UseAction.BLOCK;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 72000;
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      player.startUsingItem(hand);
      return ActionResult.consume(itemstack);
   }

   public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return ItemTags.PLANKS.contains(p_82789_2_.getItem()) || super.isValidRepairItem(p_82789_1_, p_82789_2_);
   }

   public static DyeColor getColor(ItemStack p_195979_0_) {
      return DyeColor.byId(p_195979_0_.getOrCreateTagElement("BlockEntityTag").getInt("Base"));
   }
}