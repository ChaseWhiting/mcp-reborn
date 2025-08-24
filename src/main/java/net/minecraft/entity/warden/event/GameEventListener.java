package net.minecraft.entity.warden.event;

import net.minecraft.entity.warden.event.position.PositionSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;


public interface GameEventListener {
    public PositionSource getListenerSource();

    public int getListenerRadius();

    public boolean handleGameEvent(ServerWorld var1, GameEvent var2, GameEvent.Context var3, Vector3d var4);

    default public DeliveryMode getDeliveryMode() {
        return DeliveryMode.UNSPECIFIED;
    }

    public static enum DeliveryMode {
        UNSPECIFIED,
        BY_DISTANCE;
    }


}

