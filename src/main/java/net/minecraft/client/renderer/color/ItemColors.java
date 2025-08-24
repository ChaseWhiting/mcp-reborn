package net.minecraft.client.renderer.color;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.bundle.BundleItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.dyeable.PaintTubeItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemColors {
   private final ObjectIntIdentityMap<IItemColor> itemColors = new ObjectIntIdentityMap<>(32);

   public static ItemColors createDefault(BlockColors p_186729_0_) {
      ItemColors itemcolors = new ItemColors();
      itemcolors.register((p_210239_0_, p_210239_1_) -> {
         return p_210239_1_ > 0 ? -1 : ((IDyeableArmorItem)p_210239_0_.getItem()).getColor(p_210239_0_);
      }, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR);

      for (Item item : PaintTubeItem.PAINT_TUBES) {
         itemcolors.register((stack, i) -> {
            return i > 0 ? -1 : switch (((PaintTubeItem)stack.getItem()).getDyeColor().getId()) {
               case 0 -> 0xF9FFFE;
               case 1 -> 0xF9801D;
               case 2 -> 0xC74EBD;
               case 3 -> 0x3AB3DA;
               case 4 -> 0xFED83D;
               case 5 -> 0x80C71F;
               case 6 -> 0xF38BAA;
               case 7 -> 0x474F52;
               case 8 -> 0x9D9D97;
               case 9 -> 0x169C9C;
               case 10 -> 0x8932B8;
               case 11 -> 0x3C44AA;
               case 12 -> 0x835432;
               case 13 -> 0x5E7C16;
               case 14 -> 0xB02E26;
               case 15 -> 0x1D1D21;
               default -> 0xFFFFFF;
            };
         }, item);
      }

      itemcolors.register((p_210236_0_, p_210236_1_) -> {
         return GrassColors.get(0.5D, 1.0D);
      }, Blocks.TALL_GRASS, Blocks.LARGE_FERN);
      itemcolors.register((p_210241_0_, p_210241_1_) -> {
         if (p_210241_1_ != 1) {
            return -1;
         } else {
            CompoundNBT compoundnbt = p_210241_0_.getTagElement("Explosion");
            int[] aint = compoundnbt != null && compoundnbt.contains("Colors", 11) ? compoundnbt.getIntArray("Colors") : null;
            if (aint != null && aint.length != 0) {
               if (aint.length == 1) {
                  return aint[0];
               } else {
                  int i = 0;
                  int j = 0;
                  int k = 0;

                  for(int l : aint) {
                     i += (l & 16711680) >> 16;
                     j += (l & '\uff00') >> 8;
                     k += (l & 255) >> 0;
                  }

                  i = i / aint.length;
                  j = j / aint.length;
                  k = k / aint.length;
                  return i << 16 | j << 8 | k;
               }
            } else {
               return 9079434;
            }
         }
      }, Items.FIREWORK_STAR);
      itemcolors.register((p_210238_0_, p_210238_1_) -> {
         return p_210238_1_ > 0 ? -1 : PotionUtils.getColor(p_210238_0_);
      }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);

      for(SpawnEggItem spawneggitem : SpawnEggItem.eggs()) {
         itemcolors.register((p_198141_1_, p_198141_2_) -> {
            return spawneggitem.getColor(p_198141_2_);
         }, spawneggitem);
      }

      itemcolors.register((p_210235_1_, p_210235_2_) -> {
         BlockState blockstate = ((BlockItem)p_210235_1_.getItem()).getBlock().defaultBlockState();
         return p_186729_0_.getColor(blockstate, (IBlockDisplayReader)null, (BlockPos)null, p_210235_2_);
      }, Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
      itemcolors.register((stack, v1) -> {
         return v1 == 0 ? PotionUtils.getColor(stack) : -1;
      }, Items.TIPPED_ARROW);

      itemcolors.register((p_210237_0_, p_210237_1_) -> {
         return p_210237_1_ == 0 ? -1 : FilledMapItem.getColor(p_210237_0_);
      }, Items.FILLED_MAP);



//      itemcolors.register((stack, tintIndex) -> {
//         if (tintIndex == 1) {  // layer1 (the overlay)
//            int baseColor = BundleItem.getBarColor();
//            double weightPercentage = getBundleWeightPercentage(stack);
//
//            // Extract RGB components from baseColor
//            int red = (baseColor >> 16) & 0xFF;
//            int green = (baseColor >> 8) & 0xFF;
//            int blue = baseColor & 0xFF;
//
//            // Apply the weight percentage to the color intensity
//            red = (int) (red * weightPercentage);
//            green = (int) (green * weightPercentage);
//            blue = (int) (blue * weightPercentage);
//
//            // Recombine the RGB components into a single int color
//            return (red << 16) | (green << 8) | blue;
//         }
//         return -1;  // No tint for layer0
//      }, Items.BUNDLE);



      return itemcolors;
   }

   public static int getBundleWeightPercentage(ItemStack stack) {
      return BundleItem.getContentWeight(stack);
   }

   public int getColor(ItemStack p_186728_1_, int p_186728_2_) {
      IItemColor iitemcolor = this.itemColors.byId(Registry.ITEM.getId(p_186728_1_.getItem()));
      return iitemcolor == null ? -1 : iitemcolor.getColor(p_186728_1_, p_186728_2_);
   }

   public void register(IItemColor p_199877_1_, IItemProvider... p_199877_2_) {
      for(IItemProvider iitemprovider : p_199877_2_) {
         this.itemColors.addMapping(p_199877_1_, Item.getId(iitemprovider.asItem()));
      }

   }
}