package net.minecraft.entity.monster.creaking.block;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.entity.monster.creaking.CreakingTransient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SpawnUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Optional;
import java.util.Random;

public class CreakingHeartBlockEntity extends TileEntity {
    private static final int PLAYER_DETECTION_RADIUS = 32;
    private static final int MAX_CREATURE_ROAM_DISTANCE = 34;
    private static final int CREATURE_SPAWN_RANGE_XZ = 16;
    private static final int CREATURE_SPAWN_RANGE_Y = 8;
    private static final int SPAWN_ATTEMPTS = 5;
    private static final int TICK_INTERVAL = 20;
    private static final int HURT_CALL_DURATION = 100;
    private static final int NUM_HURT_CALLS = 10;
    private static final int HURT_CALL_INTERVAL = 10;
    private static final int PARTICLE_EMIT_DURATION = 50;
    private static final int MAX_DEPTH = 2;
    private static final int MAX_SEARCH_COUNT = 64;

    @Nullable
    private CreakingTransient protector;
    private int tickCounter;
    private int hurtCallCooldown;
    @Nullable
    private Vector3d hurtCallTarget;
    private int redstoneSignalStrength;

    public CreakingHeartBlockEntity() {
        super(TileEntityType.CREAKING_HEART);
    }

    public static void serverTick(World level, BlockPos pos, BlockState state, CreakingHeartBlockEntity entity) {
        ServerWorld serverWorld;

        int currentSignal = entity.computeRedstoneSignal();
        if (entity.redstoneSignalStrength != currentSignal) {
            entity.redstoneSignalStrength = currentSignal;
            level.updateNeighbourForOutputSignal(pos, Blocks.CREAKING_HEART);
        }

//        if (entity.hurtCallCooldown > 0) {
//            entity.handleHurtCall((ServerWorld) level, pos);
//        }

        if (entity.tickCounter-- > 0) {
            return;
        }
        entity.tickCounter = TICK_INTERVAL;

        if (entity.protector != null) {
            if (!CreakingHeartBlock.isNaturalNight(level) || entity.getDistanceToProtector() > MAX_CREATURE_ROAM_DISTANCE) {
                entity.removeProtector(null);
                return;
            }

            if (entity.protector.removed) {
                entity.protector = null;
            }

            if (!CreakingHeartBlock.hasRequiredLogs(state, level, pos) && entity.protector == null) {
                level.setBlock(pos, state.setValue(CreakingHeartBlock.ACTIVE, false), 3);
            }
            return;
        }

        if (!CreakingHeartBlock.hasRequiredLogs(state, level, pos)) {
            level.setBlock(pos, state.setValue(CreakingHeartBlock.ACTIVE, false), 3);
            return;
        }

        if (!state.getValue(CreakingHeartBlock.ACTIVE)) {
            return;
        }

        if (!CreakingHeartBlock.isNaturalNight(level) || level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }

        if (level instanceof ServerWorld && !(serverWorld = (ServerWorld) level).getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return;
        }

        PlayerEntity nearestPlayer = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), PLAYER_DETECTION_RADIUS, false);
        if (nearestPlayer != null) {
            entity.protector = spawnProtector((ServerWorld) level, entity);
            if (entity.protector != null) {
                //entity.protector.playSpawnSound();
                level.playSound(null, pos, SoundEvents.CREAKING_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    private double getDistanceToProtector() {
        if (this.protector == null) {
            return 0.0;
        }
        return Math.sqrt(this.protector.distanceToSqr(Vector3d.atBottomCenterOf(this.getBlockPos())));
    }

    @Nullable
    private static CreakingTransient spawnProtector(ServerWorld level, CreakingHeartBlockEntity entity) {
        BlockPos pos = entity.getBlockPos();
        Optional<CreakingTransient> spawnedProtector = SpawnUtil.trySpawnMob(EntityType.CREAKING_TRANSIENT, SpawnReason.SPAWNER, level, pos, SPAWN_ATTEMPTS, CREATURE_SPAWN_RANGE_XZ, CREATURE_SPAWN_RANGE_Y, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER_NO_LEAVES, true);

        if (spawnedProtector.isEmpty()) {
            return null;
        }

        CreakingTransient protectorEntity = spawnedProtector.get();
        level.broadcastEntityEvent(protectorEntity, (byte) 60);
        protectorEntity.bindToCreakingHeart(pos);
        return protectorEntity;
    }

    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return super.getUpdateTag();
    }

    public void handleProtectorDamage() {
        if (this.protector == null) {
            return;
        }

        World level = this.level;
        if (!(level instanceof ServerWorld)) {
            return;
        }

        ServerWorld serverWorld = (ServerWorld) level;
        if (this.hurtCallCooldown > 0) {
            return;
        }

        emitParticles(serverWorld, 20, false);


        this.hurtCallCooldown = HURT_CALL_DURATION;
        this.hurtCallTarget = this.protector.getBoundingBox().getCenter();
    }

    private void emitParticles(ServerWorld level, int particleCount, boolean reverse) {
        if (this.protector == null) {
            return;
        }

        int particleColor = reverse ? 16545810 : 0x5F5F5F;
        Random random = level.random;

        for (int i = 0; i < particleCount; i++) {
            AxisAlignedBB bb = protector.getBoundingBox();
            Vector3d start = new Vector3d(bb.minX,bb.minY,bb.minZ).add(random.nextDouble() * this.protector.getBoundingBox().getXsize(), random.nextDouble() * this.protector.getBoundingBox().getYsize(), random.nextDouble() * this.protector.getBoundingBox().getZsize());
            Vector3d end = Vector3d.atLowerCornerOf(this.getBlockPos()).add(random.nextDouble(), random.nextDouble(), random.nextDouble());

            if (reverse) {
                Vector3d temp = start;
                start = end;
                end = temp;
            }

            //TrailParticleOption particle = new TrailParticleOption(end, particleColor, random.nextInt(40) + 10);
           // level.sendParticles(particle, true, true, start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public void removeProtector(@Nullable DamageSource damageSource) {
        if (this.protector != null) {
            if (damageSource == null) {
                this.protector.tearDown();
            } else {
                this.protector.creakingDeathEffects(damageSource);
                this.protector.setTearingDown();
                this.protector.setHealth(0.0f);
            }
            this.protector = null;
        }
    }

    public boolean isProtector(CreakingEntity entity) {
        return this.protector == entity;
    }

    public int getRedstoneSignal() {
        return this.redstoneSignalStrength;
    }

    public int computeRedstoneSignal() {
        if (this.protector == null) {
            return 0;
        }
        double distance = this.getDistanceToProtector();
        double scaledDistance = MathHelper.clamp(distance, 0.0, PLAYER_DETECTION_RADIUS) / PLAYER_DETECTION_RADIUS;
        return 15 - (int) Math.floor(scaledDistance * 15.0);
    }
}
