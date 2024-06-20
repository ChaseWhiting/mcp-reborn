package net.minecraft.world;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.codec.DatapackCodec;

public final class WorldSettings {
   private final String levelName;
   private final GameType gameType;
   private final boolean hardcore;
   private final Difficulty difficulty;
   private final boolean allowCommands;
   private final GameRules gameRules;
   private final DatapackCodec dataPackConfig;

   public WorldSettings(String p_i231620_1_, GameType gameType, boolean hardcore, Difficulty difficulty, boolean allowCommands, GameRules gamerules, DatapackCodec codec) {
      this.levelName = p_i231620_1_;
      this.gameType = gameType;
      this.hardcore = hardcore;
      this.difficulty = difficulty;
      this.allowCommands = allowCommands;
      this.gameRules = gamerules;
      this.dataPackConfig = codec;
   }

   public static WorldSettings parse(Dynamic<?> p_234951_0_, DatapackCodec p_234951_1_) {
      GameType gametype = GameType.byId(p_234951_0_.get("GameType").asInt(0));
      return new WorldSettings(p_234951_0_.get("LevelName").asString(""), gametype, p_234951_0_.get("hardcore").asBoolean(false), p_234951_0_.get("Difficulty").asNumber().map((p_234952_0_) -> {
         return Difficulty.byId(p_234952_0_.byteValue());
      }).result().orElse(Difficulty.NORMAL), p_234951_0_.get("allowCommands").asBoolean(gametype == GameType.CREATIVE), new GameRules(p_234951_0_.get("GameRules")), p_234951_1_);
   }

   public String levelName() {
      return this.levelName;
   }

   public GameType gameType() {
      return this.gameType;
   }

   public boolean hardcore() {
      return this.hardcore;
   }

   public Difficulty difficulty() {
      return this.difficulty;
   }

   public boolean allowCommands() {
      return this.allowCommands;
   }

   public GameRules gameRules() {
      return this.gameRules;
   }

   public DatapackCodec getDataPackConfig() {
      return this.dataPackConfig;
   }

   public WorldSettings withGameType(GameType p_234950_1_) {
      return new WorldSettings(this.levelName, p_234950_1_, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.dataPackConfig);
   }

   public WorldSettings withDifficulty(Difficulty p_234948_1_) {
      return new WorldSettings(this.levelName, this.gameType, this.hardcore, p_234948_1_, this.allowCommands, this.gameRules, this.dataPackConfig);
   }

   public WorldSettings withDataPackConfig(DatapackCodec p_234949_1_) {
      return new WorldSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, p_234949_1_);
   }

   public WorldSettings copy() {
      return new WorldSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules.copy(), this.dataPackConfig);
   }
}