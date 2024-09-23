package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.Mob;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SummonCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.failed"));
   private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.failed.uuid"));
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.invalidPosition"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("summon")
              .requires(source -> source.hasPermission(2))
              .then(Commands.argument("entity", EntitySummonArgument.id())
                      .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                      .executes(context -> spawnEntity(context.getSource(), EntitySummonArgument.getSummonableEntity(context, "entity"), context.getSource().getPosition(), new CompoundNBT(), true, 1))
                      .then(Commands.argument("pos", Vec3Argument.vec3())
                              .executes(context -> spawnEntity(context.getSource(), EntitySummonArgument.getSummonableEntity(context, "entity"), Vec3Argument.getVec3(context, "pos"), new CompoundNBT(), true, 1))
                              .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                      .executes(context -> spawnEntity(context.getSource(), EntitySummonArgument.getSummonableEntity(context, "entity"), Vec3Argument.getVec3(context, "pos"), new CompoundNBT(), false, IntegerArgumentType.getInteger(context, "count"))))
                              .then(Commands.argument("nbt", NBTCompoundTagArgument.compoundTag())
                                      .executes(context -> spawnEntity(context.getSource(), EntitySummonArgument.getSummonableEntity(context, "entity"), Vec3Argument.getVec3(context, "pos"), NBTCompoundTagArgument.getCompoundTag(context, "nbt"), false, 1))
                                      .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                              .executes(context -> spawnEntity(context.getSource(), EntitySummonArgument.getSummonableEntity(context, "entity"), Vec3Argument.getVec3(context, "pos"), NBTCompoundTagArgument.getCompoundTag(context, "nbt"), false, IntegerArgumentType.getInteger(context, "count"))))))));
   }

   private static int spawnEntity(CommandSource source, ResourceLocation entityLocation, Vector3d position, CompoundNBT nbtData, boolean applySpawningLogic, int count) throws CommandSyntaxException {
      BlockPos blockPos = new BlockPos(position);
      if (!World.isInSpawnableBounds(blockPos)) {
         throw INVALID_POSITION.create();
      }

      ServerWorld serverWorld = source.getLevel();
      int spawnedEntities = 0;
      String entityLocationString = entityLocation.toString();
      String nameOfEntity = entityLocationString.replace("minecraft", "entity.minecraft").replace(":", ".");
      TranslationTextComponent nameTranslation = new TranslationTextComponent(nameOfEntity);
      for (int i = 0; i < count; i++) {
         CompoundNBT compoundNBT = nbtData.copy();
         compoundNBT.putString("id", entityLocation.toString());
         Entity entity = EntityType.loadEntityRecursive(compoundNBT, serverWorld, (loadedEntity) -> {
            loadedEntity.moveTo(position.x, position.y, position.z, loadedEntity.yRot, loadedEntity.xRot);
            return loadedEntity;
         });

         if (entity == null) {
            throw ERROR_FAILED.create();
         }

         if (applySpawningLogic && entity instanceof Mob) {
            ((Mob) entity).finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(entity.blockPosition()), SpawnReason.COMMAND, null, null);
         }

         if (!serverWorld.tryAddFreshEntityWithPassengers(entity)) {
            throw ERROR_DUPLICATE_UUID.create();
         } else {
            entity.onSpawned(source, position, compoundNBT);
            spawnedEntities++;
         }
      }

      if (spawnedEntities > 0) {
         if (spawnedEntities == 1) {
            source.sendSuccess(new TranslationTextComponent("commands.summon.success", nameTranslation.getString()), true);
         } else {
            source.sendSuccess(new TranslationTextComponent("commands.summon.success_multiple", spawnedEntities, nameTranslation.getString()), true);
         }
      }
      return spawnedEntities;
   }
}
