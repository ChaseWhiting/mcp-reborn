package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PotionItem extends Item {
   public PotionItem(Item.Properties p_i48476_1_) {
      super(p_i48476_1_);
   }

   public ItemStack getDefaultInstance() {
      return PotionUtils.setPotion(super.getDefaultInstance(), Potions.WATER);
   }

   public int getWeight(ItemStack bundle) {
      return 5;
   }

   public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity entity) {
      PlayerEntity playerentity = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
      if (playerentity instanceof ServerPlayerEntity) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)playerentity, itemStack);
      }

      if (!world.isClientSide) {
         for(EffectInstance effectinstance : PotionUtils.getMobEffects(itemStack)) {
            if (effectinstance.getEffect().isInstantenous()) {
               effectinstance.getEffect().applyInstantenousEffect(playerentity, playerentity, entity, effectinstance.getAmplifier(), 1.0D);
            } else {
               entity.addEffect(new EffectInstance(effectinstance));
            }
         }
      }

      if (playerentity != null) {
         playerentity.awardStat(Stats.ITEM_USED.get(this));
         if (!playerentity.abilities.instabuild) {
            itemStack.shrink(1);
         }
      }

      if (playerentity == null || !playerentity.abilities.instabuild) {
         if (itemStack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         if (playerentity != null) {
            playerentity.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
         }
      }
      entity.gameEvent(GameEvent.DRINK);
      return itemStack;
   }


   public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerentity, LivingEntity entity, Hand hand) {
      if (PotionUtils.getPotion(stack) == Potions.WATER && entity instanceof ShulkerEntity) {
         ShulkerEntity shulkerEntity = (ShulkerEntity) entity;

         if (shulkerEntity.getTeamColor() == 16777215) {
            if (shulkerEntity.isAlive() && shulkerEntity.getColor() != null) {
               shulkerEntity.level.playSound(playerentity, shulkerEntity, SoundEvents.BOTTLE_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!playerentity.level.isClientSide) {
                    shulkerEntity.setColor(null);
                    if (stack.getCount() > 1) {
                       if (!playerentity.abilities.instabuild) {
                          stack.shrink(1);
                          playerentity.addOrDrop(new ItemStack(Items.GLASS_BOTTLE, 1));
                       }
                    } else {
                        if (!playerentity.abilities.instabuild) {
                            playerentity.setItemInHand(hand, new ItemStack(Items.GLASS_BOTTLE, 1));
                        }
                    }
                }

                return ActionResultType.sidedSuccess(playerentity.level.isClientSide);
            }
         }

      }

      return ActionResultType.PASS;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return 32;
   }

   public UseAction getUseAnimation(ItemStack p_77661_1_) {
      return UseAction.DRINK;
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      return DrinkHelper.useDrink(world, player, hand);
   }

   public String getDescriptionId(ItemStack p_77667_1_) {
      return PotionUtils.getPotion(p_77667_1_).getName(this.getDescriptionId() + ".effect.");
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      PotionUtils.addPotionTooltip(p_77624_1_, p_77624_3_, 1.0F);
   }

   public boolean isFoil(ItemStack p_77636_1_) {
      return super.isFoil(p_77636_1_);
//      return super.isFoil(p_77636_1_) || !PotionUtils.getMobEffects(p_77636_1_).isEmpty();
   }

   public void fillItemCategory(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.allowdedIn(p_150895_1_)) {
         for(Potion potion : Registry.POTION) {
            if (potion != Potions.EMPTY) {
               p_150895_2_.add(PotionUtils.setPotion(new ItemStack(this), potion));
            }
         }
      }

   }
}