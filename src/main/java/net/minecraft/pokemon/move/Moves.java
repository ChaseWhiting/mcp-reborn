package net.minecraft.pokemon.move;

import net.minecraft.entity.LivingEntity;
import net.minecraft.pokemon.entity.Pokemon;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class Moves {

    public static final Move TACKLE = new Move(40, false, 1F, true) {
        @Override
        public boolean useMove(Pokemon pokemon, World world, LivingEntity againstTarget) {
            double distance = pokemon.distanceTo(againstTarget);
            if (distance > 6 || !pokemon.canSee(againstTarget)) {
                return false;
            } else {
                Vector3d delta = new Vector3d(pokemon.getX() - againstTarget.getX(), pokemon.getY() - againstTarget.getY(), pokemon.getZ() - againstTarget.getZ());

                pokemon.setDeltaMovement(pokemon.getDeltaMovement().add(delta.reverse().normalize()));


                return againstTarget.hurt(DamageSource.mobAttack(pokemon), (float) this.getBasePower());
            }
        }
    };
}
