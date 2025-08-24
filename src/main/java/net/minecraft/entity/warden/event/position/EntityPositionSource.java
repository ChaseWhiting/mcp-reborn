
package net.minecraft.entity.warden.event.position;

import com.mojang.datafixers.util.Either;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.UUIDCodec;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;


public class EntityPositionSource
implements PositionSource {
    public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(UUIDCodec.CODEC.fieldOf("source_entity")
                    .forGetter(EntityPositionSource::getUuid), Codec.FLOAT.fieldOf("y_offset")
                    .orElse(0.0f).forGetter(
                            entityPositionSource -> entityPositionSource.yOffset))
                    .apply(instance, (uUID, f) ->
                            new EntityPositionSource(Either.right(Either.left(uUID)), f)));
    private Either<Entity, Either<UUID, Integer>> entityOrUuidOrId;
    final float yOffset;

    public EntityPositionSource(Entity entity, float f) {
        this(Either.left(entity), f);
    }

    EntityPositionSource(Either<Entity, Either<UUID, Integer>> either, float f) {
        this.entityOrUuidOrId = either;
        this.yOffset = f;
    }

    @Override
    public Optional<Vector3d> getPosition(World level) {
        if (this.entityOrUuidOrId.left().isEmpty()) {
            this.resolveEntity(level);
        }
        return this.entityOrUuidOrId.left().map(entity -> entity.position().add(0.0, this.yOffset, 0.0));
    }

    private void resolveEntity(World level) {
        (this.entityOrUuidOrId.map(Optional::of, either -> Optional.ofNullable((Entity)either.map(uUID -> {
            Entity entity;
            if (level instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)level;
                entity = serverWorld.getEntity((UUID)uUID);
            } else {
                entity = null;
            }
            return entity;
        }, level::getEntity)))).ifPresent(entity -> {
            this.entityOrUuidOrId = Either.left(entity);
        });
    }

    private UUID getUuid() {
        return (UUID)this.entityOrUuidOrId.map(Entity::getUUID, either -> (UUID)either.map(Function.identity(), n -> {
            throw new RuntimeException("Unable to get entityId from uuid");
        }));
    }

    int getId() {
        return (Integer)this.entityOrUuidOrId.map(Entity::getId, either -> (Integer)either.map(uUID -> {
            throw new IllegalStateException("Unable to get entityId from uuid");
        }, Function.identity()));
    }

    @Override
    public PositionSourceType<?> getType() {
        return PositionSourceType.ENTITY;
    }

    public static class Type
    implements PositionSourceType<EntityPositionSource> {
        @Override
        public EntityPositionSource read(PacketBuffer friendlyByteBuf) {
            return new EntityPositionSource(Either.right(Either.right(friendlyByteBuf.readVarInt())), friendlyByteBuf.readFloat());
        }

        @Override
        public void write(PacketBuffer friendlyByteBuf, EntityPositionSource entityPositionSource) {
            friendlyByteBuf.writeVarInt(entityPositionSource.getId());
            friendlyByteBuf.writeFloat(entityPositionSource.yOffset);
        }

        @Override
        public Codec<EntityPositionSource> codec() {
            return CODEC;
        }
    }
}

