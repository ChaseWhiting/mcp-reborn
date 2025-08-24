package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.*;
import net.minecraft.entity.allay.AllayEntity;
import net.minecraft.entity.axolotl.AxolotlEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.sovereign.InfernalSovereignEntity;
import net.minecraft.entity.camel.CamelEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.frog.FrogEntity;
import net.minecraft.entity.frog.TadpoleEntity;
import net.minecraft.entity.goat.GoatEntity;
import net.minecraft.entity.gumbeeper.GumballEntity;
import net.minecraft.entity.gumbeeper.GumbeeperEntity;
import net.minecraft.entity.happy_ghast.HappyGhastEntity;
import net.minecraft.entity.herobrine.HerobrineEntity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.item.minecart.*;
import net.minecraft.entity.merchant.villager.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.monster.bogged.BoggedEntity;
import net.minecraft.entity.monster.breeze.BreezeEntity;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.entity.monster.creaking.CreakingTransient;
import net.minecraft.entity.monster.crimson_mosquito.CrimsonMosquitoEntity;
import net.minecraft.entity.monster.enderiophage.EntityEnderiophage;
import net.minecraft.entity.monster.piglin.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.fish.*;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.entity.passive.roadrunner.RoadrunnerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowEntity;
import net.minecraft.entity.terraria.boss.eyeofcthulhu.EyeOfCthulhuEntity;
import net.minecraft.entity.terraria.boss.eyeofcthulhu.EyeOfCthulhuSecondFormEntity;
import net.minecraft.entity.terraria.boss.twins.RetinazerEntity;
import net.minecraft.entity.terraria.creature.WormEntity;
import net.minecraft.entity.terraria.monster.demoneye.DemonEyeEntity;
import net.minecraft.entity.warden.WardenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.dagger.DesolateDaggerEntity;
import net.minecraft.nbt.*;
import net.minecraft.pokemon.entity.RattataEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.*;
import net.minecraft.util.*;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.*;
import org.apache.logging.log4j.*;

import static net.minecraft.entity.EntityClassification.CREATURE;

public class EntityType<T extends Entity> {

    private static final Logger LOGGER = LogManager.getLogger();
   public static final EntityType<AllayEntity> ALLAY = register("allay", EntityType.Builder.of(AllayEntity::new, CREATURE).sized(0.35F, 0.6F).clientTrackingRange(8).updateInterval(2));
   public static final EntityType<RoadrunnerEntity> ROADRUNNER = register("roadrunner", EntityType.Builder.of(RoadrunnerEntity::new, CREATURE).sized(0.45F, 0.75F).clientTrackingRange(10));
   public static final EntityType<EntityEnderiophage> ENDERIOPHAGE = register("enderiophage", EntityType.Builder.of(EntityEnderiophage::new, CREATURE).sized(0.85F, 1.95F).clientTrackingRange(8).updateInterval(1));


