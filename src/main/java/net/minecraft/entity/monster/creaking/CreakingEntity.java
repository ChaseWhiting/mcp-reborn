package net.minecraft.entity.monster.creaking;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;

import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.creaking.block.CreakingHeartBlock;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CreakingHeartItem;
import net.minecraft.item.tool.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;

/**
 * @see net.minecraft.entity.monster.creaking.CreakingModel
 * @see net.minecraft.client.animation.definitions.CreakingAnimation
 *
 * The Creaking, which is a tree-like mob exclusive to the Pale Garden biome or can spawn if there are nearby Pale Oak trees or blocks.
 * It can apply the rooted effect, which completely stops any regeneration except from things like the regeneration effect or healing,
 * and also exhausts hunger faster.
 *
 *
 *
 *
 * The Creaking mainly spawns inside of the Pale Garden, near Pale Oak trees, which cover the entirely of the forest, covering any visible skylight most of the time.
 *
 * ^ Since mobs don't spawn inside of the Pale Garden other than the Creaking, chopping down in the trees is a good way to make a base
 * if players don't want mobs, however the Garden also has a very dense fog, especially at night. This can be removed by wearing a carved Pale Pumpkin,
 * which can be found as pale pumpkins spawning around the biome, however wearing pumpkins do give a pumpkin overlay which can cover some of the screen.
 *
 * ^ Players must also be wary of Creakings spawning at night while chopping down the trees, so this should mostly be performed during the day, as removing all trees
 * will no longer allow anything to spawn inside of the biome.
 *
 * Players still must be cautious about Creakings if they don't encounter a Pale Garden. Pale Gardens normally replace Dark Forests, however they are rare, and there
 * are chances for Pale Oak trees to replace regular dark oak trees, which permit Creakings spawning near them.
 *
 * The Creaking, is similar to the Weeping Angels from Doctor Who. It will only pursue its target if not being looked at, which means that,
 * if a player looks away from the Creaking, then looks back, it would've moved closer, and it will not do anything as long as a player or villager
 * has the Creaking within their FOV.
 *
 * A bit of lore; All illager mobs are very, very scared of the Creaking, and will instantly run away when near one, avoiding to even fight.
 *
 * A group of Creakings is called a 'crunch'.
 *
 * The Creaking moves at a very fast speed toward players and villagers, and it can catch up to a stopped player in just a few seconds.
 * Its speed is about the same of a sprinting player who isn't jumping, but it WILL be able to catch up to a just sprinting player.
 *
 * Because of the above statement, it is very very difficult to escape the Creaking if you have been hit by it, as the Rooted effect will
 * drain your hunger even faster than normal, which will cause you to run out of sprint.
 *
 *
 * The Creaking has about a 50% chance to drop its Creaking heart, which will not function if it hasn't killed anything. However,
 * if the Creaking has killed any villagers, their souls are stored in the heart. If any villagers were killed by the Creaking,
 * and the Creaking Heart item is used, their souls will be released, bringing the villagers back into the world, which also retain
 * their states, trades, etc. The Creaking could be used to transport villagers, but it comes at the cost of a chance of
 * losing the villagers.
 *
 * @see net.minecraft.item.CreakingHeartItem
 *
 * The Creaking Heart can also be used, if the player is holding it in their main hand or off hand, and kills a villager, there is a 75% chance that
 * the villager's soul will be put into the heart, which can be used the same as said above.
 *
 * @since  Oct 5 2024
 * @author Chase W
 */
@SuppressWarnings("all")
public class CreakingEntity extends Monster implements IDropsCustomLoot {

    private static int ROOTED_EFFECT_MIN_SECONDS = 120;
    private static int ROOTED_EFFECT_MAX_SECONDS = 480;


