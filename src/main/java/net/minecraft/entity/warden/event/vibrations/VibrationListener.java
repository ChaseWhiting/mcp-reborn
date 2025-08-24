/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.warden.event.vibrations;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.entity.warden.event.GameEventListener;
import net.minecraft.entity.warden.event.position.PositionSource;
import net.minecraft.particles.VibrationParticleOption;
import net.minecraft.util.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;


public class VibrationListener
implements GameEventListener {
    @VisibleForTesting
    public static final Object2IntMap<GameEvent> VIBRATION_FREQUENCY_FOR_EVENT = Object2IntMaps.unmodifiable(Util.make(new Object2IntOpenHashMap<GameEvent>(), object2IntOpenHashMap -> {
        object2IntOpenHashMap.put(GameEvent.STEP, 1);
        object2IntOpenHashMap.put(GameEvent.FLAP, 2);
        object2IntOpenHashMap.put(GameEvent.SWIM, 3);
        object2IntOpenHashMap.put(GameEvent.ELYTRA_GLIDE, 4);
        object2IntOpenHashMap.put(GameEvent.HIT_GROUND, 5);
        object2IntOpenHashMap.put(GameEvent.TELEPORT, 5);
        object2IntOpenHashMap.put(GameEvent.SPLASH, 6);
        object2IntOpenHashMap.put(GameEvent.ENTITY_SHAKE, 6);
        object2IntOpenHashMap.put(GameEvent.BLOCK_CHANGE, 6);
        object2IntOpenHashMap.put(GameEvent.NOTE_BLOCK_PLAY, 6);
        object2IntOpenHashMap.put(GameEvent.PROJECTILE_SHOOT, 7);
        object2IntOpenHashMap.put(GameEvent.DRINK, 7);
        object2IntOpenHashMap.put(GameEvent.PRIME_FUSE, 7);
        object2IntOpenHashMap.put(GameEvent.PROJECTILE_LAND, 8);
        object2IntOpenHashMap.put(GameEvent.EAT, 8);
        object2IntOpenHashMap.put(GameEvent.ENTITY_INTERACT, 8);
        object2IntOpenHashMap.put(GameEvent.ENTITY_DAMAGE, 8);
        object2IntOpenHashMap.put(GameEvent.EQUIP, 9);
        object2IntOpenHashMap.put(GameEvent.SHEAR, 9);
        object2IntOpenHashMap.put(GameEvent.ENTITY_ROAR, 9);
        object2IntOpenHashMap.put(GameEvent.BLOCK_CLOSE, 10);
        object2IntOpenHashMap.put(GameEvent.BLOCK_DEACTIVATE, 10);
        object2IntOpenHashMap.put(GameEvent.BLOCK_DETACH, 10);
        object2IntOpenHashMap.put(GameEvent.DISPENSE_FAIL, 10);
        object2IntOpenHashMap.put(GameEvent.BLOCK_OPEN, 11);
        object2IntOpenHashMap.put(GameEvent.BLOCK_ACTIVATE, 11);
        object2IntOpenHashMap.put(GameEvent.BLOCK_ATTACH, 11);
        object2IntOpenHashMap.put(GameEvent.ENTITY_PLACE, 12);
        object2IntOpenHashMap.put(GameEvent.ENTITY_ACTION, 12);
        object2IntOpenHashMap.put(GameEvent.BLOCK_PLACE, 12);
        object2IntOpenHashMap.put(GameEvent.FLUID_PLACE, 12);
        object2IntOpenHashMap.put(GameEvent.ENTITY_DIE, 13);
        object2IntOpenHashMap.put(GameEvent.BLOCK_DESTROY, 13);
        object2IntOpenHashMap.put(GameEvent.FLUID_PICKUP, 13);
        object2IntOpenHashMap.put(GameEvent.ITEM_INTERACT_FINISH, 14);
        object2IntOpenHashMap.put(GameEvent.CONTAINER_CLOSE, 14);
        object2IntOpenHashMap.put(GameEvent.PISTON_CONTRACT, 14);
        object2IntOpenHashMap.put(GameEvent.PISTON_EXTEND, 15);
        object2IntOpenHashMap.put(GameEvent.CONTAINER_OPEN, 15);
        object2IntOpenHashMap.put(GameEvent.EXPLODE, 15);
        object2IntOpenHashMap.put(GameEvent.LIGHTNING_STRIKE, 15);
        object2IntOpenHashMap.put(GameEvent.INSTRUMENT_PLAY, 15);
    }));
    protected final PositionSource listenerSource;
    protected final int listenerRange;
    protected final VibrationListenerConfig config;
    @Nullable
    protected VibrationInfo currentVibration;
    protected int travelTimeInTicks;
    private final VibrationSelector selectionStrategy;

    public static Codec<VibrationListener> codec(VibrationListenerConfig vibrationListenerConfig) {
        return RecordCodecBuilder.create(instance ->
                instance.group(PositionSource.CODEC.fieldOf("source").forGetter(vibrationListener -> vibrationListener.listenerSource),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("range").forGetter(vibrationListener -> vibrationListener.listenerRange),
                        VibrationInfo.CODEC.optionalFieldOf("event").forGetter(vibrationListener -> Optional.ofNullable(vibrationListener.currentVibration)),
                        VibrationSelector.CODEC.fieldOf("selector").forGetter(vibrationListener -> vibrationListener.selectionStrategy),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter(vibrationListener -> vibrationListener.travelTimeInTicks))
                        .apply(instance, (positionSource, n, optional, vibrationSelector, n2) ->
                                new VibrationListener((PositionSource)positionSource, (int)n, vibrationListenerConfig,
                                        optional.orElse(null), (VibrationSelector)vibrationSelector, (int)n2)));
    }

    private VibrationListener(PositionSource positionSource, int n, VibrationListenerConfig vibrationListenerConfig, @Nullable VibrationInfo vibrationInfo, VibrationSelector vibrationSelector, int n2) {
        this.listenerSource = positionSource;
        this.listenerRange = n;
        this.config = vibrationListenerConfig;
        this.currentVibration = vibrationInfo;
        this.travelTimeInTicks = n2;
        this.selectionStrategy = vibrationSelector;
    }

    public VibrationListener(PositionSource positionSource, int n, VibrationListenerConfig vibrationListenerConfig) {
        this(positionSource, n, vibrationListenerConfig, null, new VibrationSelector(), 0);
    }

    public static int getGameEventFrequency(GameEvent gameEvent) {
        return VIBRATION_FREQUENCY_FOR_EVENT.getOrDefault((Object)gameEvent, 0);
    }

    public void tick(World level) {
        if (level instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)level;
            if (this.currentVibration == null) {
                this.selectionStrategy.chosenCandidate(serverWorld.getGameTime()).ifPresent(vibrationInfo -> {
                    this.currentVibration = vibrationInfo;
                    Vector3d vector3D = this.currentVibration.pos();
                    this.travelTimeInTicks = MathHelper.floor(this.currentVibration.distance());
                    serverWorld.sendParticles(new VibrationParticleOption(this.listenerSource, this.travelTimeInTicks), vector3D.x, vector3D.y, vector3D.z, 1, 0.0, 0.0, 0.0, 0.0);
                    this.config.onSignalSchedule();
                    this.selectionStrategy.startOver();
                });
            }
            if (this.currentVibration != null) {
                --this.travelTimeInTicks;
                if (this.travelTimeInTicks <= 0) {
                    this.travelTimeInTicks = 0;
                    this.config.onSignalReceive(serverWorld, this, new BlockPos(this.currentVibration.pos()), this.currentVibration.gameEvent(), this.currentVibration.getEntity(serverWorld).orElse(null), this.currentVibration.getProjectileOwner(serverWorld).orElse(null), this.currentVibration.distance());
                    this.currentVibration = null;
                }
            }
        }
    }

    @Override
    public PositionSource getListenerSource() {
        return this.listenerSource;
    }

    @Override
    public int getListenerRadius() {
        return this.listenerRange;
    }

    @Override
    public boolean handleGameEvent(ServerWorld serverWorld, GameEvent gameEvent, GameEvent.Context context, Vector3d vector3D) {
        System.out.println("Got vibration in VibrationListener#handleGameEvent. Detail: {context=" + context.toString() + "} event: " + gameEvent.getName() + " location: " + vector3D.toString());
        if (this.currentVibration != null) {
            System.out.println("Already had vibration, returning false in handleGameEvent (VibrationListener class)");
            return false;
        }
        if (!this.config.isValidVibration(gameEvent, context)) {
            System.out.println("Config returned vibration is not valid, returning false in handleGameEvent (VibrationListener class)");
            return false;
        }
        Optional<Vector3d> optional = this.listenerSource.getPosition(serverWorld);
        if (optional.isEmpty()) {
            System.out.println("Optional for listener source position is empty, returning false in handleGameEvent (VibrationListener class)");

            return false;
        }
        Vector3d vector32D = optional.get();
        if (!this.config.shouldListen(serverWorld, this, new BlockPos(vector3D), gameEvent, context)) {
            System.out.println("Config returned this vibration is not listenable, returning false in handleGameEvent (VibrationListener class)");

            return false;
        }
        if (VibrationListener.isOccluded(serverWorld, vector3D, vector32D)) {
            System.out.println("Vibration was occluded by wool, returning false in handleGameEvent (VibrationListener class)");

            return false;
        }
        this.scheduleVibration(serverWorld, gameEvent, context, vector3D, vector32D);
        System.out.println("Vibration scheduled in handleGameEvent (VibrationListener class)");
        return true;
    }

    public void forceGameEvent(ServerWorld serverWorld, GameEvent gameEvent, GameEvent.Context context, Vector3d vector3D) {
        this.listenerSource.getPosition(serverWorld).ifPresent(vec32 -> this.scheduleVibration(serverWorld, gameEvent, context, vector3D, (Vector3d)vec32));
    }

    public void scheduleVibration(ServerWorld serverWorld, GameEvent gameEvent, GameEvent.Context context, Vector3d vector3D, Vector3d vector32D) {
        this.selectionStrategy.addCandidate(new VibrationInfo(gameEvent, (float) vector3D.distanceTo(vector32D), vector3D, context.sourceEntity), serverWorld.getGameTime());
    }

    private static boolean isOccluded(World level, Vector3d vector3D, Vector3d vector32D) {
        Vector3d vector33D = new Vector3d((double)MathHelper.floor(vector3D.x) + 0.5, (double)MathHelper.floor(vector3D.y) + 0.5, (double)MathHelper.floor(vector3D.z) + 0.5);
        Vector3d vector34D = new Vector3d((double)MathHelper.floor(vector32D.x) + 0.5, (double)MathHelper.floor(vector32D.y) + 0.5, (double)MathHelper.floor(vector32D.z) + 0.5);
        for (Direction direction : Direction.values()) {
            Vector3d vector35D = vector33D.relative(direction, 1.0E-5f);
            if (level.isBlockInLine(new ClipBlockStateContext(vector35D, vector34D, blockState -> blockState.is(GameEvent.OCCLUDES_VIBRATION_SIGNALS))).getType() == RayTraceResult.Type.BLOCK) continue;
            return false;
        }
        return true;
    }

    public static interface VibrationListenerConfig {
        default public ImmutableList<GameEvent> getListenableEvents() {
            return GameEvent.VIBRATIONS;
        }

        default public boolean canTriggerAvoidVibration() {
            return false;
        }

        default public boolean isValidVibration(GameEvent gameEvent, GameEvent.Context context) {
            if (!gameEvent.is(this.getListenableEvents())) {
                return false;
            }
            Entity entity = context.sourceEntity;
            if (entity != null) {
                if (entity.isSpectator()) {
                    return false;
                }
                if (entity.isSteppingCarefully() && gameEvent.is(GameEvent.IGNORE_VIBRATION_SNEAKING)) {
                    if (this.canTriggerAvoidVibration() && entity instanceof ServerPlayerEntity) {
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                        //CriteriaTriggers.AVOID_VIBRATION.trigger(serverPlayerEntity);
                    }
                    return false;
                }
                if (entity.dampensVibrations()) {
                    return false;
                }
            }
            if (context.affectedState != null) {
                return !context.affectedState.is(GameEvent.DAMPENS_VIBRATIONS);
            }
            return true;
        }

        public boolean shouldListen(ServerWorld var1, GameEventListener var2, BlockPos var3, GameEvent var4, GameEvent.Context var5);

        public void onSignalReceive(ServerWorld var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5, @Nullable Entity var6, float var7);

        default public void onSignalSchedule() {
        }
    }
}

