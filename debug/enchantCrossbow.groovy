import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.Enchantments
import java.util.HashMap
import java.util.Random

class CrossbowEnchanter {
    // Initialize RANDOM_CROSSBOW_ENCHANT map
    static def RANDOM_CROSSBOW_ENCHANT = new HashMap<Enchantment, Integer>()
    static Random random = new Random()

    static {
        RANDOM_CROSSBOW_ENCHANT.put(Enchantments.MULTISHOT, 1)
        RANDOM_CROSSBOW_ENCHANT.put(Enchantments.QUICK_CHARGE, random.nextInt(3) + 1) // Ensure level is at least 1
        RANDOM_CROSSBOW_ENCHANT.put(Enchantments.PIERCING, random.nextInt(3) + 1) // Ensure level is at least 1
        RANDOM_CROSSBOW_ENCHANT.put(Enchantments.UNBREAKING, random.nextInt(3) + 1) // Ensure level is at least 1
    }

    // Method to get a random crossbow enchantment
    static def getRandomCrossbowEnchant() {
        def enchantments = RANDOM_CROSSBOW_ENCHANT.keySet().toList()
        Enchantment randomEnchantment = enchantments.get(random.nextInt(enchantments.size()))
        int level = RANDOM_CROSSBOW_ENCHANT.get(randomEnchantment)
        return [randomEnchantment, level]
    }
}

// Execute the method to get a random crossbow enchantment
return CrossbowEnchanter.getRandomCrossbowEnchant()
