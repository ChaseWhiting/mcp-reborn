package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class V0099 extends Schema {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<String, String> ITEM_TO_BLOCKENTITY = DataFixUtils.make(Maps.newHashMap(), (map) -> {
      map.put("minecraft:furnace", "Furnace");
      map.put("minecraft:lit_furnace", "Furnace");
      map.put("minecraft:chest", "Chest");
      map.put("minecraft:trapped_chest", "Chest");
      map.put("minecraft:ender_chest", "EnderChest");
      map.put("minecraft:jukebox", "RecordPlayer");
      map.put("minecraft:dispenser", "Trap");
      map.put("minecraft:dropper", "Dropper");
      map.put("minecraft:sign", "Sign");
      map.put("minecraft:mob_spawner", "MobSpawner");
      map.put("minecraft:noteblock", "Music");
      map.put("minecraft:brewing_stand", "Cauldron");
      map.put("minecraft:enhanting_table", "EnchantTable");
      map.put("minecraft:command_block", "CommandBlock");
      map.put("minecraft:beacon", "Beacon");
      map.put("minecraft:skull", "Skull");
      map.put("minecraft:daylight_detector", "DLDetector");
      map.put("minecraft:hopper", "Hopper");
      map.put("minecraft:banner", "Banner");
      map.put("minecraft:flower_pot", "FlowerPot");
      map.put("minecraft:repeating_command_block", "CommandBlock");
      map.put("minecraft:chain_command_block", "CommandBlock");
      map.put("minecraft:standing_sign", "Sign");
      map.put("minecraft:wall_sign", "Sign");
      map.put("minecraft:piston_head", "Piston");
      map.put("minecraft:daylight_detector_inverted", "DLDetector");
      map.put("minecraft:unpowered_comparator", "Comparator");
      map.put("minecraft:powered_comparator", "Comparator");
      map.put("minecraft:wall_banner", "Banner");
      map.put("minecraft:standing_banner", "Banner");
      map.put("minecraft:structure_block", "Structure");
      map.put("minecraft:end_portal", "Airportal");
      map.put("minecraft:end_gateway", "EndGateway");
      map.put("minecraft:shield", "Banner");
   });
   protected static final HookFunction ADD_NAMES = new HookFunction() {
      public <T> T apply(DynamicOps<T> p_apply_1_, T p_apply_2_) {
         return V0099.addNames(new Dynamic<>(p_apply_1_, p_apply_2_), V0099.ITEM_TO_BLOCKENTITY, "ArmorStand");
      }
   };

   public V0099(int p_i49580_1_, Schema p_i49580_2_) {
      super(p_i49580_1_, p_i49580_2_);
   }

   protected static TypeTemplate equipment(Schema p_206658_0_) {
      return DSL.optionalFields("Equipment", DSL.list(TypeReferences.ITEM_STACK.in(p_206658_0_)));
   }

   protected static void registerMob(Schema p_206690_0_, Map<String, Supplier<TypeTemplate>> p_206690_1_, String p_206690_2_) {
      p_206690_0_.register(p_206690_1_, p_206690_2_, () -> {
         return equipment(p_206690_0_);
      });
   }

   protected static void registerThrowableProjectile(Schema p_206668_0_, Map<String, Supplier<TypeTemplate>> p_206668_1_, String p_206668_2_) {
      p_206668_0_.register(p_206668_1_, p_206668_2_, () -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_206668_0_));
      });
   }

   protected static void registerMinecart(Schema p_206674_0_, Map<String, Supplier<TypeTemplate>> p_206674_1_, String p_206674_2_) {
      p_206674_0_.register(p_206674_1_, p_206674_2_, () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_206674_0_));
      });
   }

   protected static void registerInventory(Schema p_206680_0_, Map<String, Supplier<TypeTemplate>> p_206680_1_, String p_206680_2_) {
      p_206680_0_.register(p_206680_1_, p_206680_2_, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_206680_0_)));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      p_registerEntities_1_.register(map, "Item", (p_206678_1_) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "XPOrb");
      registerThrowableProjectile(p_registerEntities_1_, map, "ThrownEgg");
      p_registerEntities_1_.registerSimple(map, "LeashKnot");
      p_registerEntities_1_.registerSimple(map, "Painting");
      p_registerEntities_1_.register(map, "Arrow", (p_206682_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "TippedArrow", (p_206655_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "SpectralArrow", (p_206671_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerThrowableProjectile(p_registerEntities_1_, map, "Snowball");
      registerThrowableProjectile(p_registerEntities_1_, map, "Fireball");
      registerThrowableProjectile(p_registerEntities_1_, map, "SmallFireball");
      registerThrowableProjectile(p_registerEntities_1_, map, "ThrownEnderpearl");
      p_registerEntities_1_.registerSimple(map, "EyeOfEnderSignal");
      p_registerEntities_1_.register(map, "ThrownPotion", (p_206688_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Potion", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      registerThrowableProjectile(p_registerEntities_1_, map, "ThrownExpBottle");
      p_registerEntities_1_.register(map, "ItemFrame", (p_206661_1_) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      registerThrowableProjectile(p_registerEntities_1_, map, "WitherSkull");
      p_registerEntities_1_.registerSimple(map, "PrimedTnt");
      p_registerEntities_1_.register(map, "FallingSand", (p_206679_1_) -> {
         return DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "FireworksRocketEntity", (p_206651_1_) -> {
         return DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "Boat");
      p_registerEntities_1_.register(map, "Minecart", () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      registerMinecart(p_registerEntities_1_, map, "MinecartRideable");
      p_registerEntities_1_.register(map, "MinecartChest", (p_206663_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      registerMinecart(p_registerEntities_1_, map, "MinecartFurnace");
      registerMinecart(p_registerEntities_1_, map, "MinecartTNT");
      p_registerEntities_1_.register(map, "MinecartSpawner", () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), TypeReferences.UNTAGGED_SPAWNER.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(map, "MinecartHopper", (p_210752_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      registerMinecart(p_registerEntities_1_, map, "MinecartCommandBlock");
      registerMob(p_registerEntities_1_, map, "ArmorStand");
      registerMob(p_registerEntities_1_, map, "Creeper");
      registerMob(p_registerEntities_1_, map, "Skeleton");
      registerMob(p_registerEntities_1_, map, "Spider");
      registerMob(p_registerEntities_1_, map, "Giant");
      registerMob(p_registerEntities_1_, map, "Zombie");
      registerMob(p_registerEntities_1_, map, "Slime");
      registerMob(p_registerEntities_1_, map, "Ghast");
      registerMob(p_registerEntities_1_, map, "PigZombie");
      p_registerEntities_1_.register(map, "Enderman", (p_206686_1_) -> {
         return DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), equipment(p_registerEntities_1_));
      });
      registerMob(p_registerEntities_1_, map, "CaveSpider");
      registerMob(p_registerEntities_1_, map, "Silverfish");
      registerMob(p_registerEntities_1_, map, "Blaze");
      registerMob(p_registerEntities_1_, map, "LavaSlime");
      registerMob(p_registerEntities_1_, map, "EnderDragon");
      registerMob(p_registerEntities_1_, map, "WitherBoss");
      registerMob(p_registerEntities_1_, map, "Bat");
      registerMob(p_registerEntities_1_, map, "Witch");
      registerMob(p_registerEntities_1_, map, "Endermite");
      registerMob(p_registerEntities_1_, map, "Guardian");
      registerMob(p_registerEntities_1_, map, "Pig");
      registerMob(p_registerEntities_1_, map, "Sheep");
      registerMob(p_registerEntities_1_, map, "Cow");
      registerMob(p_registerEntities_1_, map, "Chicken");
      registerMob(p_registerEntities_1_, map, "Squid");
      registerMob(p_registerEntities_1_, map, "Wolf");
      registerMob(p_registerEntities_1_, map, "MushroomCow");
      registerMob(p_registerEntities_1_, map, "SnowMan");
      registerMob(p_registerEntities_1_, map, "Ozelot");
      registerMob(p_registerEntities_1_, map, "VillagerGolem");
      p_registerEntities_1_.register(map, "EntityHorse", (p_206670_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), equipment(p_registerEntities_1_));
      });
      registerMob(p_registerEntities_1_, map, "Rabbit");
      p_registerEntities_1_.register(map, "Villager", (p_206656_1_) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), equipment(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(map, "EnderCrystal");
      p_registerEntities_1_.registerSimple(map, "AreaEffectCloud");
      p_registerEntities_1_.registerSimple(map, "ShulkerBullet");
      registerMob(p_registerEntities_1_, map, "Shulker");
      return map;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      registerInventory(p_registerBlockEntities_1_, map, "Furnace");
      registerInventory(p_registerBlockEntities_1_, map, "Chest");
      p_registerBlockEntities_1_.registerSimple(map, "EnderChest");
      p_registerBlockEntities_1_.register(map, "RecordPlayer", (p_206684_1_) -> {
         return DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(p_registerBlockEntities_1_));
      });
      registerInventory(p_registerBlockEntities_1_, map, "Trap");
      registerInventory(p_registerBlockEntities_1_, map, "Dropper");
      p_registerBlockEntities_1_.registerSimple(map, "Sign");
      p_registerBlockEntities_1_.register(map, "MobSpawner", (p_206667_1_) -> {
         return TypeReferences.UNTAGGED_SPAWNER.in(p_registerBlockEntities_1_);
      });
      p_registerBlockEntities_1_.registerSimple(map, "Music");
      p_registerBlockEntities_1_.registerSimple(map, "Piston");
      registerInventory(p_registerBlockEntities_1_, map, "Cauldron");
      p_registerBlockEntities_1_.registerSimple(map, "EnchantTable");
      p_registerBlockEntities_1_.registerSimple(map, "Airportal");
      p_registerBlockEntities_1_.registerSimple(map, "Control");
      p_registerBlockEntities_1_.registerSimple(map, "Beacon");
      p_registerBlockEntities_1_.registerSimple(map, "Skull");
      p_registerBlockEntities_1_.registerSimple(map, "DLDetector");
      registerInventory(p_registerBlockEntities_1_, map, "Hopper");
      p_registerBlockEntities_1_.registerSimple(map, "Comparator");
      p_registerBlockEntities_1_.register(map, "FlowerPot", (p_206653_1_) -> {
         return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(p_registerBlockEntities_1_)));
      });
      p_registerBlockEntities_1_.registerSimple(map, "Banner");
      p_registerBlockEntities_1_.registerSimple(map, "Structure");
      p_registerBlockEntities_1_.registerSimple(map, "EndGateway");
      return map;
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      p_registerTypes_1_.registerType(false, TypeReferences.LEVEL, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.PLAYER, () -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(p_registerTypes_1_)));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(p_registerTypes_1_)), "TileEntities", DSL.list(TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_)), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))));
      });
      p_registerTypes_1_.registerType(true, TypeReferences.BLOCK_ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), p_registerTypes_3_);
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ENTITY_TREE, () -> {
         return DSL.optionalFields("Riding", TypeReferences.ENTITY_TREE.in(p_registerTypes_1_), TypeReferences.ENTITY.in(p_registerTypes_1_));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.ENTITY_NAME, () -> {
         return DSL.constType(NamespacedSchema.namespacedString());
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), p_registerTypes_2_);
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(p_registerTypes_1_)), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TREE.in(p_registerTypes_1_), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))), ADD_NAMES, HookFunction.IDENTITY);
      });
      p_registerTypes_1_.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_NAME, () -> {
         return DSL.or(DSL.constType(DSL.intType()), DSL.constType(NamespacedSchema.namespacedString()));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.ITEM_NAME, () -> {
         return DSL.constType(NamespacedSchema.namespacedString());
      });
      p_registerTypes_1_.registerType(false, TypeReferences.STATS, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.SAVED_DATA, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(p_registerTypes_1_)), "Objectives", DSL.list(TypeReferences.OBJECTIVE.in(p_registerTypes_1_)), "Teams", DSL.list(TypeReferences.TEAM.in(p_registerTypes_1_))));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE_FEATURE, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.TEAM, DSL::remainder);
      p_registerTypes_1_.registerType(true, TypeReferences.UNTAGGED_SPAWNER, DSL::remainder);
      p_registerTypes_1_.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);
      p_registerTypes_1_.registerType(true, TypeReferences.WORLD_GEN_SETTINGS, DSL::remainder);
   }

   protected static <T> T addNames(Dynamic<T> p_209869_0_, Map<String, String> p_209869_1_, String p_209869_2_) {
      return p_209869_0_.update("tag", (p_209868_3_) -> {
         return p_209868_3_.update("BlockEntityTag", (p_209870_2_) -> {
            String s = p_209869_0_.get("id").asString("");
            String s1 = p_209869_1_.get(NamespacedSchema.ensureNamespaced(s));
            if (s1 == null) {
               LOGGER.warn("Unable to resolve BlockEntity for ItemStack: {}", (Object)s);
               return p_209870_2_;
            } else {
               return p_209870_2_.set("id", p_209869_0_.createString(s1));
            }
         }).update("EntityTag", (p_209866_2_) -> {
            String s = p_209869_0_.get("id").asString("");
            return Objects.equals(NamespacedSchema.ensureNamespaced(s), "minecraft:armor_stand") ? p_209866_2_.set("id", p_209869_0_.createString(p_209869_2_)) : p_209866_2_;
         });
      }).getValue();
   }
}