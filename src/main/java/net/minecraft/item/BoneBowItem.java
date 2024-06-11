package net.minecraft.item;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.command.CommandSource;
import net.minecraft.command.impl.FleeCommand;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.BoneArrowEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class BoneBowItem extends BowItem implements IVanishable {
    public BoneBowItem(Item.Properties properties) {
        super(properties);
    }
    @Override
    public void releaseUsing(ItemStack item, World world, LivingEntity entity, int releaseTime) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)entity;
            boolean flag = playerentity.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, item) > 0;
            float saveArrowChance = 0.0F;
            float chanceRoll = new Random().nextFloat();
            ItemStack itemstack = playerentity.getProjectile(item);
            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                int i = this.getUseDuration(item) - releaseTime;
                float f = getPowerForTime(i);
                if (!((double)f < 0.1D)) {
                    boolean flag1 = flag && itemstack.getItem() == Items.ARROW || flag && itemstack.getItem() == Items.BONE_ARROW;
                    AbstractArrowEntity abstractarrowentity = null;
                    if (!world.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.BONE_ARROW);
                        abstractarrowentity = arrowitem.createArrow(world, itemstack, playerentity);
                        abstractarrowentity.shootFromRotation(playerentity, playerentity.xRot, playerentity.yRot, 0.0F, f * 3.0F, 1.0F);
                        if (f >= 1F) {
                            if (itemstack.getItem() == Items.BONE_ARROW && !(entity instanceof SkeletonEntity)) {
                                AxisAlignedBB area = new AxisAlignedBB(playerentity.blockPosition()).inflate(12D, 5D, 12D);
                                List<MobEntity> mobs = world.getEntitiesOfClass(MobEntity.class, area, e -> e instanceof CreeperEntity || e instanceof SpiderEntity);
                                world.playSound(null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.SKELETON_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                if (!mobs.isEmpty()) {
                                    for (LivingEntity mob : mobs) {
                                        scareMobs(mobs, 1.12F, 12, world);
                                    }
                                }
                            }
                            if (f >= 1.3F && abstractarrowentity instanceof BoneArrowEntity) {
                                f = 1.3F;
                            } else {
                                f = 1.0F;
                            }

                            abstractarrowentity.setCritArrow(true);
                            abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() * f);
                        }

                        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, item);
                        if (j > 0) {
                                abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + (double) j * 0.5D + 1.0D);
                                abstractarrowentity.setBaseDamage(
                                        abstractarrowentity instanceof BoneArrowEntity
                                                ? abstractarrowentity.getBaseDamage() + (double) j * 0.5D + 1.0D
                                                : abstractarrowentity.getBaseDamage() + (double) j * 0.5D
                                );
                        }

                     //   DebugUtils.sendErrorMessage((ServerPlayerEntity) playerentity, world, String.format("Arrow base damage after power: %.1f", abstractarrowentity.getBaseDamage()));

                        int x = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MARROW_QUIVER, item);
                        if (x > 0) {
                            saveArrowChance = 0.1F * x;
                        }

                        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, item);
                        if (k > 0) {
                            abstractarrowentity.setKnockback(k);
                        }

                        item.hurtAndBreak(1, playerentity, (p_220009_1_) -> {
                            p_220009_1_.broadcastBreakEvent(playerentity.getUsedItemHand());
                        });
                        if (flag1 || playerentity.abilities.instabuild && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                            abstractarrowentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        }
                        abstractarrowentity.setBoundingBox(new AxisAlignedBB(
                                abstractarrowentity.getX() - 0.57F / 2,
                                abstractarrowentity.getY() - 0.57F / 2,
                                abstractarrowentity.getZ() - 0.57F / 2,
                                abstractarrowentity.getX() + 0.57F / 2,
                                abstractarrowentity.getY() + 0.57F / 2,
                                abstractarrowentity.getZ() + 0.57F / 2));
                        world.addFreshEntity(abstractarrowentity);
                    }

                    world.playSound((PlayerEntity) null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    dealWithItem(playerentity, itemstack, abstractarrowentity, flag1, chanceRoll, saveArrowChance, world);
                    playerentity.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    public void dealWithItem(PlayerEntity playerentity, ItemStack itemstack, AbstractArrowEntity abstractarrowentity, boolean flag1, float roll, float saveArrowChance, World world) {
        boolean isArrowSaved = roll < saveArrowChance;

        ItemStack itemstack2 = ItemStack.EMPTY;
        if (isArrowSaved && itemstack.getCount() >= 1) {
            itemstack2 = itemstack.copy();
            itemstack2.setCount(1);
        }

        if (!flag1 && !playerentity.abilities.instabuild) {
            itemstack.shrink(1);
            if (itemstack.isEmpty()) {
                playerentity.inventory.removeItem(itemstack);
            }
            if (!itemstack2.isEmpty() && itemstack2.getCount() != 0 && !itemstack.isEmpty()) {
                playerentity.addItem(itemstack2);
                abstractarrowentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
            } else {
                    playerentity.inventory.removeItem(itemstack2);
            }
        }
    }

    public static float getPowerForTime(int power) {
        float f = (float)power / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;  // Decrease this denominator to increase output power
        if (f > 1.3F) {
            f = 1.3F;
        }

        return f;
    }

    public int getUseDuration(ItemStack item) {
        return 72000;
    }

    public UseAction getUseAnimation(ItemStack item) {
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

    private static void scareMobs(List<MobEntity> mobs, float speed, int radius, World world) {
        int count = 0;
        for (MobEntity mob : mobs) {
            ++count;
            double X = mob.getRandomX((double) radius);
            double Z = mob.getRandomZ((double) radius);
            int Y = world.getHeight(Heightmap.Type.WORLD_SURFACE, (int) X, (int) Z);
            mob.getNavigation().moveTo(X, Y, Z, speed);
        }
    }

    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_OR_BONE_ARROW;
    }
    @Override
    public int getDefaultProjectileRange() {
        return 20;
    }
}