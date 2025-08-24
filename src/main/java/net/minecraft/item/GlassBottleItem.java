package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class GlassBottleItem extends Item {
   public GlassBottleItem(Item.Properties p_i48523_1_) {
      super(p_i48523_1_);
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      List<AreaEffectCloudEntity> list = world.getEntitiesOfClass(AreaEffectCloudEntity.class, player.getBoundingBox().inflate(2.0D), (p_210311_0_) -> {
         return p_210311_0_ != null && p_210311_0_.isAlive() && p_210311_0_.getOwner() instanceof EnderDragonEntity;
      });
      ItemStack itemstack = player.getItemInHand(hand);
      if (!list.isEmpty()) {
         AreaEffectCloudEntity areaeffectcloudentity = list.get(0);
         areaeffectcloudentity.setRadius(areaeffectcloudentity.getRadius() - 0.5F);
         world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
         world.gameEvent(player, GameEvent.FLUID_PICKUP, player.position());
         return ActionResult.sidedSuccess(this.turnBottleIntoItem(itemstack, player, new ItemStack(Items.DRAGON_BREATH)), world.isClientSide());
      } else {
         RayTraceResult raytraceresult = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
         if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.pass(itemstack);
         } else {
            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
               BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getBlockPos();
               if (!world.mayInteract(player, blockpos)) {
                  return ActionResult.pass(itemstack);
               }

               if (world.getFluidState(blockpos).is(FluidTags.WATER)) {
                  world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                  world.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);
                  return ActionResult.sidedSuccess(this.turnBottleIntoItem(itemstack, player, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), world.isClientSide());
               }
            }

            return ActionResult.pass(itemstack);
         }
      }
   }

   protected ItemStack turnBottleIntoItem(ItemStack p_185061_1_, PlayerEntity p_185061_2_, ItemStack p_185061_3_) {
      p_185061_2_.awardStat(Stats.ITEM_USED.get(this));
      return DrinkHelper.createFilledResult(p_185061_1_, p_185061_2_, p_185061_3_);
   }
}