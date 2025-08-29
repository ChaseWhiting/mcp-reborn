package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FireSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractFireBlock extends Block {
   private final float fireDamage;
   protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

   public AbstractFireBlock(AbstractBlock.Properties p_i241173_1_, float p_i241173_2_) {
      super(p_i241173_1_);
      this.fireDamage = p_i241173_2_;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return getState(p_196258_1_.getLevel(), p_196258_1_.getClickedPos());
   }

   public static BlockState getState(IBlockReader p_235326_0_, BlockPos p_235326_1_) {
      BlockPos blockpos = p_235326_1_.below();
      BlockState blockstate = p_235326_0_.getBlockState(blockpos);
      if (blockstate.getBlock() == Blocks.HELLFIRE) {
         return Blocks.HELLFIRE.defaultBlockState();
      }
      return SoulFireBlock.canSurviveOnBlock(blockstate.getBlock()) ? Blocks.SOUL_FIRE.defaultBlockState() : ((FireBlock)Blocks.FIRE).getStateForPlacement(p_235326_0_, p_235326_1_);
   }

   public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      return DOWN_AABB;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World world, BlockPos pos, Random random) {
      if (random.nextInt(24) == 0) {
         world.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
      }

      BlockPos blockpos = pos.below();
      BlockState blockstate = world.getBlockState(blockpos);
      if (!this.canBurn(blockstate) && !blockstate.isFaceSturdy(world, blockpos, Direction.UP)) {
         if (this.canBurn(world.getBlockState(pos.west()))) {
            for(int j = 0; j < 2; ++j) {
               double d3 = (double)pos.getX() + random.nextDouble() * (double)0.1F;
               double d8 = (double)pos.getY() + random.nextDouble();
               double d13 = (double)pos.getZ() + random.nextDouble();
               world.addParticle(ParticleTypes.LARGE_SMOKE, d3, d8, d13, 0.0D, 0.0D, 0.0D);

               if (random.nextFloat() < 0.15F) {
                  BasicParticleType bp = random.nextFloat() > 0.8F ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
                  world.addAlwaysVisibleParticle(bp, true,
                          d3,
                          d8,
                          d13, 0.0D, 0.07D, 0.0D);
               }

               if (random.nextInt(5) == 0) {
                  for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                     BasicParticleType bp = (random.nextFloat() > 0.8F ? ParticleTypes.TNT_LAVA : ParticleTypes.LAVA);
                     world.addParticle(bp,
                             d3,
                             d8,
                             d13,
                             (random.nextFloat() / 2.0F), 5.0E-5D,
                             (random.nextFloat() / 2.0F));
                  }
               }
            }
         }

         if (this.canBurn(world.getBlockState(pos.east()))) {
            for(int k = 0; k < 2; ++k) {
               double d4 = (double)(pos.getX() + 1) - random.nextDouble() * (double)0.1F;
               double d9 = (double)pos.getY() + random.nextDouble();
               double d14 = (double)pos.getZ() + random.nextDouble();
               world.addParticle(ParticleTypes.LARGE_SMOKE, d4, d9, d14, 0.0D, 0.0D, 0.0D);

               if (random.nextFloat() < 0.15F) {
                  BasicParticleType bp = random.nextFloat() > 0.8F ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
                  world.addAlwaysVisibleParticle(bp, true,
                          d4,
                          d9,
                          d14, 0.0D, 0.07D, 0.0D);
               }

               if (random.nextInt(5) == 0) {
                  for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                     BasicParticleType bp = (random.nextFloat() > 0.8F ? ParticleTypes.TNT_LAVA : ParticleTypes.LAVA);
                     world.addParticle(bp,
                             d4,
                             d9,
                             d14,
                             (random.nextFloat() / 2.0F), 5.0E-5D,
                             (random.nextFloat() / 2.0F));
                  }
               }
            }
         }

         if (this.canBurn(world.getBlockState(pos.north()))) {
            for(int l = 0; l < 2; ++l) {
               double d5 = (double)pos.getX() + random.nextDouble();
               double d10 = (double)pos.getY() + random.nextDouble();
               double d15 = (double)pos.getZ() + random.nextDouble() * (double)0.1F;
               world.addParticle(ParticleTypes.LARGE_SMOKE, d5, d10, d15, 0.0D, 0.0D, 0.0D);

               if (random.nextFloat() < 0.15F) {
                  BasicParticleType bp = random.nextFloat() > 0.8F ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
                  world.addAlwaysVisibleParticle(bp, true,
                          d5,
                          d10,
                          d15, 0.0D, 0.07D, 0.0D);
               }

               if (random.nextInt(5) == 0) {
                  for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                     BasicParticleType bp = (random.nextFloat() > 0.8F ? ParticleTypes.TNT_LAVA : ParticleTypes.LAVA);
                     world.addParticle(bp,
                             d5,
                             d10,
                             d15,
                             (random.nextFloat() / 2.0F), 5.0E-5D,
                             (random.nextFloat() / 2.0F));
                  }
               }
            }
         }

         if (this.canBurn(world.getBlockState(pos.south()))) {
            for(int i1 = 0; i1 < 2; ++i1) {
               double d6 = (double)pos.getX() + random.nextDouble();
               double d11 = (double)pos.getY() + random.nextDouble();
               double d16 = (double)(pos.getZ() + 1) - random.nextDouble() * (double)0.1F;
               world.addParticle(ParticleTypes.LARGE_SMOKE, d6, d11, d16, 0.0D, 0.0D, 0.0D);

               if (random.nextFloat() < 0.15F) {
                  BasicParticleType bp = random.nextFloat() > 0.8F ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
                  world.addAlwaysVisibleParticle(bp, true,
                          d6,
                          d11,
                          d16, 0.0D, 0.07D, 0.0D);
               }

               if (random.nextInt(5) == 0) {
                  for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                     BasicParticleType bp = (random.nextFloat() > 0.8F ? ParticleTypes.TNT_LAVA : ParticleTypes.LAVA);
                     world.addParticle(bp,
                             d6,
                             d11,
                             d16,
                             (random.nextFloat() / 2.0F), 5.0E-5D,
                             (random.nextFloat() / 2.0F));
                  }
               }
            }
         }

         if (this.canBurn(world.getBlockState(pos.above()))) {
            for(int j1 = 0; j1 < 2; ++j1) {
               double d7 = (double)pos.getX() + random.nextDouble();
               double d12 = (double)(pos.getY() + 1) - random.nextDouble() * (double)0.1F;
               double d17 = (double)pos.getZ() + random.nextDouble();
               world.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);

               if (random.nextFloat() < 0.15F) {
                  BasicParticleType bp = random.nextFloat() > 0.8F ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
                  world.addAlwaysVisibleParticle(bp, true,
                          d7,
                          d12,
                          d17, 0.0D, 0.07D, 0.0D);
               }

               if (random.nextInt(5) == 0) {
                  for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                     BasicParticleType bp = (random.nextFloat() > 0.8F ? ParticleTypes.TNT_LAVA : ParticleTypes.LAVA);
                     world.addParticle(bp,
                             d7,
                             d12,
                             d17,
                             (random.nextFloat() / 2.0F), 5.0E-5D,
                             (random.nextFloat() / 2.0F));
                  }
               }
            }
         }
      } else {
         for(int i = 0; i < 3; ++i) {
            double d0 = (double)pos.getX() + random.nextDouble();
            double d1 = (double)pos.getY() + random.nextDouble() * 0.5D + 0.5D;
            double d2 = (double)pos.getZ() + random.nextDouble();
            world.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);

            if (random.nextFloat() < 0.15F) {
               BasicParticleType bp = random.nextFloat() > 0.8F ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
               world.addAlwaysVisibleParticle(bp, true,
                       d0,
                       d1,
                       d2, 0.0D, 0.07D, 0.0D);
            }

            if (random.nextInt(5) == 0) {
               for(int x = 0; x < random.nextInt(1) + 1; ++x) {
                  BasicParticleType bp = (random.nextFloat() > 0.8F ? ParticleTypes.TNT_LAVA : ParticleTypes.LAVA);
                  world.addParticle(bp,
                          d0,
                          d1,
                          d2,
                          (random.nextFloat() / 2.0F), 5.0E-5D,
                          (random.nextFloat() / 2.0F));
               }
            }
         }
      }

   }

   protected abstract boolean canBurn(BlockState p_196446_1_);

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_4_.fireImmune()) {
         p_196262_4_.setRemainingFireTicks(p_196262_4_.getRemainingFireTicks() + 1, this instanceof SoulFireBlock ? FireSource.SOUL_FIRE : FireSource.FIRE);
         if (p_196262_4_.getRemainingFireTicks() == 0) {
            p_196262_4_.setSecondsOnFire(8, this instanceof SoulFireBlock ? FireSource.SOUL_FIRE : FireSource.FIRE);
         }


         p_196262_4_.hurt(DamageSource.IN_FIRE, this.fireDamage);
      }

      super.entityInside(p_196262_1_, p_196262_2_, p_196262_3_, p_196262_4_);
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_4_.is(p_220082_1_.getBlock())) {
         if (inPortalDimension(p_220082_2_)) {
            Optional<PortalSize> optional = PortalSize.findEmptyPortalShape(p_220082_2_, p_220082_3_, Direction.Axis.X);
            if (optional.isPresent()) {
               optional.get().createPortalBlocks();
               return;
            }
         }

         if (!p_220082_1_.canSurvive(p_220082_2_, p_220082_3_)) {
            p_220082_2_.removeBlock(p_220082_3_, false);
         }

      }
   }

   private static boolean inPortalDimension(World p_242649_0_) {
      return p_242649_0_.dimension() == World.OVERWORLD || p_242649_0_.dimension() == World.NETHER;
   }

   public BlockState playerWillDestroy(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      if (!p_176208_1_.isClientSide()) {
         p_176208_1_.levelEvent((PlayerEntity)null, 1009, p_176208_2_, 0);
      }
      return p_176208_3_;
   }

   public static boolean canBePlacedAt(World p_241465_0_, BlockPos p_241465_1_, Direction p_241465_2_) {
      BlockState blockstate = p_241465_0_.getBlockState(p_241465_1_);
      if (!blockstate.isAir()) {
         return false;
      } else {
         return getState(p_241465_0_, p_241465_1_).canSurvive(p_241465_0_, p_241465_1_) || isPortal(p_241465_0_, p_241465_1_, p_241465_2_);
      }
   }

   private static boolean isPortal(World p_241466_0_, BlockPos p_241466_1_, Direction p_241466_2_) {
      if (!inPortalDimension(p_241466_0_)) {
         return false;
      } else {
         BlockPos.Mutable blockpos$mutable = p_241466_1_.mutable();
         boolean flag = false;

         for(Direction direction : Direction.values()) {
            if (p_241466_0_.getBlockState(blockpos$mutable.set(p_241466_1_).move(direction)).is(Blocks.OBSIDIAN)) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            return false;
         } else {
            Direction.Axis direction$axis = p_241466_2_.getAxis().isHorizontal() ? p_241466_2_.getCounterClockWise().getAxis() : Direction.Plane.HORIZONTAL.getRandomAxis(p_241466_0_.random);
            return PortalSize.findEmptyPortalShape(p_241466_0_, p_241466_1_, direction$axis).isPresent();
         }
      }
   }
}