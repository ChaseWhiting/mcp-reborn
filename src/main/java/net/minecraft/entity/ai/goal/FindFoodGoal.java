package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Creature;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.crafting.Ingredient;

public class FindFoodGoal extends AbstractFindItemsGoal {
        private final float moveSpeed;
        private final Ingredient test;
        public FindFoodGoal(Creature mob, float moveSpeed, Ingredient ingredient) {
            super(mob, item -> item.getItem().isEdible() && ingredient.test(item.getItem()));
            this.moveSpeed = moveSpeed;
            this.test = ingredient;
        }

        @Override
        protected float getMoveSpeed() {
            return this.moveSpeed;
        }

        @Override
        public boolean canUse() {
            if (mob.getItemBySlot(EquipmentSlotType.MAINHAND).isEdible()) {
                return false;
            }
            return super.canUse();
        }
    }