package net.minecraft.item;
import net.minecraft.nbt.ListNBT;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment;

import java.util.Map;

public class TransferEnchantments {



    public static void transferEnchantments(ItemStack sourceStack, ItemStack targetStack) {
        // Get the enchantments from the source item stack
        ListNBT enchantmentTags = sourceStack.getEnchantmentTags();

        // Deserialize the enchantments into a Map
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(enchantmentTags);

        // Apply each enchantment to the target item stack
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            targetStack.enchant(enchantment, level);
        }
    }

}
