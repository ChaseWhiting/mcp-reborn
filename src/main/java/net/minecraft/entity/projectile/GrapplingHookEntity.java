package net.minecraft.entity.projectile;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.play.server.SSpawnObjectPacket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GrapplingHookEntity extends ProjectileEntity {
    private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.defineId(GrapplingHookEntity.class, DataSerializers.INT);
    private Entity hookedIn;
    private boolean isHooked;
    private final PlayerEntity owner;
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean isSwinging;

    private int breakTime;
    private int breakTimeDis;
    private boolean lower;

    public GrapplingHookEntity(EntityType<? extends GrapplingHookEntity> entityType, World world) {
        super(entityType, world);
        this.owner = null;
        this.noCulling = true;
        this.noPhysics = false;
        this.breakTime = 100;
        this.breakTimeDis = 200;
    }

    public GrapplingHookEntity(PlayerEntity owner, World world) {
        super(EntityType.GRAPPLING_HOOK_ENTITY, world);
        this.setOwner(owner);
        this.noCulling = true;
        this.owner = owner;
        this.noPhysics = false;
        this.breakTime = 100;
        this.breakTimeDis = 200;
        float pitch = owner.xRot;
        float yaw = owner.yRot;
        float f2 = MathHelper.cos(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-pitch * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-pitch * ((float)Math.PI / 180F));
        double d0 = owner.getX() - (double)f3 * 0.3D;
        double d1 = owner.getEyeY();
        double d2 = owner.getZ() - (double)f2 * 0.3D;
        this.setPos(d0, d1, d2);
        Vector3d vector3d = new Vector3d((double)(-f3), (double)MathHelper.clamp(-(f5 / f4), -5.0F, 5.0F), (double)(-f2));
        double d3 = vector3d.length();
        vector3d = vector3d.multiply(1.5D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 1.5D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 1.5D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D);        this.setDeltaMovement(vector3d);
        this.yRot = (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI));
        this.xRot = (float)(MathHelper.atan2(vector3d.y, (double)MathHelper.sqrt(getHorizontalDistanceSqr(vector3d))) * (double)(180F / (float)Math.PI));
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_HOOKED_ENTITY, 0);
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> key) {
        if (DATA_HOOKED_ENTITY.equals(key)) {
            int id = this.getEntityData().get(DATA_HOOKED_ENTITY);
            this.hookedIn = id > 0 ? this.level.getEntity(id - 1) : null;
        }
        super.onSyncedDataUpdated(key);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0D;
    }

    public int retrieve(ItemStack itemStack) {
        if (this.level.isClientSide) {
            return 0;
        } else {
            this.remove();
            return 1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posIncrements, boolean teleport) {}

    @Override
    public void tick() {
        super.tick();
        PlayerEntity player = this.getOwner();
        if (player == null) {
            LOGGER.warn("Grappling hook removed: no owner found.");
            this.remove();
            return;
        }

        if ((this.onGround || this.horizontalCollision)) {
            this.setDeltaMovement(Vector3d.ZERO);
            this.isHooked = true;
        } else {
            this.isHooked = false;
        }

        if (this.level.isClientSide) {
            LOGGER.debug("Client-side tick, skipping removal checks.");
        } else if (!player.isAlive()) {
            LOGGER.warn("Grappling hook removed: owner is not alive.");
            this.remove();
            return;
        } else if (this.distanceToSqr(player) > 3000.0D) {
            if (breakTimeDis > 0) {
                --breakTimeDis;
            } else {
                LOGGER.warn("Grappling hook removed: owner is too far away.");
                player.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
                this.remove();
                return;
            }
        }

        // Check if the grappling hook can see the player
        if (!canSeePlayer(player)) {
            if (this.breakTime > 0) {
                --this.breakTime;
            } else {
                LOGGER.debug("Grappling hook cannot see the player, removing hook.");
                player.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
                this.remove();
                return;
            }
        }



        // Check if player is holding the grappling hook item
        boolean isHoldingGrapplingHook = player.getMainHandItem().getItem() == Items.GRAPPLING_HOOK || player.getOffhandItem().getItem() == Items.GRAPPLING_HOOK;

        if (this.isHooked) {
            if (this.hookedIn != null) {
                LOGGER.debug("Grappling hook is hooked to an entity.");
                this.pullEntityTowardsPlayer();
            } else if (isHoldingGrapplingHook) {
                this.swingPlayerTowardsHook();
            } else {
                LOGGER.debug("Player is not holding the grappling hook, removing hook.");
                this.remove();
            }
        } else {
            Vector3d motion = this.getDeltaMovement();
            motion = motion.add(0, -0.03, 0); // Apply gravity
            LOGGER.debug("Grappling hook in motion: {}", motion);
            this.move(MoverType.SELF, motion);
            this.setDeltaMovement(motion.scale(0.92D));
            this.checkCollision();
        }

        this.updateRotation();
    }

    private void checkCollision() {
        RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
        this.onHit(raytraceresult);
    }

    private boolean canSeePlayer(PlayerEntity player) {
        Vector3d start = this.position();
        Vector3d end = player.position().add(0, player.getEyeHeight(), 0);


        RayTraceContext context = new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this);


        RayTraceResult result = this.level.clip(context);


        return result.getType() == RayTraceResult.Type.MISS || result.getLocation().distanceTo(end) < 1.0;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return entity != this.owner && super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        if (!this.level.isClientSide) {
            this.hookedIn = result.getEntity();
            this.isHooked = true;
            LOGGER.debug("Grappling hook hit entity: {}", this.hookedIn);
            this.setHookedEntity();
        }
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult result) {
        super.onHitBlock(result);
        if (!this.level.isClientSide) {
            this.isHooked = true;
            LOGGER.debug("Grappling hook hit block at: {}", result.getBlockPos());
            this.setDeltaMovement(Vector3d.ZERO);
        }
    }

    private void setHookedEntity() {
        this.getEntityData().set(DATA_HOOKED_ENTITY, this.hookedIn == null ? 0 : this.hookedIn.getId() + 1);
    }

    protected void pullEntityTowardsPlayer() {
        if (this.hookedIn != null) {
            if (!this.hookedIn.isAlive())
                this.remove();

            Vector3d pull = new Vector3d(this.owner.getX() - this.hookedIn.getX(), this.owner.getY() - this.hookedIn.getY(), this.owner.getZ() - this.hookedIn.getZ()).scale(0.05D);
            this.hookedIn.setDeltaMovement(this.hookedIn.getDeltaMovement().add(pull));

            this.setPos(hookedIn.getX(), hookedIn.getY(0.8), hookedIn.getZ());
        } else {
            this.remove();
        }
    }

    protected void swingPlayerTowardsHook() {
        Vector3d hookPosition = this.position();
        Vector3d playerPosition = this.owner.position();
        Vector3d direction = hookPosition.subtract(playerPosition).normalize();
        double distance = hookPosition.distanceTo(playerPosition);
        // Ensure the player doesn't take fall damage
        this.owner.fallDistance = 0;

        // Add upward motion if the player is below the hook
        if (this.owner.getY() < this.getY()) {
            direction.add(0, 0.2, 0);
        }

        // Apply the swinging motion
        this.owner.setDeltaMovement(this.owner.getDeltaMovement().add(direction.scale(0.1)));

        // Set isSwinging flag if the player is swinging
        if (distance > 1.0) {
            this.isSwinging = true;
        } else {
            this.isSwinging = false;
            this.remove();
        }
    }

    @Nullable
    @Override
    public PlayerEntity getOwner() {
        Entity entity = super.getOwner();
        return entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
    }

    @Nullable
    public Entity getHookedIn() {
        return this.hookedIn;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new SSpawnObjectPacket(this, entity == null ? this.getId() : entity.getId());
    }
}
