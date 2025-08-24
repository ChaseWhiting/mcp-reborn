package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Mob;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.leashable.Leashable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class LeadItem extends Item {
   public LeadItem(Item.Properties p_i48484_1_) {
      super(p_i48484_1_);
   }

   @Override
   public ActionResultType useOn(ItemUseContext useOnContext) {
      BlockPos blockPos;
      World level = useOnContext.getLevel();
      BlockState blockState = level.getBlockState(blockPos = useOnContext.getClickedPos());
      if (blockState.is(BlockTags.FENCES)) {
         PlayerEntity player = useOnContext.getPlayer();
         if (!level.isClientSide && player != null) {
            return LeadItem.bindPlayerMobs(player, level, blockPos);
         }
      }
      return ActionResultType.PASS;
   }

   public static ActionResultType bindPlayerMobs(PlayerEntity player, World level, BlockPos blockPos) {
      LeashKnotEntity leashFenceKnotEntity = null;
      List<Leashable> list = Leashable.leashableInArea(level, Vector3d.atCenterOf(blockPos), leashable -> leashable.getLeashHolder() == player);
      boolean bl = false;
      for (Leashable leashable2 : list) {
         if (leashFenceKnotEntity == null) {
            leashFenceKnotEntity = LeashKnotEntity.getOrCreateKnot(level, blockPos);
            leashFenceKnotEntity.playPlacementSound();
         }
         if (!leashable2.canHaveALeashAttachedTo(leashFenceKnotEntity)) continue;
         leashable2.setLeashedTo(leashFenceKnotEntity, true);
         bl = true;
      }
      if (bl) {
         level.gameEvent(GameEvent.BLOCK_ATTACH, blockPos, GameEvent.Context.of(player));
         return ActionResultType.SUCCESS;
      }
      return ActionResultType.PASS;
   }
}