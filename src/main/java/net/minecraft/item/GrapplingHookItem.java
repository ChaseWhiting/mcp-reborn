package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.GrapplingHookEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class GrapplingHookItem extends Item {
    public GrapplingHookItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.getGrapplingHook() != null) {
            if (!world.isClientSide) {
                int damage = player.getGrapplingHook().retrieve(itemstack);
                itemstack.hurtAndBreak(damage, player, (p) -> p.broadcastBreakEvent(hand));
                player.setGrapplingHook(null);
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        } else {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
            if (!world.isClientSide) {
                GrapplingHookEntity grapplingHookEntity = new GrapplingHookEntity(player, world);
                grapplingHookEntity.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                world.addFreshEntity(grapplingHookEntity);
                player.setGrapplingHook(grapplingHookEntity);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return ActionResult.sidedSuccess(itemstack, world.isClientSide());
    }
}
