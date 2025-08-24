package net.minecraft.item.dagger;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public class DesolateDaggerEntity extends Entity {

    private static final DataParameter<Integer> TARGET_ID = EntityDataManager.defineId(DesolateDaggerEntity.class, DataSerializers.INT);
    private static final DataParameter<Float> STAB = EntityDataManager.defineId(DesolateDaggerEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> PLAYER_ID = EntityDataManager.defineId(DesolateDaggerEntity.class, DataSerializers.INT);
    private static final DataParameter<ItemStack> ITEMSTACK = EntityDataManager.defineId(DesolateDaggerEntity.class, DataSerializers.ITEM_STACK);

    protected final Random orbitRandom = new Random();
    private float orbitOffset = 0;
    private float prevStab = 0;
    public int orbitFor = 20;
    public ItemStack daggerRenderStack = new ItemStack(Items.DESOLATE_DAGGER);
    public PlayerEntity player;
    public Entity entity;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    private boolean playedSummonNoise = false;

    public DesolateDaggerEntity(EntityType<?> entityType, World level) {
        super(entityType, level);
        orbitFor = 20 + level.random.nextInt(10);
    }

    public DesolateDaggerEntity(World world, double x, double y, double z) {
        super(EntityType.DESOLATE_DAGGER, world);
        this.setPos(x, y, z);

    }



    @Override
    public void tick() {
        super.tick();
        prevStab = this.getStab();
        Entity entity = getTargetEntity();
        if (level().isClientSide) {
            level().addParticle(RedstoneParticleData.REDSTONE, (double) this.getRandomX(0.75F), (double) this.getRandomY(), (double) this.getRandomZ(0.75F), 0.0D, 0.0D, 0.0D);
        }
        if (!playedSummonNoise) {
            this.playSound(SoundEvents.DESOLATE_DAGGER_SUMMON, 1.0F, 1.0F);
            playedSummonNoise = true;
        }
        if (entity != null) {
            this.noPhysics = true;
            float invStab = 1F - getStab();
            Vector3d orbitAround = entity.position().add(0, entity.getBbHeight() * 0.25F, 0);
            orbitRandom.setSeed(this.getId());
            if (orbitOffset == 0) {
                orbitOffset = orbitRandom.nextInt(360);
            }
            Vector3d orbitAdd = new Vector3d(0, (orbitRandom.nextFloat() + entity.getBbHeight()) * invStab, (orbitRandom.nextFloat() + entity.getBbWidth()) * invStab).yRot((float) Math.toRadians((orbitOffset)));
            this.setDeltaMovement(orbitAround.add(orbitAdd).subtract(this.position()));
            if (!level().isClientSide) {
                if (orbitFor > 0 && entity.isAlive()) {
                    orbitFor--;
                } else {
                    this.setStab(Math.min(this.getStab() + 0.2F, 1F));
                }
                if (this.getStab() >= 1F) {
                    Entity player = getPlayer();
                    Entity damageFrom = player == null ? this.entity != null ? this.entity : this : player;
                    float damage = 2 + this.getItemStack().getEnchantmentLevel(Enchantments.IMPENDING_STAB) * 2F;
                    if (entity.hurt(DamageSource.causeDesolateDaggerDamage(damageFrom), damage)) {
                        this.playSound(SoundEvents.DESOLATE_DAGGER_HIT, 1.0F, 1.0F);
                        int healBy = this.getItemStack().getEnchantmentLevel(Enchantments.SATED_BLADE);
                        if(healBy > 0 && damageFrom instanceof PlayerEntity){
                            PlayerEntity healPlayer = (PlayerEntity) damageFrom;
                            healPlayer.getFoodData().eat(random.nextInt(2) + random.nextInt(healBy + 1) + 1, 0.1F * healBy + random.nextFloat() * 0.15F);
                        }
                        if (healBy > 0 && this.entity != null && this.entity instanceof LivingEntity) {
                            ((LivingEntity)this.entity).heal(random.nextInt(2) + random.nextInt(healBy + 1) + (random.nextFloat() * 1.5F));
                        }
                    }
                    this.discard();
                }
            }
            double d1 = entity.getZ() - this.getZ();
            double d3 = entity.getEyeY() - this.getEyeY();
            double d2 = entity.getX() - this.getX();
            float f = MathHelper.sqrt((float) (d2 * d2 + d1 * d1));
            this.yRot = (-((float) MathHelper.atan2(d2, d1)) * (180F / (float) Math.PI));
            this.xRot = (-(float) (MathHelper.atan2(d3, f) * (double) (180F / (float) Math.PI)));
        } else if (tickCount > 3) {
            this.noPhysics = false;
            this.discard();
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9F));

        if (this.level().isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.yRot = (MathHelper.wrapDegrees((float) this.lyr));
                this.xRot = (this.xRot + (float) (this.lxr - (double) this.xRot) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
        }
    }

    public ItemStack getItemStack() {
        return this.entityData.get(ITEMSTACK);
    }

    public void setItemStack(ItemStack item) {
        this.entityData.set(ITEMSTACK, item);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return new SSpawnObjectPacket(this);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TARGET_ID, -1);
        this.entityData.define(PLAYER_ID, -1);
        this.entityData.define(STAB, 0F);
        this.entityData.define(ITEMSTACK, new ItemStack(Items.IRON_SWORD));
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT tag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tag) {

    }

    private int getTargetId() {
        return this.entityData.get(TARGET_ID);
    }

    public void setTargetId(int id) {
        this.entityData.set(TARGET_ID, id);
    }

    private int getPlayerId() {
        return this.entityData.get(PLAYER_ID);
    }

    public void setPlayerId(int id) {
        this.entityData.set(PLAYER_ID, id);
    }

    public float getStab() {
        return this.entityData.get(STAB);
    }

    public float getStab(float partialTicks) {
        return prevStab + (getStab() - prevStab) * partialTicks;
    }

    public void setStab(float stab) {
        this.entityData.set(STAB, stab);
    }

    private Entity getTargetEntity() {
        int id = getTargetId();
        return id == -1 ? null : level().getEntity(id);
    }

    private Entity getPlayer() {
        if (player != null) return player;

        int id = getPlayerId();
        return id == -1 ? null : level().getEntity(id);
    }
}