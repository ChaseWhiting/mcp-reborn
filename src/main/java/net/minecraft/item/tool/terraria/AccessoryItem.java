package net.minecraft.item.tool.terraria;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.world.World;

public abstract class AccessoryItem extends Item {

    boolean active; // Tracks if the accessory is currently active

    public AccessoryItem(Properties properties, Rarity rarity) {
        super(properties.weight(1).fireResistant().rarity(rarity).tab(ItemGroup.TAB_TOOLS));
    }

    public AccessoryItem(Properties properties) {
        super(properties.weight(1).fireResistant().tab(ItemGroup.TAB_TOOLS));
    }

    /**
     * Checks if the accessory is active (only active if in an AccessoryHolder slot).
     * This method should be implemented to define specific conditions for the accessory's activation.
     */
    public abstract boolean isAccessoryActive(ItemStack stack, PlayerEntity player);

    /**
     * Called each tick while the item is in the inventory of an entity.
     * Handles activation and deactivation of the accessory.
     */
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {

    }

    public void tick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = entity.asPlayer();
            boolean currentlyActive = isAccessoryActive(stack, player);

            if (currentlyActive && !active) {
                onAccessoryActivated(stack, player);
            } else if (!currentlyActive && active) {
                onAccessoryDeactivated(stack, player);
            }

            // Update the active status
            active = currentlyActive;

            if (currentlyActive) {
                applyAccessoryEffect(stack, world, player);
            }
        }
    }



    /**
     * Called when the accessory becomes active.
     * Subclasses should implement this to define behavior on activation.
     */
    protected abstract void onAccessoryActivated(ItemStack stack, PlayerEntity player);

    /**
     * Called when the accessory is deactivated.
     * Subclasses should implement this to define behavior on deactivation.
     */
    protected abstract void onAccessoryDeactivated(ItemStack stack, PlayerEntity player);

    /**
     * Called every tick when the accessory is active in the AccessoryHolder.
     * Subclasses should implement this to define the effect of the accessory while it is active.
     */
    protected abstract void applyAccessoryEffect(ItemStack stack, World world, PlayerEntity player);

    /**
     * Optional: Add other methods to apply Terraria-like behavior for effects.
     * Subclasses can override this to apply additional stat boosts (e.g., movement speed, health regen).
     */
    protected abstract void applyStatBoosts(PlayerEntity player);
}
