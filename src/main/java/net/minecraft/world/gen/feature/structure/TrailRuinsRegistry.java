package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.StructureProcessorList;

public class TrailRuinsRegistry {
    private static final ResourceLocation EMPTY = new ResourceLocation("minecraft", "empty");


    public static final JigsawPattern START = JigsawPatternRegistry.register(
            new JigsawPattern(
                    new ResourceLocation("trail_ruins/tower"),
                    EMPTY,
                    ImmutableList.of(
                            Pair.of(JigsawPiece.legacy(tower(1), ProcessorLists.TRAIL_RUINS_HOUSES_ARCHAEOLOGY), 1),
                            Pair.of(JigsawPiece.legacy(tower(2), ProcessorLists.TRAIL_RUINS_HOUSES_ARCHAEOLOGY), 1),
                            Pair.of(JigsawPiece.legacy(tower(3), ProcessorLists.TRAIL_RUINS_HOUSES_ARCHAEOLOGY), 1),
                            Pair.of(JigsawPiece.legacy(tower(4), ProcessorLists.TRAIL_RUINS_HOUSES_ARCHAEOLOGY), 1),
                            Pair.of(JigsawPiece.legacy(tower(5), ProcessorLists.TRAIL_RUINS_HOUSES_ARCHAEOLOGY), 1)
                    ),
                    JigsawPattern.PlacementBehaviour.RIGID
            )
    );

    private static String tower(int i) {
        return "trail_ruins/tower/tower_" + String.valueOf(i);
    }


    public static void bootstrap() {
    }

    static {
        StructureProcessorList ruinsTowerTop = ProcessorLists.TRAIL_RUINS_TOWER_TOP_ARCHAEOLOGY;
        StructureProcessorList ruinsRoads = ProcessorLists.TRAIL_RUINS_ROADS_ARCHAEOLOGY;
        StructureProcessorList ruinsHouses = ProcessorLists.TRAIL_RUINS_HOUSES_ARCHAEOLOGY;

        register(new JigsawPattern(new ResourceLocation("trail_ruins/tower/tower_top"), EMPTY, ImmutableList.of(
                Pair.of(JigsawPiece.single("trail_ruins/tower/tower_top_1", ruinsTowerTop), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/tower_top_2", ruinsTowerTop), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/tower_top_3", ruinsTowerTop), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/tower_top_4", ruinsTowerTop), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/tower_top_5", ruinsTowerTop), 1)
        ), JigsawPattern.PlacementBehaviour.RIGID));

        register(new JigsawPattern(new ResourceLocation("trail_ruins/tower/additions"), EMPTY, ImmutableList.of(
                Pair.of(JigsawPiece.single("trail_ruins/tower/hall_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/hall_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/hall_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/hall_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/hall_5", ruinsHouses), 1),

                Pair.of(JigsawPiece.single("trail_ruins/tower/large_hall_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/large_hall_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/large_hall_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/large_hall_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/large_hall_5", ruinsHouses), 1),

                Pair.of(JigsawPiece.single("trail_ruins/tower/one_room_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/one_room_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/one_room_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/one_room_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/one_room_5", ruinsHouses), 1),

                Pair.of(JigsawPiece.single("trail_ruins/tower/platform_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/platform_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/platform_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/platform_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/platform_5", ruinsHouses), 1),

                Pair.of(JigsawPiece.single("trail_ruins/tower/tower_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/tower_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/tower_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/tower_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/tower/tower_5", ruinsHouses), 1)



        ), JigsawPattern.PlacementBehaviour.RIGID));

        register(new JigsawPattern(new ResourceLocation("trail_ruins/roads"), EMPTY, ImmutableList.of(
                Pair.of(JigsawPiece.single("trail_ruins/roads/long_road_end", ruinsRoads), 1),
                Pair.of(JigsawPiece.single("trail_ruins/roads/road_end_1", ruinsRoads), 1),
                Pair.of(JigsawPiece.single("trail_ruins/roads/road_section_1", ruinsRoads), 1),
                Pair.of(JigsawPiece.single("trail_ruins/roads/road_section_2", ruinsRoads), 1),
                Pair.of(JigsawPiece.single("trail_ruins/roads/road_section_3", ruinsRoads), 1),
                Pair.of(JigsawPiece.single("trail_ruins/roads/road_section_4", ruinsRoads), 1),
                Pair.of(JigsawPiece.single("trail_ruins/roads/road_spacer_1", ruinsRoads), 1)
        ), JigsawPattern.PlacementBehaviour.RIGID));

        register(new JigsawPattern(new ResourceLocation("trail_ruins/buildings"), EMPTY, ImmutableList.of(
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_hall_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_hall_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_hall_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_hall_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_hall_5", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/large_room_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/large_room_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/large_room_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/large_room_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/large_room_5", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/one_room_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/one_room_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/one_room_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/one_room_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/one_room_5", ruinsHouses), 1)
        ), JigsawPattern.PlacementBehaviour.RIGID));


        register(new JigsawPattern(new ResourceLocation("trail_ruins/buildings/grouped"), EMPTY, ImmutableList.of(
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_full_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_full_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_full_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_full_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_full_5", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_lower_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_lower_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_lower_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_lower_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_lower_5", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_upper_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_upper_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_upper_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_upper_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_upper_5", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_room_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_room_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_room_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_room_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/buildings/group_room_5", ruinsHouses), 1)
        ), JigsawPattern.PlacementBehaviour.RIGID));


        register(new JigsawPattern(new ResourceLocation("trail_ruins/decor"), EMPTY, ImmutableList.of(
                Pair.of(JigsawPiece.single("trail_ruins/decor/decor_1", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/decor/decor_2", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/decor/decor_3", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/decor/decor_4", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/decor/decor_5", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/decor/decor_6", ruinsHouses), 1),
                Pair.of(JigsawPiece.single("trail_ruins/decor/decor_7", ruinsHouses), 1)
        ), JigsawPattern.PlacementBehaviour.RIGID));
    }

    private static JigsawPattern register(JigsawPattern jigsawPattern) {
        return JigsawPatternRegistry.register(jigsawPattern);
    }

}
