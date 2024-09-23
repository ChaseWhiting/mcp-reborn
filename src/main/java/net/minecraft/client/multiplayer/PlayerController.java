package net.minecraft.client.multiplayer;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StructureBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.network.play.client.CEnchantItemPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.network.play.client.CPlaceRecipePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Gamemode;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PlayerController {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final ClientPlayNetHandler connection;
   private BlockPos destroyBlockPos = new BlockPos(-1, -1, -1);
   private ItemStack destroyingItem = ItemStack.EMPTY;
   private float destroyProgress;
   private float destroyTicks;
   private int destroyDelay;
   private boolean isDestroying;
   private Gamemode localPlayerMode = Gamemode.SURVIVAL;
   private Gamemode previousLocalPlayerMode = Gamemode.NOT_SET;
   private final Object2ObjectLinkedOpenHashMap<Pair<BlockPos, CPlayerDiggingPacket.Action>, Vector3d> unAckedActions = new Object2ObjectLinkedOpenHashMap<>();
   private int carriedIndex;

   public PlayerController(Minecraft p_i45062_1_, ClientPlayNetHandler p_i45062_2_) {
      this.minecraft = p_i45062_1_;
      this.connection = p_i45062_2_;
   }

   public void adjustPlayer(PlayerEntity p_78748_1_) {
      this.localPlayerMode.updatePlayerAbilities(p_78748_1_.abilities);
   }

   public void setPreviousLocalMode(Gamemode p_241675_1_) {
      this.previousLocalPlayerMode = p_241675_1_;
   }

   public void setLocalMode(Gamemode p_78746_1_) {
      if (p_78746_1_ != this.localPlayerMode) {
         this.previousLocalPlayerMode = this.localPlayerMode;
      }

      this.localPlayerMode = p_78746_1_;
      this.localPlayerMode.updatePlayerAbilities(this.minecraft.player.abilities);
   }

   public boolean canHurtPlayer() {
      return this.localPlayerMode.isSurvival();
   }

   public boolean destroyBlock(BlockPos p_187103_1_) {
      if (this.minecraft.player.blockActionRestricted(this.minecraft.level, p_187103_1_, this.localPlayerMode)) {
         return false;
      } else {
         World world = this.minecraft.level;
         BlockState blockstate = world.getBlockState(p_187103_1_);
         if (!this.minecraft.player.getMainHandItem().getItem().canAttackBlock(blockstate, world, p_187103_1_, this.minecraft.player)) {
            return false;
         } else {
            Block block = blockstate.getBlock();
            if ((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.minecraft.player.canUseGameMasterBlocks()) {
               return false;
            } else if (blockstate.isAir()) {
               return false;
            } else {
               block.playerWillDestroy(world, p_187103_1_, blockstate, this.minecraft.player);
               FluidState fluidstate = world.getFluidState(p_187103_1_);
               boolean flag = world.setBlock(p_187103_1_, fluidstate.createLegacyBlock(), 11);
               if (flag) {
                  block.destroy(world, p_187103_1_, blockstate);
               }

               return flag;
            }
         }
      }
   }

   public boolean startDestroyBlock(BlockPos p_180511_1_, Direction p_180511_2_) {
      if (this.minecraft.player.blockActionRestricted(this.minecraft.level, p_180511_1_, this.localPlayerMode)) {
         return false;
      } else if (!this.minecraft.level.getWorldBorder().isWithinBounds(p_180511_1_)) {
         return false;
      } else {
         if (this.localPlayerMode.isCreative()) {
            BlockState blockstate = this.minecraft.level.getBlockState(p_180511_1_);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, p_180511_1_, blockstate, 1.0F);
            this.sendBlockAction(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, p_180511_1_, p_180511_2_);
            this.destroyBlock(p_180511_1_);
            this.destroyDelay = 5;
         } else if (!this.isDestroying || !this.sameDestroyTarget(p_180511_1_)) {
            if (this.isDestroying) {
               this.sendBlockAction(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, p_180511_2_);
            }

            BlockState blockstate1 = this.minecraft.level.getBlockState(p_180511_1_);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, p_180511_1_, blockstate1, 0.0F);
            this.sendBlockAction(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, p_180511_1_, p_180511_2_);
            boolean flag = !blockstate1.isAir();
            if (flag && this.destroyProgress == 0.0F) {
               blockstate1.attack(this.minecraft.level, p_180511_1_, this.minecraft.player);
            }

            if (flag && blockstate1.getDestroyProgress(this.minecraft.player, this.minecraft.player.level, p_180511_1_) >= 1.0F) {
               this.destroyBlock(p_180511_1_);
            } else {
               this.isDestroying = true;
               this.destroyBlockPos = p_180511_1_;
               this.destroyingItem = this.minecraft.player.getMainHandItem();
               this.destroyProgress = 0.0F;
               this.destroyTicks = 0.0F;
               this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, (int)(this.destroyProgress * 10.0F) - 1);
            }
         }

         return true;
      }
   }

   public void stopDestroyBlock() {
      if (this.isDestroying) {
         BlockState blockstate = this.minecraft.level.getBlockState(this.destroyBlockPos);
         this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, this.destroyBlockPos, blockstate, -1.0F);
         this.sendBlockAction(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, Direction.DOWN);
         this.isDestroying = false;
         this.destroyProgress = 0.0F;
         this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, -1);
         this.minecraft.player.resetAttackStrengthTicker();
      }

   }

   public boolean continueDestroyBlock(BlockPos p_180512_1_, Direction p_180512_2_) {
      this.ensureHasSentCarriedItem();
      if (this.destroyDelay > 0) {
         --this.destroyDelay;
         return true;
      } else if (this.localPlayerMode.isCreative() && this.minecraft.level.getWorldBorder().isWithinBounds(p_180512_1_)) {
         this.destroyDelay = 5;
         BlockState blockstate1 = this.minecraft.level.getBlockState(p_180512_1_);
         this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, p_180512_1_, blockstate1, 1.0F);
         this.sendBlockAction(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, p_180512_1_, p_180512_2_);
         this.destroyBlock(p_180512_1_);
         return true;
      } else if (this.sameDestroyTarget(p_180512_1_)) {
         BlockState blockstate = this.minecraft.level.getBlockState(p_180512_1_);
         if (blockstate.isAir()) {
            this.isDestroying = false;
            return false;
         } else {
            this.destroyProgress += blockstate.getDestroyProgress(this.minecraft.player, this.minecraft.player.level, p_180512_1_);
            if (this.destroyTicks % 4.0F == 0.0F) {
               SoundType soundtype = blockstate.getSoundType();
               this.minecraft.getSoundManager().play(new SimpleSound(soundtype.getHitSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F, p_180512_1_));
            }

            ++this.destroyTicks;
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, p_180512_1_, blockstate, MathHelper.clamp(this.destroyProgress, 0.0F, 1.0F));
            if (this.destroyProgress >= 1.0F) {
               this.isDestroying = false;
               this.sendBlockAction(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, p_180512_1_, p_180512_2_);
               this.destroyBlock(p_180512_1_);
               this.destroyProgress = 0.0F;
               this.destroyTicks = 0.0F;
               this.destroyDelay = 5;
            }

            this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, (int)(this.destroyProgress * 10.0F) - 1);
            return true;
         }
      } else {
         return this.startDestroyBlock(p_180512_1_, p_180512_2_);
      }
   }

   public float getPickRange() {
      return this.localPlayerMode.isCreative() ? 5.0F : 4.5F;
   }

   public void tick() {
      this.ensureHasSentCarriedItem();
      if (this.connection.getConnection().isConnected()) {
         this.connection.getConnection().tick();
      } else {
         this.connection.getConnection().handleDisconnection();
      }

   }

   private boolean sameDestroyTarget(BlockPos p_178893_1_) {
      ItemStack itemstack = this.minecraft.player.getMainHandItem();
      boolean flag = this.destroyingItem.isEmpty() && itemstack.isEmpty();
      if (!this.destroyingItem.isEmpty() && !itemstack.isEmpty()) {
         flag = itemstack.getItem() == this.destroyingItem.getItem() && ItemStack.tagMatches(itemstack, this.destroyingItem) && (itemstack.isDamageableItem() || itemstack.getDamageValue() == this.destroyingItem.getDamageValue());
      }

      return p_178893_1_.equals(this.destroyBlockPos) && flag;
   }

   private void ensureHasSentCarriedItem() {
      int i = this.minecraft.player.inventory.selected;
      if (i != this.carriedIndex) {
         this.carriedIndex = i;
         this.connection.send(new CHeldItemChangePacket(this.carriedIndex));
      }

   }

   public ActionResultType useItemOn(ClientPlayerEntity p_217292_1_, ClientWorld p_217292_2_, Hand p_217292_3_, BlockRayTraceResult p_217292_4_) {
      this.ensureHasSentCarriedItem();
      BlockPos blockpos = p_217292_4_.getBlockPos();
      if (!this.minecraft.level.getWorldBorder().isWithinBounds(blockpos)) {
         return ActionResultType.FAIL;
      } else {
         ItemStack itemstack = p_217292_1_.getItemInHand(p_217292_3_);
         if (this.localPlayerMode == Gamemode.SPECTATOR) {
            this.connection.send(new CPlayerTryUseItemOnBlockPacket(p_217292_3_, p_217292_4_));
            return ActionResultType.SUCCESS;
         } else {
            boolean flag = !p_217292_1_.getMainHandItem().isEmpty() || !p_217292_1_.getOffhandItem().isEmpty();
            boolean flag1 = p_217292_1_.isSecondaryUseActive() && flag;
            if (!flag1) {
               ActionResultType actionresulttype = p_217292_2_.getBlockState(blockpos).use(p_217292_2_, p_217292_1_, p_217292_3_, p_217292_4_);
               if (actionresulttype.consumesAction()) {
                  this.connection.send(new CPlayerTryUseItemOnBlockPacket(p_217292_3_, p_217292_4_));
                  return actionresulttype;
               }
            }

            this.connection.send(new CPlayerTryUseItemOnBlockPacket(p_217292_3_, p_217292_4_));
            if (!itemstack.isEmpty() && !p_217292_1_.getCooldowns().isOnCooldown(itemstack.getItem())) {
               ItemUseContext itemusecontext = new ItemUseContext(p_217292_1_, p_217292_3_, p_217292_4_);
               ActionResultType actionresulttype1;
               if (this.localPlayerMode.isCreative()) {
                  int i = itemstack.getCount();
                  actionresulttype1 = itemstack.useOn(itemusecontext);
                  itemstack.setCount(i);
               } else {
                  actionresulttype1 = itemstack.useOn(itemusecontext);
               }

               return actionresulttype1;
            } else {
               return ActionResultType.PASS;
            }
         }
      }
   }

   public ActionResultType useItem(PlayerEntity p_187101_1_, World p_187101_2_, Hand p_187101_3_) {
      if (this.localPlayerMode == Gamemode.SPECTATOR) {
         return ActionResultType.PASS;
      } else {
         this.ensureHasSentCarriedItem();
         this.connection.send(new CPlayerTryUseItemPacket(p_187101_3_));
         ItemStack itemstack = p_187101_1_.getItemInHand(p_187101_3_);
         if (p_187101_1_.getCooldowns().isOnCooldown(itemstack.getItem())) {
            return ActionResultType.PASS;
         } else {
            int i = itemstack.getCount();
            ActionResult<ItemStack> actionresult = itemstack.use(p_187101_2_, p_187101_1_, p_187101_3_);
            ItemStack itemstack1 = actionresult.getObject();
            if (itemstack1 != itemstack) {
               p_187101_1_.setItemInHand(p_187101_3_, itemstack1);
            }

            return actionresult.getResult();
         }
      }
   }

   public ClientPlayerEntity createPlayer(ClientWorld p_199681_1_, StatisticsManager p_199681_2_, ClientRecipeBook p_199681_3_) {
      return this.createPlayer(p_199681_1_, p_199681_2_, p_199681_3_, false, false);
   }

   public ClientPlayerEntity createPlayer(ClientWorld p_239167_1_, StatisticsManager p_239167_2_, ClientRecipeBook p_239167_3_, boolean p_239167_4_, boolean p_239167_5_) {
      return new ClientPlayerEntity(this.minecraft, p_239167_1_, this.connection, p_239167_2_, p_239167_3_, p_239167_4_, p_239167_5_);
   }

   public void attack(PlayerEntity p_78764_1_, Entity p_78764_2_) {
      this.ensureHasSentCarriedItem();
      this.connection.send(new CUseEntityPacket(p_78764_2_, p_78764_1_.isShiftKeyDown()));
      if (this.localPlayerMode != Gamemode.SPECTATOR) {
         p_78764_1_.attack(p_78764_2_);
         p_78764_1_.resetAttackStrengthTicker();
      }

   }

   public ActionResultType interact(PlayerEntity p_187097_1_, Entity p_187097_2_, Hand p_187097_3_) {
      this.ensureHasSentCarriedItem();
      this.connection.send(new CUseEntityPacket(p_187097_2_, p_187097_3_, p_187097_1_.isShiftKeyDown()));
      return this.localPlayerMode == Gamemode.SPECTATOR ? ActionResultType.PASS : p_187097_1_.interactOn(p_187097_2_, p_187097_3_);
   }

   public ActionResultType interactAt(PlayerEntity p_187102_1_, Entity p_187102_2_, EntityRayTraceResult p_187102_3_, Hand p_187102_4_) {
      this.ensureHasSentCarriedItem();
      Vector3d vector3d = p_187102_3_.getLocation().subtract(p_187102_2_.getX(), p_187102_2_.getY(), p_187102_2_.getZ());
      this.connection.send(new CUseEntityPacket(p_187102_2_, p_187102_4_, vector3d, p_187102_1_.isShiftKeyDown()));
      return this.localPlayerMode == Gamemode.SPECTATOR ? ActionResultType.PASS : p_187102_2_.interactAt(p_187102_1_, vector3d, p_187102_4_);
   }

   public ItemStack handleInventoryMouseClick(int p_187098_1_, int p_187098_2_, int p_187098_3_, ClickType p_187098_4_, PlayerEntity p_187098_5_) {
      short short1 = p_187098_5_.containerMenu.backup(p_187098_5_.inventory);
      ItemStack itemstack = p_187098_5_.containerMenu.clicked(p_187098_2_, p_187098_3_, p_187098_4_, p_187098_5_);
      this.connection.send(new CClickWindowPacket(p_187098_1_, p_187098_2_, p_187098_3_, p_187098_4_, itemstack, short1));
      return itemstack;
   }

   public void handlePlaceRecipe(int p_203413_1_, IRecipe<?> p_203413_2_, boolean p_203413_3_) {
      this.connection.send(new CPlaceRecipePacket(p_203413_1_, p_203413_2_, p_203413_3_));
   }

   public void handleInventoryButtonClick(int p_78756_1_, int p_78756_2_) {
      this.connection.send(new CEnchantItemPacket(p_78756_1_, p_78756_2_));
   }

   public void handleCreativeModeItemAdd(ItemStack p_78761_1_, int p_78761_2_) {
      if (this.localPlayerMode.isCreative()) {
         this.connection.send(new CCreativeInventoryActionPacket(p_78761_2_, p_78761_1_));
      }

   }

   public void handleCreativeModeItemDrop(ItemStack p_78752_1_) {
      if (this.localPlayerMode.isCreative() && !p_78752_1_.isEmpty()) {
         this.connection.send(new CCreativeInventoryActionPacket(-1, p_78752_1_));
      }

   }

   public void releaseUsingItem(PlayerEntity p_78766_1_) {
      this.ensureHasSentCarriedItem();
      this.connection.send(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
      p_78766_1_.releaseUsingItem();
   }

   public boolean hasExperience() {
      return this.localPlayerMode.isSurvival();
   }

   public boolean hasMissTime() {
      return !this.localPlayerMode.isCreative();
   }

   public boolean hasInfiniteItems() {
      return this.localPlayerMode.isCreative();
   }

   public boolean hasFarPickRange() {
      return this.localPlayerMode.isCreative();
   }

   public boolean isServerControlledInventory() {
      return this.minecraft.player.isPassenger() && this.minecraft.player.getVehicle() instanceof AbstractHorseEntity;
   }

   public boolean isAlwaysFlying() {
      return this.localPlayerMode == Gamemode.SPECTATOR;
   }

   public Gamemode getPreviousPlayerMode() {
      return this.previousLocalPlayerMode;
   }

   public Gamemode getPlayerMode() {
      return this.localPlayerMode;
   }

   public boolean isDestroying() {
      return this.isDestroying;
   }

   public void handlePickItem(int p_187100_1_) {
      this.connection.send(new CPickItemPacket(p_187100_1_));
   }

   private void sendBlockAction(CPlayerDiggingPacket.Action p_225324_1_, BlockPos p_225324_2_, Direction p_225324_3_) {
      ClientPlayerEntity clientplayerentity = this.minecraft.player;
      this.unAckedActions.put(Pair.of(p_225324_2_, p_225324_1_), clientplayerentity.position());
      this.connection.send(new CPlayerDiggingPacket(p_225324_1_, p_225324_2_, p_225324_3_));
   }

   public void handleBlockBreakAck(ClientWorld p_225323_1_, BlockPos p_225323_2_, BlockState p_225323_3_, CPlayerDiggingPacket.Action p_225323_4_, boolean p_225323_5_) {
      Vector3d vector3d = this.unAckedActions.remove(Pair.of(p_225323_2_, p_225323_4_));
      BlockState blockstate = p_225323_1_.getBlockState(p_225323_2_);
      if ((vector3d == null || !p_225323_5_ || p_225323_4_ != CPlayerDiggingPacket.Action.START_DESTROY_BLOCK && blockstate != p_225323_3_) && blockstate != p_225323_3_) {
         p_225323_1_.setKnownState(p_225323_2_, p_225323_3_);
         PlayerEntity playerentity = this.minecraft.player;
         if (vector3d != null && p_225323_1_ == playerentity.level && playerentity.isColliding(p_225323_2_, p_225323_3_)) {
            playerentity.absMoveTo(vector3d.x, vector3d.y, vector3d.z);
         }
      }

      while(this.unAckedActions.size() >= 50) {
         Pair<BlockPos, CPlayerDiggingPacket.Action> pair = this.unAckedActions.firstKey();
         this.unAckedActions.removeFirst();
         LOGGER.error("Too many unacked block actions, dropping " + pair);
      }

   }
}