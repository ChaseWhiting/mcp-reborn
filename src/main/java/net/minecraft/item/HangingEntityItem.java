package net.minecraft.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import java.util.List;

public class HangingEntityItem extends Item {
   private final EntityType<? extends HangingEntity> type;

   public HangingEntityItem(EntityType<? extends HangingEntity> p_i50043_1_, Item.Properties p_i50043_2_) {
      super(p_i50043_2_);
      this.type = p_i50043_1_;
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      BlockPos blockpos = p_195939_1_.getClickedPos();
      Direction direction = p_195939_1_.getClickedFace();
      BlockPos blockpos1 = blockpos.relative(direction);
      PlayerEntity playerentity = p_195939_1_.getPlayer();
      ItemStack itemstack = p_195939_1_.getItemInHand();
      if (playerentity != null && !this.mayPlace(playerentity, direction, itemstack, blockpos1)) {
         return ActionResultType.FAIL;
      } else {
         World world = p_195939_1_.getLevel();
         HangingEntity hangingentity;
         if (this.type == EntityType.PAINTING) {
            CompoundNBT nbt = itemstack.getOrCreateTag();

            if (nbt.contains("EntityTag")) {
               CompoundNBT entityTag = nbt.getCompound("EntityTag");
               PaintingType motive = Registry.MOTIVE.get(ResourceLocation.tryParse(entityTag.getString("Motive")));
               hangingentity = new PaintingEntity(world, blockpos1, direction, motive);
               hangingentity.as(PaintingEntity.class).isFixed = true;
            } else {
               hangingentity = new PaintingEntity(world, blockpos1, direction);

            }
         } else {
            if (this.type != EntityType.ITEM_FRAME) {
               return ActionResultType.sidedSuccess(world.isClientSide);
            }

            hangingentity = new ItemFrameEntity(world, blockpos1, direction);
         }

         CompoundNBT compoundnbt = itemstack.getTag();
         if (compoundnbt != null) {
            EntityType.updateCustomEntityTag(world, playerentity, hangingentity, compoundnbt);
         }

         if (hangingentity.survives()) {
            if (!world.isClientSide) {
               hangingentity.playPlacementSound();
               world.gameEvent(playerentity, GameEvent.ENTITY_PLACE, hangingentity.position());
               world.addFreshEntity(hangingentity);
            }

            itemstack.shrink(1);
            return ActionResultType.sidedSuccess(world.isClientSide);
         } else {
            return ActionResultType.CONSUME;
         }
      }
   }


   @Override
   public void fillItemCategory(ItemGroup itemGroup, NonNullList<ItemStack> items) {
      if (type != EntityType.PAINTING) {
         super.fillItemCategory(itemGroup, items);
         return;
      }

      if (this.allowdedIn(ItemGroup.TAB_NATURAL) && (itemGroup == ItemGroup.TAB_NATURAL || itemGroup == ItemGroup.TAB_SEARCH)) {
         items.add(new ItemStack(Items.PAINTING));
         for (PaintingType paintingType : PaintingType.IN_ORDER) {
            items.add(PaintingType.withVariant(paintingType));
         }
      }
   }

   public void appendHoverText(ItemStack painting, World level, List<ITextComponent> flags, ITooltipFlag tooltip) {
      if (type != EntityType.PAINTING) {
         super.appendHoverText(painting, level, flags, tooltip);
         return;
      }

      CompoundNBT nbt = painting.getOrCreateTag();

      if (nbt.contains("EntityTag")) {
         CompoundNBT entityTag = nbt.getCompound("EntityTag");
         PaintingType motive = Registry.MOTIVE.get(ResourceLocation.tryParse(entityTag.getString("Motive")));
         if (motive == null) {
            super.appendHoverText(painting, level, flags, tooltip);
            return;
         }

         String author = motive.getAuthor();
         int blockWidth = motive.getWidthInBlocks();
         int blockHeight = motive.getHeightInBlocks();
         String title = motive.getTitle();

         IFormattableTextComponent titleName = new StringTextComponent(title).withStyle(TextFormatting.YELLOW);

         IFormattableTextComponent authorName = new StringTextComponent(author).withStyle(TextFormatting.GRAY);
         IFormattableTextComponent size = new StringTextComponent(blockWidth + "x" + blockHeight).withStyle(TextFormatting.WHITE);

         flags.addAll(List.of(titleName, authorName, size));
      } else {
         IFormattableTextComponent titleName = new StringTextComponent("Random variant").withStyle(TextFormatting.GRAY);
         flags.add(titleName);
      }
      super.appendHoverText(painting, level, flags, tooltip);
   }

   protected boolean mayPlace(PlayerEntity p_200127_1_, Direction p_200127_2_, ItemStack p_200127_3_, BlockPos p_200127_4_) {
      return !p_200127_2_.getAxis().isVertical() && p_200127_1_.mayUseItemAt(p_200127_4_, p_200127_2_, p_200127_3_);
   }
}