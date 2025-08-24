/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.UniformInt;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;


public class LongJumpToRandomPos<E extends Mob>
extends Task<E> {
    protected static final int FIND_JUMP_TRIES = 20;
    private static final int PREPARE_JUMP_DURATION = 40;
    protected static final int MIN_PATHFIND_DISTANCE_TO_VALID_JUMP = 8;
    private static final int TIME_OUT_DURATION = 200;
    private static final List<Integer> ALLOWED_ANGLES = Lists.newArrayList(new Integer[]{65, 70, 75, 80});
    private final UniformInt timeBetweenLongJumps;
    protected final int maxLongJumpHeight;
    protected final int maxLongJumpWidth;
    protected final float maxJumpVelocity;
    protected List<PossibleJump> jumpCandidates = Lists.newArrayList();
    protected Optional<Vector3d> initialPosition = Optional.empty();
    @Nullable
    protected Vector3d chosenJump;
    protected int findJumpTries;
    protected long prepareJumpStart;
    private final Function<E, SoundEvent> getJumpSound;
    private final BiPredicate<E, BlockPos> acceptableLandingSpot;

    public LongJumpToRandomPos(UniformInt uniformInt, int n, int n2, float f, Function<E, SoundEvent> function) {
        this(uniformInt, n, n2, f, function, LongJumpToRandomPos::defaultAcceptableLandingSpot);
    }

    public static <E extends Mob> boolean defaultAcceptableLandingSpot(E e, BlockPos blockPos) {
        World level = e.level;
        BlockPos blockPos2 = blockPos.below();
        return level.getBlockState(blockPos2).isSolidRender(level, blockPos2) && e.getPathfindingMalus(WalkNodeProcessor.getBlockPathTypeStatic(level, blockPos.mutable())) == 0.0f;
    }

    public LongJumpToRandomPos(UniformInt uniformInt, int n, int n2, float f, Function<E, SoundEvent> function, BiPredicate<E, BlockPos> biPredicate) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleStatus.VALUE_ABSENT), 200);
        this.timeBetweenLongJumps = uniformInt;
        this.maxLongJumpHeight = n;
        this.maxLongJumpWidth = n2;
        this.maxJumpVelocity = f;
        this.getJumpSound = function;
        this.acceptableLandingSpot = biPredicate;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld serverLevel, Mob mob) {
        boolean bl;
        boolean bl2 = bl = mob.isOnGround() && !mob.isInWater() && !mob.isInLava() && !serverLevel.getBlockState(mob.blockPosition()).is(Blocks.HONEY_BLOCK);
        if (!bl) {
            mob.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.timeBetweenLongJumps.sample(serverLevel.random) / 2);
        }
        return bl;
    }

    @Override
    protected boolean canStillUse(ServerWorld serverLevel, Mob mob, long l) {
        boolean bl;
        boolean bl2 = bl = this.initialPosition.isPresent() && this.initialPosition.get().equals(mob.position()) && this.findJumpTries > 0 && !mob.isInWaterOrBubble() && (this.chosenJump != null || !this.jumpCandidates.isEmpty());
        if (!bl && mob.getBrain().getMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isEmpty()) {
            mob.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.timeBetweenLongJumps.sample(serverLevel.random) / 2);
            mob.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        }
        return bl;
    }

    @Override
    protected void start(ServerWorld serverLevel, E e, long l) {
        this.chosenJump = null;
        this.findJumpTries = 20;
        this.initialPosition = Optional.of(((Entity)e).position());
        BlockPos blockPos = ((Entity)e).blockPosition();
        int n = blockPos.getX();
        int n2 = blockPos.getY();
        int n3 = blockPos.getZ();
        this.jumpCandidates = BlockPos.betweenClosedStream(n - this.maxLongJumpWidth, n2 - this.maxLongJumpHeight, n3 - this.maxLongJumpWidth, n + this.maxLongJumpWidth, n2 + this.maxLongJumpHeight, n3 + this.maxLongJumpWidth).filter(blockPos2 -> !blockPos2.equals(blockPos)).map(blockPos2 -> new PossibleJump(blockPos2.immutable(), MathHelper.ceil(blockPos.distSqr((Vector3i) blockPos2)))).collect(Collectors.toCollection(Lists::newArrayList));
    }

    @Override
    protected void tick(ServerWorld serverLevel, E e, long l) {
        if (this.chosenJump != null) {
            if (l - this.prepareJumpStart >= 40L) {
                ((Entity)e).yRot = (((Mob)e).yBodyRot);
                ((LivingEntity)e).setDiscardFriction(true);
                double d = this.chosenJump.length();
                double d2 = d + ((LivingEntity)e).getJumpBoostPower();
                ((Entity)e).setDeltaMovement(this.chosenJump.scale(d2 / d));
                ((LivingEntity)e).getBrain().setMemory(MemoryModuleType.LONG_JUMP_MID_JUMP, true);
                serverLevel.playSound(null, (Entity)e, this.getJumpSound.apply(e), SoundCategory.NEUTRAL, 1.0f, 1.0f);
            }
        } else {
            --this.findJumpTries;
            this.pickCandidate(serverLevel, e, l);
        }
    }

    protected void pickCandidate(ServerWorld serverLevel, E e, long l) {
        while (!this.jumpCandidates.isEmpty()) {
            Vector3d vec3;
            Vector3d vec32;
            PossibleJump possibleJump;
            BlockPos blockPos;
            Optional<PossibleJump> optional = this.getJumpCandidate(serverLevel);
            if (optional.isEmpty() || !this.isAcceptableLandingPosition(serverLevel, e, blockPos = (possibleJump = optional.get()).getJumpTarget()) || (vec32 = this.calculateOptimalJumpVector((Mob)e, vec3 = Vector3d.atCenterOf(blockPos))) == null) continue;
            ((LivingEntity)e).getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(blockPos));
            PathNavigator pathNavigation = ((Mob)e).getNavigation();
            Path path = pathNavigation.createPath(blockPos, 0);
            if (path != null && path.canReach()) continue;
            this.chosenJump = vec32;
            this.prepareJumpStart = l;
            return;
        }
    }

    protected Optional<PossibleJump> getJumpCandidate(ServerWorld serverLevel) {
        Optional<PossibleJump> optional = Optional.of(WeightedRandom.getRandomItem(serverLevel.random, this.jumpCandidates));
        optional.ifPresent(this.jumpCandidates::remove);
        return optional;
    }

    private boolean isAcceptableLandingPosition(ServerWorld serverLevel, E e, BlockPos blockPos) {
        BlockPos blockPos2 = ((Entity)e).blockPosition();
        int n = blockPos2.getX();
        int n2 = blockPos2.getZ();
        if (n == blockPos.getX() && n2 == blockPos.getZ()) {
            return false;
        }
        return this.acceptableLandingSpot.test(e, blockPos);
    }

    @Nullable
    protected Vector3d calculateOptimalJumpVector(Mob mob, Vector3d vec3) {
        ArrayList arrayList = Lists.newArrayList(ALLOWED_ANGLES);
        Collections.shuffle(arrayList);
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            int n = (Integer)iterator.next();
            Vector3d vec32 = this.calculateJumpVectorForAngle(mob, vec3, n);
            if (vec32 == null) continue;
            return vec32;
        }
        return null;
    }

    @Nullable
    private Vector3d calculateJumpVectorForAngle(Mob mob, Vector3d vec3, int n) {
        Vector3d vec32 = mob.position();
        Vector3d vec33 = new Vector3d(vec3.x - vec32.x, 0.0, vec3.z - vec32.z).normalize().scale(0.5);
        vec3 = vec3.subtract(vec33);
        Vector3d vec34 = vec3.subtract(vec32);
        float f = (float)n * (float)Math.PI / 180.0f;
        double d = Math.atan2(vec34.z, vec34.x);
        double d2 = vec34.subtract(0.0, vec34.y, 0.0).lengthSqr();
        double d3 = Math.sqrt(d2);
        double d4 = vec34.y;
        double d5 = Math.sin(2.0f * f);
        double d6 = 0.08;
        double d7 = Math.pow(Math.cos(f), 2.0);
        double d8 = Math.sin(f);
        double d9 = Math.cos(f);
        double d10 = Math.sin(d);
        double d11 = Math.cos(d);
        double d12 = d2 * 0.08 / (d3 * d5 - 2.0 * d4 * d7);
        if (d12 < 0.0) {
            return null;
        }
        double d13 = Math.sqrt(d12);
        if (d13 > (double)this.maxJumpVelocity) {
            return null;
        }
        double d14 = d13 * d9;
        double d15 = d13 * d8;
        int n2 = MathHelper.ceil(d3 / d14) * 2;
        double d16 = 0.0;
        Vector3d vec35 = null;
        EntitySize entityDimensions = mob.getDimensions(Pose.LONG_JUMPING);
        for (int i = 0; i < n2 - 1; ++i) {
            double d17 = d8 / d9 * (d16 += d3 / (double)n2) - Math.pow(d16, 2.0) * 0.08 / (2.0 * d12 * Math.pow(d9, 2.0));
            double d18 = d16 * d11;
            double d19 = d16 * d10;
            Vector3d vec36 = new Vector3d(vec32.x + d18, vec32.y + d17, vec32.z + d19);
            if (vec35 != null && !this.isClearTransition(mob, entityDimensions, vec35, vec36)) {
                return null;
            }
            vec35 = vec36;
        }
        return new Vector3d(d14 * d11, d15, d14 * d10).scale(0.95f);
    }

    private boolean isClearTransition(Mob mob, EntitySize entityDimensions, Vector3d vec3, Vector3d vec32) {
        Vector3d vec33 = vec32.subtract(vec3);
        double d = Math.min(entityDimensions.width, entityDimensions.height);
        int n = MathHelper.ceil(vec33.length() / d);
        Vector3d vec34 = vec33.normalize();
        Vector3d vec35 = vec3;
        for (int i = 0; i < n; ++i) {
            Vector3d vec36 = vec35 = i == n - 1 ? vec32 : vec35.add(vec34.scale(d * (double)0.9f));
            if (mob.level.noCollision(mob, entityDimensions.makeBoundingBox(vec35))) continue;
            return false;
        }
        return true;
    }



    public static class PossibleJump
    extends WeightedRandom.Item {
        private final BlockPos jumpTarget;

        public PossibleJump(BlockPos blockPos, int n) {
            super(n);
            this.jumpTarget = blockPos;
        }

        public BlockPos getJumpTarget() {
            return this.jumpTarget;
        }
    }
}

