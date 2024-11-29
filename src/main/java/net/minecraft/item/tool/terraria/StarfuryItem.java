package net.minecraft.item.tool.terraria;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.StarfuryStarEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class StarfuryItem extends TerrariaSwordItem {

    private static final double MAX_RAY_TRACE_DISTANCE = 25.0;
    static final double Y_SPAWN_OFFSET = 27.5;
    static final double HORIZONTAL_SPAWN_RADIUS = 18.5;
    static final double LANDING_RADIUS = 2;

    public StarfuryItem() {
        super(ItemTier.STARFURY, 7, -2F, ItemTier.STARFURY.getUses(), Rarity.GREEN);
    }

    public StarfuryItem(ItemTier tier, int baseDamage, float attackSpeed, int baseDurability, Rarity rarity) {
        super(tier, baseDamage, attackSpeed, baseDurability, rarity);
    }

    @Override
    public double getMaxRayTraceDistance() {
        return MAX_RAY_TRACE_DISTANCE;
    }

    public int getProjectileCount() {
        return 1;
    }

    @Override
    public float getKnockbackPower() {
        return 0.5f;
    }

    @Override
    public float getVerticalKnockbackPower() {
        return 0.2f;
    }

    @Override
    public double getMaxKnockPower() {
        return 0.6;
    }

    public Item getItem() {
        return Items.STARFURY;
    }

    public int getCooldownTime() {
        return 13;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (this.getItem() == Items.STAR_WRATH) {
            world.playSound(player, player.blockPosition(), SoundEvents.STAR_WRATH_USE, SoundCategory.PLAYERS, 2f, 1.0f);
        }
        if (!world.isClientSide()) {
            BlockRayTraceResult rayTraceResult = getRayTrace(player);
            if (!player.getCooldowns().isOnCooldown(this.getItem())) {
                player.getCooldowns().addCooldown(this.getItem(), getCooldownTime());
            }

            if (rayTraceResult.getType() == BlockRayTraceResult.Type.BLOCK || rayTraceResult.getType() == BlockRayTraceResult.Type.ENTITY) {
                Vector3d targetPos = rayTraceResult.getLocation();
                spawnProjectilesAbove(world, player, targetPos);

                int durabilityLoss = (int) (itemStack.getMaxDamage() * (0.01 + new Random().nextDouble() * 0.01));
                itemStack.hurtAndBreak(durabilityLoss, player, (entity) -> entity.broadcastBreakEvent(hand));
            }
        }
        player.awardStat(Stats.ITEM_USED.get(this), 1);
        return ActionResult.success(itemStack);
    }

    @Override
    public boolean couldAttack(ItemStack sword, LivingEntity target, LivingEntity mob) {
        if (this.getItem() == Items.STAR_WRATH && mob instanceof PlayerEntity) {
           mob.asPlayer().level.playSound(mob.asPlayer(), mob.blockPosition(), SoundEvents.STAR_WRATH_USE, SoundCategory.PLAYERS, 2f, 1.0f);
        }
        if (!mob.level.isClientSide() && mob instanceof PlayerEntity) {
            PlayerEntity player = mob.asPlayer();
            if (!player.getCooldowns().isOnCooldown(this.getItem())) {
                player.getCooldowns().addCooldown(this.getItem(), getCooldownTime());
            }

                spawnProjectilesAbove(mob.level, player, target.blockPosition().asVector());

                int durabilityLoss = (int) (sword.getMaxDamage() * (0.01 + new Random().nextDouble() * 0.01));
                sword.hurtAndBreak(durabilityLoss, player, (entity) -> entity.broadcastBreakEvent(Hand.MAIN_HAND));

        }
        return super.couldAttack(sword, target, mob);
    }


    protected void spawnProjectilesAbove(World world, PlayerEntity player, Vector3d targetPos) {
        Random random = new Random();

        Vector3d playerLookDir = player.getLookAngle().normalize();

        for (int i = 0; i < this.getProjectileCount(); i++) {
            double xOffset = -playerLookDir.x * (HORIZONTAL_SPAWN_RADIUS + random.nextDouble() * (HORIZONTAL_SPAWN_RADIUS / 2));
            double zOffset = -playerLookDir.z * (HORIZONTAL_SPAWN_RADIUS + random.nextDouble() * (HORIZONTAL_SPAWN_RADIUS / 2));

            Vector3d spawnPos = new Vector3d(
                    targetPos.x + xOffset,
                    targetPos.y + Y_SPAWN_OFFSET,
                    targetPos.z + zOffset
            );

            double landingXOffset = (random.nextDouble() * 2 - 1) * LANDING_RADIUS;
            double landingZOffset = (random.nextDouble() * 2 - 1) * LANDING_RADIUS;
            Vector3d adjustedTargetPos = targetPos.add(landingXOffset, 0, landingZOffset);

            summonProjectileTowardsTarget(world, spawnPos, adjustedTargetPos, player);
        }
    }

    public float getProjectileSpeed() {
        return 1.7f;
    }

    private void summonProjectileTowardsTarget(World world, Vector3d spawnPos, Vector3d targetPos, PlayerEntity player) {
        StarfuryStarEntity arrowEntity = new StarfuryStarEntity(world, spawnPos.x, spawnPos.y, spawnPos.z);

        Vector3d direction = targetPos.subtract(spawnPos).normalize();

        float speed = getProjectileSpeed();
        arrowEntity.setDeltaMovement(direction.x * speed, direction.y * speed, direction.z * speed);
        arrowEntity.setOwner(player);

        arrowEntity.setItem(new ItemStack(this::getVisibleItem));

        world.addFreshEntity(arrowEntity);
        world.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z, SoundEvents.STARFURY_SHOOT, SoundCategory.PLAYERS, 3F, 1.0F);
    }

    public Item getVisibleItem() {
        return Items.STAR;
    }


    @Override
    public void appendText(ItemStack sword, World level, List<ITextComponent> flags, ITooltipFlag tooltip) {}
}