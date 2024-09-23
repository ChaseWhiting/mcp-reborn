package net.minecraft.client.audio;

import java.nio.ByteBuffer;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.openal.AL10;

@OnlyIn(Dist.CLIENT)
public class AudioStreamBuffer {
   @Nullable
   private ByteBuffer data;
   public final AudioFormat format;
   private boolean hasAlBuffer;
   private int alBuffer;

   public AudioStreamBuffer(ByteBuffer p_i51176_1_, AudioFormat p_i51176_2_) {
      this.data = p_i51176_1_;
      this.format = p_i51176_2_;
   }

   OptionalInt getAlBuffer() {
      if (!this.hasAlBuffer) {
         if (this.data == null) {
            return OptionalInt.empty();
         }

         int i = ALUtils.audioFormatToOpenAl(this.format);
         int[] aint = new int[1];
         AL10.alGenBuffers(aint);
         if (ALUtils.checkALError("Creating buffer")) {
            return OptionalInt.empty();
         }

         AL10.alBufferData(aint[0], i, this.data, (int)this.format.getSampleRate());
         if (ALUtils.checkALError("Assigning buffer data")) {
            return OptionalInt.empty();
         }

         this.alBuffer = aint[0];
         this.hasAlBuffer = true;
         this.data = null;
      }

      return OptionalInt.of(this.alBuffer);
   }

   public void discardAlBuffer() {
      if (this.hasAlBuffer) {
         AL10.alDeleteBuffers(new int[]{this.alBuffer});
         if (ALUtils.checkALError("Deleting stream buffers")) {
            return;
         }
      }

      this.hasAlBuffer = false;
   }

   public OptionalInt releaseAlBuffer() {
      OptionalInt optionalint = this.getAlBuffer();
      this.hasAlBuffer = false;
      return optionalint;
   }

   // New method to get byte array from ByteBuffer
   public byte[] getByteArray() {
      if (this.data != null) {
         byte[] byteArray = new byte[this.data.remaining()];
         this.data.get(byteArray);
         return byteArray;
      }
      return new byte[0]; // or throw an exception if data is null
   }

   public static ByteBuffer convertToByteBuffer(byte[] byteArray) {
      ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
      byteBuffer.put(byteArray);
      byteBuffer.flip(); // Flip the buffer to prepare it for reading
      return byteBuffer;
   }

}