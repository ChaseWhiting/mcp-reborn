package net.minecraft.entity.item;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public abstract class BlockAttachedEntity extends Entity {

    public static final Logger LOGGER = LogManager.getLogger();
    private int checkInterval;
    protected BlockPos pos;

    protected BlockAttachedEntity(EntityType<? extends BlockAttachedEntity> entityType, World level) {
        super(entityType, level);
    }

    protected BlockAttachedEntity(EntityType<? extends BlockAttachedEntity> entityType, World level, BlockPos blockPos) {
        this(entityType, level);
        this.pos = blockPos;
    }

    protected abstract void recalculateBoundingBox();

    @Override
    public void tick() {
        World level = this.level();
        if (level instanceof ServerWorld serverWorld) {
            if (this.getY() < -64.0D) {
                this.outOfWorld();
            }
            if (this.checkInterval++ == 1) {
                this.checkInterval = 0;
                if (!this.removed && !this.survives()) {
                    this.discard();
                    this.dropItem(serverWorld, null);
                }
            }
        }
    }

    public abstract boolean survives();

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            if (!this.level().mayInteract(player, this.pos)) {
                return true;
            }
            return this.hurtOrSimulate(DamageSource.playerAttack(player), 0.0f);
        }
        return false;
    }

    @Override
    public boolean hurtClient(DamageSource damageSource) {
        return !this.isInvulnerableTo(damageSource);
    }

    @Override
    public boolean hurtServer(ServerWorld serverLevel, DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && damageSource.getEntity() instanceof Mob) {
            return false;
        }
        if (!this.removed) {
            this.kill();
            this.markHurt();
            this.dropItem(serverLevel, damageSource.getEntity());
        }
        return true;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public void move(MoverType moverType, Vector3d vec3) {
        World level = this.level();
        if (level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld)level;
            if (!this.removed && vec3.lengthSqr() > 0.0) {
                this.kill();
                this.dropItem(serverLevel, null);
            }
        }
    }

    @Override
    public void push(double d, double d2, double d3) {
        World level = this.level();
        if (level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld)level;
            if (!this.removed && d * d + d2 * d2 + d3 * d3 > 0.0) {
                this.kill();
                this.dropItem(serverLevel, null);
            }
        }
    }

    public void addAdditionalSaveData(CompoundNBT nbt) {
        BlockPos.CODEC.encodeStart(NBTDynamicOps.INSTANCE, this.pos)
                .resultOrPartial(LOGGER::error)
                .ifPresent(inbt -> nbt.put("block_pos", inbt));
    }

    public void readAdditionalSaveData(CompoundNBT nbt) {
        BlockPos.CODEC.decode(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt.get("block_pos")))
                .resultOrPartial(LOGGER::error)
                .ifPresent(position -> this.pos = position.getFirst());
    }



    public abstract void dropItem(ServerWorld var1, @Nullable Entity var2);

    @Override
    protected boolean repositionEntityAfterLoad() {
        return false;
    }

    @Override
    public void setPos(double d, double d2, double d3) {
        this.pos = BlockPos.containing(d, d2, d3);
        this.recalculateBoundingBox();
        this.hasImpulse = true;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public void thunderHit(ServerWorld serverLevel, LightningBoltEntity lightningBolt) {
    }

    @Override
    public void refreshDimensions() {
    }
}
