package net.minecraft.entity.ai.controller;

import net.minecraft.block.Block;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WoodpeckerFlyingController extends FlyingMovementController {
   private final World world;

   public WoodpeckerFlyingController(Mob mob, int maxTurn, boolean hoversInPlace, World world) {
      super(mob, maxTurn, hoversInPlace);
      this.world = world;
   }

   @Override
   public void tick() {
      if (this.operation == MovementController.Action.MOVE_TO) {
         this.operation = MovementController.Action.WAIT;
         this.mob.setNoGravity(true);

         // Calculate desired movement direction
         double d0 = this.wantedX - this.mob.getX();
         double d1 = this.wantedY - this.mob.getY();
         double d2 = this.wantedZ - this.mob.getZ();
         double d3 = d0 * d0 + d1 * d1 + d2 * d2;

         if (d3 < 2.5E-7) {
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
            return;
         }

         // Validate the target position before moving
         BlockPos targetPos = new BlockPos(this.wantedX, this.wantedY, this.wantedZ);
         if (!isValidTarget(targetPos)) {
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
            return;
         }

         float f = (float) (MathHelper.atan2(d2, d0) * (180F / Math.PI)) - 90.0F;
         this.mob.yRot = this.rotlerp(this.mob.yRot, f, 90.0F);
         float speed = (float) (this.speedModifier * this.mob.getAttributeValue(this.mob.isOnGround()
                 ? Attributes.MOVEMENT_SPEED : Attributes.FLYING_SPEED));
         this.mob.setSpeed(speed);

         double d4 = MathHelper.sqrt(d0 * d0 + d2 * d2);
         float pitch = (float) (-(MathHelper.atan2(d1, d4) * (180F / Math.PI)));
         this.mob.xRot = this.rotlerp(this.mob.xRot, pitch, this.maxTurn);
         this.mob.setYya(d1 > 0.0 ? speed : -speed);
      } else {
         if (!this.hoversInPlace) {
            this.mob.setNoGravity(false);
         }
         this.mob.setYya(0.0F);
         this.mob.setZza(0.0F);
      }
   }

   // Validates if the target position is a valid block like a log
   private boolean isValidTarget(BlockPos pos) {
      Block block = this.world.getBlockState(pos).getBlock();
      return block.is(BlockTags.LOGS_THAT_BURN);
   }
}
