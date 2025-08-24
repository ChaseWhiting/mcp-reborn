package net.minecraft.pokemon.move;

import net.minecraft.entity.LivingEntity;
import net.minecraft.pokemon.entity.Pokemon;
import net.minecraft.world.World;

public abstract class Move {
    private final double basePower;
    private final boolean statusMove;
    private final float accuracy;
    private final boolean physical;

    public Move(double basePower, boolean statusMove, float accuracy, boolean physical) {
        this.basePower = basePower;
        this.statusMove = statusMove;
        this.accuracy = accuracy;
        this.physical = physical;
    }


    public abstract boolean useMove(Pokemon pokemon, World world, LivingEntity againstTarget);


    public double getBasePower() {
        return basePower;
    }
}

