package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class EntityCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.failed"));
   private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.failed.uuid"));
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.invalidPosition"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("entity")
              .requires(source -> source.hasPermission(2))
              .then(Commands.literal("count")
                      .then(Commands.argument("entity", EntitySummonArgument.id())
                              .suggests(SuggestionProviders.ALL_ENTITIES)
                              .executes(context -> getEntities(context.getSource(), EntitySummonArgument.getSummonableEntity(context, "entity")))
                      )
              )
      );
   }

   private static int getEntities(CommandSource source, ResourceLocation entityLocation) throws CommandSyntaxException {
      ServerWorld world = source.getLevel();
      int entitiesCounted = 0;
      EntityType<?> entityType = Registry.ENTITY_TYPE.get(entityLocation);
      Entity entity = entityType.create(world);
      if (entityType == null) {
         source.sendFailure(new StringTextComponent("Invalid entity type: " + entityLocation.toString()));
         return 0;
      }

      AxisAlignedBB AABB = source.getEntityOrException().getBoundingBox().inflate(400D);
      List<? extends Entity> foundEntities = world.getLoadedEntitiesOfClass(entity.getClass(), AABB);

      entitiesCounted = foundEntities.size();
      String entityLocationString = entityLocation.toString();
      String nameOfEntity = entityLocationString.replace("minecraft", "entity.minecraft").replace(":", ".");
      TranslationTextComponent nameTranslation = new TranslationTextComponent(nameOfEntity);
      boolean flag = entitiesCounted > 1;
      String formatted = String.format("There %s %d %s%s nearby", flag ? "are" : "is", entitiesCounted, nameTranslation.getString(), flag ? "s" : "");
      source.sendSuccess(new StringTextComponent(formatted), false);

      return entitiesCounted;
   }

}
