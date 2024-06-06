package net.minecraft.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;

public class ServerStatusResponse {
   private ITextComponent description;
   private ServerStatusResponse.Players players;
   private ServerStatusResponse.Version version;
   private String favicon;

   public ITextComponent getDescription() {
      return this.description;
   }

   public void setDescription(ITextComponent p_151315_1_) {
      this.description = p_151315_1_;
   }

   public ServerStatusResponse.Players getPlayers() {
      return this.players;
   }

   public void setPlayers(ServerStatusResponse.Players p_151319_1_) {
      this.players = p_151319_1_;
   }

   public ServerStatusResponse.Version getVersion() {
      return this.version;
   }

   public void setVersion(ServerStatusResponse.Version p_151321_1_) {
      this.version = p_151321_1_;
   }

   public void setFavicon(String p_151320_1_) {
      this.favicon = p_151320_1_;
   }

   public String getFavicon() {
      return this.favicon;
   }

   public static class Players {
      private final int maxPlayers;
      private final int numPlayers;
      private GameProfile[] sample;

      public Players(int p_i45274_1_, int p_i45274_2_) {
         this.maxPlayers = p_i45274_1_;
         this.numPlayers = p_i45274_2_;
      }

      public int getMaxPlayers() {
         return this.maxPlayers;
      }

      public int getNumPlayers() {
         return this.numPlayers;
      }

      public GameProfile[] getSample() {
         return this.sample;
      }

      public void setSample(GameProfile[] p_151330_1_) {
         this.sample = p_151330_1_;
      }

      public static class Serializer implements JsonDeserializer<ServerStatusResponse.Players>, JsonSerializer<ServerStatusResponse.Players> {
         public ServerStatusResponse.Players deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject jsonobject = JSONUtils.convertToJsonObject(p_deserialize_1_, "players");
            ServerStatusResponse.Players serverstatusresponse$players = new ServerStatusResponse.Players(JSONUtils.getAsInt(jsonobject, "max"), JSONUtils.getAsInt(jsonobject, "online"));
            if (JSONUtils.isArrayNode(jsonobject, "sample")) {
               JsonArray jsonarray = JSONUtils.getAsJsonArray(jsonobject, "sample");
               if (jsonarray.size() > 0) {
                  GameProfile[] agameprofile = new GameProfile[jsonarray.size()];

                  for(int i = 0; i < agameprofile.length; ++i) {
                     JsonObject jsonobject1 = JSONUtils.convertToJsonObject(jsonarray.get(i), "player[" + i + "]");
                     String s = JSONUtils.getAsString(jsonobject1, "id");
                     agameprofile[i] = new GameProfile(UUID.fromString(s), JSONUtils.getAsString(jsonobject1, "name"));
                  }

                  serverstatusresponse$players.setSample(agameprofile);
               }
            }

            return serverstatusresponse$players;
         }

         public JsonElement serialize(ServerStatusResponse.Players p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("max", p_serialize_1_.getMaxPlayers());
            jsonobject.addProperty("online", p_serialize_1_.getNumPlayers());
            if (p_serialize_1_.getSample() != null && p_serialize_1_.getSample().length > 0) {
               JsonArray jsonarray = new JsonArray();

               for(int i = 0; i < p_serialize_1_.getSample().length; ++i) {
                  JsonObject jsonobject1 = new JsonObject();
                  UUID uuid = p_serialize_1_.getSample()[i].getId();
                  jsonobject1.addProperty("id", uuid == null ? "" : uuid.toString());
                  jsonobject1.addProperty("name", p_serialize_1_.getSample()[i].getName());
                  jsonarray.add(jsonobject1);
               }

               jsonobject.add("sample", jsonarray);
            }

            return jsonobject;
         }
      }
   }

   public static class Serializer implements JsonDeserializer<ServerStatusResponse>, JsonSerializer<ServerStatusResponse> {
      public ServerStatusResponse deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_deserialize_1_, "status");
         ServerStatusResponse serverstatusresponse = new ServerStatusResponse();
         if (jsonobject.has("description")) {
            serverstatusresponse.setDescription(p_deserialize_3_.deserialize(jsonobject.get("description"), ITextComponent.class));
         }

         if (jsonobject.has("players")) {
            serverstatusresponse.setPlayers(p_deserialize_3_.deserialize(jsonobject.get("players"), ServerStatusResponse.Players.class));
         }

         if (jsonobject.has("version")) {
            serverstatusresponse.setVersion(p_deserialize_3_.deserialize(jsonobject.get("version"), ServerStatusResponse.Version.class));
         }

         if (jsonobject.has("favicon")) {
            serverstatusresponse.setFavicon(JSONUtils.getAsString(jsonobject, "favicon"));
         }

         return serverstatusresponse;
      }

      public JsonElement serialize(ServerStatusResponse p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         if (p_serialize_1_.getDescription() != null) {
            jsonobject.add("description", p_serialize_3_.serialize(p_serialize_1_.getDescription()));
         }

         if (p_serialize_1_.getPlayers() != null) {
            jsonobject.add("players", p_serialize_3_.serialize(p_serialize_1_.getPlayers()));
         }

         if (p_serialize_1_.getVersion() != null) {
            jsonobject.add("version", p_serialize_3_.serialize(p_serialize_1_.getVersion()));
         }

         if (p_serialize_1_.getFavicon() != null) {
            jsonobject.addProperty("favicon", p_serialize_1_.getFavicon());
         }

         return jsonobject;
      }
   }

   public static class Version {
      private final String name;
      private final int protocol;

      public Version(String p_i45275_1_, int p_i45275_2_) {
         this.name = p_i45275_1_;
         this.protocol = p_i45275_2_;
      }

      public String getName() {
         return this.name;
      }

      public int getProtocol() {
         return this.protocol;
      }

      public static class Serializer implements JsonDeserializer<ServerStatusResponse.Version>, JsonSerializer<ServerStatusResponse.Version> {
         public ServerStatusResponse.Version deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject jsonobject = JSONUtils.convertToJsonObject(p_deserialize_1_, "version");
            return new ServerStatusResponse.Version(JSONUtils.getAsString(jsonobject, "name"), JSONUtils.getAsInt(jsonobject, "protocol"));
         }

         public JsonElement serialize(ServerStatusResponse.Version p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("name", p_serialize_1_.getName());
            jsonobject.addProperty("protocol", p_serialize_1_.getProtocol());
            return jsonobject;
         }
      }
   }
}