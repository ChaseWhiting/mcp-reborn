package net.minecraft.entity.ai.goal;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.SpellcastingIllagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AttackSpellGoal<E extends SpellcastingIllagerEntity> extends SpellcastingIllagerEntity.UseSpellGoal {
    private final E shaman;
    private Map<UUID, WolfEntity> wolves = new ConcurrentHashMap<>();

    public AttackSpellGoal(E entity) {
        entity.super();
        this.shaman = entity;
    }

    public boolean canUse() {

        return super.canUse() && this.shaman.getTarget() != null;
    }

    protected int getCastingTime() {
        return 40;
    }

    protected int getCastingInterval() {
        return 25 * 20;
    }

    public void tick() {
        super.tick();
    }

    protected void performSpellCasting() {
        LivingEntity target = shaman.getTarget();
        if (target == null) {
            return;
        }


        // Coordinates for spawning wolves in a circle around the shaman
        double d0 = Math.min(target.getY(), shaman.getY());
        double d1 = Math.max(target.getY(), shaman.getY()) + 1.0D;
        float f = (float) MathHelper.atan2(target.getZ() - shaman.getZ(), target.getX() - shaman.getX());

        int wolfCount = 0;
        int maxWolves = 2;
        float angleIncrement = (float) (2 * Math.PI / maxWolves);

        for (int i = 0; i < maxWolves; ++i) {
            float f1 = f + i * angleIncrement;
            summonWolves(shaman.getX() + (double) MathHelper.cos(f1) * 2.5D, shaman.getZ() + (double) MathHelper.sin(f1) * 2.5D, d0, d1, f1);
            wolfCount++;

            if (wolfCount >= maxWolves) {
                break;
            }
        }
    }

    private void summonWolves(double x, double z, double minY, double maxY, float rotation) {
        BlockPos blockPos = new BlockPos(x, minY, z);
        ServerWorld world = (ServerWorld) shaman.level;

        for (int i = 0; i < 3; i++) {
            WolfEntity wolf = EntityType.WOLF.create(world);
            if (wolf != null) {
                UUID wolfUUID;
                do {
                    wolfUUID = UUID.randomUUID();
                } while (wolves.containsKey(wolfUUID)); // Assuming `wolves` is a Map<UUID, WolfEntity>

                wolf.setUUID(wolfUUID);
                wolf.setPos(x, minY + 1, z);
             //   wolf.setOwnerUUID(shaman.getUUID()); // Assuming wolves should be owned by the spellcaster

                wolf.setTarget(shaman.getTarget());
                wolf.setRemainingPersistentAngerTime(1200);
                wolf.setPersistentAngerTarget(shaman.getTarget().getUUID());
                wolf.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5D);
              //  wolf.setCollarColor(DyeColor.GREEN);
                wolves.put(wolfUUID, wolf); // Assuming `wolves` is a Map<UUID, WolfEntity>
                world.addFreshEntity(wolf);
            }
        }
    }

    protected SoundEvent getSpellPrepareSound() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    protected SpellcastingIllagerEntity.SpellType getSpell() {
        return SpellcastingIllagerEntity.SpellType.WOLVES;
    }
}
