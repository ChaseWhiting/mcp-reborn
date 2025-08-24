package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.BrushableBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.BrushableBlockEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BrushItem extends Item implements IVanishable {
    public static final int ANIMATION_DURATION = 10;
    private static final int USE_DURATION = 200;
    private static final double MAX_BRUSH_DISTANCE = Math.sqrt(MathHelper.square(6.0)) - 1.0;

    public BrushItem() {
        super(new Item.Properties().durability(64));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && this.calculateHitResult(player).getType() == RayTraceResult.Type.BLOCK) {
            player.startUsingItem(context.getHand());
        }

        return ActionResultType.CONSUME;
    }

    @Override
    public UseAction getUseAnimation(ItemStack p_77661_1_) {
        return UseAction.BRUSH;
    }

    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public void onUseTick(World world, LivingEntity entity, ItemStack stack, int time) {
        PlayerEntity player;
        if (time < 0 || !(entity instanceof PlayerEntity)) {
            entity.releaseUsingItem();
            return;
        }
        player = (PlayerEntity) entity;

        BlockRayTraceResult brtr;

        RayTraceResult result = this.calculateHitResult(player);

        if (result.getType() == RayTraceResult.Type.MISS) {
            player.releaseUsingItem();
            return;
        }

        if (!(result instanceof BlockRayTraceResult)) {
            player.releaseUsingItem();
            return;
        }

        brtr = (BlockRayTraceResult) result;

        int animDuration = getAnimationDuration(stack);
        int targetFrame = Math.min(5, animDuration - 1); // Clamp 5 to the max allowed value
        if ((this.getUseDuration(stack) - time + 1) % animDuration == targetFrame) {
            BlockPos position = brtr.getBlockPos();
            BlockState state = world.getBlockState(position);

            HandSide handSide = player.getUsedItemHand() == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
            this.spawnDustParticles(world, brtr, state, player.getViewVector(), handSide);
            SoundEvent soundEvent = SoundEvents.BRUSH_GENERIC;

            if (state.getBlock() instanceof BrushableBlock brushableBlock) {
                soundEvent = brushableBlock.getBrushSound();
            }

            world.playSound(player, position, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClientSide) {
                if (world.getBlockEntity(position) instanceof BrushableBlockEntity brushableBlockEntity &&
                        brushableBlockEntity.brush(world.getGameTime(), player, brtr.getDirection(), stack)) {

                    if (!stack.isDamageableItem()) return;

                    stack.hurtAndBreak(1, player, playerEntity -> playerEntity.broadcastBreakEvent(playerEntity.getUsedItemHand()));
                }
            }

        }
    }

    public int getAnimationDuration(ItemStack stack) {
        return ANIMATION_DURATION - 2 * EnchantmentHelper.getItemEnchantmentLevel(Enchantments.EXCAVATION, stack);
    }

    public int getWeight(ItemStack bundle) {
        return 14;
    }

    private RayTraceResult calculateHitResult(LivingEntity livingEntity) {
        return ProjectileHelper.getHitResultOnViewVector(livingEntity, entity -> !entity.isSpectator() && entity.isPickable(), MAX_BRUSH_DISTANCE);
    }

    public void spawnDustParticles(World level, BlockRayTraceResult blockHitResult, BlockState blockState, Vector3d vec3, HandSide humanoidArm) {
        double d = 3.0;
        int n = humanoidArm == HandSide.RIGHT ? 1 : -1;
        int n2 = level.getRandom().nextInt(7, 12);
        BlockParticleData blockParticleOption = new BlockParticleData(ParticleTypes.BLOCK, blockState);
        Direction direction = blockHitResult.getDirection();
        DustParticlesDelta dustParticlesDelta = DustParticlesDelta.fromDirection(vec3, direction);
        Vector3d vec32 = blockHitResult.getLocation();
        for (int i = 0; i < n2; ++i) {
            level.addParticle(blockParticleOption, vec32.x - (double)(direction == Direction.WEST ? 1.0E-6f : 0.0f), vec32.y, vec32.z - (double)(direction == Direction.NORTH ? 1.0E-6f : 0.0f), dustParticlesDelta.xd() * (double)n * 3.0 * level.getRandom().nextDouble(), 0.0, dustParticlesDelta.zd() * (double)n * 3.0 * level.getRandom().nextDouble());
        }
    }

    record DustParticlesDelta(double xd, double yd, double zd) {
        public static DustParticlesDelta fromDirection(Vector3d vec3, Direction direction) {
            return switch (direction) {
                default -> throw new IncompatibleClassChangeError();
                case DOWN, UP -> new DustParticlesDelta(vec3.z(), 0.0, -vec3.x());
                case NORTH -> new DustParticlesDelta(1.0, 0.0, -0.1);
                case SOUTH -> new DustParticlesDelta(-1.0, 0.0, 0.1);
                case WEST -> new DustParticlesDelta(-0.1, 0.0, -1.0);
                case EAST -> new DustParticlesDelta(0.1, 0.0, 1.0);
            };
        }
    }
}
