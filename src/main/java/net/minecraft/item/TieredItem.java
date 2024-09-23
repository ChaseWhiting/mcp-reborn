package net.minecraft.item;

import net.minecraft.bundle.BundleItem;

public class TieredItem extends Item {
   private final IItemTier tier;
   private final ItemTier tier1;

   public TieredItem(IItemTier p_i48459_1_, Properties p_i48459_2_) {
      super(p_i48459_2_.defaultDurability(p_i48459_1_.getUses()));
      this.tier = p_i48459_1_;
      this.tier1 = (ItemTier) tier;
   }

   public IItemTier getTier() {
      return this.tier;
   }

   public ItemTier tier() {
       return this.tier1;
   }

    public int getWeight(ItemStack bundle) {
        return switch (tier1) {
            case WOOD -> 1;        // Wood: 1-3 units
            case STONE -> 7;       // Stone: 6-8 units
            case IRON -> 12;       // Iron: 10-12 units
            case GOLD -> 16;       // Gold: 15-20 units (balanced lower than Diamond)
            case DIAMOND -> 10;    // Diamond: 8-10 units
            case NETHERITE -> 32;  // Netherite: 20-32 units
        };
    }

   public int getEnchantmentValue() {
      return this.tier.getEnchantmentValue();
   }

   public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return this.tier.getRepairIngredient().test(p_82789_2_) || super.isValidRepairItem(p_82789_1_, p_82789_2_);
   }
}