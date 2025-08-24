package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ChorusFruitItem extends Item {
   public ChorusFruitItem(Item.Properties p_i50053_1_) {
      super(p_i50053_1_);
   }

   public ItemStack finishUsingItem(ItemStack itemStack, World world, LivingEntity entity) {
      ItemStack itemstack = super.finishUsingItem(itemStack, world, entity);
      if (!world.isClientSide) {
         double d0 = entity.getX();
         double d1 = entity.getY();
         double d2 = entity.getZ();
         if (entity.hasEffect(Effects.ENDER_FLU)) {
            entity.removeEffect(Effects.ENDER_FLU);
         }
         for(int i = 0; i < 16; ++i) {
            double d3 = entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * 16.0D;
            double d4 = MathHelper.clamp(entity.getY() + (double)(entity.getRandom().nextInt(16) - 8), 0.0D, (double)(world.getHeight() - 1));
            double d5 = entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * 16.0D;
            if (entity.isPassenger()) {
               entity.stopRiding();
            }

            if (entity.randomTeleport(d3, d4, d5, true)) {
               world.gameEvent(GameEvent.TELEPORT, entity.position(), GameEvent.Context.of(entity));
               SoundEvent soundevent = entity instanceof FoxEntity ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
               world.playSound((PlayerEntity)null, d0, d1, d2, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
               entity.playSound(soundevent, 1.0F, 1.0F);
               break;
            }
         }

         if (entity instanceof PlayerEntity) {
            ((PlayerEntity) entity).getCooldowns().addCooldown(this, 20);
         }
      }

      return itemstack;
   }
}