package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FortressPieces {
   private static final FortressPieces.PieceWeight[] BRIDGE_PIECE_WEIGHTS = new FortressPieces.PieceWeight[]{new FortressPieces.PieceWeight(FortressPieces.Straight.class, 30, 0, true), new FortressPieces.PieceWeight(FortressPieces.Crossing3.class, 10, 4), new FortressPieces.PieceWeight(FortressPieces.Crossing.class, 10, 4), new FortressPieces.PieceWeight(FortressPieces.Stairs.class, 10, 3), new FortressPieces.PieceWeight(FortressPieces.Throne.class, 5, 2), new FortressPieces.PieceWeight(FortressPieces.Entrance.class, 5, 1)};
   private static final FortressPieces.PieceWeight[] CASTLE_PIECE_WEIGHTS = new FortressPieces.PieceWeight[]{new FortressPieces.PieceWeight(FortressPieces.Corridor5.class, 25, 0, true), new FortressPieces.PieceWeight(FortressPieces.Crossing2.class, 15, 5), new FortressPieces.PieceWeight(FortressPieces.Corridor2.class, 5, 10), new FortressPieces.PieceWeight(FortressPieces.Corridor.class, 5, 10), new FortressPieces.PieceWeight(FortressPieces.Corridor3.class, 10, 3, true), new FortressPieces.PieceWeight(FortressPieces.Corridor4.class, 7, 2), new FortressPieces.PieceWeight(FortressPieces.NetherStalkRoom.class, 5, 2)};

   private static FortressPieces.Piece findAndCreateBridgePieceFactory(FortressPieces.PieceWeight p_175887_0_, List<StructurePiece> p_175887_1_, Random p_175887_2_, int p_175887_3_, int p_175887_4_, int p_175887_5_, Direction p_175887_6_, int p_175887_7_) {
      Class<? extends FortressPieces.Piece> oclass = p_175887_0_.pieceClass;
      FortressPieces.Piece fortresspieces$piece = null;
      if (oclass == FortressPieces.Straight.class) {
         fortresspieces$piece = FortressPieces.Straight.createPiece(p_175887_1_, p_175887_2_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (oclass == FortressPieces.Crossing3.class) {
         fortresspieces$piece = FortressPieces.Crossing3.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (oclass == FortressPieces.Crossing.class) {
         fortresspieces$piece = FortressPieces.Crossing.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (oclass == FortressPieces.Stairs.class) {
         fortresspieces$piece = FortressPieces.Stairs.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_7_, p_175887_6_);
      } else if (oclass == FortressPieces.Throne.class) {
         fortresspieces$piece = FortressPieces.Throne.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_7_, p_175887_6_);
      } else if (oclass == FortressPieces.Entrance.class) {
         fortresspieces$piece = FortressPieces.Entrance.createPiece(p_175887_1_, p_175887_2_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (oclass == FortressPieces.Corridor5.class) {
         fortresspieces$piece = FortressPieces.Corridor5.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (oclass == FortressPieces.Corridor2.class) {
         fortresspieces$piece = FortressPieces.Corridor2.createPiece(p_175887_1_, p_175887_2_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (oclass == FortressPieces.Corridor.class) {
         fortresspieces$piece = FortressPieces.Corridor.createPiece(p_175887_1_, p_175887_2_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (oclass == FortressPieces.Corridor3.class) {
         fortresspieces$piece = FortressPieces.Corridor3.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (oclass == FortressPieces.Corridor4.class) {
         fortresspieces$piece = FortressPieces.Corridor4.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (oclass == FortressPieces.Crossing2.class) {
         fortresspieces$piece = FortressPieces.Crossing2.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (oclass == FortressPieces.NetherStalkRoom.class) {
         fortresspieces$piece = FortressPieces.NetherStalkRoom.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      }

      return fortresspieces$piece;
   }

   public static class Corridor extends FortressPieces.Piece {
      private boolean isNeedingChest;

      public Corridor(int p_i45615_1_, Random p_i45615_2_, MutableBoundingBox p_i45615_3_, Direction p_i45615_4_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, p_i45615_1_);
         this.setOrientation(p_i45615_4_);
         this.boundingBox = p_i45615_3_;
         this.isNeedingChest = p_i45615_2_.nextInt(3) == 0;
      }

      public Corridor(TemplateManager p_i50272_1_, CompoundNBT p_i50272_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, p_i50272_2_);
         this.isNeedingChest = p_i50272_2_.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putBoolean("Chest", this.isNeedingChest);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateChildLeft((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 1, true);
      }

      public static FortressPieces.Corridor createPiece(List<StructurePiece> p_175879_0_, Random p_175879_1_, int p_175879_2_, int p_175879_3_, int p_175879_4_, Direction p_175879_5_, int p_175879_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175879_2_, p_175879_3_, p_175879_4_, -1, 0, 0, 5, 7, 5, p_175879_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175879_0_, mutableboundingbox) == null ? new FortressPieces.Corridor(p_175879_6_, p_175879_1_, mutableboundingbox, p_175879_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(p_230383_1_, p_230383_5_, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 3, 1, 4, 4, 1, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 3, 3, 4, 4, 3, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 4, 1, 4, 4, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 3, 4, 3, 4, 4, blockstate, blockstate, false);
         if (this.isNeedingChest && p_230383_5_.isInside(new BlockPos(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3)))) {
            this.isNeedingChest = false;
            this.createChest(p_230383_1_, p_230383_5_, p_230383_4_, 3, 2, 3, LootTables.NETHER_BRIDGE);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 0; j <= 4; ++j) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_230383_5_);
            }
         }

         return true;
      }
   }

   public static class Corridor2 extends FortressPieces.Piece {
      private boolean isNeedingChest;

      public Corridor2(int p_i45613_1_, Random p_i45613_2_, MutableBoundingBox p_i45613_3_, Direction p_i45613_4_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, p_i45613_1_);
         this.setOrientation(p_i45613_4_);
         this.boundingBox = p_i45613_3_;
         this.isNeedingChest = p_i45613_2_.nextInt(3) == 0;
      }

      public Corridor2(TemplateManager p_i50266_1_, CompoundNBT p_i50266_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, p_i50266_2_);
         this.isNeedingChest = p_i50266_2_.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putBoolean("Chest", this.isNeedingChest);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateChildRight((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 1, true);
      }

      public static FortressPieces.Corridor2 createPiece(List<StructurePiece> p_175876_0_, Random p_175876_1_, int p_175876_2_, int p_175876_3_, int p_175876_4_, Direction p_175876_5_, int p_175876_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175876_2_, p_175876_3_, p_175876_4_, -1, 0, 0, 5, 7, 5, p_175876_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175876_0_, mutableboundingbox) == null ? new FortressPieces.Corridor2(p_175876_6_, p_175876_1_, mutableboundingbox, p_175876_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 1, 0, 4, 1, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 3, 0, 4, 3, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 4, 1, 4, 4, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 3, 4, 3, 4, 4, blockstate, blockstate, false);
         if (this.isNeedingChest && p_230383_5_.isInside(new BlockPos(this.getWorldX(1, 3), this.getWorldY(2), this.getWorldZ(1, 3)))) {
            this.isNeedingChest = false;
            this.createChest(p_230383_1_, p_230383_5_, p_230383_4_, 1, 2, 3, LootTables.NETHER_BRIDGE);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 0; j <= 4; ++j) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_230383_5_);
            }
         }

         return true;
      }
   }

   public static class Corridor3 extends FortressPieces.Piece {
      public Corridor3(int p_i50280_1_, MutableBoundingBox p_i50280_2_, Direction p_i50280_3_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, p_i50280_1_);
         this.setOrientation(p_i50280_3_);
         this.boundingBox = p_i50280_2_;
      }

      public Corridor3(TemplateManager p_i50281_1_, CompoundNBT p_i50281_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, p_i50281_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateChildForward((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 1, 0, true);
      }

      public static FortressPieces.Corridor3 createPiece(List<StructurePiece> p_175883_0_, int p_175883_1_, int p_175883_2_, int p_175883_3_, Direction p_175883_4_, int p_175883_5_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175883_1_, p_175883_2_, p_175883_3_, -1, -7, 0, 5, 14, 10, p_175883_4_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175883_0_, mutableboundingbox) == null ? new FortressPieces.Corridor3(p_175883_5_, mutableboundingbox, p_175883_4_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         BlockState blockstate = Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.SOUTH);
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));

         for(int i = 0; i <= 9; ++i) {
            int j = Math.max(1, 7 - i);
            int k = Math.min(Math.max(j + 5, 14 - i), 13);
            int l = i;
            this.generateBox(p_230383_1_, p_230383_5_, 0, 0, i, 4, j, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(p_230383_1_, p_230383_5_, 1, j + 1, i, 3, k - 1, i, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            if (i <= 6) {
               this.placeBlock(p_230383_1_, blockstate, 1, j + 1, i, p_230383_5_);
               this.placeBlock(p_230383_1_, blockstate, 2, j + 1, i, p_230383_5_);
               this.placeBlock(p_230383_1_, blockstate, 3, j + 1, i, p_230383_5_);
            }

            this.generateBox(p_230383_1_, p_230383_5_, 0, k, i, 4, k, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, j + 1, i, 0, k - 1, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(p_230383_1_, p_230383_5_, 4, j + 1, i, 4, k - 1, i, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            if ((i & 1) == 0) {
               this.generateBox(p_230383_1_, p_230383_5_, 0, j + 2, i, 0, j + 3, i, blockstate1, blockstate1, false);
               this.generateBox(p_230383_1_, p_230383_5_, 4, j + 2, i, 4, j + 3, i, blockstate1, blockstate1, false);
            }

            for(int i1 = 0; i1 <= 4; ++i1) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i1, -1, l, p_230383_5_);
            }
         }

         return true;
      }
   }

   public static class Corridor4 extends FortressPieces.Piece {
      public Corridor4(int p_i50277_1_, MutableBoundingBox p_i50277_2_, Direction p_i50277_3_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, p_i50277_1_);
         this.setOrientation(p_i50277_3_);
         this.boundingBox = p_i50277_2_;
      }

      public Corridor4(TemplateManager p_i50278_1_, CompoundNBT p_i50278_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, p_i50278_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         int i = 1;
         Direction direction = this.getOrientation();
         if (direction == Direction.WEST || direction == Direction.NORTH) {
            i = 5;
         }

         this.generateChildLeft((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, i, p_74861_3_.nextInt(8) > 0);
         this.generateChildRight((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, i, p_74861_3_.nextInt(8) > 0);
      }

      public static FortressPieces.Corridor4 createPiece(List<StructurePiece> p_214814_0_, int p_214814_1_, int p_214814_2_, int p_214814_3_, Direction p_214814_4_, int p_214814_5_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_214814_1_, p_214814_2_, p_214814_3_, -3, 0, 0, 9, 7, 9, p_214814_4_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_214814_0_, mutableboundingbox) == null ? new FortressPieces.Corridor4(p_214814_5_, mutableboundingbox, p_214814_4_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 8, 5, 8, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 0, 1, 4, 0, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 3, 0, 7, 4, 0, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 1, 4, 2, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 4, 7, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 8, 7, 3, 8, blockstate1, blockstate1, false);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 0, 3, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 8, 3, 8, p_230383_5_);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 6, 0, 3, 7, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 8, 3, 6, 8, 3, 7, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 4, 5, 1, 5, 5, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 4, 5, 7, 5, 5, blockstate1, blockstate1, false);

         for(int i = 0; i <= 5; ++i) {
            for(int j = 0; j <= 8; ++j) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), j, -1, i, p_230383_5_);
            }
         }

         return true;
      }
   }

   public static class Corridor5 extends FortressPieces.Piece {
      public Corridor5(int p_i50268_1_, MutableBoundingBox p_i50268_2_, Direction p_i50268_3_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, p_i50268_1_);
         this.setOrientation(p_i50268_3_);
         this.boundingBox = p_i50268_2_;
      }

      public Corridor5(TemplateManager p_i50269_1_, CompoundNBT p_i50269_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, p_i50269_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateChildForward((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 1, 0, true);
      }

      public static FortressPieces.Corridor5 createPiece(List<StructurePiece> p_175877_0_, int p_175877_1_, int p_175877_2_, int p_175877_3_, Direction p_175877_4_, int p_175877_5_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175877_1_, p_175877_2_, p_175877_3_, -1, 0, 0, 5, 7, 5, p_175877_4_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175877_0_, mutableboundingbox) == null ? new FortressPieces.Corridor5(p_175877_5_, mutableboundingbox, p_175877_4_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 1, 0, 4, 1, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 3, 0, 4, 3, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 3, 1, 4, 4, 1, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 3, 3, 4, 4, 3, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 0; j <= 4; ++j) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_230383_5_);
            }
         }

         return true;
      }
   }

   public static class Crossing extends FortressPieces.Piece {
      public Crossing(int p_i50258_1_, MutableBoundingBox p_i50258_2_, Direction p_i50258_3_) {
         super(IStructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, p_i50258_1_);
         this.setOrientation(p_i50258_3_);
         this.boundingBox = p_i50258_2_;
      }

      public Crossing(TemplateManager p_i50259_1_, CompoundNBT p_i50259_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, p_i50259_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateChildForward((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 2, 0, false);
         this.generateChildLeft((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 2, false);
         this.generateChildRight((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 2, false);
      }

      public static FortressPieces.Crossing createPiece(List<StructurePiece> p_175873_0_, int p_175873_1_, int p_175873_2_, int p_175873_3_, Direction p_175873_4_, int p_175873_5_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175873_1_, p_175873_2_, p_175873_3_, -2, 0, 0, 7, 9, 7, p_175873_4_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175873_0_, mutableboundingbox) == null ? new FortressPieces.Crossing(p_175873_5_, mutableboundingbox, p_175873_4_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 6, 7, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(p_230383_1_, p_230383_5_, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 5, 0, 4, 5, 0, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 5, 6, 4, 5, 6, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 5, 2, 0, 5, 4, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 5, 2, 6, 5, 4, blockstate1, blockstate1, false);

         for(int i = 0; i <= 6; ++i) {
            for(int j = 0; j <= 6; ++j) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_230383_5_);
            }
         }

         return true;
      }
   }

   public static class Crossing2 extends FortressPieces.Piece {
      public Crossing2(int p_i50273_1_, MutableBoundingBox p_i50273_2_, Direction p_i50273_3_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, p_i50273_1_);
         this.setOrientation(p_i50273_3_);
         this.boundingBox = p_i50273_2_;
      }

      public Crossing2(TemplateManager p_i50274_1_, CompoundNBT p_i50274_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, p_i50274_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateChildForward((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 1, 0, true);
         this.generateChildLeft((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 1, true);
         this.generateChildRight((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 1, true);
      }

      public static FortressPieces.Crossing2 createPiece(List<StructurePiece> p_175878_0_, int p_175878_1_, int p_175878_2_, int p_175878_3_, Direction p_175878_4_, int p_175878_5_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175878_1_, p_175878_2_, p_175878_3_, -1, 0, 0, 5, 7, 5, p_175878_4_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175878_0_, mutableboundingbox) == null ? new FortressPieces.Crossing2(p_175878_5_, mutableboundingbox, p_175878_4_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 0; j <= 4; ++j) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_230383_5_);
            }
         }

         return true;
      }
   }

   public static class Crossing3 extends FortressPieces.Piece {
      public Crossing3(int p_i50286_1_, MutableBoundingBox p_i50286_2_, Direction p_i50286_3_) {
         super(IStructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, p_i50286_1_);
         this.setOrientation(p_i50286_3_);
         this.boundingBox = p_i50286_2_;
      }

      protected Crossing3(Random p_i2042_1_, int p_i2042_2_, int p_i2042_3_) {
         super(IStructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0);
         this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(p_i2042_1_));
         if (this.getOrientation().getAxis() == Direction.Axis.Z) {
            this.boundingBox = new MutableBoundingBox(p_i2042_2_, 64, p_i2042_3_, p_i2042_2_ + 19 - 1, 73, p_i2042_3_ + 19 - 1);
         } else {
            this.boundingBox = new MutableBoundingBox(p_i2042_2_, 64, p_i2042_3_, p_i2042_2_ + 19 - 1, 73, p_i2042_3_ + 19 - 1);
         }

      }

      protected Crossing3(IStructurePieceType p_i50287_1_, CompoundNBT p_i50287_2_) {
         super(p_i50287_1_, p_i50287_2_);
      }

      public Crossing3(TemplateManager p_i50288_1_, CompoundNBT p_i50288_2_) {
         this(IStructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, p_i50288_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateChildForward((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 8, 3, false);
         this.generateChildLeft((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 3, 8, false);
         this.generateChildRight((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 3, 8, false);
      }

      public static FortressPieces.Crossing3 createPiece(List<StructurePiece> p_175885_0_, int p_175885_1_, int p_175885_2_, int p_175885_3_, Direction p_175885_4_, int p_175885_5_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175885_1_, p_175885_2_, p_175885_3_, -8, -3, 0, 19, 10, 19, p_175885_4_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175885_0_, mutableboundingbox) == null ? new FortressPieces.Crossing3(p_175885_5_, mutableboundingbox, p_175885_4_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 8, 5, 0, 10, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 5, 8, 18, 7, 10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 7; i <= 11; ++i) {
            for(int j = 0; j <= 2; ++j) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_230383_5_);
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, 18 - j, p_230383_5_);
            }
         }

         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int k = 0; k <= 2; ++k) {
            for(int l = 7; l <= 11; ++l) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), k, -1, l, p_230383_5_);
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), 18 - k, -1, l, p_230383_5_);
            }
         }

         return true;
      }
   }

   public static class End extends FortressPieces.Piece {
      private final int selfSeed;

      public End(int p_i45621_1_, Random p_i45621_2_, MutableBoundingBox p_i45621_3_, Direction p_i45621_4_) {
         super(IStructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, p_i45621_1_);
         this.setOrientation(p_i45621_4_);
         this.boundingBox = p_i45621_3_;
         this.selfSeed = p_i45621_2_.nextInt();
      }

      public End(TemplateManager p_i50285_1_, CompoundNBT p_i50285_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, p_i50285_2_);
         this.selfSeed = p_i50285_2_.getInt("Seed");
      }

      public static FortressPieces.End createPiece(List<StructurePiece> p_175884_0_, Random p_175884_1_, int p_175884_2_, int p_175884_3_, int p_175884_4_, Direction p_175884_5_, int p_175884_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175884_2_, p_175884_3_, p_175884_4_, -1, -3, 0, 5, 10, 8, p_175884_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175884_0_, mutableboundingbox) == null ? new FortressPieces.End(p_175884_6_, p_175884_1_, mutableboundingbox, p_175884_5_) : null;
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putInt("Seed", this.selfSeed);
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         Random random = new Random((long)this.selfSeed);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 3; j <= 4; ++j) {
               int k = random.nextInt(8);
               this.generateBox(p_230383_1_, p_230383_5_, i, j, 0, i, j, k, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }
         }

         int l = random.nextInt(8);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 5, 0, 0, 5, l, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         l = random.nextInt(8);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 5, 0, 4, 5, l, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i1 = 0; i1 <= 4; ++i1) {
            int k1 = random.nextInt(5);
            this.generateBox(p_230383_1_, p_230383_5_, i1, 2, 0, i1, 2, k1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         }

         for(int j1 = 0; j1 <= 4; ++j1) {
            for(int l1 = 0; l1 <= 1; ++l1) {
               int i2 = random.nextInt(3);
               this.generateBox(p_230383_1_, p_230383_5_, j1, l1, 0, j1, l1, i2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }
         }

         return true;
      }
   }

   public static class Entrance extends FortressPieces.Piece {
      public Entrance(int p_i45617_1_, Random p_i45617_2_, MutableBoundingBox p_i45617_3_, Direction p_i45617_4_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, p_i45617_1_);
         this.setOrientation(p_i45617_4_);
         this.boundingBox = p_i45617_3_;
      }

      public Entrance(TemplateManager p_i50276_1_, CompoundNBT p_i50276_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, p_i50276_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateChildForward((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 5, 3, true);
      }

      public static FortressPieces.Entrance createPiece(List<StructurePiece> p_175881_0_, Random p_175881_1_, int p_175881_2_, int p_175881_3_, int p_175881_4_, Direction p_175881_5_, int p_175881_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175881_2_, p_175881_3_, p_175881_4_, -5, -3, 0, 13, 14, 13, p_175881_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175881_0_, mutableboundingbox) == null ? new FortressPieces.Entrance(p_175881_6_, p_175881_1_, mutableboundingbox, p_175881_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.defaultBlockState(), Blocks.NETHER_BRICK_FENCE.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));

         for(int i = 1; i <= 11; i += 2) {
            this.generateBox(p_230383_1_, p_230383_5_, i, 10, 0, i, 11, 0, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, i, 10, 12, i, 11, 12, blockstate, blockstate, false);
            this.generateBox(p_230383_1_, p_230383_5_, 0, 10, i, 0, 11, i, blockstate1, blockstate1, false);
            this.generateBox(p_230383_1_, p_230383_5_, 12, 10, i, 12, 11, i, blockstate1, blockstate1, false);
            this.placeBlock(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 0, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 12, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, i, p_230383_5_);
            this.placeBlock(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, i, p_230383_5_);
            if (i != 11) {
               this.placeBlock(p_230383_1_, blockstate, i + 1, 13, 0, p_230383_5_);
               this.placeBlock(p_230383_1_, blockstate, i + 1, 13, 12, p_230383_5_);
               this.placeBlock(p_230383_1_, blockstate1, 0, 13, i + 1, p_230383_5_);
               this.placeBlock(p_230383_1_, blockstate1, 12, 13, i + 1, p_230383_5_);
            }
         }

         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 0, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 12, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 12, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 0, p_230383_5_);

         for(int k = 3; k <= 9; k += 2) {
            this.generateBox(p_230383_1_, p_230383_5_, 1, 7, k, 1, 8, k, blockstate1.setValue(FenceBlock.WEST, Boolean.valueOf(true)), blockstate1.setValue(FenceBlock.WEST, Boolean.valueOf(true)), false);
            this.generateBox(p_230383_1_, p_230383_5_, 11, 7, k, 11, 8, k, blockstate1.setValue(FenceBlock.EAST, Boolean.valueOf(true)), blockstate1.setValue(FenceBlock.EAST, Boolean.valueOf(true)), false);
         }

         this.generateBox(p_230383_1_, p_230383_5_, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int l = 4; l <= 8; ++l) {
            for(int j = 0; j <= 2; ++j) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), l, -1, j, p_230383_5_);
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), l, -1, 12 - j, p_230383_5_);
            }
         }

         for(int i1 = 0; i1 <= 2; ++i1) {
            for(int j1 = 4; j1 <= 8; ++j1) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i1, -1, j1, p_230383_5_);
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - i1, -1, j1, p_230383_5_);
            }
         }

         this.generateBox(p_230383_1_, p_230383_5_, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 1, 6, 6, 4, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), 6, 0, 6, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.LAVA.defaultBlockState(), 6, 5, 6, p_230383_5_);
         BlockPos blockpos = new BlockPos(this.getWorldX(6, 6), this.getWorldY(5), this.getWorldZ(6, 6));
         if (p_230383_5_.isInside(blockpos)) {
            p_230383_1_.getLiquidTicks().scheduleTick(blockpos, Fluids.LAVA, 0);
         }

         return true;
      }
   }

   public static class NetherStalkRoom extends FortressPieces.Piece {
      public NetherStalkRoom(int p_i50264_1_, MutableBoundingBox p_i50264_2_, Direction p_i50264_3_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, p_i50264_1_);
         this.setOrientation(p_i50264_3_);
         this.boundingBox = p_i50264_2_;
      }

      public NetherStalkRoom(TemplateManager p_i50265_1_, CompoundNBT p_i50265_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, p_i50265_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateChildForward((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 5, 3, true);
         this.generateChildForward((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 5, 11, true);
      }

      public static FortressPieces.NetherStalkRoom createPiece(List<StructurePiece> p_175875_0_, int p_175875_1_, int p_175875_2_, int p_175875_3_, Direction p_175875_4_, int p_175875_5_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175875_1_, p_175875_2_, p_175875_3_, -5, -3, 0, 13, 14, 13, p_175875_4_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175875_0_, mutableboundingbox) == null ? new FortressPieces.NetherStalkRoom(p_175875_5_, mutableboundingbox, p_175875_4_) : null;
      }

      public boolean postProcess(ISeedReader world, StructureManager manager, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox mutableBoundingBox, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(world, mutableBoundingBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         BlockState blockstate2 = blockstate1.setValue(FenceBlock.WEST, Boolean.valueOf(true));
         BlockState blockstate3 = blockstate1.setValue(FenceBlock.EAST, Boolean.valueOf(true));

         for(int i = 1; i <= 11; i += 2) {
            this.generateBox(world, mutableBoundingBox, i, 10, 0, i, 11, 0, blockstate, blockstate, false);
            this.generateBox(world, mutableBoundingBox, i, 10, 12, i, 11, 12, blockstate, blockstate, false);
            this.generateBox(world, mutableBoundingBox, 0, 10, i, 0, 11, i, blockstate1, blockstate1, false);
            this.generateBox(world, mutableBoundingBox, 12, 10, i, 12, 11, i, blockstate1, blockstate1, false);
            this.placeBlock(world, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 0, mutableBoundingBox);
            this.placeBlock(world, Blocks.NETHER_BRICKS.defaultBlockState(), i, 13, 12, mutableBoundingBox);
            this.placeBlock(world, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, i, mutableBoundingBox);
            this.placeBlock(world, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, i, mutableBoundingBox);
            if (i != 11) {
               this.placeBlock(world, blockstate, i + 1, 13, 0, mutableBoundingBox);
               this.placeBlock(world, blockstate, i + 1, 13, 12, mutableBoundingBox);
               this.placeBlock(world, blockstate1, 0, 13, i + 1, mutableBoundingBox);
               this.placeBlock(world, blockstate1, 12, 13, i + 1, mutableBoundingBox);
            }
         }

         this.placeBlock(world, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 0, mutableBoundingBox);
         this.placeBlock(world, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 12, mutableBoundingBox);
         this.placeBlock(world, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 12, mutableBoundingBox);
         this.placeBlock(world, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 0, mutableBoundingBox);

         for(int j1 = 3; j1 <= 9; j1 += 2) {
            this.generateBox(world, mutableBoundingBox, 1, 7, j1, 1, 8, j1, blockstate2, blockstate2, false);
            this.generateBox(world, mutableBoundingBox, 11, 7, j1, 11, 8, j1, blockstate3, blockstate3, false);
         }

         BlockState blockstate4 = Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairsBlock.FACING, Direction.NORTH);

         for(int j = 0; j <= 6; ++j) {
            int k = j + 4;

            for(int l = 5; l <= 7; ++l) {
               this.placeBlock(world, blockstate4, l, 5 + j, k, mutableBoundingBox);
            }

            if (k >= 5 && k <= 8) {
               this.generateBox(world, mutableBoundingBox, 5, 5, k, 7, j + 4, k, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            } else if (k >= 9 && k <= 10) {
               this.generateBox(world, mutableBoundingBox, 5, 8, k, 7, j + 4, k, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }

            if (j >= 1) {
               this.generateBox(world, mutableBoundingBox, 5, 6 + j, k, 7, 9 + j, k, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            }
         }

         for(int k1 = 5; k1 <= 7; ++k1) {
            this.placeBlock(world, blockstate4, k1, 12, 11, mutableBoundingBox);
         }

         this.generateBox(world, mutableBoundingBox, 5, 6, 7, 5, 7, 7, blockstate3, blockstate3, false);
         this.generateBox(world, mutableBoundingBox, 7, 6, 7, 7, 7, 7, blockstate2, blockstate2, false);
         this.generateBox(world, mutableBoundingBox, 5, 13, 12, 7, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState blockstate5 = blockstate4.setValue(StairsBlock.FACING, Direction.EAST);
         BlockState blockstate6 = blockstate4.setValue(StairsBlock.FACING, Direction.WEST);
         this.placeBlock(world, blockstate6, 4, 5, 2, mutableBoundingBox);
         this.placeBlock(world, blockstate6, 4, 5, 3, mutableBoundingBox);
         this.placeBlock(world, blockstate6, 4, 5, 9, mutableBoundingBox);
         this.placeBlock(world, blockstate6, 4, 5, 10, mutableBoundingBox);
         this.placeBlock(world, blockstate5, 8, 5, 2, mutableBoundingBox);
         this.placeBlock(world, blockstate5, 8, 5, 3, mutableBoundingBox);
         this.placeBlock(world, blockstate5, 8, 5, 9, mutableBoundingBox);
         this.placeBlock(world, blockstate5, 8, 5, 10, mutableBoundingBox);
         this.generateBox(world, mutableBoundingBox, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(world, mutableBoundingBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int l1 = 4; l1 <= 8; ++l1) {
            for(int i1 = 0; i1 <= 2; ++i1) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultBlockState(), l1, -1, i1, mutableBoundingBox);
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultBlockState(), l1, -1, 12 - i1, mutableBoundingBox);
            }
         }

         for(int i2 = 0; i2 <= 2; ++i2) {
            for(int j2 = 4; j2 <= 8; ++j2) {
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultBlockState(), i2, -1, j2, mutableBoundingBox);
               this.fillColumnDown(world, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - i2, -1, j2, mutableBoundingBox);
            }
         }

         return true;
      }
   }

   abstract static class Piece extends StructurePiece {
      protected Piece(IStructurePieceType p_i50260_1_, int p_i50260_2_) {
         super(p_i50260_1_, p_i50260_2_);
      }

      public Piece(IStructurePieceType p_i50261_1_, CompoundNBT p_i50261_2_) {
         super(p_i50261_1_, p_i50261_2_);
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
      }

      private int updatePieceWeight(List<FortressPieces.PieceWeight> p_74960_1_) {
         boolean flag = false;
         int i = 0;

         for(FortressPieces.PieceWeight fortresspieces$pieceweight : p_74960_1_) {
            if (fortresspieces$pieceweight.maxPlaceCount > 0 && fortresspieces$pieceweight.placeCount < fortresspieces$pieceweight.maxPlaceCount) {
               flag = true;
            }

            i += fortresspieces$pieceweight.weight;
         }

         return flag ? i : -1;
      }

      private FortressPieces.Piece generatePiece(FortressPieces.Start p_175871_1_, List<FortressPieces.PieceWeight> p_175871_2_, List<StructurePiece> p_175871_3_, Random p_175871_4_, int p_175871_5_, int p_175871_6_, int p_175871_7_, Direction p_175871_8_, int p_175871_9_) {
         int i = this.updatePieceWeight(p_175871_2_);
         boolean flag = i > 0 && p_175871_9_ <= 30;
         int j = 0;

         while(j < 5 && flag) {
            ++j;
            int k = p_175871_4_.nextInt(i);

            for(FortressPieces.PieceWeight fortresspieces$pieceweight : p_175871_2_) {
               k -= fortresspieces$pieceweight.weight;
               if (k < 0) {
                  if (!fortresspieces$pieceweight.doPlace(p_175871_9_) || fortresspieces$pieceweight == p_175871_1_.previousPiece && !fortresspieces$pieceweight.allowInRow) {
                     break;
                  }

                  FortressPieces.Piece fortresspieces$piece = FortressPieces.findAndCreateBridgePieceFactory(fortresspieces$pieceweight, p_175871_3_, p_175871_4_, p_175871_5_, p_175871_6_, p_175871_7_, p_175871_8_, p_175871_9_);
                  if (fortresspieces$piece != null) {
                     ++fortresspieces$pieceweight.placeCount;
                     p_175871_1_.previousPiece = fortresspieces$pieceweight;
                     if (!fortresspieces$pieceweight.isValid()) {
                        p_175871_2_.remove(fortresspieces$pieceweight);
                     }

                     return fortresspieces$piece;
                  }
               }
            }
         }

         return FortressPieces.End.createPiece(p_175871_3_, p_175871_4_, p_175871_5_, p_175871_6_, p_175871_7_, p_175871_8_, p_175871_9_);
      }

      private StructurePiece generateAndAddPiece(FortressPieces.Start p_175870_1_, List<StructurePiece> p_175870_2_, Random p_175870_3_, int p_175870_4_, int p_175870_5_, int p_175870_6_, @Nullable Direction p_175870_7_, int p_175870_8_, boolean p_175870_9_) {
         if (Math.abs(p_175870_4_ - p_175870_1_.getBoundingBox().x0) <= 112 && Math.abs(p_175870_6_ - p_175870_1_.getBoundingBox().z0) <= 112) {
            List<FortressPieces.PieceWeight> list = p_175870_1_.availableBridgePieces;
            if (p_175870_9_) {
               list = p_175870_1_.availableCastlePieces;
            }

            StructurePiece structurepiece = this.generatePiece(p_175870_1_, list, p_175870_2_, p_175870_3_, p_175870_4_, p_175870_5_, p_175870_6_, p_175870_7_, p_175870_8_ + 1);
            if (structurepiece != null) {
               p_175870_2_.add(structurepiece);
               p_175870_1_.pendingChildren.add(structurepiece);
            }

            return structurepiece;
         } else {
            return FortressPieces.End.createPiece(p_175870_2_, p_175870_3_, p_175870_4_, p_175870_5_, p_175870_6_, p_175870_7_, p_175870_8_);
         }
      }

      @Nullable
      protected StructurePiece generateChildForward(FortressPieces.Start p_74963_1_, List<StructurePiece> p_74963_2_, Random p_74963_3_, int p_74963_4_, int p_74963_5_, boolean p_74963_6_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return this.generateAndAddPiece(p_74963_1_, p_74963_2_, p_74963_3_, this.boundingBox.x0 + p_74963_4_, this.boundingBox.y0 + p_74963_5_, this.boundingBox.z0 - 1, direction, this.getGenDepth(), p_74963_6_);
            case SOUTH:
               return this.generateAndAddPiece(p_74963_1_, p_74963_2_, p_74963_3_, this.boundingBox.x0 + p_74963_4_, this.boundingBox.y0 + p_74963_5_, this.boundingBox.z1 + 1, direction, this.getGenDepth(), p_74963_6_);
            case WEST:
               return this.generateAndAddPiece(p_74963_1_, p_74963_2_, p_74963_3_, this.boundingBox.x0 - 1, this.boundingBox.y0 + p_74963_5_, this.boundingBox.z0 + p_74963_4_, direction, this.getGenDepth(), p_74963_6_);
            case EAST:
               return this.generateAndAddPiece(p_74963_1_, p_74963_2_, p_74963_3_, this.boundingBox.x1 + 1, this.boundingBox.y0 + p_74963_5_, this.boundingBox.z0 + p_74963_4_, direction, this.getGenDepth(), p_74963_6_);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateChildLeft(FortressPieces.Start p_74961_1_, List<StructurePiece> p_74961_2_, Random p_74961_3_, int p_74961_4_, int p_74961_5_, boolean p_74961_6_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return this.generateAndAddPiece(p_74961_1_, p_74961_2_, p_74961_3_, this.boundingBox.x0 - 1, this.boundingBox.y0 + p_74961_4_, this.boundingBox.z0 + p_74961_5_, Direction.WEST, this.getGenDepth(), p_74961_6_);
            case SOUTH:
               return this.generateAndAddPiece(p_74961_1_, p_74961_2_, p_74961_3_, this.boundingBox.x0 - 1, this.boundingBox.y0 + p_74961_4_, this.boundingBox.z0 + p_74961_5_, Direction.WEST, this.getGenDepth(), p_74961_6_);
            case WEST:
               return this.generateAndAddPiece(p_74961_1_, p_74961_2_, p_74961_3_, this.boundingBox.x0 + p_74961_5_, this.boundingBox.y0 + p_74961_4_, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth(), p_74961_6_);
            case EAST:
               return this.generateAndAddPiece(p_74961_1_, p_74961_2_, p_74961_3_, this.boundingBox.x0 + p_74961_5_, this.boundingBox.y0 + p_74961_4_, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth(), p_74961_6_);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateChildRight(FortressPieces.Start p_74965_1_, List<StructurePiece> p_74965_2_, Random p_74965_3_, int p_74965_4_, int p_74965_5_, boolean p_74965_6_) {
         Direction direction = this.getOrientation();
         if (direction != null) {
            switch(direction) {
            case NORTH:
               return this.generateAndAddPiece(p_74965_1_, p_74965_2_, p_74965_3_, this.boundingBox.x1 + 1, this.boundingBox.y0 + p_74965_4_, this.boundingBox.z0 + p_74965_5_, Direction.EAST, this.getGenDepth(), p_74965_6_);
            case SOUTH:
               return this.generateAndAddPiece(p_74965_1_, p_74965_2_, p_74965_3_, this.boundingBox.x1 + 1, this.boundingBox.y0 + p_74965_4_, this.boundingBox.z0 + p_74965_5_, Direction.EAST, this.getGenDepth(), p_74965_6_);
            case WEST:
               return this.generateAndAddPiece(p_74965_1_, p_74965_2_, p_74965_3_, this.boundingBox.x0 + p_74965_5_, this.boundingBox.y0 + p_74965_4_, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth(), p_74965_6_);
            case EAST:
               return this.generateAndAddPiece(p_74965_1_, p_74965_2_, p_74965_3_, this.boundingBox.x0 + p_74965_5_, this.boundingBox.y0 + p_74965_4_, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth(), p_74965_6_);
            }
         }

         return null;
      }

      protected static boolean isOkBox(MutableBoundingBox p_74964_0_) {
         return p_74964_0_ != null && p_74964_0_.y0 > 10;
      }
   }

   static class PieceWeight {
      public final Class<? extends FortressPieces.Piece> pieceClass;
      public final int weight;
      public int placeCount;
      public final int maxPlaceCount;
      public final boolean allowInRow;

      public PieceWeight(Class<? extends FortressPieces.Piece> p_i2055_1_, int p_i2055_2_, int p_i2055_3_, boolean p_i2055_4_) {
         this.pieceClass = p_i2055_1_;
         this.weight = p_i2055_2_;
         this.maxPlaceCount = p_i2055_3_;
         this.allowInRow = p_i2055_4_;
      }

      public PieceWeight(Class<? extends FortressPieces.Piece> p_i2056_1_, int p_i2056_2_, int p_i2056_3_) {
         this(p_i2056_1_, p_i2056_2_, p_i2056_3_, false);
      }

      public boolean doPlace(int p_78822_1_) {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }

      public boolean isValid() {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }
   }

   public static class Stairs extends FortressPieces.Piece {
      public Stairs(int p_i50255_1_, MutableBoundingBox p_i50255_2_, Direction p_i50255_3_) {
         super(IStructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, p_i50255_1_);
         this.setOrientation(p_i50255_3_);
         this.boundingBox = p_i50255_2_;
      }

      public Stairs(TemplateManager p_i50256_1_, CompoundNBT p_i50256_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, p_i50256_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateChildRight((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 6, 2, false);
      }

      public static FortressPieces.Stairs createPiece(List<StructurePiece> p_175872_0_, int p_175872_1_, int p_175872_2_, int p_175872_3_, int p_175872_4_, Direction p_175872_5_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175872_1_, p_175872_2_, p_175872_3_, -2, 0, 0, 7, 11, 7, p_175872_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175872_0_, mutableboundingbox) == null ? new FortressPieces.Stairs(p_175872_4_, mutableboundingbox, p_175872_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 6, 10, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 2, 0, 5, 4, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 3, 2, 6, 5, 2, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 3, 4, 6, 5, 4, blockstate1, blockstate1, false);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), 5, 2, 5, p_230383_5_);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 8, 2, 6, 8, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 5, 0, 4, 5, 0, blockstate, blockstate, false);

         for(int i = 0; i <= 6; ++i) {
            for(int j = 0; j <= 6; ++j) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_230383_5_);
            }
         }

         return true;
      }
   }

   public static class Start extends FortressPieces.Crossing3 {
      public FortressPieces.PieceWeight previousPiece;
      public List<FortressPieces.PieceWeight> availableBridgePieces;
      public List<FortressPieces.PieceWeight> availableCastlePieces;
      public final List<StructurePiece> pendingChildren = Lists.newArrayList();

      public Start(Random p_i2059_1_, int p_i2059_2_, int p_i2059_3_) {
         super(p_i2059_1_, p_i2059_2_, p_i2059_3_);
         this.availableBridgePieces = Lists.newArrayList();

         for(FortressPieces.PieceWeight fortresspieces$pieceweight : FortressPieces.BRIDGE_PIECE_WEIGHTS) {
            fortresspieces$pieceweight.placeCount = 0;
            this.availableBridgePieces.add(fortresspieces$pieceweight);
         }

         this.availableCastlePieces = Lists.newArrayList();

         for(FortressPieces.PieceWeight fortresspieces$pieceweight1 : FortressPieces.CASTLE_PIECE_WEIGHTS) {
            fortresspieces$pieceweight1.placeCount = 0;
            this.availableCastlePieces.add(fortresspieces$pieceweight1);
         }

      }

      public Start(TemplateManager p_i50253_1_, CompoundNBT p_i50253_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_START, p_i50253_2_);
      }
   }

   public static class Straight extends FortressPieces.Piece {
      public Straight(int p_i45620_1_, Random p_i45620_2_, MutableBoundingBox p_i45620_3_, Direction p_i45620_4_) {
         super(IStructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, p_i45620_1_);
         this.setOrientation(p_i45620_4_);
         this.boundingBox = p_i45620_3_;
      }

      public Straight(TemplateManager p_i50283_1_, CompoundNBT p_i50283_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, p_i50283_2_);
      }

      public void addChildren(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.generateChildForward((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 1, 3, false);
      }

      public static FortressPieces.Straight createPiece(List<StructurePiece> p_175882_0_, Random p_175882_1_, int p_175882_2_, int p_175882_3_, int p_175882_4_, Direction p_175882_5_, int p_175882_6_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175882_2_, p_175882_3_, p_175882_4_, -1, -3, 0, 5, 10, 19, p_175882_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175882_0_, mutableboundingbox) == null ? new FortressPieces.Straight(p_175882_6_, p_175882_1_, mutableboundingbox, p_175882_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 5, 0, 3, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int i = 0; i <= 4; ++i) {
            for(int j = 0; j <= 2; ++j) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_230383_5_);
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, 18 - j, p_230383_5_);
            }
         }

         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         BlockState blockstate2 = blockstate1.setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate = blockstate1.setValue(FenceBlock.WEST, Boolean.valueOf(true));
         this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 1, 0, 4, 1, blockstate2, blockstate2, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 4, 0, 4, 4, blockstate2, blockstate2, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 3, 14, 0, 4, 14, blockstate2, blockstate2, false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 1, 17, 0, 4, 17, blockstate2, blockstate2, false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 1, 4, 4, 1, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 3, 4, 4, 4, 4, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 3, 14, 4, 4, 14, blockstate, blockstate, false);
         this.generateBox(p_230383_1_, p_230383_5_, 4, 1, 17, 4, 4, 17, blockstate, blockstate, false);
         return true;
      }
   }

   public static class Throne extends FortressPieces.Piece {
      private boolean hasPlacedSpawner;

      public Throne(int p_i50262_1_, MutableBoundingBox p_i50262_2_, Direction p_i50262_3_) {
         super(IStructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, p_i50262_1_);
         this.setOrientation(p_i50262_3_);
         this.boundingBox = p_i50262_2_;
      }

      public Throne(TemplateManager p_i50263_1_, CompoundNBT p_i50263_2_) {
         super(IStructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, p_i50263_2_);
         this.hasPlacedSpawner = p_i50263_2_.getBoolean("Mob");
      }

      protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
         super.addAdditionalSaveData(p_143011_1_);
         p_143011_1_.putBoolean("Mob", this.hasPlacedSpawner);
      }

      public static FortressPieces.Throne createPiece(List<StructurePiece> p_175874_0_, int p_175874_1_, int p_175874_2_, int p_175874_3_, int p_175874_4_, Direction p_175874_5_) {
         MutableBoundingBox mutableboundingbox = MutableBoundingBox.orientBox(p_175874_1_, p_175874_2_, p_175874_3_, -2, 0, 0, 7, 8, 9, p_175874_5_);
         return isOkBox(mutableboundingbox) && StructurePiece.findCollisionPiece(p_175874_0_, mutableboundingbox) == null ? new FortressPieces.Throne(p_175874_4_, mutableboundingbox, p_175874_5_) : null;
      }

      public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
         this.generateBox(p_230383_1_, p_230383_5_, 0, 2, 0, 6, 7, 7, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState blockstate = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState blockstate1 = Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)), 1, 6, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)), 5, 6, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)).setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 0, 6, 3, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 6, 6, 3, p_230383_5_);
         this.generateBox(p_230383_1_, p_230383_5_, 0, 6, 4, 0, 6, 7, blockstate1, blockstate1, false);
         this.generateBox(p_230383_1_, p_230383_5_, 6, 6, 4, 6, 6, 7, blockstate1, blockstate1, false);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 0, 6, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 6, 6, 8, p_230383_5_);
         this.generateBox(p_230383_1_, p_230383_5_, 1, 6, 8, 5, 6, 8, blockstate, blockstate, false);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)), 1, 7, 8, p_230383_5_);
         this.generateBox(p_230383_1_, p_230383_5_, 2, 7, 8, 4, 7, 8, blockstate, blockstate, false);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)), 5, 7, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)), 2, 8, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, blockstate, 3, 8, 8, p_230383_5_);
         this.placeBlock(p_230383_1_, Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)), 4, 8, 8, p_230383_5_);
         if (!this.hasPlacedSpawner) {
            BlockPos blockpos = new BlockPos(this.getWorldX(3, 5), this.getWorldY(5), this.getWorldZ(3, 5));
            if (p_230383_5_.isInside(blockpos)) {
               this.hasPlacedSpawner = true;
               p_230383_1_.setBlock(blockpos, Blocks.SPAWNER.defaultBlockState(), 2);
               TileEntity tileentity = p_230383_1_.getBlockEntity(blockpos);
               if (tileentity instanceof MobSpawnerTileEntity) {
                  ((MobSpawnerTileEntity)tileentity).getSpawner().setEntityId(EntityType.BLAZE);
               }
            }
         }

         for(int i = 0; i <= 6; ++i) {
            for(int j = 0; j <= 6; ++j) {
               this.fillColumnDown(p_230383_1_, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, p_230383_5_);
            }
         }

         return true;
      }
   }
}