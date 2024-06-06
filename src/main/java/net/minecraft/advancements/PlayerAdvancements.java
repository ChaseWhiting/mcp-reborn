package net.minecraft.advancements;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancements {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).setPrettyPrinting().create();
   private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> TYPE_TOKEN = new TypeToken<Map<ResourceLocation, AdvancementProgress>>() {
   };
   private final DataFixer dataFixer;
   private final PlayerList playerList;
   private final File file;
   private final Map<Advancement, AdvancementProgress> advancements = Maps.newLinkedHashMap();
   private final Set<Advancement> visible = Sets.newLinkedHashSet();
   private final Set<Advancement> visibilityChanged = Sets.newLinkedHashSet();
   private final Set<Advancement> progressChanged = Sets.newLinkedHashSet();
   private ServerPlayerEntity player;
   @Nullable
   private Advancement lastSelectedTab;
   private boolean isFirstPacket = true;

   public PlayerAdvancements(DataFixer p_i232594_1_, PlayerList p_i232594_2_, AdvancementManager p_i232594_3_, File p_i232594_4_, ServerPlayerEntity p_i232594_5_) {
      this.dataFixer = p_i232594_1_;
      this.playerList = p_i232594_2_;
      this.file = p_i232594_4_;
      this.player = p_i232594_5_;
      this.load(p_i232594_3_);
   }

   public void setPlayer(ServerPlayerEntity p_192739_1_) {
      this.player = p_192739_1_;
   }

   public void stopListening() {
      for(ICriterionTrigger<?> icriteriontrigger : CriteriaTriggers.all()) {
         icriteriontrigger.removePlayerListeners(this);
      }

   }

   public void reload(AdvancementManager p_240918_1_) {
      this.stopListening();
      this.advancements.clear();
      this.visible.clear();
      this.visibilityChanged.clear();
      this.progressChanged.clear();
      this.isFirstPacket = true;
      this.lastSelectedTab = null;
      this.load(p_240918_1_);
   }

   private void registerListeners(AdvancementManager p_240919_1_) {
      for(Advancement advancement : p_240919_1_.getAllAdvancements()) {
         this.registerListeners(advancement);
      }

   }

   private void ensureAllVisible() {
      List<Advancement> list = Lists.newArrayList();

      for(Entry<Advancement, AdvancementProgress> entry : this.advancements.entrySet()) {
         if (entry.getValue().isDone()) {
            list.add(entry.getKey());
            this.progressChanged.add(entry.getKey());
         }
      }

      for(Advancement advancement : list) {
         this.ensureVisibility(advancement);
      }

   }

   private void checkForAutomaticTriggers(AdvancementManager p_240920_1_) {
      for(Advancement advancement : p_240920_1_.getAllAdvancements()) {
         if (advancement.getCriteria().isEmpty()) {
            this.award(advancement, "");
            advancement.getRewards().grant(this.player);
         }
      }

   }

   private void load(AdvancementManager p_240921_1_) {
      if (this.file.isFile()) {
         try (JsonReader jsonreader = new JsonReader(new StringReader(Files.toString(this.file, StandardCharsets.UTF_8)))) {
            jsonreader.setLenient(false);
            Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(jsonreader));
            if (!dynamic.get("DataVersion").asNumber().result().isPresent()) {
               dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
            }

            dynamic = this.dataFixer.update(DefaultTypeReferences.ADVANCEMENTS.getType(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getCurrentVersion().getWorldVersion());
            dynamic = dynamic.remove("DataVersion");
            Map<ResourceLocation, AdvancementProgress> map = GSON.getAdapter(TYPE_TOKEN).fromJsonTree(dynamic.getValue());
            if (map == null) {
               throw new JsonParseException("Found null for advancements");
            }

            Stream<Entry<ResourceLocation, AdvancementProgress>> stream = map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));

            for(Entry<ResourceLocation, AdvancementProgress> entry : stream.collect(Collectors.toList())) {
               Advancement advancement = p_240921_1_.getAdvancement(entry.getKey());
               if (advancement == null) {
                  LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), this.file);
               } else {
                  this.startProgress(advancement, entry.getValue());
               }
            }
         } catch (JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't parse player advancements in {}", this.file, jsonparseexception);
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't access player advancements in {}", this.file, ioexception);
         }
      }

      this.checkForAutomaticTriggers(p_240921_1_);
      this.ensureAllVisible();
      this.registerListeners(p_240921_1_);
   }

   public void save() {
      Map<ResourceLocation, AdvancementProgress> map = Maps.newHashMap();

      for(Entry<Advancement, AdvancementProgress> entry : this.advancements.entrySet()) {
         AdvancementProgress advancementprogress = entry.getValue();
         if (advancementprogress.hasProgress()) {
            map.put(entry.getKey().getId(), advancementprogress);
         }
      }

      if (this.file.getParentFile() != null) {
         this.file.getParentFile().mkdirs();
      }

      JsonElement jsonelement = GSON.toJsonTree(map);
      jsonelement.getAsJsonObject().addProperty("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());

      try (
         OutputStream outputstream = new FileOutputStream(this.file);
         Writer writer = new OutputStreamWriter(outputstream, Charsets.UTF_8.newEncoder());
      ) {
         GSON.toJson(jsonelement, writer);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save player advancements to {}", this.file, ioexception);
      }

   }

   public boolean award(Advancement p_192750_1_, String p_192750_2_) {
      boolean flag = false;
      AdvancementProgress advancementprogress = this.getOrStartProgress(p_192750_1_);
      boolean flag1 = advancementprogress.isDone();
      if (advancementprogress.grantProgress(p_192750_2_)) {
         this.unregisterListeners(p_192750_1_);
         this.progressChanged.add(p_192750_1_);
         flag = true;
         if (!flag1 && advancementprogress.isDone()) {
            p_192750_1_.getRewards().grant(this.player);
            if (p_192750_1_.getDisplay() != null && p_192750_1_.getDisplay().shouldAnnounceChat() && this.player.level.getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
               this.playerList.broadcastMessage(new TranslationTextComponent("chat.type.advancement." + p_192750_1_.getDisplay().getFrame().getName(), this.player.getDisplayName(), p_192750_1_.getChatComponent()), ChatType.SYSTEM, Util.NIL_UUID);
            }
         }
      }

      if (advancementprogress.isDone()) {
         this.ensureVisibility(p_192750_1_);
      }

      return flag;
   }

   public boolean revoke(Advancement p_192744_1_, String p_192744_2_) {
      boolean flag = false;
      AdvancementProgress advancementprogress = this.getOrStartProgress(p_192744_1_);
      if (advancementprogress.revokeProgress(p_192744_2_)) {
         this.registerListeners(p_192744_1_);
         this.progressChanged.add(p_192744_1_);
         flag = true;
      }

      if (!advancementprogress.hasProgress()) {
         this.ensureVisibility(p_192744_1_);
      }

      return flag;
   }

   private void registerListeners(Advancement p_193764_1_) {
      AdvancementProgress advancementprogress = this.getOrStartProgress(p_193764_1_);
      if (!advancementprogress.isDone()) {
         for(Entry<String, Criterion> entry : p_193764_1_.getCriteria().entrySet()) {
            CriterionProgress criterionprogress = advancementprogress.getCriterion(entry.getKey());
            if (criterionprogress != null && !criterionprogress.isDone()) {
               ICriterionInstance icriterioninstance = entry.getValue().getTrigger();
               if (icriterioninstance != null) {
                  ICriterionTrigger<ICriterionInstance> icriteriontrigger = CriteriaTriggers.getCriterion(icriterioninstance.getCriterion());
                  if (icriteriontrigger != null) {
                     icriteriontrigger.addPlayerListener(this, new ICriterionTrigger.Listener<>(icriterioninstance, p_193764_1_, entry.getKey()));
                  }
               }
            }
         }

      }
   }

   private void unregisterListeners(Advancement p_193765_1_) {
      AdvancementProgress advancementprogress = this.getOrStartProgress(p_193765_1_);

      for(Entry<String, Criterion> entry : p_193765_1_.getCriteria().entrySet()) {
         CriterionProgress criterionprogress = advancementprogress.getCriterion(entry.getKey());
         if (criterionprogress != null && (criterionprogress.isDone() || advancementprogress.isDone())) {
            ICriterionInstance icriterioninstance = entry.getValue().getTrigger();
            if (icriterioninstance != null) {
               ICriterionTrigger<ICriterionInstance> icriteriontrigger = CriteriaTriggers.getCriterion(icriterioninstance.getCriterion());
               if (icriteriontrigger != null) {
                  icriteriontrigger.removePlayerListener(this, new ICriterionTrigger.Listener<>(icriterioninstance, p_193765_1_, entry.getKey()));
               }
            }
         }
      }

   }

   public void flushDirty(ServerPlayerEntity p_192741_1_) {
      if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()) {
         Map<ResourceLocation, AdvancementProgress> map = Maps.newHashMap();
         Set<Advancement> set = Sets.newLinkedHashSet();
         Set<ResourceLocation> set1 = Sets.newLinkedHashSet();

         for(Advancement advancement : this.progressChanged) {
            if (this.visible.contains(advancement)) {
               map.put(advancement.getId(), this.advancements.get(advancement));
            }
         }

         for(Advancement advancement1 : this.visibilityChanged) {
            if (this.visible.contains(advancement1)) {
               set.add(advancement1);
            } else {
               set1.add(advancement1.getId());
            }
         }

         if (this.isFirstPacket || !map.isEmpty() || !set.isEmpty() || !set1.isEmpty()) {
            p_192741_1_.connection.send(new SAdvancementInfoPacket(this.isFirstPacket, set, set1, map));
            this.visibilityChanged.clear();
            this.progressChanged.clear();
         }
      }

      this.isFirstPacket = false;
   }

   public void setSelectedTab(@Nullable Advancement p_194220_1_) {
      Advancement advancement = this.lastSelectedTab;
      if (p_194220_1_ != null && p_194220_1_.getParent() == null && p_194220_1_.getDisplay() != null) {
         this.lastSelectedTab = p_194220_1_;
      } else {
         this.lastSelectedTab = null;
      }

      if (advancement != this.lastSelectedTab) {
         this.player.connection.send(new SSelectAdvancementsTabPacket(this.lastSelectedTab == null ? null : this.lastSelectedTab.getId()));
      }

   }

   public AdvancementProgress getOrStartProgress(Advancement p_192747_1_) {
      AdvancementProgress advancementprogress = this.advancements.get(p_192747_1_);
      if (advancementprogress == null) {
         advancementprogress = new AdvancementProgress();
         this.startProgress(p_192747_1_, advancementprogress);
      }

      return advancementprogress;
   }

   private void startProgress(Advancement p_192743_1_, AdvancementProgress p_192743_2_) {
      p_192743_2_.update(p_192743_1_.getCriteria(), p_192743_1_.getRequirements());
      this.advancements.put(p_192743_1_, p_192743_2_);
   }

   private void ensureVisibility(Advancement p_192742_1_) {
      boolean flag = this.shouldBeVisible(p_192742_1_);
      boolean flag1 = this.visible.contains(p_192742_1_);
      if (flag && !flag1) {
         this.visible.add(p_192742_1_);
         this.visibilityChanged.add(p_192742_1_);
         if (this.advancements.containsKey(p_192742_1_)) {
            this.progressChanged.add(p_192742_1_);
         }
      } else if (!flag && flag1) {
         this.visible.remove(p_192742_1_);
         this.visibilityChanged.add(p_192742_1_);
      }

      if (flag != flag1 && p_192742_1_.getParent() != null) {
         this.ensureVisibility(p_192742_1_.getParent());
      }

      for(Advancement advancement : p_192742_1_.getChildren()) {
         this.ensureVisibility(advancement);
      }

   }

   private boolean shouldBeVisible(Advancement p_192738_1_) {
      for(int i = 0; p_192738_1_ != null && i <= 2; ++i) {
         if (i == 0 && this.hasCompletedChildrenOrSelf(p_192738_1_)) {
            return true;
         }

         if (p_192738_1_.getDisplay() == null) {
            return false;
         }

         AdvancementProgress advancementprogress = this.getOrStartProgress(p_192738_1_);
         if (advancementprogress.isDone()) {
            return true;
         }

         if (p_192738_1_.getDisplay().isHidden()) {
            return false;
         }

         p_192738_1_ = p_192738_1_.getParent();
      }

      return false;
   }

   private boolean hasCompletedChildrenOrSelf(Advancement p_192746_1_) {
      AdvancementProgress advancementprogress = this.getOrStartProgress(p_192746_1_);
      if (advancementprogress.isDone()) {
         return true;
      } else {
         for(Advancement advancement : p_192746_1_.getChildren()) {
            if (this.hasCompletedChildrenOrSelf(advancement)) {
               return true;
            }
         }

         return false;
      }
   }
}