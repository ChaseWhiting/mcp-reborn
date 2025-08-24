package net.minecraft.item;

import java.util.Arrays;
import java.util.function.Supplier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ArmorMaterial implements IArmorMaterial, IStringSerializable {
   LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
      return Ingredient.of(Items.LEATHER);
   }),
   CHAIN("chainmail", 15, new int[]{1, 3, 4, 2}, 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> {
      return Ingredient.of(Items.IRON_NUGGET);
   }),
   IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> {
      return Ingredient.of(Items.IRON_INGOT);
   }),
   GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
      return Ingredient.of(Items.GOLD_INGOT);
   }),
   ROSE_GOLD("rose_gold", 12, new int[]{1, 3, 5, 3}, 18, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
      return Ingredient.of(Items.ROSE_GOLD_INGOT);
   }),
   DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> {
      return Ingredient.of(Items.DIAMOND);
   }),
   TURTLE("turtle", 25, new int[]{2, 3, 5, 1}, 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.2F, 0.01F, () -> {
      return Ingredient.of(Items.SCUTE);
   }),
   BURNT_TURTLE("burnt_turtle", 22, new int[]{3, 6, 7, 2}, 15, SoundEvents.ARMOR_EQUIP_BURNT_TURTLE, 1.5F, 0.2F, () -> {
      return Ingredient.of(Items.TOASTED_SCUTE);
   }),
   FLOWER_CROWN("flower_crown", 10, new int[]{0, 3, 5, 1}, 9, SoundEvents.WAX_ON, 0F, 0F, () -> {
      return Ingredient.of(ItemTags.SMALL_FLOWERS);
   }),
   BEESWAX("beeswax", 17, new int[]{3, 4, 5, 2}, 14, SoundEvents.ARMOR_EQUIP_TURTLE, 0.5F, 0.005F, () -> {
      return Ingredient.of(Items.HONEYCOMB_BLOCK);
   }),
   NETHERITE("netherite", 37, new int[]{3, 6, 8, 3}, 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, () -> {
      return Ingredient.of(Items.NETHERITE_INGOT);
   }),
   WITHERED("withered", 25, new int[]{2, 4, 8, 2}, 8, SoundEvents.ARMOR_EQUIP_CHAIN, new float[]{0.3F, 0.8F, 1.3F, 0.2F}, new float[]{0.05F, 0.07F, 0.08F, 0.02F}, () -> Ingredient.of(Items.WITHER_BONE));

   public static final IStringSerializable.EnumCodec<ArmorMaterial> CODEC;
   private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
   private final String name;
   private final int durabilityMultiplier;
   private final int[] slotProtections;
   private final int enchantmentValue;
   private final SoundEvent sound;

   private final float[] toughness;
   private final float[] knockbackResistance;
   private final LazyValue<Ingredient> repairIngredient;

   private ArmorMaterial(String p_i231593_3_, int p_i231593_4_, int[] p_i231593_5_, int p_i231593_6_, SoundEvent p_i231593_7_, float p_i231593_8_, float p_i231593_9_, Supplier<Ingredient> p_i231593_10_) {
      this.name = p_i231593_3_;
      this.durabilityMultiplier = p_i231593_4_;
      this.slotProtections = p_i231593_5_;
      this.enchantmentValue = p_i231593_6_;
      this.sound = p_i231593_7_;

      this.knockbackResistance = new float[4];
      Arrays.fill(this.knockbackResistance, p_i231593_9_);
      this.toughness = new float[4];
      Arrays.fill(this.toughness, p_i231593_8_);

      this.repairIngredient = new LazyValue<>(p_i231593_10_);
   }

   private ArmorMaterial(String name, int durability, int[] slotProtection, int enchantmentVal, SoundEvent equipSound, float[] t, float[] kbr, Supplier<Ingredient> repair) {
      this.name = name;
      this.durabilityMultiplier = durability;
      this.slotProtections = slotProtection;
      this.enchantmentValue = enchantmentVal;
      this.sound = equipSound;

      this.knockbackResistance = kbr;
      this.toughness = t;

      this.repairIngredient = new LazyValue<>(repair);
   }

   public int getDurabilityForSlot(EquipmentSlotType p_200896_1_) {
      return HEALTH_PER_SLOT[p_200896_1_.getIndex()] * this.durabilityMultiplier;
   }

   public int getDefenseForSlot(EquipmentSlotType p_200902_1_) {
      return this.slotProtections[p_200902_1_.getIndex()];
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   public SoundEvent getEquipSound() {
      return this.sound;
   }

   public Ingredient getRepairIngredient() {
      return this.repairIngredient.get();
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return this.name;
   }

   public float getToughness() {
      return this.getToughnessForSlot(EquipmentSlotType.CHEST);
   }

   public float getKnockbackResistance() {
      return this.getKnockbackResistanceForSlot(EquipmentSlotType.CHEST);
   }

   @Override
   public float getToughnessForSlot(EquipmentSlotType type) {
      return this.toughness[type.getIndex()];
   }

   @Override
   public float getKnockbackResistanceForSlot(EquipmentSlotType type) {
      return this.knockbackResistance[type.getIndex()];
   }

   @Override
   public String getSerializedName() {
      return getName();
   }

   static {
      CODEC = IStringSerializable.fromEnum(ArmorMaterial::values);
   }
}