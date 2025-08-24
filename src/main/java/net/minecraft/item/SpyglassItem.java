package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SpyglassItem extends Item {
    public static final int USE_DURATION = 1200;
    public static final float ZOOM_FOV_MODIFIER = 0.1f;

    public SpyglassItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 1200;
    }

    @Override
    public UseAction getUseAnimation(ItemStack itemStack) {
        return UseAction.SPYGLASS;
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand interactionHand) {
        player.playSound(SoundEvents.SPYGLASS_USE, 1.0f, 1.0f);
        player.awardStat(Stats.ITEM_USED.get(this));
        return ItemUtils.startUsingInstantly(level, player, interactionHand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, World level, LivingEntity livingEntity) {
        this.stopUsing(livingEntity);
        return itemStack;
    }

    @Override
    public void releaseUsing(ItemStack itemStack, World level, LivingEntity livingEntity, int n) {
        this.stopUsing(livingEntity);
    }

    private void stopUsing(LivingEntity livingEntity) {
        livingEntity.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0f, 1.0f);
    }
}
