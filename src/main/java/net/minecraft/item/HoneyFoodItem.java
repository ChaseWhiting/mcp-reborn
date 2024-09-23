package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HoneyFoodItem extends Item {
   public int useDuration = 40;
   public onEat onEat;
   public List<Effect> removeEffects;
   public HoneyFoodItem(Food food, int useDuration, @Nullable onEat onEat, @Nullable Effect... removeEffects) {
      super(new Properties().tab(ItemGroup.TAB_FOOD).food(food));
      if (removeEffects != null) {
         this.removeEffects = Arrays.stream(removeEffects).collect(Collectors.toList());
      } else {
         this.removeEffects = List.of(Effects.POISON);
      }
      this.useDuration = useDuration;
      this.onEat = onEat;

   }

   public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity entity) {
      super.finishUsingItem(itemStack, world, entity);
      if (entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entity;
         CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStack);
         serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
      }
      if (onEat != null) {
         onEat.eat(entity, world, itemStack);
      }

      removeEffects(world, entity);

      return itemStack;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      return this.useDuration;
   }

   public void removeEffects(World world, LivingEntity livingEntity) {
      if (!world.isClientSide) {
         for (Effect effect : removeEffects) {
            livingEntity.removeEffect(effect);
         }
      }
   }
   @FunctionalInterface
   public interface onEat {
      void eat(LivingEntity entity, World world, ItemStack food);
   }
}