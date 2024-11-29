package net.minecraft.entity.passive.allay;

import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.Creature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;  // Import MathHelper for lerp
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class AllayEntity extends Creature {
    private static final DataParameter<Boolean> IS_DANCING = EntityDataManager.defineId(AllayEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CAN_DUPLICATE = EntityDataManager.defineId(AllayEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<UUID>> TRUSTED_PLAYER = EntityDataManager.defineId(AllayEntity.class, DataSerializers.OPTIONAL_UUID);

    private final Inventory inventory = new Inventory(1);  // Small inventory of 1 slot
    private BlockPos jukeboxPos = null;
    private long duplicationCooldown;

    // Animation variables
    private float holdingItemAnimationTicks;
    private float holdingItemAnimationTicks0;
    private float dancingAnimationTicks;
    private float spinningAnimationTicks;
    private float spinningAnimationTicks0;
    // Animation states
    public final AnimationState holdingItemAnimationState = new AnimationState();
    public final AnimationState danceAnimationState = new AnimationState();

    public AllayEntity(EntityType<? extends AllayEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlyingMovementController(this, 20, true);  // Using flying movement control
        this.getNavigation().setCanFloat(true);
    }

    // Attributes
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.6D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new GoToWantedItemGoal(this, 1.2F, 32));  // Custom goal to go to items
        this.goalSelector.addGoal(3, new GiveItemToTrustedPlayerGoal(this, 1.5F));  // Custom goal to give item to trusted player
        this.goalSelector.addGoal(4, new StayCloseToTrustedPlayerGoal(this, 2.25F, 16));  // Stay close to trusted player
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));  // Wander around when not interacting
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    }

    // Synched data for client-server communication
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_DANCING, false);
        this.entityData.define(CAN_DUPLICATE, true);
        this.entityData.define(TRUSTED_PLAYER, Optional.empty());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide) {
            updateClientAnimations();
        } else {
            updateDuplicationCooldown();
        }
    }

    private void setupAnimationStates() {
        this.holdingItemAnimationState.animateWhen(this.hasItemInHand(), this.tickCount);
        this.danceAnimationState.animateWhen(this.isDancing(), this.tickCount);
    }

    private void updateClientAnimations() {
        // Handle animation updates on the client side for holding item, dancing, and spinning

        // Update holding item animation
        this.holdingItemAnimationTicks0 = this.holdingItemAnimationTicks;
        if (this.hasItemInHand()) {
            this.holdingItemAnimationTicks = MathHelper.clamp(this.holdingItemAnimationTicks + 1.0F, 0.0F, 5.0F);
        } else {
            this.holdingItemAnimationTicks = MathHelper.clamp(this.holdingItemAnimationTicks - 1.0F, 0.0F, 5.0F);
        }

        // Update dancing and spinning animations
        if (this.isDancing()) {
            this.dancingAnimationTicks++;
            this.spinningAnimationTicks0 = this.spinningAnimationTicks;
            if (this.isSpinning()) {
                this.spinningAnimationTicks++;
            } else {
                this.spinningAnimationTicks--;
            }
            this.spinningAnimationTicks = MathHelper.clamp(this.spinningAnimationTicks, 0.0F, 15.0F);
        } else {
            this.dancingAnimationTicks = 0.0F;
            this.spinningAnimationTicks = 0.0F;
            this.spinningAnimationTicks0 = 0.0F;
        }
    }

    private void updateDuplicationCooldown() {
        if (this.duplicationCooldown > 0) {
            this.duplicationCooldown--;
        }
        if (this.duplicationCooldown == 0 && !this.canDuplicate()) {
            this.entityData.set(CAN_DUPLICATE, true);
        }
    }

    // Animation progress functions

    // Get the progress of the holding item animation
    public float getHoldingItemAnimationProgress(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.holdingItemAnimationTicks0, this.holdingItemAnimationTicks) / 5.0F;
    }

    // Check if the entity is spinning
    public boolean isSpinning() {
        return (this.dancingAnimationTicks % 55.0F) < 15.0F;
    }

    // Get the progress of the spinning animation
    public float getSpinningProgress(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.spinningAnimationTicks0, this.spinningAnimationTicks) / 15.0F;
    }

    // Interaction with players
    @Override
    public ActionResultType interactAt(PlayerEntity player, Vector3d vec, Hand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack currentItem = this.getItemInHand(Hand.MAIN_HAND);

        if (this.isDancing() && this.isDuplicationItem(heldItem) && this.canDuplicate()) {
            duplicateAllay(player);
            this.playSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 1.0F, 1.0F);
            heldItem.shrink(1);
            return ActionResultType.SUCCESS;
        } else if (currentItem.isEmpty() && !heldItem.isEmpty()) {
            // Give the Allay an item to hold and trust the player
            this.setTrustedPlayer(player);
            ItemStack copy = heldItem.copy();
            copy.setCount(1);
            this.setItemInHand(Hand.MAIN_HAND, copy);
            heldItem.shrink(1);
            //this.playSound(SoundEvents.ALLAY_ITEM_GIVEN, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        } else if (!currentItem.isEmpty() && heldItem.isEmpty()) {
            // Take the item back from the Allay
            player.addItem(currentItem);
            this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
          //  this.playSound(SoundEvents.ALLAY_ITEM_TAKEN, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        }

        return super.interactAt(player, vec, hand);
    }

    // Duplicating the Allay
    private void duplicateAllay(PlayerEntity player) {
        AllayEntity newAllay = EntityType.ALLAY.create(this.level);
        if (newAllay != null) {
            newAllay.setPos(this.getX(), this.getY(), this.getZ());
            newAllay.setPersistenceRequired();
            this.resetDuplicationCooldown();
            newAllay.resetDuplicationCooldown();
            this.level.addFreshEntity(newAllay);
        }
    }

    // Reset duplication cooldown
    private void resetDuplicationCooldown() {
        this.duplicationCooldown = 6000;  // 5-minute cooldown
        this.entityData.set(CAN_DUPLICATE, false);
    }

    private boolean canDuplicate() {
        return this.entityData.get(CAN_DUPLICATE);
    }

    private boolean isDuplicationItem(ItemStack stack) {
        return stack.getItem() == Items.PALE_STICK;
    }

    // Set trusted player
    public void setTrustedPlayer(PlayerEntity player) {
        this.entityData.set(TRUSTED_PLAYER, Optional.of(player.getUUID()));
    }

    // Get trusted player UUID
    public Optional<UUID> getTrustedPlayer() {
        return this.entityData.get(TRUSTED_PLAYER);
    }

    // Inventory handling (example of save/load inventory state)
    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.put("Inventory", this.inventory.createTag());
        nbt.putLong("DuplicationCooldown", this.duplicationCooldown);
        nbt.putBoolean("CanDuplicate", this.canDuplicate());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.inventory.fromTag(nbt.getList("Inventory", 10));
        this.duplicationCooldown = nbt.getLong("DuplicationCooldown");
        this.entityData.set(CAN_DUPLICATE, nbt.getBoolean("CanDuplicate"));
    }

    public boolean hasItemInHand() {
        return !this.getItemInHand(Hand.MAIN_HAND).isEmpty();
    }

    // Helper method to check if the Allay is dancing
    public boolean isDancing() {
        return this.entityData.get(IS_DANCING);
    }

    public void setDancing(boolean dancing) {
        this.entityData.set(IS_DANCING, dancing);
    }
}
