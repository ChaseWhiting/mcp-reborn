package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IBee;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.QueenBeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BeehiveBlock extends ContainerBlock {
   private static final Direction[] SPAWN_DIRECTIONS = new Direction[]{Direction.WEST, Direction.EAST, Direction.SOUTH};
   public static final DirectionProperty FACING = HorizontalBlock.FACING;
   public static final IntegerProperty HONEY_LEVEL = BlockStateProperties.LEVEL_HONEY;

   public BeehiveBlock(AbstractBlock.Properties p_i225756_1_) {
      super(p_i225756_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(HONEY_LEVEL, Integer.valueOf(0)).setValue(FACING, Direction.NORTH));
   }

   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return p_180641_1_.getValue(HONEY_LEVEL);
   }

   public void playerDestroy(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack itemStack) {
      super.playerDestroy(world, player, pos, state, tileEntity, itemStack);
      if (!world.isClientSide && tileEntity instanceof BeehiveTileEntity) {
         BeehiveTileEntity beehiveTileEntity = (BeehiveTileEntity) tileEntity;
         if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
            beehiveTileEntity.emptyAllLivingFromHive(player, state, BeehiveTileEntity.State.EMERGENCY);
            world.updateNeighbourForOutputSignal(pos, this);
            this.angerNearbyBees(world, pos);
         }

         CriteriaTriggers.BEE_NEST_DESTROYED.trigger((ServerPlayerEntity) player, state.getBlock(), itemStack, beehiveTileEntity.getOccupantCount());
      }
   }


   private void angerNearbyBees(World world, BlockPos pos) {
      List<Entity> bees = world.getEntitiesOfClass(Entity.class, new AxisAlignedBB(pos).inflate(8.0D, 6.0D, 8.0D), entity -> entity instanceof IBee);
      if (!bees.isEmpty()) {
         List<PlayerEntity> players = world.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(pos).inflate(8.0D, 6.0D, 8.0D));
         int playerCount = players.size();

         for (Entity entity : bees) {
            if (entity instanceof BeeEntity) {
               BeeEntity bee = (BeeEntity) entity;
               if (bee.getTarget() == null) {
                  bee.setTarget(players.get(world.random.nextInt(playerCount)));
               }
            } else if (entity instanceof QueenBeeEntity) {
               QueenBeeEntity queenBee = (QueenBeeEntity) entity;
               if (queenBee.getTarget() == null) {
                  queenBee.setTarget(players.get(world.random.nextInt(playerCount)));
               }
            }
         }
      }
   }

   public static void dropHoneycomb(World level, BlockPos blockPos) {
      popResource(level, blockPos, new ItemStack(Items.HONEYCOMB, 3));
   }

   public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
      ItemStack itemstack = player.getItemInHand(hand);
      int i = state.getValue(HONEY_LEVEL);
      boolean flag = false;
      if (i >= 5) {
         if (itemstack.getItem() == Items.SHEARS) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            dropHoneycomb(level, pos);
            itemstack.hurtAndBreak(1, player, (p_226874_1_) -> {
               p_226874_1_.broadcastBreakEvent(hand);
            });
            flag = true;
         } else if (itemstack.getItem() == Items.GLASS_BOTTLE) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);

            if (level.isServerSide) {
               itemstack.shrink(1);  // Decrease the bottle count
               ItemStack newItem = new ItemStack(Items.HONEY_BOTTLE); // Default item

               // Random chance for royal jelly
               if (level.random.nextInt(10) == 0) {
                  newItem = new ItemStack(Items.ROYAL_JELLY);
               }

               // Check if the player's hand is empty and replace the empty itemstack with the new item
               if (itemstack.isEmpty()) {
                  player.setItemInHand(hand, newItem);
               } else if (!player.inventory.add(newItem)) {
                  // Drop the new item if it can't be added to the inventory
                  player.drop(newItem, false);
               }
               player.inventory.setChanged();
            }

            flag = true;
         }
      }

      if (flag) {
         if (!CampfireBlock.isSmokeyPos(level, pos)) {
            if (this.hiveContainsBees(level, pos)) {
               this.angerNearbyBees(level, pos);
            }
            this.releaseBeesAndResetHoneyLevel(level, state, pos, player, BeehiveTileEntity.State.EMERGENCY);
         } else {
            this.resetHoneyLevel(level, state, pos);
         }

         return ActionResultType.sidedSuccess(level.isClientSide);
      } else {
         return super.use(state, level, pos, player, hand, result);
      }
   }


   private boolean hiveContainsBees(World world, BlockPos pos) {
      TileEntity tileentity = world.getBlockEntity(pos);
      if (tileentity instanceof BeehiveTileEntity) {
         BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
         return !beehivetileentity.isEmpty();
      } else {
         return false;
      }
   }

   public void releaseBeesAndResetHoneyLevel(World blockPos, BlockState p_226877_2_, BlockPos p_226877_3_, @Nullable PlayerEntity p_226877_4_, BeehiveTileEntity.State p_226877_5_) {
      this.resetHoneyLevel(blockPos, p_226877_2_, p_226877_3_);
      TileEntity tileentity = blockPos.getBlockEntity(p_226877_3_);
      if (tileentity instanceof BeehiveTileEntity) {
         BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
         beehivetileentity.emptyAllLivingFromHive(p_226877_4_, p_226877_2_, p_226877_5_);
      }

   }

   public void resetHoneyLevel(World p_226876_1_, BlockState p_226876_2_, BlockPos p_226876_3_) {
      p_226876_1_.setBlock(p_226876_3_, p_226876_2_.setValue(HONEY_LEVEL, Integer.valueOf(0)), 3);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState state, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (state.getValue(HONEY_LEVEL) >= 5) {
         for(int i = 0; i < p_180655_4_.nextInt(1) + 1; ++i) {
            this.trySpawnDripParticles(p_180655_2_, p_180655_3_, state);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   private void trySpawnDripParticles(World p_226879_1_, BlockPos p_226879_2_, BlockState p_226879_3_) {
      if (p_226879_3_.getFluidState().isEmpty() && !(p_226879_1_.random.nextFloat() < 0.3F)) {
         VoxelShape voxelshape = p_226879_3_.getCollisionShape(p_226879_1_, p_226879_2_);
         double d0 = voxelshape.max(Direction.Axis.Y);
         if (d0 >= 1.0D && !p_226879_3_.is(BlockTags.IMPERMEABLE)) {
            double d1 = voxelshape.min(Direction.Axis.Y);
            if (d1 > 0.0D) {
               this.spawnParticle(p_226879_1_, p_226879_2_, voxelshape, (double)p_226879_2_.getY() + d1 - 0.05D);
            } else {
               BlockPos blockpos = p_226879_2_.below();
               BlockState blockstate = p_226879_1_.getBlockState(blockpos);
               VoxelShape voxelshape1 = blockstate.getCollisionShape(p_226879_1_, blockpos);
               double d2 = voxelshape1.max(Direction.Axis.Y);
               if ((d2 < 1.0D || !blockstate.isCollisionShapeFullBlock(p_226879_1_, blockpos)) && blockstate.getFluidState().isEmpty()) {
                  this.spawnParticle(p_226879_1_, p_226879_2_, voxelshape, (double)p_226879_2_.getY() - 0.05D);
               }
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   private void spawnParticle(World p_226880_1_, BlockPos p_226880_2_, VoxelShape p_226880_3_, double p_226880_4_) {
      this.spawnFluidParticle(p_226880_1_, (double)p_226880_2_.getX() + p_226880_3_.min(Direction.Axis.X), (double)p_226880_2_.getX() + p_226880_3_.max(Direction.Axis.X), (double)p_226880_2_.getZ() + p_226880_3_.min(Direction.Axis.Z), (double)p_226880_2_.getZ() + p_226880_3_.max(Direction.Axis.Z), p_226880_4_);
   }

   @OnlyIn(Dist.CLIENT)
   private void spawnFluidParticle(World p_226875_1_, double p_226875_2_, double p_226875_4_, double p_226875_6_, double p_226875_8_, double p_226875_10_) {
      p_226875_1_.addParticle(ParticleTypes.DRIPPING_HONEY, MathHelper.lerp(p_226875_1_.random.nextDouble(), p_226875_2_, p_226875_4_), p_226875_10_, MathHelper.lerp(p_226875_1_.random.nextDouble(), p_226875_6_, p_226875_8_), 0.0D, 0.0D, 0.0D);
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HONEY_LEVEL, FACING);
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   @Nullable
   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new BeehiveTileEntity();
   }

   public void playerWillDestroy(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      if (!p_176208_1_.isClientSide && p_176208_4_.isCreative() && p_176208_1_.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
         TileEntity tileentity = p_176208_1_.getBlockEntity(p_176208_2_);
         if (tileentity instanceof BeehiveTileEntity) {
            BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
            ItemStack itemstack = new ItemStack(this);
            int i = p_176208_3_.getValue(HONEY_LEVEL);
            boolean flag = !beehivetileentity.isEmpty();
            if (!flag && i == 0) {
               return;
            }

            if (flag) {
               CompoundNBT compoundnbt = new CompoundNBT();
               compoundnbt.put("Bees", beehivetileentity.writeBees());
               itemstack.addTagElement("BlockEntityTag", compoundnbt);
            }

            CompoundNBT compoundnbt1 = new CompoundNBT();
            compoundnbt1.putInt("honey_level", i);
            itemstack.addTagElement("BlockStateTag", compoundnbt1);
            ItemEntity itementity = new ItemEntity(p_176208_1_, (double)p_176208_2_.getX(), (double)p_176208_2_.getY(), (double)p_176208_2_.getZ(), itemstack);
            itementity.setDefaultPickUpDelay();
            p_176208_1_.addFreshEntity(itementity);
         }
      }

      super.playerWillDestroy(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
      Entity entity = p_220076_2_.getOptionalParameter(LootParameters.THIS_ENTITY);
      if (entity instanceof TNTEntity || entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof WitherEntity || entity instanceof TNTMinecartEntity) {
         TileEntity tileentity = p_220076_2_.getOptionalParameter(LootParameters.BLOCK_ENTITY);
         if (tileentity instanceof BeehiveTileEntity) {
            BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
            beehivetileentity.emptyAllLivingFromHive((PlayerEntity)null, p_220076_1_, BeehiveTileEntity.State.EMERGENCY);
         }
      }

      return super.getDrops(p_220076_1_, p_220076_2_);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_4_.getBlockState(p_196271_6_).getBlock() instanceof FireBlock) {
         TileEntity tileentity = p_196271_4_.getBlockEntity(p_196271_5_);
         if (tileentity instanceof BeehiveTileEntity) {
            BeehiveTileEntity beehivetileentity = (BeehiveTileEntity)tileentity;
            beehivetileentity.emptyAllLivingFromHive((PlayerEntity)null, p_196271_1_, BeehiveTileEntity.State.EMERGENCY);
         }
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public static Direction getRandomOffset(Random p_235331_0_) {
      return Util.getRandom(SPAWN_DIRECTIONS, p_235331_0_);
   }
}