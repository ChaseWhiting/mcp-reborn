package net.minecraft.item.tool.terraria;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.StarfuryStarEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

import static net.minecraft.item.tool.terraria.StarfuryItem.*;

public class StarCloakItem extends AccessoryItem {

    public StarCloakItem() {
        super(new Properties().durability(1244), Rarity.LIGHT_RED);
    }

    @Override
    public boolean isAccessoryActive(ItemStack stack, PlayerEntity player) {
        boolean flag = player.getCooldowns().isOnCooldown(Items.STAR_CLOAK) || player.getCooldowns().isOnCooldown(Items.BEE_CLOAK);

        return player.hurtTime > 0
                && player.getLastHurtByMob() != null
                && player.tickCount - player.getLastHurtByMobTimestamp() < 60 && !flag && player.getLastHurtByMob() != player;
    }

    @Override
    protected void onAccessoryActivated(ItemStack stack, PlayerEntity player) {
    }

    @Override
    protected void onAccessoryDeactivated(ItemStack stack, PlayerEntity player) {
    }

    @Override
    protected void applyAccessoryEffect(ItemStack stack, World world, PlayerEntity player) {
        player.getCooldowns().addCooldown(Items.STAR_CLOAK, 80);
        player.getCooldowns().addCooldown(Items.BEE_CLOAK, 80);
        this.spawnProjectilesAbove(world, player, player.getLastHurtByMob().blockPosition().asVector());
        int durabilityLoss = (int) (stack.getMaxDamage() * (0.01 + new Random().nextDouble() * 0.01));
        stack.hurtAndBreak(durabilityLoss, player, (entity) -> entity.broadcastBreakEvent(Hand.MAIN_HAND));
    }



    protected void spawnProjectilesAbove(World world, PlayerEntity player, Vector3d targetPos) {
        Random random = new Random();

        Vector3d playerLookDir = player.getLookAngle().normalize();

        for (int i = 0; i < 3; i++) {
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

        world.addFreshEntity(arrowEntity);
        world.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z, SoundEvents.STARFURY_SHOOT, SoundCategory.PLAYERS, 3F, 1.0F);
    }

    @Override
    protected void applyStatBoosts (PlayerEntity player){
    }
}
