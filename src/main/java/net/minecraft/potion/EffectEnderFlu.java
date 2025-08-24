package net.minecraft.potion;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.enderiophage.EntityEnderiophage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;

public class EffectEnderFlu extends Effect {

    private int lastDuration = -1;

    public EffectEnderFlu() {
        super(EffectType.HARMFUL, 0X6836AA);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (lastDuration == 1) {
            int phages = amplifier + 1;
            entity.hurt(DamageSource.MAGIC, phages * 10);
            for (int i = 0; i < phages; i++) {
                EntityEnderiophage phage = EntityType.ENDERIOPHAGE.create(entity.level());
                phage.copyPosition(entity);
                phage.onSpawnFromEffect();
                phage.setSkinForDimension();
                if (!entity.level().isClientSide) {
                    phage.setStandardFleeTime();
                    entity.level().addFreshEntity(phage);
                }
                for (PlayerEntity player : entity.level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(entity.blockPosition()).inflate(16D, 16D, 16D))) {
                    if (player instanceof ServerPlayerEntity) {
                        CriteriaTriggers.ENDERIOPHAGE_SPAWN_FROM_FLU.trigger(((ServerPlayerEntity)player));
                        if (phage.getVariant() == 2 || phage.getVariant() == 1) {
                            CriteriaTriggers.ENDER_RELOCATION.trigger((ServerPlayerEntity)player);
                        }
                    }
                }
            }
        }
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

}