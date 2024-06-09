package net.minecraft.entity.projectile.custom.arrow;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

public class CustomArrowEntity extends AbstractArrowEntity {

    private static final DataParameter<CustomArrowType> ARROW_TYPE = EntityDataManager.defineId(CustomArrowEntity.class, DataSerializers.CUSTOM_ARROW_TYPE);
    private static final Map<CustomArrowType, Item> ARROW_ITEM_MAP = new HashMap<>();

    static {
        ARROW_ITEM_MAP.put(CustomArrowType.BURNING, Items.BURNING_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.POISON, Items.POISON_ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.FROZEN, Items.ARROW);
        ARROW_ITEM_MAP.put(CustomArrowType.TELEPORTATION, Items.TELEPORTATION_ARROW);
        // Add other custom arrow types here
    }

    private double baseDamage;

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
                entity.addEffect(new EffectInstance(Effects.POISON, 120, 0));
                break;
            case 3:
                useTeleportation(entity.level, entity);
                break;
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        CustomArrowType type = this.getArrowType();
        return new ItemStack(ARROW_ITEM_MAP.getOrDefault(type, Items.ARROW));
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

}