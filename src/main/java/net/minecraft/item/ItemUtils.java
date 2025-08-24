package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemUtils {
    public static ActionResult<ItemStack> startUsingInstantly(World level, PlayerEntity player, Hand interactionHand) {
        player.startUsingItem(interactionHand);
        return ActionResult.consume(player.getItemInHand(interactionHand));
    }
}
