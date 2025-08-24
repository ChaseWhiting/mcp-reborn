package net.minecraft.item;

import java.util.function.Supplier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyValue;

public enum ItemTier implements IItemTier {
   WOOD(0, 59, 2.0F, 0.0F, 15, () -> {
      return Ingredient.of(ItemTags.PLANKS);
   }),
   BAT(2, 240, 4.0F, 7.5F, 0, () -> {
      return Ingredient.of(ItemTags.PLANKS);
   }),
   STONE(1, 131, 4.0F, 1.0F, 5, () -> {
      return Ingredient.of(ItemTags.STONE_TOOL_MATERIALS);
   }),
   IRON(2, 250, 6.0F, 2.0F, 14, () -> {
      return Ingredient.of(Items.IRON_INGOT);
   }),
   DIAMOND(3, 1561, 8.0F, 3.0F, 10, () -> {
      return Ingredient.of(Items.DIAMOND);
   }),
   GOLD(0, 32, 12.0F, 0.0F, 22, () -> {
      return Ingredient.of(Items.GOLD_INGOT);
   }),
   ROSE_GOLD(1, 350, 10.5F, 1F, 18, () -> {
      return Ingredient.of(Items.ROSE_GOLD_INGOT);
   }),
   NETHERITE(4, 2031, 9.0F, 4.0F, 15, () -> {
      return Ingredient.of(Items.NETHERITE_INGOT);
   }),
   WITHER_BONE(3, 855, 6.0F, 3.5F, 13, () -> {
      return Ingredient.of(Items.WITHER_BONE);
   }),
   BEEKEEPER(5, 1836, 6.0F, 0, 13, () -> {
      return Ingredient.of(Items.BEE_POLLEN);
   }),
   STARFURY(4, 1455, 6.0F, 0, 8, () -> {
      return Ingredient.of(Items.NETHER_STAR);
   }),
   MEOWMERE(6, 2644, 6.0f, 0, 22, () -> {
      return Ingredient.EMPTY;
   });

   private final int level;
   private final int uses;
   private final float speed;
   private final float damage;
   private final int enchantmentValue;
   private final LazyValue<Ingredient> repairIngredient;

   private ItemTier(int toolLevel, int durability, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repair) {
      this.level = toolLevel;
      this.uses = durability;
      this.speed = speed;
      this.damage = damage;
      this.enchantmentValue = enchantmentValue;
      this.repairIngredient = new LazyValue<>(repair);
   }

   public int getUses() {
      return this.uses;
   }

   public float getSpeed() {
      return this.speed;
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   public int getLevel() {
      return this.level;
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   public Ingredient getRepairIngredient() {
      return this.repairIngredient.get();
   }
}