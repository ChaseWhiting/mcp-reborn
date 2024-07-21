package net.minecraft.world.spawner;

import java.awt.*;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.command.impl.RaidCommand;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.EntityBuilder;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.server.ServerWorld;

public class PatrolSpawner implements ISpecialSpawner {
   private int nextTick;

   public int tick(ServerWorld serverWorld, boolean val, boolean val1) {
      if (!val) {
         return 0;
      } else if (!serverWorld.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
         return 0;
      } else {
         Random random = serverWorld.random;
         --this.nextTick;
         if (this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick += 12000 + random.nextInt(1200);
            long i = serverWorld.getDayTime() / 24000L;
            if (i >= 5L && serverWorld.isDay()) {
               if (random.nextInt(5) != 0) {
                  return 0;
               } else {
                  int j = serverWorld.players().size();
                  if (j < 1) {
                     return 0;
                  } else {
                     PlayerEntity playerentity = serverWorld.players().get(random.nextInt(j));
                     if (playerentity.isSpectator()) {
                        return 0;
                     }

                     BlockPos.Mutable blockpos$mutable = findSpawnPosition(serverWorld, playerentity.blockPosition(), random);
                     if (blockpos$mutable == null) {
                        return 0;
                     }

                     int i1 = spawnPillagers(serverWorld, blockpos$mutable, random);
                     return i1;
                  }
               }
            } else {
               return 0;
            }
         }
      }
   }


   private BlockPos.Mutable findSpawnPosition(ServerWorld serverWorld, BlockPos playerPos, Random random) {
      int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
      int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
      BlockPos.Mutable blockpos$mutable = playerPos.mutable().move(k, 0, l);

      if (!serverWorld.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)) {
         return null;
      }

      Biome biome = serverWorld.getBiome(blockpos$mutable);
      Biome.Category biome$category = biome.getBiomeCategory();
      if (biome$category == Biome.Category.MUSHROOM) {
         return null;
      }

      return blockpos$mutable;
   }

   private int spawnPillagers(ServerWorld serverWorld, BlockPos.Mutable blockpos$mutable, Random random) {
      int i1 = 0;
      int j1 = (int) Math.ceil((double) serverWorld.getCurrentDifficultyAt(blockpos$mutable).getEffectiveDifficulty()) + 1;

      for (int k1 = 0; k1 < j1; ++k1) {
         ++i1;
         blockpos$mutable.setY(serverWorld.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY());
         if (k1 == 0) {
            if (!this.spawnPatrolMember(serverWorld, blockpos$mutable, random, true)) {
               break;
            }
         } else {
            this.spawnPatrolMember(serverWorld, blockpos$mutable, random, false);
         }

         blockpos$mutable.setX(blockpos$mutable.getX() + random.nextInt(5) - random.nextInt(5));
         blockpos$mutable.setZ(blockpos$mutable.getZ() + random.nextInt(5) - random.nextInt(5));
      }


      return i1;
   }

   private boolean spawnPatrolMember(ServerWorld world, BlockPos pos, Random random, boolean setLeader) {
      BlockState blockstate = world.getBlockState(pos);
      if (!WorldEntitySpawner.isValidEmptySpawnBlock(world, pos, blockstate, blockstate.getFluidState(), EntityType.PILLAGER)) {
         return false;
      } else if (!PatrollerEntity.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, world, SpawnReason.PATROL, pos, random)) {
         return false;
      } else {
         PatrollerEntity patrollerentity = setLeader ? EntityType.PILLAGER_CAPTAIN.create(world) : EntityType.PILLAGER.create(world);
         if (patrollerentity != null) {
            if (setLeader) {
               patrollerentity.setPatrolLeader(true);
               patrollerentity.setCanSpawnRaid();
               patrollerentity.addEffect(new EffectInstance(Effects.BAD_OMEN, 120000, 1, false, true));
               patrollerentity.findPatrolTarget();
            }

            patrollerentity.setPos((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
            patrollerentity.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), SpawnReason.PATROL, (ILivingEntityData) null, (CompoundNBT) null);
            world.addFreshEntityWithPassengers(patrollerentity);
            return true;
         } else {
            return false;
         }
      }
   }

}
