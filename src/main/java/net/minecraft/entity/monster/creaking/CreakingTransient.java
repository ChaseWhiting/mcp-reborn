package net.minecraft.entity.monster.creaking;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.creaking.block.CreakingHeartBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class CreakingTransient extends CreakingEntity {

    private static final int INVULNERABILITY_ANIMATION_DURATION = 8;
    private int invulnerabilityAnimationRemainingTicks;
    @Nullable
    private BlockPos homePos;

    public CreakingTransient(EntityType<? extends CreakingEntity> entityType, World world) {
        super(entityType, world);
        this.navigation = new CreakingTransientNavigator(this, world);
    }

    public void bindToCreakingHeart(BlockPos homePos) {
        this.homePos = homePos;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level.isClientSide) {
            return super.hurt(source, amount);
        }

        if (source.isBypassInvul()) {
            return super.hurt(source, amount);
        }

        if (this.invulnerabilityAnimationRemainingTicks > 0 || !(source.getEntity() instanceof PlayerEntity)) {
            return true;
        }

        this.invulnerabilityAnimationRemainingTicks = INVULNERABILITY_ANIMATION_DURATION;
        this.level.broadcastEntityEvent(this, (byte) 66);

        TileEntity tileEntity = this.level.getBlockEntity(this.homePos);
        if (tileEntity instanceof CreakingHeartBlockEntity) {
            CreakingHeartBlockEntity creakingHeart = (CreakingHeartBlockEntity) tileEntity;
            if (creakingHeart.isProtector(this)) {
                creakingHeart.handleProtectorDamage();
                this.playHurtSound(source);
            }
        }
        return true;
    }

    @Override
    public void aiStep() {
        if (this.invulnerabilityAnimationRemainingTicks > 0) {
            --this.invulnerabilityAnimationRemainingTicks;
        }
        super.aiStep();
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (this.homePos == null || !(this.level.getBlockEntity(this.homePos) instanceof CreakingHeartBlockEntity)) {
                this.remove();
            }
        }
        super.tick();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 66) {
            this.invulnerabilityAnimationRemainingTicks = INVULNERABILITY_ANIMATION_DURATION;
            this.playHurtSound(DamageSource.GENERIC);
        } else {
            super.handleEntityEvent(id);
        }
    }



    public void tearDown(@Nullable DamageSource source) {
        if (this.level instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) this.level;
            AxisAlignedBB boundingBox = this.getBoundingBox();
            Vector3d center = boundingBox.getCenter();
            double xSize = boundingBox.getXsize() * 0.3;
            double ySize = boundingBox.getYsize() * 0.3;
            double zSize = boundingBox.getZsize() * 0.3;

            serverWorld.sendParticles(new BlockParticleData(ParticleTypes.BLOCK, Blocks.OAK_WOOD.defaultBlockState()),
                    center.x, center.y, center.z, 100, xSize, ySize, zSize, 0.0);
            serverWorld.sendParticles(new BlockParticleData(ParticleTypes.BLOCK, Blocks.CREAKING_HEART.defaultBlockState()),
                    center.x, center.y, center.z, 10, xSize, ySize, zSize, 0.0);
        }

        this.playSound(SoundEvents.GENERIC_DEATH, 1.0F, 1.0F);
        this.remove();
    }

    @Override
    protected boolean canRide(Entity entityIn) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }
    class CreakingTransientNavigator extends GroundPathNavigator {
        public CreakingTransientNavigator(CreakingEntity creaking, World world) {
            super(creaking, world);
        }

        @Override
        public void tick() {
            if (CreakingTransient.this.canMove()) {
                super.tick();
            }
        }

        @Override
        protected PathFinder createPathFinder(int p_179679_1_) {
            this.nodeEvaluator = new HomeNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, p_179679_1_);
        }
    }

    class HomeNodeEvaluator extends WalkNodeProcessor {
        @Override
        public PathNodeType getBlockPathType(IBlockReader world, int x, int y, int z) {
            if (CreakingTransient.this.homePos != null && CreakingTransient.this.homePos.distSqr(new BlockPos(x, y, z)) > 1024.0) {
                return PathNodeType.BLOCKED;
            }
            return super.getBlockPathType(world, x, y, z);
        }
    }
}
