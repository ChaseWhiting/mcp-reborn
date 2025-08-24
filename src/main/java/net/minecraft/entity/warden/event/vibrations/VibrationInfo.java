package net.minecraft.entity.warden.event.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.util.UUIDCodec;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class VibrationInfo {

    public GameEvent gameEvent;
    public float distance;
    public Vector3d pos;
    @Nullable
    UUID uuid;
    @Nullable
    UUID projectileOwnerUUID;
    @Nullable
    Entity entity;

    public static final Codec<VibrationInfo> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Registry.GAME_EVENT.fieldOf("game_event")
                    .forGetter(VibrationInfo::gameEvent),
                    Codec.floatRange((float)0.0f, (float)Float.MAX_VALUE)
                            .fieldOf("distance").forGetter(VibrationInfo::distance),
                    Vector3d.CODEC.fieldOf("pos").forGetter(VibrationInfo::pos),
                    UUIDCodec.CODEC.optionalFieldOf("source").forGetter(vibrationInfo ->
                            Optional.ofNullable(vibrationInfo.uuid())), UUIDCodec.CODEC.optionalFieldOf("projectile_owner")
                            .forGetter(vibrationInfo -> Optional.ofNullable(vibrationInfo.projectileOwnerUuid())))
                    .apply(instance, (gameEvent, f, vec3, optional, optional2) ->
                            new VibrationInfo((GameEvent)gameEvent, f.floatValue(), (Vector3d)vec3, optional.orElse(null), optional2.orElse(null))));

    public VibrationInfo(GameEvent gameEvent, float f, Vector3d vector3D, UUID uuid, UUID projectileOwner, Entity entity) {
        this.gameEvent = gameEvent;
        this.distance = f;
        this.pos = vector3D;
        this.uuid = uuid;
        this.projectileOwnerUUID = projectileOwner;
        this.entity = entity;
    }


    public GameEvent gameEvent() {
        return gameEvent;
    }

    public float distance() {
        return distance;
    }

    public Vector3d pos() {
        return pos;
    }

    public UUID uuid() {
        return uuid;
    }

    public UUID projectileOwnerUuid() {
        return projectileOwnerUUID;
    }



    public VibrationInfo(GameEvent gameEvent, float f, Vector3d vector3D, @Nullable UUID uUID, @Nullable UUID uUID2) {
        this(gameEvent, f, vector3D, uUID, uUID2, null);
    }

    public VibrationInfo(GameEvent gameEvent, float f, Vector3d vector3D, @Nullable Entity entity) {
        this(gameEvent, f, vector3D, entity == null ? null : entity.getUUID(), VibrationInfo.getProjectileOwner(entity), entity);
    }

    @Nullable
    private static UUID getProjectileOwner(@Nullable Entity entity) {
        ProjectileEntity projectile;
        if (entity instanceof ProjectileEntity && (projectile = (ProjectileEntity)entity).getOwner() != null) {
            return projectile.getOwner().getUUID();
        }
        return null;
    }

    public Optional<Entity> getEntity(ServerWorld serverWorld) {
        return Optional.ofNullable(this.entity).or(() -> Optional.ofNullable(this.uuid).map(serverWorld::getEntity));
    }

    public Optional<Entity> getProjectileOwner(ServerWorld serverWorld) {
        return this.getEntity(serverWorld).filter(entity -> entity instanceof ProjectileEntity).map(entity -> (ProjectileEntity)entity).map(ProjectileEntity::getOwner).or(() -> Optional.ofNullable(this.projectileOwnerUUID).map(serverWorld::getEntity));
    }
}
