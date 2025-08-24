package net.minecraft.item.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.TieredItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BatItem extends TieredItem implements IVanishable {
   protected final float attackDamage;
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;

   public BatItem(float p_i48460_3_, Properties properties) {
      super(ItemTier.BAT, properties);
      this.attackDamage = (float)ItemTier.BAT.getAttackDamageBonus();
      Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
      builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
      builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)p_i48460_3_, AttributeModifier.Operation.ADDITION));
      this.defaultModifiers = builder.build();
   }

   public float getDamage() {
      return this.attackDamage;
   }

   public boolean hurtEnemy(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
      p_77644_1_.hurtAndBreak(p_77644_2_.getRandom().nextInt(5) + 1, p_77644_3_, (p_220045_0_) -> {
         p_220045_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
      });
      return true;
   }

   public boolean mineBlock(ItemStack itemStack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
      if (state.getDestroySpeed(world, pos) != 0.0F) {
         itemStack.hurtAndBreak(entity.veryHardmode() ? 5 : 1, entity, (p_220044_0_) -> {
            p_220044_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
         });
      }

      return true;
   }

   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType p_111205_1_) {
      return p_111205_1_ == EquipmentSlotType.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(p_111205_1_);
   }
}