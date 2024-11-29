package net.minecraft.item.tool.terraria;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class BeekeeperItem extends TerrariaSwordItem {

    public BeekeeperItem() {
        super(ItemTier.BEEKEEPER, 5, -2.6F, ItemTier.BEEKEEPER.getUses(), Rarity.ORANGEE);
    }

    @Override
    public float getKnockbackPower() {
        return 0.53F;
    }

    @Override
    public float getVerticalKnockbackPower() {
        return 0.3f;
    }

    @Override
    public double getMaxKnockPower() {
        return 0.8D;
    }

    @Override
    public boolean couldAttack(ItemStack sword, LivingEntity target, LivingEntity mob) {
        int existingBees = mob.level.getEntitiesOfClass(BeeEntity.class, mob.getBoundingBox().inflate(18D),
                BeeEntity::fromBeeKeeper).size();
        boolean isBoss = target instanceof EnderDragonEntity || target instanceof WitherEntity;
        int maxBeesToSummon = Math.min(isBoss ? 12 - existingBees : 3 - existingBees, 1 + target.getRandom().nextInt(3));

        if (!(mob instanceof PlayerEntity) || !mob.asPlayer().getCooldowns().isOnCooldown(Items.BEEKEEPER)) {
            for (int i = 0; i < maxBeesToSummon; i++) {
                BeeEntity beeEntity = EntityType.BEE.create(mob.level);
                if (beeEntity != null) {
                    beeEntity.setPos(mob.blockPosition().offset(mob.getRandom().nextDouble() * 0.6, 1, mob.getRandom().nextDouble() * 0.6));
                    beeEntity.setRemainingPersistentAngerTime(10 * 20);
                    beeEntity.setFromBeeKeeper(true);
                    beeEntity.setTarget(target);
                    beeEntity.setInvulnerable(true);
                    mob.level.addFreshEntity(beeEntity);
                }
            }
        }

        if (target.getRandom().nextFloat() < 0.9F) {
            target.addEffect(new EffectInstance(Effects.CONFUSED, 40, 0, false, false));
        }

        if (mob instanceof PlayerEntity && !mob.asPlayer().getCooldowns().isOnCooldown(Items.BEEKEEPER)) {
            mob.asPlayer().getCooldowns().addCooldown(this, 25);
        }

        return super.couldAttack(sword, target, mob);
    }

    @Override
    public void appendText(ItemStack sword, World level, List<ITextComponent> flags, ITooltipFlag tooltip) {
        flags.add(new TranslationTextComponent("item.minecraft.beekeeper.description1"));
        flags.add(new TranslationTextComponent("item.minecraft.beekeeper.description2"));
        flags.add(new StringTextComponent(" "));
        flags.add(new TranslationTextComponent("item.minecraft.beekeeper.description3"));
        flags.add(new TranslationTextComponent("item.minecraft.beekeeper.description4"));
    }

    @Override
    public double getMaxRayTraceDistance() {
        return 20D;
    }
}