    // Entity Data Keys
    private static final UUID TREE_ARMOR_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
    private static final AttributeModifier COVERED_ARMOR_MODIFIER = new AttributeModifier(TREE_ARMOR_MODIFIER_UUID, "Covered armor bonus", 60.0D, AttributeModifier.Operation.ADDITION);
    private static final DataParameter<Boolean> CAN_MOVE = EntityDataManager.defineId(CreakingEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_TARGET = EntityDataManager.defineId(CreakingEntity.class, DataSerializers.BOOLEAN);

    private static final DataParameter<Boolean> IS_TEARING_DOWN = EntityDataManager.defineId(CreakingEntity.class, DataSerializers.BOOLEAN);

    private static final DataParameter<Boolean> IS_ACTIVE = EntityDataManager.defineId(CreakingEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> SWAYING_TIME = EntityDataManager.defineId(CreakingEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> ATTACKING_TICKS = EntityDataManager.defineId(CreakingEntity.class, DataSerializers.INT);
    private static final RangedInteger randomAttackTime = TickRangeConverter.rangeOfTicks(17, 40);

    // Attack properties

    public PlayerEntity creakingTarget = null;
    public LivingEntity optionalTarget = null;
    private boolean canAlwaysMove = false;
    public static int MAX_INVUL_TIME = 8;
    private int nextFlickerTime;
    public boolean eyesGlowing = false;
    public ItemStack creakingHeart = new ItemStack(Items.CREAKING_HEART_ITEM, 1);

    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState invulnerabilityAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();


    public int attackTime = 35;
    @OnlyIn(Dist.CLIENT)
    public void checkEyeBlink() {
        if (this.deathTime > this.nextFlickerTime) {
            this.nextFlickerTime = this.deathTime + nextIntBetweenInclusive(this.eyesGlowing() ? 2 : this.deathTime / 4, this.eyesGlowing() ? 8 : this.deathTime / 2);
            this.setEyesGlowing(!this.eyesGlowing());
        }
    }

    @Override
    public boolean isPersistenceRequired() {
        return super.isPersistenceRequired() || !CreakingHeartItem.hasRoom(this.creakingHeart);
    }

    @OnlyIn(Dist.CLIENT)
    public void setEyesGlowing(boolean b) {
        this.eyesGlowing = b;
    }
    @OnlyIn(Dist.CLIENT)
    public boolean eyesGlowing() { //control if eyes have emmissive glowing
        return this.eyesGlowing;
    }

    public int nextIntBetweenInclusive(int $$0, int $$1) {
        return this.random.nextInt($$1 - $$0 + 1) + $$0;
    }

    public void setSwayingTime(int time) {
        this.entityData.set(SWAYING_TIME, time);
    }

    public void setTearingDown() {
        this.entityData.set(IS_TEARING_DOWN, true);
    }

    public boolean isTearingDown() {
        return this.entityData.get(IS_TEARING_DOWN);
    }

    @Override
    protected void tickDeath() {
        if (this.isTearingDown()) {
            ++this.deathTime;
            if (this.deathTime > 45 && !this.level().isClientSide() && !this.removed) {
                this.tearDown();
            }
        } else {
            super.tickDeath();
        }
    }

    public void tearDown() {
        if (level instanceof ServerWorld) {
            ServerWorld world = (ServerWorld)level;
            AxisAlignedBB $$1 = this.getBoundingBox();
            Vector3d $$2 = $$1.getCenter();
            double $$3 = $$1.getXsize() * 0.3;
            double $$4 = $$1.getYsize() * 0.3;
            double $$5 = $$1.getZsize() * 0.3;
            world.sendParticles(new BlockParticleData(ParticleTypes.BLOCK, Blocks.PALE_OAK_WOOD.defaultBlockState()), $$2.x, $$2.y, $$2.z, 100, $$3, $$4, $$5, 0.0);
            world.sendParticles(new BlockParticleData(ParticleTypes.BLOCK, (BlockState)Blocks.CREAKING_HEART.defaultBlockState().setValue(CreakingHeartBlock.CREAKING, CreakingHeartBlock.CreakingHeartState.ACTIVE)), $$2.x, $$2.y, $$2.z, 10, $$3, $$4, $$5, 0.0);
        }
        if (random.nextBoolean()) {
            this.spawnAtLocation(this.creakingHeart, 0.6f);
        }

        this.makeSound(this.getDeathSound());
        this.remove();
    }

    public void killed(ServerWorld world, LivingEntity entityKilled) {
        super.killed(world, entityKilled);

        if (entityKilled instanceof VillagerEntity) {
            if (this.level.isServerSide) {
                CreakingHeartItem.addEntityToCreakingHeart(this.creakingHeart, entityKilled);
            }
        }
    }

    public void die(DamageSource source) {
        this.creakingDeathEffects(source);
        if (!this.removed && !this.dead) { //same as super.die(), just removed the death sound, as this will be handled when it tears down.
            Entity entity = source.getEntity();
            LivingEntity livingentity = this.getKillCredit();
            if (this.deathScore >= 0 && livingentity != null) {
                livingentity.awardKillScore(this, this.deathScore, source);
            }

            if (this.isSleeping()) {
                this.stopSleeping();
            }

            this.dead = true;
            this.getCombatTracker().recheckStatus();
            if (this.level instanceof ServerWorld) {
                if (entity != null) {
                    entity.killed((ServerWorld) this.level, this);
                }

                this.dropAllDeathLoot(source);
                this.createWitherRose(livingentity);
            }
            this.setPose(Pose.DYING);
        }
        this.setTearingDown();
    }

    @Override
    public int getHeadRotSpeed() {
        return 30;
    }

    public void creakingDeathEffects(@Nullable DamageSource $$0) {
        Entity entity;
        if ($$0 != null && (entity = $$0.getEntity()) instanceof LivingEntity) {
            LivingEntity $$1 = (LivingEntity)entity;
            $$1.awardKillScore(this,1,  $$0);
        }
        this.makeSound(SoundEvents.CREAKING_TWITCH);
    }

    public int getSwayingTime() {
        return this.entityData.get(SWAYING_TIME);
    }

    public void setAttackTicks(int time) {
        this.entityData.set(ATTACKING_TICKS, time);
    }

    public int getAttackTicks() {
        return this.entityData.get(ATTACKING_TICKS);
    }

    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("CanAlwaysMove", this.canAlwaysMove);
        nbt.putInt("AttackTime", this.attackTime);
        nbt.put("CreakingHeart", this.creakingHeart.save(new CompoundNBT()));
        nbt.putBoolean("GlowingEyes", this.eyesGlowing());
    }

    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("CanAlwaysMove")) {
            this.canAlwaysMove = nbt.getBoolean("CanAlwaysMove");
        }
        if (nbt.contains("AttackTime")) {
            this.attackTime = nbt.getInt("AttackTime");
        }
        this.setEyesGlowing(nbt.getBoolean("GlowingEyes"));

        this.creakingHeart = ItemStack.of(nbt.getCompound("CreakingHeart"));
    }

