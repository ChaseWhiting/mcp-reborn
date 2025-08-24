package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import net.minecraft.advancements.criterion.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.potion.Effects;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementManager extends JsonReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).create();
   private AdvancementList advancements = new AdvancementList();
   private final LootPredicateManager predicateManager;

   public AdvancementManager(LootPredicateManager p_i232595_1_) {
      super(GSON, "advancements");
      this.predicateManager = p_i232595_1_;
   }

   protected void apply(Map<ResourceLocation, JsonElement> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      Map<ResourceLocation, Advancement.Builder> map = Maps.newHashMap();

      addHardcodedAdvancements(map);
      p_212853_1_.forEach((p_240923_2_, p_240923_3_) -> {
         try {
            JsonObject jsonobject = JSONUtils.convertToJsonObject(p_240923_3_, "advancement");
            Advancement.Builder advancement$builder = Advancement.Builder.fromJson(jsonobject, new ConditionArrayParser(p_240923_2_, this.predicateManager));
            map.put(p_240923_2_, advancement$builder);
         } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
            LOGGER.error("Parsing error loading custom advancement {}: {}", p_240923_2_, jsonparseexception.getMessage());
         }

      });

       // === Load custom advancements from fixed directory ===
       String customAdvDirPath = "G:\\MCP-Reborn-1.16-MOJO\\src\\main\\resources\\data\\minecraft\\customAdvancements";
       File customAdvDir = new File(customAdvDirPath);

       if (customAdvDir.exists() && customAdvDir.isDirectory()) {
           try (Stream<Path> paths = Files.walk(customAdvDir.toPath())) {
               paths.filter(Files::isRegularFile)
                       .filter(path -> path.toString().endsWith(".json"))
                       .forEach(path -> {
                           try (FileReader reader = new FileReader(path.toFile())) {
                               JsonElement json = JSONUtils.fromJson(GSON, reader, JsonElement.class);
                               String relPath = customAdvDir.toPath().relativize(path).toString().replace(File.separatorChar, '/');
                               String advancementName = relPath.substring(0, relPath.length() - ".json".length());
                               ResourceLocation advancementId = new ResourceLocation("minecraft", advancementName);

                               if (!map.containsKey(advancementId)) {
                                   JsonObject jsonobject = JSONUtils.convertToJsonObject(json, "advancement");
                                   Advancement.Builder builder = Advancement.Builder.fromJson(jsonobject, new ConditionArrayParser(advancementId, this.predicateManager));
                                   map.put(advancementId, builder);
                                   LOGGER.info("Loaded custom advancement: {}", advancementId);
                               } else {
                                   LOGGER.warn("Custom advancement '{}' skipped (already exists)", advancementId);
                               }
                           } catch (Exception e) {
                               LOGGER.error("Error loading custom advancement from file {}", path.toString(), e);
                           }
                       });
           } catch (IOException e) {
               LOGGER.error("Error walking custom advancements directory {}", customAdvDirPath, e);
           }
       } else {
           LOGGER.warn("Custom advancement directory '{}' does not exist or is not a directory.", customAdvDirPath);
       }
