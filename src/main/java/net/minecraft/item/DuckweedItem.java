package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;

public class DuckweedItem extends BlockItem {
   public DuckweedItem(Block p_i48456_1_, Properties p_i48456_2_) {
      super(p_i48456_1_, p_i48456_2_);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      return ActionResultType.PASS;
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      BlockRayTraceResult blockraytraceresult = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
      BlockRayTraceResult blockraytraceresult1 = blockraytraceresult.withPosition(blockraytraceresult.getBlockPos().above());
      ActionResultType actionresulttype = super.useOn(new ItemUseContext(player, hand, blockraytraceresult1));
      return new ActionResult<>(actionresulttype, player.getItemInHand(hand));
   }
}