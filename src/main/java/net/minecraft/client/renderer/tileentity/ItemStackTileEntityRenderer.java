package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.CthulhuShieldModel;
import net.minecraft.client.renderer.entity.model.ShieldModel;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tool.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ItemStackTileEntityRenderer {
   private static final ShulkerBoxTileEntity[] SHULKER_BOXES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxTileEntity::new).toArray((p_199929_0_) -> {
      return new ShulkerBoxTileEntity[p_199929_0_];
   });
   private static final ShulkerBoxTileEntity DEFAULT_SHULKER_BOX = new ShulkerBoxTileEntity((DyeColor)null);
   public static final ItemStackTileEntityRenderer instance = new ItemStackTileEntityRenderer();
   private final ChestTileEntity chest = new ChestTileEntity();
   private final ChestTileEntity trappedChest = new TrappedChestTileEntity();
   private final EnderChestTileEntity enderChest = new EnderChestTileEntity();
   private final BannerTileEntity banner = new BannerTileEntity();
   private final BedTileEntity bed = new BedTileEntity();
   private final ConduitTileEntity conduit = new ConduitTileEntity();
   private final ShieldModel shieldModel = new ShieldModel();
   private final CthulhuShieldModel cthulhuShieldModel = new CthulhuShieldModel();

   private final TridentModel tridentModel = new TridentModel();

   public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int i, int i2) {
      Item item = itemStack.getItem();
      if (item instanceof BlockItem) {
         Block block = ((BlockItem)item).getBlock();
         if (block instanceof AbstractSkullBlock) {
            GameProfile gameprofile = null;
            if (itemStack.hasTag()) {
               CompoundNBT compoundnbt = itemStack.getTag();
               if (compoundnbt.contains("SkullOwner", 10)) {
                  gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
               } else if (compoundnbt.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundnbt.getString("SkullOwner"))) {
                  GameProfile gameprofile1 = new GameProfile((UUID)null, compoundnbt.getString("SkullOwner"));
                  gameprofile = SkullTileEntity.updateGameprofile(gameprofile1);
                  compoundnbt.remove("SkullOwner");
                  compoundnbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
               }
            }

            SkullTileEntityRenderer.renderSkull((Direction)null, 180.0F, ((AbstractSkullBlock)block).getType(), gameprofile, 0.0F, matrixStack, buffer, i);
         } else {
            TileEntity tileentity;
            if (block instanceof AbstractBannerBlock) {
               this.banner.fromItem(itemStack, ((AbstractBannerBlock)block).getColor());
               tileentity = this.banner;
            } else if (block instanceof BedBlock) {
               this.bed.setColor(((BedBlock)block).getColor());
               tileentity = this.bed;
            } else if (block == Blocks.CONDUIT) {
               tileentity = this.conduit;
            } else if (block == Blocks.CHEST) {
               tileentity = this.chest;
            } else if (block == Blocks.ENDER_CHEST) {
               tileentity = this.enderChest;
            } else if (block == Blocks.TRAPPED_CHEST) {
               tileentity = this.trappedChest;
            } else {
               if (!(block instanceof ShulkerBoxBlock)) {
                  return;
               }

               DyeColor dyecolor = ShulkerBoxBlock.getColorFromItem(item);
               if (dyecolor == null) {
                  tileentity = DEFAULT_SHULKER_BOX;
               } else {
                  tileentity = SHULKER_BOXES[dyecolor.getId()];
               }
            }

            TileEntityRendererDispatcher.instance.renderItem(tileentity, matrixStack, buffer, i, i2);
         }
      } else {
         if (item == Items.SHIELD) {
            boolean flag = itemStack.getTagElement("BlockEntityTag") != null;
            matrixStack.pushPose();
            matrixStack.scale(1.0F, -1.0F, -1.0F);
            RenderMaterial rendermaterial = flag ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
            IVertexBuilder ivertexbuilder = rendermaterial.sprite().wrap(ItemRenderer.getFoilBufferDirect(buffer, this.shieldModel.renderType(rendermaterial.atlasLocation()), true, itemStack.hasFoil()));
            this.shieldModel.handle().render(matrixStack, ivertexbuilder, i, i2, 1.0F, 1.0F, 1.0F, 1.0F);
            if (flag) {
               List<Pair<BannerPattern, DyeColor>> list = BannerTileEntity.createPatterns(ShieldItem.getColor(itemStack), BannerTileEntity.getItemPatterns(itemStack));
               BannerTileEntityRenderer.renderPatterns(matrixStack, buffer, i, i2, this.shieldModel.plate(), rendermaterial, false, list, itemStack.hasFoil());
            } else {
               this.shieldModel.plate().render(matrixStack, ivertexbuilder, i, i2, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            matrixStack.popPose();
         } else if (item == Items.TRIDENT) {
            matrixStack.pushPose();
            matrixStack.scale(1.0F, -1.0F, -1.0F);
            IVertexBuilder ivertexbuilder1 = ItemRenderer.getFoilBufferDirect(buffer, this.tridentModel.renderType(TridentModel.TEXTURE), false, itemStack.hasFoil());
            this.tridentModel.renderToBuffer(matrixStack, ivertexbuilder1, i, i2, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
         } else if (item == Items.NETHERITE_SHIELD) {
            matrixStack.pushPose();
            matrixStack.scale(1.0F, -1.0F, -1.0F);

            // Use your custom material for the netherite shield
            RenderMaterial rendermaterial = ModelBakery.NETHERITE_SHIELD; // Replace with your actual render material

            IVertexBuilder ivertexbuilder = rendermaterial.sprite().wrap(ItemRenderer.getFoilBufferDirect(buffer, this.shieldModel.renderType(rendermaterial.atlasLocation()), true, itemStack.hasFoil()));
            this.shieldModel.handle().render(matrixStack, ivertexbuilder, i, i2, 1.0F, 1.0F, 1.0F, 1.0F);

            // Since the netherite shield has no patterns, we render the plate directly
            this.shieldModel.plate().render(matrixStack, ivertexbuilder, i, i2, 1.0F, 1.0F, 1.0F, 1.0F);

            matrixStack.popPose();
         }
      }
   }
}