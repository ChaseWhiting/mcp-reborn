package net.minecraft.entity.warden.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class EuclideanGameEventListenerRegistry
implements GameEventListenerRegistry {
    private static final Logger LOGGER = LogManager.getLogger();

    private final List<GameEventListener> listeners = Lists.newArrayList();
    private final Set<GameEventListener> listenersToRemove = Sets.newHashSet();
    private final List<GameEventListener> listenersToAdd = Lists.newArrayList();
    private boolean processing;
    private final ServerWorld level;

    public EuclideanGameEventListenerRegistry(ServerWorld serverWorld) {
        this.level = serverWorld;
    }

    @Override
    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }

    public boolean has(GameEventListener gameEventListener) {
        return this.listeners.contains(gameEventListener);
    }

    @Override
    public void register(GameEventListener gameEventListener) {
        if (this.processing) {

            this.listenersToAdd.add(gameEventListener);
        } else {

            this.listeners.add(gameEventListener);
        }
        //DebugPackets.sendGameEventListenerInfo(this.level, gameEventListener);
    }

    @Override
    public void unregister(GameEventListener gameEventListener) {
        if (this.processing) {
            this.listenersToRemove.add(gameEventListener);
        } else {
            this.listeners.remove(gameEventListener);
        }
    }

    @Override
    public boolean visitInRangeListeners(GameEvent gameEvent, Vector3d vector3D, GameEvent.Context context, GameEventListenerRegistry.ListenerVisitor listenerVisitor) {
        this.processing = true;
        boolean bl = false;
        try {

            Iterator<GameEventListener> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                GameEventListener gameEventListener = iterator.next();
                if (this.listenersToRemove.remove(gameEventListener)) {
                    iterator.remove();
                    continue;
                }
                Optional<Vector3d> optional = EuclideanGameEventListenerRegistry.getPostableListenerPosition(this.level, vector3D, gameEventListener);
                if (!optional.isPresent()) continue;
                listenerVisitor.visit(gameEventListener, optional.get());
                bl = true;
            }
        }
        finally {
            this.processing = false;
        }
        if (!this.listenersToAdd.isEmpty()) {
            this.listeners.addAll(this.listenersToAdd);
            this.listenersToAdd.clear();
        }
        if (!this.listenersToRemove.isEmpty()) {
            this.listeners.removeAll(this.listenersToRemove);
            this.listenersToRemove.clear();
        }
        return bl;
    }

    private static Optional<Vector3d> getPostableListenerPosition(ServerWorld serverWorld, Vector3d vector3D, GameEventListener gameEventListener) {
        int n;

        Optional<Vector3d> optional = gameEventListener.getListenerSource().getPosition(serverWorld);
        if (optional.isEmpty()) {

            return Optional.empty();
        }

        double d = optional.get().distanceToSqr(vector3D);
        double range = gameEventListener.getListenerRadius() * gameEventListener.getListenerRadius();
        if (d > range) {

            return Optional.empty();
        }
        return optional;
    }
}

