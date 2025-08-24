package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class AutowallCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("autowall")
                .requires((source) -> source.hasPermission(2))
                .executes((context) -> {
                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                    BlockPos villagePos = findVillageAndBuildWall(player);
                    if (villagePos != null) {
                        context.getSource().sendSuccess(new StringTextComponent("Village wall built successfully."), true);
                    } else {
                        context.getSource().sendFailure(new StringTextComponent("No village found within range."));
                    }
                    return 1;
                }));
    }

    private static BlockPos findVillageAndBuildWall(ServerPlayerEntity player) {
        ServerWorld serverWorld = (ServerWorld) player.level;
        BlockPos entityPos = player.blockPosition();

        // Find the nearest village
        BlockPos villagePos = serverWorld.findNearestMapFeature(Structure.VILLAGE, entityPos, 100, false);
        if (villagePos == null) {
            return null;  // No village found within range
        }

        // Get the structure start and bounding box
        StructureStart structureStart = serverWorld.structureFeatureManager().getStructureAt(villagePos, true, Structure.VILLAGE);
        MutableBoundingBox boundingBox = structureStart.getBoundingBox();
        List<StructurePiece> pieces = structureStart.getPieces();

        // Expand the bounding box slightly for the wall placement
        boundingBox = new MutableBoundingBox(
                boundingBox.x0 - 5,
                boundingBox.y0,
                boundingBox.z0 - 5,
                boundingBox.x1 + 5,
                boundingBox.y1,
                boundingBox.z1 + 5
        );

        // Place the wall around the bounding box
        buildVillageWall(serverWorld, boundingBox, villagePos.getY());

        return villagePos;
    }

    private static void buildVillageWall(World world, MutableBoundingBox boundingBox, int baseY) {
        for (int x = boundingBox.x0; x <= boundingBox.x1; x++) {
            for (int z = boundingBox.z0; z <= boundingBox.z1; z++) {
                if (x == boundingBox.x0 || x == boundingBox.x1 || z == boundingBox.z0 || z == boundingBox.z1) {
                    for (int y = baseY; y < baseY + 10; y++) {  // Example height of 10 blocks
                        world.setBlock(new BlockPos(x, y, z), Blocks.STONE_BRICKS.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}