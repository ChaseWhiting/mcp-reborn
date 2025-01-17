package net.minecraft.util.datafix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.fixes.*;
import net.minecraft.util.datafix.versions.*;

public class DataFixesManager {
   private static final BiFunction<Integer, Schema, Schema> SAME = Schema::new;
   private static final BiFunction<Integer, Schema, Schema> SAME_NAMESPACED = NamespacedSchema::new;
   private static final DataFixer DATA_FIXER = createFixerUpper();

   private static DataFixer createFixerUpper() {
      DataFixerBuilder datafixerbuilder = new DataFixerBuilder(SharedConstants.getCurrentVersion().getWorldVersion());
      addFixers(datafixerbuilder);
      return datafixerbuilder.build(Util.bootstrapExecutor());
   }

   public static DataFixer getDataFixer() {
      return DATA_FIXER;
   }

   private static void addFixers(DataFixerBuilder datafixer) {
      Schema schema = datafixer.addSchema(99, V0099::new);
      Schema schema1 = datafixer.addSchema(100, V0100::new);
      datafixer.addFixer(new EntityArmorAndHeld(schema1, true));
      Schema schema2 = datafixer.addSchema(101, SAME);
      datafixer.addFixer(new SignStrictJSON(schema2, false));
      Schema schema3 = datafixer.addSchema(102, V0102::new);
      datafixer.addFixer(new ItemIntIDToString(schema3, true));
      datafixer.addFixer(new PotionItems(schema3, false));
      Schema schema4 = datafixer.addSchema(105, SAME);
      datafixer.addFixer(new SpawnEggNames(schema4, true));
      Schema schema5 = datafixer.addSchema(106, V0106::new);
      datafixer.addFixer(new SpawnerEntityTypes(schema5, true));
      Schema schema6 = datafixer.addSchema(107, V0107::new);
      datafixer.addFixer(new MinecartEntityTypes(schema6, true));
      Schema schema7 = datafixer.addSchema(108, SAME);
      datafixer.addFixer(new StringToUUID(schema7, true));
      Schema schema8 = datafixer.addSchema(109, SAME);
      datafixer.addFixer(new EntityHealth(schema8, true));
      Schema schema9 = datafixer.addSchema(110, SAME);
      datafixer.addFixer(new HorseSaddle(schema9, true));
      Schema schema10 = datafixer.addSchema(111, SAME);
      datafixer.addFixer(new PaintingDirection(schema10, true));
      Schema schema11 = datafixer.addSchema(113, SAME);
      datafixer.addFixer(new RedundantChanceTags(schema11, true));
      Schema schema12 = datafixer.addSchema(135, V0135::new);
      datafixer.addFixer(new RidingToPassengers(schema12, true));
      Schema schema13 = datafixer.addSchema(143, V0143::new);
      datafixer.addFixer(new TippedArrow(schema13, true));
      Schema schema14 = datafixer.addSchema(147, SAME);
      datafixer.addFixer(new ArmorStandSilent(schema14, true));
      Schema schema15 = datafixer.addSchema(165, SAME);
      datafixer.addFixer(new BookPagesStrictJSON(schema15, true));
      Schema schema16 = datafixer.addSchema(501, V0501::new);
      datafixer.addFixer(new AddNewChoices(schema16, "Add 1.10 entities fix", TypeReferences.ENTITY));
      Schema schema17 = datafixer.addSchema(502, SAME);
      datafixer.addFixer(ItemRename.create(schema17, "cooked_fished item renamer", (p_207111_0_) -> {
         return Objects.equals(NamespacedSchema.ensureNamespaced(p_207111_0_), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : p_207111_0_;
      }));
      datafixer.addFixer(new ZombieProfToType(schema17, false));
      Schema schema18 = datafixer.addSchema(505, SAME);
      datafixer.addFixer(new ForceVBOOn(schema18, false));
      Schema schema19 = datafixer.addSchema(700, V0700::new);
      datafixer.addFixer(new ElderGuardianSplit(schema19, true));
      Schema schema20 = datafixer.addSchema(701, V0701::new);
      datafixer.addFixer(new SkeletonSplit(schema20, true));
      Schema schema21 = datafixer.addSchema(702, V0702::new);
      datafixer.addFixer(new ZombieSplit(schema21, true));
      Schema schema22 = datafixer.addSchema(703, V0703::new);
      datafixer.addFixer(new HorseSplit(schema22, true));
      Schema schema23 = datafixer.addSchema(704, V0704::new);
      datafixer.addFixer(new TileEntityId(schema23, true));
      Schema schema24 = datafixer.addSchema(705, V0705::new);
      datafixer.addFixer(new EntityId(schema24, true));
      Schema schema25 = datafixer.addSchema(804, SAME_NAMESPACED);
      datafixer.addFixer(new BannerItemColor(schema25, true));
      Schema schema26 = datafixer.addSchema(806, SAME_NAMESPACED);
      datafixer.addFixer(new PotionWater(schema26, false));
      Schema schema27 = datafixer.addSchema(808, V0808::new);
      datafixer.addFixer(new AddNewChoices(schema27, "added shulker box", TypeReferences.BLOCK_ENTITY));
      Schema schema28 = datafixer.addSchema(808, 1, SAME_NAMESPACED);
      datafixer.addFixer(new ShulkerBoxEntityColor(schema28, false));
      Schema schema29 = datafixer.addSchema(813, SAME_NAMESPACED);
      datafixer.addFixer(new ShulkerBoxItemColor(schema29, false));
      datafixer.addFixer(new ShulkerBoxTileColor(schema29, false));
      Schema schema30 = datafixer.addSchema(816, SAME_NAMESPACED);
      datafixer.addFixer(new OptionsLowerCaseLanguage(schema30, false));
      Schema schema31 = datafixer.addSchema(820, SAME_NAMESPACED);
      datafixer.addFixer(ItemRename.create(schema31, "totem item renamer", createRenamer("minecraft:totem", "minecraft:totem_of_undying")));
      Schema schema32 = datafixer.addSchema(1022, V1022::new);
      datafixer.addFixer(new WriteAndReadDataFix(schema32, "added shoulder entities to players", TypeReferences.PLAYER));
      Schema schema33 = datafixer.addSchema(1125, V1125::new);
      datafixer.addFixer(new AddBedTileEntity(schema33, true));
      datafixer.addFixer(new BedItemColor(schema33, false));
      Schema schema34 = datafixer.addSchema(1344, SAME_NAMESPACED);
      datafixer.addFixer(new LWJGL3KeyOptions(schema34, false));
      Schema schema35 = datafixer.addSchema(1446, SAME_NAMESPACED);
      datafixer.addFixer(new KeyOptionsTranslation(schema35, false));
      Schema schema36 = datafixer.addSchema(1450, SAME_NAMESPACED);
      datafixer.addFixer(new BlockStateFlattenStructures(schema36, false));
      Schema schema37 = datafixer.addSchema(1451, V1451::new);
      datafixer.addFixer(new AddNewChoices(schema37, "AddTrappedChestFix", TypeReferences.BLOCK_ENTITY));
      Schema schema38 = datafixer.addSchema(1451, 1, V1451_1::new);
      datafixer.addFixer(new ChunkPaletteFormat(schema38, true));
      Schema schema39 = datafixer.addSchema(1451, 2, V1451_2::new);
      datafixer.addFixer(new PistonPushedBlock(schema39, true));
      Schema schema40 = datafixer.addSchema(1451, 3, V1451_3::new);
      datafixer.addFixer(new BlockStateFlatternEntities(schema40, true));
      datafixer.addFixer(new ItemFilledMapMetadata(schema40, false));
      Schema schema41 = datafixer.addSchema(1451, 4, V1451_4::new);
      datafixer.addFixer(new BlockNameFlattening(schema41, true));
      datafixer.addFixer(new ItemStackDataFlattening(schema41, false));
      Schema schema42 = datafixer.addSchema(1451, 5, V1451_5::new);
      datafixer.addFixer(new AddNewChoices(schema42, "RemoveNoteBlockFlowerPotFix", TypeReferences.BLOCK_ENTITY));
      datafixer.addFixer(new ItemSpawnEggSplit(schema42, false));
      datafixer.addFixer(new WolfCollarColor(schema42, false));
      datafixer.addFixer(new BlockEntityBannerColor(schema42, false));
      datafixer.addFixer(new BlockStateFlattenGenOptions(schema42, false));
      Schema schema43 = datafixer.addSchema(1451, 6, V1451_6::new);
      datafixer.addFixer(new StatsRenaming(schema43, true));
      datafixer.addFixer(new JukeboxRecordItem(schema43, false));
      Schema schema44 = datafixer.addSchema(1451, 7, V1451_7::new);
      datafixer.addFixer(new BlockStateFlattenVillageCrops(schema44, true));
      Schema schema45 = datafixer.addSchema(1451, 7, SAME_NAMESPACED);
      datafixer.addFixer(new VillagerTrades(schema45, false));
      Schema schema46 = datafixer.addSchema(1456, SAME_NAMESPACED);
      datafixer.addFixer(new EntityItemFrameFacing(schema46, false));
      Schema schema47 = datafixer.addSchema(1458, SAME_NAMESPACED);
      datafixer.addFixer(new CustomNameStringToComponentEntity(schema47, false));
      datafixer.addFixer(new CustomNameStringToComponentItem(schema47, false));
      datafixer.addFixer(new CustomNameStringToComponentFixTileEntity(schema47, false));
      Schema schema48 = datafixer.addSchema(1460, V1460::new);
      datafixer.addFixer(new PaintingMotive(schema48, false));
      Schema schema49 = datafixer.addSchema(1466, V1466::new);
      datafixer.addFixer(new ChunkGenStatus(schema49, true));
      Schema schema50 = datafixer.addSchema(1470, V1470::new);
      datafixer.addFixer(new AddNewChoices(schema50, "Add 1.13 entities fix", TypeReferences.ENTITY));
      Schema schema51 = datafixer.addSchema(1474, SAME_NAMESPACED);
      datafixer.addFixer(new ColorlessShulkerEntityFix(schema51, false));
      datafixer.addFixer(BlockRename.create(schema51, "Colorless shulker block fixer", (p_207106_0_) -> {
         return Objects.equals(NamespacedSchema.ensureNamespaced(p_207106_0_), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : p_207106_0_;
      }));
      datafixer.addFixer(ItemRename.create(schema51, "Colorless shulker item fixer", (p_207101_0_) -> {
         return Objects.equals(NamespacedSchema.ensureNamespaced(p_207101_0_), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : p_207101_0_;
      }));
      Schema schema52 = datafixer.addSchema(1475, SAME_NAMESPACED);
      datafixer.addFixer(BlockRename.create(schema52, "Flowing fixer", createRenamer(ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava"))));
      Schema schema53 = datafixer.addSchema(1480, SAME_NAMESPACED);
      datafixer.addFixer(BlockRename.create(schema53, "Rename coral blocks", createRenamer(RenamedCoral.RENAMED_IDS)));
      datafixer.addFixer(ItemRename.create(schema53, "Rename coral items", createRenamer(RenamedCoral.RENAMED_IDS)));
      Schema schema54 = datafixer.addSchema(1481, V1481::new);
      datafixer.addFixer(new AddNewChoices(schema54, "Add conduit", TypeReferences.BLOCK_ENTITY));
      Schema schema55 = datafixer.addSchema(1483, V1483::new);
      datafixer.addFixer(new PufferfishRename(schema55, true));
      datafixer.addFixer(ItemRename.create(schema55, "Rename pufferfish egg item", createRenamer(PufferfishRename.RENAMED_IDS)));
      Schema schema56 = datafixer.addSchema(1484, SAME_NAMESPACED);
      datafixer.addFixer(ItemRename.create(schema56, "Rename seagrass items", createRenamer(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))));
      datafixer.addFixer(BlockRename.create(schema56, "Rename seagrass blocks", createRenamer(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))));
      datafixer.addFixer(new HeightmapRenamingFix(schema56, false));
      Schema schema57 = datafixer.addSchema(1486, V1486::new);
      datafixer.addFixer(new EntityCodSalmonFix(schema57, true));
      datafixer.addFixer(ItemRename.create(schema57, "Rename cod/salmon egg items", createRenamer(EntityCodSalmonFix.RENAMED_EGG_IDS)));
      Schema schema58 = datafixer.addSchema(1487, SAME_NAMESPACED);
      datafixer.addFixer(ItemRename.create(schema58, "Rename prismarine_brick(s)_* blocks", createRenamer(ImmutableMap.of("minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"))));
      datafixer.addFixer(BlockRename.create(schema58, "Rename prismarine_brick(s)_* items", createRenamer(ImmutableMap.of("minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"))));
      Schema schema59 = datafixer.addSchema(1488, SAME_NAMESPACED);
      datafixer.addFixer(BlockRename.create(schema59, "Rename kelp/kelptop", createRenamer(ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant"))));
      datafixer.addFixer(ItemRename.create(schema59, "Rename kelptop", createRenamer("minecraft:kelp_top", "minecraft:kelp")));
      datafixer.addFixer(new NamedEntityFix(schema59, false, "Command block block entity custom name fix", TypeReferences.BLOCK_ENTITY, "minecraft:command_block") {
         protected Typed<?> fix(Typed<?> p_207419_1_) {
            return p_207419_1_.update(DSL.remainderFinder(), CustomNameStringToComponentEntity::fixTagCustomName);
         }
      });
      datafixer.addFixer(new NamedEntityFix(schema59, false, "Command block minecart custom name fix", TypeReferences.ENTITY, "minecraft:commandblock_minecart") {
         protected Typed<?> fix(Typed<?> p_207419_1_) {
            return p_207419_1_.update(DSL.remainderFinder(), CustomNameStringToComponentEntity::fixTagCustomName);
         }
      });
      datafixer.addFixer(new IglooMetadataRemoval(schema59, false));
      Schema schema60 = datafixer.addSchema(1490, SAME_NAMESPACED);
      datafixer.addFixer(BlockRename.create(schema60, "Rename melon_block", createRenamer("minecraft:melon_block", "minecraft:melon")));
      datafixer.addFixer(ItemRename.create(schema60, "Rename melon_block/melon/speckled_melon", createRenamer(ImmutableMap.of("minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice"))));
      Schema schema61 = datafixer.addSchema(1492, SAME_NAMESPACED);
      datafixer.addFixer(new ChunkStructuresTemplateRenameFix(schema61, false));
      Schema schema62 = datafixer.addSchema(1494, SAME_NAMESPACED);
      datafixer.addFixer(new ItemStackEnchantmentFix(schema62, false));
      Schema schema63 = datafixer.addSchema(1496, SAME_NAMESPACED);
      datafixer.addFixer(new LeavesFix(schema63, false));
      Schema schema64 = datafixer.addSchema(1500, SAME_NAMESPACED);
      datafixer.addFixer(new BlockEntityKeepPacked(schema64, false));
      Schema schema65 = datafixer.addSchema(1501, SAME_NAMESPACED);
      datafixer.addFixer(new AdvancementRenamer1501(schema65, false));
      Schema schema66 = datafixer.addSchema(1502, SAME_NAMESPACED);
      datafixer.addFixer(new RecipeRenamer1502(schema66, false));
      Schema schema67 = datafixer.addSchema(1506, SAME_NAMESPACED);
      datafixer.addFixer(new LevelDataGeneratorOptionsFix(schema67, false));
      Schema schema68 = datafixer.addSchema(1510, V1510::new);
      datafixer.addFixer(BlockRename.create(schema68, "Block renamening fix", createRenamer(EntityRenaming1510.RENAMED_BLOCKS)));
      datafixer.addFixer(ItemRename.create(schema68, "Item renamening fix", createRenamer(EntityRenaming1510.RENAMED_ITEMS)));
      datafixer.addFixer(new RecipeRenamer1510(schema68, false));
      datafixer.addFixer(new EntityRenaming1510(schema68, true));
      datafixer.addFixer(new SwimStatsRename(schema68, false));
      Schema schema69 = datafixer.addSchema(1514, SAME_NAMESPACED);
      datafixer.addFixer(new ObjectiveDisplayName(schema69, false));
      datafixer.addFixer(new TeamDisplayName(schema69, false));
      datafixer.addFixer(new ObjectiveRenderType(schema69, false));
      Schema schema70 = datafixer.addSchema(1515, SAME_NAMESPACED);
      datafixer.addFixer(BlockRename.create(schema70, "Rename coral fan blocks", createRenamer(CoralFansRenameList.RENAMED_IDS)));
      Schema schema71 = datafixer.addSchema(1624, SAME_NAMESPACED);
      datafixer.addFixer(new TrappedChestTileEntitySplit(schema71, false));
      Schema schema72 = datafixer.addSchema(1800, V1800::new);
      datafixer.addFixer(new AddNewChoices(schema72, "Added 1.14 mobs fix", TypeReferences.ENTITY));
      datafixer.addFixer(ItemRename.create(schema72, "Rename dye items", createRenamer(DyeRenameMap.RENAMED_IDS)));
      Schema schema73 = datafixer.addSchema(1801, V1801::new);
      datafixer.addFixer(new AddNewChoices(schema73, "Added Illager Beast", TypeReferences.ENTITY));
      Schema schema74 = datafixer.addSchema(1802, SAME_NAMESPACED);
      datafixer.addFixer(BlockRename.create(schema74, "Rename sign blocks & stone slabs", createRenamer(ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign", "minecraft:wall_sign", "minecraft:oak_wall_sign"))));
      datafixer.addFixer(ItemRename.create(schema74, "Rename sign item & stone slabs", createRenamer(ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign"))));
      Schema schema75 = datafixer.addSchema(1803, SAME_NAMESPACED);
      datafixer.addFixer(new ItemLoreComponentizeFix(schema75, false));
      Schema schema76 = datafixer.addSchema(1904, V1904::new);
      datafixer.addFixer(new AddNewChoices(schema76, "Added Cats", TypeReferences.ENTITY));
      datafixer.addFixer(new EntityCatSplitFix(schema76, false));
      Schema schema77 = datafixer.addSchema(1905, SAME_NAMESPACED);
      datafixer.addFixer(new ChunkStatusFix(schema77, false));
      Schema schema78 = datafixer.addSchema(1906, V1906::new);
      datafixer.addFixer(new AddNewChoices(schema78, "Add POI Blocks", TypeReferences.BLOCK_ENTITY));
      Schema schema79 = datafixer.addSchema(1909, V1909::new);
      datafixer.addFixer(new AddNewChoices(schema79, "Add jigsaw", TypeReferences.BLOCK_ENTITY));
      Schema schema80 = datafixer.addSchema(1911, SAME_NAMESPACED);
      datafixer.addFixer(new ChunkStatusFix2(schema80, false));
      Schema schema81 = datafixer.addSchema(1917, SAME_NAMESPACED);
      datafixer.addFixer(new CatTypeFix(schema81, false));
      Schema schema82 = datafixer.addSchema(1918, SAME_NAMESPACED);
      datafixer.addFixer(new VillagerProfessionFix(schema82, "minecraft:villager"));
      datafixer.addFixer(new VillagerProfessionFix(schema82, "minecraft:zombie_villager"));
      Schema schema83 = datafixer.addSchema(1920, V1920::new);
      datafixer.addFixer(new NewVillageFix(schema83, false));
      datafixer.addFixer(new AddNewChoices(schema83, "Add campfire", TypeReferences.BLOCK_ENTITY));
      Schema schema84 = datafixer.addSchema(1925, SAME_NAMESPACED);
      datafixer.addFixer(new MapIdFix(schema84, false));
      Schema schema85 = datafixer.addSchema(1928, V1928::new);
      datafixer.addFixer(new EntityRavagerRenameFix(schema85, true));
      datafixer.addFixer(ItemRename.create(schema85, "Rename ravager egg item", createRenamer(EntityRavagerRenameFix.RENAMED_IDS)));
      Schema schema86 = datafixer.addSchema(1929, V1929::new);
      datafixer.addFixer(new AddNewChoices(schema86, "Add Wandering Trader and Trader Llama", TypeReferences.ENTITY));
      Schema schema87 = datafixer.addSchema(1931, V1931::new);
      datafixer.addFixer(new AddNewChoices(schema87, "Added Fox", TypeReferences.ENTITY));
      Schema schema88 = datafixer.addSchema(1936, SAME_NAMESPACED);
      datafixer.addFixer(new OptionsAddTextBackgroundFix(schema88, false));
      Schema schema89 = datafixer.addSchema(1946, SAME_NAMESPACED);
      datafixer.addFixer(new PointOfInterestReorganizationFix(schema89, false));
      Schema schema90 = datafixer.addSchema(1948, SAME_NAMESPACED);
      datafixer.addFixer(new OminousBannerRenameFix(schema90, false));
      Schema schema91 = datafixer.addSchema(1953, SAME_NAMESPACED);
      datafixer.addFixer(new OminousBannerTileEntityRenameFix(schema91, false));
      Schema schema92 = datafixer.addSchema(1955, SAME_NAMESPACED);
      datafixer.addFixer(new VillagerLevelAndXpFix(schema92, false));
      datafixer.addFixer(new ZombieVillagerXpFix(schema92, false));
      Schema schema93 = datafixer.addSchema(1961, SAME_NAMESPACED);
      datafixer.addFixer(new ChunkLightRemoveFix(schema93, false));
      Schema schema94 = datafixer.addSchema(1963, SAME_NAMESPACED);
      datafixer.addFixer(new RemoveGolemGossip(schema94, false));
      Schema schema95 = datafixer.addSchema(2100, V2100::new);
      datafixer.addFixer(new AddNewChoices(schema95, "Added Bee and Bee Stinger", TypeReferences.ENTITY));
      datafixer.addFixer(new AddNewChoices(schema95, "Add beehive", TypeReferences.BLOCK_ENTITY));
      datafixer.addFixer(new RecipeRenamer(schema95, false, "Rename sugar recipe", createRenamer("minecraft:sugar", "sugar_from_sugar_cane")));
      datafixer.addFixer(new AdvancementRenamer(schema95, false, "Rename sugar recipe advancement", createRenamer("minecraft:recipes/misc/sugar", "minecraft:recipes/misc/sugar_from_sugar_cane")));
      Schema schema96 = datafixer.addSchema(2202, SAME_NAMESPACED);
      datafixer.addFixer(new BiomeIdFix(schema96, false));
      Schema schema97 = datafixer.addSchema(2209, SAME_NAMESPACED);
      datafixer.addFixer(ItemRename.create(schema97, "Rename bee_hive item to beehive", createRenamer("minecraft:bee_hive", "minecraft:beehive")));
      datafixer.addFixer(new RenameBeehivePointOfInterest(schema97));
      datafixer.addFixer(BlockRename.create(schema97, "Rename bee_hive block to beehive", createRenamer("minecraft:bee_hive", "minecraft:beehive")));
      Schema schema98 = datafixer.addSchema(2211, SAME_NAMESPACED);
      datafixer.addFixer(new StructureReferenceFix(schema98, false));
      Schema schema99 = datafixer.addSchema(2218, SAME_NAMESPACED);
      datafixer.addFixer(new PointOfInterestRebuild(schema99, false));
      Schema schema100 = datafixer.addSchema(2501, V2501::new);
      datafixer.addFixer(new FurnaceRecipes(schema100, true));
      Schema schema101 = datafixer.addSchema(2502, V2502::new);
      datafixer.addFixer(new AddNewChoices(schema101, "Added Hoglin", TypeReferences.ENTITY));
      Schema schema102 = datafixer.addSchema(2503, SAME_NAMESPACED);
      datafixer.addFixer(new WallProperty(schema102, false));
      datafixer.addFixer(new AdvancementRenamer(schema102, false, "Composter category change", createRenamer("minecraft:recipes/misc/composter", "minecraft:recipes/decorations/composter")));
      Schema schema103 = datafixer.addSchema(2505, V2505::new);
      datafixer.addFixer(new AddNewChoices(schema103, "Added Piglin", TypeReferences.ENTITY));
      datafixer.addFixer(new MemoryExpiry(schema103, "minecraft:villager"));
      Schema schema104 = datafixer.addSchema(2508, SAME_NAMESPACED);
      datafixer.addFixer(ItemRename.create(schema104, "Renamed fungi items to fungus", createRenamer(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))));
      datafixer.addFixer(BlockRename.create(schema104, "Renamed fungi blocks to fungus", createRenamer(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))));
      Schema schema105 = datafixer.addSchema(2509, V2509::new);
      datafixer.addFixer(new ZombifiedPiglinRename(schema105));
      datafixer.addFixer(ItemRename.create(schema105, "Rename zombie pigman egg item", createRenamer(ZombifiedPiglinRename.RENAMED_IDS)));
      Schema schema106 = datafixer.addSchema(2511, SAME_NAMESPACED);
      datafixer.addFixer(new ProjectileOwner(schema106));
      Schema schema107 = datafixer.addSchema(2514, SAME_NAMESPACED);
      datafixer.addFixer(new EntityUUID(schema107));
      datafixer.addFixer(new BlockEntityUUID(schema107));
      datafixer.addFixer(new PlayerUUID(schema107));
      datafixer.addFixer(new LevelUUID(schema107));
      datafixer.addFixer(new SavedDataUUID(schema107));
      datafixer.addFixer(new ItemStackUUID(schema107));
      Schema schema108 = datafixer.addSchema(2516, SAME_NAMESPACED);
      datafixer.addFixer(new Gossip(schema108, "minecraft:villager"));
      datafixer.addFixer(new Gossip(schema108, "minecraft:zombie_villager"));
      Schema schema109 = datafixer.addSchema(2518, SAME_NAMESPACED);
      datafixer.addFixer(new JigsawProperties(schema109, false));
      datafixer.addFixer(new JigsawRotation(schema109, false));
      Schema schema110 = datafixer.addSchema(2519, V2519::new);
      datafixer.addFixer(new AddNewChoices(schema110, "Added Strider", TypeReferences.ENTITY));
      Schema schema111 = datafixer.addSchema(2522, V2522::new);
      datafixer.addFixer(new AddNewChoices(schema111, "Added Zoglin", TypeReferences.ENTITY));
      Schema schema112 = datafixer.addSchema(2523, SAME_NAMESPACED);
      datafixer.addFixer(new AttributesFix(schema112));
      Schema schema113 = datafixer.addSchema(2527, SAME_NAMESPACED);
      datafixer.addFixer(new BitStorageAlignFix(schema113));
      Schema schema114 = datafixer.addSchema(2528, SAME_NAMESPACED);
      datafixer.addFixer(ItemRename.create(schema114, "Rename soul fire torch and soul fire lantern", createRenamer(ImmutableMap.of("minecraft:soul_fire_torch", "minecraft:soul_torch", "minecraft:soul_fire_lantern", "minecraft:soul_lantern"))));
      datafixer.addFixer(BlockRename.create(schema114, "Rename soul fire torch and soul fire lantern", createRenamer(ImmutableMap.of("minecraft:soul_fire_torch", "minecraft:soul_torch", "minecraft:soul_fire_wall_torch", "minecraft:soul_wall_torch", "minecraft:soul_fire_lantern", "minecraft:soul_lantern"))));
      Schema schema115 = datafixer.addSchema(2529, SAME_NAMESPACED);
      datafixer.addFixer(new StriderGravity(schema115, false));
      Schema schema116 = datafixer.addSchema(2531, SAME_NAMESPACED);
      datafixer.addFixer(new RedstoneConnections(schema116));
      Schema schema117 = datafixer.addSchema(2533, SAME_NAMESPACED);
      datafixer.addFixer(new VillagerFollowRange(schema117));
      Schema schema118 = datafixer.addSchema(2535, SAME_NAMESPACED);
      datafixer.addFixer(new ShulkerRotation(schema118));
      Schema schema119 = datafixer.addSchema(2550, SAME_NAMESPACED);
      datafixer.addFixer(new WorldGenSettings(schema119));
      Schema schema120 = datafixer.addSchema(2551, V2551::new);
      datafixer.addFixer(new WriteAndReadDataFix(schema120, "add types to WorldGenData", TypeReferences.WORLD_GEN_SETTINGS));
      Schema schema121 = datafixer.addSchema(2552, SAME_NAMESPACED);
      datafixer.addFixer(new BiomeName(schema121, false, "Nether biome rename", ImmutableMap.of("minecraft:nether", "minecraft:nether_wastes")));
      Schema schema122 = datafixer.addSchema(2553, SAME_NAMESPACED);
      datafixer.addFixer(new BiomeRenames(schema122, false));
      Schema schema123 = datafixer.addSchema(2558, SAME_NAMESPACED);
      datafixer.addFixer(new MissingDimensionFix(schema123, false));
      datafixer.addFixer(new SwapHandsFix(schema123, false, "Rename swapHands setting", "key_key.swapHands", "key_key.swapOffhand"));
      Schema schema124 = datafixer.addSchema(2568, V2568::new);
      datafixer.addFixer(new AddNewChoices(schema124, "Added Piglin Brute", TypeReferences.ENTITY));
      Schema schema126 = datafixer.addSchema(2570, V2570::new);
      datafixer.addFixer(new AddOwlDataFix(schema126, false));
      datafixer.addFixer(new AddNewChoices(schema126, "Added Owl", TypeReferences.ENTITY));
      Schema schema127 = datafixer.addSchema(2571, V2571::new);
      datafixer.addFixer(new AddNewChoices(schema127, "Added new mobs", TypeReferences.ENTITY));
   }

   private static UnaryOperator<String> createRenamer(Map<String, String> p_241301_0_) {
      return (p_241302_1_) -> {
         return p_241301_0_.getOrDefault(p_241302_1_, p_241302_1_);
      };
   }

   private static UnaryOperator<String> createRenamer(String p_241299_0_, String p_241299_1_) {
      return (p_241300_2_) -> {
         return Objects.equals(p_241300_2_, p_241299_0_) ? p_241299_1_ : p_241300_2_;
      };
   }
}