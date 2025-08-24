package net.minecraft.entity.ai.brain.sensor;

import java.util.function.Supplier;

import net.minecraft.entity.axolotl.AxolotlAi;
import net.minecraft.entity.camel.CamelAi;
import net.minecraft.entity.frog.FrogAi;
import net.minecraft.entity.goat.GoatAi;
import net.minecraft.entity.happy_ghast.HappyGhastEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SensorType<U extends Sensor<?>> {
   public static final SensorType<DummySensor> DUMMY = register("dummy", DummySensor::new);
   public static final SensorType<WantedItemsSensor> NEAREST_ITEMS = register("nearest_items", WantedItemsSensor::new);
   public static final SensorType<NearestLivingEntitiesSensor> NEAREST_LIVING_ENTITIES = register("nearest_living_entities", NearestLivingEntitiesSensor::new);
   public static final SensorType<NearestPlayersSensor> NEAREST_PLAYERS = register("nearest_players", NearestPlayersSensor::new);
   public static final SensorType<NearestBedSensor> NEAREST_BED = register("nearest_bed", NearestBedSensor::new);
   public static final SensorType<HurtBySensor> HURT_BY = register("hurt_by", HurtBySensor::new);
   public static final SensorType<VillagerHostilesSensor> VILLAGER_HOSTILES = register("villager_hostiles", VillagerHostilesSensor::new);
   public static final SensorType<VillagerBabiesSensor> VILLAGER_BABIES = register("villager_babies", VillagerBabiesSensor::new);
   public static final SensorType<SecondaryPositionSensor> SECONDARY_POIS = register("secondary_pois", SecondaryPositionSensor::new);
   public static final SensorType<GolemLastSeenSensor> GOLEM_DETECTED = register("golem_detected", GolemLastSeenSensor::new);
   public static final SensorType<PiglinMobsSensor> PIGLIN_SPECIFIC_SENSOR = register("piglin_specific_sensor", PiglinMobsSensor::new);
   public static final SensorType<PiglinBruteSpecificSensor> PIGLIN_BRUTE_SPECIFIC_SENSOR = register("piglin_brute_specific_sensor", PiglinBruteSpecificSensor::new);
   public static final SensorType<BoggedEntitySensor> BOGGED_SENSOR = register("bogged_sensor", BoggedEntitySensor::new);
   public static final SensorType<TemptingSensor> HAPPY_GHAST_TEMPTATIONS = SensorType.register("happy_ghast_temptations", () -> new TemptingSensor(Ingredient.of(Items.SNOWBALL)));

   public static final SensorType<TemptingSensor> GOAT_TEMPTATIONS = SensorType.register("goat_temptations", () -> new TemptingSensor(GoatAi.getTemptations()));
   public static final SensorType<TemptingSensor> AXOLOTL_TEMPTATIONS = SensorType.register("axolotl_temptations", () -> new TemptingSensor(AxolotlAi.getTemptations()));
   public static final SensorType<TemptingSensor> FROG_TEMPTATIONS = SensorType.register("frog_temptations", () -> new TemptingSensor(FrogAi.getTemptations()));
   public static final SensorType<TemptingSensor> CAMEL_TEMPTATIONS = SensorType.register("camel_temptations", () -> new TemptingSensor(CamelAi.getTemptations()));
   public static final SensorType<AdultSensor> NEAREST_ADULT_ANY_TYPE = SensorType.register("nearest_adult_any_type", AdultSensorAnyType::new);

   public static final SensorType<IsInWaterSensor> IS_IN_WATER = SensorType.register("is_in_water", IsInWaterSensor::new);
   public static final SensorType<FrogAttackablesSensor> FROG_ATTACKABLES = SensorType.register("frog_attackables", FrogAttackablesSensor::new);

   public static final SensorType<HoglinMobsSensor> HOGLIN_SPECIFIC_SENSOR = register("hoglin_specific_sensor", HoglinMobsSensor::new);
   public static final SensorType<MateSensor> NEAREST_ADULT = register("nearest_adult", MateSensor::new);
   public static final SensorType<SameEntitySensor> NEAREST_SAME_MOB = register("nearest_same_mob", SameEntitySensor::new);
   public static final SensorType<AxolotlAttackablesSensor> AXOLOTL_ATTACKABLES = register("axolotl_attackables", AxolotlAttackablesSensor::new);
   public static final SensorType<WardenEntitySensor> WARDEN_ENTITY_SENSOR = SensorType.register("warden_entity_sensor", WardenEntitySensor::new);


   public static final SensorType<PlayerSensor> NEAREST_PLAYERS_SENSOR = SensorType.register("nearest_players_sensor", PlayerSensor::new);


   private final Supplier<U> factory;

   private SensorType(Supplier<U> p_i51500_1_) {
      this.factory = p_i51500_1_;
   }

   public U create() {
      return this.factory.get();
   }

   private static <U extends Sensor<?>> SensorType<U> register(String p_220996_0_, Supplier<U> p_220996_1_) {
      return Registry.register(Registry.SENSOR_TYPE, new ResourceLocation(p_220996_0_), new SensorType<>(p_220996_1_));
   }
}