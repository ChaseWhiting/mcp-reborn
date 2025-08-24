package net.minecraft.network.play.server;

import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class MosquitoDismount implements IPacket<IClientPlayNetHandler> {

    public int rider;
    public int mount;

    public MosquitoDismount(int rider, int mount) {
        this.rider = rider;
        this.mount = mount;
    }

    public MosquitoDismount() {

    }


    @Override
    public void read(PacketBuffer p_148837_1_) throws IOException {
        this.rider = p_148837_1_.readInt();
        this.mount = p_148837_1_.readInt();
    }

    @Override
    public void write(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.rider);
        p_148840_1_.writeInt(this.mount);
    }

    @Override
    public void handle(IClientPlayNetHandler p_148833_1_) {
        p_148833_1_.handleMosquitoDismount(this);
    }
}
