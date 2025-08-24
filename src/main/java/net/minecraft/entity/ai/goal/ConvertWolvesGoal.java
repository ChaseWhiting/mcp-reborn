package net.minecraft.entity.ai.goal;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.ShamanEntity;
import net.minecraft.entity.monster.SpellcastingIllagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.GameRules;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ConvertWolvesGoal extends SpellcastingIllagerEntity.UseSpellGoal {

    private final ShamanEntity shaman;

    private final EntityPredicate wolfTargetting = (new EntityPredicate()).range(10.0D).allowInvulnerable().selector((wolf) -> {
        return ((WolfEntity)wolf).getOwnerUUID() == null && ((WolfEntity)wolf).getOwner() == null && !((WolfEntity) wolf).isFromShaman();
    });

    public ConvertWolvesGoal(ShamanEntity entity) {
        entity.super();
        this.shaman = entity;
    }

    public boolean canUse() {
        Random random = new Random();

        if (shaman.getTarget() != null) {
            return false;
        } else if (shaman.isCastingSpell()) {
            return false;
        } else if (shaman.tickCount < this.nextAttackTickCount) {
            return false;
        } else if (!shaman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        } else {
            List<WolfEntity> list = shaman.level.getNearbyEntities(WolfEntity.class, this.wolfTargetting, shaman, shaman.getBoundingBox().inflate(10.0D, 6.0D, 10.0D));
            List<WolfEntity> filteredList = list.stream()
                    .filter(wolf -> wolf.getShaman() == null && wolf.getOwner() != shaman && wolf.getOwner() == null && wolf.getOwnerUUID() == null) // Filter out wolves with a shaman
                    .collect(Collectors.toList());
            if (filteredList.isEmpty()) {
                return false;
            } else {
                // validation on wolfEntities length is needed here to avoid ArrayIndexOutOfBoundsException
                for(int i = 0; i < filteredList.size() && i < shaman.wolfEntities.size(); i++) {

                    shaman.setWolfTarget(filteredList.get(i), i);
                }
                return true;
            }
        }
    }

    public boolean canContinueToUse() {
        return shaman.getWolfEntities() != null && !shaman.getWolfEntities().isEmpty() && this.attackWarmupDelay > 0;
    }

    public void stop() {
        super.stop();
        if(shaman.getWolfEntities() != null)
            shaman.getWolfEntities().clear();
    }

    protected void performSpellCasting() {
        List<WolfEntity> wolfTargets = shaman.getWolfEntities(); // assuming this method returns an array of WolfEntity
        if (wolfTargets != null) {
            for (WolfEntity wolfEntity : wolfTargets) {
                if (wolfEntity != null && wolfEntity.isAlive()) {
                   // wolfEntity.setOwnerUUID(shaman.getUUID());
                    wolfEntity.setFromShaman(true, 12 * 20);
                  //  wolfEntity.setCollarColor(DyeColor.GREEN);
                    wolfEntity.setShaman(shaman);
                    if (shaman.getTarget() != null) {
                        wolfEntity.setTarget(shaman.getTarget());
                        wolfEntity.setRemainingPersistentAngerTime(1200);
                        wolfEntity.setPersistentAngerTarget(shaman.getTarget().getUUID());
                        wolfEntity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5D);
                    }
                }
            }
        }
    }

    protected int getCastWarmupTime() {
        return 40;
    }

    protected int getCastingTime() {
        return 60;
    }

    protected int getCastingInterval() {
        return 140;
    }

    protected SoundEvent getSpellPrepareSound() {
        return SoundEvents.EVOKER_PREPARE_WOLOLO;
    }

    protected SpellcastingIllagerEntity.SpellType getSpell() {
        return SpellcastingIllagerEntity.SpellType.WOLOLO;
    }
}
