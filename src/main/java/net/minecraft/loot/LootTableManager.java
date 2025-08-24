package net.minecraft.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.FileReader;
import java.util.*;

import com.google.gson.JsonParser;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.*;
import net.minecraft.block.family.SimpleWoodFamily;
import net.minecraft.block.family.WoodFamilies;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.monster.SpellcastingIllagerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.conditions.*;
import net.minecraft.loot.functions.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.Property;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.storage.MapDecoration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.data.loot.BlockLootTables.*;

public class LootTableManager extends JsonReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = LootSerializers.createLootTableSerializer().create();
   private Map<ResourceLocation, LootTable> tables = ImmutableMap.of();
   private final LootPredicateManager predicateManager;

   public LootTableManager(LootPredicateManager p_i225887_1_) {
      super(GSON, "loot_tables");
      this.predicateManager = p_i225887_1_;
   }

   public LootTable get(ResourceLocation p_186521_1_) {
      return this.tables.getOrDefault(p_186521_1_, LootTable.EMPTY);
   }

   protected void apply(Map<ResourceLocation, JsonElement> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      Builder<ResourceLocation, LootTable> builder = ImmutableMap.builder();
      JsonElement jsonelement = p_212853_1_.remove(LootTables.EMPTY);
      if (jsonelement != null) {
         LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)LootTables.EMPTY);
      }

      addHardcodedLootTables(builder);
      Set<ResourceLocation> resourceLocations = new HashSet<>();
      this.overwriteExistingLootTables(builder, resourceLocations);
      p_212853_1_.forEach((p_237403_1_, p_237403_2_) -> {
         try {
            LootTable loottable = GSON.fromJson(p_237403_2_, LootTable.class);

             if (!resourceLocations.contains(p_237403_1_)) {
                 builder.put(p_237403_1_, loottable);
             }
         } catch (Exception exception) {
            LOGGER.error("Couldn't parse loot table {}", p_237403_1_, exception);
         }

      });
      builder.put(LootTables.EMPTY, LootTable.EMPTY);
      ImmutableMap<ResourceLocation, LootTable> immutablemap = builder.build();
      ValidationTracker validationtracker = new ValidationTracker(LootParameterSets.ALL_PARAMS, this.predicateManager::get, immutablemap::get);
      immutablemap.forEach((p_227509_1_, p_227509_2_) -> {
         validate(validationtracker, p_227509_1_, p_227509_2_);
      });
      validationtracker.getProblems().forEach((p_215303_0_, p_215303_1_) -> {
         LOGGER.warn("Found validation problem in " + p_215303_0_ + ": " + p_215303_1_);
      });
      this.tables = immutablemap;
   }

   protected LootTable.Builder createCopperOreDrops(Block block) {
      return createSilkTouchDispatchTable(block, this.applyExplosionDecay(block, (LootItem.lootTableItem(Items.RAW_COPPER).apply(SetCount.setCount(RandomValueRange.between(2.0f, 5.0f)))).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
   }

   protected LootTable.Builder createLapisOreDrops(Block block) {
      return createSilkTouchDispatchTable(block, this.applyExplosionDecay(block, (LootItem.lootTableItem(Items.LAPIS_LAZULI).apply(SetCount.setCount(RandomValueRange.between(4.0f, 9.0f)))).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
   }

   protected LootTable.Builder createRedstoneOreDrops(Block block) {
      return createSilkTouchDispatchTable(block, this.applyExplosionDecay(block, (LootItem.lootTableItem(Items.LAPIS_LAZULI).apply(SetCount.setCount(RandomValueRange.between(4.0f, 5.0f)))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))));
   }

   protected LootTable.Builder createOreDrop(Block block, Item item) {
      return createSilkTouchDispatchTable(block, this.applyExplosionDecay(block, LootItem.lootTableItem(item).apply(ApplyBonus.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
   }


   private static LootTable.Builder createSelfDropDispatchTable(Block p_218494_0_, ILootCondition.IBuilder p_218494_1_, LootEntry.Builder<?> p_218494_2_) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218494_0_).when(p_218494_1_).otherwise(p_218494_2_)));
   }

   private static LootTable.Builder createSilkTouchDispatchTable(Block p_218519_0_, LootEntry.Builder<?> p_218519_1_) {
      return createSelfDropDispatchTable(p_218519_0_, HAS_SILK_TOUCH, p_218519_1_);
   }


   private void overwriteExistingLootTables(Builder<ResourceLocation, LootTable> builder, Set<ResourceLocation> added) {
      addOreDrops(builder, added);

      this.addSimple(LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(Items.GOLDEN_APPLE).setWeight(20)).add(ItemLootEntry.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE)).add(ItemLootEntry.lootTableItem(Items.NAME_TAG).setWeight(30)).add(ItemLootEntry.lootTableItem(Items.BOOK).setWeight(10).apply(EnchantRandomly.randomApplicableEnchantment())).add(ItemLootEntry.lootTableItem(Items.IRON_PICKAXE).setWeight(5)).add(EmptyLootEntry.emptyItem().setWeight(5))).withPool(LootPool.lootPool().setRolls(RandomValueRange.between(2.0F, 4.0F)).add(ItemLootEntry.lootTableItem(Items.IRON_INGOT).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(1.0F, 5.0F)))).add(ItemLootEntry.lootTableItem(Items.GOLD_INGOT).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(1.0F, 3.0F)))).add(ItemLootEntry.lootTableItem(Items.REDSTONE).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(4.0F, 9.0F)))).add(ItemLootEntry.lootTableItem(Items.LAPIS_LAZULI).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(4.0F, 9.0F)))).add(ItemLootEntry.lootTableItem(Items.DIAMOND).setWeight(3).apply(SetCount.setCount(RandomValueRange.between(1.0F, 2.0F)))).add(ItemLootEntry.lootTableItem(Items.COAL).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(3.0F, 8.0F)))).add(ItemLootEntry.lootTableItem(Items.BREAD).setWeight(15).apply(SetCount.setCount(RandomValueRange.between(1.0F, 3.0F)))).add(ItemLootEntry.lootTableItem(Items.MELON_SEEDS).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F)))).add(ItemLootEntry.lootTableItem(Items.PUMPKIN_SEEDS).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F)))).add(ItemLootEntry.lootTableItem(Items.BEETROOT_SEEDS).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F))))).withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(3)).add(ItemLootEntry.lootTableItem(Blocks.RAIL).setWeight(20).apply(SetCount.setCount(RandomValueRange.between(4.0F, 8.0F)))).add(ItemLootEntry.lootTableItem(Blocks.GOLDEN_POWERED_RAIL).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(1.0F, 4.0F)))).add(ItemLootEntry.lootTableItem(Blocks.DETECTOR_RAIL).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(1.0F, 4.0F)))).add(ItemLootEntry.lootTableItem(Blocks.ACTIVATOR_RAIL).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(1.0F, 4.0F)))).add(ItemLootEntry.lootTableItem(Blocks.TORCH).setWeight(15).apply(SetCount.setCount(RandomValueRange.between(1.0F, 16.0F))))).build(),
              builder, added, "chests/abandoned_mineshaft");


      this.addSimple(LootTable.lootTable().withPool(LootPool.lootPool()
              .setRolls(ConstantRange.exactly(1))
              .add(ItemLootEntry.lootTableItem(Items.COAL)
                      .apply(SetCount.setCount(RandomValueRange.between(-1.0F, 1.0F)))
                      .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F)))))
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(ItemLootEntry.lootTableItem(Items.BONE)
                              .apply(SetCount.setCount(RandomValueRange.between(0.0F, 2.0F)))
                              .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F)))))
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(ItemLootEntry.lootTableItem(Blocks.WITHER_SKELETON_SKULL))
                      .when(KilledByPlayer.killedByPlayer())
                      .when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.025F, 0.01F)))

              .withPool(LootPool.lootPool().setRolls(RandomValueRange.between(0.0F, 1.0F))
                      .add(ItemLootEntry.lootTableItem(Items.WITHER_SKELETON_RIBCAGE)
                              .when(KilledByPlayer.killedByPlayer())
                              .when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.05F, 0.015F))))

              .withPool(LootPool.lootPool().setRolls(RandomValueRange.between(0.0F, 2.0F))
                      .add(ItemLootEntry.lootTableItem(Items.WITHER_BONE).setCount(RandomValueRange.between(0F, 2F))
                              .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F)))
                              .when(KilledByPlayer.killedByPlayer())
                              .when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.35F, 0.025F)))
                      .add(ItemLootEntry.lootTableItem(Items.FORTIFIED_WITHER_BONE).setCount(RandomValueRange.between(0F, 1F))
                              .when(KilledByPlayer.killedByPlayer())
                              .when(RandomChanceWithLooting.randomChanceAndLootingBoost(0.2F, 0.01F))))

              .build(), builder, added, "entities/wither_skeleton");

      addSimple(LootTable.lootTable().withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .add(ItemLootEntry.lootTableItem(Items.SHULKER_SHELL).setCount(RandomValueRange.between(1F, 2F)))
                      ).build(),
              builder, added, "entities/shulker"
              );
   }

   private void addSimple(LootTable table, Builder<ResourceLocation, LootTable> builder, Set<ResourceLocation> added, String id) {
      ResourceLocation resourceLocation = new ResourceLocation(id);
      added.add(resourceLocation);
      builder.put(resourceLocation, table);
   }

   private void addOreDrops(Builder<ResourceLocation, LootTable> builder, Set<ResourceLocation> added) {
      addSimple(this.createCopperOreDrops(Blocks.COPPER_ORE).build(), builder, added, "blocks/copper_ore");
      addSimple(this.createOreDrop(Blocks.IRON_ORE, Items.RAW_IRON).build(), builder, added, "blocks/iron_ore");
      addSimple(this.createOreDrop(Blocks.GOLD_ORE, Items.RAW_GOLD).build(), builder, added, "blocks/gold_ore");
   }




   private void addHardcodedLootTables(Builder<ResourceLocation, LootTable> builder) {
      // Custom loot table for Dead Leaves
      ResourceLocation deadLeavesLootTableId = new ResourceLocation("minecraft", "blocks/dead_leaves");
      builder.put(deadLeavesLootTableId, createDeadLeavesLootTable().build());
      ResourceLocation appleTree = new ResourceLocation("minecraft", "blocks/apple_leaves");
      builder.put(appleTree, createAppleLeavesLootTable().build());


      createSelfDrop(builder, "blocks/tinted_glass", Blocks.TINTED_GLASS, Always::new);
      createSelfDrop(builder, "blocks/piglin_head", Blocks.PIGLIN_HEAD, Always::new);
      createSelfDrop(builder, "blocks/piglin_wall_head", Blocks.PIGLIN_HEAD, Always::new);

      createSelfDrop(builder, "blocks/mud_bricks", Blocks.MUD_BRICKS, Always::new);
      createSelfDrop(builder, "blocks/packed_mud", Blocks.PACKED_MUD, Always::new);

      createSimpleSelfDrop(builder, Blocks.PALE_MOSS_BLOCK);
      createSimpleSelfDrop(builder, Blocks.PALE_MOSS_CARPET);
      createSimpleSelfDrop(builder, Blocks.ROSE_GOLD_BLOCK);
      createSimpleSelfDrop(builder, Blocks.WOODCUTTER);
      createSimpleSelfDrop(builder, Blocks.GOLDEN_POWERED_RAIL);
      createSelfDrop(builder, "blocks/pale_hanging_moss", Blocks.PALE_HANGING_MOSS, HAS_SHEARS);
      createSelfDrop(builder, "blocks/pale_oak_log", Blocks.PALE_OAK_LOG, Always::new);
      createSelfDrop(builder, "blocks/pale_oak_planks", Blocks.PALE_OAK_PLANKS, Always::new);
      createSelfDrop(builder, "blocks/mud", Blocks.MUD, Always::new);
      createSelfDrop(builder, "blocks/lightning_rod", Blocks.LIGHTNING_ROD, Always::new);
      createSelfDrop(builder, "blocks/open_eyeblossom", Blocks.OPEN_EYEBLOSSOM, Always::new);
      createSelfDrop(builder, "blocks/closed_eyeblossom", Blocks.CLOSED_EYEBLOSSOM, Always::new);
      createSelfDrop(builder, "blocks/firefly_bush", Blocks.FIREFLY_BUSH, Always::new);
      createSelfDrop(builder, "blocks/cactus_flower", Blocks.CACTUS_FLOWER, Always::new);
      createSelfDrop(builder, "blocks/short_dry_grass", Blocks.SHORT_DRY_GRASS, HAS_SHEARS);
      createSelfDrop(builder, "blocks/tall_dry_grass", Blocks.TALL_DRY_GRASS, HAS_SHEARS);
      createSelfDrop(builder, "blocks/sculk", Blocks.SCULK, HAS_SILK_TOUCH);


      createSimpleSelfDrop(builder, Blocks.RESIN_BLOCK);

      createPetalBlockLootTable(builder, "leaf_litter", Blocks.LEAF_LITTER, Items.LEAF_LITTER);
      createPetalBlockLootTable(builder, "wildflowers", Blocks.WILDFLOWERS, Items.WILDFLOWERS);
      createPetalBlockLootTable(builder, "pink_petals", Blocks.PINK_PETALS, Items.PINK_PETALS);
      createPetalBlockLootTable(builder, "pale_leaf_pile", Blocks.PALE_LEAF_PILE, Items.PALE_LEAF_PILE);


      for (Block block : Registry.BLOCK.stream().filter(block -> block instanceof CandleBlock).toList()) {
         createCandleBlockLootTable(builder, block.getDescriptionId().replace("block.minecraft.", ""), (CandleBlock) block, block.asItem());
      }

      createMultifaceBlockLootTable(builder, "resin_clump", Blocks.RESIN_CLUMP);

      LootTable tricksterLoot = LootTable.lootTable()
              .withPool(LootPool.lootPool()
                      .setRolls(RandomValueRange.between(1, 2))
                      .add(ItemLootEntry.lootTableItem(Items.FIREWORK_ARROW)).apply(
                              SetNBT.setTag(convertToEntityTag(this.getRocket(SpellcastingIllagerEntity.SpellType.FIREWORK)))
                      ).apply(SetCount.setCount(RandomValueRange.between(4, 8))).when(KilledByPlayer.killedByPlayer()))
              .withPool(LootPool.lootPool()
                      .setRolls(RandomValueRange.between(1, 2))
                      .add(ItemLootEntry.lootTableItem(Items.FIREWORK_ARROW)).apply(
                              SetNBT.setTag(convertToEntityTag(this.getRocket(SpellcastingIllagerEntity.SpellType.FIREWORK_LAUNCH)))
                      ).apply(SetCount.setCount(RandomValueRange.between(4, 8))).when(KilledByPlayer.killedByPlayer()))
              .withPool(LootPool.lootPool()
                      .setRolls(RandomValueRange.between(1, 2))
                      .add(ItemLootEntry.lootTableItem(Items.FIREWORK_ARROW)).apply(
                              SetNBT.setTag(convertToEntityTag(this.getRocket(SpellcastingIllagerEntity.SpellType.FIREWORK_CIRCLE)))
                      ).apply(SetCount.setCount(RandomValueRange.between(4, 8))).when(KilledByPlayer.killedByPlayer()))
              .withPool(LootPool.lootPool()
                      .setRolls(RandomValueRange.between(1, 2))
                      .add(ItemLootEntry.lootTableItem(Items.FIREWORK_ARROW)).apply(
                              SetNBT.setTag(convertToEntityTag(this.getRocket(SpellcastingIllagerEntity.SpellType.BLINDNESS)))
                      ).apply(SetCount.setCount(RandomValueRange.between(4, 8)).when(KilledByPlayer.killedByPlayer()))).build();

      builder.put(new ResourceLocation("entities/trickster"), tricksterLoot);




      LootTable desertWellArchaeologyLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(ItemLootEntry.lootTableItem(Items.ARMS_UP_POTTERY_SHERD).setWeight(2))
                      .add(LootItem.lootTableItem(Items.BREWER_POTTERY_SHERD).setWeight(2))
                      .add(LootItem.lootTableItem(Items.BRICK))
                      .add(LootItem.lootTableItem(Items.EMERALD))
                      .add(LootItem.lootTableItem(Items.STICK))
                      .add((LootItem.lootTableItem(Items.SUSPICIOUS_STEW)
                              .apply(SetStewEffect.stewEffect().withEffect(Effects.NIGHT_VISION, RandomValueRange.between(7.0f, 10.0f))
                                      .withEffect(Effects.JUMP, RandomValueRange.between(7.0f, 10.0f))
                                      .withEffect(Effects.WEAKNESS, RandomValueRange.between(6.0f, 8.0f))
                                      .withEffect(Effects.BLINDNESS, RandomValueRange.between(5.0f, 7.0f))
                                      .withEffect(Effects.POISON, RandomValueRange.between(10.0f, 20.0f))
                                      .withEffect(Effects.SATURATION, RandomValueRange.between(7.0f, 10.0f)))))).build();

      builder.put(LootTables.DESERT_WELL_ARCHAEOLOGY, desertWellArchaeologyLootTable);

      LootTable desertPyramidArchaeologyLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.ARCHER_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.MINER_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.PRIZE_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.SKULL_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.DIAMOND))
                      .add(LootItem.lootTableItem(Items.TNT))
                      .add(LootItem.lootTableItem(Items.GUNPOWDER))
                      .add(LootItem.lootTableItem(Items.EMERALD))).build();

      builder.put(LootTables.DESERT_PYRAMID_ARCHAEOLOGY, desertPyramidArchaeologyLootTable);


      LootTable trailRuinsArchaeologyRareLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.BURN_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.DANGER_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.FRIEND_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.HEART_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.HEARTBREAK_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.HOWL_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.SHEAF_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.POT_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE))
                      .add(LootItem.lootTableItem(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE))
                      .add(LootItem.lootTableItem(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE))
                      .add(LootItem.lootTableItem(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE))
                      .add(LootItem.lootTableItem(Items.MUSIC_DISC_RELIC))).build();

      builder.put(LootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE, trailRuinsArchaeologyRareLootTable);

      LootTable trailRuinsArchaeologyCommonLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.EMERALD).setWeight(2))
                      .add(LootItem.lootTableItem(Items.WHEAT).setWeight(2))
                      .add(LootItem.lootTableItem(Items.WOODEN_HOE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.CLAY).setWeight(2))
                      .add(LootItem.lootTableItem(Items.BRICK).setWeight(2))
                      .add(LootItem.lootTableItem(Items.YELLOW_DYE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.BLUE_DYE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.LIGHT_BLUE_DYE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.WHITE_DYE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.ORANGE_DYE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.RED_CANDLE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.GREEN_CANDLE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.PURPLE_CANDLE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.BROWN_CANDLE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.MAGENTA_STAINED_GLASS_PANE))
                      .add(LootItem.lootTableItem(Items.PINK_STAINED_GLASS_PANE))
                      .add(LootItem.lootTableItem(Items.BLUE_STAINED_GLASS_PANE))
                      .add(LootItem.lootTableItem(Items.LIGHT_BLUE_STAINED_GLASS_PANE))
                      .add(LootItem.lootTableItem(Items.RED_STAINED_GLASS_PANE))
                      .add(LootItem.lootTableItem(Items.YELLOW_STAINED_GLASS_PANE))
                      .add(LootItem.lootTableItem(Items.PURPLE_STAINED_GLASS_PANE))
