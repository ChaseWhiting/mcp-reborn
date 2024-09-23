package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.HomingModuleEnchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.passive.Animal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Level;

import java.util.*;

public class FrisbeeEntity extends ProjectileItemEntity {
    public Set<Block> breakableBlocks = new HashSet<>(Arrays.asList(
            Blocks.DIRT,
            Blocks.COARSE_DIRT,
            Blocks.PODZOL,
            Blocks.MYCELIUM,
            Blocks.GRASS_BLOCK,
            Blocks.STONE,
            Blocks.GRANITE,
            Blocks.DIORITE,
            Blocks.ANDESITE,
            Blocks.COBBLESTONE,
            Blocks.STONE_BRICKS,
            Blocks.CRACKED_STONE_BRICKS,
            Blocks.MOSSY_STONE_BRICKS,
            Blocks.GRASS,
            Blocks.BEEHIVE,
            Blocks.BEE_NEST,
            Blocks.FARMLAND,
            Blocks.SAND
    ));

    // Add more blocks as needed
    {
        breakableBlocks.addAll(BlockTags.FLOWERS.getValues());
        breakableBlocks.addAll(BlockTags.LOGS_THAT_BURN.getValues());
        breakableBlocks.addAll(BlockTags.LEAVES.getValues());
        breakableBlocks.addAll(BlockTags.CROPS.getValues());
    }

    public void breakSoft(int damage, BlockRayTraceResult result, boolean drop) {
        BlockPos blockPos = result.getBlockPos();
        BlockState blockState = this.level.getBlockState(blockPos);
        Block block = blockState.getBlock();
        // Create a set of breakable blocks

        // Check if the block is in the breakable blocks set
        if (breakableBlocks.contains(block)) {
            // Destroy the block
            this.level.destroyBlock(blockPos, drop);

            // Damage the frisbee item
            this.hurt(damage);
        }
    }

    private static final DataParameter<Byte> FRISBEE_FLAGS = EntityDataManager.defineId(FrisbeeEntity.class, DataSerializers.BYTE);

    protected boolean isInGround;
    public ItemStack frisbeeItemStack;
    private int frisbeeSpeed;
    private int returnDistanceThreshold;
    private FrisbeeData data;
    private int frisbeeLifetime;
    private boolean homing = false;
    private boolean setHoming = false;
    private int slowdownDuration;
    private boolean windResistant = false;
    private int hitEntities = 0;
    private double[] range = new double[]{12D, 6D};
    private boolean phase = false;
    private IntSet hitEntityIds = new IntOpenHashSet();  // To track hit entities
    private LivingEntity currentTarget = null;  // To track the current target
    private Vector3d targetPosition = null;  // To store the position of the current target
    private double slowdownFactor;
    private double frisbeeBaseDamage;
    private boolean shouldReturnToPlayer = false;
    private boolean hasRecordedPlayerPosition = false;
    public boolean hasDropped = false; // New flag to prevent double drops
    private boolean initialHit = false;
    private Vector3d playerInitialPosition = Vector3d.ZERO;
    private boolean bypassArmour;
    private FrisbeeWindManager windManager;

    public FrisbeeEntity(EntityType<? extends FrisbeeEntity> type, World world) {
        super(type, world);
    }

    @OnlyIn(Dist.CLIENT)
    public FrisbeeEntity(World world, double x, double y, double z) {
        super(EntityType.FRISBEE, x, y, z, world);
    }

    protected FrisbeeEntity(EntityType<? extends FrisbeeEntity> type, double x, double y, double z, World world) {
        this(type, world);
        this.setPos(x, y, z);
    }

