package net.minecraft.entity.passive.roadrunner;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.WardenEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.tool.AxeItem;
import net.minecraft.item.tool.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class RoadrunnerEntity extends Animal {

    public float oFlapSpeed;
    public float oFlap;
    public float wingRotDelta = 1.0F;
    public float wingRotation;
    public float destPos;
    public float prevAttackProgress;
    public float attackProgress;
    private boolean hasMeepSpeed = false;

    private int ticksSinceEaten;

    private LivingEntity mobWhoHurtMe = null;
    private int mobWhoHurtMeTime;


    public boolean canTakeItem(ItemStack p_213365_1_) {
        EquipmentSlotType equipmentslottype = Mob.getEquipmentSlotForItem(p_213365_1_);
        if (!this.getItemBySlot(equipmentslottype).isEmpty()) {
            return false;
        } else {
            return equipmentslottype == EquipmentSlotType.MAINHAND && super.canTakeItem(p_213365_1_);
        }
    }

    public boolean canHoldItem(ItemStack p_175448_1_) {
        Item item = p_175448_1_.getItem();
        ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
        return this.ticksSinceEaten > 0 && p_175448_1_.isEdible() && !itemstack.getItem().isEdible() && (item == Items.RABBIT_FOOT || item == Items.CHICKEN || item == Items.RABBIT || item == Items.SPIDER_EYE || item == Items.PUMPKIN_SEEDS || item == Items.BEETROOT_SEEDS);
    }

    private void spitOutItem(ItemStack p_213495_1_) {
        if (!p_213495_1_.isEmpty() && !this.level.isClientSide) {
            ItemEntity itementity = new ItemEntity(this.level, this.getX() + this.getLookAngle().x, this.getY() + 1.0D, this.getZ() + this.getLookAngle().z, p_213495_1_);
            itementity.setPickUpDelay(40);
            itementity.setThrower(this.getUUID());
            this.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
            this.level.addFreshEntity(itementity);
        }
    }

    private void dropItemStack(ItemStack p_213486_1_) {
        ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), p_213486_1_);
        this.level.addFreshEntity(itementity);
    }

    protected void pickUpItem(ItemEntity p_175445_1_) {
        ItemStack itemstack = p_175445_1_.getItem();
        if (this.canHoldItem(itemstack)) {
            int i = itemstack.getCount();
            if (i > 1) {
                this.dropItemStack(itemstack.split(i - 1));
            }

            this.spitOutItem(this.getItemBySlot(EquipmentSlotType.MAINHAND));
            this.onItemPickup(p_175445_1_);
            this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack.split(1));
            this.handDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
            this.take(p_175445_1_, itemstack.getCount());
            p_175445_1_.remove();
            this.ticksSinceEaten = 0;
        }

    }



    public boolean hurt(DamageSource s, float d) {
        boolean h = super.hurt(s, d);

        if (h && s.getEntity() != null && s.getEntity() instanceof LivingEntity) {
            this.mobWhoHurtMe = (LivingEntity) s.getEntity();
        }

        return h;
    }

    private static final DataParameter<Integer> ATTACK_TICK = EntityDataManager.defineId(RoadrunnerEntity.class, DataSerializers.INT);
    public int timeUntilNextFeather = this.random.nextInt(24000) + 24000;

    public RoadrunnerEntity(EntityType<RoadrunnerEntity> entity, World world) {
        super(entity, world);
        this.setCanPickUpLoot(true);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.45F).add(Attributes.FOLLOW_RANGE, 10D);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.1D));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(3, new FindFoodGoal(this, 1F, Ingredient.of(Items.SPIDER_EYE, Items.RABBIT_FOOT, Items.BEETROOT_SEEDS, Items.PUMPKIN_SEEDS, Items.RABBIT, Items.CHICKEN)));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.of(Items.SPIDER_EYE, Items.RABBIT_FOOT, Items.BEETROOT_SEEDS, Items.PUMPKIN_SEEDS), false));
        this.goalSelector.addGoal(5, new AnimalAIWanderRanged(this, 50, 1.0D, 25, 7));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<LivingEntity>(this, LivingEntity.class, 24F, 1.15D, 1.15D, entity -> entity instanceof WardenEntity));
        this.goalSelector.addGoal(5, new EntityAINearestTarget3D<LivingEntity>(this, LivingEntity.class, 65, true, true, entity -> {
            boolean flag = entity instanceof SpiderEntity || entity instanceof SilverfishEntity || entity instanceof EndermiteEntity;

            List<RoadrunnerEntity> allies = level.getLoadedEntitiesOfClass(RoadrunnerEntity.class,
                    this.getBoundingBox().inflate(20, 8, 20),
                    ally -> ally != this && (ally.getTarget() == entity || ally.getTarget() == null));
            return flag && (!allies.isEmpty() || random.nextFloat() < 0.15F);
        }));
        this.goalSelector.addGoal(5, new EntityAINearestTarget3D<LivingEntity>(this, LivingEntity.class, 90, true, true,
                entity -> entity instanceof RabbitEntity || entity instanceof ChickenEntity));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<LivingEntity>(this, LivingEntity.class, 7.5F, 1.1D, 1.1D, entity -> {
            if (entity == null || !entity.isAlive()) return false;

            LookController lk = this.getLookControl();

            Vector3d lc = new Vector3d(lk.getWantedX(), lk.getWantedY(), lk.getWantedZ());

            List<LivingEntity> l = this.level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(new BlockPos(lc.x,lc.y,lc.z)).inflate(0.5D));
            if (entity instanceof FoxEntity || entity instanceof WolfEntity || entity instanceof CreeperEntity) return true;

            if (entity.isShiftKeyDown() && !l.contains(entity) && random.nextFloat() < 0.90F || !this.canSee(entity)) return false;

            if (entity instanceof CatEntity) {
                if (entity.getPose() != Pose.CROUCHING) return true;
            }

            if (entity.getPose() == Pose.SPIN_ATTACK || entity.getPose() == Pose.SHOOTING || entity.getPose() == Pose.FALL_FLYING) return true;

            Predicate<LivingEntity> predicate = entity1 -> this.mobWhoHurtMe == entity1 && mobWhoHurtMe != null && entity1.isAlive() && mobWhoHurtMeTime <= 1200;
            Predicate<ItemStack> p1 = (item -> item.get() instanceof SwordItem || item.get() instanceof AxeItem || item.get() instanceof ShootableItem);
            Predicate<LivingEntity> p2 = ent -> (p1.test(ent.getMainHandItem()) || p1.test(ent.getOffhandItem()) || ent.hasEffect(Effects.DAMAGE_BOOST)) && EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(ent);

            return ((predicate.test(entity) || p2.test(entity)) || this.isMeep() && entity instanceof PlayerEntity) && (!(entity instanceof RoadrunnerEntity));
        }));

        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new CallForHelpIfAttackedByGoal(this, entity -> entity instanceof SpiderEntity || entity instanceof EndermiteEntity || entity instanceof SilverfishEntity, 32F));
        //this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, EntityRattlesnake.class, 55, true, true, null));
        //this.targetSelector.addGoal(2, (new HurtByTargetGoal(this, EntityRattlesnake.class)).setCallsForHelp());
    }

    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("FeatherTime")) {
            this.timeUntilNextFeather = compound.getInt("FeatherTime");
        }

    }



    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("FeatherTime", this.timeUntilNextFeather);
    }

    protected SoundEvent getAmbientSound() {
        return isMeep() || random.nextInt(2000) == 0 ? SoundEvents.ROADRUNNER_MEEP : SoundEvents.ROADRUNNER_IDLE;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ROADRUNNER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ROADRUNNER_HURT;
    }

    public boolean doHurtTarget(Entity entity) {
        this.entityData.set(ATTACK_TICK, 5);
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_TICK, 0);

    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == (DamageSource.CACTUS) || source.getMsgId().equals("anvil") || super.isInvulnerableTo(source);
    }

    private boolean canEat(ItemStack p_213464_1_) {
        Item item = p_213464_1_.Item();
        return p_213464_1_.getItem().isEdible() && this.getTarget() == null && this.onGround && !this.isSleeping() && (item == Items.RABBIT_FOOT || item == Items.CHICKEN || item == Items.RABBIT || item == Items.SPIDER_EYE || item == Items.PUMPKIN_SEEDS || item == Items.BEETROOT_SEEDS);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 45) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
            if (!itemstack.isEmpty()) {
                for(int i = 0; i < 8; ++i) {
                    Vector3d vector3d = (new Vector3d(((double)this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).xRot(-this.xRot * ((float)Math.PI / 180F)).yRot(-this.yRot * ((float)Math.PI / 180F));
                    this.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getX() + this.getLookAngle().x / 2.0D, this.getY(), this.getZ() + this.getLookAngle().z / 2.0D, vector3d.x, vector3d.y + 0.05D, vector3d.z);
                }
            }
        } else {
            super.handleEntityEvent(p_70103_1_);
        }

    }

    public void aiStep() {
        super.aiStep();

        if (!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
            ++this.ticksSinceEaten;
            ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
            if (this.canEat(itemstack)) {
                Item item = itemstack.Item();
                if (this.ticksSinceEaten > 600 || this.getHealth() < this.getMaxHealth()) {
                    ItemStack itemstack1 = itemstack.finishUsingItem(this.level, this);
                    if (!itemstack1.isEmpty()) {
                        this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack1);
                    }

                    if ((item == Items.RABBIT_FOOT || item == Items.CHICKEN || item == Items.RABBIT || item == Items.SPIDER_EYE || item == Items.PUMPKIN_SEEDS || item == Items.BEETROOT_SEEDS)) {
                        this.setInLoveTime(1200);
                    }

                    this.heal(2.0F);

                    this.ticksSinceEaten = 0;
                } else if (this.ticksSinceEaten > 560 && this.random.nextFloat() < 0.1F) {
                    this.playSound(this.getEatingSound(itemstack), 1.0F, 1.0F);
                    this.level.broadcastEntityEvent(this, (byte)45);
                }
            }
        }
        this.oFlap = this.wingRotation;
        this.prevAttackProgress = attackProgress;
        this.oFlapSpeed = this.destPos;
        this.destPos = (float) ((double) this.destPos + (double) (this.onGround ? -1 : 4) * 0.3D);
        this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);
        if (!this.onGround && this.wingRotDelta < 1.0F) {
            this.wingRotDelta = 1.0F;
        }
        if (!this.level().isClientSide && this.isAlive() && !this.isBaby() && --this.timeUntilNextFeather <= 0) {
            this.spawnAtLocation(isMeep() ? Items.MEEP_FEATHER : Items.ROADRUNNER_FEATHER);
            this.timeUntilNextFeather = this.random.nextInt(24000) + 24000;
        }
        this.wingRotDelta = (float) ((double) this.wingRotDelta * 0.9D);
        Vector3d vector3d = this.getDeltaMovement();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.8D, 1.0D));
        }
        this.wingRotation += this.wingRotDelta * 2.0F;

        if(this.entityData.get(ATTACK_TICK) > 0){
            if(this.entityData.get(ATTACK_TICK) == 2 && this.getTarget() != null && this.distanceTo(this.getTarget()) < 1.3D){
                int damage = 2;
                if (this.getTarget() instanceof SpiderEntity || getTarget() instanceof EndermiteEntity || getTarget() instanceof SilverfishEntity) {
                    damage = 4;
                }

                this.getTarget().hurt(DamageSource.mobAttack(this), damage);
            }
            this.entityData.set(ATTACK_TICK, this.entityData.get(ATTACK_TICK) - 1);
            if(attackProgress < 5F){
                attackProgress++;
            }
        }else{
            if(attackProgress > 0F){
                attackProgress--;
            }
        }
    }

    public void tick(){
        super.tick();

        if (!this.level.isClientSide) {
            if (mobWhoHurtMe != null && mobWhoHurtMeTime++ > 1200) {
                mobWhoHurtMeTime = 0;
                mobWhoHurtMe = null;
            }
        }

        if (isMeep()) {
            if (!hasMeepSpeed) {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(1F);
                hasMeepSpeed = true;
            }
        } else {
            if (hasMeepSpeed) {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.45F);
                hasMeepSpeed = false;
            }
        }

        if (this.level().isClientSide && this.isMeep() && this.onGround && !this.isInWaterOrBubble() && this.getDeltaMovement().lengthSqr() > 0.03D) {
            Vector3d vector3d = this.getViewVector(0.0F);
            final float yRotRad = (float) (this.yRot * MathHelper.DEG_TO_RAD);
            float f = MathHelper.cos(yRotRad) * 0.2F;
            float f1 = MathHelper.sin(yRotRad) * 0.2F;
            float f2 = 1.2F - this.random.nextFloat() * 0.7F;
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() - vector3d.x * (double) f2 + (double) f, this.getY() + random.nextFloat() * 0.2F, this.getZ() - vector3d.z * (double) f2 + (double) f1, 0.0D, 0.0D, 0.0D);
                this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX() - vector3d.x * (double) f2 - (double) f, this.getY() + random.nextFloat() * 0.2F, this.getZ() - vector3d.z * (double) f2 - (double) f1, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        if(!this.isMeep()){
            this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
        }
    }

    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.SPIDER_EYE || stack.getItem() == Items.RABBIT_FOOT;
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return EntityType.ROADRUNNER.create(p_241840_1_);
    }

    public static boolean canRoadrunnerSpawn(EntityType<? extends Animal> animal, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        boolean spawnBlock = worldIn.getBlockState(pos.below()).is(List.of(Blocks.SAND, Blocks.RED_SAND, Blocks.TERRACOTTA));
        return spawnBlock && worldIn.getRawBrightness(pos, 0) > 8 || reason == SpawnReason.SPAWNER ;
    }

    public boolean isMeep(){
        String s = TextFormatting.stripFormatting(this.getName().getString());
        LocalDate date = LocalDate.now();
        return (s != null && s.toLowerCase().contains("meep")) || date.getMonthValue() == 4 && date.getDayOfMonth() == 1;
    }

    public static boolean isAprilFools() {
        LocalDate today = LocalDate.now();
        return today.getMonthValue() == 4 && today.getDayOfMonth() == 1;
    }
}
