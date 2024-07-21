package net.minecraft.network;

import io.netty.buffer.Unpooled;

import java.util.*;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.debug.EntityAIDebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.RaccoonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.pathfinding.FlaggedPathPoint;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.PathCalculation;
import net.minecraft.util.RandomObjectDescriptor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.village.GossipManager;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugPacketSender {
   private static final Logger LOGGER = LogManager.getLogger();




   public static void sendGameTestAddMarker(ServerWorld p_229752_0_, BlockPos p_229752_1_, String p_229752_2_, int p_229752_3_, int p_229752_4_) {
      PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
      packetbuffer.writeBlockPos(p_229752_1_);
      packetbuffer.writeInt(p_229752_3_);
      packetbuffer.writeUtf(p_229752_2_);
      packetbuffer.writeInt(p_229752_4_);
      sendPacketToAllPlayers(p_229752_0_, packetbuffer, SCustomPayloadPlayPacket.DEBUG_GAME_TEST_ADD_MARKER);
   }

   public static void sendGameTestClearPacket(ServerWorld p_229751_0_) {
      PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
      sendPacketToAllPlayers(p_229751_0_, packetbuffer, SCustomPayloadPlayPacket.DEBUG_GAME_TEST_CLEAR);
   }

   public static void sendPoiPacketsForChunk(ServerWorld p_218802_0_, ChunkPos p_218802_1_) {
   }

   public static void sendPoiAddedPacket(ServerWorld p_218799_0_, BlockPos p_218799_1_) {
      sendVillageSectionsPacket(p_218799_0_, p_218799_1_);
   }

   public static void sendPoiRemovedPacket(ServerWorld p_218805_0_, BlockPos p_218805_1_) {
      sendVillageSectionsPacket(p_218805_0_, p_218805_1_);
   }

   public static void sendPoiTicketCountPacket(ServerWorld p_218801_0_, BlockPos p_218801_1_) {
      sendVillageSectionsPacket(p_218801_0_, p_218801_1_);
   }

   private static void sendVillageSectionsPacket(ServerWorld p_240840_0_, BlockPos p_240840_1_) {
   }

   public static void sendPathFindingPacket(World p_218803_0_, Mob p_218803_1_, @Nullable Path p_218803_2_, float p_218803_3_) {
   }

   public static void sendNeighborsUpdatePacket(World p_218806_0_, BlockPos p_218806_1_) {
   }

   public static void sendStructurePacket(ISeedReader p_218804_0_, StructureStart<?> p_218804_1_) {
   }

   public static void sendGoalSelector(World p_218800_0_, Mob p_218800_1_, GoalSelector p_218800_2_) {
      if (p_218800_0_ instanceof ServerWorld) {
         ;
      }
   }

   public static void sendRaids(ServerWorld p_222946_0_, Collection<Raid> p_222946_1_) {
   }

   public static void sendEntityBrain(LivingEntity p_218798_0_) {
   }

   public static void sendBeeInfo(BeeEntity p_229749_0_) {
   }

   public static void sendHiveInfo(BeehiveTileEntity p_229750_0_) {
   }

   public static void sendPacketToAllPlayers(ServerWorld p_229753_0_, PacketBuffer p_229753_1_, ResourceLocation p_229753_2_) {
      IPacket<?> ipacket = new SCustomPayloadPlayPacket(p_229753_2_, p_229753_1_);

      for(PlayerEntity playerentity : p_229753_0_.getLevel().players()) {
         ((ServerPlayerEntity)playerentity).connection.send(ipacket);
      }

   }

   public static void sendPathfindingDebugPacket(World world, Entity entity, Path path, float maxDistance, ServerPlayerEntity player) {
      PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
      packetBuffer.writeInt(entity.getId());
      packetBuffer.writeFloat(maxDistance);

      // Check if path, player, or targetNodes are null and log appropriately
      if (path == null) {
       //  System.out.println("Packet not sent: path is null.");
         return;
      }
      if (player == null) {
       //  System.out.println("Packet not sent: player is null.");
         return;
      }
      if (path.getTargetNodes() == null) {
        // System.out.println("Packet not sent: target nodes are null.");
         return;
      }

      // Serialize path data
      packetBuffer.writeBoolean(path.canReach());
      packetBuffer.writeInt(path.getNextNodeIndex());

      // Write targetNodes
      Set<FlaggedPathPoint> targetNodes = path.getTargetNodes();
      packetBuffer.writeInt(targetNodes.size());
      for (FlaggedPathPoint point : targetNodes) {
         point.write(packetBuffer);
      }

      // Write target BlockPos
      BlockPos target = path.getTarget();
      packetBuffer.writeInt(target.getX());
      packetBuffer.writeInt(target.getY());
      packetBuffer.writeInt(target.getZ());

      // Write main path nodes
      List<PathPoint> nodes = path.getNodes();
      packetBuffer.writeInt(nodes.size());
      for (PathPoint point : nodes) {
         point.write(packetBuffer);
      }

      // Write open set
      PathPoint[] openSet = path.getOpenSet();
      packetBuffer.writeInt(openSet.length);
      for (PathPoint point : openSet) {
         point.write(packetBuffer);
      }

      // Write closed set
      PathPoint[] closedSet = path.getClosedSet();
      packetBuffer.writeInt(closedSet.length);
      for (PathPoint point : closedSet) {
         point.write(packetBuffer);
      }

      SCustomPayloadPlayPacket packet = new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.DEBUG_PATHFINDING_PACKET, packetBuffer);

      player.connection.send(packet);
   }

   public static void sendGoalSelectorDebugPacket(World world, Entity entity, List<EntityAIDebugRenderer.Entry> goals, ServerPlayerEntity player) {
      PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
      packetBuffer.writeBlockPos(entity.blockPosition());
      packetBuffer.writeInt(entity.getId());
      packetBuffer.writeInt(goals.size());

      for (EntityAIDebugRenderer.Entry goal : goals) {
         packetBuffer.writeInt(goal.priority);
         packetBuffer.writeBoolean(goal.isRunning);
         packetBuffer.writeUtf(goal.name, 255);
      }
      if (entity.isAlive()) {
         SCustomPayloadPlayPacket packet = new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.DEBUG_GOAL_SELECTOR, packetBuffer);

         player.connection.send(packet);

      }
   }

   public static void sendRaidDebugPacket(ServerPlayerEntity player, Collection<BlockPos> raidCenters) {
      if (player == null) {
         System.out.println("Player null");
         return;
      }
      if (raidCenters == null) {
         System.out.println("raidCenters null");
         return;
      }
      if (raidCenters.isEmpty()) {
         System.out.println("raidCenters empty");
         return;
      }
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());

         // Write the number of raid centers
         packetBuffer.writeInt(raidCenters.size());

         // Write each BlockPos to the buffer
         for (BlockPos pos : raidCenters) {
            packetBuffer.writeBlockPos(pos);
         }

         SCustomPayloadPlayPacket packet = new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.DEBUG_RAIDS, packetBuffer);
         player.connection.send(packet);
   }

   public static void sendBeehiveDebugData(ServerPlayerEntity player, World world, BlockPos pos, BlockState state, BeehiveTileEntity blockEntity) {
      PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
      buf.writeBlockPos(pos);
      buf.writeUtf(state.getBlock().getName().getString());
      buf.writeInt(blockEntity.getOccupantCount());
      buf.writeInt(BeehiveTileEntity.getHoneyLevel(state));
      buf.writeBoolean(blockEntity.isFireNearby());

      SCustomPayloadPlayPacket packet = new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.DEBUG_HIVE, buf);
      player.connection.send(packet);
   }

   public static void sendBeeDebugData(ServerPlayerEntity player, BeeEntity bee) {
      PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
      buf.writeDouble(bee.getX());
      buf.writeDouble(bee.getY());
      buf.writeDouble(bee.getZ());
      buf.writeUUID(bee.getUUID());
      buf.writeInt(bee.getId());
      if (bee.getHivePos() != null) {
         buf.writeBoolean(true);
         buf.writeBlockPos(bee.getHivePos());
      } else {
         buf.writeBoolean(false);
      }
      if (bee.getSavedFlowerPos() != null) {
         buf.writeBoolean(true);
         buf.writeBlockPos(bee.getSavedFlowerPos());
      } else {
         buf.writeBoolean(false);
      }
      buf.writeInt(0); // not sure what this is supposed to be
      Path path = bee.getNavigation().getPath();
      if (path != null) {
         buf.writeBoolean(true);
         path.write(buf);
      } else {
         buf.writeBoolean(false);
      }
      buf.writeInt(0); // labels???
      buf.writeInt(0); // blacklist???
      SCustomPayloadPlayPacket packet = new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.DEBUG_BEE, buf);
      player.connection.send(packet);
   }

    public static void sendRaccoonDebugData(ServerPlayerEntity player, RaccoonEntity raccoon) {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        buf.writeInt(raccoon.getDirtiness());
        buf.writeDouble(raccoon.getX());
        buf.writeDouble(raccoon.getY());
        buf.writeDouble(raccoon.getZ());
        if (raccoon.getNavigation().getTargetPos() != null) {
            buf.writeBlockPos(raccoon.getNavigation().getTargetPos());
        } else {
            buf.writeBlockPos(new BlockPos(BlockPos.ZERO));
        }
        buf.writeUUID(raccoon.getUUID());
        buf.writeInt(raccoon.getId());

        if (raccoon.getHomePos() != null) {
            buf.writeBoolean(true);
            buf.writeBlockPos(raccoon.getHomePos());
        } else {
            buf.writeBoolean(false);
        }

        buf.writeBoolean(raccoon.isLeader());

        List<UUID> homeMembers = raccoon.getHomeMembers();
        buf.writeInt(homeMembers.size());
        for (UUID member : homeMembers) {
            buf.writeUUID(member);
        }

        buf.writeInt(raccoon.getHunger());
        buf.writeInt(raccoon.getThirst());

        Path path = raccoon.getNavigation().getPath();
        if (path != null) {
            buf.writeBoolean(true);
            path.write(buf);
        } else {
            buf.writeBoolean(false);
        }

        SCustomPayloadPlayPacket packet = new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.DEBUG_RACCOON, buf);
        player.connection.send(packet);
    }

    public static void sendPathDebugData(ServerPlayerEntity player, Mob mob, Path path, World world) {
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBuffer.writeInt(mob.getId());
        System.out.println("Sending mob ID: " + mob.getId());

        if (path == null) {
            System.out.println("Packet not sent: path is null.");
            return;
        }
        if (player == null) {
            System.out.println("Packet not sent: player is null.");
            return;
        }

        packetBuffer.writeBoolean(path.canReach());
        System.out.println("Sending canReach: " + path.canReach());
        packetBuffer.writeInt(path.getNextNodeIndex());
        System.out.println("Sending nextNodeIndex: " + path.getNextNodeIndex());

        Set<FlaggedPathPoint> targetNodes = path.getTargetNodes();
        packetBuffer.writeInt(targetNodes.size());
        System.out.println("Sending targetNodesCount: " + targetNodes.size());
        for (FlaggedPathPoint point : targetNodes) {
            point.write(packetBuffer);
            System.out.println("Sending FlaggedPathPoint: " + point.x + ", " + point.y + ", " + point.z);
        }

        BlockPos target = path.getTarget();
        packetBuffer.writeInt(target.getX());
        packetBuffer.writeInt(target.getY());
        packetBuffer.writeInt(target.getZ());
        System.out.println("Sending targetPos: " + target.getX() + ", " + target.getY() + ", " + target.getZ());

        List<PathPoint> nodes = path.getNodes();
        packetBuffer.writeInt(nodes.size());
        System.out.println("Sending node count: " + nodes.size());
        for (PathPoint point : nodes) {
            point.write(packetBuffer);
            System.out.println("Sending PathPoint: " + point.x + ", " + point.y + ", " + point.z);
        }

        PathPoint[] openSet = path.getOpenSet();
        packetBuffer.writeInt(openSet.length);
        System.out.println("Sending openSet length: " + openSet.length);
        for (PathPoint point : openSet) {
            point.write(packetBuffer);
            System.out.println("Sending openSet PathPoint: " + point.x + ", " + point.y + ", " + point.z);
        }

        PathPoint[] closedSet = path.getClosedSet();
        packetBuffer.writeInt(closedSet.length);
        System.out.println("Sending closedSet length: " + closedSet.length);
        for (PathPoint point : closedSet) {
            point.write(packetBuffer);
            System.out.println("Sending closedSet PathPoint: " + point.x + ", " + point.y + ", " + point.z);
        }

        System.out.println("Sending packet with data: " + packetBuffer);
        SCustomPayloadPlayPacket packet = new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.DEBUG_PATH, packetBuffer);
        if (player != null) {
            player.connection.send(packet);
        }
    }

    // Method to test and use PathCalculation's calculateOptimalNodes method
    public static void testCalculateOptimalNodes(ServerPlayerEntity player, Mob mob, World world, int radius) {
        List<PathPoint> nodes = PathCalculation.calculateOptimalNodes(mob, world, radius);
        Path path = PathCalculation.findPath(mob, mob.blockPosition(), nodes);
        sendPathDebugData(player, mob, path, world);
    }

   public static void sendBrainDebugData(ServerPlayerEntity player, LivingEntity living) {
       PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
       buf.writeDouble(living.getX());
       buf.writeDouble(living.getY());
       buf.writeDouble(living.getZ());
       buf.writeUUID(living.getUUID());
       buf.writeInt(living.getId());
       buf.writeUtf(RandomObjectDescriptor.getEntityName(living.getUUID()));
       if (living instanceof VillagerEntity) {
           buf.writeUtf(((VillagerEntity) living).getVillagerData().getProfession().toString());
       } else {
           buf.writeUtf("none");
       }
       if (living instanceof VillagerEntity) {
           buf.writeInt(((VillagerEntity) living).getVillagerData().getLevel());
       } else {
           buf.writeInt(0);
       }
       buf.writeFloat(living.getHealth());
       buf.writeFloat(living.getMaxHealth());
       buf.writeUtf(""); // Placeholder for s3

       // Write the path data (boolean flag + Path data)

       Path path = null;
       if (living instanceof VillagerEntity) {
           path = ((VillagerEntity) living).getNavigation().getPath();
       }
       if (path != null) {
           buf.writeBoolean(true);
           path.write(buf);
       } else {
           buf.writeBoolean(false);
       }

       buf.writeBoolean(false); // Placeholder for flag2, assumed to be false

       // Activities
       List<String> activities = getActivities(living);
       buf.writeInt(activities.size());
       for (String activity : activities) {
           buf.writeUtf(activity);
       }

       // Behaviors
       List<String> behaviors = getBehaviors(living);
       buf.writeInt(behaviors.size());
       for (String behavior : behaviors) {
           buf.writeUtf(behavior);
       }

       // Memories
       List<String> memories = getMemories(living);
       buf.writeInt(memories.size());
       for (String memory : memories) {
           buf.writeUtf(memory);
       }

       // Points of Interest (POIs)
       List<BlockPos> pois = getPOIs(living);
       buf.writeInt(pois.size());
       for (BlockPos poi : pois) {
           buf.writeBlockPos(poi);
       }

       // Potential POIs
       List<BlockPos> potentialPois = getPotentialPOIs(living);
       buf.writeInt(potentialPois.size());
       for (BlockPos potentialPoi : potentialPois) {
           buf.writeBlockPos(potentialPoi);
       }

       // Gossips
       List<String> gossips = getGossips(living);
       buf.writeInt(gossips.size());
       for (String gossip : gossips) {
           buf.writeUtf(gossip);
       }

       SCustomPayloadPlayPacket packet = new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.DEBUG_BRAIN, buf);
       player.connection.send(packet);
   }

    private static List<String> getActivities(LivingEntity living) {
        List<String> activities = new ArrayList<>();
        if (!(living instanceof PlayerEntity)) {
            Brain<?> brain = living.getBrain();

            Optional<Activity> activityOptional = brain.getActiveNonCoreActivity();
            activityOptional.ifPresent(activity -> activities.add(activity.getName()));


        }
        return activities;
    }

    private static List<String> getBehaviors(LivingEntity living) {
        List<String> behaviors = new ArrayList<>();
        if (!(living instanceof PlayerEntity)) {
            Brain<?> brain = living.getBrain();

            brain.getRunningBehaviors().forEach(behavior -> {
                behaviors.add(behavior.toString()); // Using toString() for simplicity; adjust based on available methods
            });
        }
        return behaviors;
    }

    private static List<String> getMemories(LivingEntity living) {
        List<String> memories = new ArrayList<>();
        if (!(living instanceof PlayerEntity)) {
            Brain<?> brain = living.getBrain();

            // List of known memory types (this needs to be populated with actual types)
            List<MemoryModuleType<?>> memoryTypes = Arrays.asList(
                    MemoryModuleType.HOME, // Example memory types
                    MemoryModuleType.JOB_SITE,
                    MemoryModuleType.POTENTIAL_JOB_SITE,
                    MemoryModuleType.MEETING_POINT,
                    MemoryModuleType.BREED_TARGET,
                    MemoryModuleType.INTERACTION_TARGET,
                    MemoryModuleType.NEAREST_VISIBLE_ADULT,
                    MemoryModuleType.NEAREST_HOSTILE,
                    MemoryModuleType.WALK_TARGET,
                    MemoryModuleType.LOOK_TARGET,
                    MemoryModuleType.ATTACK_TARGET,
                    MemoryModuleType.ATTACK_COOLING_DOWN,
                    MemoryModuleType.HURT_BY,
                    MemoryModuleType.NEAREST_PLAYERS,
                    MemoryModuleType.HURT_BY_ENTITY,
                    MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                    MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM
                    // Add other memory types here as needed
            );

            for (MemoryModuleType<?> memoryType : memoryTypes) {
                if (brain.hasMemoryValue(memoryType)) {
                    Optional<?> memoryOptional = brain.getMemory(memoryType);
                    memoryOptional.ifPresent(memory -> {
                        String memoryDescription = formatMemoryDescription(memoryType, memory);
                        memories.add(memoryDescription);
                    });
                }
            }
        }
        return Collections.emptyList();
    }

    private static String formatMemoryDescription(MemoryModuleType<?> memoryType, Object memory) {
        // Provide a concise description for each memory type
        if (memory instanceof BlockPos) {
            BlockPos pos = (BlockPos) memory;
            return memoryType + ": " + pos.getX() + "," + pos.getY() + "," + pos.getZ();
        } else if (memory instanceof Entity) {
            Entity entity = (Entity) memory;
            return memoryType + ": " + entity.getName().getString();
        } else {
            return memoryType + ": " + memory.toString();
        }
    }

   private static List<BlockPos> getPOIs(LivingEntity living) {
      // Replace this with the actual logic to get POIs
      return Collections.emptyList();
   }

    private static List<BlockPos> getPotentialPOIs(LivingEntity living) {
        List<BlockPos> potentialPOIs = new ArrayList<>();
        if (living instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) living;
            PointOfInterestManager poiManager = villager.level.getPoiManager();

            // Retrieve all POIs within a certain radius
            // Retrieve all POIs within a certain radius
            poiManager.findAll(
                    poiType -> true, // Considering all POI types; adjust the filter as needed
                    pos -> true,     // Considering all positions; adjust the filter as needed
                    living.blockPosition(),
                    32, // Radius within which to look for POIs
                    PointOfInterestManager.Status.ANY
            ).forEach(potentialPOIs::add); // Adding the positions to the list
        }
        return potentialPOIs;
    }

    private static List<String> getGossips(LivingEntity living) {
        List<String> gossips = new ArrayList<>();
        if (living instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) living;
            GossipManager gossipManager = villager.getGossips();

            // Iterate over the gossip entries and format them into strings
            gossipManager.unpack().forEach(entry -> {
                //String name = living.level.getPlayerByUUID(entry.target).getName().getString();
                gossips.add(entry.type + ": " + entry.value + " (target: "  + ")");
            });
        }
        return gossips;
    }

}
