package net.minecraft.entity.projectile;

import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.pokemon.item.pokeball.AbstractPokeballItem;
import net.minecraft.pokemon.item.pokeball.data.PokeballData;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

public class PokeballEntity extends ThrowableEntity {

    private static final DataParameter<Integer> SHAKE_AMOUNT = EntityDataManager.defineId(PokeballEntity.class, DataSerializers.INT);

    private PokeballData data;
    private static final Random RANDOM = new Random();

    private double captureRate = 0.0; // Store calculated capture rate
    private int tickDelay = 0;       // Track delay for animations

    public AnimationState shakeOnce = new AnimationState();
    public AnimationState shakeTwice = new AnimationState();
    public AnimationState shakeThreeTimes = new AnimationState();
    public AnimationState shakeThreeTimesAndCatch = new AnimationState();

    public CompoundNBT currentCaptureEntity = null;

    public PokeballEntity(EntityType<PokeballEntity> pokeball, World world) {
        super(pokeball, world);
    }

    public PokeballEntity(EntityType<PokeballEntity> pokeball, World world, PokeballData data) {
        super(pokeball, world);
        this.data = data;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SHAKE_AMOUNT, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level.isClientSide) {
            // Client-side animation handling
            int shakes = this.entityData.get(SHAKE_AMOUNT);

            if (shakes == 1) {
                this.shakeOnce.animateWhen(tickDelay == 0, this.tickCount);
            } else if (shakes == 2) {
                this.shakeTwice.animateWhen(tickDelay == 25, this.tickCount);
            } else if (shakes == 3) {
                this.shakeThreeTimes.animateWhen(tickDelay == 50, this.tickCount);
            } else if (shakes == 4) {
                this.shakeThreeTimesAndCatch.animateWhen(tickDelay == 70, this.tickCount);
            }

            this.tickDelay++;
        } else {
            // Server-side capture and breakout handling
            int shakes = this.entityData.get(SHAKE_AMOUNT);
            double breakoutTime = 0;

            if (shakes == 1) breakoutTime = 1.67 * 20;
            else if (shakes == 2) breakoutTime = 2.42 * 20;
            else if (shakes == 3) breakoutTime = 3.67 * 20;
            else if (shakes == 4) breakoutTime = 3.17 * 20;

            // Ensure this logic only runs if an entity is captured
            if (this.currentCaptureEntity != null && this.tickCount >= breakoutTime) {
                if (shakes == 4) {
                    handleCapture();
                } else {
                    onPkmnBrokeOut();
                }
                this.discard();
            }
        }
    }


    /**
     * Handles Pokémon capture and adds it to the Poké Ball.
     */
    private void handleCapture() {
        if (this.currentCaptureEntity != null && this.getOwner() instanceof PlayerEntity) {
            PlayerEntity owner = (PlayerEntity) this.getOwner();
            ItemStack heldItem = owner.getMainHandItem();

            if (heldItem.getItem() instanceof AbstractPokeballItem) {
                AbstractPokeballItem.addPokemonToBall(heldItem, (LivingEntity) EntityType.loadEntityRecursive(this.currentCaptureEntity, level, entity -> {
                    entity.setUUID(UUID.randomUUID());
                    return entity;
                }), owner.getUUID());
            }
        }
    }


    @Override
    public IPacket<?> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new SSpawnObjectPacket(this, entity == null ? 0 : entity.getId());
    }

    /**
     * Determines capture logic and sets shake amount.
     */
    public boolean canCapture(LivingEntity attemptedCapture) {
        if (attemptedCapture == null || data == null) {
            return false;
        }

        // Default values for base catch rate, ball modifier, and status modifier
        double baseCatchRate = 45;
        double ballModifier = data.getCatchRate();
        double statusModifier = 1.0; // Placeholder for status modifier logic

        double currentHP = attemptedCapture.getHealth();
        double maxHP = attemptedCapture.getMaxHealth();

        // Step 1: Calculate capture rate
        this.captureRate = ((3 * maxHP - 2 * currentHP) * baseCatchRate * ballModifier * statusModifier) / (3 * maxHP);

        // Step 2: Perform shake checks
        if (this.captureRate >= 255) {
            this.entityData.set(SHAKE_AMOUNT, 4);
            return true; // Instant capture
        }

        double shakeThreshold = 65536 * Math.pow(this.captureRate / 255.0, 0.25);
        int successfulShakes = 0;

        for (int shake = 0; shake < 4; shake++) {
            int randomValue = RANDOM.nextInt(65536);
            if (randomValue <= shakeThreshold) {
                successfulShakes++;
            } else {
                break;
            }
        }

        this.entityData.set(SHAKE_AMOUNT, successfulShakes);
        return successfulShakes == 4; // True if all shakes pass
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (entity != this.getOwner() && entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {
            CompoundNBT saved = new CompoundNBT();
            entity.save(saved);
            this.currentCaptureEntity = saved;
            entity.remove();

            // Capture logic
            boolean caught = this.canCapture((LivingEntity) entity);
            this.tickDelay = 0; // Reset tick delay for animations
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
        super.onHitBlock(p_230299_1_);
        if (currentCaptureEntity == null) {
            this.discard();
            this.spawnAtLocation(new ItemStack(Items.POKEBALL));

            if (data != null) {
                if (data.containsPokemon()) {
                    try {
                        data.getCapturedPokemon().ifPresent(entity -> {
                            entity.setPos(p_230299_1_.getBlockPos().above());
                            level.addFreshEntity(entity);
                        });
                        this.discard();
                    } catch (Exception e) {

                    }
                }
            }
        }
        this.setDeltaMovement(0, 0, 0);
    }

    public void onPkmnBrokeOut() {
        if (!this.level.isClientSide) {
            LivingEntity livingEntity = (LivingEntity) EntityType.loadEntityRecursive(this.currentCaptureEntity, this.level, entity -> {
                entity.setUUID(UUID.randomUUID());
                entity.setPos(this.blockPosition().offset(0, 0.5, 0));
                return entity;
            });
            if (livingEntity != null) {
                level.addFreshEntity(livingEntity);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putDouble("CaptureRate", this.captureRate);
        nbt.putInt("ShakeCount", this.entityData.get(SHAKE_AMOUNT));
        if (this.currentCaptureEntity != null) {
            nbt.put("CapturedEntity", this.currentCaptureEntity);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.captureRate = nbt.getDouble("CaptureRate");
        this.entityData.set(SHAKE_AMOUNT, nbt.getInt("ShakeCount"));
        if (nbt.contains("CapturedEntity")) {
            this.currentCaptureEntity = nbt.getCompound("CapturedEntity");
        }
    }
}
