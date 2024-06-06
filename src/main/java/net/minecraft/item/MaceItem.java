package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class MaceItem extends SwordItem {

    private static final double BASE_DAMAGE = 3;
    private static final double BASE_ATTACK_SPEED = -3.3F;
    private static final int DURABILITY = 500;

    public MaceItem(ItemTier tier, Item.Properties properties) {
        super(tier, (int) BASE_DAMAGE, (float) BASE_ATTACK_SPEED, properties.durability(DURABILITY));
    }



    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.isOnGround()) {
            return ActionResult.pass(itemStack); // Normal attack, handled elsewhere
        } else {
            performSmashAttack(world, player, itemStack);
            return ActionResult.sidedSuccess(itemStack, world.isClientSide());
        }
    }

    private void performSmashAttack(World world, PlayerEntity player, ItemStack itemStack) {
        double fallDistance = player.fallDistance;
        if (fallDistance > 1.5) {
            // Calculate extra damage based on fall distance
            double extraDamage = calculateSmashDamage(fallDistance);
            // Deal damage to entities within 2.5 blocks
            Vector3d playerPos = player.position();
            List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(2.5));
            for (LivingEntity entity : entities) {
                if (entity != player) {
                    entity.hurt(DamageSource.playerAttack(player), (float) (BASE_DAMAGE + extraDamage));
                }
            }
            // Reset fall distance
            player.fallDistance = 0;
            // Play smash sound
            playSmashSound(world, playerPos, fallDistance);
            // Reduce durability
            itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }
    }

    private double calculateSmashDamage(double fallDistance) {
        double extraDamage = 0.0;
        if (fallDistance <= 3) {
            extraDamage = fallDistance * 4;
        } else if (fallDistance <= 8) {
            extraDamage = 3 * 4 + (fallDistance - 3) * 2;
        } else {
            extraDamage = 3 * 4 + 5 * 2 + (fallDistance - 8);
        }
        return extraDamage;
    }

    private void playSmashSound(World world, Vector3d pos, double fallDistance) {
        SoundEvent soundEvent;
        if (fallDistance <= 5) {
            soundEvent = new SoundEvent(new ResourceLocation("item.mace.smash_ground"));
        } else {
            soundEvent = new SoundEvent(new ResourceLocation("item.mace.smash_heavy"));
        }
        world.playSound(null, new BlockPos(pos), soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity entity, LivingEntity hurt) {
        // This method handles the swinging of the mace and performing attacks
        if (hurt instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) hurt;
            if (!player.isOnGround() && player.fallDistance > 1.5) {
                performSmashAttack(player.level, player, stack);
                return true; // Prevent normal attack
            }
        }
        return super.hurtEnemy(stack, entity, hurt);
    }

}
