package net.minecraft.bundle;

import net.minecraft.util.text.Style;

@FunctionalInterface
public interface FormattedCharSink {
   boolean accept(int p_13746_, Style p_13747_, int p_13748_);
}