package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class ACWeaponEnchantment extends Enchantment {

    private int levels;
    private int minXP;
    private String registryName;

    protected ACWeaponEnchantment(String name, Rarity rarity, EnchantmentType category, int levels, int minXP, EquipmentSlotType... equipmentSlot) {
        super(rarity, category, equipmentSlot);
        this.levels = levels;
        this.minXP = minXP;
        this.registryName = name;
    }

    public int getMinCost(int i) {
        return 1 + (i - 1) * minXP;
    }

    public int getMaxCost(int i) {
        return super.getMinCost(i) + 30;
    }

    public int getMaxLevel() {
        return levels;
    }


    protected boolean checkCompatibility(Enchantment enchantment) {
        return this != enchantment && Enchantments.areCompatible(this, enchantment);
    }

    public boolean isTradeable() {
        return true;
    }

    public boolean isDiscoverable() {
        return true;
    }

    public boolean isAllowedOnBooks() {
        return true;
    }

    public String getName(){
        return registryName;
    }
}