package net.minecraft.entity.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.Pose;
import net.minecraft.entity.leashable.Leashable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LeashKnotEntity extends BlockAttachedEntity {

   public static final double OFFSET_Y = 0.375;

   public LeashKnotEntity(EntityType<? extends LeashKnotEntity> entityType, World level) {
      super(entityType, level);
   }

   public LeashKnotEntity(World level, BlockPos blockPos) {
      super(EntityType.LEASH_KNOT, level, blockPos);
      this.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
      this.forcedLoading = true;
   }

   @Override
   protected void defineSynchedData() {
   }



   @Override
   public boolean shouldRenderAtSqrDistance(double d) {
      return d < 1024.0;
   }

   @Override
   public void dropItem(ServerWorld serverLevel, @Nullable Entity entity) {
      this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0f, 1.0f);
   }

   @Override
   public void addAdditionalSaveData(CompoundNBT valueOutput) {
   }

   @Override
   public void readAdditionalSaveData(CompoundNBT valueInput) {
   }

   @Override
   public ActionResultType interact(PlayerEntity player, Hand hand) {
      ActionResultType resultType;

      if (level.isClientSide) {
         return ActionResultType.SUCCESS;
      }

      if (player.getItemInHand(hand).is(Items.SHEARS)) {
         resultType = super.interact(player, hand);

         return resultType;
      }
      boolean bl = false;

      List<Leashable> leashedList = Leashable.leashableLeashedTo(player);

      Iterator<Leashable> iterator = leashedList.iterator();

      while (iterator.hasNext()) {
         Leashable leashable = iterator.next();
         if (!leashable.canHaveALeashAttachedTo(this)) continue;
         leashable.setLeashedTo(this, true);
         bl = true;
      }
      boolean bl2 = false;

      if (!bl && !player.isSecondaryUseActive()) {
         List<Leashable> leashedToPost = Leashable.leashableLeashedTo(this);
         Iterator<Leashable> iterator2 = leashedToPost.iterator();

         while (iterator2.hasNext()) {
            Leashable leashable = iterator2.next();
            if (!leashable.canHaveALeashAttachedTo(player)) continue;
            leashable.setLeashedTo(player, true);
            bl2 = true;
         }
      }

      if (bl || bl2) {
         this.gameEvent(GameEvent.BLOCK_ATTACH, player);
         this.playPlacementSound();
         return ActionResultType.SUCCESS;
      }

      return super.interact(player, hand);
   }


   @Override
   public void notifyLeasheeRemoved(Leashable leashable) {
      if (Leashable.leashableLeashedTo(this).isEmpty()) {
         this.discard();
      }
   }

   @Override
   public boolean survives() {
      return this.level().getBlockState(this.pos).is(BlockTags.FENCES);
   }

   public static LeashKnotEntity getOrCreateKnot(World level, BlockPos blockPos) {
      int n = blockPos.getX();
      int n2 = blockPos.getY();
      int n3 = blockPos.getZ();
      List<LeashKnotEntity> list = level.getEntitiesOfClass(LeashKnotEntity.class, new AxisAlignedBB((double)n - 1.0, (double)n2 - 1.0, (double)n3 - 1.0, (double)n + 1.0, (double)n2 + 1.0, (double)n3 + 1.0));
      for (LeashKnotEntity leashFenceKnotEntity : list) {
         if (!leashFenceKnotEntity.getPos().equals(blockPos)) continue;
         return leashFenceKnotEntity;
      }
      LeashKnotEntity leashFenceKnotEntity = new LeashKnotEntity(level, blockPos);
      level.addFreshEntity(leashFenceKnotEntity);
      return leashFenceKnotEntity;
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0f, 1.0f);
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this, this.getType(), 0, this.getPos());
   }


   @Override
   public Vector3d getRopeHoldPosition(float f) {
      return this.getPosition(f).add(0.0, 0.2, 0.0);
   }

   @Override
   public ItemStack getPickResult() {
      return new ItemStack(Items.LEAD);
   }










   @Override
   protected void recalculateBoundingBox() {
      this.setPosRaw((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.375, (double)this.pos.getZ() + 0.5);
      double d = (double)this.getType().getWidth() / 2.0;
      double d2 = this.getType().getHeight();
      double yOffset = 0.125; // 2 pixels in Minecraft units
      this.setBoundingBox(new AxisAlignedBB(
              this.getX() - d,
              this.getY() - yOffset,                    // Lowered by 0.125
              this.getZ() - d,
              this.getX() + d,
              this.getY() + d2 - yOffset,               // Same height, just offset down
              this.getZ() + d
      ));
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
}
