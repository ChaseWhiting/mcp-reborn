package net.minecraft.network.datasync;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

public class DataSerializers {
   private static final IntIdentityHashBiMap<IDataSerializer<?>> SERIALIZERS = new IntIdentityHashBiMap<>(17);

   public static final IDataSerializer<Byte> BYTE = new IDataSerializer<Byte>() {
      public void write(PacketBuffer buffer, Byte value) {
         buffer.writeByte(value);
      }

      public Byte read(PacketBuffer buffer) {
         return buffer.readByte();
      }

      public Byte copy(Byte value) {
         return value;
      }
   };

   public static final IDataSerializer<Integer> INT = new IDataSerializer<Integer>() {
      public void write(PacketBuffer buffer, Integer value) {
         buffer.writeVarInt(value);
      }

      public Integer read(PacketBuffer buffer) {
         return buffer.readVarInt();
      }

      public Integer copy(Integer value) {
         return value;
      }
   };

   public static final IDataSerializer<EntityType<?>> ENTITY_TYPE = new IDataSerializer<EntityType<?>>() {
      public void write(PacketBuffer buffer, EntityType<?> value) {
         buffer.writeResourceLocation(Registry.ENTITY_TYPE.getKey(value));
      }

      public EntityType<?> read(PacketBuffer buffer) {
         return Registry.ENTITY_TYPE.get(buffer.readResourceLocation());
      }

      public EntityType<?> copy(EntityType<?> value) {
         return value;
      }
   };

   public static final IDataSerializer<Float> FLOAT = new IDataSerializer<Float>() {
      public void write(PacketBuffer buffer, Float value) {
         buffer.writeFloat(value);
      }

      public Float read(PacketBuffer buffer) {
         return buffer.readFloat();
      }

      public Float copy(Float value) {
         return value;
      }
   };

   public static final IDataSerializer<String> STRING = new IDataSerializer<String>() {
      public void write(PacketBuffer buffer, String value) {
         buffer.writeUtf(value);
      }

      public String read(PacketBuffer buffer) {
         return buffer.readUtf(32767);
      }

      public String copy(String value) {
         return value;
      }
   };

   public static final IDataSerializer<ITextComponent> COMPONENT = new IDataSerializer<ITextComponent>() {
      public void write(PacketBuffer buffer, ITextComponent value) {
         buffer.writeComponent(value);
      }

      public ITextComponent read(PacketBuffer buffer) {
         return buffer.readComponent();
      }

      public ITextComponent copy(ITextComponent value) {
         return value;
      }
   };

   public static final IDataSerializer<Optional<ITextComponent>> OPTIONAL_COMPONENT = new IDataSerializer<Optional<ITextComponent>>() {
      public void write(PacketBuffer buffer, Optional<ITextComponent> value) {
         if (value.isPresent()) {
            buffer.writeBoolean(true);
            buffer.writeComponent(value.get());
         } else {
            buffer.writeBoolean(false);
         }
      }

      public Optional<ITextComponent> read(PacketBuffer buffer) {
         return buffer.readBoolean() ? Optional.of(buffer.readComponent()) : Optional.empty();
      }

      public Optional<ITextComponent> copy(Optional<ITextComponent> value) {
         return value;
      }
   };

   public static final IDataSerializer<ItemStack> ITEM_STACK = new IDataSerializer<ItemStack>() {
      public void write(PacketBuffer buffer, ItemStack value) {
         buffer.writeItem(value);
      }

      public ItemStack read(PacketBuffer buffer) {
         return buffer.readItem();
      }

      public ItemStack copy(ItemStack value) {
         return value.copy();
      }
   };