   public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD = register("area_effect_cloud", EntityType.Builder.<AreaEffectCloudEntity>of(AreaEffectCloudEntity::new, EntityClassification.MISC).fireImmune().sized(6.0F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<ArmorStandEntity> ARMOR_STAND = register("armor_stand", EntityType.Builder.<ArmorStandEntity>of(ArmorStandEntity::new, EntityClassification.MISC).sized(0.5F, 1.975F).clientTrackingRange(10));
   public static final EntityType<BoneArrowEntity> BONE_ARROW = register("bone_arrow", EntityType.Builder.<BoneArrowEntity>of(BoneArrowEntity::new, EntityClassification.MISC).sized(0.58F, 0.5F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<CustomArrowEntity> CUSTOM_ARROW = register("custom_arrow", EntityType.Builder.<CustomArrowEntity>of(CustomArrowEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<ArrowEntity> ARROW = register("arrow", EntityType.Builder.<ArrowEntity>of(ArrowEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<BatEntity> BAT = register("bat", EntityType.Builder.<BatEntity>of(BatEntity::new, EntityClassification.AMBIENT).sized(0.5F, 0.95F).clientTrackingRange(5));
   public static final EntityType<PaleGardenBatEntity> PALE_GARDEN_BAT = register("pale_garden_bat", EntityType.Builder.<PaleGardenBatEntity>of(PaleGardenBatEntity::new, EntityClassification.MONSTER).sized(0.5F, 0.9F).clientTrackingRange(5));

   public static final EntityType<CrimsonMosquitoEntity> CRIMSON_MOSQUITO = register("crimson_mosquito", EntityType.Builder.<CrimsonMosquitoEntity>of(CrimsonMosquitoEntity::new, EntityClassification.MONSTER).sized(1.25F, 1.15f).fireImmune());

   public static final EntityType<OwlEntity> OWL = register("owl", Builder.<OwlEntity>of(OwlEntity::new, CREATURE).sized(0.7F, 0.8F).clientTrackingRange(12));
   public static final EntityType<QueenBeeEntity> QUEEN_BEE = register("queen_bee", EntityType.Builder.<QueenBeeEntity>of(QueenBeeEntity::new, CREATURE).sized(0.9F, 0.79F).clientTrackingRange(10));
   public static final EntityType<FakeEndstoneBlockEntity> ENDSTONE_BLOCK = register("endstone_block", EntityType.Builder.<FakeEndstoneBlockEntity>of(FakeEndstoneBlockEntity::new, EntityClassification.MONSTER).sized(1,1).clientTrackingRange(12));
   public static final EntityType<AxolotlEntity> AXOLOTL = EntityType.register("axolotl", Builder.of(AxolotlEntity::new, EntityClassification.WATER_AMBIENT).sized(0.75f, 0.42f).clientTrackingRange(10));

   public static final EntityType<CamelEntity> CAMEL = EntityType.register("camel", Builder.of(CamelEntity::new, EntityClassification.CREATURE).sized(1.7f, 2.375f).clientTrackingRange(10));
   public static final EntityType<TadpoleEntity> TADPOLE = EntityType.register("tadpole", Builder.of(TadpoleEntity::new, EntityClassification.CREATURE).sized(TadpoleEntity.HITBOX_WIDTH, TadpoleEntity.HITBOX_HEIGHT).clientTrackingRange(10));
   public static final EntityType<WardenEntity> WARDEN = EntityType.register("warden", Builder.of(WardenEntity::new, EntityClassification.MONSTER).sized(0.9f, 2.9f).clientTrackingRange(16).fireImmune());

   public static final EntityType<HappyGhastEntity> HAPPY_GHAST = EntityType.register("happy_ghast", Builder.of(HappyGhastEntity::new, EntityClassification.CREATURE).sized(4.0f, 4.0f).eyeHeight(2.6f).passengerAttachments(new Vector3d(0.0, 4.0, 1.7), new Vector3d(-1.7, 4.0, 0.0), new Vector3d(0.0, 4.0, -1.7), new Vector3d(1.7, 4.0, 0.0)).ridingOffset(0.5f).clientTrackingRange(10));


   public static final EntityType<FrogEntity> FROG = EntityType.register("frog", Builder.of(FrogEntity::new, EntityClassification.CREATURE).sized(0.5f, 0.5f).clientTrackingRange(10));


   public static final EntityType<GoatEntity> GOAT = EntityType.register("goat", Builder.of(GoatEntity::new, EntityClassification.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));

   public static final EntityType<GreatHungerEntity> GREAT_HUNGER = register("great_hunger", EntityType.Builder.<GreatHungerEntity>of(GreatHungerEntity::new, EntityClassification.MONSTER).sized(0.65F,0.62F).clientTrackingRange(12));
   public static final EntityType<CreakingEntity> CREAKING = register("creaking", EntityType.Builder.<CreakingEntity>of(CreakingEntity::new, EntityClassification.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(0.9F,2.7F).clientTrackingRange(12));
   public static final EntityType<CreakingTransient> CREAKING_TRANSIENT = register("creaking_transient", EntityType.Builder.<CreakingTransient>of(CreakingTransient::new, EntityClassification.MONSTER).fireImmune().sized(0.9F,2.7F).clientTrackingRange(12));
   public static final EntityType<InfernalSovereignEntity> INFERNAL_SOVEREIGN = register("infernal_sovereign", EntityType.Builder.<InfernalSovereignEntity>of(InfernalSovereignEntity::new, EntityClassification.MONSTER).fireImmune().sized(0.9F,2.6F).clientTrackingRange(12));


   public static final EntityType<RayTracing> RAY_TRACING = EntityType.register("ray_tracing", Builder.of(RayTracing::new, EntityClassification.AMBIENT).noSave().sized(0.6f, 1.8f).clientTrackingRange(8));

   public static final EntityType<BeeEntity> BEE = register("bee", EntityType.Builder.<BeeEntity>of(BeeEntity::new, CREATURE).sized(0.7F, 0.6F).clientTrackingRange(8));
   public static final EntityType<BlazeEntity> BLAZE = register("blaze", EntityType.Builder.<BlazeEntity>of(BlazeEntity::new, EntityClassification.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(8));
   public static final EntityType<WildfireEntity> WILDFIRE = register("wildfire", EntityType.Builder.<WildfireEntity>of(WildfireEntity::new, EntityClassification.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(8));

   public static final EntityType<BoatEntity> BOAT = register("boat", EntityType.Builder.<BoatEntity>of(BoatEntity::new, EntityClassification.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10));
   public static final EntityType<CatEntity> CAT = register("cat", EntityType.Builder.<CatEntity>of(CatEntity::new, CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8));
   public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = register("cave_spider", EntityType.Builder.<CaveSpiderEntity>of(CaveSpiderEntity::new, EntityClassification.MONSTER).sized(0.7F, 0.5F).clientTrackingRange(8));
   public static final EntityType<ChickenEntity> CHICKEN = register("chicken", EntityType.Builder.<ChickenEntity>of(ChickenEntity::new, CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10));
   public static final EntityType<CodEntity> COD = register("cod", EntityType.Builder.<CodEntity>of(CodEntity::new, EntityClassification.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4));
   public static final EntityType<CowEntity> COW = register("cow", EntityType.Builder.<CowEntity>of(CowEntity::new, CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10));

   public static final EntityType<CopperGolemEntity> COPPER_GOLEM = register("copper_golem", EntityType.Builder.<CopperGolemEntity>of(CopperGolemEntity::new, CREATURE).sized(0.6F, 0.95F).clientTrackingRange(10));


   public static final EntityType<BreezeEntity> BREEZE = EntityType.register("breeze", Builder.of(BreezeEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.77f).clientTrackingRange(10));

   public static final EntityType<CreeperEntity> CREEPER = register("creeper", EntityType.Builder.<CreeperEntity>of(CreeperEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8));
   public static final EntityType<GumbeeperEntity> GUMBEEPER = register("gumbeeper", EntityType.Builder.<GumbeeperEntity>of(GumbeeperEntity::new, EntityClassification.MONSTER).sized(0.8F, 1.6F));


   public static final EntityType<DolphinEntity> DOLPHIN = register("dolphin", EntityType.Builder.<DolphinEntity>of(DolphinEntity::new, EntityClassification.WATER_CREATURE).sized(0.9F, 0.6F));
   public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = register("dragon_fireball", EntityType.Builder.<DragonFireballEntity>of(DragonFireballEntity::new, EntityClassification.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<DrownedEntity> DROWNED = register("drowned", EntityType.Builder.<DrownedEntity>of(DrownedEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = register("elder_guardian", EntityType.Builder.<ElderGuardianEntity>of(ElderGuardianEntity::new, EntityClassification.MONSTER).sized(1.9975F, 1.9975F).clientTrackingRange(10));
   public static final EntityType<EnderCrystalEntity> END_CRYSTAL = register("end_crystal", EntityType.Builder.<EnderCrystalEntity>of(EnderCrystalEntity::new, EntityClassification.MISC).sized(2.0F, 2.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<BlackholeEntity> BLACKHOLE = register("blackhole", EntityType.Builder.<BlackholeEntity>of(BlackholeEntity::new, EntityClassification.MISC).sized(2.0F, 2.0F).clientTrackingRange(16));
   public static final EntityType<EnderDragonEntity> ENDER_DRAGON = register("ender_dragon", EntityType.Builder.<EnderDragonEntity>of(EnderDragonEntity::new, EntityClassification.MONSTER).fireImmune().sized(16.0F, 8.0F).clientTrackingRange(10));
   public static final EntityType<EndermanEntity> ENDERMAN = register("enderman", EntityType.Builder.<EndermanEntity>of(EndermanEntity::new, EntityClassification.MONSTER).sized(0.6F, 2.9F).clientTrackingRange(8));
   public static final EntityType<EndermiteEntity> ENDERMITE = register("endermite", EntityType.Builder.<EndermiteEntity>of(EndermiteEntity::new, EntityClassification.MONSTER).sized(0.4F, 0.3F).clientTrackingRange(8));
   public static final EntityType<ShamanEntity> SHAMAN = register("shaman", EntityType.Builder.<ShamanEntity>of(ShamanEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = register("evoker_fangs", EntityType.Builder.<EvokerFangsEntity>of(EvokerFangsEntity::new, EntityClassification.MISC).sized(0.5F, 0.8F).clientTrackingRange(6).updateInterval(2));
   public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = register("experience_orb", EntityType.Builder.<ExperienceOrbEntity>of(ExperienceOrbEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20));
   public static final EntityType<EyeOfEnderEntity> EYE_OF_ENDER = register("eye_of_ender", EntityType.Builder.<EyeOfEnderEntity>of(EyeOfEnderEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(4));
   public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register("falling_block", EntityType.Builder.<FallingBlockEntity>of(FallingBlockEntity::new, EntityClassification.MISC).sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20));
   public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = register("firework_rocket", EntityType.Builder.<FireworkRocketEntity>of(FireworkRocketEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<FoxEntity> FOX = register("fox", EntityType.Builder.<FoxEntity>of(FoxEntity::new, CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8).immuneTo(Blocks.SWEET_BERRY_BUSH));
   public static final EntityType<RaccoonEntity> RACCOON = register("raccoon", EntityType.Builder.<RaccoonEntity>of(RaccoonEntity::new, CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8).immuneTo(Blocks.SWEET_BERRY_BUSH));
   public static final EntityType<GhastEntity> GHAST = register("ghast", EntityType.Builder.<GhastEntity>of(GhastEntity::new, EntityClassification.MONSTER).fireImmune().sized(4.0F, 4.0F).clientTrackingRange(10));
   public static final EntityType<GiantEntity> GIANT = register("giant", EntityType.Builder.<GiantEntity>of(GiantEntity::new, EntityClassification.MONSTER).sized(3.6F, 12.0F).clientTrackingRange(10));
   public static final EntityType<GuardianEntity> GUARDIAN = register("guardian", EntityType.Builder.<GuardianEntity>of(GuardianEntity::new, EntityClassification.MONSTER).sized(0.85F, 0.85F).clientTrackingRange(8));
   public static final EntityType<HoglinEntity> HOGLIN = register("hoglin", EntityType.Builder.<HoglinEntity>of(HoglinEntity::new, EntityClassification.MONSTER).sized(1.3964844F, 1.4F).clientTrackingRange(8));
   public static final EntityType<HuskEntity> HUSK = register("husk", EntityType.Builder.<HuskEntity>of(HuskEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<IronGolemEntity> IRON_GOLEM = register("iron_golem", EntityType.Builder.<IronGolemEntity>of(IronGolemEntity::new, EntityClassification.MISC).sized(1.4F, 2.7F).clientTrackingRange(10));
   public static final EntityType<ItemEntity> ITEM = register("item", EntityType.Builder.<ItemEntity>of(ItemEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20));
   public static final EntityType<ItemFrameEntity> ITEM_FRAME = register("item_frame", EntityType.Builder.<ItemFrameEntity>of(ItemFrameEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<LeashKnotEntity> LEASH_KNOT = register("leash_knot", EntityType.Builder.<LeashKnotEntity>of(LeashKnotEntity::new, EntityClassification.MISC).noSave().sized(0.375F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<LightningBoltEntity> LIGHTNING_BOLT = register("lightning_bolt", EntityType.Builder.<LightningBoltEntity>of(LightningBoltEntity::new, EntityClassification.MISC).noSave().sized(0.0F, 0.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = register("llama_spit", EntityType.Builder.<LlamaSpitEntity>of(LlamaSpitEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<EntityMosquitoSpit> MOSQUITO_SPIT = register("mosquito_spit", EntityType.Builder.<EntityMosquitoSpit>of(EntityMosquitoSpit::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10).fireImmune());


   public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = register("magma_cube", EntityType.Builder.<MagmaCubeEntity>of(MagmaCubeEntity::new, EntityClassification.MONSTER).fireImmune().sized(2.04F, 2.04F).clientTrackingRange(8));
   public static final EntityType<MinecartEntity> MINECART = register("minecart", EntityType.Builder.<MinecartEntity>of(MinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<ChestMinecartEntity> CHEST_MINECART = register("chest_minecart", EntityType.Builder.<ChestMinecartEntity>of(ChestMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = register("command_block_minecart", EntityType.Builder.<CommandBlockMinecartEntity>of(CommandBlockMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = register("furnace_minecart", EntityType.Builder.<FurnaceMinecartEntity>of(FurnaceMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = register("hopper_minecart", EntityType.Builder.<HopperMinecartEntity>of(HopperMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART = register("spawner_minecart", EntityType.Builder.<SpawnerMinecartEntity>of(SpawnerMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<TNTMinecartEntity> TNT_MINECART = register("tnt_minecart", EntityType.Builder.<TNTMinecartEntity>of(TNTMinecartEntity::new, EntityClassification.MISC).sized(0.98F, 0.7F).clientTrackingRange(8));
   public static final EntityType<MooshroomEntity> MOOSHROOM = register("mooshroom", EntityType.Builder.<MooshroomEntity>of(MooshroomEntity::new, CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10));
   public static final EntityType<OcelotEntity> OCELOT = register("ocelot", EntityType.Builder.<OcelotEntity>of(OcelotEntity::new, CREATURE).sized(0.6F, 0.7F).clientTrackingRange(10));
   public static final EntityType<PaintingEntity> PAINTING = register("painting", EntityType.Builder.<PaintingEntity>of(PaintingEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
   public static final EntityType<PandaEntity> PANDA = register("panda", EntityType.Builder.<PandaEntity>of(PandaEntity::new, CREATURE).sized(1.3F, 1.25F).clientTrackingRange(10));
   public static final EntityType<ParrotEntity> PARROT = register("parrot", EntityType.Builder.<ParrotEntity>of(ParrotEntity::new, CREATURE).sized(0.5F, 0.9F).clientTrackingRange(8));
   public static final EntityType<WoodpeckerEntity> WOODPECKER = register("woodpecker", EntityType.Builder.<WoodpeckerEntity>of(WoodpeckerEntity::new, CREATURE).sized(0.5F, 0.4F).clientTrackingRange(8));


   public static final EntityType<PhantomEntity> PHANTOM = register("phantom", EntityType.Builder.<PhantomEntity>of(PhantomEntity::new, EntityClassification.MONSTER).sized(0.9F, 0.5F).clientTrackingRange(8));
   public static final EntityType<PigEntity> PIG = register("pig", EntityType.Builder.<PigEntity>of(PigEntity::new, CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10));
   public static final EntityType<PiglinEntity> PIGLIN = register("piglin", EntityType.Builder.<PiglinEntity>of(PiglinEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<PiglinBruteEntity> PIGLIN_BRUTE = register("piglin_brute", EntityType.Builder.<PiglinBruteEntity>of(PiglinBruteEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<PolarBearEntity> POLAR_BEAR = register("polar_bear", EntityType.Builder.<PolarBearEntity>of(PolarBearEntity::new, CREATURE).sized(1.4F, 1.4F).clientTrackingRange(10));
   public static final EntityType<TNTEntity> TNT = register("tnt", EntityType.Builder.<TNTEntity>of(TNTEntity::new, EntityClassification.MISC).fireImmune().sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10));
   public static final EntityType<PufferfishEntity> PUFFERFISH = register("pufferfish", EntityType.Builder.<PufferfishEntity>of(PufferfishEntity::new, EntityClassification.WATER_AMBIENT).sized(0.7F, 0.7F).clientTrackingRange(4));
   public static final EntityType<RabbitEntity> RABBIT = register("rabbit", EntityType.Builder.<RabbitEntity>of(RabbitEntity::new, CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8));
   public static final EntityType<SalmonEntity> SALMON = register("salmon", EntityType.Builder.<SalmonEntity>of(SalmonEntity::new, EntityClassification.WATER_AMBIENT).sized(0.7F, 0.4F).clientTrackingRange(4));

   public static final EntityType<RokfiskEntity> ROKFISK = register("rokfisk", EntityType.Builder.<RokfiskEntity>of(RokfiskEntity::new, EntityClassification.AMBIENT).sized(0.9F, 0.6F).clientTrackingRange(12).fireImmune().immuneTo(Blocks.LAVA));


   public static final EntityType<SheepEntity> SHEEP = register("sheep", EntityType.Builder.<SheepEntity>of(SheepEntity::new, CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10));
   public static final EntityType<ShulkerEntity> SHULKER = register("shulker", EntityType.Builder.<ShulkerEntity>of(ShulkerEntity::new, EntityClassification.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(1.0F, 1.0F).clientTrackingRange(10));
   public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = register("shulker_bullet", EntityType.Builder.<ShulkerBulletEntity>of(ShulkerBulletEntity::new, EntityClassification.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(8));
   public static final EntityType<SilverfishEntity> SILVERFISH = register("silverfish", EntityType.Builder.<SilverfishEntity>of(SilverfishEntity::new, EntityClassification.MONSTER).sized(0.4F, 0.3F).clientTrackingRange(8));
   public static final EntityType<SlimeEntity> SLIME = register("slime", EntityType.Builder.<SlimeEntity>of(SlimeEntity::new, EntityClassification.MONSTER).sized(2.04F, 2.04F).clientTrackingRange(10));
   public static final EntityType<SnowGolemEntity> SNOW_GOLEM = register("snow_golem", EntityType.Builder.<SnowGolemEntity>of(SnowGolemEntity::new, EntityClassification.MISC).sized(0.7F, 1.9F).clientTrackingRange(8));
   public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = register("spectral_arrow", EntityType.Builder.<SpectralArrowEntity>of(SpectralArrowEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<SpiderEntity> SPIDER = register("spider", EntityType.Builder.<SpiderEntity>of(SpiderEntity::new, EntityClassification.MONSTER).sized(1.4F, 0.9F).clientTrackingRange(8));
   public static final EntityType<SquidEntity> SQUID = register("squid", EntityType.Builder.<SquidEntity>of(SquidEntity::new, EntityClassification.WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(8));
   public static final EntityType<StrayEntity> STRAY = register("stray", EntityType.Builder.<StrayEntity>of(StrayEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8));
   public static final EntityType<StriderEntity> STRIDER = register("strider", EntityType.Builder.<StriderEntity>of(StriderEntity::new, CREATURE).fireImmune().sized(0.9F, 1.7F).clientTrackingRange(10));
   public static final EntityType<FrisbeeEntity> FRISBEE = register("frisbee", EntityType.Builder.<FrisbeeEntity>of(FrisbeeEntity::new, EntityClassification.MISC).sized(0.6F, 0.6F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<TropicalFishEntity> TROPICAL_FISH = register("tropical_fish", EntityType.Builder.<TropicalFishEntity>of(TropicalFishEntity::new, EntityClassification.WATER_AMBIENT).sized(0.5F, 0.4F).clientTrackingRange(4));
   public static final EntityType<TurtleEntity> TURTLE = register("turtle", EntityType.Builder.<TurtleEntity>of(TurtleEntity::new, CREATURE).sized(1.2F, 0.4F).clientTrackingRange(10));
   public static final EntityType<VexEntity> VEX = register("vex", EntityType.Builder.<VexEntity>of(VexEntity::new, EntityClassification.MONSTER).fireImmune().sized(0.4F, 0.8F).clientTrackingRange(8));
   public static final EntityType<WitchEntity> WITCH = register("witch", EntityType.Builder.<WitchEntity>of(WitchEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<WitherEntity> WITHER = register("wither", EntityType.Builder.<WitherEntity>of(WitherEntity::new, EntityClassification.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.9F, 3.5F).clientTrackingRange(10));
   public static final EntityType<WitherSkullEntity> WITHER_SKULL = register("wither_skull", EntityType.Builder.<WitherSkullEntity>of(WitherSkullEntity::new, EntityClassification.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<WolfEntity> WOLF = register("wolf", EntityType.Builder.<WolfEntity>of(WolfEntity::new, CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));

   public static final EntityType<ColetteEntity> COLETTE = register("colette", EntityType.Builder.<ColetteEntity>of(ColetteEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));

   public static final EntityType<PlayerEntity> PLAYER = register("player", EntityType.Builder.<PlayerEntity>createNothing(EntityClassification.MISC).noSave().noSummon().sized(0.6F, 1.8F).clientTrackingRange(32).eyeHeight(1.62f).vehicleAttachment(PlayerEntity.DEFAULT_VEHICLE_ATTACHMENT).updateInterval(2));
   public static final EntityType<FishingBobberEntity> FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.<FishingBobberEntity>createNothing(EntityClassification.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
   public static final EntityType<GrapplingHookEntity> GRAPPLING_HOOK_ENTITY = register("grappling_hook",
           EntityType.Builder.<GrapplingHookEntity>createNothing(EntityClassification.MISC)
                   .noSave()
                   .noSummon()
                   .sized(0.25F, 0.25F)
                   .clientTrackingRange(4)
                   .updateInterval(5)

   );
   public static final EntityType<HerobrineEntity> HEROBRINE = register("herobrine", EntityType.Builder.<HerobrineEntity>of(HerobrineEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.8F).clientTrackingRange(32).updateInterval(2));



   //zombie
   public static final EntityType<ZombieEntity> ZOMBIE = register("zombie", EntityType.Builder.<ZombieEntity>of(ZombieEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = register("zombie_horse", EntityType.Builder.<ZombieHorseEntity>of(ZombieHorseEntity::new, CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
   public static final EntityType<ZombifiedPiglinEntity> ZOMBIFIED_PIGLIN = register("zombified_piglin", EntityType.Builder.<ZombifiedPiglinEntity>of(ZombifiedPiglinEntity::new, EntityClassification.MONSTER).fireImmune().sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<ZoglinEntity> ZOGLIN = register("zoglin", EntityType.Builder.<ZoglinEntity>of(ZoglinEntity::new, EntityClassification.MONSTER).fireImmune().sized(1.3964844F, 1.4F).clientTrackingRange(8));
   public static final EntityType<DemonEyeEntity> DEMON_EYE = register("demon_eye", Builder.of(DemonEyeEntity::new, EntityClassification.MONSTER).sized(0.5f, 0.45f).clientTrackingRange(8).canSpawnFarFromPlayer());
   public static final EntityType<EyeOfCthulhuEntity> EYE_OF_CTHULHU = register("eye_of_cthulhu", Builder.of(EyeOfCthulhuEntity::new, EntityClassification.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(16).canSpawnFarFromPlayer().immuneTo(Blocks.SWEET_BERRY_BUSH, Blocks.CACTUS, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE));
   public static final EntityType<WormEntity> GIANT_WORM = register("giant_worm", Builder.of(WormEntity::new, EntityClassification.MONSTER).sized(0.4f, 0.4f).clientTrackingRange(8));

   public static final EntityType<EyeOfCthulhuSecondFormEntity> EYE_OF_CTHULHU_SECOND_FORM = register("eye_of_cthulhu_second_form", Builder.of(EyeOfCthulhuSecondFormEntity::new, EntityClassification.MONSTER).sized(1.6f, 1.8f).clientTrackingRange(16).canSpawnFarFromPlayer().immuneTo(Blocks.SWEET_BERRY_BUSH, Blocks.CACTUS, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE));

   public static final EntityType<RetinazerEntity> RETINAZER = register("retinazer", Builder.of(RetinazerEntity::new, EntityClassification.MONSTER).sized(1.6f, 1.6f).clientTrackingRange(16).canSpawnFarFromPlayer().immuneTo(Blocks.SWEET_BERRY_BUSH, Blocks.CACTUS, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE));

   //skeleton
   public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = register("wither_skeleton", EntityType.Builder.<WitherSkeletonEntity>of(WitherSkeletonEntity::new, EntityClassification.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.7F, 2.4F).clientTrackingRange(8));
   public static final EntityType<SkeletonEntity> SKELETON = register("skeleton", EntityType.Builder.<SkeletonEntity>of(SkeletonEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8));
   public static final EntityType<BoggedEntity> BOGGED = register("bogged", EntityType.Builder.<BoggedEntity>of(BoggedEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8));
   public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = register("skeleton_horse", EntityType.Builder.<SkeletonHorseEntity>of(SkeletonHorseEntity::new, CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));

   // Horse Entities
   public static final EntityType<HorseEntity> HORSE = register("horse", EntityType.Builder.<HorseEntity>of(HorseEntity::new, CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
   public static final EntityType<DonkeyEntity> DONKEY = register("donkey", EntityType.Builder.<DonkeyEntity>of(DonkeyEntity::new, CREATURE).sized(1.3964844F, 1.5F).clientTrackingRange(10));
   public static final EntityType<MuleEntity> MULE = register("mule", EntityType.Builder.<MuleEntity>of(MuleEntity::new, CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(8));
   public static final EntityType<LlamaEntity> LLAMA = register("llama", EntityType.Builder.<LlamaEntity>of(LlamaEntity::new, CREATURE).sized(0.9F, 1.87F).clientTrackingRange(10));
   public static final EntityType<TraderLlamaEntity> TRADER_LLAMA = register("trader_llama", EntityType.Builder.<TraderLlamaEntity>of(TraderLlamaEntity::new, CREATURE).sized(0.9F, 1.87F).clientTrackingRange(10));

   // Projectile Entities
   public static final EntityType<SnowballEntity> SNOWBALL = register("snowball", EntityType.Builder.<SnowballEntity>of(SnowballEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<PokeballEntity> POKEBALL = register("pokeball", EntityType.Builder.<PokeballEntity>of(PokeballEntity::new, EntityClassification.MISC).sized(0.4F, 0.4F).clientTrackingRange(4).updateInterval(10));

   public static final EntityType<TemperateEggEntity> EGG = register("egg", EntityType.Builder.<TemperateEggEntity>of(TemperateEggEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<WarmEggEntity> WARM_EGG = register("warm_egg", EntityType.Builder.<WarmEggEntity>of(WarmEggEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<ColdEggEntity> COLD_EGG = register("egg", EntityType.Builder.<ColdEggEntity>of(ColdEggEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));




   public static final EntityType<StarfuryStarEntity> STARFURY_STAR = register("star", EntityType.Builder.<StarfuryStarEntity>of(StarfuryStarEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<MeowmereProjectileEntity> CAT_PROJECTILE = register("cat_projectile", EntityType.Builder.<MeowmereProjectileEntity>of(MeowmereProjectileEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<GumballEntity> GUMBALL = register("gumball", EntityType.Builder.<GumballEntity>of(GumballEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(1));
   public static final EntityType<DesolateDaggerEntity> DESOLATE_DAGGER = register("desolate_dagger", EntityType.Builder.<DesolateDaggerEntity>of(DesolateDaggerEntity::new, EntityClassification.MISC).sized(0.6F, 0.6F).clientTrackingRange(4).updateInterval(1));


   public static final EntityType<EnderPearlEntity> ENDER_PEARL = register("ender_pearl", EntityType.Builder.<EnderPearlEntity>of(EnderPearlEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<ExperienceBottleEntity> EXPERIENCE_BOTTLE = register("experience_bottle", EntityType.Builder.<ExperienceBottleEntity>of(ExperienceBottleEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<PotionEntity> POTION = register("potion", EntityType.Builder.<PotionEntity>of(PotionEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<TridentEntity> TRIDENT = register("trident", EntityType.Builder.<TridentEntity>of(TridentEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
   public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = register("small_fireball", EntityType.Builder.<SmallFireballEntity>of(SmallFireballEntity::new, EntityClassification.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<FireballEntity> FIREBALL = register("fireball", EntityType.Builder.<FireballEntity>of(FireballEntity::new, EntityClassification.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));
   public static final EntityType<InfernalFireballEntity> INFERNAL_FIREBALL = register("infernal_fireball", EntityType.Builder.<InfernalFireballEntity>of(InfernalFireballEntity::new, EntityClassification.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10));

   // Villager Entities
   public static final EntityType<VillagerEntity> VILLAGER = register("villager", EntityType.Builder.<VillagerEntity>of(VillagerEntity::new, EntityClassification.MISC).sized(0.6F, 1.95F).clientTrackingRange(10));
   public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = register("zombie_villager", EntityType.Builder.<ZombieVillagerEntity>of(ZombieVillagerEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<WanderingTraderEntity> WANDERING_TRADER = register("wandering_trader", EntityType.Builder.<WanderingTraderEntity>of(WanderingTraderEntity::new, CREATURE).sized(0.6F, 1.95F).clientTrackingRange(10));



   public static final EntityType<RattataEntity> RATTATA = register("rattata", EntityType.Builder.<RattataEntity>of(RattataEntity::new, EntityClassification.MISC).sized(0.65F, 0.45F).clientTrackingRange(10));


   // Illager Entities
   public static final EntityType<EvokerEntity> EVOKER = register("evoker", EntityType.Builder.<EvokerEntity>of(EvokerEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<TricksterEntity> TRICKSTER = register("trickster", EntityType.Builder.<TricksterEntity>of(TricksterEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));

   public static final EntityType<VindicatorEntity> VINDICATOR = register("vindicator", EntityType.Builder.<VindicatorEntity>of(VindicatorEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<MarauderEntity> MARAUDER = register("marauder", EntityType.Builder.<MarauderEntity>of(MarauderEntity::new, EntityClassification.MONSTER).sized(0.6F, 2.1F).clientTrackingRange(8));
   public static final EntityType<IllusionerEntity> ILLUSIONER = register("illusioner", EntityType.Builder.<IllusionerEntity>of(IllusionerEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<PillagerEntity> PILLAGER = register("pillager", EntityType.Builder.<PillagerEntity>of(PillagerEntity::new, EntityClassification.MONSTER).canSpawnFarFromPlayer().sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<PillagerCaptainEntity> PILLAGER_CAPTAIN = register("pillager_captain", EntityType.Builder.<PillagerCaptainEntity>of(PillagerCaptainEntity::new, EntityClassification.MONSTER).canSpawnFarFromPlayer().sized(0.6F, 1.95F).clientTrackingRange(8));
   public static final EntityType<RavagerEntity> RAVAGER = register("ravager", EntityType.Builder.<RavagerEntity>of(RavagerEntity::new, EntityClassification.MONSTER).sized(1.95F, 2.2F).clientTrackingRange(10));
   public static final EntityType<GildedRavagerEntity> GILDED_RAVAGER = register("gilded_ravager", EntityType.Builder.<GildedRavagerEntity>of(GildedRavagerEntity::new, EntityClassification.MONSTER).sized(1.95F, 2.45F).clientTrackingRange(10));


   private final EntityType.IFactory<T> factory;
   private final EntityClassification category;
   private final ImmutableSet<Block> immuneTo;


   public static boolean rollSpawn(int rolls, Random random, SpawnReason reason){
      if(reason == SpawnReason.SPAWNER){
         return true;
      } else {
         return rolls <= 0 || random.nextInt(rolls) == 0;
      }
   }


   private final boolean serialize;
   private final boolean summon;
   private final boolean fireImmune;
   private final boolean canSpawnFarFromPlayer;
   private final int clientTrackingRange;
   private final int updateInterval;
   @Nullable
   private String descriptionId;
   @Nullable
   private ITextComponent description;
   @Nullable
   private ResourceLocation lootTable;
   private final EntitySize dimensions;

   public static <T extends Entity> EntityType<T> register(String p_200712_0_, EntityType.Builder<T> p_200712_1_) {
      return Registry.register(Registry.ENTITY_TYPE, p_200712_0_, p_200712_1_.build(p_200712_0_));
   }

   public ResourceLocation getRegistryName() {
      return Registry.ENTITY_TYPE.getKey(this);
   }

   public static ResourceLocation getKey(EntityType<?> p_200718_0_) {
      return Registry.ENTITY_TYPE.getKey(p_200718_0_);
   }

   public static Optional<EntityType<?>> byString(String p_220327_0_) {
      return Registry.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(p_220327_0_));
   }

   public EntityType(EntityType.IFactory<T> p_i231489_1_, EntityClassification p_i231489_2_, boolean p_i231489_3_, boolean p_i231489_4_, boolean p_i231489_5_, boolean p_i231489_6_, ImmutableSet<Block> p_i231489_7_, EntitySize p_i231489_8_, int p_i231489_9_, int p_i231489_10_) {
      this.factory = p_i231489_1_;
      this.category = p_i231489_2_;
      this.canSpawnFarFromPlayer = p_i231489_6_;
      this.serialize = p_i231489_3_;
      this.summon = p_i231489_4_;
      this.fireImmune = p_i231489_5_;
      this.immuneTo = p_i231489_7_;
      this.dimensions = p_i231489_8_;
      this.clientTrackingRange = p_i231489_9_;
      this.updateInterval = p_i231489_10_;
   }

   @Nullable
   public Entity spawn(ServerWorld p_220331_1_, @Nullable ItemStack p_220331_2_, @Nullable PlayerEntity p_220331_3_, BlockPos p_220331_4_, SpawnReason p_220331_5_, boolean p_220331_6_, boolean p_220331_7_) {
      return this.spawn(p_220331_1_, p_220331_2_ == null ? null : p_220331_2_.getTag(), p_220331_2_ != null && p_220331_2_.hasCustomHoverName() ? p_220331_2_.getHoverName() : null, p_220331_3_, p_220331_4_, p_220331_5_, p_220331_6_, p_220331_7_);
   }

   @Nullable
   public T spawn(ServerWorld p_220342_1_, @Nullable CompoundNBT p_220342_2_, @Nullable ITextComponent p_220342_3_, @Nullable PlayerEntity p_220342_4_, BlockPos p_220342_5_, SpawnReason p_220342_6_, boolean p_220342_7_, boolean p_220342_8_) {
      T t = this.create(p_220342_1_, p_220342_2_, p_220342_3_, p_220342_4_, p_220342_5_, p_220342_6_, p_220342_7_, p_220342_8_);
      if (t != null) {
         p_220342_1_.addFreshEntityWithPassengers(t);
      }

      return t;
   }

   public AxisAlignedBB getSpawnAABB(double $$0, double $$1, double $$2) {
      float $$3 = this.getWidth() / 2.0f;
      float $$4 = this.getHeight();
      return new AxisAlignedBB($$0 - (double)$$3, $$1, $$2 - (double)$$3, $$0 + (double)$$3, $$1 + (double)$$4, $$2 + (double)$$3);
   }

   @Nullable
   public T create(ServerWorld p_220349_1_, @Nullable CompoundNBT p_220349_2_, @Nullable ITextComponent p_220349_3_, @Nullable PlayerEntity p_220349_4_, BlockPos p_220349_5_, SpawnReason p_220349_6_, boolean p_220349_7_, boolean p_220349_8_) {
      T t = this.create(p_220349_1_);
      if (t == null) {
         return (T)null;
      } else {
         double d0;
         if (p_220349_7_) {
            t.setPos((double)p_220349_5_.getX() + 0.5D, (double)(p_220349_5_.getY() + 1), (double)p_220349_5_.getZ() + 0.5D);
            d0 = getYOffset(p_220349_1_, p_220349_5_, p_220349_8_, t.getBoundingBox());
         } else {
            d0 = 0.0D;
         }

         t.moveTo((double)p_220349_5_.getX() + 0.5D, (double)p_220349_5_.getY() + d0, (double)p_220349_5_.getZ() + 0.5D, MathHelper.wrapDegrees(p_220349_1_.random.nextFloat() * 360.0F), 0.0F);
         if (t instanceof Mob) {
            Mob mobentity = (Mob)t;
            mobentity.yHeadRot = mobentity.yRot;
            mobentity.yBodyRot = mobentity.yRot;
            mobentity.finalizeSpawn(p_220349_1_, p_220349_1_.getCurrentDifficultyAt(mobentity.blockPosition()), p_220349_6_, (ILivingEntityData)null, p_220349_2_);
            mobentity.playAmbientSound();
         }

         if (p_220349_3_ != null && t instanceof LivingEntity) {
            t.setCustomName(p_220349_3_);
         }

         updateCustomEntityTag(p_220349_1_, p_220349_4_, t, p_220349_2_);
         return t;
      }
   }

   protected static double getYOffset(IWorldReader p_208051_0_, BlockPos p_208051_1_, boolean p_208051_2_, AxisAlignedBB p_208051_3_) {
      AxisAlignedBB axisalignedbb = new AxisAlignedBB(p_208051_1_);
      if (p_208051_2_) {
         axisalignedbb = axisalignedbb.expandTowards(0.0D, -1.0D, 0.0D);
      }

      Stream<VoxelShape> stream = p_208051_0_.getCollisions((Entity)null, axisalignedbb, (p_233596_0_) -> {
         return true;
      });
      return 1.0D + VoxelShapes.collide(Direction.Axis.Y, p_208051_3_, stream, p_208051_2_ ? -2.0D : -1.0D);
   }

   public static void updateCustomEntityTag(World p_208048_0_, @Nullable PlayerEntity p_208048_1_, @Nullable Entity p_208048_2_, @Nullable CompoundNBT p_208048_3_) {
      if (p_208048_3_ != null && p_208048_3_.contains("EntityTag", 10)) {
         MinecraftServer minecraftserver = p_208048_0_.getServer();
         if (minecraftserver != null && p_208048_2_ != null) {
            if (p_208048_0_.isClientSide || !p_208048_2_.onlyOpCanSetNbt() || p_208048_1_ != null && minecraftserver.getPlayerList().isOp(p_208048_1_.getGameProfile())) {
               CompoundNBT compoundnbt = p_208048_2_.saveWithoutId(new CompoundNBT());
               UUID uuid = p_208048_2_.getUUID();
               compoundnbt.merge(p_208048_3_.getCompound("EntityTag"));
               p_208048_2_.setUUID(uuid);
               p_208048_2_.load(compoundnbt);
            }
         }
      }
   }

   public boolean canSerialize() {
      return this.serialize;
   }

   public boolean canSummon() {
      return this.summon;
   }

   public boolean fireImmune() {
      return this.fireImmune;
   }

   public boolean canSpawnFarFromPlayer() {
      return this.canSpawnFarFromPlayer;
   }

   public EntityClassification getCategory() {
      return this.category;
   }


   public T getEntity(World level) {
      T entity = this.create(level);
      if (entity != null) {
         return entity;
      } else {
         throw new IllegalStateException("Failed to create entity of type: " + this);
      }
   }

   public boolean is(EntityType<?> entity) {
      return entity.equals(this);
   }

   public String getDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("entity", Registry.ENTITY_TYPE.getKey(this));
      }

      return this.descriptionId;
   }

   public ITextComponent getDescription() {
      if (this.description == null) {
         this.description = new TranslationTextComponent(this.getDescriptionId());
      }

      return this.description;
   }

   public String toString() {
      return this.getDescriptionId();
   }

   public ResourceLocation getDefaultLootTable() {
      if (this.lootTable == null) {
         ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(this);
         this.lootTable = new ResourceLocation(resourcelocation.getNamespace(), "entities/" + resourcelocation.getPath());
      }

      return this.lootTable;
   }

   public float getWidth() {
      return this.dimensions.width;
   }

   public float getHeight() {
      return this.dimensions.height;
   }

   @Nullable
   public T create(World p_200721_1_) {
      return this.factory.create(this, p_200721_1_);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Entity create(int p_200717_0_, World p_200717_1_) {
      return create(p_200717_1_, Registry.ENTITY_TYPE.byId(p_200717_0_));
   }

   public static Optional<Entity> create(CompoundNBT p_220330_0_, World p_220330_1_) {
      return Util.ifElse(by(p_220330_0_).map((p_220337_1_) -> {
         return p_220337_1_.create(p_220330_1_);
      }), (p_220329_1_) -> {
         p_220329_1_.load(p_220330_0_);
      }, () -> {
         LOGGER.warn("Skipping Entity with id {}", (Object)p_220330_0_.getString("id"));
      });
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   private static Entity create(World p_200719_0_, @Nullable EntityType<?> p_200719_1_) {
      return p_200719_1_ == null ? null : p_200719_1_.create(p_200719_0_);
   }

   public AxisAlignedBB getAABB(double p_220328_1_, double p_220328_3_, double p_220328_5_) {
      float f = this.getWidth() / 2.0F;
      return new AxisAlignedBB(p_220328_1_ - (double)f, p_220328_3_, p_220328_5_ - (double)f, p_220328_1_ + (double)f, p_220328_3_ + (double)this.getHeight(), p_220328_5_ + (double)f);
   }

   public boolean isBlockDangerous(BlockState p_233597_1_) {
      if (this.immuneTo.contains(p_233597_1_.getBlock())) {
         return false;
      } else if (this.fireImmune || !p_233597_1_.is(BlockTags.FIRE) && !p_233597_1_.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(p_233597_1_) && !p_233597_1_.is(Blocks.LAVA)) {
         return p_233597_1_.is(Blocks.WITHER_ROSE) || p_233597_1_.is(Blocks.SWEET_BERRY_BUSH) || p_233597_1_.is(Blocks.CACTUS);
      } else {
         return true;
      }
   }

   public EntitySize getDimensions() {
      return this.dimensions;
   }

   public static Optional<EntityType<?>> by(CompoundNBT p_220347_0_) {
      return Registry.ENTITY_TYPE.getOptional(new ResourceLocation(p_220347_0_.getString("id")));
   }

   @Nullable
   public static Entity loadEntityRecursive(CompoundNBT p_220335_0_, World p_220335_1_, Function<Entity, Entity> p_220335_2_) {
      return loadStaticEntity(p_220335_0_, p_220335_1_).map(p_220335_2_).map((p_220346_3_) -> {
         if (p_220335_0_.contains("Passengers", 9)) {
            ListNBT listnbt = p_220335_0_.getList("Passengers", 10);

            for(int i = 0; i < listnbt.size(); ++i) {
               Entity entity = loadEntityRecursive(listnbt.getCompound(i), p_220335_1_, p_220335_2_);
               if (entity != null) {
                  entity.startRiding(p_220346_3_, true);
               }
            }
         }

         return p_220346_3_;
      }).orElse((Entity)null);
   }

   private static Optional<Entity> loadStaticEntity(CompoundNBT p_220343_0_, World p_220343_1_) {
      try {
         return create(p_220343_0_, p_220343_1_);
      } catch (RuntimeException runtimeexception) {
         LOGGER.warn("Exception loading entity: ", (Throwable)runtimeexception);
         return Optional.empty();
      }
   }

   public int clientTrackingRange() {
      return this.clientTrackingRange;
   }

   public int updateInterval() {
      return this.updateInterval;
   }

   public boolean trackDeltas() {
      return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != PALE_GARDEN_BAT && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
   }



   public boolean is(ITag<EntityType<?>> p_220341_1_) {
      return p_220341_1_.contains(this);
   }



    public static class Builder<T extends Entity> {
      public final EntityType.IFactory<T> factory;
      private final EntityClassification category;
      private ImmutableSet<Block> immuneTo = ImmutableSet.of();
      private boolean serialize = true;
      private boolean summon = true;
      private boolean fireImmune;
      private boolean canSpawnFarFromPlayer;
      private int clientTrackingRange = 5;
      private int updateInterval = 3;
       private EntityAttachments.Builder attachments = EntityAttachments.builder();
       private EntitySize dimensions = EntitySize.scalable(0.6F, 1.8F);

      private Builder(EntityType.IFactory<T> p_i50479_1_, EntityClassification p_i50479_2_) {
         this.factory = p_i50479_1_;
         this.category = p_i50479_2_;
         this.canSpawnFarFromPlayer = p_i50479_2_ == CREATURE || p_i50479_2_ == EntityClassification.MISC;
      }



      public static <T extends Entity> EntityType.Builder<T> of(EntityType.IFactory<T> p_220322_0_, EntityClassification p_220322_1_) {
         return new EntityType.Builder<>(p_220322_0_, p_220322_1_);
      }

      public static <T extends Entity> EntityType.Builder<T> createNothing(EntityClassification p_220319_0_) {
         return new EntityType.Builder<>((p_220323_0_, p_220323_1_) -> {
            return (T)null;
         }, p_220319_0_);
      }

      public EntityType.Builder<T> sized(float p_220321_1_, float p_220321_2_) {
         this.dimensions = EntitySize.scalable(p_220321_1_, p_220321_2_);
         return this;
      }

       public Builder<T> eyeHeight(float f) {
          this.dimensions = this.dimensions.withEyeHeight(f);
          return this;
       }

       public Builder<T> passengerAttachments(float ... fArray) {
          for (float f : fArray) {
             this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, 0.0f, f, 0.0f);
          }
          return this;
       }

       public Builder<T> passengerAttachments(Vector3d ... vec3Array) {
          for (Vector3d vec3 : vec3Array) {
             this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, vec3);
          }
          return this;
       }

      public EntityType.Builder<T> sized(float size) {
         return sized(size, size);
      }


       public Builder<T> vehicleAttachment(Vector3d vec3) {
          return this.attach(EntityAttachment.VEHICLE, vec3);
       }

       public Builder<T> ridingOffset(float f) {
          return this.attach(EntityAttachment.VEHICLE, 0.0f, -f, 0.0f);
       }

       public Builder<T> nameTagOffset(float f) {
          return this.attach(EntityAttachment.NAME_TAG, 0.0f, f, 0.0f);
       }

       public Builder<T> attach(EntityAttachment entityAttachment, float f, float f2, float f3) {
          //System.out.println("Attaching: " + entityAttachment.name() + " with value: " + (new Vector3d(f, f2, f3)));
          this.attachments = this.attachments.attach(entityAttachment, f, f2, f3);
          //System.out.println("Current attachments: " + this.attachments.toString());
          return this;
       }

       public Builder<T> attach(EntityAttachment entityAttachment, Vector3d vec3) {
         //System.out.println("Attaching: " + entityAttachment.name() + " with value: " + vec3.toString());
          this.attachments = this.attachments.attach(entityAttachment, vec3);

          //System.out.println("Current attachments: " + this.attachments.toString());
          return this;
       }


      public EntityType.Builder<T> noSummon() {
         this.summon = false;
         return this;
      }

      public EntityType.Builder<T> noSave() {
         this.serialize = false;
         return this;
      }

      public EntityType.Builder<T> fireImmune() {
         this.fireImmune = true;
         return this;
      }

      public EntityType.Builder<T> immuneTo(Block... p_233607_1_) {
         this.immuneTo = ImmutableSet.copyOf(p_233607_1_);
         return this;
      }

      public EntityType.Builder<T> canSpawnFarFromPlayer() {
         this.canSpawnFarFromPlayer = true;
         return this;
      }

      public EntityType.Builder<T> clientTrackingRange(int p_233606_1_) {
         this.clientTrackingRange = p_233606_1_;
         return this;
      }

      public EntityType.Builder<T> updateInterval(int p_233608_1_) {
         this.updateInterval = p_233608_1_;
         return this;
      }

      public EntityType<T> build(String p_206830_1_) {
         if (this.serialize) {
            Util.fetchChoiceType(TypeReferences.ENTITY_TREE, p_206830_1_);
         }
         //System.out.println("Building EntityType with attachments: " + this.attachments.toString());

         return new EntityType<>(this.factory, this.category, this.serialize, this.summon, this.fireImmune, this.canSpawnFarFromPlayer, this.immuneTo, this.dimensions.withAttachments(attachments), this.clientTrackingRange, this.updateInterval);
      }
   }

   public interface IFactory<T extends Entity> {
      T create(EntityType<T> p_create_1_, World p_create_2_);
   }
}