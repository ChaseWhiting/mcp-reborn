package net.minecraft.util.codec;

@FunctionalInterface
public interface StreamEncoder<O, T> {
    public void encode(O var1, T var2);
}
