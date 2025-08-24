package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.resources.FolderResourceIndex;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.util.Session;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameConfiguration {
   public final GameConfiguration.UserInformation user;
   public final ScreenSize display;
   public final GameConfiguration.FolderInformation location;
   public final GameConfiguration.GameInformation game;
   public final GameConfiguration.ServerInformation server;

   public GameConfiguration(GameConfiguration.UserInformation p_i51071_1_, ScreenSize p_i51071_2_, GameConfiguration.FolderInformation p_i51071_3_, GameConfiguration.GameInformation p_i51071_4_, GameConfiguration.ServerInformation p_i51071_5_) {
      this.user = p_i51071_1_;
      this.display = p_i51071_2_;
      this.location = p_i51071_3_;
      this.game = p_i51071_4_;
      this.server = p_i51071_5_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class FolderInformation {
      public final File gameDirectory;
      public final File resourcePackDirectory;
      public final File assetDirectory;
      @Nullable
      public final String assetIndex;

      public FolderInformation(File p_i45489_1_, File p_i45489_2_, File p_i45489_3_, @Nullable String p_i45489_4_) {
         this.gameDirectory = p_i45489_1_;
         this.resourcePackDirectory = p_i45489_2_;
         this.assetDirectory = p_i45489_3_;
         this.assetIndex = p_i45489_4_;
      }

      public ResourceIndex getAssetIndex() {
         return (ResourceIndex)(this.assetIndex == null ? new FolderResourceIndex(this.assetDirectory) : new ResourceIndex(this.assetDirectory, this.assetIndex));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class GameInformation {
      public final boolean demo;
      public final String launchVersion;
      public final String versionType;
      public final boolean disableMultiplayer;
      public final boolean disableChat;

      public GameInformation(boolean isDemo, String launchVersion, String version, boolean multiplayerDisabled, boolean chattingDisabled) {
         this.demo = isDemo;
         this.launchVersion = launchVersion;
         this.versionType = version;
         this.disableMultiplayer = multiplayerDisabled;
         this.disableChat = chattingDisabled;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerInformation {
      @Nullable
      public final String hostname;
      public final int port;

      public ServerInformation(@Nullable String hostname, int port) {
         this.hostname = hostname;
         this.port = port;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UserInformation {
      public final Session user;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      public final Proxy proxy;

      public UserInformation(Session sessions, PropertyMap userProperties, PropertyMap profileProperties, Proxy proxy) {
         this.user = sessions;
         this.userProperties = userProperties;
         this.profileProperties = profileProperties;
         this.proxy = proxy;
      }
   }
}