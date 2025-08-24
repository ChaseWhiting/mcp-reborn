package net.minecraft.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.advancements.criterion.*;
import net.minecraft.util.ResourceLocation;

public class CriteriaTriggers {
   private static final Map<ResourceLocation, ICriterionTrigger<?>> CRITERIA = Maps.newHashMap();
   public static final ImpossibleTrigger IMPOSSIBLE = register(new ImpossibleTrigger());
   public static final KilledTrigger PLAYER_KILLED_ENTITY = register(new KilledTrigger(new ResourceLocation("player_killed_entity")));
   public static final KilledTrigger ENTITY_KILLED_PLAYER = register(new KilledTrigger(new ResourceLocation("entity_killed_player")));
   public static final EnterBlockTrigger ENTER_BLOCK = register(new EnterBlockTrigger());
   public static final InventoryChangeTrigger INVENTORY_CHANGED = register(new InventoryChangeTrigger());
   public static final RecipeUnlockedTrigger RECIPE_UNLOCKED = register(new RecipeUnlockedTrigger());
   public static final PlayerHurtEntityTrigger PLAYER_HURT_ENTITY = register(new PlayerHurtEntityTrigger());
   public static final EntityHurtPlayerTrigger ENTITY_HURT_PLAYER = register(new EntityHurtPlayerTrigger());
   public static final EnchantedItemTrigger ENCHANTED_ITEM = register(new EnchantedItemTrigger());
   public static final FilledBucketTrigger FILLED_BUCKET = register(new FilledBucketTrigger());
   public static final BrewedPotionTrigger BREWED_POTION = register(new BrewedPotionTrigger());
   public static final ConstructBeaconTrigger CONSTRUCT_BEACON = register(new ConstructBeaconTrigger());
   public static final UsedEnderEyeTrigger USED_ENDER_EYE = register(new UsedEnderEyeTrigger());
   public static final SummonedEntityTrigger SUMMONED_ENTITY = register(new SummonedEntityTrigger());
   public static final BredAnimalsTrigger BRED_ANIMALS = register(new BredAnimalsTrigger());
   public static final WatchEnderiophageBreedTrigger WATCH_ENDERIOPHAGE_BREED_TRIGGER = register(new WatchEnderiophageBreedTrigger());
   public static final EnderiophagePickUpEye ENDERIOPHAGE_TAKE_EYE = register(new EnderiophagePickUpEye());
   public static final EnderiophageInfectMobs ENDERIOPHAGE_INFECT_MOBS = register(new EnderiophageInfectMobs());
   public static final EnderiophageKilledWhileInfecting ENDERIOPHAGE_KILLED_WHILE_INFECTING = register(new EnderiophageKilledWhileInfecting());
   public static final EncounterEnderiophage ENCOUNTER_ENDERIOPHAGE = register(new EncounterEnderiophage());
   public static final EnderiophageSpawnFromFlu ENDERIOPHAGE_SPAWN_FROM_FLU = register(new EnderiophageSpawnFromFlu());
   public static final EnderiophageKilledWhileBreeding ENDERIOPHAGE_KILLED_WHILE_BREEDING = register(new EnderiophageKilledWhileBreeding());
   public static final EnderiophageStealEye ENDERIOPHAGE_STEAL_EYE = register(new EnderiophageStealEye());
   public static final DissectEnderiophage DISSECT_ENDERIOPHAGE = register(new DissectEnderiophage());
   public static final BrokenCapsid BROKEN_CAPSID = register(new BrokenCapsid());
   public static final DestroyCapsid DESTROY_CAPSID = register(new DestroyCapsid());
   public static final EnderRelocation ENDER_RELOCATION = register(new EnderRelocation());
   public static final EnderiophageAttackBlaze ENDERIOPHAGE_ATTACK_BLAZE = register(new EnderiophageAttackBlaze());

   public static final EnderiophageFailedExperiment FAILED_EXPERIMENT = register(new EnderiophageFailedExperiment());
   public static final EnderiophageRestoration RESTORATION = register(new EnderiophageRestoration());
   public static final BlazePowderFromEnderiophage NATURAL_RESIDUE = register(new BlazePowderFromEnderiophage());


