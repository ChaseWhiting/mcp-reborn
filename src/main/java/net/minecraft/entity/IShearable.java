package net.minecraft.entity;

import net.minecraft.util.SoundCategory;

public interface IShearable {
   void shear(SoundCategory p_230263_1_);

   boolean readyForShearing();
}