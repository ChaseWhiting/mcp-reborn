package net.minecraft.server;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SessionLockManager implements AutoCloseable {
   private final FileChannel lockFile;
   private final FileLock lock;
   private static final ByteBuffer DUMMY;

   public static SessionLockManager create(Path p_232998_0_) throws IOException {
      Path path = p_232998_0_.resolve("session.lock");
      if (!Files.isDirectory(p_232998_0_)) {
         Files.createDirectories(p_232998_0_);
      }

      FileChannel filechannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

      try {
         filechannel.write(DUMMY.duplicate());
         filechannel.force(true);
         FileLock filelock = filechannel.tryLock();
         if (filelock == null) {
            throw SessionLockManager.AlreadyLockedException.alreadyLocked(path);
         } else {
            return new SessionLockManager(filechannel, filelock);
         }
      } catch (IOException ioexception1) {
         try {
            filechannel.close();
         } catch (IOException ioexception) {
            ioexception1.addSuppressed(ioexception);
         }

         throw ioexception1;
      }
   }

   private SessionLockManager(FileChannel p_i231437_1_, FileLock p_i231437_2_) {
      this.lockFile = p_i231437_1_;
      this.lock = p_i231437_2_;
   }

   public void close() throws IOException {
      try {
         if (this.lock.isValid()) {
            this.lock.release();
         }
      } finally {
         if (this.lockFile.isOpen()) {
            this.lockFile.close();
         }

      }

   }

   public boolean isValid() {
      return this.lock.isValid();
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isLocked(Path p) throws IOException {
      Path path = p.resolve("session.lock");

      try (
         FileChannel filechannel = FileChannel.open(path, StandardOpenOption.WRITE);
         FileLock filelock = filechannel.tryLock();
      ) {
         return filelock == null;
      } catch (AccessDeniedException accessdeniedexception) {
         return true;
      } catch (NoSuchFileException nosuchfileexception) {
         return false;
      }
   }

   static {
      byte[] abyte = "\u2603".getBytes(Charsets.UTF_8);
      DUMMY = ByteBuffer.allocateDirect(abyte.length);
      DUMMY.put(abyte);
      ((Buffer)DUMMY).flip();
   }

   public static class AlreadyLockedException extends IOException {
      private AlreadyLockedException(Path path, String error) {
         super(path.toAbsolutePath() + ": " + error);
      }

      public static SessionLockManager.AlreadyLockedException alreadyLocked(Path path) {
         return new SessionLockManager.AlreadyLockedException(path, "already locked (possibly by other Minecraft instance?)");
      }
   }
}