//                      .add(LootItem.lootTableItem(Items.SPRUCE_HANGING_SIGN))
//                      .add(LootItem.lootTableItem(Items.OAK_HANGING_SIGN))
                      .add(LootItem.lootTableItem(Items.GOLD_NUGGET))
                      .add(LootItem.lootTableItem(Items.COAL))
                      .add(LootItem.lootTableItem(Items.WHEAT_SEEDS))
                      .add(LootItem.lootTableItem(Items.BEETROOT_SEEDS))
                      .add(LootItem.lootTableItem(Items.DEAD_BUSH))
                      .add(LootItem.lootTableItem(Items.FLOWER_POT))
                      .add(LootItem.lootTableItem(Items.STRING))
                      .add(LootItem.lootTableItem(Items.LEAD))).build();

      builder.put(LootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON, trailRuinsArchaeologyCommonLootTable);

      LootTable oceanRuinColdArchaeologyLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.BLADE_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.EXPLORER_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.MOURNER_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.PLENTY_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.IRON_AXE))
                      .add(LootItem.lootTableItem(Items.EMERALD).setWeight(2))
                      .add(LootItem.lootTableItem(Items.WHEAT).setWeight(2))
                      .add(LootItem.lootTableItem(Items.WOODEN_HOE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.COAL).setWeight(2))
                      .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(2))).build();

      builder.put(LootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY, oceanRuinColdArchaeologyLootTable);

      LootTable oceanRuinWarmArchaeologyLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.ANGLER_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.SHELTER_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.SNORT_POTTERY_SHERD))
                      .add(LootItem.lootTableItem(Items.SNIFFER_EGG))
                      .add(LootItem.lootTableItem(Items.IRON_AXE))
                      .add(LootItem.lootTableItem(Items.EMERALD).setWeight(2))
                      .add(LootItem.lootTableItem(Items.WHEAT).setWeight(2))
                      .add(LootItem.lootTableItem(Items.WOODEN_HOE).setWeight(2))
                      .add(LootItem.lootTableItem(Items.COAL).setWeight(2))
                      .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(2))).build();

      builder.put(LootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY, oceanRuinWarmArchaeologyLootTable);


      LootTable swampClayArchaeologyLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(7))
                      .add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(5))
                      .add(LootItem.lootTableItem(Items.CLAY_BALL).setCount(RandomValueRange.between(1F, 3.15F)).setWeight(5))
                      .add(LootItem.lootTableItem(Items.CLAY_BALL).setCount(RandomValueRange.between(2F, 4F)).setWeight(3))
                      .add(LootItem.lootTableItem(Items.CLAY))
                      .add(LootItem.lootTableItem(Items.MOURNER_POTTERY_SHERD).setWeight(2))
                      .add(LootItem.lootTableItem(Items.ANGLER_POTTERY_SHERD).setWeight(2))
                      .add(LootItem.lootTableItem(Items.EXPLORER_POTTERY_SHERD).setWeight(2))
                      .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(2))
                      .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setCount(RandomValueRange.between(1F, 2F)).setWeight(2))
                      .add(LootItem.lootTableItem(Items.STRING).setCount(RandomValueRange.between(1F, 3F)).setWeight(3))
                      .add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(3).setCount(RandomValueRange.between(1F, 5F)))
                      .add(LootItem.lootTableItem(Items.LILY_PAD).setWeight(3))
                      .add(LootItem.lootTableItem(Items.BONE).setCount(RandomValueRange.between(1F, 3F)).setWeight(2))
                      .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(2))
                      .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setCount(RandomValueRange.between(1F, 2F)))
                      .add(LootItem.lootTableItem(Items.BOW).apply(SetDamage.setDamage(RandomValueRange.between(0.35F, 0.85F))).setWeight(2))
                      .add(LootItem.lootTableItem(Items.BOW).apply(SetDamage.setDamage(RandomValueRange.between(0.4F, 0.75F)))
                              .apply(EnchantWithLevels.enchantWithLevels(RandomValueRange.between(10F, 15F)).allowTreasure()))
                      .add(LootItem.lootTableItem(Items.STONE_SHOVEL).apply(SetDamage.setDamage(RandomValueRange.between(0.25F, 0.55F))))
                      .add(LootItem.lootTableItem(Items.IRON_SHOVEL).apply(SetDamage.setDamage(RandomValueRange.between(0.7F, 0.9F))))
                      .add(LootItem.lootTableItem(Items.FISHING_ROD).apply(SetDamage.setDamage(RandomValueRange.between(0.15F, 0.23F)))
                              .apply(EnchantRandomly.randomApplicableEnchantment()))
                      .add(LootItem.lootTableItem(Items.POTION).setWeight(3).apply(SetNBT.setTag(Util.
                              make(new CompoundNBT(), nbt -> nbt.putString("Potion", "minecraft:water")))))
                      .add(LootItem.lootTableItem(Items.POTION).apply(SetNBT.setTag(Util.
                              make(new CompoundNBT(), nbt -> nbt.putString("Potion", "minecraft:poison")))))
                      .add(LootItem.lootTableItem(Items.SUSPICIOUS_STEW).apply(SetStewEffect.stewEffect()
                              .withEffect(Effects.POISON, RandomValueRange.between(5F, 15F))
                              .withEffect(Effects.WEAKNESS, RandomValueRange.between(20F, 40F))
                              .withEffect(Effects.BLINDNESS, RandomValueRange.between(10F, 15F))
                              .withEffect(Effects.ABSORPTION, RandomValueRange.between(25F, 45F))))).build();


      builder.put(LootTables.RIVER_SWAMP_CLAY_ARCHAEOLOGY, swampClayArchaeologyLootTable);


      
      LootTable riverClayArchaeologyLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(7))
                      .add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(6))
                      .add(LootItem.lootTableItem(Items.CLAY_BALL).setCount(RandomValueRange.between(1F, 3.15F)).setWeight(5))
                      .add(LootItem.lootTableItem(Items.CLAY_BALL).setCount(RandomValueRange.between(2F, 4F)).setWeight(3))
                      .add(LootItem.lootTableItem(Items.CLAY))
                      .add(LootItem.lootTableItem(Items.PLENTY_POTTERY_SHERD).setWeight(2))
                      .add(LootItem.lootTableItem(Items.MINER_POTTERY_SHERD).setWeight(2))
                      .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(2))
                      .add(LootItem.lootTableItem(Items.STRING).setCount(RandomValueRange.between(1F, 2F)).setWeight(2))
                      .add(LootItem.lootTableItem(Items.CLAY_BALL).setWeight(3).setCount(RandomValueRange.between(1F, 5F)))
                      .add(LootItem.lootTableItem(Items.BONE).setCount(RandomValueRange.between(1F, 3F)).setWeight(3))
                      .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(3))
                      .add(LootItem.lootTableItem(Items.SALMON).setWeight(3))
                      .add(LootItem.lootTableItem(Items.LEATHER_BOOTS).setWeight(2).apply(ConditionalLootFunction.function(DyeRandomly.dyeRandomly().build(), 0.85F))
                              .apply(SetDamage.setDamage(RandomValueRange.between(0.5F, 0.9F))))
                      .add(LootItem.lootTableItem(Items.LEATHER_CHESTPLATE).setWeight(2).apply(ConditionalLootFunction.function(DyeRandomly.dyeRandomly().build(), 0.85F))
                              .apply(SetDamage.setDamage(RandomValueRange.between(0.5F, 0.9F))))
                      .add(LootItem.lootTableItem(Items.LEATHER_LEGGINGS).setWeight(2).apply(ConditionalLootFunction.function(DyeRandomly.dyeRandomly().build(), 0.85F))
                              .apply(SetDamage.setDamage(RandomValueRange.between(0.5F, 0.9F))))
                      .add(LootItem.lootTableItem(Items.LEATHER_HELMET).setWeight(2).apply(ConditionalLootFunction.function(DyeRandomly.dyeRandomly().build(), 0.85F))
                              .apply(SetDamage.setDamage(RandomValueRange.between(0.5F, 0.9F))))
                      .add(LootItem.lootTableItem(Items.BOOK).apply(ConditionalLootFunction.function(EnchantWithLevels.enchantWithLevels(RandomValueRange.between(1, 4)).allowTreasure().build(), 0.25F)))
                      .add(LootItem.lootTableItem(Items.WOODEN_SHOVEL).apply(SetDamage.setDamage(RandomValueRange.between(0.65F, 0.95F))))
                      .add(LootItem.lootTableItem(Items.FISHING_ROD).apply(ConditionalLootFunction.function(EnchantRandomly.randomApplicableEnchantment().build(), 0.05F))
                              .apply(ConditionalLootFunction.function(EnchantRandomly.randomApplicableEnchantment().build(), 0.05F))
                              .apply(SetDamage.setDamage(RandomValueRange.between(0.85F, 0.95F))))
                      .add(LootItem.lootTableItem(Items.MAP).when(RandomChance.randomChance(0.6F))
                              .apply(ConditionalLootFunction.function(ExplorationMap.makeExplorationMap()
                                      .setMapDecoration(MapDecoration.Type.RED_X).setZoom((byte)1)
                                      .setSkipKnownStructures(true).setDestination(Structure.BURIED_TREASURE).build(), 0.15F))))
              .build();


      builder.put(LootTables.RIVER_CLAY_ARCHAEOLOGY, riverClayArchaeologyLootTable);

      LootTable soulSoilArchaeologyLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.BONE).setWeight(7))
                      .add(LootItem.lootTableItem(Items.BONE).setWeight(6))
                      .add(LootItem.lootTableItem(Items.BONE).setCount(RandomValueRange.between(1F, 4.15F)).setWeight(6))
                      .add(LootItem.lootTableItem(Items.BONE).setCount(RandomValueRange.between(2F, 5F)).setWeight(5))
                      .add(LootItem.lootTableItem(Items.BONE).setCount(RandomValueRange.between(1F, 4F)).setWeight(5))
                      .add(LootItem.lootTableItem(Items.BONE_BLOCK).setCount(RandomValueRange.between(1F, 2F)))
                      .add(LootItem.lootTableItem(Items.FLINT).setWeight(2).setCount(RandomValueRange.between(1F, 4F)))
                      .add(LootItem.lootTableItem(Items.STONE_SWORD).setWeight(2).apply(SetDamage.setDamage(RandomValueRange.between(0.35F, 0.8556F))))
                      .add(LootItem.lootTableItem(Items.SKULL_POTTERY_SHERD).setWeight(3))
                      .add(LootItem.lootTableItem(Items.DANGER_POTTERY_SHERD).setWeight(3))
                      .add(LootItem.lootTableItem(Items.BURN_POTTERY_SHERD).setWeight(3))
                      .add(LootItem.lootTableItem(Items.GOLDEN_HELMET).apply(SetDamage.setDamage(RandomValueRange.between(0.5332F, 0.85F))).setWeight(2))
                      .add(LootItem.lootTableItem(Items.GOLDEN_CHESTPLATE).apply(SetDamage.setDamage(RandomValueRange.between(0.5332F, 0.85F))).setWeight(2))
                      .add(LootItem.lootTableItem(Items.GOLDEN_LEGGINGS).apply(SetDamage.setDamage(RandomValueRange.between(0.5332F, 0.85F))).setWeight(2))
                      .add(LootItem.lootTableItem(Items.GOLDEN_BOOTS).apply(SetDamage.setDamage(RandomValueRange.between(0.5332F, 0.85F))).setWeight(2))
                      .add(LootItem.lootTableItem(Items.GOLDEN_SWORD).setWeight(3).apply(SetDamage.setDamage(RandomValueRange.between(0.25F, 0.55F))))
                      .add(LootItem.lootTableItem(Items.CROSSBOW).apply(SetDamage.setDamage(RandomValueRange.between(0.85F, 0.97F))))
                      .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(2))
                      .add(LootItem.lootTableItem(Items.ENDER_PEARL).apply(SetCount.setCount(RandomValueRange.between(1F, 2F))))
                      .add(LootItem.lootTableItem(Items.OBSIDIAN).apply(SetCount.setCount(RandomValueRange.between(1F, 4F))))
                      .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(3).apply(SetCount.setCount(RandomValueRange.between(2F, 6F))))
                      .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(2).apply(SetCount.setCount(RandomValueRange.between(1F, 2F))))
                      .add(LootItem.lootTableItem(Items.GHAST_TEAR))
                      .add(LootItem.lootTableItem(Items.FIRE_CHARGE).setWeight(3).setCount(RandomValueRange.between(3F, 6F)))
                      .add(LootItem.lootTableItem(Items.GUNPOWDER).setWeight(2).apply(SetCount.setCount(RandomValueRange.between(1F, 3.5F))))
                      .add(LootItem.lootTableItem(Items.NETHER_BRICK).setWeight(3).setCount(RandomValueRange.between(2F, 5F)))
                      .add(LootItem.lootTableItem(Items.QUARTZ).setWeight(2).setCount(RandomValueRange.between(1F, 3F)))
                      .add(LootItem.lootTableItem(Items.CHARCOAL).setWeight(3).setCount(RandomValueRange.between(2F, 4F)))
                      .add(LootItem.lootTableItem(Items.SKELETON_SKULL).when(RandomChance.randomChance(0.05F)))
                      .add(LootItem.lootTableItem(Items.WITHER_SKELETON_SKULL).when(RandomChance.randomChance(0.01F)))

              )
              .build();

      builder.put(LootTables.SOUL_SOIL_ARCHAEOLOGY, soulSoilArchaeologyLootTable);

      LootTable fortressCrackedBricksArchaeologyLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.NETHER_BRICK).setWeight(7).setCount(RandomValueRange.between(1F, 4F)))
                      .add(LootItem.lootTableItem(Items.NETHER_BRICK).setWeight(6).setCount(RandomValueRange.between(1F, 2F)))
                      .add(LootItem.lootTableItem(Items.NETHER_BRICK).setWeight(7).setCount(RandomValueRange.between(2F, 3F)))
                      .add(LootItem.lootTableItem(Items.NETHER_BRICK).setWeight(5).setCount(RandomValueRange.between(1F, 6F)))
                      .add(LootItem.lootTableItem(Items.BONE).setWeight(4).setCount(RandomValueRange.between(2F, 3F)))
                      .add(LootItem.lootTableItem(Items.BONE).setWeight(3).setCount(RandomValueRange.between(1F, 4F)))
                      .add(LootItem.lootTableItem(Items.STONE_SWORD).setWeight(3).apply(SetDamage.setDamage(RandomValueRange.between(0.45F, 0.8332F))))
                      .add(LootItem.lootTableItem(Items.BREWER_POTTERY_SHERD).setWeight(3))
                      .add(LootItem.lootTableItem(Items.FRIEND_POTTERY_SHERD).setWeight(2))
                      .add(LootItem.lootTableItem(Items.ARMS_UP_POTTERY_SHERD).setWeight(3))
                      .add(LootItem.lootTableItem(Items.IRON_PICKAXE).apply(SetDamage.setDamage(RandomValueRange.between(0.6F, 0.85F))))
                      .add(LootItem.lootTableItem(Items.CHAINMAIL_BOOTS).apply(SetDamage.setDamage(RandomValueRange.between(0.5F, 0.7F))))
                      .add(LootItem.lootTableItem(Items.IRON_NUGGET).setWeight(3).setCount(RandomValueRange.between(2F, 5F)))
                      .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(2).setCount(RandomValueRange.between(1F, 3F)))
                      .add(LootItem.lootTableItem(Items.SPECTRAL_ARROW).setWeight(2).setCount(RandomValueRange.between(4F, 8.6F)))
                      .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST).setWeight(2).setCount(RandomValueRange.between(1F, 2.5F)))
                      .add(LootItem.lootTableItem(Items.POTION).apply(SetNBT.setTag(Util.make(new CompoundNBT(), nbt -> PotionUtils.setPotion(nbt, Potions.FIRE_RESISTANCE)))))
                      .add(LootItem.lootTableItem(Items.FIRE_CHARGE).setWeight(3).setCount(RandomValueRange.between(2F, 4F)))
                      .add(LootItem.lootTableItem(Items.BLAZE_POWDER).setCount(RandomValueRange.between(1F, 2F)))
                      .add(LootItem.lootTableItem(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE).when(RandomChance.randomChance(0.25F)))
                      .add(LootItem.lootTableItem(Items.NETHERITE_SCRAP).when(RandomChance.randomChance(0.0556F)))
                      .add(LootItem.lootTableItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).when(RandomChance.randomChance(0.1125F)))
                      .add(LootItem.lootTableItem(Items.WITHER_SKELETON_SKULL).when(RandomChance.randomChance(0.04F)))
                      .add(LootItem.lootTableItem(Items.SKELETON_SKULL).when(RandomChance.randomChance(0.09F)))).build();


      builder.put(LootTables.FORTRESS_BRICKS_ARCHAEOLOGY, fortressCrackedBricksArchaeologyLootTable);


      LootTable fortressCrackedBricksNonSuspiciousArchaeologyLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.NETHER_BRICK).setWeight(7).setCount(RandomValueRange.between(1F, 4F)))
                      .add(LootItem.lootTableItem(Items.NETHER_BRICK).setWeight(6).setCount(RandomValueRange.between(1F, 2F)))
                      .add(LootItem.lootTableItem(Items.NETHER_BRICK).setWeight(7).setCount(RandomValueRange.between(2F, 3F)))
                      .add(LootItem.lootTableItem(Items.NETHER_BRICK).setWeight(6).setCount(RandomValueRange.between(1F, 3F)))
                      .add(LootItem.lootTableItem(Items.NETHER_BRICK).setWeight(5).setCount(RandomValueRange.between(1F, 6F)))
                      .add(LootItem.lootTableItem(Items.BONE).setWeight(4).setCount(RandomValueRange.between(2F, 3F)))
                      .add(LootItem.lootTableItem(Items.BONE).setWeight(3).setCount(RandomValueRange.between(1F, 4F)))).build();

      builder.put(LootTables.FORTRESS_BRICKS_NON_SUSPICIOUS_ARCHAEOLOGY, fortressCrackedBricksNonSuspiciousArchaeologyLootTable);


      LootTable camelLoot = LootTable.lootTable()
              .withPool(LootPool.lootPool()
                              .setRolls(ConstantRange.exactly(1))
                              .add(ItemLootEntry.lootTableItem(Items.LEATHER)
                                      .apply(SetCount.setCount(RandomValueRange.between(0.0F, 5.0F)))
                                      .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F)))))
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .add(ItemLootEntry.lootTableItem(Items.CACTUS_FLOWER)
                              .apply(SetCount.setCount(RandomValueRange.between(0.0F, 3.0F)))
                              .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F))))).build();


      LootTable decoratedPotLootTable = this.createDecoratedPotTable().build();

      builder.put(new ResourceLocation("blocks/decorated_pot"), decoratedPotLootTable);

      LootTable suspiciousGravelOreArchaeologyLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.FLINT).setWeight(1).setCount(RandomValueRange.between(1F, 4F)))
                      .add(LootItem.lootTableItem(Items.FLINT).setWeight(5).setCount(RandomValueRange.between(1F, 2F)))
                      .add(LootItem.lootTableItem(Items.FLINT).setWeight(2).setCount(RandomValueRange.between(2F, 3F)))

              )
              .build();

      LootTable suspiciousGravelOreArchaeologyRareLootTable = LootTable.lootTable()
              .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                      .add(LootItem.lootTableItem(Items.IRON_NUGGET).setWeight(1).setCount(RandomValueRange.between(1F, 4F)))
                      .add(LootItem.lootTableItem(Items.FLINT).setWeight(3).setCount(RandomValueRange.between(2F, 6F)))
                      .add(LootItem.lootTableItem(Items.GOLD_NUGGET).setWeight(2).setCount(RandomValueRange.between(2F, 3F)))
                      .add(LootItem.lootTableItem(Items.PRIZE_POTTERY_SHERD).setWeight(2))

              )
              .build();

      builder.put(LootTables.GRAVEL_ORE_COMMON_ARCHAEOLOGY, suspiciousGravelOreArchaeologyLootTable);
      builder.put(LootTables.GRAVEL_ORE_RARE_ARCHAEOLOGY, suspiciousGravelOreArchaeologyRareLootTable);

      LootPool.Builder poolBuilder = LootPool.lootPool().setRolls(ConstantRange.exactly(1))
              .add(LootItem.lootTableItem(Items.PAPER).setCount(RandomValueRange.between(1F, 3F)));

      for (SimpleWoodFamily family : WoodFamilies.allFamilies()) {

         // Add log block with weight 2 and count 1–4
         family.getLogBlock().ifPresent(log ->
                 poolBuilder.add(LootItem.lootTableItem(log.asItem()).setWeight(2).setCount(RandomValueRange.between(1F, 4F)))
         );

         // Add planks
         family.getPlankBlock().ifPresent(block ->
                 poolBuilder.add(LootItem.lootTableItem(block.asItem()).setWeight(4).setCount(RandomValueRange.between(6F, 12F)))
         );

         // Add doors
         family.getDoorBlock().ifPresent(block ->
                 poolBuilder.add(LootItem.lootTableItem(block.asItem()).setWeight(3).setCount(RandomValueRange.between(2F, 3F)))
         );

         // Add stairs
         family.getStairsBlock().ifPresent(block ->
                 poolBuilder.add(LootItem.lootTableItem(block.asItem()).setWeight(3).setCount(RandomValueRange.between(3F, 8F)))
         );

         // Add fences
         family.getFenceBlock().ifPresent(block ->
                 poolBuilder.add(LootItem.lootTableItem(block.asItem()).setWeight(3).setCount(RandomValueRange.between(6F, 12F)))
         );

         // Add trapdoors
         family.getTrapdoorBlock().ifPresent(block ->
                 poolBuilder.add(LootItem.lootTableItem(block.asItem()).setWeight(3).setCount(RandomValueRange.between(2F, 6F)))
         );

         // Add slabs
         family.getSlabBlock().ifPresent(block ->
                 poolBuilder.add(LootItem.lootTableItem(block.asItem()).setWeight(3).setCount(RandomValueRange.between(3F, 6F)))
         );
      }

      LootTable carpenterGiftLootTable = LootTable.lootTable()
              .withPool(poolBuilder)
              .build();

      builder.put(LootTables.CARPENTER_GIFT, carpenterGiftLootTable);





      builder.put(new ResourceLocation("entities/camel"), camelLoot);

      LootTable roadrunnerLoot = LootTable.lootTable()
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .add(ItemLootEntry.lootTableItem(Items.ROADRUNNER_FEATHER)
                              .apply(SetCount.setCount(RandomValueRange.between(0.0F, 2.0F)))
                              .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F))))
                      .add(ItemLootEntry.lootTableItem(Items.FEATHER)
                              .apply(SetCount.setCount(RandomValueRange.between(0.0F, 1.0F)))
                              .apply(LootingEnchantBonus.lootingMultiplier(RandomValueRange.between(0.0F, 1.0F))))).build();

      builder.put(new ResourceLocation("entities/roadrunner"), roadrunnerLoot);

      createSelfDrop(builder, "blocks/copper_bulb", Blocks.COPPER_BULB, Always::new);
      createSelfDrop(builder, "blocks/exposed_copper_bulb", Blocks.EXPOSED_COPPER_BULB, Always::new);
      createSelfDrop(builder, "blocks/weathered_copper_bulb", Blocks.WEATHERED_COPPER_BULB, Always::new);
      createSelfDrop(builder, "blocks/oxidized_copper_bulb", Blocks.OXIDIZED_COPPER_BULB, Always::new);
      createSelfDrop(builder, "blocks/waxed_copper_bulb", Blocks.WAXED_COPPER_BULB, Always::new);
      createSelfDrop(builder, "blocks/waxed_exposed_copper_bulb", Blocks.WAXED_EXPOSED_COPPER_BULB, Always::new);
      createSelfDrop(builder, "blocks/waxed_weathered_copper_bulb", Blocks.WAXED_WEATHERED_COPPER_BULB, Always::new);
      createSelfDrop(builder, "blocks/waxed_oxidized_copper_bulb", Blocks.WAXED_OXIDIZED_COPPER_BULB, Always::new);

      createSelfDrop(builder, "blocks/copper_block", Blocks.COPPER_BLOCK, Always::new);
      createSelfDrop(builder, "blocks/exposed_copper", Blocks.EXPOSED_COPPER, Always::new);
      createSelfDrop(builder, "blocks/weathered_copper", Blocks.WEATHERED_COPPER, Always::new);
      createSelfDrop(builder, "blocks/oxidized_copper", Blocks.OXIDIZED_COPPER, Always::new);

      createSelfDrop(builder, "blocks/cut_copper", Blocks.CUT_COPPER, Always::new);
      createSelfDrop(builder, "blocks/exposed_cut_copper", Blocks.EXPOSED_CUT_COPPER, Always::new);
      createSelfDrop(builder, "blocks/weathered_cut_copper", Blocks.WEATHERED_CUT_COPPER, Always::new);
      createSelfDrop(builder, "blocks/oxidized_cut_copper", Blocks.OXIDIZED_CUT_COPPER, Always::new);

      createSelfDrop(builder, "blocks/copper_grate", Blocks.COPPER_GRATE, Always::new);
      createSelfDrop(builder, "blocks/exposed_copper_grate", Blocks.EXPOSED_COPPER_GRATE, Always::new);
      createSelfDrop(builder, "blocks/weathered_copper_grate", Blocks.WEATHERED_COPPER_GRATE, Always::new);
      createSelfDrop(builder, "blocks/oxidized_copper_grate", Blocks.OXIDIZED_COPPER_GRATE, Always::new);
      createSelfDrop(builder, "blocks/waxed_copper_grate", Blocks.WAXED_COPPER_GRATE, Always::new);
      createSelfDrop(builder, "blocks/waxed_exposed_copper_grate", Blocks.WAXED_EXPOSED_COPPER_GRATE, Always::new);
      createSelfDrop(builder, "blocks/waxed_weathered_copper_grate", Blocks.WAXED_WEATHERED_COPPER_GRATE, Always::new);
      createSelfDrop(builder, "blocks/waxed_oxidized_copper_grate", Blocks.WAXED_OXIDIZED_COPPER_GRATE, Always::new);

      createSelfDrop(builder, "blocks/waxed_copper_block", Blocks.WAXED_COPPER_BLOCK, Always::new);
      createSelfDrop(builder, "blocks/waxed_weathered_copper", Blocks.WAXED_WEATHERED_COPPER, Always::new);
      createSelfDrop(builder, "blocks/waxed_exposed_copper", Blocks.WAXED_EXPOSED_COPPER, Always::new);
      createSelfDrop(builder, "blocks/waxed_oxidized_copper", Blocks.WAXED_OXIDIZED_COPPER, Always::new);
      createSelfDrop(builder, "blocks/waxed_oxidized_cut_copper", Blocks.WAXED_OXIDIZED_CUT_COPPER, Always::new);
      createSelfDrop(builder, "blocks/waxed_weathered_cut_copper", Blocks.WAXED_WEATHERED_CUT_COPPER, Always::new);
      createSelfDrop(builder, "blocks/waxed_exposed_cut_copper", Blocks.WAXED_EXPOSED_CUT_COPPER, Always::new);
      createSelfDrop(builder, "blocks/waxed_cut_copper", Blocks.WAXED_CUT_COPPER, Always::new);

      createSelfDrop(builder, "blocks/oxidized_cut_copper_stairs", Blocks.OXIDIZED_CUT_COPPER_STAIRS, Always::new);
      createSelfDrop(builder, "blocks/weathered_cut_copper_stairs", Blocks.WEATHERED_CUT_COPPER_STAIRS, Always::new);
      createSelfDrop(builder, "blocks/exposed_cut_copper_stairs", Blocks.EXPOSED_CUT_COPPER_STAIRS, Always::new);
      createSelfDrop(builder, "blocks/cut_copper_stairs", Blocks.CUT_COPPER_STAIRS, Always::new);

      createSelfDrop(builder, "blocks/copper_trapdoor", Blocks.COPPER_TRAPDOOR, Always::new);
      createSelfDrop(builder, "blocks/weathered_copper_trapdoor", Blocks.WEATHERED_COPPER_TRAPDOOR, Always::new);
      createSelfDrop(builder, "blocks/exposed_copper_trapdoor", Blocks.EXPOSED_COPPER_TRAPDOOR, Always::new);
      createSelfDrop(builder, "blocks/oxidized_copper_trapdoor", Blocks.OXIDIZED_COPPER_TRAPDOOR, Always::new);

      createSelfDrop(builder, "blocks/copper_button", Blocks.COPPER_BUTTON, Always::new);
      createSelfDrop(builder, "blocks/weathered_copper_button", Blocks.WEATHERED_COPPER_BUTTON, Always::new);
      createSelfDrop(builder, "blocks/exposed_copper_button", Blocks.EXPOSED_COPPER_BUTTON, Always::new);
      createSelfDrop(builder, "blocks/oxidized_copper_button", Blocks.OXIDIZED_COPPER_BUTTON, Always::new);


      createSelfDrop(builder, "blocks/waxed_copper_button", Blocks.WAXED_COPPER_BUTTON, Always::new);
      createSelfDrop(builder, "blocks/waxed_weathered_copper_button", Blocks.WAXED_WEATHERED_COPPER_BUTTON, Always::new);
      createSelfDrop(builder, "blocks/waxed_exposed_copper_button", Blocks.WAXED_EXPOSED_COPPER_BUTTON, Always::new);
      createSelfDrop(builder, "blocks/waxed_oxidized_copper_button", Blocks.WAXED_OXIDIZED_COPPER_BUTTON, Always::new);



      createSelfDrop(builder, "blocks/waxed_copper_trapdoor", Blocks.WAXED_COPPER_TRAPDOOR, Always::new);
      createSelfDrop(builder, "blocks/waxed_weathered_copper_trapdoor", Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, Always::new);
      createSelfDrop(builder, "blocks/waxed_exposed_copper_trapdoor", Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, Always::new);
      createSelfDrop(builder, "blocks/waxed_oxidized_copper_trapdoor", Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, Always::new);


      createSelfDrop(builder, "blocks/waxed_oxidized_cut_copper_stairs", Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Always::new);
      createSelfDrop(builder, "blocks/waxed_weathered_cut_copper_stairs", Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Always::new);
      createSelfDrop(builder, "blocks/waxed_exposed_cut_copper_stairs", Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Always::new);
      createSelfDrop(builder, "blocks/waxed_cut_copper_stairs", Blocks.WAXED_CUT_COPPER_STAIRS, Always::new);

      createSelfDrop(builder, "blocks/oxidized_chiseled_copper", Blocks.OXIDIZED_CHISELED_COPPER, Always::new);
      createSelfDrop(builder, "blocks/weathered_chiseled_copper", Blocks.WEATHERED_CHISELED_COPPER, Always::new);
      createSelfDrop(builder, "blocks/exposed_chiseled_copper", Blocks.EXPOSED_CHISELED_COPPER, Always::new);
      createSelfDrop(builder, "blocks/chiseled_copper", Blocks.CHISELED_COPPER, Always::new);

      createSelfDrop(builder, "blocks/waxed_oxidized_chiseled_copper", Blocks.WAXED_OXIDIZED_CHISELED_COPPER, Always::new);
      createSelfDrop(builder, "blocks/waxed_weathered_chiseled_copper", Blocks.WAXED_WEATHERED_CHISELED_COPPER, Always::new);
      createSelfDrop(builder, "blocks/waxed_exposed_chiseled_copper", Blocks.WAXED_EXPOSED_CHISELED_COPPER, Always::new);
      createSelfDrop(builder, "blocks/waxed_chiseled_copper", Blocks.WAXED_CHISELED_COPPER, Always::new);



      createDoorTable(builder, "blocks/copper_door", Blocks.COPPER_DOOR, Always::new);
      createDoorTable(builder, "blocks/exposed_copper_door", Blocks.EXPOSED_COPPER_DOOR, Always::new);
      createDoorTable(builder, "blocks/oxidized_copper_door", Blocks.OXIDIZED_COPPER_DOOR, Always::new);
      createDoorTable(builder, "blocks/weathered_copper_door", Blocks.WEATHERED_COPPER_DOOR, Always::new);

      createDoorTable(builder, "blocks/waxed_copper_door", Blocks.WAXED_COPPER_DOOR, Always::new);
      createDoorTable(builder, "blocks/waxed_exposed_copper_door", Blocks.WAXED_EXPOSED_COPPER_DOOR, Always::new);
      createDoorTable(builder, "blocks/waxed_oxidized_copper_door", Blocks.WAXED_OXIDIZED_COPPER_DOOR, Always::new);
      createDoorTable(builder, "blocks/waxed_weathered_copper_door", Blocks.WAXED_WEATHERED_COPPER_DOOR, Always::new);

      createSlabItemTable(builder, "blocks/oxidized_cut_copper_slab", Blocks.OXIDIZED_CUT_COPPER_SLAB);
      createSlabItemTable(builder, "blocks/weathered_cut_copper_slab", Blocks.WEATHERED_CUT_COPPER_SLAB);
      createSlabItemTable(builder, "blocks/exposed_cut_copper_slab", Blocks.EXPOSED_CUT_COPPER_SLAB);
      createSlabItemTable(builder, "blocks/cut_copper_slab", Blocks.CUT_COPPER_SLAB);
      createSlabItemTable(builder, "blocks/waxed_oxidized_cut_copper_slab", Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB);
      createSlabItemTable(builder, "blocks/waxed_weathered_cut_copper_slab", Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB);
      createSlabItemTable(builder, "blocks/waxed_exposed_cut_copper_slab", Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB);
      createSlabItemTable(builder, "blocks/waxed_cut_copper_slab", Blocks.WAXED_CUT_COPPER_SLAB);

   }




   private LootTable.Builder createDecoratedPotTable() {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
              .add((DynamicLootEntry.dynamicEntry(DecoratedPotBlock.SHERDS_DYNAMIC_DROP_ID).when(BlockStateProperty.hasBlockStateProperties(Blocks.DECORATED_POT).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DecoratedPotBlock.CRACKED, true)))).otherwise((LootItem.lootTableItem(Blocks.DECORATED_POT).apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY).copy("sherds", "BlockEntityTag.sherds"))))));
   }




   public static void createDoorTable(Builder<ResourceLocation, LootTable> builder, String name, Block block, ILootCondition.IBuilder iBuilder) {
      LootTable.Builder b = createSinglePropConditionTable(block, DoorBlock.HALF, DoubleBlockHalf.LOWER);
      builder.put(new ResourceLocation("minecraft", name), b.build());
   }

   private static <T extends Comparable<T> & IStringSerializable> LootTable.Builder createSinglePropConditionTable(Block p_218562_0_, Property<T> p_218562_1_, T p_218562_2_) {
      return LootTable.lootTable().withPool(applyExplosionCondition(p_218562_0_, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(p_218562_0_).when(BlockStateProperty.hasBlockStateProperties(p_218562_0_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(p_218562_1_, p_218562_2_))))));
   }

   public CompoundNBT convertToEntityTag(ItemStack firework) {
      CompoundNBT entityTag = new CompoundNBT();
      CompoundNBT nbt = new CompoundNBT();
      nbt.put("Firework", firework.save(new CompoundNBT()));

      entityTag.put("EntityTag", nbt);

      return entityTag;
   }

    private void createPetalBlockLootTable(Builder<ResourceLocation, LootTable> builder, String blockID, Block block,Item dropItem) {
        LootTable table = LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantRange.exactly(1))
                        .add(ItemLootEntry.lootTableItem(dropItem)
                                .apply(ExplosionDecay.explosionDecay())
                                .apply(SetCount.setCount(ConstantRange.exactly(1))
                                        .when(BlockStateProperty.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(PetalBlock.AMOUNT, 1))))
                                .apply(SetCount.setCount(ConstantRange.exactly(2))
                                        .when(BlockStateProperty.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(PetalBlock.AMOUNT, 2))))
                                .apply(SetCount.setCount(ConstantRange.exactly(3))
                                        .when(BlockStateProperty.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(PetalBlock.AMOUNT, 3))))
                                .apply(SetCount.setCount(ConstantRange.exactly(4))
                                        .when(BlockStateProperty.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(PetalBlock.AMOUNT, 4))))
                        )
                ).build();

        builder.put(new ResourceLocation("blocks/" + blockID), table);
    }

   private void createCandleBlockLootTable(Builder<ResourceLocation, LootTable> builder, String blockID, CandleBlock block,Item dropItem) {
      LootTable table = LootTable.lootTable()
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .add(ItemLootEntry.lootTableItem(dropItem)
                              .apply(ExplosionDecay.explosionDecay())
                              .apply(SetCount.setCount(ConstantRange.exactly(1))
                                      .when(BlockStateProperty.hasBlockStateProperties(block)
                                              .setProperties(StatePropertiesPredicate.Builder.properties()
                                                      .hasProperty(CandleBlock.CANDLES, 1))))
                              .apply(SetCount.setCount(ConstantRange.exactly(2))
                                      .when(BlockStateProperty.hasBlockStateProperties(block)
                                              .setProperties(StatePropertiesPredicate.Builder.properties()
                                                      .hasProperty(CandleBlock.CANDLES, 2))))
                              .apply(SetCount.setCount(ConstantRange.exactly(3))
                                      .when(BlockStateProperty.hasBlockStateProperties(block)
                                              .setProperties(StatePropertiesPredicate.Builder.properties()
                                                      .hasProperty(CandleBlock.CANDLES, 3))))
                              .apply(SetCount.setCount(ConstantRange.exactly(4))
                                      .when(BlockStateProperty.hasBlockStateProperties(block)
                                              .setProperties(StatePropertiesPredicate.Builder.properties()
                                                      .hasProperty(CandleBlock.CANDLES, 4))))
                      )
              ).build();

      builder.put(new ResourceLocation("blocks/" + blockID), table);
   }

   private static void createSlabItemTable(Builder<ResourceLocation, LootTable> builder, String name, Block block) {
      LootTable t = LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(applyExplosionDecay(block, ItemLootEntry.lootTableItem(block).apply(SetCount.setCount(ConstantRange.exactly(2)).when(BlockStateProperty.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))))))).build();

      builder.put(new ResourceLocation("minecraft", name), t);
   }

   private static <T> T applyExplosionDecay(IItemProvider p_218552_0_, ILootFunctionConsumer<T> p_218552_1_) {
      return (T)(!EXPLOSION_RESISTANT.contains(p_218552_0_.asItem()) ? p_218552_1_.apply(ExplosionDecay.explosionDecay()) : p_218552_1_.unwrap());
   }


   public ItemStack getRocket(SpellcastingIllagerEntity.SpellType type) {
      FireworkUtility.Builder builder = new FireworkUtility.Builder();

      return switch (type) {
         case BLINDNESS -> builder.setFlightDuration(1)
                 .addExplosion(FireworkRocketItem.Shape.BURST,
                         new DyeColor[]{DyeColor.BLACK},
                         new DyeColor[]{DyeColor.GRAY}, true, false)
                 .build();
         case FIREWORK_LAUNCH -> builder.setFlightDuration(1)
                 .addExplosion(FireworkRocketItem.Shape.SMALL_BALL,
                         new DyeColor[]{DyeColor.GREEN, DyeColor.PURPLE},
                         new DyeColor[]{DyeColor.PURPLE}, true, true)
                 .build();
         case FIREWORK_CIRCLE -> builder.setFlightDuration(1)
                 .addExplosion(FireworkRocketItem.Shape.LARGE_BALL,
                         new DyeColor[]{DyeColor.RED, DyeColor.BLACK},
                         new DyeColor[]{DyeColor.RED, DyeColor.BLACK}, true, true)
                 .build();
         default -> builder.setFlightDuration(1)
                 .addExplosion(FireworkRocketItem.Shape.BURST,
                         new DyeColor[]{DyeColor.RED, DyeColor.BLUE},
                         new DyeColor[]{DyeColor.PINK, DyeColor.PURPLE}, false, true)
                 .build();

      };

   }

    private void createMultifaceBlockLootTable(Builder<ResourceLocation, LootTable> builder, String blockID, Block block) {
        LootTable.Builder lootTable = LootTable.lootTable();

        // Loop through all 6 directions and create a separate loot pool for each face
        for (Direction direction : Direction.values()) {
            lootTable.withPool(LootPool.lootPool()
                    .setRolls(ConstantRange.exactly(1))
                    .add(ItemLootEntry.lootTableItem(block)
                            .apply(ExplosionDecay.explosionDecay()) // Apply explosion decay
                            .when(BlockStateProperty.hasBlockStateProperties(block)
                                    .setProperties(StatePropertiesPredicate.Builder.properties()
                                            .hasProperty(MultifaceBlock.getFaceProperty(direction), true)))));
        }

        builder.put(new ResourceLocation("blocks/" + blockID), lootTable.build());
    }






    private static IRandomRange ONE = ConstantRange.exactly(1);

   private void createSelfDrop(Builder<ResourceLocation, LootTable> builder, String name, Block block, ILootCondition.IBuilder iBuilder) { {
      LootTable.Builder lootTable = LootTable.lootTable().withPool(BlockLootTables.applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).when(iBuilder).add(ItemLootEntry.lootTableItem(block))));

      builder.put(new ResourceLocation("minecraft", name), lootTable.build());
   }

   }

   private void createSimpleSelfDrop(Builder<ResourceLocation, LootTable> builder, Block block) { {
      ILootCondition.IBuilder iBuilder = Always::new;
      String name;
      String id = Registry.BLOCK.getKey(block).getPath();
      name = ("blocks/" + id);
      LootTable.Builder lootTable = LootTable.lootTable().withPool(BlockLootTables.applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).when(iBuilder).add(ItemLootEntry.lootTableItem(block))));

      builder.put(new ResourceLocation("minecraft", name), lootTable.build());
   }

   }

   private LootTable.Builder createDeadLeavesLootTable() {
      return LootTable.lootTable()
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                      .add(ItemLootEntry.lootTableItem(Items.LEAF_LITTER).when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE,
                                      0.025f, 0.05f, 0.1f, 0.3f, 0.5f))
                              .apply(SetCount.setCount(RandomValueRange.between(3, 7)))
                      )
              )
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                      .add(ItemLootEntry.lootTableItem(Items.APPLE)
                              .when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE,
                                      0.0025F, 0.003F, 0.0035F, 0.004F, 0.01F))
                      )
              )
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                      .add(ItemLootEntry.lootTableItem(Items.STICK)
                              .apply(SetCount.setCount(RandomValueRange.between(1, 2)))
                              .when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE,
                                      0.05F, 0.055F, 0.06F, 0.065F, 0.08F))
                      )
              )
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                      .add(ItemLootEntry.lootTableItem(Items.DEAD_SAPLING)
                              .when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE,
                                      0.01F, 0.012F, 0.015F, 0.02F, 0.05F))
                      )
              );
   }

   private LootTable.Builder createAppleLeavesLootTable() {
      return LootTable.lootTable()
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                      .add(ItemLootEntry.lootTableItem(Items.APPLE)
                              .apply(SetCount.setCount(RandomValueRange.between(1, 4)))
                              .when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE,
                                      0.5F, 0.8F, 0.9F, 1F, 1F))
                      )
              )
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                      .add(ItemLootEntry.lootTableItem(Items.GOLDEN_APPLE)
                              .apply(SetCount.setCount(RandomValueRange.between(1, 2)))
                              .when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE,
                                      0.005F, 0.008F, 0.009F, 0.01F, 0.015F))
                      )
              )
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                      .add(ItemLootEntry.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE)
                              .apply(SetCount.setCount(ONE))
                              .when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE,
                                      0.002F, 0.005F, 0.007F, 0.009F, 0.012F))
                      )
              )
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                      .add(ItemLootEntry.lootTableItem(Items.STICK)
                              .apply(SetCount.setCount(RandomValueRange.between(1, 2)))
                              .when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE,
                                      0.05F, 0.055F, 0.06F, 0.065F, 0.08F))
                      )
              )
              .withPool(LootPool.lootPool()
                      .setRolls(ConstantRange.exactly(1))
                      .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                      .add(ItemLootEntry.lootTableItem(Items.APPLE_SAPLING)
                              .when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE,
                                      0.007F, 0.01F, 0.014F, 0.02F, 0.05F))
                      )
              );
   }



   public static void validate(ValidationTracker p_227508_0_, ResourceLocation p_227508_1_, LootTable p_227508_2_) {
      p_227508_2_.validate(p_227508_0_.setParams(p_227508_2_.getParamSet()).enterTable("{" + p_227508_1_ + "}", p_227508_1_));
   }

   public static JsonElement serialize(LootTable p_215301_0_) {
      return GSON.toJsonTree(p_215301_0_);
   }

   public static LootTable readLootTableFromFile(String path) {
      try (FileReader reader = new FileReader(path)) {
         JsonParser parser = new JsonParser();
         JsonElement json = parser.parse(reader);
         return GSON.fromJson(json, LootTable.class);
      } catch (Exception e) {
         e.printStackTrace();
         return LootTable.EMPTY;
      }
   }

   public Set<ResourceLocation> getIds() {
      return this.tables.keySet();
   }
}