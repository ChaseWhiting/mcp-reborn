package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import java.util.List;

public class CustomFunctionCommand {

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder<CommandSource> baseCommand = Commands.literal("func")
              .requires(source -> source.hasPermission(2));

      dispatcher.register(baseCommand
              .then(addWithArguments(Commands.literal("with")))
              .then(addAsArguments(Commands.literal("as")))
              .then(addWithoutArguments(Commands.literal("without")))
              .then(addIfElseArguments(Commands.literal("if"), true))
              .then(addIfElseArguments(Commands.literal("else"), false))
              .then(addBlockOperations(Commands.literal("block")))
              .then(addEntitySpawn(Commands.literal("spawn"))));
   }

   private static ArgumentBuilder<CommandSource, ?> addWithArguments(LiteralArgumentBuilder<CommandSource> command) {
      return command
              .then(Commands.argument("attribute", ResourceLocationArgument.id())
                      .then(Commands.argument("value", IntegerArgumentType.integer())
                              .executes(context -> applyAttribute(context))));
   }

   private static ArgumentBuilder<CommandSource, ?> addAsArguments(LiteralArgumentBuilder<CommandSource> command) {
      return command
              .then(Commands.argument("targets", EntityArgument.entities())
                      .executes(context -> applyAs(context)));
   }

   private static ArgumentBuilder<CommandSource, ?> addWithoutArguments(LiteralArgumentBuilder<CommandSource> command) {
      return command
              .then(Commands.literal("ai")
                      .then(Commands.argument("target", EntityArgument.entity())
                              .executes(context -> removeAI(context))));
   }

   private static ArgumentBuilder<CommandSource, ?> addIfElseArguments(LiteralArgumentBuilder<CommandSource> command, boolean isIf) {
      return command
              .then(Commands.argument("condition", NBTPathArgument.nbtPath())
                      .then(Commands.literal("block")
                              .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                      .then(Commands.argument("block", BlockStateArgument.block())
                                              .executes(context -> checkBlockCondition(context, isIf))))));
   }

   private static ArgumentBuilder<CommandSource, ?> addBlockOperations(LiteralArgumentBuilder<CommandSource> command) {
      return command
              .then(Commands.literal("destroy")
                      .then(Commands.argument("pos", BlockPosArgument.blockPos())
                              .executes(context -> destroyBlock(context))))
              .then(Commands.literal("replace")
                      .then(Commands.argument("pos", BlockPosArgument.blockPos())
                              .then(Commands.argument("newBlock", BlockStateArgument.block())
                                      .executes(context -> replaceBlock(context)))));
   }

   private static ArgumentBuilder<CommandSource, ?> addEntitySpawn(LiteralArgumentBuilder<CommandSource> command) {
      return command
              .then(Commands.argument("entity", ResourceLocationArgument.id())
                      .then(Commands.argument("pos", Vec3Argument.vec3())
                              .then(Commands.argument("health", IntegerArgumentType.integer())
                                      .executes(context -> spawnEntityWithAttributes(context)))));
   }

   private static int applyAttribute(CommandContext<CommandSource> context) {
      // Logic for applying an attribute (e.g., "health" or other entity attributes)
      return 1;
   }

   private static int applyAs(CommandContext<CommandSource> context) {
      // Logic for applying operations as another entity
      return 1;
   }

   private static int removeAI(CommandContext<CommandSource> context) {
      try {
         Entity entity = EntityArgument.getEntity(context, "target");
         entity.as(Mob.class).setNoAi(true);  // Disable AI for the entity
      } catch (CommandSyntaxException e) {
         throw new RuntimeException(e);
      }
      return 1;
   }

   private static int checkBlockCondition(CommandContext<CommandSource> context, boolean isIf) {
      try {
         BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
         ServerWorld world = context.getSource().getLevel();
         if (isIf && world.getBlockState(pos).getBlock() == BlockStateArgument.getBlock(context, "block").getState().getBlock()) {
            context.getSource().sendSuccess(new StringTextComponent("Condition met."), false);
            return 1;
         } else if (!isIf) {
            context.getSource().sendSuccess(new StringTextComponent("Condition not met."), false);
            return 1;
         }
      } catch (CommandSyntaxException e) {
         throw new RuntimeException(e);
      }
      return 0;
   }

   private static int destroyBlock(CommandContext<CommandSource> context) {
      try {
         BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
         ServerWorld world = context.getSource().getLevel();
         world.destroyBlock(pos, true);
      } catch (CommandSyntaxException e) {
         throw new RuntimeException(e);
      }
      return 1;
   }

   private static int replaceBlock(CommandContext<CommandSource> context) {
      try {
         BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
         Block block = BlockStateArgument.getBlock(context, "newBlock").getState().getBlock();
         ServerWorld world = context.getSource().getLevel();
         world.setBlock(pos, block.defaultBlockState(), 3);
      } catch (CommandSyntaxException e) {
         throw new RuntimeException(e);
      }
      return 1;
   }

   private static int spawnEntityWithAttributes(CommandContext<CommandSource> context) {
      try {
         ResourceLocation entityId = ResourceLocationArgument.getId(context, "entity");
         EntityType<?> entityType = Registry.ENTITY_TYPE.get(entityId);
         BlockPos pos = Vec3Argument.getVec3(context, "pos").asBlockPos();
         int health = IntegerArgumentType.getInteger(context, "health");

         ServerWorld world = context.getSource().getLevel();
         Entity entity = entityType.create(world);
         if (entity != null) {
            entity.setPos(pos.getX(), pos.getY(), pos.getZ());
            entity.as(Mob.class).setHealth(health);  // Setting health, more attributes can be added similarly
            world.addFreshEntity(entity);
         }
      } catch (CommandSyntaxException e) {
         throw new RuntimeException(e);
      }

      return 1;
   }
}
