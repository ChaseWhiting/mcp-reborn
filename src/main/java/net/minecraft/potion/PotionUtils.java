package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PotionUtils {
   private static final IFormattableTextComponent NO_EFFECT = (new TranslationTextComponent("effect.none")).withStyle(TextFormatting.GRAY);

   public static List<EffectInstance> getMobEffects(ItemStack potion) {
      return getAllEffects(potion.getTag());
   }

   public static List<EffectInstance> getAllEffects(Potion potion, Collection<EffectInstance> effects) {
      List<EffectInstance> list = Lists.newArrayList();
      list.addAll(potion.getEffects());
      list.addAll(effects);
      return list;
   }

   public static ItemStack ofPotion(Potion potion) {
      ItemStack s = new ItemStack(Items.POTION);
      s.getOrCreateTag().remove("Potion");
      s.getOrCreateTag().putString("Potion", Registry.POTION.getKey(potion).toString());
      return s;
   }

   public static List<EffectInstance> getAllEffects(@Nullable CompoundNBT p_185185_0_) {
      List<EffectInstance> list = Lists.newArrayList();
      list.addAll(getPotion(p_185185_0_).getEffects());
      getCustomEffects(p_185185_0_, list);
      return list;
   }

   public static List<EffectInstance> getCustomEffects(ItemStack p_185190_0_) {
      return getCustomEffects(p_185190_0_.getTag());
   }

   public static List<EffectInstance> getCustomEffects(@Nullable CompoundNBT p_185192_0_) {
      List<EffectInstance> list = Lists.newArrayList();
      getCustomEffects(p_185192_0_, list);
      return list;
   }

   public static void getCustomEffects(@Nullable CompoundNBT p_185193_0_, List<EffectInstance> p_185193_1_) {
      if (p_185193_0_ != null && p_185193_0_.contains("CustomPotionEffects", 9)) {
         ListNBT listnbt = p_185193_0_.getList("CustomPotionEffects", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            EffectInstance effectinstance = EffectInstance.load(compoundnbt);
            if (effectinstance != null) {
               p_185193_1_.add(effectinstance);
            }
         }
      }

   }

   public static int getColor(ItemStack p_190932_0_) {
      CompoundNBT compoundnbt = p_190932_0_.getTag();
      if (compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99)) {
         return compoundnbt.getInt("CustomPotionColor");
      } else {
         return getPotion(p_190932_0_) == Potions.EMPTY ? 16253176 : getColor(getMobEffects(p_190932_0_));
      }
   }

   public static int getColor(Potion p_185183_0_) {
      return p_185183_0_ == Potions.EMPTY ? 16253176 : getColor(p_185183_0_.getEffects());
   }

   public static int getColor(Collection<EffectInstance> p_185181_0_) {
      int i = 3694022;
      if (p_185181_0_.isEmpty()) {
         return 3694022;
      } else {
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         int j = 0;

         for(EffectInstance effectinstance : p_185181_0_) {
            if (effectinstance.isVisible()) {
               int k = effectinstance.getEffect().getColor();
               int l = effectinstance.getAmplifier() + 1;
               f += (float)(l * (k >> 16 & 255)) / 255.0F;
               f1 += (float)(l * (k >> 8 & 255)) / 255.0F;
               f2 += (float)(l * (k >> 0 & 255)) / 255.0F;
               j += l;
            }
         }

         if (j == 0) {
            return 0;
         } else {
            f = f / (float)j * 255.0F;
            f1 = f1 / (float)j * 255.0F;
            f2 = f2 / (float)j * 255.0F;
            return (int)f << 16 | (int)f1 << 8 | (int)f2;
         }
      }
   }

   public static Potion getPotion(ItemStack p_185191_0_) {
      return getPotion(p_185191_0_.getTag());
   }

   public static Potion getPotion(@Nullable CompoundNBT p_185187_0_) {
      return p_185187_0_ == null ? Potions.EMPTY : Potion.byName(p_185187_0_.getString("Potion"));
   }

   public static ItemStack setPotion(ItemStack p_185188_0_, Potion p_185188_1_) {
      ResourceLocation resourcelocation = Registry.POTION.getKey(p_185188_1_);
      if (p_185188_1_ == Potions.EMPTY) {
         p_185188_0_.removeTagKey("Potion");
      } else {
         p_185188_0_.getOrCreateTag().putString("Potion", resourcelocation.toString());
      }

      return p_185188_0_;
   }


   public static CompoundNBT setPotion(CompoundNBT p_185188_0_, Potion p_185188_1_) {
      ResourceLocation resourcelocation = Registry.POTION.getKey(p_185188_1_);
      if (p_185188_1_ == Potions.EMPTY) {
         p_185188_0_.remove("Potion");
      } else {
         p_185188_0_.putString("Potion", resourcelocation.toString());
      }

      return p_185188_0_;
   }

   public static ItemStack setCustomEffects(ItemStack p_185184_0_, Collection<EffectInstance> p_185184_1_) {
      if (p_185184_1_.isEmpty()) {
         return p_185184_0_;
      } else {
         CompoundNBT compoundnbt = p_185184_0_.getOrCreateTag();
         ListNBT listnbt = compoundnbt.getList("CustomPotionEffects", 9);

         for(EffectInstance effectinstance : p_185184_1_) {
            listnbt.add(effectinstance.save(new CompoundNBT()));
         }

         compoundnbt.put("CustomPotionEffects", listnbt);
         return p_185184_0_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void addPotionTooltip(ItemStack itemStack, List<ITextComponent> tooltip, float durationFactor) {
      // Get the list of active potion effects on the item
      List<EffectInstance> effectInstances = getMobEffects(itemStack);
      List<Pair<Attribute, AttributeModifier>> attributeModifiers = Lists.newArrayList();

      if (effectInstances.isEmpty()) {
         tooltip.add(NO_EFFECT);
      } else {
         for (EffectInstance effectInstance : effectInstances) {
            IFormattableTextComponent effectTextComponent = new TranslationTextComponent(effectInstance.getDescriptionId());
            Effect effect = effectInstance.getEffect();
            Map<Attribute, AttributeModifier> effectAttributes = effect.getAttributeModifiers();

            // Process attribute modifiers for each effect
            if (!effectAttributes.isEmpty()) {
               for (Entry<Attribute, AttributeModifier> entry : effectAttributes.entrySet()) {
                  AttributeModifier originalModifier = entry.getValue();
                  AttributeModifier newModifier = new AttributeModifier(
                          originalModifier.getName(),
                          effect.getAttributeModifierValue(effectInstance.getAmplifier(), originalModifier),
                          originalModifier.getOperation()
                  );
                  attributeModifiers.add(new Pair<>(entry.getKey(), newModifier));
               }
            }

            // Add amplifier and duration information to the effect text
            if (effectInstance.getAmplifier() > 0) {
               effectTextComponent = new TranslationTextComponent(
                       "potion.withAmplifier",
                       effectTextComponent,
                       new TranslationTextComponent("potion.potency." + effectInstance.getAmplifier())
               );
            }

            if (effectInstance.getDuration() > 20) {
               effectTextComponent = new TranslationTextComponent(
                       "potion.withDuration",
                       effectTextComponent,
                       EffectUtils.formatDuration(effectInstance, durationFactor)
               );
            }

            tooltip.add(effectTextComponent.withStyle(effect.getCategory().getTooltipFormatting()));
         }
      }

      // Add attribute modifier information to the tooltip
      if (!attributeModifiers.isEmpty()) {
         tooltip.add(StringTextComponent.EMPTY);
         tooltip.add((new TranslationTextComponent("potion.whenDrank")).withStyle(TextFormatting.DARK_PURPLE));

         for (Pair<Attribute, AttributeModifier> pair : attributeModifiers) {
            AttributeModifier modifier = pair.getSecond();
            double amount = modifier.getAmount();
            double displayAmount;

            if (modifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE &&
                    modifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
               displayAmount = modifier.getAmount();
            } else {
               displayAmount = modifier.getAmount() * 100.0D;
            }

            if (amount > 0.0D) {
               tooltip.add((new TranslationTextComponent(
                       "attribute.modifier.plus." + modifier.getOperation().toValue(),
                       ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount),
                       new TranslationTextComponent(pair.getFirst().getDescriptionId())
               )).withStyle(TextFormatting.BLUE));
            } else if (amount < 0.0D) {
               displayAmount = displayAmount * -1.0D;
               tooltip.add((new TranslationTextComponent(
                       "attribute.modifier.take." + modifier.getOperation().toValue(),
                       ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount),
                       new TranslationTextComponent(pair.getFirst().getDescriptionId())
               )).withStyle(TextFormatting.RED));
            }
         }
      }
   }
}