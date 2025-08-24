package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Fluid {
   public static final ObjectIntIdentityMap<FluidState> FLUID_STATE_REGISTRY = new ObjectIntIdentityMap<>();
   protected final StateContainer<Fluid, FluidState> stateDefinition;
   private FluidState defaultFluidState;

   protected Fluid() {
      StateContainer.Builder<Fluid, FluidState> builder = new StateContainer.Builder<>(this);
      this.createFluidStateDefinition(builder);
      this.stateDefinition = builder.create(Fluid::defaultFluidState, FluidState::new);
      this.registerDefaultState(this.stateDefinition.any());
   }

   protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> p_207184_1_) {
   }

   public StateContainer<Fluid, FluidState> getStateDefinition() {
      return this.stateDefinition;
   }

   protected final void registerDefaultState(FluidState p_207183_1_) {
      this.defaultFluidState = p_207183_1_;
   }

   public final FluidState defaultFluidState() {
      return this.defaultFluidState;
   }

   public abstract Item getBucket();

   @OnlyIn(Dist.CLIENT)
   protected void animateTick(World p_204522_1_, BlockPos p_204522_2_, FluidState p_204522_3_, Random p_204522_4_) {
   }

   protected void tick(World p_207191_1_, BlockPos p_207191_2_, FluidState p_207191_3_) {
   }

   protected void randomTick(World p_207186_1_, BlockPos p_207186_2_, FluidState p_207186_3_, Random p_207186_4_) {
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   protected IParticleData getDripParticle() {
      return null;
   }

   protected abstract boolean canBeReplacedWith(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_);

   protected abstract Vector3d getFlow(IBlockReader p_215663_1_, BlockPos p_215663_2_, FluidState p_215663_3_);

   public abstract int getTickDelay(IWorldReader p_205569_1_);

   protected boolean isRandomlyTicking() {
      return false;
   }

   protected boolean isEmpty() {
      return false;
   }

   protected abstract float getExplosionResistance();

   public abstract float getHeight(FluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_);

   public abstract float getOwnHeight(FluidState p_223407_1_);

   protected abstract BlockState createLegacyBlock(FluidState p_204527_1_);

   public abstract boolean isSource(FluidState p_207193_1_);

   public abstract int getAmount(FluidState p_207192_1_);

   public boolean isSame(Fluid p_207187_1_) {
      return p_207187_1_ == this;
   }

   public boolean is(ITag<Fluid> p_207185_1_) {
      return p_207185_1_.contains(this);
   }

   public abstract VoxelShape getShape(FluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_);

   @Nullable
   public AxisAlignedBB getAABB(FluidState fluidState, IWorld blockGetter, BlockPos blockPos) {
      if (this.isEmpty()) {
         return null;
      }
      float f = fluidState.getHeight(blockGetter, blockPos);
      return new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), (double)blockPos.getX() + 1.0, (float)blockPos.getY() + f, (double)blockPos.getZ() + 1.0);
   }
}