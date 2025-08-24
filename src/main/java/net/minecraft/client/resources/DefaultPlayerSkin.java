package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.imageio.ImageIO;

@OnlyIn(Dist.CLIENT)
public class DefaultPlayerSkin {
   private static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
   private static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");
   private static Map<UUID, UUID> newSkinMap = new HashMap<>();
   static {
      newSkinMap.put(UUID.fromString("6d48835f-5798-3dfe-91b7-de4161fd7397"), UUID.fromString("133ef920-44e2-46dd-a160-9119baf2a964"));
   }
   public static ResourceLocation getDefaultSkin() {
      return STEVE_SKIN_LOCATION;
   }

   public static ResourceLocation getDefaultSkin(UUID p_177334_0_) {
      return isAlexDefault(p_177334_0_) ? ALEX_SKIN_LOCATION : STEVE_SKIN_LOCATION;
   }

   public static ResourceLocation tryGetPlayerSkin(UUID uuid) throws Exception {
      return getDefaultSkin(uuid);
//      try {
//         if (newSkinMap.containsKey(uuid)) {
//            uuid = newSkinMap.get(uuid);
//         }
//         if (MinecraftUUIDFetcher.getUsernameFromUUID(uuid.toString()).isEmpty()) {
//            return getDefaultSkin(uuid);
//         }
//         String skinURL = MinecraftSkinFetcher.getSkin(uuid); // Use your existing method to get the skin URL
//
//         // Download and cache the skin
//         BufferedImage skinImage = MinecraftSkinFetcher.downloadSkin(skinURL);
//         File skinFile = MinecraftSkinFetcher.cacheSkin(skinImage, uuid);
//
//         // Convert to ResourceLocation
//         return loadSkinAsResourceLocation(skinFile, uuid);
//
//      } catch (Exception e) {
//         throw new Exception("Cannot find skin of UUID " + uuid, e);
//      }
   }

   public static ResourceLocation loadSkinAsResourceLocation(File skinFile, UUID uuid) throws Exception {
      // Load the skin file as a NativeImage
      InputStream inputStream = new FileInputStream(skinFile);
      BufferedImage bufferedImage = ImageIO.read(inputStream);
      NativeImage nativeImage = convertToNativeImage(bufferedImage);

      ResourceLocation resourceLocation = new ResourceLocation("custom_skins", uuid.toString());

      // Register the skin in Minecraft's texture manager
      Minecraft.getInstance().getTextureManager().register(resourceLocation, new DynamicTexture(nativeImage));

      inputStream.close();
      return resourceLocation;
   }

   private static NativeImage convertToNativeImage(BufferedImage bufferedImage) {
      NativeImage nativeImage = new NativeImage(bufferedImage.getWidth(), bufferedImage.getHeight(), true);
      for (int y = 0; y < bufferedImage.getHeight(); y++) {
         for (int x = 0; x < bufferedImage.getWidth(); x++) {
            int argb = bufferedImage.getRGB(x, y);
            nativeImage.setPixelRGBA(x, y, argb);
         }
      }
      return nativeImage;
   }

   public static String getSkinModelName(UUID p_177332_0_) {
      return isAlexDefault(p_177332_0_) ? "slim" : "default";
   }

   private static boolean isAlexDefault(UUID p_177333_0_) {
      return (p_177333_0_.hashCode() & 1) == 1;
   }
}