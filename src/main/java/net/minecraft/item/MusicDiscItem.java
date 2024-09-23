package net.minecraft.item;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FrisbeeEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MusicDiscItem extends Item {
   private static final Map<SoundEvent, MusicDiscItem> BY_NAME = Maps.newHashMap();
   public final FrisbeeData data;
   private final int analogOutput;
   private final SoundEvent sound;
   public final boolean isFrisbee;
   private final int orignalData;

   protected MusicDiscItem(int p_i48475_1_, SoundEvent p_i48475_2_, Item.Properties p_i48475_3_) {
      super(p_i48475_3_);
      this.analogOutput = p_i48475_1_;
      this.sound = p_i48475_2_;
      this.isFrisbee = false;
      this.data = new FrisbeeData.FrisbeeDataBuilder().DEFAULT();
      orignalData = 0;
      BY_NAME.put(this.sound, this);
   }

   public int getWeight(ItemStack stack) {
      return this.analogOutput;
   }

   protected MusicDiscItem(int p_i48475_1_, SoundEvent p_i48475_2_, Item.Properties p_i48475_3_, FrisbeeData data) {
      super(data.isFireResistant() ? p_i48475_3_.fireResistant() : p_i48475_3_);
      this.analogOutput = p_i48475_1_;
      this.sound = p_i48475_2_;
      this.isFrisbee = true;
      this.data = data;
      orignalData = data.getDistanceToComeBack();
      BY_NAME.put(this.sound, this);
   }

   @Override
   public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
      if (isFrisbee && data != null) {
         RegistryKey<World> level = world.dimension();
         if (!Arrays.asList(data.getDimensions()).contains(level)) {
            return ActionResult.fail(playerEntity.getItemInHand(hand));
         }
         ItemStack itemstack = playerEntity.getItemInHand(hand);
         world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                 SoundEvents.ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F,
                 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         playerEntity.getCooldowns().addCooldown(this, 5);

         if (!world.isClientSide) {
            ItemStack stack = new ItemStack(this);
            getDistance(playerEntity, data);
            FrisbeeEntity frisbeeEntity = new FrisbeeEntity(EntityType.FRISBEE, playerEntity, world, data, itemstack);
            frisbeeEntity.setOwner(playerEntity);
            frisbeeEntity.shootFromRotation(playerEntity, playerEntity.xRot, playerEntity.yRot, 0.0F,
                    getVelocity(playerEntity), 1.0F);
            stack.setDamageValue(itemstack.getDamageValue());
            TransferEnchantments.transferEnchantments(itemstack, stack);
            frisbeeEntity.setItemStack(stack);
            frisbeeEntity.setItem(stack);
            frisbeeEntity.setNoGravity(true);
            data.triggerOnThrow(frisbeeEntity, playerEntity);
            world.addFreshEntity(frisbeeEntity);
         }

         playerEntity.awardStat(Stats.ITEM_USED.get(this));
         if (!playerEntity.abilities.instabuild) {
            itemstack.shrink(1);
         }

         return ActionResult.sidedSuccess(itemstack, world.isClientSide());
      }
      return ActionResult.fail(playerEntity.getItemInHand(hand));
   }

   public float getVelocity(PlayerEntity player) {
      return !player.isShiftKeyDown() ? 1.5F + 0.01F * data.getSpeed() : 0.7F + 0.005F * data.getSpeed();
   }

   public void getDistance(PlayerEntity player, FrisbeeData data) {
      if (player.isShiftKeyDown()) {
         data.distanceToComeBack = data.getDistanceHalved();
      } else {
         data.distanceToComeBack = orignalData;
      }
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (blockstate.is(Blocks.JUKEBOX) && !blockstate.getValue(JukeboxBlock.HAS_RECORD)) {
         ItemStack itemstack = p_195939_1_.getItemInHand();
         if (!world.isClientSide) {
            ((JukeboxBlock)Blocks.JUKEBOX).setRecord(world, blockpos, blockstate, itemstack);
            world.levelEvent((PlayerEntity)null, 1010, blockpos, Item.getId(this));
            itemstack.shrink(1);
            PlayerEntity playerentity = p_195939_1_.getPlayer();
            if (playerentity != null) {
               playerentity.awardStat(Stats.PLAY_RECORD);
            }
         }

         return ActionResultType.sidedSuccess(world.isClientSide);
      } else {
         return ActionResultType.PASS;
      }
   }

   public int getAnalogOutput() {
      return this.analogOutput;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      p_77624_3_.add(this.getDisplayName().withStyle(TextFormatting.GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   public IFormattableTextComponent getDisplayName() {
      return new TranslationTextComponent(this.getDescriptionId() + ".desc");
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static MusicDiscItem getBySound(SoundEvent p_185074_0_) {
      return BY_NAME.get(p_185074_0_);
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent getSound() {
      return this.sound;
   }
}