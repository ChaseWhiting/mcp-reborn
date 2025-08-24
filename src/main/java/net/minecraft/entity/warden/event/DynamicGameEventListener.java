package net.minecraft.entity.warden.event;

import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class DynamicGameEventListener<T extends GameEventListener> {
    private T listener;
    @Nullable
    private SectionPos lastSection;

    private static final Logger LOGGER = LogManager.getLogger();

    public DynamicGameEventListener(T t) {
        this.listener = t;
    }

    public void add(ServerWorld serverWorld) {
        this.move(serverWorld);
        LOGGER.info("Adding listener: {}", listener);

    }

    public void updateListener(T t, @Nullable World level) {
        Object t2 = this.listener;
        if (t2 == t) {
            return;
        }
        if (level instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)level;
            DynamicGameEventListener.ifChunkExists(serverWorld, this.lastSection, gameEventListenerRegistry -> gameEventListenerRegistry.unregister((GameEventListener)t2));
            DynamicGameEventListener.ifChunkExists(serverWorld, this.lastSection, gameEventListenerRegistry -> gameEventListenerRegistry.register((GameEventListener)t));
        }
        this.listener = t;
    }

    public T getListener() {
        return this.listener;
    }

    public void remove(ServerWorld serverWorld) {
        LOGGER.info("Removing listener: {}", listener);

        DynamicGameEventListener.ifChunkExists(serverWorld, this.lastSection, gameEventListenerRegistry -> gameEventListenerRegistry.unregister(this.listener));
    }

    public void move(ServerWorld serverWorld) {
        this.listener.getListenerSource().getPosition(serverWorld).map(SectionPos::of).ifPresent(sectionPos -> {
            if (this.lastSection == null || !this.lastSection.equals(sectionPos)) {
                LOGGER.info("Unregistering Listener: {}", listener);

                DynamicGameEventListener.ifChunkExists(serverWorld, this.lastSection, gameEventListenerRegistry -> gameEventListenerRegistry.unregister(this.listener));
                this.lastSection = sectionPos;
                DynamicGameEventListener.ifChunkExists(serverWorld, this.lastSection, gameEventListenerRegistry -> gameEventListenerRegistry.register(this.listener));
                LOGGER.info("Reregistering Listener: {}", listener);
            }
        });
    }

    private static void ifChunkExists(ServerWorld levelReader, @Nullable SectionPos sectionPos, Consumer<GameEventListenerRegistry> consumer) {
        if (sectionPos == null) {
            LOGGER.warn("ifChunkExists: SectionPos is null!");
            return;
        }
        LOGGER.info("ifChunkExists: levelReader class is {}", levelReader.getClass().getName());


        LOGGER.info("Checking chunk at {},{}", sectionPos.x(), sectionPos.z());
        if (!(levelReader.isLoaded(sectionPos))) return;
        Chunk chunkAccess = levelReader.getChunkSource().getChunk(sectionPos.x(), sectionPos.z(), false);


        if (chunkAccess == null) {
            LOGGER.warn("Chunk at {},{} is not loaded!", sectionPos.x(), sectionPos.z());
            return;
        }

        LOGGER.info("Consumer for game event registry accepted, chunk access was not null.");
        consumer.accept(chunkAccess.getListenerRegistry(sectionPos.y()));
    }

}

