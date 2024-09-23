package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class MaceItem extends SwordItem {

    private static final double BASE_DAMAGE = 5;
    private static final double BASE_ATTACK_SPEED = -3.3F;
    private static final int DURABILITY = 500;
    private static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
    private static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5F;
    private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7F;

    public MaceItem(ItemTier tier, Item.Properties properties) {
        super(tier, (int) BASE_DAMAGE, (float) BASE_ATTACK_SPEED, properties.durability(DURABILITY));
    }

    // Smash Attack: Handles the smash attack if fall distance exceeds the threshold
    public boolean canSmashAttack(LivingEntity entity) {
        return entity.fallDistance > SMASH_ATTACK_FALL_THRESHOLD && !entity.isFallFlying();
    }

    // Calculate Knockback Power
    private static double getKnockbackPower(PlayerEntity player, LivingEntity target, Vector3d vector) {
        return (SMASH_ATTACK_KNOCKBACK_RADIUS - vector.length()) * SMASH_ATTACK_KNOCKBACK_POWER *
                (player.fallDistance > 5.0F ? 2 : 1) * (1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }

    // Knockback Effect
    private static void knockback(World world, PlayerEntity player, LivingEntity target) {
        world.levelEvent(2001, new BlockPos(target.position()), 750);
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3.5),
                knockbackPredicate(player, target));

        for (LivingEntity entity : entities) {
            Vector3d knockbackVec = entity.position().subtract(target.position());
            double power = getKnockbackPower(player, entity, knockbackVec);
            Vector3d knockbackMovement = knockbackVec.normalize().scale(power);
            if (power > 0) {
                entity.setDeltaMovement(knockbackMovement.x, 0.7, knockbackMovement.z);
                entity.hurtMarked = true;
            }
        }
    }

    // Predicate to filter knockback targets
    private static Predicate<LivingEntity> knockbackPredicate(PlayerEntity player, LivingEntity target) {
        return entity -> {
            if (entity.isSpectator()) return false;
            if (entity == player || entity == target) return false;
            if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isAlliedTo(player)) return false;
            return player.distanceToSqr(entity) <= SMASH_ATTACK_KNOCKBACK_RADIUS * SMASH_ATTACK_KNOCKBACK_RADIUS;
        };
    }

    // Perform smash attack on enemies when player is not on the ground
    private void performSmashAttack(World world, PlayerEntity player, ItemStack stack) {
        double fallDistance = player.fallDistance;
        if (fallDistance > SMASH_ATTACK_FALL_THRESHOLD) {
            double extraDamage = calculateSmashDamage(fallDistance);
            List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(3.5));

            for (LivingEntity entity : entities) {
                if (entity != player) {
                    entity.hurt(DamageSource.playerAttack(player), (float) (BASE_DAMAGE + extraDamage));
                    knockback(world, player, entity);
                }
            }

            player.fallDistance = 0;
            playSmashSound(world, player.getPosition(1.0F), fallDistance);
            stack.hurt(1, player);
        }
    }

    private double calculateSmashDamage(double fallDistance) {
        if (fallDistance <= 3.0) {
            return 4.0 * fallDistance;
        } else if (fallDistance <= 8.0) {
            return 12.0 + 2.0 * (fallDistance - 3.0);
        } else {
            return 22.0 + (fallDistance - 8.0);
        }
    }

    private void playSmashSound(World world, Vector3d pos, double fallDistance) {
        SoundEvent soundEvent = fallDistance > 5.0 ? SoundEvents.IRON_GOLEM_ATTACK : SoundEvents.ANVIL_LAND;
        world.playSound(null, new BlockPos(pos), soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!player.isOnGround()) {
            performSmashAttack(world, player, itemStack);
            return ActionResult.success(itemStack);
        }
        return ActionResult.pass(itemStack);
    }

}
