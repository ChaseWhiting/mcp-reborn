package net.minecraft.network.play.server;

import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class SPlayGameEventPacket implements IPacket<INetHandler> {
   public  GameEvent type;
   public BlockPos pos;
   public int data;
   private boolean globalEvent;

   public SPlayGameEventPacket() {
   }

   public SPlayGameEventPacket(GameEvent event, BlockPos p_i46940_2_, int p_i46940_3_, boolean p_i46940_4_) {
      this.type = event;
      this.pos = p_i46940_2_.immutable();
      this.data = p_i46940_3_;
      this.globalEvent = p_i46940_4_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.type = p_148837_1_.readEnum(GameEvent.class);
      this.pos = p_148837_1_.readBlockPos();
      this.data = p_148837_1_.readInt();
      this.globalEvent = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.type);
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeInt(this.data);
      p_148840_1_.writeBoolean(this.globalEvent);
   }

   public void handle(INetHandler handler) {
      if (handler instanceof IClientPlayNetHandler) {
         ((IClientPlayNetHandler)handler).handleGameEvent(this);
      } else if (handler instanceof IServerPlayNetHandler) {
         ((IServerPlayNetHandler)handler).handleGameEvent(this);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isGlobalEvent() {
      return this.globalEvent;
   }

   @OnlyIn(Dist.CLIENT)
   public GameEvent getType() {
      return this.type;
   }

   @OnlyIn(Dist.CLIENT)
   public int getData() {
      return this.data;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }
}