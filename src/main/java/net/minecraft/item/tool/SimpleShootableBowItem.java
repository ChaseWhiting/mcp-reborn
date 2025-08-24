package net.minecraft.item.tool;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.item.tool.bow.BowReleaseInfo;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import java.util.function.Predicate;

public abstract class SimpleShootableBowItem extends ShootableItem implements IVanishable, BowSource {

    public SimpleShootableBowItem(Properties properties) {
        super(properties);
    }

    public static float getPowerForTime(int time, float tickTime) {
        float f = (float) time / tickTime;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public static float getPowerForTime(int time) {
        float f = (float) time / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean flag = !player.getProjectile(itemstack).isEmpty();
        if (!player.abilities.instabuild && !flag) {
            return ActionResult.fail(itemstack);
        } else {
            player.startUsingItem(hand);
            return ActionResult.consume(itemstack);
        }
    }

    public int getDefaultProjectileRange() {
        return 15;
    }

    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_OR_BONE_ARROW;
    }

    public static void addEnchantmentsToArrow(AbstractArrowEntity arrowEntity, ItemStack bowStack, ItemStack projectileStack, float power, PlayerEntity player) {
        boolean infiniteArrows = player.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bowStack) > 0;
        boolean isCreativeOrInfiniteArrow = infiniteArrows && projectileStack.getItem() == Items.ARROW;


        if (power == 1.0F) {
            arrowEntity.setCritArrow(true);
        }

        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bowStack);
        if (powerLevel > 0) {
            arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
        }

        int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bowStack);
        if (punchLevel > 0) {
            arrowEntity.setKnockback(punchLevel);
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, bowStack) > 0) {
            arrowEntity.setSecondsOnFire(100);
        }

        bowStack.hurtAndBreak(1, player, (p_220009_1_) -> {
            p_220009_1_.broadcastBreakEvent(player.getUsedItemHand());
        });

        if (isCreativeOrInfiniteArrow || player.abilities.instabuild && (projectileStack.getItem() == Items.SPECTRAL_ARROW || projectileStack.getItem() == Items.TIPPED_ARROW)) {
            arrowEntity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
        }
    }

    public void releaseUsing(ItemStack bowStack, World world, LivingEntity entity, int timeLeft) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            boolean infiniteArrows = player.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bowStack) > 0;
            ItemStack projectileStack = player.getProjectile(bowStack);

            if (!projectileStack.isEmpty() || infiniteArrows) {
                if (projectileStack.isEmpty()) {
                    projectileStack = new ItemStack(Items.ARROW);
                }

                int drawDuration = this.getUseDuration(bowStack) - timeLeft;
                float power = getPowerForTime(drawDuration);
                if (!((double) power < this.getMinDrawTime())) {
                    boolean isCreativeOrInfiniteArrow = infiniteArrows && projectileStack.getItem() == Items.ARROW;
                    if (!world.isClientSide) {
                        this.onReleaseUsing(new BowReleaseInfo(power, infiniteArrows, player, world, bowStack, projectileStack));
                    }

                    this.playShootSound(world, player, power);

                    if (!isCreativeOrInfiniteArrow && !player.abilities.instabuild) {
                        projectileStack.shrink(1);
                        if (projectileStack.isEmpty()) {
                            player.inventory.removeItem(projectileStack);
                        }

                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    public void onReleaseUsing(BowReleaseInfo info) {
        PlayerEntity player = info.player();
        float power = info.getPower();
        ItemStack bowStack = info.getBowStack();
        ItemStack arrowStack = info.getArrowStack();

        if (!arrowStack.isEmpty() || info.infiniteArrows()) {
            AbstractArrowEntity arrowEntity = this.getArrow(player.level, player, (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW), bowStack, arrowStack, power);

            info.getLevel().addFreshEntity(arrowEntity);
        }
    }

    public AbstractArrowEntity getArrow(World world, PlayerEntity player, ArrowItem arrowItem, ItemStack bowStack, ItemStack arrowStack, float power) {
        AbstractArrowEntity arrowEntity = arrowItem.createArrow(world, arrowStack, player);

        arrowEntity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, power * 3.0F, 1.0F);

        addEnchantmentsToArrow(arrowEntity, bowStack, arrowStack, power, player);

        return arrowEntity;
    }

    public void playShootSound(World world, PlayerEntity player, float power) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + power * 0.5F);
    }

    public double getMinDrawTime() {
        return 0.1D;
    }
}