    public static boolean checkCreakingRules(EntityType<CreakingEntity> type, IServerWorld world, SpawnReason reason, BlockPos pos, Random random) {
        Optional<RegistryKey<Biome>> currentBiome = world.getBiomeName(pos);

        if (currentBiome.isPresent() && currentBiome.get() == Biomes.PALE_GARDEN) {
            return checkMonsterSpawnRules(type, world, reason, pos, random)
                    && (reason == SpawnReason.SPAWNER || (pos.getY() > 84))
                    && checkNearbyBlocks(world, pos);
        }

        return checkMonsterSpawnRules(type, world, reason, pos, random)
                && checkNearbyBlocks(world, pos);
    }

    /**
     *
     * Other mobs don't spawn near pale oaks,
     * only mobs that are allowed to spawn are:
     * {@link net.minecraft.entity.monster.SlimeEntity},
     * {@link net.minecraft.entity.passive.BatEntity}, and
     * {@link CreakingEntity}
     */
    public static boolean checkNearbyBlocks(IServerWorld world, BlockPos pos) {
        int paleHangingMossCount = 0;
        int paleMossBlockCount = 0;
        int paleOakLogCount = 0;
        int paleOakLeavesCount = 0;

        int radius = 10;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos currentPos = pos.offset(x, y, z);
                    Block block = world.getBlockState(currentPos).getBlock();

                    if (block == Blocks.PALE_HANGING_MOSS) {
                        paleHangingMossCount++;
                    } else if (block == Blocks.PALE_MOSS_BLOCK) {
                        paleMossBlockCount++;
                    } else if (block == Blocks.PALE_OAK_LOG) {
                        paleOakLogCount++;
                    } else if (block == Blocks.PALE_OAK_LEAVES) {
                        paleOakLeavesCount++;
                    }
                }
            }
        }

        return paleHangingMossCount >= 10
                && paleMossBlockCount >= 8
                && paleOakLogCount >= 15
                && paleOakLeavesCount >= 12;
    }


    public CreakingEntity(EntityType<? extends Monster> type, World world) {
        super(type, world);
        this.xpReward = 5;
        this.maxUpStep = 1.2F;
        this.setPathfindingMalus(PathNodeType.WATER, 30.0F);
        this.setPathfindingMalus(PathNodeType.WATER_BORDER, 30.0F);
        this.setPathfindingMalus(PathNodeType.LAVA, 0.0F);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(PathNodeType.DAMAGE_CACTUS, 0.0F);
        this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(PathNodeType.DAMAGE_OTHER, 0.0F);
        this.setPathfindingMalus(PathNodeType.DANGER_OTHER, 0.0F);
        this.setPathfindingMalus(PathNodeType.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(PathNodeType.FENCE, 0.0F);
        this.setPathfindingMalus(PathNodeType.LEAVES, 0.0F);
        this.setPathfindingMalus(PathNodeType.STICKY_HONEY, 0.0F);
        this.setPathfindingMalus(PathNodeType.BREACH, 0.0F);
        // Custom entity movement, jump, and look control logic
        this.lookControl = new CreakingLookController(this);
        this.moveControl = new CreakingMoveController(this);
        this.bodyRotationControl = new CreakingBodyController(this);
        this.jumpControl = new CreakingJumpController(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setupAnimationStates();

            if (this.isTearingDown()) {
                this.checkEyeBlink();
            } else {
                this.setEyesGlowing(this.isActive());
            }

        }
    }

    private void setupAnimationStates() {
        this.attackAnimationState.animateWhen(this.getAttackTicks() > 0, this.tickCount);
        this.invulnerabilityAnimationState.animateWhen(this.getSwayingTime()> 0, this.tickCount);
        this.deathAnimationState.animateWhen(this.isTearingDown(), this.tickCount);

    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    // Define entity data
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CAN_MOVE, true);
        this.entityData.define(HAS_TARGET, false);
        this.entityData.define(IS_ACTIVE, false);
        this.entityData.define(IS_TEARING_DOWN, false);
        this.entityData.define(SWAYING_TIME, 0);
        this.entityData.define(ATTACKING_TICKS, 0);
    }

    public int getMaxFallDistance() { // will jump from high distances to reach its target, even if it gets hurt or dies
        return 60;
    }

    public boolean hasTarget() {
        return this.entityData.get(HAS_TARGET) ;
    }

    static class LookAtEntitiesGoal extends LookAtGoal {
        public final CreakingEntity mob;
        public LookAtEntitiesGoal(CreakingEntity mob, Class<? extends LivingEntity> p_i1631_2_, float p_i1631_3_) {
            super(mob, p_i1631_2_, p_i1631_3_);

            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && mob.canMove();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && mob.canMove();
        }
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new LookAtEntitiesGoal(this, LivingEntity.class, 14f));
        this.goalSelector.addGoal(1, new CreakingAttackGoal(this, 1.3D));
        this.goalSelector.addGoal(2, new CreakingUniversalAttackGoal(this, 1.13F, entity -> entity instanceof VillagerEntity, false, true));
        this.goalSelector.addGoal(2, new CreakingUniversalAttackGoal(this, 1.2F, entity -> entity instanceof AbstractRaiderEntity, true, false));
        this.goalSelector.addGoal(3, new CreakingUniversalAttackGoal(this, 1.2F, entity -> entity instanceof VexEntity, true, false));
        this.goalSelector.addGoal(2, new CreakingUniversalAttackGoal(this, 1.1f, entity -> {
            boolean flag = !(entity instanceof PlayerEntity)
                    && !(entity instanceof VexEntity)
                    && !(entity instanceof VillagerEntity)
                    && !(entity instanceof AbstractRaiderEntity)
                    && !(entity instanceof CreakingEntity);

            if (entity == null)return false;
            if (entity == this)return false;
            Optional<RegistryKey<Biome>> biome = entity.level.getBiomeName(entity.blockPosition());
            if (biome.isPresent() && biome.get() == Biomes.PALE_GARDEN && this.distanceTo(entity) < 16 && flag) {
                return true;
            }

            return false;
        }, true, false));

        this.goalSelector.addGoal(3, new RandomWalkingGoal(this, 0.5D, 4, true, mob -> {
            if (mob instanceof CreakingEntity) {
                if (mob.as(CreakingEntity.class).canAlwaysMove) {
                    return true;
                }
                if (mob.as(CreakingEntity.class).creakingTarget != null) {
                    return !mob.canSee(mob.as(CreakingEntity.class).creakingTarget) && ((CreakingEntity)mob).canMove();
                }
                boolean flag = mob.as(CreakingEntity.class).creakingTarget == null && ((CreakingEntity)mob).canMove();

                if (!flag) {
                    ((CreakingEntity)mob).stopInPlace();
                }
                return flag;
            }
            return false;
        }));
    }

    protected float getWaterSlowDown() {
        return 1F;
    }

    // Attribute definition for the entity
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MAX_HEALTH, 45.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    // Check if the entity can move
    public boolean canMove() {
        if (this.canAlwaysMove) return true;

        return this.entityData.get(CAN_MOVE);
    }

    // Check if the entity is active
    public boolean isActive() {
        return this.entityData.get(IS_ACTIVE);
    }

    public void setIsActive(boolean isActive) {
        this.entityData.set(IS_ACTIVE, isActive);
    }

    // Custom AI step logic to control movement and behavior
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getAttackTicks() > 0) {
            this.setAttackTicks(this.getAttackTicks() - 1);
        }
        if (this.getSwayingTime() > 0) {
            this.setSwayingTime(this.getSwayingTime() - 1);
        }

        if (!this.level.isClientSide) {
            boolean canMove = this.canMove();
            boolean shouldMove = this.checkCanMove(null);
            if (this.getAttributes().hasModifier(Attributes.ARMOR, COVERED_ARMOR_MODIFIER.getId())) {
                this.getAttribute(Attributes.ARMOR).removeModifier(COVERED_ARMOR_MODIFIER);
            }
            if (!this.canAlwaysMove) {
                if (shouldMove != canMove) {
                    if (!shouldMove) {
                        this.getAttribute(Attributes.ARMOR).addPermanentModifier(COVERED_ARMOR_MODIFIER);
                        this.playSound(SoundEvents.CREAKING_FREEZE, 1.0F, 1.0F);
                    } else {
                        this.stopInPlace();
                        this.playSound(SoundEvents.CREAKING_UNFREEZE, 1.0F, 1.0F);
                    }
                }
            }
            this.entityData.set(CAN_MOVE, canAlwaysMove ? true : shouldMove);
        }
    }

    // Check if the entity should move
    private boolean checkCanMove(@Nullable Predicate<LivingEntity> additionalEntityPredicate) {
        if (this.canAlwaysMove) return true;

        if (additionalEntityPredicate == null) {
            additionalEntityPredicate = entity -> entity instanceof VillagerEntity;
        }

        additionalEntityPredicate = additionalEntityPredicate.and(entity -> entity instanceof VillagerEntity);
        // Define a list to hold all nearby entities (players + others that match the predicate)
        List<LivingEntity> nearbyEntities = new ArrayList<>();

        // Get all players within a 32.0D radius
        List<PlayerEntity> nearbyPlayers = this.level.getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(32.0D));
        nearbyEntities.addAll(nearbyPlayers);

        // If an additional predicate is provided, include other nearby entities that match it
        if (additionalEntityPredicate != null) {
            List<LivingEntity> otherNearbyEntities = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8.0D), additionalEntityPredicate);
            nearbyEntities.addAll(otherNearbyEntities);
        }

        // Track previous active state
        boolean wasActive = this.isActive();

        // If no entities (players or others matching the predicate) are nearby
        if (nearbyEntities.isEmpty()) {
            if (wasActive) {
                this.playSound(SoundEvents.CREAKING_DEACTIVATE, 1.0F, 1.0F);  // Deactivate sound effect
                this.setIsActive(false);  // Set entity to inactive
            }
            return true;
        }

        // Set up a predicate to filter based on visibility or criteria
        Predicate<LivingEntity> visibilityPredicate = this.isActive()
                ? LivingEntity.PLAYER_NOT_WEARING_DISGUISE_ITEM
                : entity -> true;  // If inactive, no filtering

        // Loop through each nearby entity
        for (LivingEntity entity : nearbyEntities) {
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = entity.asPlayer();
                if (player.isCreative() || player.isSpectator()) {
                    continue;  // Skip this player; do not restrict movement if they are creative or spectator
                }
            }
            // Use the modified canEntitySee method to check if the entity can see this CreakingEntity
            if (!this.canEntitySee(entity, 0.63, false, true, visibilityPredicate, this::getEyeY, this::getY, () -> (this.getEyeY() + this.getY()) / 2.0)) {
                continue;  // If the entity cannot see, continue to the next one
            }

            // If the entity is already active, prevent movement
            if (wasActive) {
                return false;
            }

            // Check if the entity is close enough (within 32 units distance)
            if (entity.distanceTo(this) < 32.0) {
                if (!wasActive) {
                    this.playSound(SoundEvents.CREAKING_ACTIVATE, 1.0F, 1.0F);  // Activation sound effect
                    this.setIsActive(true);  // Set entity to active
                }
                return false;
            }
        }

        return true;  // If no entities meet the criteria, return true (can move)
    }


    @Nullable
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData spawnData, @Nullable CompoundNBT data) {
        spawnData = super.finalizeSpawn(world, difficulty, reason, spawnData, data);
        this.attackTime = randomAttackTime.randomValue(this.random);
        return spawnData;
    }

    public List<WeightedItemStack> getDropItems() {
        List<WeightedItemStack> weightedItemStackList = new ArrayList<>();

        WeightedItemStack.Builder builder = new WeightedItemStack.Builder()
                .addWeightedItem(new ItemStack(Items.PALE_STICK), 15, 0, 2) // Common
                .addWeightedItem(new ItemStack(Items.PALE_OAK_SAPLING), 10, 0, 1) // Less common
                .addWeightedItem(new ItemStack(Items.PALE_OAK_LOG), 5, 0, 1) // Rare
                .addWeightedItem(new ItemStack(Items.PALE_OAK_PLANKS), 8, 0, 2) // Uncommon
                .addWeightedItem(new ItemStack(Items.PALE_OAK_SAPLING), 3, 0, 1) // Rare
                .setRolls(1, 2) // Ensures the mob can drop multiple items
                .withCondition(context -> context.hasParam(LootParameters.LAST_DAMAGE_PLAYER)) // Only drops if killed by player
                .setBonusRolls(new WeightedItemStack.RandomValueRange(0, 1)); // Chance for bonus drop

        weightedItemStackList.add(builder.build());

        return weightedItemStackList;
    }

    // Creaking-specific AI for targeting and attacking
    static class CreakingAttackGoal extends Goal {
        private final CreakingEntity mob;
        private final double speedModifier;
        private int attackCooldown = 20;

        public CreakingAttackGoal(CreakingEntity mob, double speedModifier) {
            this.mob = mob;
            this.speedModifier = mob.veryHardmode() || mob.level.getDifficulty() == Difficulty.HARD ? speedModifier + 0.1F : speedModifier;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            // First check if there is a valid player nearby
            this.mob.creakingTarget = mob.level.getNearestSurvivalPlayer(mob, 32.0D);

            if (this.mob.creakingTarget != null && !this.mob.canSee(mob.creakingTarget)) return false;

            if (this.mob.creakingTarget == null) {
                if (this.mob.hasTarget()) {
                    this.mob.entityData.set(HAS_TARGET, false);
                }
                return false;  // No valid target found
            } else {
                if (!this.mob.hasTarget()) {
                    this.mob.entityData.set(HAS_TARGET, true);
                }
            }

            // If the player is within range, and the mob can see the player, proceed with the attack
            if (this.mob.canSee(this.mob.creakingTarget)) {
                return true;
            }

            // If the mob cannot see the player, we should still check if it has a target from previous encounters
            return this.mob.creakingTarget != null;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.mob.creakingTarget == null) {
                if (this.mob.hasTarget()) {
                    this.mob.entityData.set(HAS_TARGET, false);
                }
                return false;  // No valid target found
            } else {
                if (!this.mob.hasTarget()) {
                    this.mob.entityData.set(HAS_TARGET, true);
                }
            }

            // Continue using the goal if the target is still nearby, even if the player is out of sight temporarily
            if (this.mob.distanceTo(this.mob.creakingTarget) < 88.0D && this.mob.creakingTarget != null && this.mob.creakingTarget.isAlive()) {
                return true;
            }

            // Lose the target if the player gets too far away
            return this.mob.creakingTarget != null;
        }

        @Override
        public void tick() {
            if (this.mob.creakingTarget != null) {
                super.tick();
                if (!EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(this.mob.creakingTarget)) {
                    this.mob.stopInPlace();
                    this.mob.creakingTarget = null;
                    this.stop();
                    return;
                }

                // Calculate the player's current position and view direction
                Vector3d playerPosition = this.mob.creakingTarget.position();
                Vector3d playerLookDirection = this.mob.creakingTarget.getLookAngle();  // Get player's view direction



                // Calculate a position behind the player (in the opposite direction of where they're looking)
                double offsetDistance = 0.6;  // How far behind the player we want to go
                Vector3d behindPlayerPosition = playerPosition.subtract(playerLookDirection.scale(offsetDistance));
                // Check if the player is looking at the mob
                if (!playerLookingAtMe(this.mob.creakingTarget, mob)) {
                    // Path towards the position behind the player if they're not looking at the mob
                    this.mob.getNavigation().moveTo(behindPlayerPosition.x, behindPlayerPosition.y, behindPlayerPosition.z, speedModifier);
                } else {
                    // Recalculate a new position at an angle behind the player
                    Vector3d strafeDirection = playerLookDirection.cross(Vector3d.UP).normalize().scale(0.5); // Move to the side slightly
                    behindPlayerPosition = playerPosition.subtract(playerLookDirection.scale(offsetDistance)).add(strafeDirection);
                    this.mob.getNavigation().moveTo(behindPlayerPosition.x, behindPlayerPosition.y, behindPlayerPosition.z, speedModifier);
                }

                // If close enough to the player, attack
                double extendedReach = mob.getBbWidth() * 2.0D + 0.4D; // Add 0.4 blocks to the reach
                if (mob.distanceToSqr(this.mob.creakingTarget) < extendedReach * extendedReach  && this.attackCooldown <= 0 && !playerLookingAtMe(this.mob.creakingTarget, mob)) {
                    mob.doHurtTarget(this.mob.creakingTarget);
                    resetAttackCooldown();
                    mob.playSound(SoundEvents.CREAKING_ATTACK, 1.0F, 1.0F);
                } else {
                    --this.attackCooldown;
                }
            } else {
                if (this.mob.creakingTarget == null) {
                    if (this.mob.hasTarget()) {
                        this.mob.entityData.set(HAS_TARGET, false);
                    }

                }
                // If no player, stop the mob in place
                this.mob.stopInPlace();
            }
        }

        private void resetAttackCooldown() {
            this.attackCooldown = mob.attackTime; // Reset cooldown to 20 ticks (matching MeleeAttackGoal)
        }


        private boolean playerLookingAtMe(PlayerEntity player, Mob mob) {
            boolean flag = !((CreakingEntity)mob).checkCanMove(null);
            if (flag) {
                ((CreakingEntity)mob).stopInPlace();
            }
            return flag;
        }
    }


    public void knockback(float $$0, double $$1, double $$2) {
    }

    public boolean canEntitySee(LivingEntity entity, double tolerance, boolean useDistance, boolean useVisualClip, Predicate<LivingEntity> visibilityPredicate, DoubleSupplier ... heightSuppliers) {
        if (!visibilityPredicate.test(entity)) {
            return false;
        }

        Vector3d viewDirection = entity.getViewVector(1.0f).normalize();

        for (DoubleSupplier heightSupplier : heightSuppliers) {
            Vector3d directionToTarget = new Vector3d(this.getX() - entity.getX(),
                    heightSupplier.getAsDouble() - entity.getEyeY(),
                    this.getZ() - entity.getZ());
            double distanceToTarget = directionToTarget.length();

            directionToTarget = directionToTarget.normalize();

            double dotProduct = viewDirection.dot(directionToTarget);

            double adjustedTolerance = useDistance ? distanceToTarget : 1.0;

            if (dotProduct <= 1.0 - tolerance / adjustedTolerance) {
                continue;
            }

            return entity.hasLineOfSight(this,
                    useVisualClip ? RayTraceContext.BlockMode.VISUAL : RayTraceContext.BlockMode.COLLIDER,
                    RayTraceContext.FluidMode.NONE,
                    heightSupplier);
        }

        return false;
    }





    public boolean doHurtTarget(Entity entity) {
        this.setAttackTicks(8);
        this.level.broadcastEntityEvent(this, (byte)4);
        if (super.doHurtTarget(entity)) {
            if (entity instanceof PlayerEntity && !entity.asPlayer().hasEffect(Effects.ROOTED)) {
                entity.asPlayer().addEffect(new EffectInstance(Effects.ROOTED, TickRangeConverter.rangeOfSeconds(ROOTED_EFFECT_MIN_SECONDS, ROOTED_EFFECT_MAX_SECONDS).randomValue(this.random), switch(this.level.getDifficulty()) {
                    case PEACEFUL, EASY -> 0;
                    case NORMAL -> 1;
                    case HARD -> 2;
                }, true, false));
            } // rooted effect completely prevents natural regeneration and decreases hunger faster even when not able to regenerate any health
            return true;
        }

        return false;
    }

    public void stopInPlace() {
        if (this.canAlwaysMove) return;

        this.getNavigation().stop();
        this.setXxa(0.0f);
        this.setYya(0.0f);
        this.setSpeed(0.0f);
    }

    // Custom movement controller for the entity
    static class CreakingMoveController extends MovementController {
        private final CreakingEntity mob;

        public CreakingMoveController(CreakingEntity mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void tick() {
            if (mob.canMove()) {
                super.tick();
            }
        }
    }

    static class CreakingBodyController extends BodyController {
        private final CreakingEntity mob;

        public CreakingBodyController(CreakingEntity mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void clientTick() {
            //body doesn't turn to follow head
            if (this.isMoving()) {
                this.mob.yBodyRot = this.mob.yRot;
                this.lastStableYHeadRot = this.mob.yHeadRot;
                this.headStableTime = 0;
            } else {
                this.headStableTime++;
            }
        }


    }

    public int getMaxHeadYRot() {
        return 180;
    } // can turn head in all directions


    // Custom look controller for the entity
    static class CreakingLookController extends LookController {
        private final CreakingEntity mob;

        public CreakingLookController(CreakingEntity mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void tick() {
            if (mob.canMove()) {
                super.tick();
                if (mob.creakingTarget != null && mob.canSee(mob.creakingTarget)) {
                    mob.lookControl.setLookAt(mob.creakingTarget.getEyePosition(1.0f).add(0, -0.2, 0));
                }
                if (mob.optionalTarget != null && mob.canSee(mob.optionalTarget)) {
                    mob.lookControl.setLookAt(mob.optionalTarget.getEyePosition(1.0f).add(0, -0.2, 0));
                }
            } else {
                this.hasWanted = false;
            }
        }
    }

    // Custom jump controller for the entity
    static class CreakingJumpController extends JumpController {
        private final CreakingEntity mob;

        public CreakingJumpController(CreakingEntity mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void tick() {
            if (mob.canMove()) {
                super.tick();
            } else {
                mob.setJumping(false);  // Prevent jumping if not allowed to move
            }
        }
    }

    // Sound for steps
    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.CREAKING_STEP, 0.15F, 1.0F);
    }

    // Custom sounds for ambient, hurt, and death
    @Override
    protected SoundEvent getAmbientSound() {
        return isActive() ? null : SoundEvents.CREAKING_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.CREAKING_SWAY;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREAKING_DEATH;
    }

    @Override
    public void push(Entity entity) { // this mob is impossible to be pushed by anything
        // Only allow the push if the entity is of the same type (e.g., same class)
        if (entity.getClass() == this.getClass()) {
            // Directly use the provided logic to calculate the push effect

            double d0 = entity.getX() - this.getX();
            double d1 = entity.getZ() - this.getZ();
            double d2 = MathHelper.absMax(d0, d1);

            if (d2 >= 0.01) {
                d2 = MathHelper.sqrt(d2);
                d0 = d0 / d2;
                d1 = d1 / d2;
                double d3 = 1.0D / d2;
                if (d3 > 1.0D) {
                    d3 = 1.0D;
                }

                d0 = d0 * d3 * 0.05 * (1.0F - this.pushthrough);
                d1 = d1 * d3 * 0.05 * (1.0F - this.pushthrough);

                // Directly set delta movement for gentle separation
                this.setDeltaMovement(this.getDeltaMovement().add(-d0, 0.0D, -d1));
                entity.setDeltaMovement(entity.getDeltaMovement().add(d0, 0.0D, d1));
            }
        } else if (!(entity instanceof PlayerEntity)) {
            // Default behavior for other entities that are not players
            super.push(entity);
        }
    }

    // Override push methods to handle other types of pushes if necessary
    public void push(double a, double b, double c) {
    }

    public void push(Vector3d d) {
    }




    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source == DamageSource.OUT_OF_WORLD) return super.hurt(source, amount);
        if (getSwayingTime() > 0) {
            this.setSwayingTime(MAX_INVUL_TIME);
            return false;
        }
        // Logic for damage handling
        boolean holdingAxe = source.getEntity() == null ? false : source.getEntity().as(LivingEntity.class).getMainHandItem().get() instanceof AxeItem;
        if (source.getEntity() instanceof PlayerEntity && ((PlayerEntity) source.getEntity()).isCreative() || holdingAxe ? random.nextFloat() < 0.95F : random.nextFloat() < 0.35F) {
            return super.hurt(source, amount);
        } else {
            this.setSwayingTime(MAX_INVUL_TIME);
            this.makeSound(SoundEvents.CREAKING_SWAY);
            return true;  // Invulnerable to non-creative mode damage
        }
    }


    // Creaking-specific AI for targeting and attacking any entity
    static class CreakingUniversalAttackGoal extends Goal {
        private final CreakingEntity mob;
        private final double speedModifier;
        private final Predicate<LivingEntity> targetPredicate;
        private final boolean ignoreLookCheck;
        private int attackCooldown = 20;
        private final boolean needsToSee;

        public CreakingUniversalAttackGoal(CreakingEntity mob, double speedModifier, Predicate<LivingEntity> targetPredicate, boolean ignoreLookCheck, boolean needsToSee) {
            this.mob = mob;
            this.speedModifier = mob.veryHardmode() || mob.level.getDifficulty() == Difficulty.HARD ? speedModifier + 0.1F : speedModifier;
            this.targetPredicate = targetPredicate;
            this.ignoreLookCheck = ignoreLookCheck;
            this.needsToSee = needsToSee;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            List<LivingEntity> potentialTargets = this.mob.level.getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(32.0D), this.targetPredicate);
            if (potentialTargets.isEmpty()) {
                return false;
            }

            // Set the first valid target found
            this.mob.optionalTarget = potentialTargets.get(mob.random.nextInt(potentialTargets.size()));

            if (needsToSee && this.mob.optionalTarget != null && !this.mob.canSee(this.mob.optionalTarget)) {
                return false;
            }

            return true;
        }

        @Override
        public boolean canContinueToUse() {
            // Validate that the target is still alive and within range
            if (this.mob.optionalTarget == null || !this.mob.optionalTarget.isAlive()) {
                return false;
            }

            // Continue using the goal if the target is still nearby and valid
            return this.mob.distanceTo(this.mob.optionalTarget) < 88.0D;
        }

        private boolean playerLookingAtMob(Mob mob) {
            // Check if any player nearby is looking at the mob
            List<PlayerEntity> nearbyPlayers = this.mob.level.getEntitiesOfClass(PlayerEntity.class, this.mob.getBoundingBox().inflate(32.0D));

            for (PlayerEntity player : nearbyPlayers) {
                // Check if player is looking at the mob; if so, stop the mob's movement
                boolean canMove = ((CreakingEntity) mob).checkCanMove(entity -> entity == player);
                if (!canMove) {
                    ((CreakingEntity) mob).stopInPlace();
                    return true;
                }
            }
            return false;
        }

        private boolean targetLookingAtMob(LivingEntity target) {
            // Only check the target’s look direction if `ignoreLookCheck` is false
            if (ignoreLookCheck) return false;

            boolean canMove = ((CreakingEntity) mob).checkCanMove(entity -> entity.getClass().isInstance(target));
            if (!canMove) {
                ((CreakingEntity) mob).stopInPlace();
                return true;
            }
            return false;
        }

        @Override
        public void tick() {
            if (this.mob.optionalTarget != null) {
                // Check if the target is in creative or spectator mode
                if (!EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(this.mob.optionalTarget)) {
                    this.mob.stopInPlace();
                    this.mob.optionalTarget = null;
                    this.stop();
                    return;
                }

                // Calculate the target's current position and view direction
                Vector3d targetPosition = this.mob.optionalTarget.position();
                Vector3d targetLookDirection = this.mob.optionalTarget.getLookAngle();
                double offsetDistance = 0.6;
                Vector3d behindTargetPosition = targetPosition.subtract(targetLookDirection.scale(offsetDistance));

                // Always check if any player is looking at the mob
                if (playerLookingAtMob(mob)) {
                    this.mob.stopInPlace();
                    return;
                }

                // Only consider the specific target's look direction if `ignoreLookCheck` is false
                if (ignoreLookCheck || !targetLookingAtMob(this.mob.optionalTarget)) {
                    this.mob.getNavigation().moveTo(behindTargetPosition.x, behindTargetPosition.y, behindTargetPosition.z, speedModifier);
                } else {
                    // If the target is looking at the mob and `ignoreLookCheck` is false, stop movement
                    this.mob.stopInPlace();
                    return;
                }

                // Attack if close enough and neither the target nor a player is looking at the mob
                double extendedReach = mob.getBbWidth() * 2.0D + 0.4D;
                if (mob.distanceToSqr(this.mob.optionalTarget) < extendedReach * extendedReach && this.attackCooldown <= 0 && !targetLookingAtMob(this.mob.optionalTarget)) {
                    mob.doHurtTarget(this.mob.optionalTarget);
                    resetAttackCooldown();
                    mob.playSound(SoundEvents.CREAKING_ATTACK, 1.0F, 1.0F);
                } else {
                    --this.attackCooldown;
                }
            } else {
                this.mob.stopInPlace();
            }
        }

        private void resetAttackCooldown() {
            this.attackCooldown = mob.attackTime; // Reset cooldown to 20 ticks (matching MeleeAttackGoal)
        }
    }


}
