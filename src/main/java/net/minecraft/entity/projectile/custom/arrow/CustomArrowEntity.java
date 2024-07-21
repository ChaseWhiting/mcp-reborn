package net.minecraft.entity.projectile.custom.arrow;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class CustomArrowEntity extends AbstractArrowEntity {

    private static final DataParameter<CustomArrowType> ARROW_TYPE = EntityDataManager.defineId(CustomArrowEntity.class, DataSerializers.CUSTOM_ARROW_TYPE);
    private static final Map<CustomArrowType, Item> ARROW_ITEM_MAP = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        ARROW_ITEM_MAP.put(CustomArrowType.BURNING, Items.BURNING_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.POISON, Items.POISON_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.FROZEN, Items.ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.TELEPORTATION, Items.TELEPORTATION_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.HEALING, Items.HEALING_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.FIREWORK, Items.FIREWORK_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.GILDED, Items.GILDED_ARROW);
        // Add other custom arrow types here
    }


    public CustomArrowEntity(EntityType<? extends CustomArrowEntity> arrow, World world) {
        super(arrow, world);
        this.addBaseDamage(this.getArrowType().getBaseDamage());
    }

    public CustomArrowEntity(World world, double x, double y, double z) {
        super(EntityType.CUSTOM_ARROW, x, y, z, world);
        this.addBaseDamage(this.getArrowType().getBaseDamage());
    }

    public CustomArrowEntity(World world, LivingEntity entity) {
        super(EntityType.CUSTOM_ARROW, entity, world);
    }

    public void setArrowType(CustomArrowType arrowType){
        this.getEntityData().set(ARROW_TYPE, arrowType);
    }

    public CustomArrowType getArrowType(){
        return this.getEntityData().get(ARROW_TYPE);
    }

    public double getBaseDamage() {
        return super.getBaseDamage();
    }

    @ParametersAreNonnullByDefault
    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ArrowType", getArrowType().getType()); // Save the type of the arrow
    }

    @ParametersAreNonnullByDefault
    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        CustomArrowType customArrowType = CustomArrowType.getCustomArrowTypeByType(compound.getInt("ArrowType")); // get arrow type by saved type
        if (customArrowType == null)
            customArrowType = CustomArrowType.NULL; // Default to NULL type when not found
        setArrowType(customArrowType);
    }

    @ParametersAreNonnullByDefault
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        CustomArrowType type = this.getArrowType();
        applyArrowEffects(entity, type);
    }

    private void applyArrowEffects(LivingEntity entity, CustomArrowType type) {
        switch (type.getType()) {
            case 1:
                entity.setSecondsOnFire(6);
                break;
            case 2:
                entity.addEffect(new EffectInstance(Effects.POISON, 120, 1));
                break;
            case 3:
                useTeleportation(entity.level, entity);
                break;
            case 4:
                entity.addEffect(new EffectInstance(Effects.HEAL, 1, 1));
                break;
            case 5:
                spawnFireworks(entity);
                break;
        }
    }

    private void spawnFireworks(LivingEntity entity) {
        int numFireworks = RANDOM.nextInt(8) + 1;
        for (int i = 0; i < numFireworks; i++) {
            ItemStack item = FireworkUtility.newFirework();
            double randomX = RANDOM.nextDouble();
            double randomZ = RANDOM.nextDouble();
            FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(this.level, this.getOwner(), entity.getX() + randomX, entity.getY() + 0.5, entity.getZ() + randomZ, item);
            fireworkRocket.setFromFireworkArrow(true);
            double deltaX = randomDelta();
            double deltaY = randomDelta();
            double deltaZ = randomDelta();
            if (RANDOM.nextFloat() < 0.4F) {
                fireworkRocket.setDeltaMovement(deltaX, deltaY + 0.2, deltaZ);
            } else {
                fireworkRocket.setDeltaMovement(deltaX, deltaY - 0.4, deltaZ);
            }
            this.level.addFreshEntity(fireworkRocket);
        }
    }

    @Nullable
    public Entity getEntityByUUID(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return ((ServerWorld) this.level).getEntity(uuid);
    }

    protected void onHitBlock(BlockRayTraceResult result) {
        super.onHitBlock(result);
        BlockState blockstate = this.level.getBlockState(result.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, result, this);
        CustomArrowType type = this.getArrowType();
        handleBlockHitEffects(type);
    }

    private void handleBlockHitEffects(CustomArrowType type) {
        switch (type.getType()) {
            case 3:
                handleTeleportationHit();
                break;
            case 5:
                spawnFireworkOnBlockHit(false);
                break;
        }
    }

    private void handleTeleportationHit() {
        this.remove();
        if (this.getOwner() != null) {
            LivingEntity owner = (LivingEntity) this.getOwner();
            double targetX = this.blockPosition().getX() + 0.5;
            double targetY = this.blockPosition().getY() + 1;
            double targetZ = this.blockPosition().getZ() + 0.5;
            useTeleportation(this.level, owner, targetX, targetY, targetZ);
            if (owner instanceof PlayerEntity && !this.level.isClientSide()) {
                PlayerEntity player = (PlayerEntity) owner;
                player.getCooldowns().addCooldown(Items.TELEPORTATION_ARROW, 120);
            }
        }
    }

    public void tick() {
        super.tick();

        List<Monster> nearbyEntities = this.level.getEntitiesOfClass(Monster.class, this.getBoundingBox().inflate(1.6D));
        if (!nearbyEntities.isEmpty() && this.getArrowType() == CustomArrowType.FIREWORK && this.getGravityLevel() <= 0) {
            this.spawnFireworkOnBlockHit(true);
        }
    }



    private void spawnFireworkOnBlockHit(boolean explode) {
        ItemStack item = FireworkUtility.newFirework();
        FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(this.level, this.getOwner(), this.getX(), this.getY() - 0.2, this.getZ(), item);
        fireworkRocket.setFromFireworkArrow(true);
        List<Monster> targets = this.level.getEntitiesOfClass(Monster.class, this.getBoundingBox().inflate(8D, 6D, 8D));
        if (!targets.isEmpty()) {
            fireworkRocket.setDeltaMovement(!explode ? this.getDeltaMovement().x : 0, -0.3, !explode ? this.getDeltaMovement().z : 0);
        }
        this.level.addFreshEntity(fireworkRocket);
        this.remove();
    }

    @ParametersAreNonnullByDefault
    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            // Handle additional logic for hitting an entity if needed
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        CustomArrowType type = this.getArrowType();
        return new ItemStack(ARROW_ITEM_MAP.getOrDefault(type, Items.ARROW));
    }

    public double randomDelta() {
        return RANDOM.nextDouble() * 0.2 - 0.1;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte b) { // specify what particle effects to show etc.
        super.handleEntityEvent(b);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARROW_TYPE, CustomArrowType.NULL);
    }

    public void useTeleportation(World world, LivingEntity living, double targetX, double targetY, double targetZ) {
        if (!world.isClientSide) {
            double d0 = living.getX();
            double d1 = living.getY();
            double d2 = living.getZ();
            if (living.isPassenger()) {
                living.stopRiding();
            }
            if (living.randomTeleport(targetX, targetY, targetZ, true)) {
                playTeleportSound(world, living, d0, d1, d2);
            }
        }
    }

    public void useTeleportation(World world, LivingEntity living) {
        if (!world.isClientSide) {
            double d0 = living.getX();
            double d1 = living.getY();
            double d2 = living.getZ();
            for (int i = 0; i < 16; ++i) {
                double d3 = living.getX() + (living.getRandom().nextDouble() - 0.5D) * 16.0D;
                double d4 = MathHelper.clamp(living.getY() + (double)(living.getRandom().nextInt(16) - 8), 0.0D, (double)(world.getHeight() - 1));
                double d5 = living.getZ() + (living.getRandom().nextDouble() - 0.5D) * 16.0D;
                if (living.isPassenger()) {
                    living.stopRiding();
                }
                if (living.randomTeleport(d3, d4, d5, true)) {
                    playTeleportSound(world, living, d0, d1, d2);
                    break;
                }
            }
        }
    }

    private void playTeleportSound(World world, LivingEntity living, double d0, double d1, double d2) {
        SoundEvent soundevent = living instanceof FoxEntity ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
        world.playSound(null, d0, d1, d2, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
        living.playSound(soundevent, 1.0F, 1.0F);
    }

    public ItemStack createCustomFirework() {
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        CompoundNBT fireworkTag = new CompoundNBT();
        // Set the flight duration
        fireworkTag.putByte("Flight", (byte) RANDOM.nextInt(2));
        // Create the explosions
        ListNBT explosions = new ListNBT();
        CompoundNBT explosion = new CompoundNBT();
        explosion.putByte("Type", (byte) RANDOM.nextInt(6)); // Random type of explosion (0-4)
        // Generate random colors
        int[] colors = generateRandomColors();
        explosion.putIntArray("Colors", colors);
        // Generate random fade colors
        int[] fadeColors = generateRandomColors();
        explosion.putIntArray("FadeColors", fadeColors);
        // Random trail and flicker
        explosion.putBoolean("Trail", RANDOM.nextBoolean());
        explosion.putBoolean("Flicker", RANDOM.nextBoolean());
        explosions.add(explosion);
        fireworkTag.put("Explosions", explosions);
        // Set the Firework tag to the ItemStack
        CompoundNBT itemTag = new CompoundNBT();
        itemTag.put("Fireworks", fireworkTag);
        fireworkStack.setTag(itemTag);
        System.out.println("Compound of arrow: " + FireworkUtility.getExplosionsTag(fireworkStack));
        return fireworkStack;
    }

    private int[] generateRandomColors() {
        int numColors = RANDOM.nextInt(3) + 1; // Random number of colors (1-3)
        int[] colors = new int[numColors];
        for (int i = 0; i < numColors; i++) {
            colors[i] = RANDOM.nextInt(0xFFFFFF + 1); // Random color (0x000000 to 0xFFFFFF)
        }
        return colors;
    }

    public ItemStack newFirework() {
        FireworkUtility.Builder builder = new FireworkUtility.Builder();
        return builder.addExplosion(FireworkRocketItem.Shape.getRandomShape(), new DyeColor[]{DyeColor.ORANGE, DyeColor.RED}, new DyeColor[]{DyeColor.ORANGE, DyeColor.BLACK}, true, true).build();
    }

    public ItemStack newFirework(FireworkRocketItem.Shape shape, DyeColor[] colors, DyeColor[] flickerColors, boolean trail, boolean flicker) {
        FireworkUtility.Builder builder = new FireworkUtility.Builder();
        return builder.addExplosion(shape, colors, flickerColors, trail, flicker).build();
    }




}
