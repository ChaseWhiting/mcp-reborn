package net.minecraft.server.management;

import java.util.Objects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Gamemode;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerInteractionManager {
   private static final Logger LOGGER = LogManager.getLogger();
   public ServerWorld level;
   public ServerPlayerEntity player;
   private Gamemode gameModeForPlayer = Gamemode.NOT_SET;
   private Gamemode previousGameModeForPlayer = Gamemode.NOT_SET;
   private boolean isDestroyingBlock;
   private int destroyProgressStart;
   private BlockPos destroyPos = BlockPos.ZERO;
   private int gameTicks;
   private boolean hasDelayedDestroy;
   private BlockPos delayedDestroyPos = BlockPos.ZERO;
   private int delayedTickStart;
   private int lastSentState = -1;

   public PlayerInteractionManager(ServerWorld p_i50702_1_) {
      this.level = p_i50702_1_;
   }

   public void setGameModeForPlayer(Gamemode p_73076_1_) {
      this.setGameModeForPlayer(p_73076_1_, p_73076_1_ != this.gameModeForPlayer ? this.gameModeForPlayer : this.previousGameModeForPlayer);
   }

   public void setGameModeForPlayer(Gamemode p_241820_1_, Gamemode p_241820_2_) {
      this.previousGameModeForPlayer = p_241820_2_;
      this.gameModeForPlayer = p_241820_1_;
      p_241820_1_.updatePlayerAbilities(this.player.abilities);
      this.player.onUpdateAbilities();
      this.player.server.getPlayerList().broadcastAll(new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_GAME_MODE, this.player));
      this.level.updateSleepingPlayerList();
   }

   public Gamemode getGameModeForPlayer() {
      return this.gameModeForPlayer;
   }

   public Gamemode getPreviousGameModeForPlayer() {
      return this.previousGameModeForPlayer;
   }

   public boolean isSurvival() {
      return this.gameModeForPlayer.isSurvival();
   }

   public boolean isCreative() {
      return this.gameModeForPlayer.isCreative();
   }

   public void updateGameMode(Gamemode p_73077_1_) {
      if (this.gameModeForPlayer == Gamemode.NOT_SET) {
         this.gameModeForPlayer = p_73077_1_;
      }

      this.setGameModeForPlayer(this.gameModeForPlayer);
   }

   public void tick() {
      ++this.gameTicks;
      if (this.hasDelayedDestroy) {
         BlockState blockstate = this.level.getBlockState(this.delayedDestroyPos);
         if (blockstate.isAir()) {
            this.hasDelayedDestroy = false;
         } else {
            float f = this.incrementDestroyProgress(blockstate, this.delayedDestroyPos, this.delayedTickStart);
            if (f >= 1.0F) {
               this.hasDelayedDestroy = false;
               this.destroyBlock(this.delayedDestroyPos);
            }
         }
      } else if (this.isDestroyingBlock) {
         BlockState blockstate1 = this.level.getBlockState(this.destroyPos);
         if (blockstate1.isAir()) {
            this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
            this.lastSentState = -1;
            this.isDestroyingBlock = false;
         } else {
            this.incrementDestroyProgress(blockstate1, this.destroyPos, this.destroyProgressStart);
         }
      }

   }

   private float incrementDestroyProgress(BlockState p_229859_1_, BlockPos p_229859_2_, int p_229859_3_) {
      int i = this.gameTicks - p_229859_3_;
      float f = p_229859_1_.getDestroyProgress(this.player, this.player.level, p_229859_2_) * (float)(i + 1);
      int j = (int)(f * 10.0F);
      if (j != this.lastSentState) {
         this.level.destroyBlockProgress(this.player.getId(), p_229859_2_, j);
         this.lastSentState = j;
      }

      return f;
   }

   public void handleBlockBreakAction(BlockPos p_225416_1_, CPlayerDiggingPacket.Action p_225416_2_, Direction p_225416_3_, int p_225416_4_) {
      double d0 = this.player.getX() - ((double)p_225416_1_.getX() + 0.5D);
      double d1 = this.player.getY() - ((double)p_225416_1_.getY() + 0.5D) + 1.5D;
      double d2 = this.player.getZ() - ((double)p_225416_1_.getZ() + 0.5D);
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;
      if (d3 > 36.0D) {
         this.player.connection.send(new SPlayerDiggingPacket(p_225416_1_, this.level.getBlockState(p_225416_1_), p_225416_2_, false, "too far"));
      } else if (p_225416_1_.getY() >= p_225416_4_) {
         this.player.connection.send(new SPlayerDiggingPacket(p_225416_1_, this.level.getBlockState(p_225416_1_), p_225416_2_, false, "too high"));
      } else {
         if (p_225416_2_ == CPlayerDiggingPacket.Action.START_DESTROY_BLOCK) {
            if (!this.level.mayInteract(this.player, p_225416_1_)) {
               this.player.connection.send(new SPlayerDiggingPacket(p_225416_1_, this.level.getBlockState(p_225416_1_), p_225416_2_, false, "may not interact"));
               return;
            }

            if (this.isCreative()) {
               this.destroyAndAck(p_225416_1_, p_225416_2_, "creative destroy");
               return;
            }

            if (this.player.blockActionRestricted(this.level, p_225416_1_, this.gameModeForPlayer)) {
               this.player.connection.send(new SPlayerDiggingPacket(p_225416_1_, this.level.getBlockState(p_225416_1_), p_225416_2_, false, "block action restricted"));
               return;
            }

            this.destroyProgressStart = this.gameTicks;
            float f = 1.0F;
            BlockState blockstate = this.level.getBlockState(p_225416_1_);
            if (!blockstate.isAir()) {
               blockstate.attack(this.level, p_225416_1_, this.player);
               f = blockstate.getDestroyProgress(this.player, this.player.level, p_225416_1_);
            }

            if (!blockstate.isAir() && f >= 1.0F) {
               this.destroyAndAck(p_225416_1_, p_225416_2_, "insta mine");
            } else {
               if (this.isDestroyingBlock) {
                  this.player.connection.send(new SPlayerDiggingPacket(this.destroyPos, this.level.getBlockState(this.destroyPos), CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, false, "abort destroying since another started (client insta mine, server disagreed)"));
               }

               this.isDestroyingBlock = true;
               this.destroyPos = p_225416_1_.immutable();
               int i = (int)(f * 10.0F);
               this.level.destroyBlockProgress(this.player.getId(), p_225416_1_, i);
               this.player.connection.send(new SPlayerDiggingPacket(p_225416_1_, this.level.getBlockState(p_225416_1_), p_225416_2_, true, "actual start of destroying"));
               this.lastSentState = i;
            }
         } else if (p_225416_2_ == CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK) {
            if (p_225416_1_.equals(this.destroyPos)) {
               int j = this.gameTicks - this.destroyProgressStart;
               BlockState blockstate1 = this.level.getBlockState(p_225416_1_);
               if (!blockstate1.isAir()) {
                  float f1 = blockstate1.getDestroyProgress(this.player, this.player.level, p_225416_1_) * (float)(j + 1);
                  if (f1 >= 0.7F) {
                     this.isDestroyingBlock = false;
                     this.level.destroyBlockProgress(this.player.getId(), p_225416_1_, -1);
                     this.destroyAndAck(p_225416_1_, p_225416_2_, "destroyed");
                     return;
                  }

                  if (!this.hasDelayedDestroy) {
                     this.isDestroyingBlock = false;
                     this.hasDelayedDestroy = true;
                     this.delayedDestroyPos = p_225416_1_;
                     this.delayedTickStart = this.destroyProgressStart;
                  }
               }
            }

            this.player.connection.send(new SPlayerDiggingPacket(p_225416_1_, this.level.getBlockState(p_225416_1_), p_225416_2_, true, "stopped destroying"));
         } else if (p_225416_2_ == CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK) {
            this.isDestroyingBlock = false;
            if (!Objects.equals(this.destroyPos, p_225416_1_)) {
               LOGGER.warn("Mismatch in destroy block pos: " + this.destroyPos + " " + p_225416_1_);
               this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
               this.player.connection.send(new SPlayerDiggingPacket(this.destroyPos, this.level.getBlockState(this.destroyPos), p_225416_2_, true, "aborted mismatched destroying"));
            }

            this.level.destroyBlockProgress(this.player.getId(), p_225416_1_, -1);
            this.player.connection.send(new SPlayerDiggingPacket(p_225416_1_, this.level.getBlockState(p_225416_1_), p_225416_2_, true, "aborted destroying"));
         }

      }
   }

   public void destroyAndAck(BlockPos p_229860_1_, CPlayerDiggingPacket.Action p_229860_2_, String p_229860_3_) {
      if (this.destroyBlock(p_229860_1_)) {
         this.player.connection.send(new SPlayerDiggingPacket(p_229860_1_, this.level.getBlockState(p_229860_1_), p_229860_2_, true, p_229860_3_));
      } else {
         this.player.connection.send(new SPlayerDiggingPacket(p_229860_1_, this.level.getBlockState(p_229860_1_), p_229860_2_, false, p_229860_3_));
      }

   }

   public boolean destroyBlock(BlockPos p_180237_1_) {
      BlockState blockstate = this.level.getBlockState(p_180237_1_);
      if (!this.player.getMainHandItem().getItem().canAttackBlock(blockstate, this.level, p_180237_1_, this.player)) {
         return false;
      } else {
         TileEntity tileentity = this.level.getBlockEntity(p_180237_1_);
         Block block = blockstate.getBlock();
         if ((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.player.canUseGameMasterBlocks()) {
            this.level.sendBlockUpdated(p_180237_1_, blockstate, blockstate, 3);
            return false;
         } else if (this.player.blockActionRestricted(this.level, p_180237_1_, this.gameModeForPlayer)) {
            return false;
         } else {
            block.playerWillDestroy(this.level, p_180237_1_, blockstate, this.player);
            boolean flag = this.level.removeBlock(p_180237_1_, false);
            if (flag) {
               block.destroy(this.level, p_180237_1_, blockstate);
            }

            if (this.isCreative()) {
               return true;
            } else {
               ItemStack itemstack = this.player.getMainHandItem();
               ItemStack itemstack1 = itemstack.copy();
               boolean flag1 = this.player.hasCorrectToolForDrops(blockstate);
               itemstack.mineBlock(this.level, blockstate, p_180237_1_, this.player);
               if (flag && flag1) {
                  block.playerDestroy(this.level, this.player, p_180237_1_, blockstate, tileentity, itemstack1);
               }

               return true;
            }
         }
      }
   }

   public ActionResultType useItem(ServerPlayerEntity p_187250_1_, World p_187250_2_, ItemStack p_187250_3_, Hand p_187250_4_) {
      if (this.gameModeForPlayer == Gamemode.SPECTATOR) {
         return ActionResultType.PASS;
      } else if (p_187250_1_.getCooldowns().isOnCooldown(p_187250_3_.getItem())) {
         return ActionResultType.PASS;
      } else {
         int i = p_187250_3_.getCount();
         int j = p_187250_3_.getDamageValue();
         ActionResult<ItemStack> actionresult = p_187250_3_.use(p_187250_2_, p_187250_1_, p_187250_4_);
         ItemStack itemstack = actionresult.getObject();
         if (itemstack == p_187250_3_ && itemstack.getCount() == i && itemstack.getUseDuration() <= 0 && itemstack.getDamageValue() == j) {
            return actionresult.getResult();
         } else if (actionresult.getResult() == ActionResultType.FAIL && itemstack.getUseDuration() > 0 && !p_187250_1_.isUsingItem()) {
            return actionresult.getResult();
         } else {
            p_187250_1_.setItemInHand(p_187250_4_, itemstack);
            if (this.isCreative()) {
               itemstack.setCount(i);
               if (itemstack.isDamageableItem() && itemstack.getDamageValue() != j) {
                  itemstack.setDamageValue(j);
               }
            }

            if (itemstack.isEmpty()) {
               p_187250_1_.setItemInHand(p_187250_4_, ItemStack.EMPTY);
            }

            if (!p_187250_1_.isUsingItem()) {
               p_187250_1_.refreshContainer(p_187250_1_.inventoryMenu);
            }

            return actionresult.getResult();
         }
      }
   }

   public ActionResultType useItemOn(ServerPlayerEntity p_219441_1_, World p_219441_2_, ItemStack p_219441_3_, Hand p_219441_4_, BlockRayTraceResult p_219441_5_) {
      BlockPos blockpos = p_219441_5_.getBlockPos();
      BlockState blockstate = p_219441_2_.getBlockState(blockpos);
      if (this.gameModeForPlayer == Gamemode.SPECTATOR) {
         INamedContainerProvider inamedcontainerprovider = blockstate.getMenuProvider(p_219441_2_, blockpos);
         if (inamedcontainerprovider != null) {
            p_219441_1_.openMenu(inamedcontainerprovider);
            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.PASS;
         }
      } else {
         boolean flag = !p_219441_1_.getMainHandItem().isEmpty() || !p_219441_1_.getOffhandItem().isEmpty();
         boolean flag1 = p_219441_1_.isSecondaryUseActive() && flag;
         ItemStack itemstack = p_219441_3_.copy();
         if (!flag1) {
            ActionResultType actionresulttype = blockstate.use(p_219441_2_, p_219441_1_, p_219441_4_, p_219441_5_);
            if (actionresulttype.consumesAction()) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(p_219441_1_, blockpos, itemstack);
               return actionresulttype;
            }
         }

         if (!p_219441_3_.isEmpty() && !p_219441_1_.getCooldowns().isOnCooldown(p_219441_3_.getItem())) {
            ItemUseContext itemusecontext = new ItemUseContext(p_219441_1_, p_219441_4_, p_219441_5_);
            ActionResultType actionresulttype1;
            if (this.isCreative()) {
               int i = p_219441_3_.getCount();
               actionresulttype1 = p_219441_3_.useOn(itemusecontext);
               p_219441_3_.setCount(i);
            } else {
               actionresulttype1 = p_219441_3_.useOn(itemusecontext);
            }

            if (actionresulttype1.consumesAction()) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(p_219441_1_, blockpos, itemstack);
            }

            return actionresulttype1;
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   public void setLevel(ServerWorld p_73080_1_) {
      this.level = p_73080_1_;
   }
}