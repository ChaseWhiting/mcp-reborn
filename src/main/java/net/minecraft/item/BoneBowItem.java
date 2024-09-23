package net.minecraft.item;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.BoneArrowEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;

public class BoneBowItem extends BowItem implements IVanishable {

    private static final float MAX_POWER = 1.3F;
    private static final int DEFAULT_USE_DURATION = 72000;
    private static final int SCARED_MOB_RADIUS = 12;
    private static final float SCARED_MOB_SPEED = 1.12F;

    public BoneBowItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack item, World world, LivingEntity entity, int releaseTime) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) entity;
        boolean hasInfinityEnchantment = player.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, item) > 0;
        ItemStack arrowStack = player.getProjectile(item);

        if (arrowStack.isEmpty() && !hasInfinityEnchantment) {
            return;
        }

        if (arrowStack.isEmpty()) {
            arrowStack = new ItemStack(Items.ARROW);
        }

        int chargeTime = this.getUseDuration(item) - releaseTime;
        float power = getPowerForTime(chargeTime);

        if (power < 0.1D) {
            return;
        }

        if (!world.isClientSide) {
            AbstractArrowEntity arrowEntity = createArrowEntity(world, player, arrowStack, power);
            customizeArrowEntity(arrowEntity, item, power, player);
            world.addFreshEntity(arrowEntity);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, getPitch(power));

            handleArrowUsage(player, item, arrowStack, arrowEntity, world);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
    }

    private AbstractArrowEntity createArrowEntity(World world, PlayerEntity player, ItemStack arrowStack, float power) {
        ArrowItem arrowItem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.BONE_ARROW);
        AbstractArrowEntity arrowEntity = arrowItem.createArrow(world, arrowStack, player);
        arrowEntity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, power * 3.0F, 1.0F);

        if (arrowStack.getItem() == Items.BONE_ARROW && power >= 1F) {
            scareNearbyMobs(player, world);
        }

        return arrowEntity;
    }

    private void customizeArrowEntity(AbstractArrowEntity arrowEntity, ItemStack bow, float power, PlayerEntity player) {
        boolean isBoneArrow = arrowEntity instanceof BoneArrowEntity;
        if (power >= MAX_POWER) {
            power = MAX_POWER;
        }

        arrowEntity.setCritArrow(true);
        arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() * power);

        int powerEnchantment = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bow);
        if (powerEnchantment > 0) {
            arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + (double) powerEnchantment * 0.5D + 1.0D);
        }

        int punchEnchantment = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bow);
        if (punchEnchantment > 0) {
            arrowEntity.setKnockback(punchEnchantment);
        }

        int marrowEnchantment = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MARROW_QUIVER, bow);
        if (marrowEnchantment > 0) {
            arrowEntity.setCritArrow(isBoneArrow);
        }

        arrowEntity.setBoundingBox(new AxisAlignedBB(
                arrowEntity.getX() - 0.285F, arrowEntity.getY() - 0.285F, arrowEntity.getZ() - 0.285F,
                arrowEntity.getX() + 0.285F, arrowEntity.getY() + 0.285F, arrowEntity.getZ() + 0.285F));
    }

    private void scareNearbyMobs(PlayerEntity player, World world) {
        AxisAlignedBB area = new AxisAlignedBB(player.blockPosition()).inflate(SCARED_MOB_RADIUS, 5D, SCARED_MOB_RADIUS);
        List<Mob> mobs = world.getEntitiesOfClass(Mob.class, area, e -> e instanceof CreeperEntity || e instanceof SpiderEntity);

        if (!mobs.isEmpty()) {
            for (Mob mob : mobs) {
                double targetX = mob.getRandomX(SCARED_MOB_RADIUS);
                double targetZ = mob.getRandomZ(SCARED_MOB_RADIUS);
                int targetY = world.getHeight(Heightmap.Type.WORLD_SURFACE, (int) targetX, (int) targetZ);
                mob.getNavigation().moveTo(targetX, targetY, targetZ, SCARED_MOB_SPEED);
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SKELETON_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    private void handleArrowUsage(PlayerEntity player, ItemStack bow, ItemStack arrowStack, AbstractArrowEntity arrowEntity, World world) {
        float saveArrowChance = 0.1F * EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MARROW_QUIVER, bow);
        boolean arrowSaved = new Random().nextFloat() < saveArrowChance;

        if (!player.abilities.instabuild && !arrowSaved) {
            arrowStack.shrink(1);
            if (arrowStack.isEmpty()) {
                player.inventory.removeItem(arrowStack);
            }
        }

        if (arrowEntity.pickup != AbstractArrowEntity.PickupStatus.CREATIVE_ONLY && player.abilities.instabuild) {
            arrowEntity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
        }

        bow.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
    }

    private float getPitch(float power) {
        return 1.0F / (random.nextFloat() * 0.4F + 1.2F) + power * 0.5F;
    }

    public static float getPowerForTime(int charge) {
        float f = (float) charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, MAX_POWER);
    }

    @Override
    public int getUseDuration(ItemStack item) {
        return DEFAULT_USE_DURATION;
    }

    @Override
    public UseAction getUseAnimation(ItemStack item) {
        return UseAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean hasProjectile = !player.getProjectile(itemstack).isEmpty();

        if (player.abilities.instabuild || hasProjectile) {
            player.startUsingItem(hand);
            return ActionResult.consume(itemstack);
        } else {
            return ActionResult.fail(itemstack);
        }
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_OR_BONE_ARROW;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 20;
    }
}
