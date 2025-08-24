package net.minecraft.pokemon.item.pokeball.data;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PokeballData {
    public static List<PokeballType> knownTypes = new CopyOnWriteArrayList<>();

    @NotNull
    private final PokeballType type;
    @Nullable
    private final LivingEntity capturedPokemon;
    @Nullable
    private final UUID trainerID;

    public PokeballData(@NotNull PokeballType type, @Nullable LivingEntity capturedPokemon, @Nullable UUID trainerID) {
        this.type = type;
        this.capturedPokemon = capturedPokemon;
        this.trainerID = trainerID;
    }

    public static PokeballData registerType(PokeballType type) {
        knownTypes.add(type);
        return new PokeballData(type, null, null);
    }

    public double getCatchRate() {
        return this.type.getCatchRate();
    }


    public Optional<UUID> getTrainerID() {
        return Optional.ofNullable(trainerID);
    }

    public Optional<LivingEntity> getCapturedPokemon() {
        return Optional.ofNullable(capturedPokemon);
    }

    public boolean hasTrainer() {
        return this.getTrainerID().isPresent();
    }

    public boolean containsPokemon() {
        return this.getCapturedPokemon().isPresent();
    }

    public CompoundNBT save() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("Type", this.type.getName());
        if (this.hasTrainer()) {
            nbt.putUUID("TrainerID", this.getTrainerID().get());
        }
        if (this.containsPokemon()) {
            CompoundNBT nbt1 = new CompoundNBT();
            this.getCapturedPokemon().get().saveWithoutId(nbt1);
            nbt.put("CapturedPokemon", nbt1);
        }

        return nbt;
    }

    public boolean isOwnedBy(UUID otherPlayer) {
        return this.hasTrainer() && otherPlayer.equals(this.getTrainerID().get());
    }

    public static PokeballData load(CompoundNBT newNBT, World world) {
        PokeballType type = PokeballType.from(newNBT.getString("Type"));
        UUID trainerID = newNBT.contains("TrainerID") ? newNBT.getUUID("TrainerID") : null;
        CompoundNBT entityNBT = newNBT.contains("CapturedPokemon") ? newNBT.getCompound("CapturedPokemon") : null;
        if (entityNBT != null) {
            LivingEntity entity = (LivingEntity) EntityType.loadEntityRecursive(entityNBT, world, ent -> {
                ent.setUUID(UUID.randomUUID());
                return ent;
            });
            return new PokeballData(type, entity, trainerID);
        }
        return new PokeballData(type, null, trainerID);
    }
}
