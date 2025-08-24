package net.minecraft.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FallingLeavesParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Creature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomSource;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class ParticleLeavesBlock extends LeavesBlock {
    private final IParticleData particle;
    private final int chance;

    public ParticleLeavesBlock(int chance, IParticleData particle, Properties properties) {
        super(properties);
        this.chance = chance;
        this.particle = particle;
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        super.animateTick(state, world, pos, random);

        if (random.nextInt(this.chance) != 0) {
            return;
        }

        BlockPos belowPos = pos.below();
        BlockState belowState = world.getBlockState(belowPos);

        if (!belowState.canOcclude() || !belowState.isFaceSturdy(world, belowPos, Direction.UP)) {
            spawnParticleBelow(world, pos, random, this.particle);
        }
    }


    @Override
    public void fallOn(World world, BlockPos pos, Entity entity, float entityFallDistance) {
        super.fallOn(world, pos, entity, entityFallDistance);

        if (entityFallDistance > world.getParticleFallDistanceOntoLeaves(world.getGameRules().getBoolean(GameRules.RULE_ADVANCED_FALL_CALCULATION))
                && entity instanceof LivingEntity living) {
            RandomSource random = RandomSource.create(entity.getId() & world.getRandom().nextLong());
            int count = 5 + (int) ((random.nextIntBetweenInclusive(1, 3) * (entityFallDistance - 3F)));
            count = MathHelper.clamp(count, 5, 150);

            if (living.getItemBySlot(EquipmentSlotType.FEET).getItem() instanceof ArmorItem armor) {
                count += BiasedToBottomInt.of(1, armor.getWeight(null)).sample(random);
            }

            if (this != Blocks.DEAD_LEAVES) {
                for (int i = 0; i < count; i++) {

                    Particle particle = world.addAlwaysVisibleParticleAndReturn(ParticleTypes.BURSTING_TINTED_LEAVES,
                            entity.getX(),
                            living.blockPosition().getY() + 0.3,
                            entity.getZ(), 0.0, 0.0, 0.0);

                    if (particle != null) {
    
                        BlockState blockState = world.getBlockState(pos);
                        int n = Minecraft.getInstance().getBlockColors().getColor(blockState, world, pos, 0);
                        particle.setColor(ARGB.redFloat(n), ARGB.greenFloat(n), ARGB.blueFloat(n));

                        FallingLeavesParticle fallingLeavesParticle = (FallingLeavesParticle) particle;

                        entityFallDistance = MathHelper.clamp(entityFallDistance, 0.0F, 100F);

                        double baseSpeed = 0.085 + 0.001 * entityFallDistance;

                        double xSpeed = (random.nextDouble() - 0.5) * baseSpeed * 2;
                        double zSpeed = (random.nextDouble() - 0.5) * baseSpeed * 2;

                        fallingLeavesParticle.setXd(xSpeed);
                        fallingLeavesParticle.setZd(zSpeed);

                        entityFallDistance = MathHelper.clamp(entityFallDistance, 0.0F, 45.5F);

                        fallingLeavesParticle.setYd((0.018 + 0.001 * entityFallDistance) + random.nextDouble() * 0.1);

                        float f = MathHelper.clamp(world.getRandom().nextFloat(0.045F, 0.075F + (0.005F * entityFallDistance)), 0.045F, 0.095F);


                        fallingLeavesParticle.setQuadSize(2.0F * f);
                    }
                }
            }
        }
    }

    @Nullable
    private static void addParticle(ServerWorld world, double x, double y, double z) {
        double rx = world.getRandom().nextDouble() * 0.4;
        double rz = world.getRandom().nextDouble() * 0.4;
        double ry = world.getRandom().nextDouble() * 0.2;


        world.sendParticles(ParticleTypes.BURSTING_TINTED_LEAVES, x + rx, y - 0.5D - 0.05D, z + rz, 1, 0, 0, 0, 0);
    }

    private static void spawnParticleBelow(World world, BlockPos pos, Random random, IParticleData particle) {
        double x = (double) pos.getX() + random.nextDouble();
        double y = (double) pos.getY() - 0.05;
        double z = (double) pos.getZ() + random.nextDouble();
        world.addAlwaysVisibleParticle(particle, x, y, z, 0.0, 0.0, 0.0);
    }

    public static int getDistanceToGround(World world, BlockPos pos) {
        int i = 0;

        while (i <= 16) {
            BlockState state = world.getBlockState(pos.below());

            if (!state.isSolidRender(world, pos.below()) || state.isAir()) {
                i++;
            } else if (state.isFaceSturdy(world, pos.below(), Direction.UP) && !state.getBlock().is(BlockTags.LEAVES)) {
                break;
            }
            pos = pos.below();

        }

        return i;
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.getValue(PERSISTENT) && state.getValue(DISTANCE) == 7) {
            int distanceToGround = getDistanceToGround(world, pos);
            int count = random.nextInt(distanceToGround <= 5 ? 2 : 5, distanceToGround <= 5 ? 6 : 12);

            if (this != Blocks.DEAD_LEAVES) {
                for (int i = 0; i < count; i++) {
                    addParticle(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);


                }
            }
            dropResources(state, world, pos);
            world.removeBlock(pos, false);


        }

    }
}
