package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FillCommand {
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((size, limit) -> new TranslationTextComponent("commands.fill.toobig", size, limit));
   private static final BlockStateInput HOLLOW_CORE = new BlockStateInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), (CompoundNBT) null);
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.fill.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("fill").requires(source -> source.hasPermission(2))
              .then(Commands.argument("from", BlockPosArgument.blockPos())
                      .then(Commands.argument("to", BlockPosArgument.blockPos())
                              .then(Commands.argument("block", BlockStateArgument.block())
                                      .executes(context -> fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")), BlockStateArgument.getBlock(context, "block"), Mode.REPLACE, null))
                                      .then(Commands.literal("replace")
                                              .executes(context -> fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")), BlockStateArgument.getBlock(context, "block"), Mode.REPLACE, null))
                                              .then(Commands.argument("filter1", BlockPredicateArgument.blockPredicate())
                                                      .executes(context -> fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")), BlockStateArgument.getBlock(context, "block"), Mode.REPLACE, BlockPredicateArgument.getBlockPredicate(context, "filter1")))
                                                      .then(Commands.argument("filter2", BlockPredicateArgument.blockPredicate())
                                                              .executes(context -> fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")), BlockStateArgument.getBlock(context, "block"), Mode.REPLACE, combinePredicates(BlockPredicateArgument.getBlockPredicate(context, "filter1"), BlockPredicateArgument.getBlockPredicate(context, "filter2"))))
                                                              .then(Commands.argument("filter3", BlockPredicateArgument.blockPredicate())
                                                                      .executes(context -> fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")), BlockStateArgument.getBlock(context, "block"), Mode.REPLACE, combinePredicates(BlockPredicateArgument.getBlockPredicate(context, "filter1"), BlockPredicateArgument.getBlockPredicate(context, "filter2"), BlockPredicateArgument.getBlockPredicate(context, "filter3"))))
                                                                      .then(Commands.argument("filter4", BlockPredicateArgument.blockPredicate())
                                                                              .executes(context -> fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")), BlockStateArgument.getBlock(context, "block"), Mode.REPLACE, combinePredicates(BlockPredicateArgument.getBlockPredicate(context, "filter1"), BlockPredicateArgument.getBlockPredicate(context, "filter2"), BlockPredicateArgument.getBlockPredicate(context, "filter3"), BlockPredicateArgument.getBlockPredicate(context, "filter4"))))
                                                                              .then(Commands.argument("filter5", BlockPredicateArgument.blockPredicate())
                                                                                      .executes(context -> fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")), BlockStateArgument.getBlock(context, "block"), Mode.REPLACE, combinePredicates(BlockPredicateArgument.getBlockPredicate(context, "filter1"), BlockPredicateArgument.getBlockPredicate(context, "filter2"), BlockPredicateArgument.getBlockPredicate(context, "filter3"), BlockPredicateArgument.getBlockPredicate(context, "filter4"), BlockPredicateArgument.getBlockPredicate(context, "filter5"))))
                                                                              )
                                                                      )
                                                              )
                                                      )
                                              )
                                      )
                                      .then(Commands.literal("keep").executes(context -> fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")), BlockStateArgument.getBlock(context, "block"), Mode.REPLACE, cachedBlockInfo -> cachedBlockInfo.getLevel().isEmptyBlock(cachedBlockInfo.getPos()))))
                                      .then(Commands.literal("outline").executes(context -> fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")), BlockStateArgument.getBlock(context, "block"), Mode.OUTLINE, null)))
                                      .then(Commands.literal("hollow").executes(context -> fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")), BlockStateArgument.getBlock(context, "block"), Mode.HOLLOW, null)))
                                      .then(Commands.literal("destroy").executes(context -> fillBlocks(context.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(context, "from"), BlockPosArgument.getLoadedBlockPos(context, "to")), BlockStateArgument.getBlock(context, "block"), Mode.DESTROY, null)))
                              )
                      )
              )
      );
   }

   private static Predicate<CachedBlockInfo> combinePredicates(Predicate<CachedBlockInfo>... predicates) {
      return Stream.of(predicates).reduce(x -> false, Predicate::or);
   }

   private static int fillBlocks(CommandSource source, MutableBoundingBox boundingBox, BlockStateInput blockStateInput, Mode mode, @Nullable Predicate<CachedBlockInfo> filter) throws CommandSyntaxException {
      int volume = boundingBox.getXSpan() * boundingBox.getYSpan() * boundingBox.getZSpan();
      if (volume > 1111132768) {
         throw ERROR_AREA_TOO_LARGE.create(32768, volume);
      } else {
         List<BlockPos> affectedBlocks = Lists.newArrayList();
         ServerWorld world = source.getLevel();
         int affectedCount = 0;

         for (BlockPos pos : BlockPos.betweenClosed(boundingBox.x0, boundingBox.y0, boundingBox.z0, boundingBox.x1, boundingBox.y1, boundingBox.z1)) {
            if (filter == null || filter.test(new CachedBlockInfo(world, pos, true))) {
               BlockStateInput stateInput = mode.filter.filter(boundingBox, pos, blockStateInput, world);
               if (stateInput != null) {
                  TileEntity tileEntity = world.getBlockEntity(pos);
                  IClearable.tryClear(tileEntity);
                  if (stateInput.place(world, pos, 2)) {
                     affectedBlocks.add(pos.immutable());
                     affectedCount++;
                  }
               }
            }
         }

         for (BlockPos pos : affectedBlocks) {
            Block block = world.getBlockState(pos).getBlock();
            world.blockUpdated(pos, block);
         }

         if (affectedCount == 0) {
            throw ERROR_FAILED.create();
         } else {
            source.sendSuccess(new TranslationTextComponent("commands.fill.success", affectedCount), true);
            return affectedCount;
         }
      }
   }

   static enum Mode {
      REPLACE((boundingBox, pos, blockStateInput, world) -> blockStateInput),
      OUTLINE((boundingBox, pos, blockStateInput, world) -> (pos.getX() != boundingBox.x0 && pos.getX() != boundingBox.x1 && pos.getY() != boundingBox.y0 && pos.getY() != boundingBox.y1 && pos.getZ() != boundingBox.z0 && pos.getZ() != boundingBox.z1) ? null : blockStateInput),
      HOLLOW((boundingBox, pos, blockStateInput, world) -> (pos.getX() != boundingBox.x0 && pos.getX() != boundingBox.x1 && pos.getY() != boundingBox.y0 && pos.getY() != boundingBox.y1 && pos.getZ() != boundingBox.z0 && pos.getZ() != boundingBox.z1) ? HOLLOW_CORE : blockStateInput),
      DESTROY((boundingBox, pos, blockStateInput, world) -> {
         world.destroyBlock(pos, true);
         return blockStateInput;
      });

      public final SetBlockCommand.IFilter filter;

      private Mode(SetBlockCommand.IFilter filter) {
         this.filter = filter;
      }
   }
}
