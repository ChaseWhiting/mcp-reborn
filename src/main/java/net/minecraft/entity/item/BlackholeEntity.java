package net.minecraft.entity.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlackholeEntity extends ProjectileItemEntity {
   private static final DataParameter<Float> SIZE = EntityDataManager.defineId(BlackholeEntity.class, DataSerializers.FLOAT);
   private static final List<UUID> SWALLOWED_MOBS = new ArrayList<>();
   public int time;
   public Item defaultItem;
   private EntitySize dimensions = new EntitySize(this.getSize(),this.getSize(), false);

   public BlackholeEntity(EntityType<? extends BlackholeEntity> type, World world) {
      super(type, world);
      this.blocksBuilding = true;
   }

   public BlackholeEntity(World world, double x, double y, double z) {
      this(EntityType.BLACKHOLE, world);
      this.setPos(x, y, z);
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return Mob.createMobAttributes()
              .add(Attributes.MAX_HEALTH, 40.0D);
   }

   @Override
   protected Item getDefaultItem() {
      return defaultItem;
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      defaultItem = Items.AIR;
      this.entityData.define(SIZE, 0.5F);
   }

   public void setDefaultItem(String string) {
      ResourceLocation location = ResourceLocation.tryParse(string);
      Item item = Registry.ITEM.get(location);
      this.setItem(new ItemStack(item));
   }



   @Override
   public void tick() {
      //super.tick();
      ++this.time;
      if (this.level instanceof ServerWorld) {
         this.getDimensions().scale(this.getSize());
         float scale = this.getSize();
         List<Entity> nearbyMobs = this.level.getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(5D + .5D * scale), entity -> entity != this && !entity.isSpectator() && !(entity instanceof PlayerEntity));

         if (!nearbyMobs.isEmpty()) {
            for (Entity entity : nearbyMobs) {
               double x = this.getX() - entity.getX();
               double y = this.getY() - entity.getY();
               double z = this.getZ() - entity.getZ();

               double distance = Math.sqrt(
                       Math.pow(this.getX() - entity.getX(), 2) +
                               Math.pow(this.getY() - entity.getY(), 2) +
                               Math.pow(this.getZ() - entity.getZ(), 2)
               );
               float radius = scale / 2.0f;



               // Check if the entity is close enough to the black hole
               if (distance != 0 && distance <= radius && entity.isAlive()) {
                  // Remove the entity from the world
                  if(entity instanceof PlayerEntity && !((PlayerEntity)entity).isCreative()) {
                     entity.hurt(DamageSource.OUT_OF_WORLD, 5);
                     this.setSize(this.getSize() + 0.00000000001F);
                  } else {
                     entity.remove();
                     this.setSize(this.getSize() + 1);
                  }
                  // Increase the size of the black hole
               } else {
                  // Avoid division by zero
                  if (distance > 0) {
                     // Calculate the scale of the force. Adjust the gravitational constant as needed.
                     float gravitationalConstant = 0.05f; // You may need to tweak this value for desired effect
                     float vecScale = gravitationalConstant / (float) distance;

                     Vector3d vector3d = new Vector3d(x, y, z).normalize().scale(vecScale);

                     // Apply the force to the entity
                     entity.setDeltaMovement(entity.getDeltaMovement().add(vector3d));
                     entity.hurtMarked = true;
                     entity.hasImpulse = true;
                  }
               }
            }
         }

      }
   }


   public float getSize() {
      return this.entityData.get(SIZE);
   }

   public void setSize(float size) {
      this.entityData.set(SIZE, size);
   }


   @Override
   public void addAdditionalSaveData(CompoundNBT compound) {
      super.addAdditionalSaveData(compound);
      compound.putFloat("Size", this.getSize());
      compound.putString("Item", this.getDefaultItem().getRegistryName());
   }

   @Override
   public void readAdditionalSaveData(CompoundNBT compound) {
      super.readAdditionalSaveData(compound);
      if (compound.contains("Size")) {
         this.setSize(compound.getFloat("Size"));
      }
      if(compound.contains("Item")) {
         this.setDefaultItem(compound.getString("Item"));
      }
   }

   @Override
   public boolean hurt(DamageSource source, float amount) {
      return source.isCreativePlayer();
   }

   @Override
   public void kill() {
      super.kill();
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean shouldRenderAtSqrDistance(double distance) {
      return true;
   }

   public void travel(Vector3d vec) {
      vec = new Vector3d(0,0,0);
   }

}
