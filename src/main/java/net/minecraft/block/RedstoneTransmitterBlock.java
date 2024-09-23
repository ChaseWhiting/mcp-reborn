package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class RedstoneTransmitterBlock extends Block implements ITileEntityProvider {
   public static final IntegerProperty FREQUENCY = IntegerProperty.create("frequency", 1, 10); // Frequency range 1-10

   public RedstoneTransmitterBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any().setValue(FREQUENCY, 1));
   }

   @Override
   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
      if (!world.isClientSide) {
         int currentFrequency = state.getValue(FREQUENCY);
         int newFrequency = currentFrequency == 10 ? 1 : currentFrequency + 1;
         world.setBlock(pos, state.setValue(FREQUENCY, newFrequency), 3);
         world.playSound(null, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 1.0F);
         player.displayClientMessage(new StringTextComponent("Set Frequency to " + newFrequency), true);
      }
      return ActionResultType.sidedSuccess(world.isClientSide);
   }

   @Override
   public TileEntity newBlockEntity(IBlockReader worldIn) {
      return new RedstoneTransmitterTileEntity();
   }

   @Override
   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FREQUENCY);
   }

   @Override
   public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
      TileEntity tileEntity = world.getBlockEntity(pos);
      if (tileEntity instanceof RedstoneTransmitterTileEntity) {
         RedstoneTransmitterTileEntity transmitter = (RedstoneTransmitterTileEntity) tileEntity;
         transmitter.transmitSignal();  // Transmit wireless signal every tick
      }
   }

   public class RedstoneTransmitterTileEntity extends TileEntity {
      private int frequency;
      private int signalStrength;

      public RedstoneTransmitterTileEntity() {
         super(TileEntityType.COMPARATOR); // Replace with custom TileEntityType
      }

      public void transmitSignal() {
         if (this.level != null && !this.level.isClientSide) {
            int range = this.signalStrength * 10; // Example: Signal strength determines range
            BlockPos receiverPos = findReceiver(this.level, this.worldPosition, this.frequency, range);
            if (receiverPos != null) {
               this.level.setBlock(receiverPos, this.level.getBlockState(receiverPos).setValue(BlockStateProperties.POWERED, true), 3);
            }
         }
      }

      private BlockPos findReceiver(World world, BlockPos pos, int frequency, int range) {
         for (BlockPos targetPos : BlockPos.betweenClosed(pos.offset(-range, -range, -range), pos.offset(range, range, range))) {
            BlockState state = world.getBlockState(targetPos);
            if (state.getBlock() instanceof RedstoneReceiverBlock) {
               int receiverFrequency = state.getValue(RedstoneReceiverBlock.FREQUENCY);
               if (receiverFrequency == frequency) {
                  return targetPos;
               }
            }
         }
         return null;
      }

      @Override
      public CompoundNBT save(CompoundNBT compound) {
         super.save(compound);
         compound.putInt("Frequency", this.frequency);
         return compound;
      }

      @Override
      public void load(BlockState state, CompoundNBT nbt) {
         super.load(state, nbt);
         this.frequency = nbt.getInt("Frequency");
      }
   }


}
