package net.minecraft.entity.monster;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ShamanEntity extends SpellcastingIllagerEntity {

    public ShamanEntity(EntityType<? extends ShamanEntity> type, World worldIn) {
        super(type, worldIn);
        setShieldTime(240);
        currentShield = null;
    }

    private WolfEntity wolfTarget;
    public List<WolfEntity> wolfEntities = new ArrayList<>();

    @Nullable
    private ElementalShieldType currentShield;
    private int shieldTime;



    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new CastingSpellGoal());
        this.goalSelector.addGoal(3, new AttackSpellGoal<>(this));
        this.goalSelector.addGoal(1, new ElementalShieldGoal(this, ElementalShieldType.FIRE));
        this.goalSelector.addGoal(2, new ConvertWolvesGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PlayerEntity.class, 2.0F, 0.6D, 1.0D));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());
        this.goalSelector.addGoal(1, new SummonAnimalGoal(this));
        this.goalSelector.addGoal(2, new KnockbackSummoningGoal(this));
        this.goalSelector.addGoal(2, new HealingRitualGoal(this));
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
        // Add more custom goals as needed
    }

    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("ShieldTime", shieldTime);
        if (currentShield != null) {
            compoundNBT.put("ElementalShield", currentShield.toNBT());
        }
    }

    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if(compoundNBT.contains("ShieldTime")) {
            shieldTime = compoundNBT.getInt("ShieldTime");
        }
        if (compoundNBT.contains("ElementalShield")) {
            currentShield = ElementalShieldType.fromNBT(compoundNBT.getCompound("ElementalShield"));
            System.out.println("Shield type: " + ElementalShieldType.fromNBT(compoundNBT.getCompound("ElementalShield")));
        }
    }

    public void setCurrentShield(@Nullable ElementalShieldType type) {
        this.currentShield = type;
    }

    @Nullable
    public ElementalShieldType getCurrentShield() {
        return currentShield;
    }

    public void tick() {
        super.tick();
        if (this.getCurrentShield() == ElementalShieldType.FIRE) {
            if (shieldTime-- < 0) {
                shieldTime = 240;
                this.setCurrentShield(null);
            }
            if(this.level.isClientSide)
                spawnFireShield();
        }
    }

    private void spawnFireShield() {
        double height = this.getBbHeight();
        double width = this.getBbWidth();
        double radius = width + 0.5; // Radius of the ring around the mob

        int particleCount = 20; // Number of particles in the ring
        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double x = this.getX() + radius * Math.cos(angle);
            double y = this.getY() + height / 2; // Adjust as needed to position the particles vertically
            double z = this.getZ() + radius * Math.sin(angle);

            this.level.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
        }
    }

    public boolean hurt(DamageSource source, float damage) {
        if (this.getCurrentShield() != null) {
            if(source.isMagic() || source.isBypassMagic() || source.isBypassArmor() || source.isBypassInvul() || source.isProjectile()) {
                damage = source.isProjectile() ? damage / this.random.nextFloat() : 0.0F;

                return super.hurt(source, damage);
            }
            if (source.getEntity() != null) {

                damage = this.getCurrentShield() != null ? damage / this.getCurrentShield().getProtection() : damage;

                switch (this.getCurrentShield()) {
                    case FIRE:
                        source.getEntity().setRemainingFireTicks(source.getEntity() instanceof PlayerEntity && ((PlayerEntity) source.getEntity()).isCreative() ? 0 : 80);
                        source.getEntity().hurt(DamageSource.mobAttack(this), 2.0F);
                        break;
                    case ICE:
                        if (source.getEntity() instanceof Mob) {
                            ((Mob) source.getEntity()).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 100, 1));
                        }
                        source.getEntity().hurt(DamageSource.mobAttack(this), 3.5F);
                }
            }
        }
        return super.hurt(source, damage);
    }

    class CastingSpellGoal extends SpellcastingIllagerEntity.CastingASpellGoal {
        private CastingSpellGoal() {
        }

        public void tick() {
            if (ShamanEntity.this.getTarget() != null) {
                ShamanEntity.this.getLookControl().setLookAt(ShamanEntity.this.getTarget(), (float)ShamanEntity.this.getMaxHeadYRot(), (float)ShamanEntity.this.getMaxHeadXRot());
            }
        }
    }

    public void setShieldTime(int value) {
        this.shieldTime = value;
    }

    public int getShieldTime() {
        return shieldTime;
    }


    @Nullable
    public List<WolfEntity> getWolfEntities() {
        return this.wolfEntities;
    }

    @Nullable
    public WolfEntity getWolfTarget(int index) {
        // Check if the index is within the bounds of the array
        if (index >= 0 && index < wolfEntities.size()) {
            return wolfEntities.get(index);
        } else {
            // If the index is out of bounds, return null
            return null;
        }
    }

    public void setWolfTarget(@Nullable WolfEntity wolf, int index) {
        if (index >= 0 && index < wolfEntities.size()) {
            this.wolfEntities.set(index, wolf);
        } else {
            System.out.println("Invalid index. Please provide an index between 0 and " + (wolfEntities.size() - 1));
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.52D).add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MAX_HEALTH, 30.0D);
    }

    @Override
    public void applyRaidBuffs(int p_213660_1_, boolean p_213660_2_) {

    }

    public boolean isAlliedTo(Entity entity) {
        if (entity == null) {
            return false;
        } else if (entity == this) {
            return true;
        } else if (super.isAlliedTo(entity)) {
            return true;
        } else if (entity instanceof VexEntity) {
            return this.isAlliedTo(((VexEntity)entity).getOwner());
        } else if (entity instanceof LivingEntity && ((LivingEntity)entity).getMobType() == CreatureAttribute.ILLAGER) {
            return this.getTeam() == null && entity.getTeam() == null;
        } else {
            return false;
        }
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    static class KnockbackSummoningGoal extends SpellcastingIllagerEntity.UseSpellGoal {
        private final ShamanEntity shaman;

        public KnockbackSummoningGoal(ShamanEntity shaman) {
            shaman.super();
            this.shaman = shaman;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.shaman.getTarget() != null && this.shaman.distanceTo(this.shaman.getTarget()) < 12;
        }

        @Override
        public void start() {
            super.start();

        }

        @Override
        protected void performSpellCasting() {
            knockbackEntities();
        }

        public void knockbackEntities() {
            List<LivingEntity> entities = this.shaman.level.getEntitiesOfClass(LivingEntity.class, this.shaman.getBoundingBox().inflate(12D, 6D, 12D));

            for (LivingEntity entity : entities) {
                boolean notIllagerOrWolf = !(entity instanceof AbstractIllagerEntity) && !(entity instanceof WolfEntity);
                boolean isVillagerGolemOrPlayer = entity instanceof AbstractVillagerEntity || entity instanceof IronGolemEntity || entity instanceof PlayerEntity;

                Vector3d entityPosition = entity.position().add(0, 1.2, 0);
                if (this.shaman.level.canSeeSky(entityPosition) && notIllagerOrWolf && isVillagerGolemOrPlayer) {
                    double xRatio = this.shaman.getX() - entity.getX();
                    double zRatio = this.shaman.getZ() - entity.getZ();
                    double distance = this.shaman.distanceTo(entity);
                    if (entity instanceof PlayerEntity) {
                        knockbackPlayer((PlayerEntity) entity, 1.2F, xRatio, zRatio);
                    } else {
                        entity.knockbackEntity(entity, 1.0F, xRatio, zRatio);
                    }
                    System.out.println("Entity: " + entity.getName().getString());
                    System.out.println("Entity knockback: " + new Vector3d(xRatio, 1.0F, zRatio).toString());


                }
            }
        }

        public void knockbackPlayer(PlayerEntity player, float strength, double xRatio, double zRatio) {
            if (strength > 0.0F) {
                player.hasImpulse = true;
                Vector3d currentMotion = player.getDeltaMovement();
                Vector3d knockbackVector = (new Vector3d(xRatio, 0.0D, zRatio)).normalize().scale((double) strength);

                player.setDeltaMovement(
                        currentMotion.x / 2.0D - knockbackVector.x,
                        0.3 + (Math.random() * (0.6 - 0.3)), // Adjusted to ensure upward knockback
                        currentMotion.z / 2.0D - knockbackVector.z
                );
                player.hurtMarked = true;

                System.out.println("Knockback vector: " + new Vector3d(xRatio, 0.0D, zRatio).normalize().scale((double) strength));
            }
        }

        @Override
        protected int getCastWarmupTime() {
            return 25;
        }

        @Override
        protected int getCastingTime() {
            return 12;
        }

        @Override
        protected int getCastingInterval() {
            return 300;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ILLUSIONER_PREPARE_BLINDNESS;
        }

        @Override
        protected SpellcastingIllagerEntity.SpellType getSpell() {
            return SpellType.BLINDNESS;
        }


    }

    // Example of a custom goal class for summoning animals
    static class SummonAnimalGoal extends SpellcastingIllagerEntity.UseSpellGoal {
        private final ShamanEntity shaman;
        private final Map<UUID, WolfEntity> wolves = new ConcurrentHashMap<>();
        private int wolvesLifetime;

        public SummonAnimalGoal(ShamanEntity shaman) {
            shaman.super();
            this.shaman = shaman;
            this.wolvesLifetime = 0;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.shaman.getTarget() != null;
        }

        @Override
        public void start() {
            super.start();
            this.wolvesLifetime = 1200; // Set lifetime of wolves to 1200 ticks
        }

        @Override
        protected void performSpellCasting() {
            summonAnimals();
        }

        private void summonAnimals() {
            World world = this.shaman.level;
            // Summoning logic, e.g., spawn wolves around the Shaman
            for (int i = 0; i < 3; i++) {
                WolfEntity wolf = EntityType.WOLF.create((ServerWorld) world);
                if (wolf != null) {
                    wolf.setPos(shaman.getX(), shaman.getY() + 1, shaman.getZ());
                   //wolf.setOwnerUUID(this.shaman.getUUID());

                    if (this.shaman.getTarget() != null) {
                        wolf.setTarget(this.shaman.getTarget());
                        wolf.setRemainingPersistentAngerTime(1200);
                        wolf.setPersistentAngerTarget(this.shaman.getTarget().getUUID());
                        wolf.setShaman(this.shaman);
                        wolf.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5D);
                       // wolf.setTame(true);
                        wolf.setFromShaman(true, 1200);
                        world.addFreshEntity(wolf);
                        //wolf.setOrderedToSit(false);
                       // wolf.setInSittingPose(false);
                    }
                }
            }
        }

        @Override
        public void tick() {
            super.tick();
            if (this.wolvesLifetime-- <= 0) {
                // Remove all wolves after their lifetime expires
                for (WolfEntity wolf : wolves.values()) {
                    wolf.remove();
                }
                wolves.clear();
            }
        }

        @Override
        protected int getCastWarmupTime() {
            return 30;
        }

        @Override
        protected int getCastingTime() {
            return 40;
        }

        @Override
        protected int getCastingInterval() {
            return 12 * 20;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON; // Example sound
        }

        @Override
        protected SpellcastingIllagerEntity.SpellType getSpell() {
            return SpellType.WOLVES; // Custom spell type
        }
    }



    static class HealingRitualGoal extends SpellcastingIllagerEntity.UseSpellGoal {
        private final ShamanEntity shaman;

        public HealingRitualGoal(ShamanEntity shaman) {
            shaman.super();
            this.shaman = shaman;
        }

        @Override
        public boolean canUse() {
            List<AbstractIllagerEntity> illagers = this.shaman.level.getEntitiesOfClass(AbstractIllagerEntity.class, this.shaman.getBoundingBox().inflate(6D, 3D, 6D), x -> x != this.shaman);
            if (!illagers.isEmpty()) {
                for (AbstractIllagerEntity illager : illagers) {
                    return illager.getHealth() < illager.getMaxHealth() && super.canUse() && this.shaman.random.nextFloat() < 0.4F;
                }
            }

            return super.canUse() && this.shaman.getHealth() < this.shaman.getMaxHealth();
        }

        @Override
        public void start() {
            super.start();
        }

        @Override
        protected void performSpellCasting() {
            performHealing();
        }

        private void performHealing() {
            this.shaman.heal(4.0F);

            double maxDistance = 6.0D; // Maximum effective healing distance
            double maxHealing = 6.0F;  // Maximum healing amount

            List<AbstractIllagerEntity> illagers = this.shaman.level.getEntitiesOfClass(AbstractIllagerEntity.class, this.shaman.getBoundingBox().inflate(maxDistance, 3D, maxDistance), x -> x != this.shaman);

            if (!illagers.isEmpty()) {
                for (AbstractIllagerEntity illager : illagers) {
                    if (illager.getHealth() < illager.getMaxHealth()) {
                        double distance = this.shaman.distanceTo(illager);
                        double healingAmount = calculateHealingAmount(distance, maxDistance, maxHealing);
                        illager.heal((float) healingAmount);
                    }
                }
            }
        }

        private double calculateHealingAmount(double distance, double maxDistance, double maxHealing) {

            if (distance > maxDistance) {
                return 0;
            }
            return maxHealing * (1 - (distance / maxDistance));
        }

        @Override
        protected int getCastWarmupTime() {
            return 35;
        }

        @Override
        protected int getCastingTime() {
            return 40;
        }

        @Override
        protected int getCastingInterval() {
            return 400;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected SpellcastingIllagerEntity.SpellType getSpell() {
            return SpellType.HEAL;
        }
    }

    static class ElementalShieldGoal extends SpellcastingIllagerEntity.UseSpellGoal {
        private final ShamanEntity shaman;
        private final ElementalShieldType element;
        public ElementalShieldGoal(ShamanEntity shaman, ElementalShieldType element) {
            shaman.super();
            this.shaman = shaman;
            this.element = element;
        }

        public ElementalShieldType getShield() {
            return element;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.shaman.getTarget() != null && this.shaman.distanceTo(this.shaman.getTarget()) < 7;
        }

        @Override
        public void start() {
            super.start();
            this.shaman.shieldTime = 240;
        }

        @Override
        protected void performSpellCasting() {
            useShield();
        }

        public void useShield() {
            this.shaman.setCurrentShield(this.element);
        }


        public float getShieldProtection() {
            return element.getProtection();
        }

        @Override
        protected int getCastWarmupTime() {
            return 50;
        }

        @Override
        protected int getCastingTime() {
            return 20;
        }

        @Override
        protected int getCastingInterval() {
            return 700;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected SpellcastingIllagerEntity.SpellType getSpell() {
            return element == ElementalShieldType.FIRE ? SpellType.SHIELD_FIRE : SpellType.NONE;
        }


    }

    public static enum ElementalShieldType {
        FIRE(5.0F, "fire"),
        ICE(8.0F, "ice");

        final float protection;
        final String name;

        ElementalShieldType(float protection, String name) {
            this.protection = protection;
            this.name = name;
        }


        public float getProtection() {
            return this.protection;
        }

        public String getName() {
            return this.name;
        }

        public CompoundNBT toNBT() {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putFloat("protection", this.protection);
            compoundNBT.putString("name", this.name);
            return compoundNBT;
        }
        @Nullable
        public static ElementalShieldType fromNBT(CompoundNBT compoundNBT) {
            String name = compoundNBT.getString("name");
            for (ElementalShieldType type : ElementalShieldType.values()) {
                if (type.getName().equals(name)) {
                    return type;
                }
            }
            return null; // or throw an exception if the name doesn't match any type
        }
    }

}