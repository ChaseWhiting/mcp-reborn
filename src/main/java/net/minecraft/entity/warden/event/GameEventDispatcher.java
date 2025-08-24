package net.minecraft.entity.warden.event;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


public class GameEventDispatcher {
    private final ServerWorld level;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean DEBUG_MODE = false;

    public GameEventDispatcher(ServerWorld serverWorld) {
        this.level = serverWorld;
    }

    public void register() {

    }


    public void post(GameEvent gameEvent, Vector3d vector3D, GameEvent.Context context) {
        int n = gameEvent.getNotificationRadius();
        BlockPos blockPos = new BlockPos(vector3D);

        // Optimized section calculations
        int sectionX = SectionPos.blockToSectionCoord(blockPos.getX());
        int sectionY = SectionPos.blockToSectionCoord(blockPos.getY());
        int sectionZ = SectionPos.blockToSectionCoord(blockPos.getZ());

        int minSectionX = sectionX - SectionPos.blockToSectionCoord(n);
        int maxSectionX = sectionX + SectionPos.blockToSectionCoord(n);
        int minSectionY = sectionY - SectionPos.blockToSectionCoord(n);
        int maxSectionY = sectionY + SectionPos.blockToSectionCoord(n);
        int minSectionZ = sectionZ - SectionPos.blockToSectionCoord(n);
        int maxSectionZ = sectionZ + SectionPos.blockToSectionCoord(n);

        if (DEBUG_MODE) { // Only log if debugging
            LOGGER.info("Event triggered in GameEventDispatcher#post: {} at {}", gameEvent.getName(), vector3D);
        }

        Set<GameEvent.ListenerInfo> listenerSet = new HashSet<>();
        GameEventListenerRegistry.ListenerVisitor listenerVisitor = (gameEventListener, vec32) -> {
            if (gameEventListener.getDeliveryMode() == GameEventListener.DeliveryMode.BY_DISTANCE) {
                listenerSet.add(new GameEvent.ListenerInfo(gameEvent, vector3D, context, gameEventListener, vec32));
            } else {
                gameEventListener.handleGameEvent(this.level, gameEvent, context, vector3D);
            }
        };

        boolean foundListeners = false;

        for (int i = minSectionX; i <= maxSectionX; ++i) {
            for (int j = minSectionZ; j <= maxSectionZ; ++j) {
                Chunk levelChunk = this.level.getChunkSource().getChunkNow(i, j);
                if (levelChunk == null || levelChunk.getListenerRegistry(sectionY).isEmpty()) continue;

                for (int k = minSectionY; k <= maxSectionY; ++k) {
                    boolean hasListeners = levelChunk.getListenerRegistry(k).visitInRangeListeners(gameEvent, vector3D, context, listenerVisitor);
                    if (hasListeners) foundListeners = true;
                }
            }
        }

        if (!listenerSet.isEmpty()) {
            List<GameEvent.ListenerInfo> sortedList = new ArrayList<>(listenerSet);
            this.handleGameEventMessagesInQueue(sortedList);
        } else if (DEBUG_MODE) {
            LOGGER.info("No listeners found for event: {}", gameEvent.getName());
        }

        if (foundListeners) {
            // DebugPacketSender.sendGameEventInfo(this.level, gameEvent, vector3D);
        }
    }


    private void handleGameEventMessagesInQueue(List<GameEvent.ListenerInfo> list) {
        Collections.sort(list);
        for (GameEvent.ListenerInfo listenerInfo : list) {
            LOGGER.info("Listener info: {}, {}, {}", listenerInfo.gameEvent().getName(), listenerInfo.context().toString(), listenerInfo.source().toString());
            GameEventListener gameEventListener = listenerInfo.recipient();
            gameEventListener.handleGameEvent(this.level, listenerInfo.gameEvent(), listenerInfo.context(), listenerInfo.source());
        }
    }
}

