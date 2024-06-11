package net.minecraft.entity.projectile.custom.arrow;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CustomArrowEntity extends AbstractArrowEntity {

    private static final DataParameter<CustomArrowType> ARROW_TYPE = EntityDataManager.defineId(CustomArrowEntity.class, DataSerializers.CUSTOM_ARROW_TYPE);
    private static final Map<CustomArrowType, Item> ARROW_ITEM_MAP = new HashMap<>();



    static {
        ARROW_ITEM_MAP.put(CustomArrowType.BURNING, Items.BURNING_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.POISON, Items.POISON_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.FROZEN, Items.ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.TELEPORTATION, Items.TELEPORTATION_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.HEALING, Items.HEALING_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.FIREWORK, Items.FIREWORK_ARROW);
        // Add other custom arrow types here
    }

    private double baseDamage = this.getArrowType().getBaseDamage();

    public CustomArrowEntity(EntityType<? extends CustomArrowEntity> arrow, World world) {
        super(arrow, world);
        this.baseDamage = this.getArrowType().getBaseDamage();
    }

    public CustomArrowEntity(World world, double x, double y, double z) {
        super(EntityType.CUSTOM_ARROW, x, y, z, world);
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
        return this.baseDamage;
    }


    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ArrowType", getArrowType().getType()); // Save the type of the arrow
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        CustomArrowType customArrowType = CustomArrowType.getCustomArrowTypeByType(compound.getInt("ArrowType")); //get arrow type by saved type
        if (customArrowType == null)
            customArrowType = CustomArrowType.NULL; // Default to NULL type when not found
        setArrowType(customArrowType);
    }

    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        CustomArrowType type = this.getArrowType();
        int id = type.getType();
        switch (id) {
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
                Random random = new Random();
                final int[] fireworks = new int[random.nextInt(8) + 1];
                for (int i = 0; i < fireworks.length; i++) {
                    ItemStack item = createCustomFirework();
                    double randomX = random.nextDouble();
                    double randomZ = random.nextDouble();
                    FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(this.level, this.getOwner(), entity.getX() + randomX, entity.getY() + 0.5, entity.getZ() + randomZ, item);
                    fireworkRocket.setFromFireworkArrow(true);
                    double deltaX = random.nextDouble() * 0.2 - 0.1;
                    double deltaY = random.nextDouble() * 0.2 - 0.1;
                    double deltaZ = random.nextDouble() * 0.2 - 0.1;
                    if (random.nextFloat() < 0.4F) {
                        fireworkRocket.setDeltaMovement(deltaX, deltaY + 0.2, deltaZ);
                    } else {
                        fireworkRocket.setDeltaMovement(deltaX, deltaY - 0.4, deltaZ);
                    }
                    this.level.addFreshEntity(fireworkRocket);
                }
                break;
        }
    }

    protected void onHitBlock(BlockRayTraceResult result) {
        BlockState blockstate = this.level.getBlockState(result.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, result, this);
        CustomArrowType type = this.getArrowType();
        int id = type.getType();
        switch (id) {
            case 5:
                ItemStack item = createCustomFirework();
                FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(this.level,this.getOwner(),this.getX(),this.getY() - 0.2,this.getZ(), item);
                fireworkRocket.setFromFireworkArrow(true);
                this.level.addFreshEntity(fireworkRocket);
                this.remove();
                break;

        }
    }

    @Override
    protected ItemStack getPickupItem() {
        CustomArrowType type = this.getArrowType();
        return new ItemStack(ARROW_ITEM_MAP.getOrDefault(type, Items.ARROW));
    }

    public double randomDelta() {
        return random.nextDouble() * 0.2 - 0.1;
    }



    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte b) { //specify what particle effects to show etc
        super.handleEntityEvent(b);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ARROW_TYPE, CustomArrowType.NULL);
    }

    public void useTeleportation(World world, LivingEntity living) {
        if (!world.isClientSide) {
            double d0 = living.getX();
            double d1 = living.getY();
            double d2 = living.getZ();

            for(int i = 0; i < 16; ++i) {
                double d3 = living.getX() + (living.getRandom().nextDouble() - 0.5D) * 16.0D;
                double d4 = MathHelper.clamp(living.getY() + (double)(living.getRandom().nextInt(16) - 8), 0.0D, (double)(world.getHeight() - 1));
                double d5 = living.getZ() + (living.getRandom().nextDouble() - 0.5D) * 16.0D;
                if (living.isPassenger()) {
                    living.stopRiding();
                }

                if (living.randomTeleport(d3, d4, d5, true)) {
                    SoundEvent soundevent = living instanceof FoxEntity ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                    world.playSound((PlayerEntity)null, d0, d1, d2, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    living.playSound(soundevent, 1.0F, 1.0F);
                    break;
                }
            }
        }
    }

    public ItemStack createCustomFirework() {
        Random random = new Random();
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET, 1);
        CompoundNBT fireworkTag = new CompoundNBT();

        // Set the flight duration
        fireworkTag.putByte("Flight", (byte) random.nextInt(2));

        // Create the explosions
        ListNBT explosions = new ListNBT();
        CompoundNBT explosion = new CompoundNBT();
        explosion.putByte("Type", (byte) random.nextInt(6)); // Random type of explosion (0-4)
        // Generate random colors
        int[] colors = generateRandomColors(random);
        explosion.putIntArray("Colors", colors);

        // Generate random fade colors
        int[] fadeColors = generateRandomColors(random);
        explosion.putIntArray("FadeColors", fadeColors);

        // Random trail and flicker
        explosion.putBoolean("Trail", random.nextBoolean());
        explosion.putBoolean("Flicker", random.nextBoolean());
        explosions.add(explosion);

        fireworkTag.put("Explosions", explosions);

        // Set the Firework tag to the ItemStack
        CompoundNBT itemTag = new CompoundNBT();
        itemTag.put("Fireworks", fireworkTag);
        fireworkStack.setTag(itemTag);

        return fireworkStack;
    }

    private int[] generateRandomColors(Random random) {
        int numColors = random.nextInt(3) + 1; // Random number of colors (1-3)
        int[] colors = new int[numColors];
        for (int i = 0; i < numColors; i++) {
            colors[i] = random.nextInt(0xFFFFFF + 1); // Random color (0x000000 to 0xFFFFFF)
        }
        return colors;
    }

}