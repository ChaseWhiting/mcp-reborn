package net.minecraft.pokemon.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EntityType;
import net.minecraft.pokemon.move.Move;
import net.minecraft.pokemon.move.Moves;
import net.minecraft.world.World;

public class RattataEntity extends Pokemon{
    public RattataEntity(EntityType<? extends RattataEntity> pokemon, World world) {
        super(pokemon, world);
    }

    @Override
    public ImmutableSet<Move> getMoves() {
        return ImmutableSet.of(Moves.TACKLE);
    }

}
