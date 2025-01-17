package net.minecraft.world.gen.foliageplacer;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.trunkplacer.PaleOakLargeTrunkPlacer;

public class FoliagePlacerType<P extends FoliagePlacer> {
   public static final FoliagePlacerType<BlobFoliagePlacer> BLOB_FOLIAGE_PLACER = register("blob_foliage_placer", BlobFoliagePlacer.CODEC);
   public static final FoliagePlacerType<SpruceFoliagePlacer> SPRUCE_FOLIAGE_PLACER = register("spruce_foliage_placer", SpruceFoliagePlacer.CODEC);
   public static final FoliagePlacerType<PineFoliagePlacer> PINE_FOLIAGE_PLACER = register("pine_foliage_placer", PineFoliagePlacer.CODEC);
   public static final FoliagePlacerType<AcaciaFoliagePlacer> ACACIA_FOLIAGE_PLACER = register("acacia_foliage_placer", AcaciaFoliagePlacer.CODEC);
   public static final FoliagePlacerType<BushFoliagePlacer> BUSH_FOLIAGE_PLACER = register("bush_foliage_placer", BushFoliagePlacer.CODEC);
   public static final FoliagePlacerType<FancyFoliagePlacer> FANCY_FOLIAGE_PLACER = register("fancy_foliage_placer", FancyFoliagePlacer.CODEC);
   public static final FoliagePlacerType<JungleFoliagePlacer> MEGA_JUNGLE_FOLIAGE_PLACER = register("jungle_foliage_placer", JungleFoliagePlacer.CODEC);
   public static final FoliagePlacerType<MegaPineFoliagePlacer> MEGA_PINE_FOLIAGE_PLACER = register("mega_pine_foliage_placer", MegaPineFoliagePlacer.CODEC);
   public static final FoliagePlacerType<DarkOakFoliagePlacer> DARK_OAK_FOLIAGE_PLACER = register("dark_oak_foliage_placer", DarkOakFoliagePlacer.CODEC);
   public static final FoliagePlacerType<PaleOakLargeFoliagePlacer> PALE_OAK_FOLIAGE_PLACER = register("pale_oak_foliage_placer", PaleOakLargeFoliagePlacer.CODEC);

   private final Codec<P> codec;

   private static <P extends FoliagePlacer> FoliagePlacerType<P> register(String p_236773_0_, Codec<P> p_236773_1_) {
      return Registry.register(Registry.FOLIAGE_PLACER_TYPES, p_236773_0_, new FoliagePlacerType<>(p_236773_1_));
   }

   private FoliagePlacerType(Codec<P> p_i232036_1_) {
      this.codec = p_i232036_1_;
   }

   public Codec<P> codec() {
      return this.codec;
   }
}