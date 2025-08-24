package net.minecraft.particles;

import com.mojang.serialization.Codec;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ParticleTypes {
   public static final BasicParticleType AMBIENT_ENTITY_EFFECT = register("ambient_entity_effect", false);
   public static final BasicParticleType ANGRY_VILLAGER = register("angry_villager", false);
   public static final BasicParticleType BARRIER = register("barrier", false);
   public static final ParticleType<BlockParticleData> BLOCK = register("block", BlockParticleData.DESERIALIZER, BlockParticleData::codec);
   public static final BasicParticleType BUBBLE = register("bubble", false);
   public static final BasicParticleType CLOUD = register("cloud", false);
   public static final BasicParticleType CRIT = register("crit", false);
   public static final BasicParticleType DAMAGE_INDICATOR = register("damage_indicator", true);
   public static final BasicParticleType DRAGON_BREATH = register("dragon_breath", false);
   public static final BasicParticleType DRIPPING_LAVA = register("dripping_lava", false);
   public static final BasicParticleType FALLING_LAVA = register("falling_lava", false);
   public static final BasicParticleType LANDING_LAVA = register("landing_lava", false);
   public static final BasicParticleType DRIPPING_WATER = register("dripping_water", false);
   public static final BasicParticleType FALLING_WATER = register("falling_water", false);
   public static final BasicParticleType DNA = register("dna", false);
   public static final BasicParticleType EGG_CRACK = ParticleTypes.register("egg_crack", false);


   public static final ParticleType<RedstoneParticleData> DUST = register("dust", RedstoneParticleData.DESERIALIZER, (p_239822_0_) -> {
      return RedstoneParticleData.CODEC;
   });
   public static final BasicParticleType SONIC_BOOM = ParticleTypes.register("sonic_boom", true);


   public static final BasicParticleType DUST_PLUME = ParticleTypes.register("dust_plume", false);


   public static final BasicParticleType EFFECT = register("effect", false);
   public static final BasicParticleType ELDER_GUARDIAN = register("elder_guardian", true);
   public static final BasicParticleType ENCHANTED_HIT = register("enchanted_hit", false);
   public static final BasicParticleType ENCHANT = register("enchant", false);
   public static final BasicParticleType END_ROD = register("end_rod", false);
   public static final BasicParticleType ENTITY_EFFECT = register("entity_effect", false);
   public static final BasicParticleType EXPLOSION_EMITTER = register("explosion_emitter", true);
   public static final BasicParticleType EXPLOSION = register("explosion", true);
   public static final ParticleType<BlockParticleData> FALLING_DUST = register("falling_dust", BlockParticleData.DESERIALIZER, BlockParticleData::codec);
   public static final BasicParticleType FIREWORK = register("firework", false);
   public static final BasicParticleType PALE_OAK_LEAVES = register("pale_oak_leaves", false);
   public static final BasicParticleType TINTED_LEAVES = register("tinted_leaves", false);
   public static final BasicParticleType BURSTING_TINTED_LEAVES = register("bursting_tinted_leaves", false);

   public static final BasicParticleType DEAD_LEAVES = register("dead_leaves", false);

   public static final BasicParticleType ELECTRIC_SPARK = ParticleTypes.register("electric_spark", true);
   public static final BasicParticleType WAX_ON = ParticleTypes.register("wax_on", true);
   public static final BasicParticleType WAX_OFF = ParticleTypes.register("wax_off", true);
   public static final BasicParticleType SCRAPE = ParticleTypes.register("scrape", true);
   public static final BasicParticleType FIREFLY = ParticleTypes.register("firefly", false);

   public static final ParticleType<TrailParticleOption> TRAIL = register("trail", TrailParticleOption.DESERIALIZER, (p_239822_0_) -> {
      return TrailParticleOption.CODEC;
   });

   public static final ParticleType<VibrationParticleOption> VIBRATION = register("vibration", VibrationParticleOption.DESERIALIZER, (p_239822_0_) -> {
      return VibrationParticleOption.CODEC;
   });

   public static final BasicParticleType FISHING = register("fishing", false);
   public static final BasicParticleType FLAME = register("flame", false);
   public static final BasicParticleType SMALL_FLAME = ParticleTypes.register("small_flame", false);
   public static final BasicParticleType SOUL_FIRE_FLAME = register("soul_fire_flame", false);
   public static final BasicParticleType SOUL = register("soul", false);
   public static final BasicParticleType FLASH = register("flash", false);
   public static final BasicParticleType HAPPY_VILLAGER = register("happy_villager", false);
   public static final BasicParticleType COMPOSTER = register("composter", false);
   public static final BasicParticleType HEART = register("heart", false);
   public static final BasicParticleType INSTANT_EFFECT = register("instant_effect", false);
   public static final ParticleType<ItemParticleData> ITEM = register("item", ItemParticleData.DESERIALIZER, ItemParticleData::codec);
   public static final BasicParticleType ITEM_SLIME = register("item_slime", false);
   public static final BasicParticleType ITEM_SNOWBALL = register("item_snowball", false);
   public static final BasicParticleType LARGE_SMOKE = register("large_smoke", false);
   public static final BasicParticleType LAVA = register("lava", false);
   public static final BasicParticleType MYCELIUM = register("mycelium", false);
   public static final BasicParticleType NOTE = register("note", false);
   public static final BasicParticleType POOF = register("poof", true);
   public static final BasicParticleType PORTAL = register("portal", false);
   public static final BasicParticleType RAIN = register("rain", false);
   public static final BasicParticleType SMOKE = register("smoke", false);
   public static final BasicParticleType SNEEZE = register("sneeze", false);
   public static final BasicParticleType SPIT = register("spit", true);
   public static final BasicParticleType SQUID_INK = register("squid_ink", true);
   public static final BasicParticleType SWEEP_ATTACK = register("sweep_attack", true);
   public static final BasicParticleType TOTEM_OF_UNDYING = register("totem_of_undying", false);
   public static final BasicParticleType UNDERWATER = register("underwater", false);
   public static final BasicParticleType SPLASH = register("splash", false);
   public static final BasicParticleType WITCH = register("witch", false);
   public static final BasicParticleType BUBBLE_POP = register("bubble_pop", false);
   public static final BasicParticleType CURRENT_DOWN = register("current_down", false);
   public static final BasicParticleType BUBBLE_COLUMN_UP = register("bubble_column_up", false);
   public static final BasicParticleType NAUTILUS = register("nautilus", false);
   public static final BasicParticleType DOLPHIN = register("dolphin", false);
   public static final BasicParticleType CAMPFIRE_COSY_SMOKE = register("campfire_cosy_smoke", true);
   public static final BasicParticleType CAMPFIRE_SIGNAL_SMOKE = register("campfire_signal_smoke", true);
   public static final BasicParticleType DRIPPING_HONEY = register("dripping_honey", false);
   public static final BasicParticleType FALLING_HONEY = register("falling_honey", false);
   public static final BasicParticleType LANDING_HONEY = register("landing_honey", false);
   public static final BasicParticleType FALLING_NECTAR = register("falling_nectar", false);
   public static final BasicParticleType ASH = register("ash", false);
   public static final BasicParticleType CRIMSON_SPORE = register("crimson_spore", false);
   public static final BasicParticleType WARPED_SPORE = register("warped_spore", false);
   public static final BasicParticleType DRIPPING_OBSIDIAN_TEAR = register("dripping_obsidian_tear", false);
   public static final BasicParticleType FALLING_OBSIDIAN_TEAR = register("falling_obsidian_tear", false);
   public static final BasicParticleType LANDING_OBSIDIAN_TEAR = register("landing_obsidian_tear", false);
   public static final BasicParticleType REVERSE_PORTAL = register("reverse_portal", false);
   public static final BasicParticleType WHITE_ASH = register("white_ash", false);
   public static final Codec<IParticleData> CODEC = Registry.PARTICLE_TYPE.dispatch("type", IParticleData::getType, ParticleType::codec);

   private static BasicParticleType register(String p_218415_0_, boolean p_218415_1_) {
      return Registry.register(Registry.PARTICLE_TYPE, p_218415_0_, new BasicParticleType(p_218415_1_));
   }


   public static void spawnParticlesAlongAxis(Direction.Axis axis, World level, BlockPos blockPos, double d, IParticleData particleOptions, int[] uniformInt) {
      Vector3d vec3 = Vector3d.atCenterOf(blockPos);
      boolean bl = axis == Direction.Axis.X;
      boolean bl2 = axis == Direction.Axis.Y;
      boolean bl3 = axis == Direction.Axis.Z;
      int n = randomBetweenInclusive(level.random, uniformInt[0], uniformInt[1]);
      for (int i = 0; i < n; ++i) {
         double d2 = vec3.x + MathHelper.nextDouble(level.random, -1.0, 1.0) * (bl ? 0.5 : d);
         double d3 = vec3.y + MathHelper.nextDouble(level.random, -1.0, 1.0) * (bl2 ? 0.5 : d);
         double d4 = vec3.z + MathHelper.nextDouble(level.random, -1.0, 1.0) * (bl3 ? 0.5 : d);
         double d5 = bl ? MathHelper.nextDouble(level.random, -1.0, 1.0) : 0.0;
         double d6 = bl2 ? MathHelper.nextDouble(level.random, -1.0, 1.0) : 0.0;
         double d7 = bl3 ? MathHelper.nextDouble(level.random, -1.0, 1.0) : 0.0;
         level.addParticle(particleOptions, d2, d3, d4, d5, d6, d7);
      }
   }

   public static void spawnParticles(World levelAccessor, BlockPos blockPos, int n, double d, double d2, boolean bl, IParticleData particleOptions) {
      Random randomSource = levelAccessor.getRandom();
      for (int i = 0; i < n; ++i) {
         double d3 = randomSource.nextGaussian() * 0.02;
         double d4 = randomSource.nextGaussian() * 0.02;
         double d5 = randomSource.nextGaussian() * 0.02;
         double d6 = 0.5 - d;
         double d7 = (double)blockPos.getX() + d6 + randomSource.nextDouble() * d * 2.0;
         double d8 = (double)blockPos.getY() + randomSource.nextDouble() * d2;
         double d9 = (double)blockPos.getZ() + d6 + randomSource.nextDouble() * d * 2.0;
         if (!bl && levelAccessor.getBlockState(BlockPos.containing(d7, d8, d9).below()).isAir()) continue;
         levelAccessor.addParticle(particleOptions, d7, d8, d9, d3, d4, d5);
      }
   }

   public static int randomBetweenInclusive(Random randomSource, int n, int n2) {
      return randomSource.nextInt(n2 - n + 1) + n;
   }

   public static void spawnParticleOnFace(World level, BlockPos blockPos, Direction direction, IParticleData particleOptions, Vector3d vec3, double d) {
      Vector3d vec32 = Vector3d.atCenterOf(blockPos);
      int n = direction.getStepX();
      int n2 = direction.getStepY();
      int n3 = direction.getStepZ();
      double d2 = vec32.x + (n == 0 ? MathHelper.nextDouble(level.random, -0.5, 0.5) : (double)n * d);
      double d3 = vec32.y + (n2 == 0 ? MathHelper.nextDouble(level.random, -0.5, 0.5) : (double)n2 * d);
      double d4 = vec32.z + (n3 == 0 ? MathHelper.nextDouble(level.random, -0.5, 0.5) : (double)n3 * d);
      double d5 = n == 0 ? vec3.x() : 0.0;
      double d6 = n2 == 0 ? vec3.y() : 0.0;
      double d7 = n3 == 0 ? vec3.z() : 0.0;
      level.addParticle(particleOptions, d2, d3, d4, d5, d6, d7);
   }

   public static void spawnParticlesOnBlockFaces(World level, BlockPos blockPos, IParticleData particleOptions, int[] intProvider) {
      for (Direction direction : Direction.values()) {
         spawnParticlesOnBlockFace(level, blockPos, particleOptions, intProvider, direction, () -> getRandomSpeedRanges(level.random), 0.55);
      }
   }

   private static Vector3d getRandomSpeedRanges(Random randomSource) {
      return new Vector3d(MathHelper.nextDouble(randomSource, -0.5, 0.5), MathHelper.nextDouble(randomSource, -0.5, 0.5), MathHelper.nextDouble(randomSource, -0.5, 0.5));
   }

   public static void spawnParticlesOnBlockFace(World level, BlockPos blockPos, IParticleData particleOptions, int[] intProvider, Direction direction, Supplier<Vector3d> supplier, double d) {
      int n = randomBetweenInclusive(level.random, intProvider[0], intProvider[1]);
      for (int i = 0; i < n; ++i) {
         spawnParticleOnFace(level, blockPos, direction, particleOptions, supplier.get(), d);
      }
   }

   private static <T extends IParticleData> ParticleType<T> register(String p_218416_0_, IParticleData.IDeserializer<T> p_218416_1_, final Function<ParticleType<T>, Codec<T>> p_218416_2_) {
      return Registry.register(Registry.PARTICLE_TYPE, p_218416_0_, new ParticleType<T>(false, p_218416_1_) {
         public Codec<T> codec() {
            return p_218416_2_.apply(this);
         }
      });
   }
}