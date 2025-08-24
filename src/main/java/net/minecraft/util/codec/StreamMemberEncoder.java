package net.minecraft.util.codec;

@FunctionalInterface
public interface StreamMemberEncoder<O, T> {
    public void encode(T var1, O var2);
}