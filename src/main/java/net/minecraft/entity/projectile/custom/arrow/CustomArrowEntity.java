package net.minecraft.entity.projectile.custom.arrow;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.monster.TricksterEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.StarfuryStarEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class CustomArrowEntity extends AbstractArrowEntity {

    private static final DataParameter<CustomArrowType> ARROW_TYPE = EntityDataManager.defineId(CustomArrowEntity.class, DataSerializers.CUSTOM_ARROW_TYPE);
    private static final Map<CustomArrowType, Item> ARROW_ITEM_MAP = new HashMap<>();
    private static final Random RANDOM = new Random();
    private boolean blindness = false;
    private ItemStack fireworkStack = null;
    private boolean noDamage = false;
    private boolean canFireSpread = true;
    private LivingEntity unableToHit = null;

    private int speedUps = 0;

    static {
        ARROW_ITEM_MAP.put(CustomArrowType.BURNING, Items.BURNING_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.POISON, Items.POISON_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.FROZEN, Items.ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.TELEPORTATION, Items.TELEPORTATION_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.HEALING, Items.HEALING_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.FIREWORK, Items.FIREWORK_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.GILDED, Items.GILDED_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.FLEETING, Items.FLEETING_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.MEEP, Items.MEEP_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.AERIAL_BANE, Items.ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.JESTER, Items.JESTER_ARROW);

    }

    public void setNoDamage() {
        this.noDamage = true;
    }

    public void setFireworkStack(ItemStack fireworkStack) {
        this.fireworkStack = fireworkStack;
    }


    public CustomArrowEntity(EntityType<? extends CustomArrowEntity> arrow, World world) {
        super(arrow, world);
        this.addBaseDamage(this.getArrowType().getBaseDamage());
    }

    @MethodsReturnNonnullByDefault
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return this.getArrowType() == CustomArrowType.AERIAL_BANE ? SoundEvents.ITEM_14 : super.getDefaultHitGroundSoundEvent();
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
        if (noDamage)return 0d;

        return super.getBaseDamage();
    }

    @ParametersAreNonnullByDefault
    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SpeedUps", this.speedUps);
        compound.putInt("ArrowType", getArrowType().getType()); // Save the type of the arrow
        compound.putBoolean("Blindness", this.blindness);
        if (fireworkStack != null) {
            compound.put("Firework", this.fireworkStack.save(new CompoundNBT()));
        }
        compound.putBoolean("NoDamage", this.noDamage);
        compound.putBoolean("CanFireSpread", this.canFireSpread);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        CustomArrowType customArrowType = CustomArrowType.getCustomArrowTypeByType(compound.getInt("ArrowType")); // get arrow type by saved type
        if (customArrowType == null)
            customArrowType = CustomArrowType.NULL; // Default to NULL type when not found
        setArrowType(customArrowType);
        this.speedUps = compound.getInt("SpeedUps");
        this.blindness = compound.getBoolean("Blindness");
        if (compound.contains("Firework")) {
            this.fireworkStack = ItemStack.read(compound.getCompound("Firework"));
        }
        this.noDamage = compound.getBoolean("NoDamage");
        this.canFireSpread = compound.getBoolean("CanFireSpread");
    }

    @ParametersAreNonnullByDefault
    protected void doPostHurtEffects(LivingEntity entity) {
        if (!noDamage) {
            super.doPostHurtEffects(entity);
        }
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
                if (this.blindness) {
                    entity.addEffect(new EffectInstance(Effects.BLINDNESS, 10 * 20, 0, false, true));
                }
                spawnFireworks(entity);
                break;

            case 9: {

                break;
            }
        }
    }

    public void fireArrowSpread(LivingEntity targetHit) {
        this.playSound(SoundEvents.ITEM_14, 1.0F, 1.0F);
        float[] spreadOffsets = {-45f, -22.5f, 0f, 22.5f, 45f};

        Vector3d arrowPos = this.position();
        Vector3d baseDir = this.getDeltaMovement().normalize();

        Vector3f upVector = new Vector3f(0, 1, 0);

        for (float offset : spreadOffsets) {
            CustomArrowEntity customArrow = new CustomArrowEntity(level, this.getOwner().as(LivingEntity.class));
            customArrow.setPos(arrowPos);
            customArrow.setArrowType(this.getArrowType());
            customArrow.canFireSpread = false;
            customArrow.pickup = PickupStatus.DISALLOWED;
            customArrow.setBaseDamage(this.getBaseDamage());

            customArrow.unableToHit = targetHit;

            Vector3f dir = new Vector3f((float)baseDir.x, (float)baseDir.y, (float)baseDir.z);
            Quaternion rotation = new Quaternion(upVector, offset, true);
            dir.transform(rotation);

            float velocity = 3.0f;
            float inaccuracy = 0.0f;

            customArrow.shoot(dir.x(), dir.y(), dir.z(), velocity, inaccuracy);

            level.addFreshEntity(customArrow);
        }
    }




    public void giveBlindness() {

        this.blindness = true;
    }

    private void spawnFireworks(LivingEntity entity) {


        int numFireworks = RANDOM.nextInt(8) + 1;
        for (int i = 0; i < numFireworks; i++) {
            ItemStack item = fireworkStack != null ? fireworkStack : FireworkUtility.newFirework();
            double randomX = RANDOM.nextDouble();
            double randomZ = RANDOM.nextDouble();
            FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(this.level, this.getOwner(), entity.getX() + randomX, entity.getY() + 0.5, entity.getZ() + randomZ, item);
            fireworkRocket.setFromFireworkArrow(true, this.blindness);
            double deltaX = randomDelta();
            double deltaY = randomDelta();
            double deltaZ = randomDelta();
            if (noDamage) {
                fireworkRocket.setNoDamage();
            }
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

        if (this.getArrowType().getType() == 7 || this.getArrowType().getType() == 8) {
            int t = this.getArrowType().getType();
            if (!onGround && this.tickCount > (t == 7 ? 5 : 3) && !inGround && ((this.getDeltaMovement().length() * 20) > 0.5)) {
                if (this.tick(2)) {
                    if (speedUps < (t == 7 ? 7 : 16)) {
                        speedUps++;
                        this.setDeltaMovement(this.getDeltaMovement().multiply(t == 7 ? 1.13 : 1.3));
                    }
                }
            }
            if (this.tickCount <= 15 && !(this.getPierceLevel() >= 8)) {
                this.setPierceLevel((byte) (this.getPierceLevel() + 8));
            }

            if (this.tick(15) && t == 8 && !onGround && !inGround) {
                this.playSound(SoundEvents.ROADRUNNER_MEEP, 1.0F, 1.0F);
            }

            if (this.level.isClientSide && t == 8 && !this.onGround && !this.isInWaterOrBubble() && !this.inGround) {
                Vector3d vector3d = this.getViewVector(0.0F);
                final float yRotRad = (float) (this.yRot * MathHelper.DEG_TO_RAD);
                float f = MathHelper.cos(yRotRad) * 0.2F;
                float f1 = MathHelper.sin(yRotRad) * 0.2F;
                float f2 = 1.2F - this.random.nextFloat() * 0.7F;
                for (int i = 0; i < 3; ++i) {
                    this.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() - vector3d.x * (double) f2 + (double) f, this.getY() + random.nextFloat() * 0.2F, this.getZ() - vector3d.z * (double) f2 + (double) f1, 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() - vector3d.x * (double) f2 - (double) f, this.getY() + random.nextFloat() * 0.2F, this.getZ() - vector3d.z * (double) f2 - (double) f1, 0.0D, 0.0D, 0.0D);
                }
            }

            if (this.level.isClientSide && t == 8 && inGround) {
                if (random.nextFloat() < 0.11F) {
                    CampfireBlock.makeParticles(level, this.getBlockPosBelowThatAffectsMyMovement(), true, false);
                }
            }
        }

        if (this.getArrowType() == CustomArrowType.AERIAL_BANE) {
            if (!this.inGround) {
                if (level.isClientSide) {
                    for (int i = 0; i < 6; i++) {
                        this.level.addAlwaysVisibleParticle(ParticleTypes.FLAME, this.getX() + random.nextGaussian() * 0.2,
                                this.getY() + random.nextGaussian() * 0.2,
                                this.getZ() + random.nextGaussian() * 0.2,
                                0, 0, 0);
                    }
                    for (int i = 0; i < 4; i++) {
                        this.level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, this.getX() + random.nextGaussian() * 0.1,
                                this.getY() + random.nextGaussian() * 0.1,
                                this.getZ() + random.nextGaussian() * 0.1,
                                0, 0, 0);
                    }
                }
            } else {
                this.playSound(SoundEvents.ITEM_14, 1.0F, 1.0F);
                for (int i = 0; i < 1; i++) {
                    this.level.addAlwaysVisibleParticle(ParticleTypes.EXPLOSION, this.getX() + random.nextGaussian() * 0.2,
                            this.getY() + random.nextGaussian() * 0.2,
                            this.getZ() + random.nextGaussian() * 0.2,
                            0, 0, 0);
                }
                remove();
            }
        }

        if (this.getArrowType() == CustomArrowType.JESTER) {
            if (this.getPierceLevel() <= 0 || this.tickCount < 4) {
                this.setPierceLevel((byte) 126);
            }

            if (!this.isNoGravity()) {
                this.setNoGravity(true);
            }
            StarfuryStarEntity.makeColouredParticles(6, level, this, DyeColor.PURPLE.getFireworkColor());
            StarfuryStarEntity.makeColouredParticles(6, level, this, DyeColor.YELLOW.getFireworkColor());
            StarfuryStarEntity.makeColouredParticles(6, level, this, DyeColor.LIGHT_BLUE.getFireworkColor());


            if (this.life > 80 && !onGround && !inGround) {
                remove();
            } else {
                if (level.isServerSide) {
                    ++life;
                }
            }

            if (inGround || onGround) {
                remove();
            }
        }

        List<Monster> nearbyEntities = this.level.getEntitiesOfClass(Monster.class, this.getBoundingBox().inflate(1.6D), monster -> monster != this.getOwner());
        PlayerEntity player = level.getNearestSurvivalPlayer(this, 1);
        if (player==this.getOwner()) player = null;
        if (this.getOwner() instanceof Monster) {
            Iterator<Monster> iterator = nearbyEntities.iterator();
            while (iterator.hasNext()) {
                Monster monster = iterator.next();
                if (((Monster) this.getOwner()).getTarget() != null && monster == ((Monster) this.getOwner()).getTarget() || this.getOwner() instanceof TricksterEntity) {
                    iterator.remove(); // Safely remove the element during iteration
                }
            }
        }
        if ((!nearbyEntities.isEmpty() || player != null) && this.getArrowType() == CustomArrowType.FIREWORK && this.getGravityLevel() <= 0) {
            this.spawnFireworkOnBlockHit(true);
        }
    }



    private void spawnFireworkOnBlockHit(boolean explode) {
        ItemStack item = fireworkStack != null ? fireworkStack : FireworkUtility.newFirework();
        FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(this.level, this.getOwner(), this.getX(), this.getY() - 0.2, this.getZ(), item);
        fireworkRocket.setFromFireworkArrow(true, this.blindness);
        if (noDamage) {
            fireworkRocket.setNoDamage();
        }
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
        if (this.unableToHit != null && result.getEntity() == this.unableToHit) return;

        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            // Handle additional logic for hitting an entity if needed

            if (canFireSpread && this.getArrowType() == CustomArrowType.AERIAL_BANE) {
                fireArrowSpread(livingEntity);
            }

            if (this.getArrowType() == CustomArrowType.JESTER) {
                this.life = 10;
            }
        }
        if (!noDamage) {
            super.onHitEntity(result);
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