// === End custom advancement loading ===


      AdvancementList advancementlist = new AdvancementList();
      advancementlist.add(map);

      for(Advancement advancement : advancementlist.getRoots()) {
         if (advancement.getDisplay() != null) {
            AdvancementTreeNode.run(advancement);
         }
      }

      this.advancements = advancementlist;
   }

   private void addHardcodedAdvancements(Map<ResourceLocation, Advancement.Builder> builderMap) {
      builderMap.put(new ResourceLocation("minecraft", "end/encounter_enderiophage"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Items.ENDER_EYE),
                              new StringTextComponent("Don't go near that thing!"),
                              new StringTextComponent("Encounter an Enderiophage"),
                              null, FrameType.TASK, true, true, false
                      ).parent(new ResourceLocation("end/kill_dragon"))
                      .addCriterion("encounter_enderiophage", new EncounterEnderiophage.Instance(EntityPredicate.AndPredicate.ANY)));

      builderMap.put(new ResourceLocation("minecraft", "end/enderiophage_steals_eye"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Items.ENDER_PEARL),
                              new StringTextComponent("No Respect!"),
                              new StringTextComponent("Witness an Enderiophage stealing an Eye of Ender from an Enderman, leaving it temporary blind"),
                              null, FrameType.TASK, true, true, false
                      ).parent(new ResourceLocation("end/encounter_enderiophage"))
                      .addCriterion("enderiophage_steals_eye", new EnderiophageStealEye.Instance(EntityPredicate.AndPredicate.ANY)));

       builderMap.put(new ResourceLocation("minecraft", "end/enderiophage_drop_blaze_powder"),
               Advancement.Builder.advancement()
                       .display(
                               new ItemStack(Items.BLAZE_POWDER),
                               new StringTextComponent("Natural Residue"),
                               new StringTextComponent("Pick up Blaze Powder that has been left behind as a byproduct by an Enderiophage after stealing the Eye from an Enderman"),
                               null, FrameType.GOAL, true, true, false
                       ).parent(new ResourceLocation("end/enderiophage_steals_eye"))
                       .addCriterion("enderiophage_blaze_powder", new BlazePowderFromEnderiophage.Instance(EntityPredicate.AndPredicate.ANY)));

      builderMap.put(new ResourceLocation("minecraft", "end/give_enderiophage_eye"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Items.ENDER_EYE),
                              new StringTextComponent("Worst Decision of My Life"),
                              new StringTextComponent("Give an Enderiophage back its Eye of Ender"),
                              null, FrameType.TASK, true, true, false
                      ).parent(new ResourceLocation("end/encounter_enderiophage"))
                      .addCriterion(
                              "give_enderiophage_eye",
                              new EnderiophagePickUpEye.Instance(EntityPredicate.AndPredicate.ANY))
                      .addCriterion("give_enderiophage_eye_direct",
                              PlayerEntityInteractionTrigger.Instance.itemUsedOnEntity(
                                      EntityPredicate.AndPredicate.ANY,
                                      ItemPredicate.Builder.item().of(Items.ENDER_EYE),
                                      EntityPredicate.AndPredicate.wrap(EntityPredicate.Builder.entity().of(EntityType.ENDERIOPHAGE).build())
                              )).requirements(IRequirementsStrategy.OR));

      builderMap.put(new ResourceLocation("minecraft", "end/killed_enderiophage_while_breeding"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Items.ENDER_EYE),
                              new StringTextComponent("Sorry, Couldn't Let Ya Do It!"),
                              new StringTextComponent("Kill an Enderiophage just before it can reproduce"),
                              null, FrameType.TASK, true, true, false
                      ).parent(new ResourceLocation("end/give_enderiophage_eye"))
                      .addCriterion("kill_enderiophage_breeding", new EnderiophageKilledWhileBreeding.Instance(EntityPredicate.AndPredicate.ANY)));

      builderMap.put(new ResourceLocation("minecraft", "end/watch_enderiophage_breed"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Items.DNA),
                              new StringTextComponent("Fascinating!"),
                              new StringTextComponent("Find out how an Enderiophage reproduces"),
                              null, FrameType.GOAL, true, true, false
                      ).parent(new ResourceLocation("end/give_enderiophage_eye"))
                      .addCriterion("watch_enderiophage_breed", new WatchEnderiophageBreedTrigger.Instance(EntityPredicate.AndPredicate.ANY)));

       builderMap.put(new ResourceLocation("minecraft", "end/failed_experiment"),
               Advancement.Builder.advancement()
                       .display(
                               new ItemStack(Items.DNA),
                               new StringTextComponent("Failed Experiment"),
                               new StringTextComponent("Watch an Enderiophage birth offspring that doesn't have a Capsid"),
                               null, FrameType.CHALLENGE, true, true, false
                       ).parent(new ResourceLocation("end/watch_enderiophage_breed"))
                       .addCriterion("failed_experiment", new EnderiophageFailedExperiment.Instance(EntityPredicate.AndPredicate.ANY)));

       builderMap.put(new ResourceLocation("minecraft", "end/enderiophage_restoration"),
               Advancement.Builder.advancement()
                       .display(
                               new ItemStack(Items.GLASS),
                               new StringTextComponent("Restoration"),
                               new StringTextComponent("Save an Enderiophage with a broken Capsid using a Glass Block, giving it unconventional protection"),
                               null, FrameType.GOAL, true, true, false
                       ).parent(new ResourceLocation("end/failed_experiment"))
                       .addCriterion("restoration", new EnderiophageRestoration.Instance(EntityPredicate.AndPredicate.ANY)));

      builderMap.put(new ResourceLocation("minecraft", "end/dissect_enderiophage"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Items.DNA),
                              new StringTextComponent("I was just testing!"),
                              new StringTextComponent("Dissect an Enderiophage by cutting out its Eye of Ender with a pair of Shears, angering the rest of them"),
                              null, FrameType.GOAL, true, true, false
                      ).parent(new ResourceLocation("end/watch_enderiophage_breed"))
                      .addCriterion("dissect_enderiophage", new DissectEnderiophage.Instance(EntityPredicate.AndPredicate.ANY)));

      builderMap.put(new ResourceLocation("minecraft", "end/dissect_enderiophage_broken_capsid"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Items.SHEARS),
                              new StringTextComponent("Oops.."),
                              new StringTextComponent("Accidentally break an Enderiophage's Capsid while dissecting it, causing it to suffocate"),
                              null, FrameType.CHALLENGE, true, true, true
                      ).parent(new ResourceLocation("end/dissect_enderiophage"))
                      .addCriterion("dissect_enderiophage_broken_capsid", new BrokenCapsid.Instance(EntityPredicate.AndPredicate.ANY)));

       builderMap.put(new ResourceLocation("minecraft", "end/dissect_repaired_enderiophage"),
               Advancement.Builder.advancement()
                       .display(
                               new ItemStack(Items.SHEARS),
                               new StringTextComponent("How could you!?"),
                               new StringTextComponent("Try to dissect an Enderiophage with an already repaired Capsid, and watch it suffocate and die"),
                               null, FrameType.CHALLENGE, true, true, false
                       ).parent(new ResourceLocation("end/dissect_enderiophage"))
                       .addCriterion("dissect_enderiophage_destroy_capsid", new DestroyCapsid.Instance(EntityPredicate.AndPredicate.ANY)));

      builderMap.put(new ResourceLocation("minecraft", "end/catch_ender_flu"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Items.ENDER_FLU_ICON),
                              new StringTextComponent("End-Pandemic"),
                              new StringTextComponent("Catch the Ender Flu! Eat Chorus Fruit or drink milk to cure it!"),
                              null, FrameType.TASK, true, true, false
                      ).parent(new ResourceLocation("end/encounter_enderiophage"))
                      .addCriterion("catch_ender_flu", EffectsChangedTrigger.Instance.hasEffects(MobEffectsPredicate.effects().and(Effects.ENDER_FLU))));

      builderMap.put(new ResourceLocation("minecraft", "end/ender_flu_runs_out"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Items.ENDER_FLU_ICON),
                              new StringTextComponent("Time's up!"),
                              new StringTextComponent("Watch the Enderiophage multiply after the Ender Flu effect runs out"),
                              null, FrameType.TASK, true, true, false
                      ).parent(new ResourceLocation("end/catch_ender_flu"))
                      .addCriterion("ender_flu_ran_out", new EnderiophageSpawnFromFlu.Instance(EntityPredicate.AndPredicate.ANY)));

      builderMap.put(new ResourceLocation("minecraft", "end/enderiophage_infect"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Items.ENDER_FLU_ICON),
                              new StringTextComponent("Phage-Pocalypse"),
                              new StringTextComponent("Find a way to get an Enderiophage to infect something that isn't a Player, an Enderman, or an Iron Golem"),
                              null, FrameType.CHALLENGE, true, true, false
                      ).parent(new ResourceLocation("end/catch_ender_flu"))
                      .addCriterion("withness_enderiophage_infect", new EnderiophageInfectMobs.Instance(EntityPredicate.AndPredicate.ANY)));

      builderMap.put(new ResourceLocation("minecraft", "end/save_mob_from_ender_flu"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Items.ENDER_FLU_ICON),
                              new StringTextComponent("So, am I a doctor now?"),
                              new StringTextComponent("Kill an Enderiophage before it can infect a mob with Ender Flu"),
                              null, FrameType.CHALLENGE, true, true, true
                      ).parent(new ResourceLocation("end/catch_ender_flu"))
                      .addCriterion("save_mob_from_ender_flu", new EnderiophageKilledWhileInfecting.Instance(EntityPredicate.AndPredicate.ANY)));

       builderMap.put(new ResourceLocation("minecraft", "end/ender_relocation"),
               Advancement.Builder.advancement()
                       .display(
                               new ItemStack(Items.BLAZE_POWDER),
                               new StringTextComponent("Ender-Relocation"),
                               new StringTextComponent("Catch the Ender Flu and let it run out in the Overworld or the Nether, letting Enderiophages of that type spawn"),
                               null,
                               FrameType.GOAL,
                               true,
                               true,
                               false
                       )
                       .parent(new ResourceLocation("end/give_enderiophage_eye"))
                       .addCriterion("ender_relocation", new EnderRelocation.Instance(EntityPredicate.AndPredicate.ANY))
       );

       builderMap.put(new ResourceLocation("minecraft", "end/enderiophage_attack_blaze"),
               Advancement.Builder.advancement()
                       .display(
                               new ItemStack(Items.BLAZE_ROD),
                               new StringTextComponent("Nether Relations"),
                               new StringTextComponent("Bring an Enderiophage to the Nether and watch it infect a Blaze to get its share of Blaze Powder from it"),
                               null,
                               FrameType.GOAL,
                               true,
                               true,
                               false
                       )
                       .parent(new ResourceLocation("end/ender_relocation"))
                       .addCriterion("enderiophage_attacks_blaze", new EnderiophageAttackBlaze.Instance(EntityPredicate.AndPredicate.ANY))
       );

      builderMap.put(new ResourceLocation("minecraft", "end/colorful_display"),
              Advancement.Builder.advancement()
                      .display(
                              new ItemStack(Blocks.PURPLE_STAINED_GLASS),
                              new StringTextComponent("A Colourful Display"),
                              new StringTextComponent("Change the colour glass of an Enderiophage that is from the Overworld or has had its Capsid repaired using Stained Glass"),
                              null,
                              FrameType.GOAL,
                              true,
                              true,
                              false
                      )
                      .parent(new ResourceLocation("end/ender_relocation"))
                      .addCriterion("color_capsid", new ChangeCapsidColour.Instance(EntityPredicate.AndPredicate.ANY))
      );
   }


   private Advancement.Builder create(Item display, String title, String description, FrameType frameType) {
      return Advancement.Builder.advancement()
              .display(new ItemStack(display), new StringTextComponent(title), new StringTextComponent(description), null, frameType, true, true, false);
   }

   private ResourceLocation location(String s) {
      return new ResourceLocation("minecraft", s);
   }


   @Nullable
   public Advancement getAdvancement(ResourceLocation p_192778_1_) {
      return this.advancements.get(p_192778_1_);
   }

   public Collection<Advancement> getAllAdvancements() {
      return this.advancements.getAllAdvancements();
   }
}