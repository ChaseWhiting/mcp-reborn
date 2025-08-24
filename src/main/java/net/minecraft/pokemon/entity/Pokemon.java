package net.minecraft.pokemon.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.Animal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pokemon.move.Move;
import net.minecraft.pokemon.move.Moves;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

public abstract class Pokemon extends Animal {
    public AnimationState idleAnimation = new AnimationState();
    protected Pokemon(EntityType<? extends Pokemon> pokemon, World world) {
        super(pokemon, world);
    }

    public boolean setShiny() {
        return this.random.nextInt(4096) == 0;
    }

    public static final DataParameter<Boolean> IS_SHINY = EntityDataManager.defineId(Pokemon.class, DataSerializers.BOOLEAN);

    public void addAdditionalSaveData(CompoundNBT nbt) {
        nbt.putBoolean("Shiny", this.isShiny());
        super.addAdditionalSaveData(nbt);
    }

    public void readAdditionalSaveData(CompoundNBT nbt) {
        this.entityData.set(IS_SHINY, nbt.getBoolean("Shiny"));
        super.readAdditionalSaveData(nbt);
    }

    public abstract ImmutableSet<Move> getMoves();

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_SHINY, false);
    }

    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData spawnData, @Nullable CompoundNBT data) {
        this.entityData.set(IS_SHINY, setShiny());
        return super.finalizeSpawn(world, difficulty, reason, spawnData, data);
    }

    public boolean isShiny() {
        return this.entityData.get(IS_SHINY);
    }

    public void tick() {
        super.tick();
        if (this.getTarget() == null) {
            this.setTarget(level.getNearestSurvivalPlayer(this, 20));
        } else {
            if (this.getTarget() != null) {
                Move tackle = Moves.TACKLE;

                if (tick(90)) {
                    tackle.useMove(this, level, this.getTarget());
                }
            }
        }


        if (this.level.isClientSide) {
            this.idleAnimation.animateWhen(this.getTarget() == null, this.tickCount);
        }
    }
}
