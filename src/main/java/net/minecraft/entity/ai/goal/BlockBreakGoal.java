package net.minecraft.entity.ai.goal;


import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakGoal extends Goal {

    protected final Mob living;
    private LivingEntity target;
    private BlockPos markedLoc;
    private BlockPos entityPos;
    private int digTimer;
    private int cooldown = 20;

    private final List<BlockPos> breakAOE = new ArrayList<>();
    private int breakIndex;

    public static boolean canHarvest(BlockState block, ItemStack item) {
        boolean flag = block.is(BlockTags.PLANKS) || block.getBlock() instanceof AbstractGlassBlock || block.is(BlockTags.BASE_STONE_OVERWORLD) || block.is(Blocks.COBBLESTONE);


        return true;
    }

    private final int digHeight;

    public BlockBreakGoal(Mob living) {
        this.living = living;
        int digWidth = living.getBbWidth() < 1 ? 0 : MathHelper.ceil(living.getBbWidth());
        this.digHeight = (int) living.getBbHeight() + 1;
        for (int i = digHeight; i >= 0; i--)
            this.breakAOE.add(new BlockPos(0, i, 0));
        for (int z = digWidth + 1; z >= -digWidth; z--)
            for (int y = digHeight; y >= 0; y--) {
                for (int x = 0; x <= digWidth; x++) {
                    if (z != 0) {
                        this.breakAOE.add(new BlockPos(x, y, z));
                        if (x != 0)
                            this.breakAOE.add(new BlockPos(-x, y, z));
                    }
                }
            }
    }

    @Override
    public boolean canUse() {
        if (!living.veryHardmode()) return false;
        this.target = this.living.getTarget();
        if (this.entityPos == null) {
            this.entityPos = this.living.blockPosition();
            this.cooldown = 20;
        }
        if (--this.cooldown <= 0) {
            if (!this.entityPos.equals(this.living.blockPosition())) {
                this.entityPos = null;
                this.cooldown = 20;
                return false;
            } else if (this.target != null && this.living.distanceToSqr(this.target) > 1D) {// && this.living.isOnGround()) {
                BlockPos blockPos = this.getDiggingLocation();
                if (blockPos == null)
                    return false;
                this.cooldown = 20;
                this.markedLoc = blockPos;
                this.entityPos = this.living.blockPosition();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (!living.veryHardmode()) return false;

        return this.target != null && this.target.isAlive() && this.living.isAlive() && this.markedLoc != null && this.nearSameSpace(this.entityPos, this.living.blockPosition()) && this.living.distanceToSqr(this.target) > 1D;
    }

    private boolean nearSameSpace(BlockPos pos1, BlockPos pos2) {
        return pos1 != null && pos2 != null && pos1.getX() == pos2.getX() && pos1.getZ() == pos2.getZ() && Math.abs(pos1.getY() - pos2.getY()) <= 1;
    }

    @Override
    public void stop() {
        this.breakIndex = 0;
        if (this.markedLoc != null)
            this.living.level.destroyBlockProgress(this.living.getId(), this.markedLoc, -1);
        this.markedLoc = null;
    }

    @Override
    public void tick() {
        if (this.markedLoc == null || this.living.level.getBlockState(this.markedLoc).getCollisionShape(this.living.level, this.markedLoc).isEmpty()) {
            this.digTimer = 0;
            return;
        }
        BlockState state = this.living.level.getBlockState(this.markedLoc);
        float str = getBlockStrength(this.living, state, this.living.level, this.markedLoc);
        str = str == Float.POSITIVE_INFINITY ? 1 : str / (1 + str * 6) * (this.digTimer + 1);
        if (str >= 1F) {
            this.digTimer = 0;
            this.cooldown *= 0.5;
            ItemStack item = this.living.getMainHandItem();
            ItemStack itemOff = this.living.getOffhandItem();
            boolean canHarvest = canHarvest(state, item) || canHarvest(state, itemOff);
            this.living.level.destroyBlock(this.markedLoc, canHarvest);
            this.markedLoc = null;
            if (!this.aboveTarget()) {
                this.living.setSpeed(0);
                this.living.getNavigation().stop();
                this.living.getNavigation().moveTo(this.living.getNavigation().createPath(this.target, 0), 1D);
            }
        } else {
            this.digTimer++;
            if (this.digTimer % 5 == 0) {
                SoundType sound = state.getSoundType();
                this.living.level.playSound(null, this.markedLoc, sound.getBreakSound(), SoundCategory.BLOCKS, 2F, 1.0F);
                this.living.swing(Hand.MAIN_HAND);
                this.living.getLookControl().setLookAt(this.markedLoc.getX(), this.markedLoc.getY(), this.markedLoc.getZ(), 0.0F, 0.0F);
                this.living.level.destroyBlockProgress(this.living.getId(), this.markedLoc, (int) (str) * this.digTimer * 10);
            }
        }
    }

    public BlockPos getDiggingLocation() {
        ItemStack item = this.living.getMainHandItem();
        ItemStack itemOff = this.living.getOffhandItem();
        BlockPos pos = this.living.blockPosition();
        BlockState state;
        if (this.living.getTarget() != null) {
            Vector3d target = this.living.getTarget().position();
            if (this.aboveTarget() && Math.abs(target.x - pos.getX()) <= 3 && Math.abs(target.z - pos.getZ()) <= 3) {
                pos = this.living.blockPosition().below();
                state = this.living.level.getBlockState(pos);
                if (this.canBreak(this.living, state, pos, item, itemOff)) {
                    this.breakIndex = 0;
                    return pos;
                }
            }

        }

        Rotation rot = getDigDirection(this.living);
        BlockPos offset = this.breakAOE.get(this.breakIndex);
        offset = new BlockPos(offset.getX(), this.aboveTarget() ? (-(offset.getY() - this.digHeight)) : offset.getY(), offset.getZ());
        pos = pos.offset(offset.rotate(rot));
        state = this.living.level.getBlockState(pos);
        if (this.canBreak(this.living, state, pos, item, itemOff)) {
            this.breakIndex = 0;
            return pos;
        }
        this.breakIndex++;
        if (this.breakIndex == this.breakAOE.size())
            this.breakIndex = 0;
        return null;
    }

    private boolean canBreak(LivingEntity entity, BlockState state, BlockPos pos, ItemStack item, ItemStack itemOff) {
        return canHarvest(state, itemOff);
    }

    private boolean aboveTarget() {
        return this.target != null && (this.target.getY() < this.living.getY() + 1.1);
    }


    public static Rotation getDigDirection(Mob mob) {
        Path path = mob.getNavigation().getPath();

        if (path != null && !path.isDone()) {
            PathPoint nextPoint = path.getNextNode();
            if (nextPoint != null) {
                Vector3d dir = new Vector3d(nextPoint.x + 0.5, mob.getY(), nextPoint.z + 0.5)
                        .subtract(mob.position());

                if (Math.abs(dir.x) < Math.abs(dir.z)) {
                    return (dir.z >= 0) ? Rotation.NONE : Rotation.CLOCKWISE_180;
                } else {
                    return (dir.x > 0) ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
                }
            }
        }

        return switch (mob.getDirection()) {
            case SOUTH -> Rotation.CLOCKWISE_180;
            case EAST -> Rotation.CLOCKWISE_90;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }



    public static float getBreakSpeed(Mob entity, ItemStack stack, BlockState state) {
        float f = stack.getDestroySpeed(state);
        if (f > 1.0F) {
            int i = EnchantmentHelper.getBlockEfficiency(entity);
            ItemStack itemstack = entity.getMainHandItem();
            if (i > 0 && !itemstack.isEmpty())
                f += i * i + 1;
        }
        if (EffectUtils.hasDigSpeed(entity))
            f *= 1.0F + (EffectUtils.getDigSpeedAmplification(entity) + 1) * 0.2F;
        if (entity.hasEffect(Effects.DIG_SLOWDOWN)) {
            switch (entity.getEffect(Effects.DIG_SLOWDOWN).getAmplifier()) {
                case 0:
                    f *= 0.3F;
                    break;
                case 1:
                    f *= 0.09F;
                    break;
                case 2:
                    f *= 0.0027F;
                    break;
                case 3:
                default:
                    f *= 8.1E-4F;
            }
        }
        if (entity.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(entity))
            f /= 5.0F;
        if (!entity.isOnGround())
            f /= 5.0F;
        return f;
    }

    public static float getBlockStrength(Mob entityLiving, BlockState state, World world, BlockPos pos) {
        float hardness = world.getBlockState(pos).getDestroySpeed(world, pos);
        if (hardness < 0) {
            return 0.0F;
        }
        ItemStack main = entityLiving.getMainHandItem();
        ItemStack off = entityLiving.getOffhandItem();
        if (canHarvest(state, main)) {
            float speed = getBreakSpeed(entityLiving, main, state);
            if (canHarvest(state, off)) {
                float offSpeed = getBreakSpeed(entityLiving, off, state);
                if (offSpeed > speed)
                    speed = offSpeed;
            }
            return speed / hardness / 30F;
        } else if (canHarvest(state, off)) {
            return getBreakSpeed(entityLiving, off, state) / hardness / 30F;
        } else {
            return getBreakSpeed(entityLiving, main, state) / hardness / 100F;
        }
    }

}