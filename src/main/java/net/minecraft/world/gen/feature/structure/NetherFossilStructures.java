package net.minecraft.world.gen.feature.structure;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.BrushableBlockEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.random.RandomSource;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class NetherFossilStructures {
    private static final ResourceLocation[] FOSSILS = new ResourceLocation[]{new ResourceLocation("nether_fossils/fossil_1"), new ResourceLocation("nether_fossils/fossil_2"), new ResourceLocation("nether_fossils/fossil_3"), new ResourceLocation("nether_fossils/fossil_4"), new ResourceLocation("nether_fossils/fossil_5"), new ResourceLocation("nether_fossils/fossil_6"), new ResourceLocation("nether_fossils/fossil_7"), new ResourceLocation("nether_fossils/fossil_8"), new ResourceLocation("nether_fossils/fossil_9"), new ResourceLocation("nether_fossils/fossil_10"), new ResourceLocation("nether_fossils/fossil_11"), new ResourceLocation("nether_fossils/fossil_12"), new ResourceLocation("nether_fossils/fossil_13"), new ResourceLocation("nether_fossils/fossil_14")};

    public static void addPieces(TemplateManager p_236994_0_, List<StructurePiece> p_236994_1_, Random p_236994_2_, BlockPos p_236994_3_) {
        Rotation rotation = Rotation.getRandom(p_236994_2_);
        p_236994_1_.add(new NetherFossilStructures.Piece(p_236994_0_, Util.getRandom(FOSSILS, p_236994_2_), p_236994_3_, rotation));
    }

    public static class Piece extends TemplateStructurePiece {
        private final ResourceLocation templateLocation;
        private final Rotation rotation;

        public Piece(TemplateManager p_i232108_1_, ResourceLocation p_i232108_2_, BlockPos p_i232108_3_, Rotation p_i232108_4_) {
            super(IStructurePieceType.NETHER_FOSSIL, 0);
            this.templateLocation = p_i232108_2_;
            this.templatePosition = p_i232108_3_;
            this.rotation = p_i232108_4_;
            this.loadTemplate(p_i232108_1_);
        }

        public Piece(TemplateManager p_i232107_1_, CompoundNBT p_i232107_2_) {
            super(IStructurePieceType.NETHER_FOSSIL, p_i232107_2_);
            this.templateLocation = new ResourceLocation(p_i232107_2_.getString("Template"));
            this.rotation = Rotation.valueOf(p_i232107_2_.getString("Rot"));
            this.loadTemplate(p_i232107_1_);
        }

        private void loadTemplate(TemplateManager p_236997_1_) {
            Template template = p_236997_1_.getOrCreate(this.templateLocation);
            PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR);
            this.setup(template, this.templatePosition, placementsettings);
        }

        protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
            super.addAdditionalSaveData(p_143011_1_);
            p_143011_1_.putString("Template", this.templateLocation.toString());
            p_143011_1_.putString("Rot", this.rotation.name());
        }

        protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IServerWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_) {
        }

        public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
            MutableBoundingBox boundingBox2 = this.template.getBoundingBox(placeSettings, templatePosition);
            p_230383_5_.expand(boundingBox2);

            boolean b = super.postProcess(p_230383_1_, p_230383_2_, p_230383_3_, p_230383_4_, p_230383_5_, p_230383_6_, p_230383_7_);
            placeSuspiciousSoulSoil(p_230383_1_, boundingBox2, p_230383_4_, p_230383_5_);
            return b;
        }

        public void placeSuspiciousSoulSoil(ISeedReader world, MutableBoundingBox boundingBox, Random random, MutableBoundingBox boundingBox2) {
            int attempts = 12; // Try multiple times to find good locations
            int placed = 0;
            int maxToPlace = 3 + random.nextInt(2); // Place between 3 and 4

            for (int i = 0; i < attempts && placed < maxToPlace; i++) {
                int x = boundingBox.x0 + random.nextInt(boundingBox.getXSpan());
                int z = boundingBox.z0 + random.nextInt(boundingBox.getZSpan());
                int baseY = boundingBox.y0;

                // Try placing 1 to 2 blocks underground
                int y = baseY - 1 - random.nextInt(2); // y -1 or y -2
                BlockPos pos = new BlockPos(x, y, z);

                if (!boundingBox2.isInside(pos)) continue;

                // Only replace if it's currently soul soil
                if (world.getBlockState(pos).getBlock() != Blocks.SOUL_SOIL) continue;

                // Prefer placements that have a bone block 1 or 2 blocks above
                BlockPos boneCheck1 = pos.above(1);
                BlockPos boneCheck2 = pos.above(2);
                boolean hasBoneAbove = world.getBlockState(boneCheck1).getBlock() == Blocks.BONE_BLOCK ||
                        world.getBlockState(boneCheck2).getBlock() == Blocks.BONE_BLOCK;

                // Check nearby blocks for other soul soil
                boolean nearbySoulSoil = false;
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos neighbor = pos.relative(dir);
                    if (world.getBlockState(neighbor).getBlock() == Blocks.SOUL_SOIL) {
                        nearbySoulSoil = true;
                        break;
                    }
                }

                if (!nearbySoulSoil) continue;

                // Replace with suspicious soul soil
                if (hasBoneAbove || random.nextFloat() < 0.5F) {
                    world.setBlock(pos, Blocks.SUSPICIOUS_SOUL_SOIL.defaultBlockState(), 2);

                    if (world.getBlockEntity(pos) instanceof BrushableBlockEntity brushableBlock) {
                        brushableBlock.setLootTable(LootTables.SOUL_SOIL_ARCHAEOLOGY, RandomSource.create(world.getSeed()).forkPositional().at(pos).nextLong());
                    }
                }

                placed++;
            }
        }

    }
}