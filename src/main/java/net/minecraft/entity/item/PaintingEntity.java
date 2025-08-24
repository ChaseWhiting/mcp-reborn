package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PaintingEntity extends HangingEntity {
   public PaintingType motive;
   public boolean isFixed = false;

   public PaintingEntity(EntityType<? extends PaintingEntity> p_i50221_1_, World p_i50221_2_) {
      super(p_i50221_1_, p_i50221_2_);
   }

   public PaintingEntity(World p_i45849_1_, BlockPos p_i45849_2_, Direction p_i45849_3_) {
      super(EntityType.PAINTING, p_i45849_1_, p_i45849_2_);
      List<PaintingType> list = Lists.newArrayList();
      int i = 0;

      for(PaintingType paintingtype : Registry.MOTIVE) {
         this.motive = paintingtype;
         this.setDirection(p_i45849_3_);
         if (this.survives()) {
            list.add(paintingtype);
            int j = paintingtype.getWidth() * paintingtype.getHeight();
            if (j > i) {
               i = j;
            }
         }
      }

      if (!list.isEmpty()) {
         Iterator<PaintingType> iterator = list.iterator();

         while(iterator.hasNext()) {
            PaintingType paintingtype1 = iterator.next();
            if (paintingtype1.getWidth() * paintingtype1.getHeight() < i) {
               iterator.remove();
            }
         }

         this.motive = list.get(this.random.nextInt(list.size()));
      }

      this.setDirection(p_i45849_3_);
   }

   public PaintingEntity(World world, BlockPos pos, @Nullable PaintingType predefinedMotive, Direction direction) {
      super(EntityType.PAINTING, world, pos);
      this.setDirection(direction);

      if (predefinedMotive != null) {
         // Use the predefined motive if it survives
         this.motive = predefinedMotive;
         if (!this.survives()) {
            this.motive = null; // Reset if it doesn't fit
         }
      }

      // Fallback to original logic if no valid predefined motive
      if (this.motive == null) {
         List<PaintingType> list = Lists.newArrayList();
         int maxArea = 0;

         for (PaintingType paintingType : Registry.MOTIVE) {
            this.motive = paintingType;
            this.setDirection(direction);
            if (this.survives()) {
               list.add(paintingType);
               int area = paintingType.getWidth() * paintingType.getHeight();
               if (area > maxArea) {
                  maxArea = area;
               }
            }
         }

         if (!list.isEmpty()) {
            int finalMaxArea = maxArea;
            list.removeIf(p -> p.getWidth() * p.getHeight() < finalMaxArea);
            this.motive = list.get(this.random.nextInt(list.size()));
         }
      }

      this.setDirection(direction);
   }

   @OnlyIn(Dist.CLIENT)
   public PaintingEntity(World p_i48559_1_, BlockPos p_i48559_2_, Direction p_i48559_3_, PaintingType p_i48559_4_) {
      this(p_i48559_1_, p_i48559_2_, p_i48559_3_);
      this.motive = p_i48559_4_;
      this.setDirection(p_i48559_3_);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      p_213281_1_.putString("Motive", Registry.MOTIVE.getKey(this.motive).toString());
      p_213281_1_.putBoolean("IsFixed", this.isFixed);
      p_213281_1_.putByte("Facing", (byte)this.direction.get2DDataValue());
      super.addAdditionalSaveData(p_213281_1_);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      this.motive = Registry.MOTIVE.get(ResourceLocation.tryParse(p_70037_1_.getString("Motive")));
      this.direction = Direction.from2DDataValue(p_70037_1_.getByte("Facing"));
      this.isFixed = p_70037_1_.getBoolean("IsFixed");
      super.readAdditionalSaveData(p_70037_1_);
      this.setDirection(this.direction);
   }

   public int getWidth() {
      return this.motive == null ? 1 : this.motive.getWidth();
   }

   public int getHeight() {
      return this.motive == null ? 1 : this.motive.getHeight();
   }

   public void dropItem(@Nullable Entity p_110128_1_) {
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
         if (p_110128_1_ instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)p_110128_1_;
            if (playerentity.abilities.instabuild) {
               return;
            }
         }
         ItemStack painting = new ItemStack(Items.PAINTING);
         if (isFixed) {
            CompoundNBT entityTag = new CompoundNBT();
            entityTag.putString("Motive", this.motive.getName());
            painting.getOrCreateTag().put("EntityTag", entityTag);
            this.spawnAtLocation(painting);
            return;
         }
         this.spawnAtLocation(Items.PAINTING);
      }
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
   }

   public void moveTo(double p_70012_1_, double p_70012_3_, double p_70012_5_, float p_70012_7_, float p_70012_8_) {
      this.setPos(p_70012_1_, p_70012_3_, p_70012_5_);
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpTo(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      BlockPos blockpos = this.pos.offset(p_180426_1_ - this.getX(), p_180426_3_ - this.getY(), p_180426_5_ - this.getZ());
      this.setPos((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnPaintingPacket(this);
   }


   @Override
   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   public void push(double p_70024_1_, double p_70024_3_, double p_70024_5_) {

   }

   @Override
   public void push(Vector3d vec) {

   }

   @Override
   public void push(Entity p_70108_1_) {

   }

   public boolean survives() {
      if (!this.level.noCollision(this)) {
         return false;
      } else {
         int i = Math.max(1, this.getWidth() / 16);
         int j = Math.max(1, this.getHeight() / 16);
         BlockPos blockpos = this.pos.relative(this.direction.getOpposite());
         Direction direction = this.direction.getCounterClockWise();
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         int i1 = (i - 1) / -2;
         int j1 = (j - 1) / -2;

         // Define the four corners
         int[][] corners = {
                 {0, 0},             // bottom-left
                 {i - 1, 0},         // bottom-right
                 {0, j - 1},         // top-left
                 {i - 1, j - 1}      // top-right
         };

         for (int[] offset : corners) {
            int k = offset[0];
            int l = offset[1];
            blockpos$mutable.set(blockpos).move(direction, k + i1).move(Direction.UP, l + j1);
            BlockState blockstate = this.level.getBlockState(blockpos$mutable);
            if (!blockstate.getMaterial().isSolid() && !RedstoneDiodeBlock.isDiode(blockstate)) {
               return false;
            }
         }

         return this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
      }
   }


}