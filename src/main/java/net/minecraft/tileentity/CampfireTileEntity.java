package net.minecraft.tileentity;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CampfireTileEntity extends TileEntity implements IClearable, ITickableTileEntity {
   private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
   private final int[] cookingProgress = new int[4];
   private final int[] cookingTime = new int[4];

   public CampfireTileEntity() {
      super(TileEntityType.CAMPFIRE);
   }

   // ... existing fields ...

   // Tunables
   private static final int ATTEMPTS_PER_BURST_MIN = 6;
   private static final int ATTEMPTS_PER_BURST_MAX = 12;
   private static final int RADIUS_XZ = 3;   // horizontal spread radius
   private static final int RADIUS_Y_UP = 4; // up
   private static final int RADIUS_Y_DOWN = 1; // down

   // Soft throttle: average one burst every ~2–6 seconds (20 ticks/sec).
   // We randomize each check so it's not periodic and stacks poorly.
   private static final int BURST_CHECK_EVERY_TICKS = 20; // check once per second
   private static final float BASE_BURST_CHANCE = 0.35f;  // chance per check to do a burst

   @Override
   public void tick() {
      if (this.level == null) return;

      boolean client = this.level.isClientSide;
      BlockState state = this.getBlockState();
      boolean lit = state.hasProperty(CampfireBlock.LIT) && state.getValue(CampfireBlock.LIT);

      if (client) {
         if (lit) this.makeParticles();
         return;
      }

      // Server side
      if (lit) {
         this.cook();
         tryStartIgnitionBurst((ServerWorld) this.level, this.worldPosition, state);
      } else {
         // existing cooling logic...
         for (int i = 0; i < this.items.size(); ++i) {
            if (this.cookingProgress[i] > 0) {
               this.cookingProgress[i] = MathHelper.clamp(this.cookingProgress[i] - 2, 0, this.cookingTime[i]);
            }
         }
      }
   }

   private void tryStartIgnitionBurst(ServerWorld world, BlockPos pos, BlockState campfireState) {
      if (!world.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) return;
      if (!world.getGameRules().getBoolean(GameRules.RULE_VERYHARD)) return;

      if ((world.getGameTime() % BURST_CHECK_EVERY_TICKS) != 0) return;

      Random rng = world.random;

      FireBlock fire = (FireBlock) Blocks.FIRE;
      int localFlammable = 0;
      for (BlockPos bp : BlockPos.betweenClosed(
              pos.offset(-RADIUS_XZ, -RADIUS_Y_DOWN, -RADIUS_XZ),
              pos.offset( RADIUS_XZ,  RADIUS_Y_UP,    RADIUS_XZ))) {
         if (bp.equals(pos)) continue;
         if (fire.canBurn(world.getBlockState(bp))) localFlammable++;
      }
      if (localFlammable == 0) return;

      float burstChance = Math.min(0.9f, BASE_BURST_CHANCE + localFlammable * 0.02f);
      if (rng.nextFloat() >= burstChance) return;

      int attempts = ATTEMPTS_PER_BURST_MIN + rng.nextInt(ATTEMPTS_PER_BURST_MAX - ATTEMPTS_PER_BURST_MIN + 1);
      for (int a = 0; a < attempts; a++) {
         BlockPos target = pos.offset(
                 rng.nextInt(RADIUS_XZ * 2 + 1) - RADIUS_XZ,
                 rng.nextInt(RADIUS_Y_UP + RADIUS_Y_DOWN + 1) - RADIUS_Y_DOWN,
                 rng.nextInt(RADIUS_XZ * 2 + 1) - RADIUS_XZ
         );

         if (!world.isEmptyBlock(target)) continue;
         if (world.isRainingAt(target)) continue;

         boolean nearFlammable = false;
         for (Direction d : Direction.values()) {
            if (fire.canBurn(world.getBlockState(target.relative(d)))) {
               nearFlammable = true;
               break;
            }
         }
         if (!nearFlammable) continue;

         BlockState fireState = fire.getStateForPlacement(world, target);
         if (!fireState.is(Blocks.FIRE)) continue;
         if (!fireState.canSurvive(world, target)) continue;

         world.setBlock(target, fireState, 11);
      }
   }

   // ... rest of your class ...

   private void cook() {
      for(int i = 0; i < this.items.size(); ++i) {
         ItemStack itemstack = this.items.get(i);
         if (!itemstack.isEmpty()) {
            int j = this.cookingProgress[i]++;
            if (this.cookingProgress[i] >= this.cookingTime[i]) {
               IInventory iinventory = new Inventory(itemstack);
               ItemStack itemstack1 = this.level.getRecipeManager().getRecipeFor(IRecipeType.CAMPFIRE_COOKING, iinventory, this.level).map((p_213979_1_) -> {
                  return p_213979_1_.assemble(iinventory, this.level.registryAccess());
               }).orElse(itemstack);
               BlockPos blockpos = this.getBlockPos();
               InventoryHelper.dropItemStack(this.level, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), itemstack1);
               this.items.set(i, ItemStack.EMPTY);
               this.markUpdated();
               level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(this.getBlockState()));
            }
         }
      }

   }

   private void makeParticles() {
      World world = this.getLevel();
      if (world != null) {
         BlockPos blockpos = this.getBlockPos();
         Random random = world.random;
         if (random.nextFloat() < 0.11F) {
            for(int i = 0; i < random.nextInt(2) + 2; ++i) {
               CampfireBlock.makeParticles(world, blockpos, this.getBlockState().getValue(CampfireBlock.SIGNAL_FIRE), false);
            }
         }

         int l = this.getBlockState().getValue(CampfireBlock.FACING).get2DDataValue();

         for(int j = 0; j < this.items.size(); ++j) {
            if (!this.items.get(j).isEmpty() && random.nextFloat() < 0.2F) {
               Direction direction = Direction.from2DDataValue(Math.floorMod(j + l, 4));
               float f = 0.3125F;
               double d0 = (double)blockpos.getX() + 0.5D - (double)((float)direction.getStepX() * 0.3125F) + (double)((float)direction.getClockWise().getStepX() * 0.3125F);
               double d1 = (double)blockpos.getY() + 0.5D;
               double d2 = (double)blockpos.getZ() + 0.5D - (double)((float)direction.getStepZ() * 0.3125F) + (double)((float)direction.getClockWise().getStepZ() * 0.3125F);

               for(int k = 0; k < 4; ++k) {
                  world.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 5.0E-4D, 0.0D);
               }
            }
         }

      }
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      this.items.clear();
      ItemStackHelper.loadAllItems(p_230337_2_, this.items);
      if (p_230337_2_.contains("CookingTimes", 11)) {
         int[] aint = p_230337_2_.getIntArray("CookingTimes");
         System.arraycopy(aint, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, aint.length));
      }

      if (p_230337_2_.contains("CookingTotalTimes", 11)) {
         int[] aint1 = p_230337_2_.getIntArray("CookingTotalTimes");
         System.arraycopy(aint1, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, aint1.length));
      }

   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      this.saveMetadataAndItems(p_189515_1_);
      p_189515_1_.putIntArray("CookingTimes", this.cookingProgress);
      p_189515_1_.putIntArray("CookingTotalTimes", this.cookingTime);
      return p_189515_1_;
   }

   private CompoundNBT saveMetadataAndItems(CompoundNBT p_213983_1_) {
      super.save(p_213983_1_);
      ItemStackHelper.saveAllItems(p_213983_1_, this.items, true);
      return p_213983_1_;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.worldPosition, 13, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.saveMetadataAndItems(new CompoundNBT());
   }

   public Optional<CampfireCookingRecipe> getCookableRecipe(ItemStack p_213980_1_) {
      return this.items.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.level.getRecipeManager().getRecipeFor(IRecipeType.CAMPFIRE_COOKING, new Inventory(p_213980_1_), this.level);
   }

   public boolean placeFood(@Nullable Entity entity, ItemStack p_213984_1_, int p_213984_2_) {
      for(int i = 0; i < this.items.size(); ++i) {
         ItemStack itemstack = this.items.get(i);
         if (itemstack.isEmpty()) {
            this.cookingTime[i] = p_213984_2_;
            this.cookingProgress[i] = 0;
            this.items.set(i, p_213984_1_.split(1));
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
            this.markUpdated();
            return true;
         }
      }

      return false;
   }

   private void markUpdated() {
      this.setChanged();
      this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public void clearContent() {
      this.items.clear();
   }

   public void dowse() {
      if (this.level != null) {
         if (!this.level.isClientSide) {
            InventoryHelper.dropContents(this.level, this.getBlockPos(), this.getItems());
         }

         this.markUpdated();
      }

   }
}