   public static final ChangeCapsidColour CHANGE_CAPSID_COLOUR = register(new ChangeCapsidColour());
   public static final PositionTrigger LOCATION = register(new PositionTrigger(new ResourceLocation("location")));
   public static final PositionTrigger SLEPT_IN_BED = register(new PositionTrigger(new ResourceLocation("slept_in_bed")));
   public static final CuredZombieVillagerTrigger CURED_ZOMBIE_VILLAGER = register(new CuredZombieVillagerTrigger());
   public static final VillagerTradeTrigger TRADE = register(new VillagerTradeTrigger());
   public static final ItemDurabilityTrigger ITEM_DURABILITY_CHANGED = register(new ItemDurabilityTrigger());
   public static final LevitationTrigger LEVITATION = register(new LevitationTrigger());
   public static final ChangeDimensionTrigger CHANGED_DIMENSION = register(new ChangeDimensionTrigger());
   public static final TickTrigger TICK = register(new TickTrigger());
   public static final TameAnimalTrigger TAME_ANIMAL = register(new TameAnimalTrigger());
   public static final PlacedBlockTrigger PLACED_BLOCK = register(new PlacedBlockTrigger());
   public static final ConsumeItemTrigger CONSUME_ITEM = register(new ConsumeItemTrigger());
   public static final EffectsChangedTrigger EFFECTS_CHANGED = register(new EffectsChangedTrigger());
   public static final UsedTotemTrigger USED_TOTEM = register(new UsedTotemTrigger());
   public static final NetherTravelTrigger NETHER_TRAVEL = register(new NetherTravelTrigger());
   public static final FishingRodHookedTrigger FISHING_ROD_HOOKED = register(new FishingRodHookedTrigger());
   public static final ChanneledLightningTrigger CHANNELED_LIGHTNING = register(new ChanneledLightningTrigger());
   public static final ShotCrossbowTrigger SHOT_CROSSBOW = register(new ShotCrossbowTrigger());
   public static final KilledByCrossbowTrigger KILLED_BY_CROSSBOW = register(new KilledByCrossbowTrigger());
   public static final PositionTrigger RAID_WIN = register(new PositionTrigger(new ResourceLocation("hero_of_the_village")));
   public static final PositionTrigger BAD_OMEN = register(new PositionTrigger(new ResourceLocation("voluntary_exile")));
   public static final SlideDownBlockTrigger HONEY_BLOCK_SLIDE = register(new SlideDownBlockTrigger());
   public static final BeeNestDestroyedTrigger BEE_NEST_DESTROYED = register(new BeeNestDestroyedTrigger());
   public static final TargetHitTrigger TARGET_BLOCK_HIT = register(new TargetHitTrigger());
   public static final RightClickBlockWithItemTrigger ITEM_USED_ON_BLOCK = register(new RightClickBlockWithItemTrigger());
   public static final PlayerGeneratesContainerLootTrigger GENERATE_LOOT = register(new PlayerGeneratesContainerLootTrigger());
   public static final ThrownItemPickedUpByEntityTrigger ITEM_PICKED_UP_BY_ENTITY = register(new ThrownItemPickedUpByEntityTrigger());
   public static final PlayerEntityInteractionTrigger PLAYER_INTERACTED_WITH_ENTITY = register(new PlayerEntityInteractionTrigger());

   private static <T extends ICriterionTrigger<?>> T register(T p_192118_0_) {
      if (CRITERIA.containsKey(p_192118_0_.getId())) {
         throw new IllegalArgumentException("Duplicate criterion id " + p_192118_0_.getId());
      } else {
         CRITERIA.put(p_192118_0_.getId(), p_192118_0_);
         return p_192118_0_;
      }
   }

   @Nullable
   public static <T extends ICriterionInstance> ICriterionTrigger<T> getCriterion(ResourceLocation p_192119_0_) {
      return (ICriterionTrigger<T>) CRITERIA.get(p_192119_0_);
   }

   public static Iterable<? extends ICriterionTrigger<?>> all() {
      return CRITERIA.values();
   }
}