    public void setItemStack(ItemStack stack) {
        this.frisbeeItemStack = stack;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FRISBEE_FLAGS, (byte) 0);
    }

    public FrisbeeEntity(EntityType<? extends FrisbeeEntity> type, LivingEntity owner, World world, FrisbeeData frisbeeData, ItemStack stack) {
        this(type, owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), world);
        setItemStack(stack);
        applyFrisbeeData(frisbeeData);
        long seed = Util.getMillis();
        if (!this.level.isClientSide) {
            seed = ((ServerWorld) this.level).getSeed();
        }
        this.windManager = new FrisbeeWindManager(this.level, seed);
        if (owner instanceof PlayerEntity && this.getOwner() != null) {
            this.playerInitialPosition = this.getOwner().position();
        }
    }

    public FrisbeeEntity(EntityType<? extends FrisbeeEntity> type, LivingEntity owner, World world, FrisbeeData frisbeeData, ItemStack stack, boolean dropFrisbee) {
        this(type,owner,world,frisbeeData,stack);
        hasDropped = dropFrisbee;
    }

    public void applyFrisbeeData(FrisbeeData data) {
        this.data = data;
        this.range = data.getRange();
        this.frisbeeBaseDamage = data.getBaseDamage();
        this.bypassArmour = data.isBypassArmour();
        this.frisbeeSpeed = data.getSpeed();
        this.returnDistanceThreshold = data.getDistanceToComeBack();
        this.slowdownFactor = data.getReducWhenHittingMob();
        this.phase = data.phase();
        this.windResistant = data.isWindResistant();
    }


    @Override
    protected Item getDefaultItem() {
        return frisbeeItemStack == null ? Items.WOODEN_FRISBEE : frisbeeItemStack.getItem();
    }

    public void absorbWater(FrisbeeEntity frisbee, World world, BlockPos pos) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Tuple<>(pos, 0));
        int absorbedWaterBlocks = 0;
        int durabilityLoss = 0;

        while (!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockPos = tuple.getA();
            int depth = tuple.getB();

            for (Direction direction : Direction.values()) {
                BlockPos adjacentPos = blockPos.relative(direction);
                BlockState blockState = world.getBlockState(adjacentPos);
                FluidState fluidState = world.getFluidState(adjacentPos);
                Material material = blockState.getMaterial();

                if (fluidState.is(FluidTags.WATER)) {
                    if (blockState.getBlock() instanceof IBucketPickupHandler &&
                            ((IBucketPickupHandler) blockState.getBlock()).takeLiquid(world, adjacentPos, blockState) != Fluids.EMPTY) {
                        absorbedWaterBlocks++;
                        durabilityLoss++;
                        if (depth < 6) {
                            queue.add(new Tuple<>(adjacentPos, depth + 1));
                        }
                    } else if (blockState.getBlock() instanceof FlowingFluidBlock) {
                        world.setBlock(adjacentPos, Blocks.AIR.defaultBlockState(), 3);
                        absorbedWaterBlocks++;
                        durabilityLoss++;
                        if (depth < 6) {
                            queue.add(new Tuple<>(adjacentPos, depth + 1));
                        }
                    } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                        TileEntity tileEntity = blockState.getBlock().isEntityBlock() ? world.getBlockEntity(adjacentPos) : null;
                        Block.dropResources(blockState, world, adjacentPos, tileEntity);
                        world.setBlock(adjacentPos, Blocks.AIR.defaultBlockState(), 3);
                        absorbedWaterBlocks++;
                        durabilityLoss++;
                        if (depth < 6) {
                            queue.add(new Tuple<>(adjacentPos, depth + 1));
                        }
                    }
                }
            }

            // Stop if too many blocks have been absorbed in this tick
            if (absorbedWaterBlocks > 64) {
                break;
            }
        }

        // Apply durability loss to the frisbee
        if (durabilityLoss > 0 && frisbee.frisbeeItemStack != null) {
            frisbee.hurt(4);
        }
    }

    public void tickWind() {
        if (this.windManager != null && !windResistant) {
            this.level.getProfiler().push("frisbeeWind");
            RegistryKey<World> level = this.level.dimension();
            Vector3d windVector = windManager.getWindVectorAtLocation(this.level, this.position().asBlockPos());
            boolean flag = level == World.END;
            boolean flag2 = level == World.NETHER;
            windVector = windVector.multiply(flag ? 0.7 : flag2 ? 0.2 : 0.08);
            this.setDeltaMovement(this.getDeltaMovement().add(windVector));
            this.level.getProfiler().pop();
        }
    }



    @Override
    public void tick() {
        if (data != null) {
            data.triggerFlyingBehavior(this);
        }
        frisbeeLifetime++;
        super.tick();

        this.tickWind();
        if (!this.level.isClientSide()) {
            Vector3d currentMovement = this.getDeltaMovement();
            LivingEntity owner = (LivingEntity) this.getOwner();

            if (owner != null && !hasRecordedPlayerPosition) {
                Vector3d vec = owner.position();
                playerInitialPosition = new Vector3d(vec.x, owner.getEyeY() - 1, vec.z);
                hasRecordedPlayerPosition = true;
            }

            if (frisbeeItemStack != null) {
                int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.HOMING, frisbeeItemStack);
                if (level > 0 && !this.setHoming) {
                    setHoming = true;
                    homing = true;
                }

                if (EnchantmentHelper.has(frisbeeItemStack, Enchantments.RETURNING) && owner != null) {
                    Vector3d vec = owner.position();
                    playerInitialPosition = new Vector3d(vec.x, owner.getEyeY() - 1, vec.z);
                    hasRecordedPlayerPosition = true;
                }
                this.setItem(frisbeeItemStack);
            }

            if (!this.isInGround) {
                if (this.shouldReturnToPlayer) {
                    if (owner != null) {
                        calculateMovement(currentMovement);
                    }
                } else if (owner != null && playerInitialPosition != null) {
                    if (this.distanceToSqr(playerInitialPosition) > returnDistanceThreshold || this.isInGround) {
                        this.shouldReturnToPlayer = true;
                        this.setNoPhysics(false);
                    }
                }

                if (homing && !this.isInGround) {
                    findAndTrackTargets();  // New method to find and track targets
                    if (currentTarget != null) {
                        calculateMovementTowardTarget(currentMovement);  // Adjust frisbee's trajectory
                    }
                }

                if (playerInitialPosition != null && !hasDropped) {  // Check hasDropped flag
                    if (owner != null && (this.frisbeeLifetime > 40 || this.shouldReturnToPlayer)) {
                        double distanceToInitialPos = this.distanceToSqr(playerInitialPosition);
                        double distanceToOwner = this.distanceToSqr(owner);

                        if (distanceToOwner < 4) {
                            dropItem();  // Call a different method if the owner is closest
                            hasDropped = true;  // Set flag after dropping
                        } else if (distanceToInitialPos < 1) {
                            dropFrisbee();  // Call the original method if the initial position is closest
                            hasDropped = true;  // Set flag after dropping
                        }
                    }
                }
            }

            this.checkInsideBlocks();
        }
    }


    private void calculateMovement(Vector3d currentMovement) {
        Vector3d returnVector = playerInitialPosition.subtract(this.position()).add(0, 0.7, 0);
        returnVector = returnVector.normalize().scale(0.05D + 0.002 * frisbeeSpeed);
        this.setDeltaMovement(currentMovement.scale(0.95D).add(returnVector));
        this.hurtMarked = true;
        this.hasImpulse = true;
    }

    private void calculateMovementTowardMob(Vector3d movement, LivingEntity entity) {
        Vector3d hitMob = entity.position().subtract(this.position()).add(0, 0.7, 0);
        hitMob = hitMob.normalize().scale(0.05D + 0.002 * frisbeeSpeed);
        this.setDeltaMovement(movement.scale(0.95D).add(hitMob));
        this.hurtMarked = true;
        this.hasImpulse = true;
    }

    public void dropFrisbee() {
        if (!hasDropped) {  // Check if the Frisbee has already been dropped
            if (!this.level.isClientSide()) {
                this.spawnAtLocation(frisbeeItemStack != null ? frisbeeItemStack : new ItemStack(Items.WOODEN_FRISBEE), 0.1F);
                this.remove();
            }
            hasDropped = true;  // Set flag to prevent further drops
        }
    }

    public void dropItem() {
        if (!hasDropped && this.getOwner() instanceof PlayerEntity) {
            data.triggerOnReturn(this, ((PlayerEntity) this.getOwner()));
            if (!this.level.isClientSide()) {
                ((PlayerEntity) this.getOwner()).addItem(frisbeeItemStack != null ? frisbeeItemStack : new ItemStack(Items.WOODEN_FRISBEE));
                this.remove();
            }
            hasDropped = true;
        }
    }

    protected boolean canHitEntity(Entity entity) {


        return super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (data != null) {
            data.triggerOnHitEntity(this, result);
        }

        if (!this.level.isClientSide()) {
            if (frisbeeItemStack != null) {
                int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.RADIANCE, frisbeeItemStack);

                if (level > 0 && result.getEntity() instanceof LivingEntity) {
                    int duration = 5 + 5 * level * 20;  // Improve readability
                    ((LivingEntity) result.getEntity()).addEffect(new EffectInstance(Effects.GLOWING, duration, 0));
                }

                hurt(1);
            }
            if (entity instanceof LivingEntity && homing && EnchantmentHelper.has(frisbeeItemStack, Enchantments.HOMING)) {
                int entityId = entity.getId();
                if (!hitEntityIds.contains(entityId)) {
                    hitEntityIds.add(entityId);
                    hitEntities++;
                    int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.HOMING, frisbeeItemStack);
                    if (hitEntities > 3 + level) {
                        this.shouldReturnToPlayer = true;
                        homing = false;// Trigger return after hitting more than 3 entities
                    }
                }
            }
            // Existing entity hit logic...
            slowdownDuration = 40;
            applySlowdown1();
            if (result.getEntity() != this.getOwner()) {
                this.shouldReturnToPlayer = true;
                initialHit = true;
                DamageSource damageSource = this.bypassArmour ? DamageSource.boomerangarmourBypass(this, this.getOwner()) : DamageSource.boomerang(this, this.getOwner());
                result.getEntity().hurt(damageSource, (float) frisbeeBaseDamage);
            }

            this.setNoPhysics(true);
        }
    }

    public void hurt(int hurt) {
        if (this.getOwner() instanceof PlayerEntity) {
            frisbeeItemStack.hurtAndBreak(hurt, (PlayerEntity) this.getOwner(),
                    (p) -> p.broadcastBreakEvent(((PlayerEntity) this.getOwner()).getUsedItemHand()));

            // Check if the item has only 1 durability left
            if (frisbeeItemStack.getDamageValue() >= frisbeeItemStack.getMaxDamage() - 1) {
                this.remove();
            }
        }
    }

    private void applySlowdown() {
        Vector3d currentMovement = this.getDeltaMovement();
        Vector3d targetMovement = currentMovement.multiply(slowdownFactor, slowdownFactor, slowdownFactor);
        double lerpFactor = 0.03D;
        Vector3d newMovement = Vector3d.lerp(currentMovement, targetMovement, lerpFactor);
        this.setDeltaMovement(newMovement);
    }

    public void applySlowdown1() {
        do {
            slowdownDuration--;
            applySlowdown();
        } while (this.slowdownDuration > 0);
    }

    public void addAdditionalSaveData(CompoundNBT nbt) {
        nbt.put("FrisbeeItemStack", this.getPickupItem().save(new CompoundNBT()));
        nbt.putInt("FrisbeeSpeed", this.frisbeeSpeed);
        nbt.putInt("ReturnDistanceThreshold", this.returnDistanceThreshold);
        nbt.putDouble("FrisbeeBaseDamage", this.frisbeeBaseDamage);
        nbt.putDouble("SlowdownFactor", this.slowdownFactor);
        nbt.putBoolean("ShouldReturnToPlayer", this.shouldReturnToPlayer);
        if (playerInitialPosition != null) {
            nbt.put("ReturnPos", NBTUtil.writeBlockPos(new BlockPos(playerInitialPosition.x, playerInitialPosition.y, playerInitialPosition.z)));
        }
        if (targetPosition != null) {
            nbt.put("TargetPos", NBTUtil.writeBlockPos(new BlockPos(targetPosition.x, targetPosition.y, targetPosition.z)));
        }

    }

    public void readAdditionalSaveData(CompoundNBT nbt) {
        if (nbt.contains("FrisbeeItemStack")) {
            this.frisbeeItemStack = ItemStack.of(nbt.getCompound("FrisbeeItemStack"));
        }
        if (nbt.contains("FrisbeeSpeed")) {
            this.frisbeeSpeed = nbt.getInt("FrisbeeSpeed");
        }
        if (nbt.contains("ReturnDistanceThreshold")) {
            this.returnDistanceThreshold = nbt.getInt("ReturnDistanceThreshold");
        }
        if (nbt.contains("FrisbeeBaseDamage")) {
            this.frisbeeBaseDamage = nbt.getDouble("FrisbeeBaseDamage");
        }
        if (nbt.contains("SlowdownFactor")) {
            this.slowdownFactor = nbt.getDouble("SlowdownFactor");
        }
        if (nbt.contains("ShouldReturnToPlayer")) {
            this.shouldReturnToPlayer = nbt.getBoolean("ShouldReturnToPlayer");
        }
        if (nbt.contains("ReturnPos")) {
            BlockPos pos = NBTUtil.readBlockPos(nbt.getCompound("ReturnPos"));
            this.playerInitialPosition = new Vector3d(pos);
        }
        if (nbt.contains("TargetPos")) {
            BlockPos pos = NBTUtil.readBlockPos(nbt.getCompound("TargetPos"));
            this.targetPosition = new Vector3d(pos);
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult result) {
        super.onHitBlock(result);
        if (data != null) {
            data.triggerOnHitBlock(this, result);
        }
        if (phase == false) {
            // Check if frisbeeItemStack is not null and doesn't have SPECTRAL_THROW enchantment
            if (frisbeeItemStack == null || !EnchantmentHelper.has(frisbeeItemStack, Enchantments.SPECTRAL_THROW)) {
                if (!hasDropped) {  // Prevent duplicate drop on block hit
                    this.dropFrisbee();
                    hasDropped = true;
                }
            }
        }
    }

    protected ItemStack getPickupItem() {
        return frisbeeItemStack;
    }

    public void setNoPhysics(boolean noPhysics) {
        if (this.isNoPhysics()) {
            return;
        }
        try {
            byte flags = this.entityData.get(FRISBEE_FLAGS);
            this.noPhysics = noPhysics;
            this.entityData.set(FRISBEE_FLAGS, (byte) (flags | 2));
        } catch (NullPointerException n) {
            LOGGER.error(Level.FATAL, n);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void findAndTrackTargets() {
        if (frisbeeItemStack != null) {
            List<LivingEntity> potentialTargets = this.level.getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(range[0] + ((double) returnDistanceThreshold / range[1])),
                    entity -> {
                        boolean initialCheck = entity != this.getOwner() && !hitEntityIds.contains(entity.getId());

                        // Check if the item has any HomingModuleEnchantment
                        boolean hasHomingModule = false;
                        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(frisbeeItemStack);
                        for (Enchantment enchantment : enchantments.keySet()) {
                            if (enchantment instanceof HomingModuleEnchantment) {
                                HomingModuleEnchantment homingModule = (HomingModuleEnchantment) enchantment;
                                hasHomingModule = true; // Indicate that the frisbee has a homing module
                                // Check if the entity is of the target type
                                if (homingModule.trySetMatchedEntity(entity)) {
                                    return initialCheck;
                                }
                            }
                        }
                        // If no HomingModuleEnchantment, or if no entity matched, proceed with normal behavior
                        return !hasHomingModule && initialCheck;
                    });


            if (!potentialTargets.isEmpty()) {
                LivingEntity closestEntity = null;
                double closestDistanceSq = Double.MAX_VALUE;

                for (LivingEntity entity : potentialTargets) {
                    double distanceSq = this.distanceToSqr(entity);
                    if (distanceSq < closestDistanceSq) {
                        closestDistanceSq = distanceSq;
                        closestEntity = entity;
                    }
                }

                this.currentTarget = closestEntity;  // Set the closest entity as the current target
                if (this.currentTarget != null) {
                    this.targetPosition = this.currentTarget.position();  // Update target position
                } else {
                    this.targetPosition = null;
                }
            } else {
                this.currentTarget = null;
                this.targetPosition = null;
            }
        }
    }


    /**
     * Calculate the Frisbee's movement towards the current target.
     */
    private void calculateMovementTowardTarget(Vector3d currentMovement) {
        if (targetPosition != null && frisbeeItemStack != null) {
            boolean flag = EnchantmentHelper.has(frisbeeItemStack, Enchantments.SPECTRAL_THROW);
            Vector3d toTarget = targetPosition.subtract(this.position()).normalize();

            // Increase the Y component to raise the frisbee above the ground level.
            double yAdjustment = 0.7; // Adjust this value as needed
            toTarget = new Vector3d(toTarget.x, toTarget.y + yAdjustment, toTarget.z).normalize();

            // Adjust movement based on block detection if flag is false
            if (!flag) {
                // Check for potential collisions in adjacent blocks
                Vector3d[] directions = {
                        new Vector3d(1, 0, 0),   // Right
                        new Vector3d(-1, 0, 0),  // Left
                        new Vector3d(0, 1, 0),   // Up
                        new Vector3d(0, -1, 0),  // Down
                        new Vector3d(0, 0, 1),   // Forward
                        new Vector3d(0, 0, -1)   // Backward
                };

                boolean blockDetected = false;
                Vector3d adjustment = new Vector3d(0, 0, 0);

                for (Vector3d direction : directions) {
                    BlockPos checkPos = new BlockPos(this.position().add(direction));
                    if (this.level.getBlockState(checkPos).getCollisionShape(this.level, checkPos).isEmpty()) {
                        continue;
                    }

                    blockDetected = true;
                    adjustment = adjustment.add(direction);
                }

                if (blockDetected) {
                    // Modify toTarget based on block avoidance
                    toTarget = toTarget.add(adjustment.normalize().scale(0.1)); // Adjust avoidance strength as needed
                }
            }

            Vector3d newMovement = toTarget.scale(0.05D + 0.002 * frisbeeSpeed);
            this.setDeltaMovement(currentMovement.scale(0.95D).add(newMovement));
            this.hurtMarked = true;
            this.hasImpulse = true;
        }
    }


    public boolean isNoPhysics() {
        return this.noPhysics;
    }
}