   public static final IDataSerializer<Optional<BlockState>> BLOCK_STATE = new IDataSerializer<Optional<BlockState>>() {
      public void write(PacketBuffer buffer, Optional<BlockState> value) {
         if (value.isPresent()) {
            buffer.writeVarInt(Block.getId(value.get()));
         } else {
            buffer.writeVarInt(0);
         }
      }

      public Optional<BlockState> read(PacketBuffer buffer) {
         int id = buffer.readVarInt();
         return id == 0 ? Optional.empty() : Optional.of(Block.stateById(id));
      }

      public Optional<BlockState> copy(Optional<BlockState> value) {
         return value;
      }
   };

   public static final IDataSerializer<CustomArrowType> CUSTOM_ARROW_TYPE = new IDataSerializer<CustomArrowType>() {
      public void write(PacketBuffer buffer, CustomArrowType value) {
         buffer.writeUtf(value.getName().toString());
      }

      public CustomArrowType read(PacketBuffer buffer) {
         String name = buffer.readUtf();
         return CustomArrowType.getCustomArrowTypeByName(name);
      }

      public CustomArrowType copy(CustomArrowType value) {
         return value;
      }
   };

   public static final IDataSerializer<Boolean> BOOLEAN = new IDataSerializer<Boolean>() {
      public void write(PacketBuffer buffer, Boolean value) {
         buffer.writeBoolean(value);
      }

      public Boolean read(PacketBuffer buffer) {
         return buffer.readBoolean();
      }

      public Boolean copy(Boolean value) {
         return value;
      }
   };

   public static final IDataSerializer<IParticleData> PARTICLE = new IDataSerializer<IParticleData>() {
      public void write(PacketBuffer buffer, IParticleData value) {
         buffer.writeVarInt(Registry.PARTICLE_TYPE.getId(value.getType()));
         value.writeToNetwork(buffer);
      }

      public IParticleData read(PacketBuffer buffer) {
         return this.readParticle(buffer, Registry.PARTICLE_TYPE.byId(buffer.readVarInt()));
      }

      private <T extends IParticleData> T readParticle(PacketBuffer buffer, ParticleType<T> type) {
         return type.getDeserializer().fromNetwork(type, buffer);
      }

      public IParticleData copy(IParticleData value) {
         return value;
      }
   };

