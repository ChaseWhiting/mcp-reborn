package net.minecraft.block;

import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

public class FanBlock extends Block {
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
   public static final IntegerProperty VECTOR_BY_CLICK = BlockStateProperties.AGE_5;
   private static double upVector = 0.17;
   private static double vector = 0.1;
   private static double itemVector = 0.05;
   private static double reachDistance = 5;

   public FanBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any()
              .setValue(FACING, Direction.NORTH)
              .setValue(POWERED, Boolean.valueOf(false))
              .setValue(INVERTED, Boolean.valueOf(false))
              .setValue(VECTOR_BY_CLICK, Integer.valueOf(0)));
   }


   @Override
   public void onPlace(BlockState blockState, World world, BlockPos pos, BlockState blockState1, boolean value) {
      super.onPlace(blockState, world, pos, blockState1, value);
      // Call neighborChanged to handle initial power state
      this.neighborChanged(blockState, world, pos, null, pos, false);
      world.getBlockTicks().scheduleTick(pos, this, 3);
   }

   @Override
   public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
      super.animateTick(state, world, pos, random);
      boolean flag = state.getValue(INVERTED);
      if (state.getValue(POWERED)) {
      Direction direction = state.getValue(FACING);

      double particlePosX = pos.getX() + 0.5 + direction.getStepX() * 0.6;
      double particlePosY = pos.getY() + 0.5 + direction.getStepY() * 0.6;
      double particlePosZ = pos.getZ() + 0.5 + direction.getStepZ() * 0.6;

      double particleVelocityX = direction.getStepX() * 0.1;
      double particleVelocityY = direction.getStepY() * 0.1;
      double particleVelocityZ = direction.getStepZ() * 0.1;

      AxisAlignedBB aabb = calculateAABB(world, pos, direction);

      for (int i = 0; i < 5; i++) {
         if (flag) {
            // Get the end position of the AABB
            double endPosX = aabb.minX + (aabb.maxX - aabb.minX) / 2.0;
            double endPosY = aabb.minY + (aabb.maxY - aabb.minY) / 2.0;
            double endPosZ = aabb.minZ + (aabb.maxZ - aabb.minZ) / 2.0;

            world.addParticle(ParticleTypes.CLOUD, endPosX, endPosY, endPosZ, -particleVelocityX, -particleVelocityY, -particleVelocityZ);

            // Spawn particles at the end and move them towards the fan
            double endPosXX = pos.getX() + 0.5 - direction.getStepX() * 0.6;
            double endPosYY = pos.getY() + 0.5 - direction.getStepY() * 0.6;
            double endPosZZ = pos.getZ() + 0.5 - direction.getStepZ() * 0.6;


            world.addParticle(ParticleTypes.CLOUD, endPosXX, endPosYY, endPosZZ, -particleVelocityX, -particleVelocityY, -particleVelocityZ);
         } else {
            world.addParticle(ParticleTypes.CLOUD, particlePosX, particlePosY, particlePosZ, particleVelocityX, particleVelocityY, particleVelocityZ);
         }
       }
      }
   }

   public ActionResultType use(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTraceResult) {
      if (player.mayBuild()) {
         if (world.isClientSide) {
            return ActionResultType.SUCCESS;
         } else {
            if (player.isShiftKeyDown()) {
               // Player is sneaking, modify the VECTOR_BY_CLICK property
               int currentAge = blockState.getValue(VECTOR_BY_CLICK);
               int newAge = (currentAge + 1) % 6;

               BlockState newState = blockState.setValue(VECTOR_BY_CLICK, newAge);
               world.setBlock(pos, newState, 2);

               // Send a message to the player
               player.sendMessage(new StringTextComponent("New Vector Value: " + newAge), player.getUUID());
               updateVectorValues(blockState);
               return ActionResultType.SUCCESS;
            } else {
               // Not sneaking, perform the existing block state cycle inversion
               BlockState blockstate = blockState.cycle(INVERTED);
               world.setBlock(pos, blockstate, 4);
               return ActionResultType.CONSUME;
            }
         }
      } else {
         return super.use(blockState, world, pos, player, hand, blockRayTraceResult);
      }
   }

   private void resetVectors() {
      // Assuming these are instance variables or they can be reset in some other way
      upVector = 0.17;
      vector = 0.1;
      itemVector = 0.05;
      reachDistance = 5;
   }

   private void updateVectorValues(BlockState state) {
      int value = state.getValue(VECTOR_BY_CLICK);
      switch (value) {
         case 1:
            upVector = 0.2;
            vector = 0.14;
            itemVector = 0.08;
            reachDistance = 5.3;
            break;
         case 2:
            upVector = 0.24;
            vector = 0.16;
            itemVector = 0.1;
            reachDistance = 5.4;
            break;
         case 3:
            upVector = 0.26;
            vector = 0.2;
            itemVector = 0.123;
            reachDistance = 5.5;
            break;
         case 4:
            upVector = 0.28;
            vector = 0.23;
            itemVector = 0.15;
            reachDistance = 5.7;
            break;
         case 5:
            upVector = 0.3;
            vector = 0.25;
            itemVector = 0.17;
            reachDistance = 5.85;
         case 0:
            resetVectors();
            break;
      }

   }



   public BlockState getStateForPlacement(BlockItemUseContext blockItemUseContext) {
      return this.defaultBlockState()
              .setValue(FACING, blockItemUseContext.getNearestLookingDirection().getOpposite())
              .setValue(INVERTED, blockItemUseContext.getPlayer().isCrouching());
   }

   @Override
   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, POWERED, INVERTED, VECTOR_BY_CLICK);
   }

   @Override
   public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
      super.neighborChanged(state, world, pos, block, fromPos, isMoving);
      boolean isPowered = world.hasNeighborSignal(pos);
      if (isPowered != state.getValue(POWERED)) {
         world.setBlock(pos, state.setValue(POWERED, isPowered), 3);
         if (isPowered) {
            world.getBlockTicks().scheduleTick(pos, this, 1);
         }
      }
   }

   @Override
   public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (state.getValue(POWERED)) {
         handleEntities(world, pos, state.getValue(FACING), state.getValue(INVERTED));
         world.getBlockTicks().scheduleTick(pos, this, 1);
      }
   }

   private void handleEntities(World world, BlockPos pos, Direction direction, boolean inverted) {
      Vector3d baseVector = new Vector3d(direction.getStepX(), direction.getStepY(), direction.getStepZ());
      if (inverted) {
         baseVector = baseVector.reverse();
      }

      AxisAlignedBB areaOfEffect = calculateAABB(world, pos, direction);
      List<Entity> entities = world.getEntities(null, areaOfEffect);

      for (Entity entity : entities) {
         double distance = entity.position().distanceTo(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
         double scale = Math.max(0.1, 1 - (distance / reachDistance)); // Scale factor reduces with distance

         if (entity instanceof ItemEntity) {
            Vector3d itemVectorScaled = baseVector.scale(itemVector);
            entity.push(itemVectorScaled);
         } else {
            double adjustedVector = (direction == Direction.UP && entity instanceof PlayerEntity) ? upVector * scale * 1.5 : (direction == Direction.UP ? upVector : vector) * scale;
            Vector3d movementVector = baseVector.scale(adjustedVector);
            entity.setDeltaMovement(entity.getDeltaMovement().add(movementVector));
         }
         if(direction == Direction.UP)
            entity.fallDistance = 0;

         entity.hasImpulse = true;

         if (entity instanceof PlayerEntity) {
            entity.hurtMarked = true;
         }
      }
   }

   private AxisAlignedBB calculateAABB(World world, BlockPos pos, Direction direction) {
      AxisAlignedBB aabb = new AxisAlignedBB(pos);

      for (int i = 1; i <= reachDistance; i++) {
         BlockPos offsetPos = pos.relative(direction, i);
         BlockState blockState = world.getBlockState(offsetPos);
         if (world.isEmptyBlock(offsetPos) || !blockState.isSolidRender(world, offsetPos)) {
            aabb = aabb.expandTowards(direction.getStepX(), direction.getStepY(), direction.getStepZ());
         } else {
            break;
         }
      }

      return aabb;
   }
}
