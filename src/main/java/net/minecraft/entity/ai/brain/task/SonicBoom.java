package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.warden.WardenEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class SonicBoom
extends Task<WardenEntity> {
    private static final int DISTANCE_XZ = 15;
    private static final int DISTANCE_Y = 20;
    private static final double KNOCKBACK_VERTICAL = 0.5;
    private static final double KNOCKBACK_HORIZONTAL = 2.5;
    public static final int COOLDOWN = 40;
    private static final int TICKS_BEFORE_PLAYING_SOUND = MathHelper.ceil(34.0);
    private static final int DURATION = MathHelper.ceil(60.0f);

    public SonicBoom() {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.SONIC_BOOM_COOLDOWN, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryModuleStatus.REGISTERED, MemoryModuleType.SONIC_BOOM_SOUND_DELAY, MemoryModuleStatus.REGISTERED), DURATION);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverWorld, WardenEntity warden) {
        if (warden.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_COOLDOWN)) {
            return false;
        }

        return warden.closerThan(warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(), warden.veryHardmode() ? 64 : 15, warden.veryHardmode() ? 64 : 20);
    }

    @Override
    protected boolean canStillUse(ServerWorld serverWorld, WardenEntity warden, long l) {
        if (warden.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_COOLDOWN)) {
            return false;
        }
        return true;
    }

    @Override
    protected void start(ServerWorld serverWorld, WardenEntity warden, long l) {
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, DURATION);
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_DELAY, Unit.INSTANCE, TICKS_BEFORE_PLAYING_SOUND);
        serverWorld.broadcastEntityEvent(warden, (byte)62);
        warden.playSound(SoundEvents.WARDEN_SONIC_CHARGE, warden.veryHardmode() ? 8F : 3F, 1.0f);
    }

    @Override
    protected void tick(ServerWorld serverWorld, WardenEntity warden, long l) {
        warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(livingEntity -> warden.getLookControl().setLookAt(livingEntity.position()));
        if (warden.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_DELAY) || warden.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN)) {
            return;
        }
        warden.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, Unit.INSTANCE, DURATION - TICKS_BEFORE_PLAYING_SOUND);

        warden.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter(warden::canTargetEntity).filter(livingEntity -> warden.closerThan((Entity)livingEntity, warden.veryHardmode() ? 64 : 15, warden.veryHardmode() ? 64 : 20)).ifPresent(livingEntity -> {
            Vector3d vector3D = warden.position().add(0.0, 1.6f, 0.0);
            Vector3d vector32D = livingEntity.getEyePosition(1F).subtract(vector3D);
            Vector3d vector33D = vector32D.normalize();
            int cc = warden.veryHardmode() ? 14 : 7;
            for (int i = 1; i < MathHelper.floor(vector32D.length()) + cc; ++i) {
                Vector3d vector34D = vector3D.add(vector33D.scale(i));
                serverWorld.sendParticles(ParticleTypes.SONIC_BOOM, vector34D.x, vector34D.y, vector34D.z, 1, 0.0, 0.0, 0.0, 0.0);

                if (warden.veryHardmode()) {
                    for (BlockPos pos : BlockPos.withinManhattan(vector34D.asBlockPos(), 1, 2, 1)) {
                        if (warden.WARDEN_BREAKABLE_BLOCKS != null && warden.WARDEN_BREAKABLE_BLOCKS.contains(serverWorld.getBlockState(pos).getBlock())) {
                            float f = serverWorld.random.nextFloat();

                            if (f < (0.93F - 0.025F * i)) {
                                serverWorld.destroyBlock(pos, true, warden, 512, false);
                            }
                        }
                    }
                }
            }
            warden.playSound(SoundEvents.WARDEN_SONIC_BOOM, warden.veryHardmode() ? 8F : 3F, 1.0f);
            livingEntity.hurt(DamageSource.sonicBoom(warden), 10.0f);
            double d = 0.5 * (1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double d2 = 2.5 * (1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            livingEntity.push(vector33D.x() * d2, vector33D.y() * d, vector33D.z() * d2);
            setCooldown(warden, warden.veryHardmode() ? 20 : COOLDOWN);
        });
    }

    @Override
    protected void stop(ServerWorld serverWorld, WardenEntity warden, long l) {
        SonicBoom.setCooldown(warden, warden.veryHardmode() ? 20 : 40);
    }

    public static void setCooldown(LivingEntity livingEntity, int n) {
        livingEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_COOLDOWN, Unit.INSTANCE, n);
    }

}

