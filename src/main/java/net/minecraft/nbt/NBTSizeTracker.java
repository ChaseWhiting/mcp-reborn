package net.minecraft.nbt;

import net.minecraft.util.codec.NbtAccounter;

public class NBTSizeTracker {
   public static final NBTSizeTracker UNLIMITED = new NBTSizeTracker(0L) {
      public void accountBits(long p_152450_1_) {
      }
   };
   private final long quota;
   private long usage;

   public NBTSizeTracker(long p_i46342_1_) {
      this.quota = p_i46342_1_;
   }

   public void accountBits(long p_152450_1_) {
      this.usage += p_152450_1_ / 8L;
      if (this.usage > this.quota) {
         throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.usage + "bytes where max allowed: " + this.quota);
      }
   }



   public NbtAccounter toAccounterType() {
      return new NbtAccounter(quota, 512);
   }
}