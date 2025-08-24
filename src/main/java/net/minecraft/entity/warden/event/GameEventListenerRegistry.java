package net.minecraft.entity.warden.event;


import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface GameEventListenerRegistry {
    static final Logger LOGGER = LogManager.getLogger();
    public static final GameEventListenerRegistry NOOP = new GameEventListenerRegistry(){

        @Override
        public boolean isEmpty() {
            LOGGER.info("Called isEmpty on GameEventListenerRegistry.NOOP#isEmpty (GameEventListenerRegistry)");

            return true;
        }

        @Override
        public void register(GameEventListener gameEventListener) {
            LOGGER.info("Called register on GameEventListenerRegistry.NOOP#register (GameEventListenerRegistry)");
        }

        @Override
        public void unregister(GameEventListener gameEventListener) {
            LOGGER.info("Called unregister on GameEventListenerRegistry.NOOP#unregister (GameEventListenerRegistry)");
        }

        @Override
        public boolean visitInRangeListeners(GameEvent gameEvent, Vector3d vector3D, GameEvent.Context context, ListenerVisitor listenerVisitor) {
            LOGGER.info("Called visitInRangeListeners on GameEventListenerRegistry.NOOP#visitInRangeListeners (GameEventListenerRegistry)");

            return false;
        }
    };

    public boolean isEmpty();

    public void register(GameEventListener var1);

    public void unregister(GameEventListener var1);

    public boolean visitInRangeListeners(GameEvent var1, Vector3d var2, GameEvent.Context var3, ListenerVisitor var4);

    @FunctionalInterface
    public static interface ListenerVisitor {
        public void visit(GameEventListener var1, Vector3d var2);
    }
}