   public static final IDataSerializer<Rotations> ROTATIONS = new IDataSerializer<Rotations>() {
      public void write(PacketBuffer buffer, Rotations value) {
         buffer.writeFloat(value.getX());
         buffer.writeFloat(value.getY());
         buffer.writeFloat(value.getZ());
      }

      public Rotations read(PacketBuffer buffer) {
         return new Rotations(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
      }

      public Rotations copy(Rotations value) {
         return value;
      }
   };

   public static final IDataSerializer<BlockPos> BLOCK_POS = new IDataSerializer<BlockPos>() {
      public void write(PacketBuffer buffer, BlockPos value) {
         buffer.writeBlockPos(value);
      }

      public BlockPos read(PacketBuffer buffer) {
         return buffer.readBlockPos();
      }

      public BlockPos copy(BlockPos value) {
         return value;
      }
   };

   public static final IDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new IDataSerializer<Optional<BlockPos>>() {
      public void write(PacketBuffer buffer, Optional<BlockPos> value) {
         buffer.writeBoolean(value.isPresent());
         if (value.isPresent()) {
            buffer.writeBlockPos(value.get());
         }
      }

      public Optional<BlockPos> read(PacketBuffer buffer) {
         return buffer.readBoolean() ? Optional.of(buffer.readBlockPos()) : Optional.empty();
      }

      public Optional<BlockPos> copy(Optional<BlockPos> value) {
         return value;
      }
   };

   public static final IDataSerializer<Direction> DIRECTION = new IDataSerializer<Direction>() {
      public void write(PacketBuffer buffer, Direction value) {
         buffer.writeEnum(value);
      }

      public Direction read(PacketBuffer buffer) {
         return buffer.readEnum(Direction.class);
      }

      public Direction copy(Direction value) {
         return value;
      }
   };

   public static final IDataSerializer<Optional<UUID>> OPTIONAL_UUID = new IDataSerializer<Optional<UUID>>() {
      public void write(PacketBuffer buffer, Optional<UUID> value) {
         buffer.writeBoolean(value.isPresent());
         if (value.isPresent()) {
            buffer.writeUUID(value.get());
         }
      }

      public Optional<UUID> read(PacketBuffer buffer) {
         return buffer.readBoolean() ? Optional.of(buffer.readUUID()) : Optional.empty();
      }

      public Optional<UUID> copy(Optional<UUID> value) {
         return value;
      }
   };

   public static final IDataSerializer<CompoundNBT> COMPOUND_TAG = new IDataSerializer<CompoundNBT>() {
      public void write(PacketBuffer buffer, CompoundNBT value) {
         buffer.writeNbt(value);
      }

      public CompoundNBT read(PacketBuffer buffer) {
         return buffer.readNbt();
      }

      public CompoundNBT copy(CompoundNBT value) {
         return value.copy();
      }
   };

   public static final IDataSerializer<VillagerData> VILLAGER_DATA = new IDataSerializer<VillagerData>() {
      public void write(PacketBuffer buffer, VillagerData value) {
         buffer.writeVarInt(Registry.VILLAGER_TYPE.getId(value.getType()));
         buffer.writeVarInt(Registry.VILLAGER_PROFESSION.getId(value.getProfession()));
         buffer.writeVarInt(value.getLevel());
      }

      public VillagerData read(PacketBuffer buffer) {
         return new VillagerData(
                 Registry.VILLAGER_TYPE.byId(buffer.readVarInt()),
                 Registry.VILLAGER_PROFESSION.byId(buffer.readVarInt()),
                 buffer.readVarInt()
         );
      }

      public VillagerData copy(VillagerData value) {
         return value;
      }
   };

   public static final IDataSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT = new IDataSerializer<OptionalInt>() {
      public void write(PacketBuffer buffer, OptionalInt value) {
         buffer.writeVarInt(value.orElse(-1) + 1);
      }

      public OptionalInt read(PacketBuffer buffer) {
         int value = buffer.readVarInt();
         return value == 0 ? OptionalInt.empty() : OptionalInt.of(value - 1);
      }

      public OptionalInt copy(OptionalInt value) {
         return value;
      }
   };

   public static final IDataSerializer<Pose> POSE = new IDataSerializer<Pose>() {
      public void write(PacketBuffer buffer, Pose value) {
         buffer.writeEnum(value);
      }

      public Pose read(PacketBuffer buffer) {
         return buffer.readEnum(Pose.class);
      }

      public Pose copy(Pose value) {
         return value;
      }
   };

   public static void registerSerializer(IDataSerializer<?> serializer) {
      SERIALIZERS.add(serializer);
   }

   @Nullable
   public static IDataSerializer<?> getSerializer(int id) {
      return SERIALIZERS.byId(id);
   }

   public static int getSerializedId(IDataSerializer<?> serializer) {
      return SERIALIZERS.getId(serializer);
   }

   static {
      registerSerializer(BYTE);
      registerSerializer(CUSTOM_ARROW_TYPE);
      registerSerializer(INT);
      registerSerializer(FLOAT);
      registerSerializer(STRING);
      registerSerializer(COMPONENT);
      registerSerializer(OPTIONAL_COMPONENT);
      registerSerializer(ITEM_STACK);
      registerSerializer(BOOLEAN);
      registerSerializer(ROTATIONS);
      registerSerializer(BLOCK_POS);
      registerSerializer(OPTIONAL_BLOCK_POS);
      registerSerializer(DIRECTION);
      registerSerializer(OPTIONAL_UUID);
      registerSerializer(BLOCK_STATE);
      registerSerializer(COMPOUND_TAG);
      registerSerializer(PARTICLE);
      registerSerializer(VILLAGER_DATA);
      registerSerializer(OPTIONAL_UNSIGNED_INT);
      registerSerializer(POSE);
      registerSerializer(ENTITY_TYPE);
   }
}
