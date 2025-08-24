package net.minecraft.command.impl;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DebugUtils;
import net.minecraft.util.PathCalculation;
import net.minecraft.util.ProfilerParser;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.NuclearExplosion;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {

   private static boolean debugPathEnabled = false;
   private static boolean debugGoalEnabled = false;
   private static boolean debugRaidEnabled = false;
   private static boolean debugBeeEnabled = false;
   private static boolean debugBrainAIEnabled = false;
   private static boolean debugRaccoonEnabled = false;
   private static boolean debugNeighbourEnabled = false;


   private static boolean shouldRender = true;
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(new TranslationTextComponent("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(new TranslationTextComponent("commands.debug.alreadyRunning"));
   @Nullable
   private static final FileSystemProvider ZIP_FS_PROVIDER = FileSystemProvider.installedProviders().stream().filter((p_225386_0_) -> {
      return p_225386_0_.getScheme().equalsIgnoreCase("jar");
   }).findFirst().orElse((FileSystemProvider)null);

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(
              Commands.literal("debug")
                      .requires((source) -> source.hasPermission(3))

                      .then(Commands.literal("start").executes((context) -> start(context.getSource())))

                      .then(Commands.literal("stop").executes((context) -> stop(context.getSource())))

                      .then(Commands.literal("report").executes((context) -> report(context.getSource())))

                      .then(Commands.literal("inventory")
                              .then(Commands.argument("target", EntityArgument.entity())
                                      .executes((context) -> inventory(context))))

                      .then(Commands.literal("wantedpos")
                              .then(Commands.argument("target1", EntityArgument.entity())
                                      .executes((context) -> wantedPos(context))))

                      .then(Commands.literal("setTargetPos")
                              .then(Commands.argument("target", EntityArgument.entity())
                                      .then(Commands.argument("x", IntegerArgumentType.integer())
                                              .then(Commands.argument("y", IntegerArgumentType.integer())
                                                      .then(Commands.argument("z", IntegerArgumentType.integer())
                                                              .then(Commands.argument("speed", FloatArgumentType.floatArg(0F, 5F))
                                                                      .executes((context) -> setTargetPos(context))))))))

                      .then(Commands.literal("stopNavigation")
                              .then(Commands.argument("targets", EntityArgument.entities())
                                      .executes((context) -> stopNavigation(context))))

                      .then(Commands.literal("goalRender").executes(DebugCommand::showGoalRender))

                      .then(Commands.literal("pathRender").executes(DebugCommand::showPathRender))

                      .then(Commands.literal("raidRender").executes(DebugCommand::showRaidRender))

                      .then(Commands.literal("brainAIRender").executes(DebugCommand::showBrainAIRender))

                      .then(Commands.literal("beeRender").executes(DebugCommand::showBeeRender))

                      .then(Commands.literal("raccoonRender").executes(DebugCommand::showRaccoonRender))

                      .then(Commands.literal("neighbourRender").executes(DebugCommand::showNeighbourRender))

                      .then(Commands.literal("stopRendering").executes(DebugCommand::stopRender))

                      .then(Commands.literal("renderAll").executes(DebugCommand::renderAll))

                      .then(Commands.literal("testNodes").then(Commands.argument("radius", IntegerArgumentType.integer(0, 40)).executes(context -> findPossibleNodes(context, IntegerArgumentType.getInteger(context, "radius")))))

                      .then(Commands.literal("nuke")
                              .then(Commands.argument("radius", IntegerArgumentType.integer(0,500))
                                      .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                              .executes(context -> nuclearBombTest(context, IntegerArgumentType.getInteger(context, "radius"), BlockPosArgument.getLoadedBlockPos(context, "pos"))))))






      );
   }

   public static boolean isDebugPathEnabled() {
      return debugPathEnabled;
   }

   public static boolean isDebugGoalEnabled() {
      return debugGoalEnabled;
   }

   public static boolean isDebugRaidEnabled() {
      return debugGoalEnabled;
   }

   public static boolean isDebugRaccoonEnabled() {

      return debugRaccoonEnabled;
   }

   public static boolean isDebugNeighbourUpdatesEnabled() {

      return debugNeighbourEnabled;
   }

   public static int stopRender(CommandContext<CommandSource> context) {
      debugRaidEnabled = false;
      debugBrainAIEnabled = false;
      debugBeeEnabled = false;
      debugGoalEnabled = false;
      debugPathEnabled = false;
      debugNeighbourEnabled = false;
      shouldRender = false;
      return 1;
   }

   public static int renderAll(CommandContext<CommandSource> context) {
      debugRaidEnabled = true;
      debugBrainAIEnabled = true;
      debugBeeEnabled = true;
      debugGoalEnabled = true;
      debugPathEnabled = true;
      debugNeighbourEnabled = true;
      shouldRender = true;
      return 1;
   }

   public static int nuclearBombTest(CommandContext<CommandSource> context, int radius, BlockPos explosion) {
      NuclearExplosion nuclearExplosion = new NuclearExplosion(context.getSource().getLevel(), null, null, null, explosion.getX(),explosion.getY(),explosion.getZ(),radius == 0 ? 6 : radius, true, NuclearExplosion.Mode.DESTROY);
      nuclearExplosion.explode();
      nuclearExplosion.finalizeExplosion(true);

      return 1;
   }

   public static boolean isDebugBeeEnabled() {
      return debugBeeEnabled;
   }
   public static boolean isDebugBrainAIEnabled() {
      return debugBrainAIEnabled;
   }

   public static boolean shouldRenderAll() {
      return shouldRender;
   }

   public static int showPathRender(CommandContext<CommandSource> context) {
      debugPathEnabled = !debugPathEnabled;
      return 1;
   }

   public static  int showRaccoonRender(CommandContext<CommandSource> context) {
      debugRaccoonEnabled = !debugRaccoonEnabled;
      return 1;
   }

   public static  int showNeighbourRender(CommandContext<CommandSource> context) {
      debugNeighbourEnabled = !debugNeighbourEnabled;
      return 1;
   }
   public static int showBrainAIRender(CommandContext<CommandSource> context) {
      debugBrainAIEnabled = !debugBrainAIEnabled;
      return 1;
   }
   public static int showBeeRender(CommandContext<CommandSource> context) {
      debugBeeEnabled = !debugBeeEnabled;
      return 1;
   }

   public static int showRaidRender(CommandContext<CommandSource> context) {
      debugRaidEnabled = !debugRaidEnabled;
      return 1;
   }

   public static int showGoalRender(CommandContext<CommandSource> context) {
      debugGoalEnabled = !debugGoalEnabled;
      return 1;
   }

   public static int findPossibleNodes(CommandContext<CommandSource> context, int radius) {
      CommandSource source = context.getSource();
      World world = source.getLevel();
      ServerPlayerEntity player;

       try {
           player = source.getPlayerOrException();
       } catch (CommandSyntaxException e) {
           throw new RuntimeException(e);
       }
      BlockPos blockPos = player.blockPosition();
       List<PathPoint> calc = PathCalculation.calculateOptimalNodesForPlayer(player, world, radius);
       List<BlockPos> positions = new ArrayList<>();
       for(PathPoint point : calc) {
          positions.add(point.asBlockPos());
       }

          if (!positions.isEmpty()) {

             for(BlockPos positions1 : positions) {
                BlockPos placeBlocks = positions1.offset(0, 1, 0);
                world.setBlock(placeBlocks, Blocks.TARGET.defaultBlockState(), 3);
             }
             source.sendSuccess(new StringTextComponent("Available nodes:" + positions.toString()), false);
          } else {
             source.sendFailure(new StringTextComponent("No available nodes that are pathfindable."));
             return 0;
          }

      return 1;
   }


   public static int stopNavigation(CommandContext<CommandSource> context) {
      try {
         Collection<? extends Entity> entities = EntityArgument.getEntities(context, "targets");
         int count = 0;
         for (Entity entity : entities) {
            if (entity instanceof Mob) {
               Mob mob = (Mob) entity;
               mob.getNavigation().stop();
               count++;
            }
         }
         if (count > 0) {
            context.getSource().sendSuccess(new StringTextComponent("Successfully stopped navigation of " + count + " entities."), false);
         } else {
            context.getSource().sendFailure(new StringTextComponent("No mobs found to stop navigation."));
         }
      } catch (CommandSyntaxException e) {
         context.getSource().sendFailure(new StringTextComponent("No valid targets specified."));
         return 0;
      }
      return 1;
   }

   private static int start(CommandSource p_198335_0_) throws CommandSyntaxException {
      MinecraftServer minecraftserver = p_198335_0_.getServer();
      if (minecraftserver.isProfiling()) {
         throw ERROR_ALREADY_RUNNING.create();
      } else {
         minecraftserver.startProfiling();
         p_198335_0_.sendSuccess(new TranslationTextComponent("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
         return 0;
      }
   }

   private static int stop(CommandSource p_198336_0_) throws CommandSyntaxException {
      MinecraftServer minecraftserver = p_198336_0_.getServer();
      if (!minecraftserver.isProfiling()) {
         throw ERROR_NOT_RUNNING.create();
      } else {
         IProfileResult iprofileresult = minecraftserver.finishProfiling();
         File file1 = new File(minecraftserver.getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
         iprofileresult.saveResults(file1);
         float f = (float)iprofileresult.getNanoDuration() / 1.0E9F;
         float f1 = (float)iprofileresult.getTickDuration() / f;
         p_198336_0_.sendSuccess(new TranslationTextComponent("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", f), iprofileresult.getTickDuration(), String.format("%.2f", f1)), true);
          ProfilerParser.parse("debug");
          return MathHelper.floor(f1);
      }
   }

   private static int report(CommandSource p_225389_0_) {
      MinecraftServer minecraftserver = p_225389_0_.getServer();
      String s = "debug-report-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());

      try {
         Path path1 = minecraftserver.getFile("debug").toPath();
         Files.createDirectories(path1);
         if (!SharedConstants.IS_RUNNING_IN_IDE && ZIP_FS_PROVIDER != null) {
            Path path2 = path1.resolve(s + ".zip");

            try (FileSystem filesystem = ZIP_FS_PROVIDER.newFileSystem(path2, ImmutableMap.of("create", "true"))) {
               minecraftserver.saveDebugReport(filesystem.getPath("/"));
            }
         } else {
            Path path = path1.resolve(s);
            minecraftserver.saveDebugReport(path);
         }

         p_225389_0_.sendSuccess(new TranslationTextComponent("commands.debug.reportSaved", s), false);
         return 1;
      } catch (IOException ioexception) {
         LOGGER.error("Failed to save debug dump", (Throwable)ioexception);
         p_225389_0_.sendFailure(new TranslationTextComponent("commands.debug.reportFailed"));
         return 0;
      }
   }

   private static int wantedPos(CommandContext<CommandSource> context) {
      return DebugUtils.getWantedPos(context);
   }

   private static int inventory(CommandContext<CommandSource> context) {
      return DebugUtils.getInventory(context);
   }

   private static int setTargetPos(CommandContext<CommandSource> context) {
      return DebugUtils.setWantedPos(context);
   }
}