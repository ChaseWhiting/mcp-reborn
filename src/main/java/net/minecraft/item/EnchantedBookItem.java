package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.bundle.BundleItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnchantedBookItem extends Item {
   public EnchantedBookItem(Properties p_i48505_1_) {
      super(p_i48505_1_);
   }

   public boolean isFoil(ItemStack p_77636_1_) {
      return true;
   }

   public int getWeight(ItemStack bundle, ItemStack enchantmentBook) {
      return 4 + returnWeightOfEnchantments(enchantmentBook);
   }

   public static int returnWeightOfEnchantments(ItemStack book) {
      ListNBT enchantments = getEnchantments(book);
      int totalWeight = 0;

      // Iterate through the list of enchantments
      for (int i = 0; i < enchantments.size(); i++) {
         CompoundNBT enchantmentData = enchantments.getCompound(i);

         // Get the enchantment's ID and level
         String enchantmentId = enchantmentData.getString("id");
         int enchantmentLevel = enchantmentData.getInt("lvl");

         // Look up the Enchantment object from the registry using its ID
         Enchantment enchantment = Registry.ENCHANTMENT.get(new ResourceLocation(enchantmentId));


         // Apply your weighting logic for this enchantment
         if (enchantment != null) {
            if (enchantment.isCurse()) {
               totalWeight += 5;
            }
            if (enchantment.isTreasureOnly()) {
               totalWeight += 5;
            }
            totalWeight += switch(enchantment.getRarity()) {
                case COMMON -> 0;
                case UNCOMMON -> 4;
                case RARE -> 6;
                case VERY_RARE -> 12;
            };
            // For example, you might want to weight it by level
             totalWeight += enchantmentLevel;


         }
      }
      boolean curse = (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.WEIGHT_CURSE, book) > 0);

      if (curse) totalWeight *= 2;

      return totalWeight;
   }

   public static int returnWeightOfEnchantments(ListNBT enchantments, ItemStack enchantmentbook) {
      int totalWeight = 0;

      // Iterate through the list of enchantments
      for (int i = 0; i < enchantments.size(); i++) {
         CompoundNBT enchantmentData = enchantments.getCompound(i);

         // Get the enchantment's ID and level
         String enchantmentId = enchantmentData.getString("id");
         int enchantmentLevel = enchantmentData.getInt("lvl");

         // Look up the Enchantment object from the registry using its ID
         Enchantment enchantment = Registry.ENCHANTMENT.get(new ResourceLocation(enchantmentId));


         // Apply your weighting logic for this enchantment
         if (enchantment != null) {
            if (enchantment.isCurse()) {
               totalWeight += 5;
            }
            if (enchantment.isTreasureOnly()) {
               totalWeight += 5;
            }
            totalWeight += switch(enchantment.getRarity()) {
               case COMMON -> 0;
               case UNCOMMON -> 4;
               case RARE -> 6;
               case VERY_RARE -> 12;
            };
            // For example, you might want to weight it by level
            totalWeight += enchantmentLevel;

         }
      }
      boolean curse = (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.WEIGHT_CURSE, enchantmentbook) > 0);

      if (curse) totalWeight *= 2;

      return totalWeight;
   }


   public boolean isEnchantable(ItemStack p_77616_1_) {
      return false;
   }

   public static ListNBT getEnchantments(ItemStack p_92110_0_) {
      CompoundNBT compoundnbt = p_92110_0_.getTag();
      return compoundnbt != null ? compoundnbt.getList("StoredEnchantments", 10) : new ListNBT();
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
      ItemStack.appendEnchantmentNames(p_77624_3_, getEnchantments(p_77624_1_));
   }

   public static void addEnchantment(ItemStack p_92115_0_, EnchantmentData p_92115_1_) {
      ListNBT listnbt = getEnchantments(p_92115_0_);
      boolean flag = true;
      ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(p_92115_1_.enchantment);

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         ResourceLocation resourcelocation1 = ResourceLocation.tryParse(compoundnbt.getString("id"));
         if (resourcelocation1 != null && resourcelocation1.equals(resourcelocation)) {
            if (compoundnbt.getInt("lvl") < p_92115_1_.level) {
               compoundnbt.putShort("lvl", (short)p_92115_1_.level);
            }

            flag = false;
            break;
         }
      }

      if (flag) {
         CompoundNBT compoundnbt1 = new CompoundNBT();
         compoundnbt1.putString("id", String.valueOf((Object)resourcelocation));
         compoundnbt1.putShort("lvl", (short)p_92115_1_.level);
         listnbt.add(compoundnbt1);
      }

      p_92115_0_.getOrCreateTag().put("StoredEnchantments", listnbt);
   }

   public static ItemStack createForEnchantment(EnchantmentData p_92111_0_) {
      ItemStack itemstack = new ItemStack(Items.ENCHANTED_BOOK);
      addEnchantment(itemstack, p_92111_0_);
      return itemstack;
   }

   public void fillItemCategory(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (p_150895_1_ == ItemGroup.TAB_SEARCH) {
         for(Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.category != null) {
               for(int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); ++i) {
                  p_150895_2_.add(createForEnchantment(new EnchantmentData(enchantment, i)));
               }
            }
         }
      } else if (p_150895_1_.getEnchantmentCategories().length != 0) {
         for(Enchantment enchantment1 : Registry.ENCHANTMENT) {
            if (p_150895_1_.hasEnchantmentCategory(enchantment1.category)) {
               p_150895_2_.add(createForEnchantment(new EnchantmentData(enchantment1, enchantment1.getMaxLevel())));
            }
         }
      }

   }
}