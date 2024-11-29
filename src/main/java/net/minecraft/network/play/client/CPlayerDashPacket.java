package net.minecraft.network.play.client;

import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public class CPlayerDashPacket implements IPacket<IServerPlayNetHandler> {
   private Vector3d dashVector;

   public ItemStack getShield() {
      return shield;
   }

   private ItemStack shield;

   public CPlayerDashPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPlayerDashPacket(Vector3d vector3d, ItemStack shield) {
      this.dashVector = vector3d;
      this.shield = shield;
   }

   public void read(PacketBuffer buffer) throws IOException {
      double x = buffer.readDouble();
      double y = buffer.readDouble();
      double z = buffer.readDouble();
      this.dashVector = new Vector3d(x,y,z);

      this.shield = buffer.readItem();
   }

   public void write(PacketBuffer buffer) throws IOException {
      buffer.writeDouble(dashVector.x);
      buffer.writeDouble(dashVector.y);
      buffer.writeDouble(dashVector.z);
      buffer.writeItem(this.shield);
   }

   public Vector3d getDashVector() {
      return this.dashVector;
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleDash(this);
   }

}