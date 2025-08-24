package net.minecraft.item.tool.terraria;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BeeCloakItem extends StarCloakItem {

    public BeeCloakItem() {
        super();
    }

    @Override
    protected void applyAccessoryEffect(ItemStack stack, World world, PlayerEntity player) {
        int existingBees = player.level.getEntitiesOfClass(BeeEntity.class, player.getBoundingBox().inflate(18D),
                BeeEntity::fromBeeKeeper).size();
        int maxBeesToSummon = Math.min(3 - existingBees, 1 + player.getRandom().nextInt(3));
        if (maxBeesToSummon > 0) {
            stack.hurt(MathHelper.nextInt(player.getRandom(), 1, 3), player);
        }
        for (int i = 0; i < maxBeesToSummon; i++) {
            BeeEntity beeEntity = EntityType.BEE.create(player.level);
            if (beeEntity != null) {
                beeEntity.setPos(player.blockPosition().offset(player.getRandom().nextDouble() * 0.6, 1, player.getRandom().nextDouble() * 0.6));
                beeEntity.setRemainingPersistentAngerTime(10 * 20);
                beeEntity.setFromBeeKeeper(true);
                beeEntity.setTarget(player.getLastHurtByMob());
                beeEntity.setInvulnerable(true);
                player.level.addFreshEntity(beeEntity);
            }
        }

        player.addEffect(new EffectInstance(Effects.HONEY, 10 * 20, 0));

        super.applyAccessoryEffect(stack, world, player);
    }
}
