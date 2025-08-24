package net.minecraft.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowEntity;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowType;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.item.tool.TerrariaBowItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ProjectileEntity extends Entity {
   private UUID ownerUUID;
   private int ownerNetworkId;
   private boolean leftOwner;
   private boolean hasBeenShot;

   ProjectileEntity(EntityType<? extends ProjectileEntity> p_i231584_1_, World p_i231584_2_) {
      super(p_i231584_1_, p_i231584_2_);
   }

   public void setOwner(@Nullable Entity p_212361_1_) {
      if (p_212361_1_ != null) {
         this.ownerUUID = p_212361_1_.getUUID();
         this.ownerNetworkId = p_212361_1_.getId();
      }

   }

   @Nullable
   public Entity getOwner() {
      if (this.ownerUUID != null && this.level instanceof ServerWorld) {
         return ((ServerWorld)this.level).getEntity(this.ownerUUID);
      } else {
         return this.ownerNetworkId != 0 ? this.level.getEntity(this.ownerNetworkId) : null;
      }
   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      if (this.ownerUUID != null) {
         p_213281_1_.putUUID("Owner", this.ownerUUID);
      }

      if (this.leftOwner) {
         p_213281_1_.putBoolean("LeftOwner", true);
      }
      p_213281_1_.putBoolean("HasBeenShot", this.hasBeenShot);

   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      if (p_70037_1_.hasUUID("Owner")) {
         this.ownerUUID = p_70037_1_.getUUID("Owner");
      }

      this.leftOwner = p_70037_1_.getBoolean("LeftOwner");
      this.hasBeenShot = p_70037_1_.getBoolean("HasBeenShot");
   }

   public void tick() {
      if (!this.hasBeenShot) {
         gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
         this.hasBeenShot = true;
      }
      if (!this.leftOwner) {
         this.leftOwner = this.checkLeftOwner();
      }

      super.tick();
   }

   private boolean checkLeftOwner() {
      Entity entity = this.getOwner();
      if (entity != null) {
         for(Entity entity1 : this.level.getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_234613_0_) -> {
            return !p_234613_0_.isSpectator() && p_234613_0_.isPickable();
         })) {
            if (entity1.getRootVehicle() == entity.getRootVehicle()) {
               return false;
            }
         }
      }

      return true;
   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      Vector3d vector3d = (new Vector3d(x, y, z)).normalize().add(this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.random.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale((double)velocity);
      this.setDeltaMovement(vector3d);
      float f = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d));
      this.yRot = (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI));
      this.xRot = (float)(MathHelper.atan2(vector3d.y, (double)f) * (double)(180F / (float)Math.PI));
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   public void shootFromRotation(Entity entity, float xRotation, float yRotation, float z, float velocity, float inaccuracy) {

      if (this instanceof CustomArrowEntity && (((CustomArrowEntity)this).getArrowType() == CustomArrowType.JESTER)) {
         velocity = TerrariaBowItem.getVelocityInMCScale(0.6f, 6);
      }

      float f = -MathHelper.sin(yRotation * ((float)Math.PI / 180F)) * MathHelper.cos(xRotation * ((float)Math.PI / 180F));
      float f1 = -MathHelper.sin((xRotation + z) * ((float)Math.PI / 180F));
      float f2 = MathHelper.cos(yRotation * ((float)Math.PI / 180F)) * MathHelper.cos(xRotation * ((float)Math.PI / 180F));
      this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
      Vector3d vector3d = entity.getDeltaMovement();
      this.setDeltaMovement(this.getDeltaMovement().add(vector3d.x, entity.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
   }

   protected void onHit(RayTraceResult p_70227_1_) {
      RayTraceResult.Type raytraceresult$type = p_70227_1_.getType();
      if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
         this.onHitEntity((EntityRayTraceResult)p_70227_1_);
         this.level.gameEvent(GameEvent.PROJECTILE_LAND, p_70227_1_.getLocation(), GameEvent.Context.of(this, null));
      } else if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
         BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) p_70227_1_;
         this.onHitBlock(blockRayTraceResult);
         BlockPos pos = blockRayTraceResult.getBlockPos();
         this.level.gameEvent(GameEvent.PROJECTILE_LAND, pos, GameEvent.Context.of(this, this.level.getBlockState(pos)));
      }

   }

   protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
   }

   protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
      BlockState blockstate = this.level.getBlockState(p_230299_1_.getBlockPos());
      blockstate.onProjectileHit(this.level, blockstate, p_230299_1_, this);
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpMotion(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.setDeltaMovement(p_70016_1_, p_70016_3_, p_70016_5_);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
         this.xRot = (float)(MathHelper.atan2(p_70016_3_, (double)f) * (double)(180F / (float)Math.PI));
         this.yRot = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * (double)(180F / (float)Math.PI));
         this.xRotO = this.xRot;
         this.yRotO = this.yRot;
         this.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
      }

   }

   protected boolean canHitEntity(Entity p_230298_1_) {
      if (!p_230298_1_.isSpectator() && p_230298_1_.isAlive() && p_230298_1_.isPickable()) {
         Entity entity = this.getOwner();
         return entity == null || this.leftOwner || !entity.isPassengerOfSameVehicle(p_230298_1_);
      } else {
         return false;
      }
   }

   protected void updateRotation() {
      Vector3d vector3d = this.getDeltaMovement();
      float f = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d));
      this.xRot = lerpRotation(this.xRotO, (float)(MathHelper.atan2(vector3d.y, (double)f) * (double)(180F / (float)Math.PI)));
      this.yRot = lerpRotation(this.yRotO, (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI)));
   }

   protected static float lerpRotation(float p_234614_0_, float p_234614_1_) {
      while(p_234614_1_ - p_234614_0_ < -180.0F) {
         p_234614_0_ -= 360.0F;
      }

      while(p_234614_1_ - p_234614_0_ >= 180.0F) {
         p_234614_0_ += 360.0F;
      }

      return MathHelper.lerp(0.2F, p_234614_0_, p_234614_1_);
   }
}