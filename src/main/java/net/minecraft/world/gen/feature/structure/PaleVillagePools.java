package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;

public class PaleVillagePools {
   public static final JigsawPattern START = JigsawPatternRegistry.register(
           new JigsawPattern(
                   // no need for a zombie variant, that would take too long. might add more town centers in the future
                   new ResourceLocation("village/pale/town_centers"),
                   new ResourceLocation("empty"), // fallback?
                   ImmutableList.of(
                           Pair.of(JigsawPiece.legacy("village/pale/town_centers/pale_meeting_point_1"), 50),
                           Pair.of(JigsawPiece.legacy("village/pale/town_centers/pale_meeting_point_2", ProcessorLists.PALE_MOSS_15_PERCENT), 50)
                   ),
                   JigsawPattern.PlacementBehaviour.RIGID
           )
   );

   // required
   public static void bootstrap() {
   }

   static {
      JigsawPatternRegistry.register(
              new JigsawPattern(
                      new ResourceLocation("village/pale/streets"),
                      new ResourceLocation("village/pale/terminators"),
                      ImmutableList.of(
                              // TODO: need to finish the rest of these and add them, one of these might also be bugged
                              Pair.of(JigsawPiece.legacy("village/pale/streets/corner_01"), 2),
                              Pair.of(JigsawPiece.legacy("village/pale/streets/corner_02"), 2),
                              Pair.of(JigsawPiece.legacy("village/pale/streets/corner_03"), 2),
                              Pair.of(JigsawPiece.legacy("village/pale/streets/crossroad_01"), 2),
                              Pair.of(JigsawPiece.legacy("village/pale/streets/crossroad_02"), 2),
                              Pair.of(JigsawPiece.legacy("village/pale/streets/crossroad_03"), 2),
                              Pair.of(JigsawPiece.legacy("village/pale/streets/turn_01"), 2),
                              Pair.of(JigsawPiece.legacy("village/pale/streets/crossroad_06"), 2),
                              Pair.of(JigsawPiece.legacy("village/pale/streets/straight_02"), 2)



                      ),
                      JigsawPattern.PlacementBehaviour.TERRAIN_MATCHING
              )
      );

      // TODO: i need a lot more houses for a complete village, this is a placeholder as i don't want to use plains villages as placeholders instead
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("village/pale/houses"), new ResourceLocation("village/pale/terminators"), ImmutableList.of(
                      Pair.of(JigsawPiece.legacy("village/pale/houses/pale_small_house_1", ProcessorLists.PALE_MOSS_15_PERCENT), 2),
              Pair.of(JigsawPiece.legacy("village/pale/houses/pale_small_house_2", ProcessorLists.PALE_MOSS_15_PERCENT), 2),
              Pair.of(JigsawPiece.legacy("village/pale/houses/pale_small_house_3", ProcessorLists.PALE_MOSS_35_PERCENT), 2),
              Pair.of(JigsawPiece.legacy("village/pale/houses/pale_small_house_4", ProcessorLists.PALE_MOSS_35_PERCENT), 2),
              Pair.of(JigsawPiece.legacy("village/pale/houses/pale_small_house_5", ProcessorLists.PALE_MOSS_35_PERCENT), 2),
              Pair.of(JigsawPiece.legacy("village/pale/houses/pale_medium_house_1", ProcessorLists.PALE_MOSS_35_PERCENT), 2),
              Pair.of(JigsawPiece.legacy("village/pale/houses/pale_medium_house_3", ProcessorLists.PALE_MOSS_35_PERCENT), 2), // this took so long and it's broken
              Pair.of(JigsawPiece.legacy("village/pale/houses/pale_temple_1", ProcessorLists.PALE_MOSS_85_PERCENT), 2),
              // houses are taking so long to make
              Pair.of(JigsawPiece.legacy("village/pale/houses/pale_armorer_2", ProcessorLists.PALE_MOSS_15_PERCENT), 2),
              Pair.of(JigsawPiece.legacy("village/pale/houses/pale_weaponsmith_2"), 2),
              Pair.of(JigsawPiece.legacy("village/pale/houses/accessory_1"), 1),
              Pair.of(JigsawPiece.legacy("village/pale/houses/pale_tool_smith_1", ProcessorLists.PALE_MOSS_35_PERCENT), 2),

              Pair.of(JigsawPiece.empty(), 8)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("village/pale/terminators"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.legacy("village/pale/terminators/terminator_01", ProcessorLists.STREET_PLAINS), 1), Pair.of(JigsawPiece.legacy("village/pale/terminators/terminator_02", ProcessorLists.STREET_PLAINS), 1), Pair.of(JigsawPiece.legacy("village/pale/terminators/terminator_03", ProcessorLists.STREET_PLAINS), 1), Pair.of(JigsawPiece.legacy("village/pale/terminators/terminator_04", ProcessorLists.STREET_PLAINS), 1)), JigsawPattern.PlacementBehaviour.TERRAIN_MATCHING));

      // TODO: probably need more decor, maybe a tiny bit more lighting, but i don't want the village to be that visible in the dark.
      JigsawPatternRegistry.register(new JigsawPattern(resource("village/pale/decor"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.legacy("village/pale/pale_lamp_post_1"), 2), Pair.of(JigsawPiece.legacy("village/pale/pale_decoration_1"), 1), Pair.of(JigsawPiece.feature(Features.EYEBLOSSOM_FEATURE), 1), Pair.of(JigsawPiece.empty(), 2), Pair.of(JigsawPiece.empty(), 2)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawPatternRegistry.register(new JigsawPattern(resource("village/pale/eyeblossom"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.feature(Features.EYEBLOSSOM_FEATURE), 1)), JigsawPattern.PlacementBehaviour.RIGID));
      JigsawPatternRegistry.register(new JigsawPattern(resource("village/pale/eyeblossom_many"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.feature(Features.EYEBLOSSOM_FEATURE_LARGE), 1)), JigsawPattern.PlacementBehaviour.RIGID));

   }


   private static ResourceLocation resource(String resource) {
       return new ResourceLocation(resource);
   }
}