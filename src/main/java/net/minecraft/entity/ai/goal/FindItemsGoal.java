package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Creature;
import net.minecraft.item.crafting.Ingredient;

public class FindItemsGoal extends AbstractFindItemsGoal {
    private final float movespeed;
        public FindItemsGoal(Creature mob, float movespeed, Ingredient filter) {
            super(mob, item -> filter.test(item.getItem()));
            this.movespeed = movespeed;
        }

        @Override
        protected float getMoveSpeed() {
            return movespeed;
        }
    }