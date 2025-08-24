package net.minecraft.entity.frog;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.controller.SmoothSwimmingLookControl;
import net.minecraft.entity.ai.controller.SmoothSwimmingMoveControl;
import net.minecraft.entity.axolotl.Bucketable;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class TadpoleEntity extends AbstractFishEntity implements Bucketable {

    public static int ticksToBeFrog = Math.abs(-24000);
    public static float HITBOX_WIDTH = 0.4f;
    public static float HITBOX_HEIGHT = 0.3f;
    private int age;

    protected static final ImmutableList<SensorType<? extends Sensor<? super TadpoleEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.FROG_TEMPTATIONS);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PANICKING);


    public TadpoleEntity(EntityType<? extends AbstractFishEntity> type, World level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02f, 0.1f, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);

    }

    @Override
    protected PathNavigator createNavigation(World p_175447_1_) {
        return new SwimmerPathNavigator(this, p_175447_1_);
    }

    protected Brain.BrainCodec<TadpoleEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return TadpoleAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    public Brain<TadpoleEntity> getBrain() {
        return (Brain<TadpoleEntity>) super.getBrain();
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.TADPOLE_FLOP;
    }



    public boolean fromBucket() {
        return true;
    }
    public void setFromBucket(boolean bl){}

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createMobAttributes().add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.MAX_HEALTH, 6.0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            this.setAge(this.age + 1);
        }
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPacketSender.sendEntityBrain(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Age", this.age);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setAge(compoundTag.getInt("Age"));
    }

    public void saveToBucketTag(ItemStack stack) {
        Bucketable.saveDefaultDataToBucketTag(this, stack);
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putInt("Age", this.getAge());
    }


    public void loadFromBucketTag(CompoundNBT compoundTag) {
        Bucketable.loadDefaultDataFromBucketTag(this, compoundTag);
        if (compoundTag.contains("Age")) {
            this.setAge(compoundTag.getInt("Age"));
        }
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("tadpoleBrain");
        this.getBrain().tick((ServerWorld) this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("tadpoleActivityUpdate");
        TadpoleAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.TADPOLE_BUCKET);
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_TADPOLE;
    }

    private boolean isFood(ItemStack itemStack) {
        return FrogEntity.TEMPTATION_ITEM.test(itemStack);
    }

    private void feed(PlayerEntity player, ItemStack itemStack) {
        this.usePlayerItem(player, itemStack);
        this.ageUp(getSpeedUpSecondsWhenFeeding(this.getTicksLeftUntilAdult()));
        this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
    }

    public static int getSpeedUpSecondsWhenFeeding(int n) {
        return (int)((float)(n / 20) * 0.1f);
    }

    private void usePlayerItem(PlayerEntity player, ItemStack itemStack) {
        if (!player.abilities.instabuild) {
            itemStack.shrink(1);
        }
    }

    private int getAge() {
        return this.age;
    }

    private void ageUp(int n) {
        this.setAge(this.age + n * 20);
    }

    private void setAge(int n) {
        this.age = n;
        if (this.age >= ticksToBeFrog) {
            this.ageUp();
        }
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.TADPOLE_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.TADPOLE_DEATH;
    }

    private void ageUp() {
        Object object = this.level;
        if (object instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld) object;
            object = EntityType.FROG.create(this.level);
            if (object != null) {
                ((Entity)object).moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
                ((FrogEntity)object).finalizeSpawn(serverLevel, this.level.getCurrentDifficultyAt(((Entity)object).blockPosition()), SpawnReason.CONVERSION, null, null);
                ((Mob)object).setNoAi(this.isNoAi());
                if (this.hasCustomName()) {
                    ((Entity)object).setCustomName(this.getCustomName());
                    ((Entity)object).setCustomNameVisible(this.isCustomNameVisible());
                }
                ((Mob)object).setPersistenceRequired();
                this.playSound(SoundEvents.TADPOLE_GROW_UP, 0.15f, 1.0f);
                serverLevel.addFreshEntityWithPassengers((Entity)object);
                this.discard();
            }
        }
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (this.isFood(itemStack)) {
            this.feed(player, itemStack);
            return ActionResultType.sidedSuccess(this.level.isClientSide);
        }
        return Bucketable.bucketMobPickup(player, interactionHand, this).orElse(super.mobInteract(player, interactionHand));
    }

    private int getTicksLeftUntilAdult() {
        return Math.max(0, ticksToBeFrog - this.age);
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }


}
