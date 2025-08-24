package net.minecraft.entity.warden;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vec3;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

public class WardenSpawnTracker {
    public static final Codec<WardenSpawnTracker> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks_since_last_warning")
                            .orElse(0)
                            .forGetter(wardenSpawnTracker -> wardenSpawnTracker.ticksSinceLastWarning),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("warning_level")
                                    .orElse(0)
                                    .forGetter(wardenSpawnTracker -> wardenSpawnTracker.warningLevel),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("cooldown_ticks")
                                    .orElse(0).forGetter(wardenSpawnTracker -> wardenSpawnTracker.cooldownTicks))
                            .apply(instance, WardenSpawnTracker::new));


    public static final int MAX_WARNING_LEVEL = 4;
    private static final double PLAYER_SEARCH_RADIUS = 16.0;
    private static final int WARNING_CHECK_DIAMETER = 48;
    private static final int DECREASE_WARNING_LEVEL_EVERY_INTERVAL = 12000;
    private static final int WARNING_LEVEL_INCREASE_COOLDOWN = 200;
    private int ticksSinceLastWarning;
    private int warningLevel;
    private int cooldownTicks;

    public WardenSpawnTracker(int n, int n2, int n3) {
        this.ticksSinceLastWarning = n;
        this.warningLevel = n2;
        this.cooldownTicks = n3;
    }

    public void tick() {
        if (this.ticksSinceLastWarning >= 12000) {
            this.decreaseWarningLevel();
            this.ticksSinceLastWarning = 0;
        } else {
            ++this.ticksSinceLastWarning;
        }
        if (this.cooldownTicks > 0) {
            --this.cooldownTicks;
        }
    }

    public void reset() {
        this.ticksSinceLastWarning = 0;
        this.warningLevel = 0;
        this.cooldownTicks = 0;
    }

    public static OptionalInt tryWarn(ServerWorld serverWorld, BlockPos blockPos, ServerPlayerEntity serverPlayerEntity2) {
        if (WardenSpawnTracker.hasNearbyWarden(serverWorld, blockPos)) {
            return OptionalInt.empty();
        }
        List<ServerPlayerEntity> list = WardenSpawnTracker.getNearbyPlayers(serverWorld, blockPos);
        if (!list.contains(serverPlayerEntity2)) {
            list.add(serverPlayerEntity2);
        }
        if (list.stream().anyMatch(serverPlayer -> serverPlayer.getWardenSpawnTracker().map(WardenSpawnTracker::onCooldown).orElse(false))) {
            return OptionalInt.empty();
        }
        Optional<WardenSpawnTracker> optional = list.stream().flatMap(serverPlayer -> serverPlayer.getWardenSpawnTracker().stream()).max(Comparator.comparingInt(WardenSpawnTracker::getWarningLevel));
        if (optional.isPresent()) {
            WardenSpawnTracker wardenSpawnTracker = optional.get();
            wardenSpawnTracker.increaseWarningLevel();
            list.forEach(serverPlayer -> serverPlayer.getWardenSpawnTracker().ifPresent(wardenSpawnTracker2 -> wardenSpawnTracker2.copyData(wardenSpawnTracker)));
            return OptionalInt.of(wardenSpawnTracker.warningLevel);
        }
        return OptionalInt.empty();
    }

    private boolean onCooldown() {
        return this.cooldownTicks > 0;
    }

    private static boolean hasNearbyWarden(ServerWorld serverWorld, BlockPos blockPos) {
        AxisAlignedBB aABB = AxisAlignedBB.ofSize(Vector3d.atCenterOf(blockPos), 48.0, 48.0, 48.0);
        return !serverWorld.getEntitiesOfClass(WardenEntity.class, aABB).isEmpty();
    }

    private static List<ServerPlayerEntity> getNearbyPlayers(ServerWorld serverWorld, BlockPos blockPos) {
        Vector3d vec3 = Vec3.atCenterOf(blockPos);
        Predicate<ServerPlayerEntity> predicate = serverPlayer -> serverPlayer.position().closerThan(vec3, 16.0);
        return serverWorld.getPlayers(predicate.and(LivingEntity::isAlive).and(EntityPredicates.NO_SPECTATORS));
    }

    private void increaseWarningLevel() {
        if (!this.onCooldown()) {
            this.ticksSinceLastWarning = 0;
            this.cooldownTicks = 200;
            this.setWarningLevel(this.getWarningLevel() + 1);
        }
    }

    private void decreaseWarningLevel() {
        this.setWarningLevel(this.getWarningLevel() - 1);
    }

    public void setWarningLevel(int n) {
        this.warningLevel = MathHelper.clamp(n, 0, 4);
    }

    public int getWarningLevel() {
        return this.warningLevel;
    }

    private void copyData(WardenSpawnTracker wardenSpawnTracker) {
        this.warningLevel = wardenSpawnTracker.warningLevel;
        this.cooldownTicks = wardenSpawnTracker.cooldownTicks;
        this.ticksSinceLastWarning = wardenSpawnTracker.ticksSinceLastWarning;
    }
}
